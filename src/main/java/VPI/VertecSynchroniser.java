package VPI;

import VPI.PDClasses.*;
import VPI.VertecClasses.JSONContact;
import VPI.VertecClasses.JSONOrganisation;
import VPI.VertecClasses.VertecService;
import VPI.VertecClasses.ZUKResponse;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by sabu on 29/04/2016.
 */
public class VertecSynchroniser {

    private PDService PDS;
    private VertecService VS;

    public List<PDContactSend> contactPutList;
    public List<PDContactSend> contactPostList;

    public VertecSynchroniser() {
        RestTemplate restTemplate = new RestTemplate();
        MyCredentials creds = new MyCredentials();
        this.PDS = new PDService(restTemplate, "https://api.pipedrive.com/v1/", creds.getApiKey());
        this.VS = new VertecService("localhost:9999");
        this.contactPostList = new ArrayList<>();
        this.contactPutList = new ArrayList<>();
    }

    public void importToPipedrive() {
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
        compareDanglingContacts(allVertecData.getDanglingContacts(), contactsWithoutOrg);
    }

    public void resolveOrganisationsAndNestedContacts(List<JSONOrganisation> vOrgs, List<PDOrganisation> pOrgs) {
        //do something
    }

    public void compareDanglingContacts(List<JSONContact> vConts, List<PDContactReceived> pContacts) {

        for(JSONContact vc : vConts) {
            Boolean matchedName = false;
            Boolean modified = false;
            PDContactReceived temp = null;
            for(PDContactReceived pc : pContacts) {
                String fullname = vc.getFirstName() + " " + vc.getSurname();
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
                contactPostList.add(new PDContactSend(vc));
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

}
