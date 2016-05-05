package VPI;

import VPI.PDClasses.*;
import VPI.InsightClasses.InsightService;
import VPI.InsightClasses.VContact;
import VPI.InsightClasses.VOrganisation;
import VPI.InsightClasses.VProject;
import org.springframework.web.client.RestTemplate;

import java.util.*;

/**
 * Created by sabu on 15/04/2016.
 */
public class InsightSynchroniser {

    private PDService PDS;
    private InsightService IS;

    public Organisations organisations;
    public Contacts contacts;

    public InsightSynchroniser(String PDserver, String Vserver) {
        RestTemplate restTemplate = new RestTemplate();
        MyCredentials creds = new MyCredentials();
        this.PDS = new PDService(restTemplate, PDserver, creds.getApiKey());
        this.IS  = new InsightService(
                restTemplate,
                Vserver,
                creds.getUserName(),
                creds.getPass()
        );
        this.organisations = new Organisations();
        this.contacts      = new Contacts();
    }


    public List<Long> importToPipedrive() {
        List<Long> org_ids = getProjectsForZUK();
        List<Long> orgs_pushed = importOrganisations(org_ids);
        System.out.println("Posted " + orgs_pushed.size() + " organisations to Pipedrive");
        //importContacts();
        clear();
        return orgs_pushed;
    }

    public List<Long> getProjectsForZUK() {
        List<VProject> projs = IS.getProjectsForZUK().getBody().getItems();
        List<Long> org_ids_to_get = new ArrayList<>();
        for(VProject p : projs) {
            if (p.getOrganisation() != null) {
                if(p.getOrganisation().getName() != null){
                    org_ids_to_get.add(p.getOrganisation().getId());
                }
                else{
                    System.out.println("Could not get Name of organisation for project: " + p.getName());
                }
            } else {
                System.out.println("Could not get organisation for project: " + p.getName());
            }
        }
        Set<Long> no_dups = new HashSet<>(org_ids_to_get);
        org_ids_to_get.clear();
        org_ids_to_get.addAll(no_dups);
        return org_ids_to_get;
    }

    public List<Long> importOrganisations(List<Long> org_ids) {
        getAllOrganisations(org_ids);
        compareOrganisations();
        return pushOrganisations();
    }

    private List<Long> importContacts() {
        addContactsForNewOrganisationsToPostList();
        resolveContactsForMatchedOrganisations();
        return pushContacts();
    }

    private void resolveContactsForMatchedOrganisations() {
        for(VOrganisation v : organisations.matchedList) {
            List<VContact> vC = Arrays.asList(IS.getContactsForOrganisation(v.getId()).getBody());
            List<PDContactReceived> pC = PDS.getContactsForOrganisation(v.getPd_id()).getBody().getData();

            compareContacts(vC, pC);
        }

        for(PDOrganisationSend p : organisations.putList) {
            List<VContact> vC = Arrays.asList(IS.getContactsForOrganisation(p.getV_id()).getBody());
            List<PDContactReceived> pC = PDS.getContactsForOrganisation(p.getId()).getBody().getData();

            compareContacts(vC, pC);
        }
    }

    private void addContactsForNewOrganisationsToPostList() {
        for(PDOrganisationSend p : organisations.postList) {
            List<VContact> contactsForOrg = Arrays.asList(IS.getContactsForOrganisation(p.getV_id()).getBody());
            for(VContact c : contactsForOrg) {
                contacts.postList.add(new PDContactSend(c));
            }
        }
    }

    private void getAllOrganisations(List<Long> org_ids) {
        this.organisations.vOrganisations = IS.getOrganisationList(org_ids);
        this.organisations.pdOrganisations = PDS.getAllOrganisations().getBody().getData();
    }

    public void compareOrganisations() {

        for(VOrganisation v : organisations.vOrganisations){
            Boolean matched = false;
            Boolean modified = false;
            PDOrganisation temp = null;
            for(PDOrganisation p : organisations.pdOrganisations) {
                if (v.getName() != null && p.getName() != null) {
                    if (v.getName().equals(p.getName())) {
                        matched = true;
                        v.setPd_id(p.getId());

                        //Resolve attributes of organisation
                        //address
                        if (!v.getFormattedAddress().equals(p.getAddress())) {
                            modified = true;
                            p.setAddress(v.getFormattedAddress());
                            p.setV_id(v.getId());
                            temp = p;
                        }
                        //further fields to compare
                        //...
                    }
                }
            }
            if(!matched && v.getName() != null){
                organisations.postList.add(new PDOrganisationSend(v));
            }
            if(modified){
                organisations.putList.add(new PDOrganisationSend(temp));
            }
            if(matched && !modified) {
                organisations.matchedList.add(v);
            }
        }
    }

    public void compareContacts(List<VContact> vContacts, List<PDContactReceived> pdContacts) {

        for(VContact v : vContacts) {
            Boolean matched = false;
            Boolean modified = false;
            PDContactReceived temp = null;
            for(PDContactReceived p : pdContacts) {
                if(v.getName().equals(p.getName())) {
                    matched = true;

                    //resolve emailDetail and phoneDetail lists of contact
                    modified = resolveContactDetails(v,p);
                    if(modified) {
                        temp = p;
                    }
                }
            }

            if(!matched) {
                contacts.postList.add(new PDContactSend(v));
            }
            if(modified) {
                contacts.putList.add(new PDContactSend(temp));
            }

        }

    }

    public Boolean resolveContactDetails(VContact v, PDContactReceived p){
        Boolean modifiedPhone = false;

        Boolean somethingToStopCOdeDupWarning = false;
        if (v.getPhone() != null) {

            Boolean matchedPhone = false;
            for (ContactDetail pph : p.getPhone()) {
                if (v.getPhone().equals(pph.getValue())) {
                    matchedPhone = true;
                    if (!pph.getPrimary()) {
                        somethingToStopCOdeDupWarning = true;
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
                        somethingToStopCOdeDupWarning = false;
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

    private List<Long> pushOrganisations() {
        List<Long> idsPosted = PDS.postOrganisationList(organisations.postList);
        List<Long> idsPutted = PDS.putOrganisationList(organisations.putList);

        List<Long> idsPushed = new ArrayList<>(idsPosted);
        idsPushed.addAll(idsPutted);
        return idsPushed;
    }

    private List<Long> pushContacts() {
        List<Long> idsPosted = PDS.postContactList(contacts.postList);
        List<Long> idsPutted = PDS.putContactList(contacts.putList);

        List<Long> idsPushed = new ArrayList<>(idsPosted);
        idsPushed.addAll(idsPutted);
        return idsPushed;
    }

    public void clear(){
        this.organisations = new Organisations();
        this.contacts = new Contacts();
    }

    public PDService getPDS() {
        return this.PDS;
    }

}
