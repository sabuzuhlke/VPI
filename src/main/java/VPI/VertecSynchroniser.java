package VPI;

import VPI.PDClasses.*;
import VPI.VertecClasses.JSONContact;
import VPI.VertecClasses.JSONOrganisation;
import VPI.VertecClasses.VertecService;
import VPI.VertecClasses.ZUKResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.*;

/**
 * Created by sabu on 29/04/2016.
 */
public class VertecSynchroniser {

    private PDService PDS;
    private VertecService VS;

    public List<PDContactSend> contactPutList;
    public List<PDContactSend> contactPostList;

    public List<PDOrganisationSend> organisationPutList;
    public List<JSONOrganisation> organisationPostList;

    private Map<String,Long> teamIdMap;

    public VertecSynchroniser() {

        //retrieve credentials from locally stored file
        MyCredentials creds = new MyCredentials();

        //set up Pipedrive and Vertec services
        this.PDS = new PDService("https://api.pipedrive.com/v1/", creds.getApiKey());
        this.VS = new VertecService("localhost:9999");

        //initialise Contact/Og put and post list (for sending to pipedrive);
        this.contactPostList = new ArrayList<>();
        this.contactPutList = new ArrayList<>();
        this.organisationPostList = new ArrayList<>();
        this.organisationPutList = new ArrayList<>();
        this.teamIdMap = new HashMap<>();
    }

    public Map<String, Long> getTeamIdMap() {
        return teamIdMap;
    }

    public void setTeamIdMap(Map<String, Long> teamIdMap) {
        this.teamIdMap = teamIdMap;
    }

    private Set<String> getVertecUserEmails(ZUKResponse data) {
        Set<String> v_emails = new HashSet<>();
        for (JSONOrganisation org : data.getOrganisationList()) {

            v_emails.add(org.getOwner());

            for (JSONContact cont : org.getContacts()) {

                v_emails.add(cont.getOwner());

            }

        }

        return v_emails;
    }

    private List<PDUser> getPipedriveUsers() {
        return PDS.getAllUsers().getBody().getData();
    }

    public void constructTeamIdMap(Set<String> v_emails, List<PDUser> pd_users) {//TODO: write test for this
        for (String v_email : v_emails) {
            Boolean mapped = false;
            for (PDUser pd_user : pd_users) {
                if (v_email.toLowerCase().equals(pd_user.getEmail().toLowerCase())) {
                    this.teamIdMap.put(v_email, pd_user.getId());
                    mapped = true;
                }
            }
            if (!mapped) {
                this.teamIdMap.put(v_email, 1363410L ); //TODO: replace id with appropriate id, wolfgangs or admin?
            }
        }
    }

    public List<List<Long>> importToPipedrive() {

        //get all Vertec Data
        ZUKResponse allVertecData = VS.getZUKinfo().getBody();

        Set<String> v_emails = getVertecUserEmails(allVertecData);
        List<PDUser> pd_users= getPipedriveUsers();

        //constructTeamIdMap(v_emails, pd_users); //TODO: use this instead of constructTestTeamMap() on deployment
        constructTestTeamMap();

        //get all Pipedrive organisations
        List<PDOrganisation> pipedriveOrgs = PDS.getAllOrganisations().getBody().getData();

        //compare pipedrive orgs along with nested contacts, removing nested contacts from contacts
        resolveOrganisationsAndNestedContacts(allVertecData.getOrganisationList(), pipedriveOrgs);

        //get all pipedrive contacts, filter to only use those without organisations
        List<PDContactReceived> pipedriveContacts = PDS.getAllContacts().getBody().getData();
        List<PDContactReceived> contactsWithoutOrg = filterContactsWithOrg(pipedriveContacts);

        //compare dangling vcontacts to leftover pdcontacts
        compareContacts(allVertecData.getDanglingContacts(), contactsWithoutOrg);

        //initialize return list
        List<List<Long>> ids = new ArrayList<>();

        //now ready to post/put
        List<List<Long>> orgsNConts = postVOrganisations();
        List<Long> orgsPut = putPdOrganisations();
        List<Long> contsPost = postContacts();
        List<Long> contsPut = putContacts();

        //return list of orgs and contact ids that have been posted/edited to pipedrive
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
            for(PDOrganisation po : pOrgs){
                if(po.getV_id() == null) continue;
                if(vo.getObjid().longValue() == po.getV_id().longValue()){
                    matched = true;
                    compareOrganisationDetails(vo, po);

                    //TODO:!! Change so that all contacts get compared !!
                    resolveContactsForOrgs(vo,po);
                }
            }
            if(!matched){
                organisationPostList.add(vo);
            }

        }
    }
    public void testresolveOrganisationsAndNestedContacts(List<JSONOrganisation> vOrgs, List<PDOrganisation> pOrgs) {
        for(JSONOrganisation vo : vOrgs){
            Boolean matched = false;
            for(PDOrganisation po : pOrgs){
                if(po.getV_id() == null) continue;
                if(vo.getObjid().longValue() == po.getV_id().longValue()){
                    matched = true;
                    compareOrganisationDetails(vo, po);
                    resolveTestContactsForOrgs(vo,po);
                }
            }
            if(!matched){
                organisationPostList.add(vo);
            }

        }
    }

    private Boolean compareOrganisationDetails(JSONOrganisation vo, PDOrganisation po){
        Boolean diff = false;
        if(! vo.getFormattedAddress().equals(po.getAddress())) diff = true;
        if( ! vo.getName().equals(po.getName())) diff = true;
        if( po.getOwner_id().getId() != teamIdMap.get(vo.getOwner().toLowerCase()).longValue()) diff = true;


        if(diff){
            Long ownerid = teamIdMap.get(vo.getOwner().toLowerCase());
            organisationPutList.add(new PDOrganisationSend(vo,po,ownerid)); //TODO: Make constructor deal with most recent
        }

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
            if(pContacts == null) continue;
            for(PDContactReceived pc : pContacts) {

                String fullname = vc.getFirstName() + " " + vc.getSurname();

                if (pc.getOrg_id() != null && pc.getOrg_id().getValue() != null) tempOrgID = pc.getOrg_id().getValue();

                if(pc.getV_id() == null) continue;
                if (vc.getObjid().longValue() == pc.getV_id().longValue()) {
                    matchedName = true;

                    //resolve internal contact details;
                    modified = resolveContactDetails(vc, pc);
                    if(modified) {
                        temp = pc;
                    }
                }

            }
            if (!matchedName) {
                Long owner = teamIdMap.get(vc.getOwner().toLowerCase());
                PDContactSend newContact = new PDContactSend(vc,owner);
                newContact.setOrg_id(tempOrgID);
                newContact.setOwner_id(teamIdMap.get(vc.getOwner()));
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

        Boolean matchedName = false;
        String fullName = v.getFirstName() + " " + v.getSurname();

        if( ! fullName.equals(p.getName())) {
            matchedName = true;
        }

        Boolean modifiedOwner = false;

        if(p.getOwner_id()
                .getId()
                != teamIdMap.
                get(v.getOwner()).
                longValue()){
            modifiedOwner = true;
        }


        return modifiedEmail || modifiedPhone || matchedName || modifiedOwner;
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
            Long ownerid = teamIdMap.get(o.getOwner());
            res = PDS.postOrganisation(new PDOrganisationSend(o, ownerid));
            orgsPosted.add(res.getBody().getData().getId());

            for(JSONContact c : o.getContacts()){
                Long owner = teamIdMap.get(c.getOwner());
                PDContactSend s = new PDContactSend(c,owner);
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

    public void constructTestTeamMap(){
        Map<String,Long> map = new HashMap<>();

        map.put("wolfgang.emmerich@zuhlke.com", 1363410L); //Wolfgang
        map.put("tim.cianchi@zuhlke.com", 1363402L); //Tim
        map.put("neil.moorcroft@zuhlke.com", 136429L); //Neil
        map.put("mike.hogg@zuhlke.com", 1363424L); //Mike
        map.put("justin.cowling@zuhlke.com", 1363416L); //Justin
        map.put("brewster.barclay@zuhlke.com", 1363403L); //Brewster
        map.put("keith.braithwaite@zuhlke.com", 1363488L); //Keith
        map.put("peter.brown@zuhlke.com", 1277584L); //Peter Brown
        map.put("steve.freeman@zuhlke.com", 1277584L); //Steve Freeman
        map.put("john.seston@zuhlke.com", 1277584L); //John Seston
        map.put("sabine.streuss@zuhlke.com", 1277584L); //Sabine
        map.put("ileana.meehan@zuhlke.com", 1277584L); //Ileana
        map.put("ina.hristova@zuhlke.com", 1277584L); //Ina
        map.put(null, 1277584L); //null

        this.teamIdMap = map;

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
