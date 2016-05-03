package VPI;

import VPI.PDClasses.*;
import VPI.VertecClasses.JSONContact;
import VPI.VertecClasses.JSONOrganisation;
import VPI.VertecClasses.VertecService;
import VPI.VertecClasses.ZUKResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sabu on 29/04/2016.
 */
public class VertecSynchroniser {

    private PDService PDS;
    private VertecService VS;

    public List<PDContactSend> contactPutList;
    public List<PDContactSend> contactPostList;

    public List<PDOrganisation> organisationPutList;
    public List<JSONOrganisation> organisationPostList;

    public VertecSynchroniser() {
        RestTemplate restTemplate = new RestTemplate();
        MyCredentials creds = new MyCredentials();
        this.PDS = new PDService(restTemplate, "https://api.pipedrive.com/v1/", creds.getApiKey());
        this.VS = new VertecService("localhost:9999");
        this.contactPostList = new ArrayList<>();
        this.contactPutList = new ArrayList<>();
        this.organisationPostList = new ArrayList<>();
        this.organisationPutList = new ArrayList<>();
    }

    public List<List<Long>> importToPipedrive() {
        //get all Vertec Data
        ZUKResponse allVertecData = VS.getZUKinfo().getBody();
        //get all Pipedrive organisations
        List<PDOrganisation> pipedriveOrgs = PDS.getAllOrganisations().getBody().getData();
        //compare pipedrive orgs along with nested contacts, removing nested contacts from contacts
        resolveOrganisationsAndNestedContacts(allVertecData.getOrganisationList(), pipedriveOrgs);

        //get all pipedrive contacts, filter to only use those without organisations
        List<PDContactReceived> pipedriveContacts = PDS.getAllContacts().getBody().getData();
        List<PDContactReceived> contactsWithoutOrg = filterContactsWithOrg(pipedriveContacts);
        //compare dangling vcontacts to leftover pdcontacts
        compareContacts(allVertecData.getDanglingContacts(), contactsWithoutOrg);

        List<List<Long>> ids = new ArrayList<>();

        //now ready to post/put
        List<List<Long>> orgsNConts = postVOrganisations();
        List<Long> orgsPut = putPdOrganisations();
        List<Long> contsPost = postContacts();
        List<Long> contsPut = putContacts();
        ids.add(orgsNConts.get(0));
        ids.add(orgsPut);
        ids.add(orgsNConts.get(1));
        ids.add(contsPost);
        ids.add(contsPut);

        return ids;
    }

    public void resolveOrganisationsAndNestedContacts(List<JSONOrganisation> vOrgs, List<PDOrganisation> pOrgs) {
        for(JSONOrganisation vo : vOrgs){
            Boolean matched = false;
            Boolean modified= false;
            PDOrganisation tmp = null;
            for(PDOrganisation po :pOrgs){

                if(vo.getName().equals(po.getName())){
                    matched = true;
                    modified = compareOrganisationDetails(vo, po);
                    resolveContactsForOrgs(vo,po);
                    if(modified){
                        tmp = po;
                    }
                }
            }
            if(!matched){
                organisationPostList.add(vo);
            }
            if(modified){
                organisationPutList.add(new PDOrganisation(vo,tmp));
            }
        }
    }
    public void testresolveOrganisationsAndNestedContacts(List<JSONOrganisation> vOrgs, List<PDOrganisation> pOrgs){
        for(JSONOrganisation vo : vOrgs){
            Boolean matched = false;
            Boolean modified= false;
            PDOrganisation tmp = null;
            for(PDOrganisation po :pOrgs){

                if(vo.getName().equals(po.getName())){
                    matched = true;
                    modified = compareOrganisationDetails(vo, po);
                    resolveTestContactsForOrgs(vo,po);
                    if(modified){
                        tmp = po;
                    }
                }
            }
            if(!matched){
                organisationPostList.add(vo);
            }
            if(modified){
                organisationPutList.add(new PDOrganisation(vo,tmp));
            }
        }
    }

    private Boolean compareOrganisationDetails(JSONOrganisation vo, PDOrganisation po){
        Boolean diff = false;
        if(! vo.getFormattedAddress().equals(po.getAddress())) diff = true;

        return diff;
    }

    public void resolveTestContactsForOrgs(JSONOrganisation jo, PDOrganisation po){
    }

    private void resolveContactsForOrgs(JSONOrganisation jo, PDOrganisation po){
        List<PDContactReceived>  pdContacts = PDS.getContactsForOrganisation(po.getId()).getBody().getData();
        compareContacts(jo.getContacts(), pdContacts);
    }
    public void compareContacts(List<JSONContact> vConts, List<PDContactReceived> pContacts) {

        for(JSONContact vc : vConts) {
            Boolean matchedName = false;
            Boolean modified = false;
            PDContactReceived temp = null;
            Long tempOrgID = null;
            for(PDContactReceived pc : pContacts) {
                String fullname = vc.getFirstName() + " " + vc.getSurname();
                if (pc.getOrg_id() != null && pc.getOrg_id().getValue() != null) tempOrgID = pc.getOrg_id().getValue();

                if (fullname.equals(pc.getName())) {
                    matchedName = true;

                    //resolve internal contact details;
                    modified = resolveContactDetails(vc, pc);
                    if(modified) {
                        temp = pc;
                    }
                }

            }
            if (!matchedName) {
                PDContactSend newContact = new PDContactSend(vc);
                newContact.setOrg_id(tempOrgID);
                contactPostList.add(newContact);
            }
            if(modified) {
                contactPutList.add(new PDContactSend(temp));
            }

        }



    }

    public Boolean resolveContactDetails(JSONContact v, PDContactReceived p){

        Boolean modifiedPhone = false;

        if(v.getMobile() != null) {
            Boolean matchedMobile = false;
            for(ContactDetail pph: p.getPhone()) {
                if(v.getMobile().equals(pph.getValue())) {
                    matchedMobile = true;
                }
            }
            if(!matchedMobile) {
                p.getPhone().add(new ContactDetail(v.getMobile(), false));
                modifiedPhone = true;
            }
        }


        if(v.getPhone() != null) {
            Boolean matchedPhone = false;
            for(ContactDetail pph: p.getPhone()) {
                if(v.getPhone().equals(pph.getValue())) {
                    matchedPhone = true;
                    if (!pph.getPrimary()) {
                        pph.setPrimary(true);
                        modifiedPhone = true;
                    }
                } else {
                    pph.setPrimary(false);
                }
            }
            if (!matchedPhone) {
                p.getPhone().add(new ContactDetail(v.getPhone(), true));
                modifiedPhone = true;
            }
        }

        Boolean modifiedEmail = false;

        if (v.getEmail() != null) {

            Boolean matchedEmail = false;
            for (ContactDetail pe : p.getEmail()) {
                if (v.getEmail().equals(pe.getValue())) {
                    matchedEmail = true;
                    if (!pe.getPrimary()) {
                        pe.setPrimary(true);
                        modifiedEmail = true;
                    }
                } else {
                    pe.setPrimary(false);
                }
            }
            if (!matchedEmail) {
                p.getEmail().add(new ContactDetail(v.getEmail(), true));
                modifiedEmail = true;
            }
        }

        return modifiedEmail || modifiedPhone;


    }

    //removes Contacts that are attached to organisations (as they are already handled
    public List<PDContactReceived> filterContactsWithOrg(List<PDContactReceived> pContacts) {
        //do more somthings
        List<PDContactReceived> filteredList = new ArrayList<>();

        for(PDContactReceived c : pContacts) {
            if(c.getOrg_id() == null) {
                filteredList.add(c);
            }
        }
        return filteredList;
    }

    public List<List<Long>> postVOrganisations(){

        ResponseEntity<PDOrganisationResponse> res = null;
        List<List<Long>> both = new ArrayList<>();
        List<Long> orgsPosted = new ArrayList<>();
        List<Long> contactsPosted = new ArrayList<>();
        for(JSONOrganisation o : organisationPostList){
            res = PDS.postOrganisation(new PDOrganisation(o));
            orgsPosted.add(res.getBody().getData().getId());

            for(JSONContact c : o.getContacts()){
                PDContactSend s = new PDContactSend(c);
                s.setOrg_id(res.getBody().getData().getId());
                contactsPosted.add(PDS.postContact(s).getBody().getData().getId());
            }
        }
        both.add(orgsPosted);
        both.add(contactsPosted);
        return both;
    }

    public List<Long> putPdOrganisations(){

        return PDS.putOrganisationList(organisationPutList);
    }

    public List<Long> postContacts(){

        return PDS.postContactList(contactPostList);
    }

    public List<Long> putContacts(){

        return PDS.putContactList(contactPutList);
    }

    public void clear(){
        this.organisationPostList.clear();
        this.organisationPutList.clear();
        this.contactPostList.clear();
        this.contactPutList.clear();
    }

    public PDService getPDS() {
        return PDS;
    }

    public void setPDS(PDService PDS) {
        this.PDS = PDS;
    }

    public VertecService getVS() {
        return VS;
    }

    public void setVS(VertecService VS) {
        this.VS = VS;
    }
}
