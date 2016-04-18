package VPI;

import VPI.PDClasses.*;
import VPI.VClasses.InsightService;
import VPI.VClasses.VContact;
import VPI.VClasses.VOrganisation;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sabu on 15/04/2016.
 */
public class Synchroniser {

    private PDService PDS;
    private InsightService IS;

    public Organisations organisations;
    public Contacts contacts;

    public Synchroniser(String PDserver, String Vserver) {
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

    public void importToPipedrive() {
        importOrganisations();
        importContacts();
    }

    public List<Long> importOrganisations() {
        getAllOrganisations();
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
            List<VContact> vC = IS.getContactsForOrganisation(v.getId()).getBody().getItems();
            List<PDContactReceived> pC = PDS.getContactsForOrganisation(v.getPd_id()).getBody().getData();

            compareContacts(vC, pC);
        }

        for(PDOrganisation p : organisations.putList) {
            List<VContact> vC = IS.getContactsForOrganisation(p.getV_id()).getBody().getItems();
            List<PDContactReceived> pC = PDS.getContactsForOrganisation(p.getId()).getBody().getData();

            compareContacts(vC, pC);
        }
    }

    private void addContactsForNewOrganisationsToPostList() {
        for(PDOrganisation p : organisations.postList) {
            List<VContact> contactsForOrg = IS.getContactsForOrganisation(p.getV_id()).getBody().getItems();
            for(VContact c : contactsForOrg) {
                contacts.postList.add(new PDContactSend(c));
            }
        }
    }

    private void getAllOrganisations() {
        this.organisations.vOrganisations = IS.getAllOrganisations().getBody().getItems();
        this.organisations.pdOrganisations = PDS.getAllOrganisations().getBody().getData();
    }

    public void compareOrganisations() {

        for(VOrganisation v : organisations.vOrganisations){
            Boolean matched = false;
            Boolean modified = false;
            PDOrganisation temp = null;
            for(PDOrganisation p : organisations.pdOrganisations){
                if(v.getName().equals(p.getName())){
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
            if(!matched){
                organisations.postList.add(new PDOrganisation(v));
            }
            if(modified){
                organisations.putList.add(temp);
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

                    //resolve email and phone lists of contact
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
        Boolean modified = false;
        for (ContactDetail vph : v.getPhone()) {
            Boolean matched = false;
            for(ContactDetail pph : p.getPhone()) {
                if (vph.getValue().equals(pph.getValue())) {
                    matched = true;
                    if(vph.getPrimary() != pph.getPrimary()){
                        pph.setPrimary(vph.getPrimary());
                        modified = true;
                    }
                }
            }
            if (!matched) {
                modified = true;
                p.getPhone().add(vph);
            }
            if (vph.getPrimary()) {
                for(ContactDetail pphones : p.getPhone()) {
                    if(!pphones.getValue().equals(vph.getValue())) {
                        pphones.setPrimary(false);
                    }
                }
            }
        }

        for (ContactDetail ve : v.getEmail()) {
            Boolean matched = false;
            for(ContactDetail pe : p.getEmail()) {
                if (ve.getValue().equals(pe.getValue())) {
                    matched = true;
                    if((ve.getPrimary() != pe.getPrimary())) {
                        pe.setPrimary(ve.getPrimary());
                        modified = true;
                    }
                }
            }
            if (!matched) {
                modified = true;
                p.getEmail().add(ve);
            }
            if (ve.getPrimary()) {
                for(ContactDetail pemails : p.getEmail()) {
                    if(!pemails.getValue().equals(ve.getValue())) {
                        pemails.setPrimary(false);
                    }
                }
            }
        }
        return modified;
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
