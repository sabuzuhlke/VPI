package VPI;

import VPI.PDClasses.PDContactReceived;
import VPI.PDClasses.PDOrganisation;
import VPI.PDClasses.PDService;
import VPI.VClasses.InsightService;
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

    private void importContacts() {
        getAllContacts();
        compareContacts();
        pushContacts();
    }

    private void getAllOrganisations() {
        this.organisations.vOrganisations = IS.getAllOrganisations().getBody().getItems();
        this.organisations.pdOrganisations = PDS.getAllOrganisations().getBody().getData();
    }

    private void getAllContacts() {
        //this.contacts.vContacts = IS.getAllContacts(organisations.vOrganisations);
        //this.contacts.pdContacts = PDS.getAllContacts(organisations.pdOrganisations);
    }

    public void compareOrganisations() {

        for(VOrganisation v : organisations.vOrganisations){
            Boolean matched = false;
            Boolean modified = false;
            PDOrganisation temp = null;
            for(PDOrganisation p : organisations.pdOrganisations){
                if(v.getName().equals(p.getName())){
                    matched = true;

                    //Resolve attributes of organisation
                    //address
                    if (!v.getFormattedAddress().equals(p.getAddress())) {
                        modified = true;
                        p.setAddress(v.getFormattedAddress());
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
        }
    }

    private void compareContacts() {

    }

    private List<Long> pushOrganisations() {
        List<Long> idsPosted = PDS.postOrganisationList(organisations.postList);
        List<Long> idsPutted = PDS.putOrganisationList(organisations.putList);

        List<Long> idsPushed = new ArrayList<>(idsPosted);
        idsPushed.addAll(idsPutted);
        return idsPushed;
    }

    private List<Long> pushContacts() {
        return new ArrayList<>();
    }

    public void clear(){
        this.organisations = new Organisations();
        this.contacts = new Contacts();
    }

    public PDService getPDS() {
        return this.PDS;
    }

}
