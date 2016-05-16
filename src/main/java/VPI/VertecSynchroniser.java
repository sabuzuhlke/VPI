package VPI;

import VPI.PDClasses.*;
import VPI.PDClasses.Contacts.ContactDetail;
import VPI.PDClasses.Contacts.PDContactReceived;
import VPI.PDClasses.Contacts.PDContactSend;
import VPI.PDClasses.Deals.PDDealSend;
import VPI.PDClasses.Organisations.PDOrganisationReceived;
import VPI.PDClasses.Organisations.PDOrganisationResponse;
import VPI.PDClasses.Organisations.PDOrganisationSend;
import VPI.PDClasses.Organisations.PDRelationship;
import VPI.PDClasses.Users.PDUser;
import VPI.VertecClasses.VertecOrganisations.JSONContact;
import VPI.VertecClasses.VertecOrganisations.JSONOrganisation;
import VPI.VertecClasses.VertecProjects.JSONPhase;
import VPI.VertecClasses.VertecProjects.JSONProject;
import VPI.VertecClasses.VertecService;
import VPI.VertecClasses.VertecOrganisations.ZUKResponse;
import org.springframework.http.ResponseEntity;

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
    //map of v_id's to p_ids used for building relationship heirarchy
    private Map<Long, Long> idMap;


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
        this.idMap = new HashMap<>();
    }

    public List<List<Long>> importOrganisationsAndContactsToPipedrive() {

        long startTime = System.nanoTime();
        //get all Vertec Data
        System.out.println("Getting ZUK data from vertec");
        ZUKResponse allVertecData = VS.getZUKinfo().getBody();
        long zukEndTime = System.nanoTime();
        System.out.println("Took " + ((zukEndTime - startTime)/1000000) + " milliseconds");

        Set<String> v_emails = getVertecUserEmails(allVertecData);
        List<PDUser> pd_users= getPipedriveUsers();

        //constructTeamIdMap(v_emails, pd_users); //TODO: use this instead of constructTestTeamMap() on deployment
        System.out.println("Contructing hardcoded team map");
        constructTestTeamMap();
        long teamEnd = System.nanoTime();
        System.out.println("Took " + ((teamEnd - zukEndTime)/1000000) + " milliseconds");

        //get all Pipedrive organisations
        System.out.println("Getting all organisations from pipedrive");
        List<PDOrganisationReceived> pipedriveOrgs = PDS.getAllOrganisations().getBody().getData();
        long pdorgTime = System.nanoTime();
        System.out.println("Took " + ((pdorgTime - teamEnd)/1000000) + " milliseconds");

        //compare pipedrive orgs along with nested contacts, removing nested contacts from contacts
        System.out.println("About to attempt to resolve organisations and their nested contacts");
        resolveOrganisationsAndNestedContacts(allVertecData.getOrganisationList(), pipedriveOrgs);
        long resolveOrgTime = System.nanoTime();
        System.out.println("Took " + ((resolveOrgTime - pdorgTime)/1000000) + " milliseconds");

        //get all pipedrive contacts, filter to only use those without organisations
        System.out.println("Getting all contacts from pipedrive");
        List<PDContactReceived> pipedriveContacts = PDS.getAllContacts().getBody().getData();
        long pdcontTime = System.nanoTime();
        System.out.println("Took " + ((pdcontTime - resolveOrgTime)/1000000) + " milliseconds");
        System.out.println("Getting all contacts (not attached to organisations) from vertec from ZUK info");
        List<PDContactReceived> contactsWithoutOrg = filterContactsWithOrg(pipedriveContacts);
        long vcontTime = System.nanoTime();
        System.out.println("Took " + ((vcontTime - pdcontTime)/1000000) + " milliseconds");

        //compare dangling vcontacts to leftover pdcontacts
        System.out.println("About to compare contacts");
        compareContacts(allVertecData.getDanglingContacts(), contactsWithoutOrg);
        long compCTime = System.nanoTime();
        System.out.println("Took " + ((compCTime - vcontTime)/1000000) + " milliseconds");


        //initialize return list
        List<List<Long>> ids = new ArrayList<>();

        //now ready to post/put
        System.out.println("Posting Organisations");
        List<List<Long>> orgsNConts = postVOrganisations();
        long orgPostTime = System.nanoTime();
        System.out.println("Took " + ((orgPostTime - compCTime)/1000000) + " milliseconds");
        System.out.println("Posted: " + orgsNConts.get(0).size() + " orgs, and " + orgsNConts.get(1).size() + " contacts");
        System.out.println("Putting Organisations");
        List<Long> orgsPut = putPdOrganisations();
        long orgPutTime = System.nanoTime();
        System.out.println("Took " + ((orgPutTime - orgPostTime)/1000000) + " milliseconds");
        System.out.println("Putted:" + orgsPut.size());
        System.out.println("Posting Dangling Contacts");
        List<Long> contsPost = postContacts();
        long contPostTime = System.nanoTime();
        System.out.println("Took " + ((contPostTime - orgPutTime)/1000000) + " milliseconds");
        System.out.println("Posted: " + contsPost.size());
        System.out.println("Putting Dangling Contacts");
        List<Long> contsPut = putContacts();
        long contputTime = System.nanoTime();
        System.out.println("Took " + ((contputTime - contPostTime)/1000000) + " milliseconds");
        System.out.println("Putted: " + contsPut.size());

        //get list of pd relationships, then post them
        System.out.println("Building relationship hierarchy");
        List<PDRelationship> relationships = getOrganistionHeirarchy(allVertecData.getOrganisationList());
        long relTime = System.nanoTime();
        System.out.println("Took " + ((relTime - contputTime)/1000000) + " milliseconds");
        System.out.println("Found " + relationships.size() + " relationships, now posting them");
        postRelationshipList(relationships);
        long relPostTime = System.nanoTime();
        System.out.println("Took " + ((relPostTime - relTime)/1000000) + " milliseconds");

        //return list of orgs and contact ids that have been posted/edited to pipedrive
        ids.add(orgsNConts.get(0));
        ids.add(orgsPut);
        ids.add(orgsNConts.get(1));
        ids.add(contsPost);
        ids.add(contsPut);

        System.out.println("Done!");

        long endTime = System.nanoTime();
        System.out.println("Took " + ((endTime - startTime)/6000000000L) + " milliseconds");

        return ids;
    }

    public List<Long> importProjectsAndPhasesToPipedrive() {

        List<JSONProject> projects = VS.getZUKProjects().getBody().getProjects();

        List<PDDealSend> dealsToPost = createDealObjects(projects);

        return new ArrayList<>();

    }

    public List<PDDealSend> createDealObjects(List<JSONProject> projects) {

        List<PDDealSend> deals = new ArrayList<>();

        for(JSONProject project : projects) {

            for(JSONPhase phase : project.getPhases()) {

                PDDealSend deal = new PDDealSend();
                /**
                 * must set:
                 * title --------- check
                 * value --------- check
                 * currency ------ check
                 * user_id ------- check
                 * person_id -----
                 * org_id -------- done (assuming idMap is populated (check))
                 * stage_id ------
                 * lost_reason?---
                 * status --------
                 * add_time ------
                 * visible_to ----
                 * v_id ---------- done
                 * zuhlke_office--
                 * lead_type -----
                 * project_number- done
                 * phase --------- done
                 * cost ----------
                 * cost_currency -
                 */

                //TODO: ensure title is set correctly
                //title
                String title = project.getTitle() + ": " + phase.getDescription();
                deal.setTitle(title);

                //value
                String value = phase.getExternalValue();
                deal.setValue(value);

                //currency
                String currency = "GBP";
                deal.setCurrency(currency);

                //sets the person responsible to be the leader of the phase,
                //but if this is null the leader of whole project? or check other phase owners?;
                // TODO: check other phases for user_id if null
                Long user_id = teamIdMap.get(phase.getPersonResponsible());
                deal.setUser_id(user_id);

                //person_id TODO: build map of customer v_id to p_id before this is called
                deal.setPerson_id(customerMap.get(project.getCustomerRef()));

                //org_id
                deal.setOrg_id(idMap.get(project.getClientRef()));

                //stage_id

                //lost_reason

                //status ('open' = Open, 'won' = Won, 'lost' = Lost, 'deleted' = Deleted)

                //add_time TODO: add creationDateTime to VRAPI and VPI

                //visible_to (1 = owner and followers, 3 = everyone)
                deal.setVisible_to(3);

                //v_id
                deal.setV_id(phase.getV_id());

                //zuhlke_office TODO: somehow get this from VRAPI

                //lead_type

                //project number
                deal.setProject_number(project.getCode());

                //phase
                String dealPhase = phase.getCode();
                deal.setPhase(dealPhase);

                //cost

                //cost_currency TODO: Get and set currency;




                //add deal to list
                deals.add(deal);
            }


        }

        return deals;
    }


    public List<PDRelationship> getOrganistionHeirarchy(List<JSONOrganisation> orgs) {

        List<PDRelationship> rels = new ArrayList<>();

        for (JSONOrganisation org : orgs) {

            Long childOrgPId = idMap.get(org.getObjid());
            Long parentOrgPId = idMap.get(org.getParentOrganisationId());

            if (childOrgPId != null && parentOrgPId != null) {

                PDRelationship rel = new PDRelationship(parentOrgPId, childOrgPId);
                rels.add(rel);

            }

        }

        return rels;

    }

    //TODO: at some point work out exactly what eachof the post functions should return;
    public void postRelationshipList(List<PDRelationship> relationships) {

        for (PDRelationship rel : relationships) {

            PDS.postOrganisationRelationship(rel);

        }

    }

    public void resolveOrganisationsAndNestedContacts(List<JSONOrganisation> vOrgs, List<PDOrganisationReceived> pOrgs) {
        for(JSONOrganisation vo : vOrgs){
            Boolean matched = false;
            for(PDOrganisationReceived po : pOrgs){
                if(po.getV_id() == null) continue;
                if(vo.getObjid().longValue() == po.getV_id().longValue()){
                    matched = true;
                    compareOrganisationDetails(vo, po);

                    //TODO: Change so that all contacts get compared as to handle contacts chaning organisations
                    resolveContactsForOrgs(vo,po);
                }
            }
            if(!matched){
                organisationPostList.add(vo);
            }

        }
    }
    public void testresolveOrganisationsAndNestedContacts(List<JSONOrganisation> vOrgs, List<PDOrganisationReceived> pOrgs) {
        for(JSONOrganisation vo : vOrgs){
            Boolean matched = false;
            for(PDOrganisationReceived po : pOrgs){
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

    private Boolean compareOrganisationDetails(JSONOrganisation vo, PDOrganisationReceived po){
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

    public void resolveTestContactsForOrgs(JSONOrganisation jo, PDOrganisationReceived po){
    }

    private void resolveContactsForOrgs(JSONOrganisation jo, PDOrganisationReceived po){
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
                newContact.setFollowers(new ArrayList<>());
                for(String f : vc.getFollowers()){
                    newContact.getFollowers().add(teamIdMap.get(f));
                }
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

            idMap.put(o.getObjid(), res.getBody().getData().getId());

            for(JSONContact c : o.getContacts()){
                Long owner = teamIdMap.get(c.getOwner());
                PDContactSend s = new PDContactSend(c,owner);
                s.setFollowers(new ArrayList<>());
                for(String f : c.getFollowers()){
                    s.getFollowers().add(teamIdMap.get(f));
                }
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

    public Map<Long, Long> getIdMap() {
        return idMap;
    }

    public void setIdMap(Map<Long, Long> idMap) {
        this.idMap = idMap;
    }
}
