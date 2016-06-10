package VPI;

import VPI.PDClasses.Contacts.*;
import VPI.PDClasses.Deals.PDDealItemsResponse;
import VPI.PDClasses.Deals.PDDealReceived;
import VPI.PDClasses.Organisations.PDOrganisationSend;
import VPI.PDClasses.Organisations.PDRelationship;
import VPI.PDClasses.PDService;
import VPI.PDClasses.Users.PDUser;
import VPI.VertecClasses.VertecActivities.JSONActivity;
import VPI.VertecClasses.VertecActivities.ZUKActivities;
import VPI.VertecClasses.VertecOrganisations.JSONContact;
import VPI.VertecClasses.VertecOrganisations.JSONOrganisation;
import VPI.VertecClasses.VertecOrganisations.ZUKOrganisations;
import VPI.VertecClasses.VertecProjects.JSONPhase;
import VPI.VertecClasses.VertecProjects.JSONProject;
import VPI.VertecClasses.VertecProjects.ZUKProjects;
import VPI.VertecClasses.VertecService;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

/**
 * Created by gebo on 07/06/2016.
 */
public class Importer {

    //Services to access both APIs
    private PDService pipedrive;
    private VertecService vertec;

    public Map<Long, Long> organisationIdMap;
    public Map<Long, Long> contactIdMap;
    public Map<Long, Long> dealIdMap;
    public Map<String, Long> teamIdMap;

    private Map<String, String> activityTypeMap;

    private ZUKOrganisations vertecOrganisations;
    private ZUKProjects vertecProjects;
    private ZUKActivities vertecActivities;

    public Set<Long> missingOrganisationIds;
    public Set<Long> missingContactIds;
    private List<PDContactReceived> pipedriveContacts;
    private PDDealItemsResponse pipedriveDeals;

    public List<PDOrganisationSend> organisationPostList;

    public List<PDContactSend> contactPostList;
    public List<PDContactSend> contactPutList;

    public List<PDFollower> followerPostList;

    public Importer(PDService pipedrive, VertecService vertec) {
        this.pipedrive = pipedrive;
        this.vertec    = vertec;

        this.organisationIdMap = new HashMap<>();
        this.contactIdMap      = new HashMap<>();
        this.dealIdMap         = new HashMap<>();

        constructTestTeamMap();

        this.activityTypeMap   = new HashMap<>();

        this.vertecOrganisations    = new ZUKOrganisations();
        this.vertecProjects         = new ZUKProjects();
        this.vertecActivities       = new ZUKActivities();

        this.missingOrganisationIds = new HashSet<>();
        this.missingContactIds      = new HashSet<>();

        this.organisationPostList = new ArrayList<>();

        this.contactPutList = new ArrayList<>();
        this.contactPostList = new ArrayList<>();

        this.followerPostList = new ArrayList<>();

    }

    /**
     * Call this function to import all ZUK data from Vertec to Pipedrive
     */
    public void importToPipedrive() {
        //import relevant information from vertec
        importOrganisationsAndContactsFromVertec();
        importDealsFromVertec();
        importActivitiesFromVertec();
        //find missing organisations and contacts linked to deals and activities
        importMissingOrganistationsFromVertec();
        importMissingContactsFromVertec();
        //import contacts and deals from pipedrive to compare to vertec contacts and deals
        importContactsFromPipedrive();
        importDealsFromPipedrive();
        //create and post organisatations from vertec to pipedrive, extract hierarchy and post
        populateOrganisationPostList();
        postOrganisationPostList();
        postOrganisationHierarchies(
                builOrganisationHierarchies());
        //compare contacts by email and populate post and put lists, send to pipedrive and post followers
        populateContactPostAndPutLists();
        postAndPutContactPostAndPutLists();
        populateFollowerPostList();
        postContactFollowers();
        //compare deals by phase number and project code and populate post and put lists, send to pipedrive
        populateDealPostAndPutList();
        postAndPutDealPostAndPutList();
        //craete and post activites once we have all vertec to pipedrive id maps populated from posting
        populateActivityPostList();
        postActivityPostList();
    }

    public void runOrgImport() {
        System.out.println("Start");
        importOrganisationsAndContactsFromVertec();
        System.out.println("imported organisations and contacts");
        importDealsFromVertec();
        System.out.println("imported deals");
        importActivitiesFromVertec();
        System.out.println("imported activities");
        //find missing organisations and contacts linked to deals and activities
        importMissingOrganistationsFromVertec();
        System.out.println("imported missing orgs");
        importMissingContactsFromVertec();
        System.out.println("imported missing contacts");
        //import contacts and deals from pipedrive to compare to vertec contacts and deals
        importContactsFromPipedrive();
        System.out.println("imported pd contacts");
        importDealsFromPipedrive();
        System.out.println("imported pd deals");
        //create and post organisatations from vertec to pipedrive, extract hierarchy and post
        populateOrganisationPostList();
        System.out.println("populated post list");
        postOrganisationPostList();
        System.out.println("posted orgs");
        postOrganisationHierarchies(
                builOrganisationHierarchies());
        System.out.println("posted hierarchy");
        System.out.println("End");
    }

    public void importOrganisationsAndContactsFromVertec() {
        this.vertecOrganisations = vertec.getZUKOrganisations().getBody();
        vertecOrganisations.getOrganisationList().stream()
                .map(JSONOrganisation::getObjid)
                .forEach(vertecID -> organisationIdMap.put(vertecID, -1L));
        vertecOrganisations.getOrganisationList().stream()
                .map(JSONOrganisation::getContacts)
                .flatMap(Collection::stream)
                .map(JSONContact::getObjid)
                .forEach(vertecID -> contactIdMap.put(vertecID, -1L));
        vertecOrganisations.getDanglingContacts().stream()
                .map(JSONContact::getObjid)
                .forEach(id -> contactIdMap.put(id, -1L));
    }

    public void importDealsFromVertec() {
        this.vertecProjects =  vertec.getZUKProjects().getBody();
        vertecProjects.getProjects().stream()
                .map(JSONProject::getPhases)
                .flatMap(Collection::stream)
                .map(JSONPhase::getV_id)
                .forEach(vertecID -> dealIdMap.put(vertecID, -1L));
    }

    public void importActivitiesFromVertec() {
        this.vertecActivities = vertec.getZUKActivities().getBody();
    }

    public void importMissingOrganistationsFromVertec() {
        extractListOfMissingOrganisationIds().stream()
                .forEach(id -> {
                    try {
                        JSONOrganisation org = vertec.getOrganisation(id).getBody();
                        vertecOrganisations.getOrganisationList().add(org);
                        organisationIdMap.put(org.getObjid(), -1L);
                        missingOrganisationIds.add(org.getObjid());
                        //checks for missing parent organisation and retrieves from vertec, then checck parent of newly imported org
                        Long parentID = org.getParentOrganisationId();
                        Boolean parentOrgNeeded = parentID != null && !organisationIdMap.containsKey(parentID);
                        while (parentOrgNeeded) {
                            JSONOrganisation orgParent = vertec.getOrganisation(parentID).getBody();
                            vertecOrganisations.getOrganisationList().add(orgParent);
                            organisationIdMap.put(orgParent.getObjid(), -1L);
                            missingOrganisationIds.add(org.getObjid());
                            parentID = orgParent.getParentOrganisationId();
                            parentOrgNeeded = parentID != null && !organisationIdMap.containsKey(parentID);
                        }
                        org.getContacts().stream()
                                .map(JSONContact::getObjid)
                                .forEach(contactID -> {
                                    contactIdMap.put(contactID, -1L);
                                    missingContactIds.add(contactID);
                                });
                    } catch (HttpClientErrorException e) { //TODO: work out how to test this properly
                        if (e.getStatusCode() != HttpStatus.NOT_FOUND) {
                            throw e;
                        }
                    }
                });
    }

    public Set<Long> extractListOfMissingOrganisationIds() {
        //build list of ids from dangling contacts
        Set<Long> idsOfMissingOrganisations = vertecOrganisations.getDanglingContacts().stream()
                .map(JSONContact::getOrganisation)
                .filter(organisationID -> organisationID != null && organisationIdMap.get(organisationID) == null)
                .collect(toSet());

        //extract parent organisation ids that we do not have
        getVertecOrganisationList().stream()
                .filter(org -> org.getParentOrganisationId() != null)
                .filter(org -> organisationIdMap.get(org.getParentOrganisationId()) == null)
                .forEach(org -> {
                    idsOfMissingOrganisations.add(org.getParentOrganisationId());
                });

        //add ids from deals
        getVertecProjectList().stream()
                .filter(project -> (project.getCustomerRef() != null || project.getClientRef() != null))
                .forEach(project -> {
                    Long clientRef = project.getClientRef();
                    if (clientRef != null && organisationIdMap.get(clientRef) == null && contactIdMap.get(clientRef) == null) {
                        idsOfMissingOrganisations.add(clientRef);
                    }
                    Long customerRef = project.getCustomerRef();
                    if (customerRef != null && organisationIdMap.get(customerRef) == null && contactIdMap.get(customerRef) == null) {
                        idsOfMissingOrganisations.add(customerRef);
                    }
                });

        getVertecActivityList().stream()
                .filter(activity -> activity.getCustomer_link() != null)
                .forEach(activity -> {
                    Long customerLink = activity.getCustomer_link();
                    if (organisationIdMap.get(customerLink) == null && contactIdMap.get(customerLink) == null) {
                        idsOfMissingOrganisations.add(customerLink);
                    }
                });

        return idsOfMissingOrganisations;
    }

    public void importMissingContactsFromVertec() {
        extractListOfMissingContactIds().stream()
                .forEach(id -> {
                    try {
                        JSONContact contact = vertec.getContact(id).getBody();
                        vertecOrganisations.getDanglingContacts().add(contact);
                        contactIdMap.put(contact.getObjid(), -1L);
                        missingContactIds.add(contact.getObjid());
                        //TODO: check if we should import organisation containing this missing contact (can be done calling importMissingOrganisations again)
                    } catch (HttpClientErrorException e) {
                        System.out.println("Caught Exception getting contact from Vertec"); //TODO: work out how to test this properly
                        if (e.getStatusCode() != HttpStatus.NOT_FOUND) {
                            throw e;
                        }
                    }
                });
    }

    public Set<Long> extractListOfMissingContactIds() {
        Set<Long> idsOfMissingContacts = new HashSet<>();

        getVertecProjectList().stream()
                .filter(project -> project.getClientRef() != null || project.getCustomerRef() != null)
                .forEach(project -> {
                    Long clientRef = project.getClientRef();
                    if(clientRef != null && contactIdMap.get(clientRef) == null && organisationIdMap.get(clientRef) == null){
                        idsOfMissingContacts.add(clientRef);
                    }
                    Long customerRef = project.getCustomerRef();
                    if(customerRef != null && contactIdMap.get(customerRef) == null && organisationIdMap.get(customerRef) == null){
                        idsOfMissingContacts.add(customerRef);
                    }

                });

        getVertecActivityList().stream()
                .filter(act -> act.getCustomer_link() != null)
                .forEach(act -> {
                    Long custpmerId = act.getCustomer_link();
                    if(contactIdMap.get(custpmerId) == null && organisationIdMap.get(custpmerId) == null){
                        idsOfMissingContacts.add(custpmerId);
                    }
                });

        return idsOfMissingContacts;
    }

    public void importContactsFromPipedrive() {
        this.pipedriveContacts = pipedrive.getAllContacts().getBody().getData();
        for(PDContactReceived pr : this.pipedriveContacts){
            for (ContactDetail email : pr.getEmail()) {
                email.setValue(email.getValue().toLowerCase());
            }
        }
    }

    public void importDealsFromPipedrive() {
        this.pipedriveDeals = pipedrive.getAllDeals().getBody();
    }

    public void populateOrganisationPostList() {
        this.organisationPostList = getVertecOrganisationList().stream()
                .map(this::convertToPDSend)
                .collect(toList());
    }

    public PDOrganisationSend convertToPDSend(JSONOrganisation jsonOrganisation) {
        Long ownerId = teamIdMap.get(jsonOrganisation.getOwner());
        return new PDOrganisationSend(jsonOrganisation, ownerId);
    }

    public void postOrganisationPostList() {
        List<Long> postedIds = pipedrive.postOrganisationList(organisationPostList);
        if (postedIds.size() == organisationPostList.size()) {
            IntStream.range(0, postedIds.size())
                    .forEach(i -> organisationIdMap.put(organisationPostList.get(i).getV_id(), postedIds.get(i)));
        } else {
            System.out.println("Could not add organisations to map, list sizes were not equal");
        }
    }

    public List<PDRelationship> builOrganisationHierarchies() {
        return getVertecOrganisationList().stream()
                .filter(org -> org.getParentOrganisationId() != null)
                .map(org -> new PDRelationship(organisationIdMap.get(org.getParentOrganisationId()), organisationIdMap.get(org.getObjid())))
                .collect(toList());
    }

    public void postOrganisationHierarchies(List<PDRelationship> relationships) {
        relationships.stream()
                .forEach(pipedrive::postOrganisationRelationship);
    }

    public void populateContactPostAndPutLists() {
        Map<Long, Boolean> matchedMap = new HashMap<>();
        int putcount = 0;
        int postcount = 0;
        Map<String, Integer> emailCountMap = new HashMap<>();
        getVertecContactList().stream()
                .map(JSONContact::getEmail)
                .forEach(email -> {
                    Integer count = emailCountMap.get(email);
                    count = (count == null) ? 1 : count + 1;
                    emailCountMap.put(email, count);
                });
//        Map<Long, Integer> pipedriveMatchedCount = new HashMap<>();
        for(JSONContact jc : getVertecContactList()) {
            if (jc.getEmail() == null || jc.getEmail().equals("")) {
                this.contactPostList.add(createPDContactSend(jc));
                continue;
            }
            int match = 0;
            PDContactReceived temp = null;
            List<PDContactReceived> matched = new ArrayList<>();
            for(PDContactReceived pc :
                    getPipedriveContactList()) {


                if(pc.getEmail().stream()
                        .map(ContactDetail::getValue)
                        .collect(toSet())
                        .contains(jc.getEmail())) {

                    match++;
                    if (match == 1) {
                        temp = pc;
                        matched.add(pc);
                    } else {
                        matched.add(pc);
                    }
//
//                    Integer count = pipedriveMatchedCount.get(pc.getId());
//                    count = (count == null) ? 1 : count + 1;
//                    pipedriveMatchedCount.put(pc.getId(), count);

                }
            }
//            if (matched.size() > 1) {
//                System.out.println(matched.size() + " Matches found for contact with email: " + jc.getEmail());
//            }
            if (match > 0) {
                if (matchedMap.get(temp.getId()) != null || emailCountMap.get(jc.getEmail()) > 1 /*|| pipedriveMatchedCount.get(temp.getId()) > 1*/) {
//                    System.out.println("Found pd contact already being put to: " + jc.getFirstName() + " " + jc.getSurname());
                    this.contactPostList.add(createPDContactSend(jc));
                } else {
                    putcount++;
                    this.contactPutList.add(createPDContactSend(jc, temp));
                    matchedMap.put(temp.getId(), true);
                }
            } else {
                postcount++;
                this.contactPostList.add(createPDContactSend(jc));
            }
        }
//        System.out.println("Put:" + putcount + ", Post:" + postcount);
    }

    private PDContactSend createPDContactSend(JSONContact jc, PDContactReceived pc) {
        //TODO: if names coming back empty then handle null names
        if(vertecDateMoreRecentThanPdDate(jc.getModified(), pc.getModifiedTime())){
            return getPdContactSendVertecMoreRecent(jc, pc);
        } else {
            return getPdContactSendPipedriveMoreRecent(jc, pc);
        }
    }

    private PDContactSend getPdContactSendPipedriveMoreRecent(JSONContact jc, PDContactReceived pc) {
        PDContactSend ps = new PDContactSend();
        //id
        ps.setId(pc.getId());
        if(pc.getName() != null && ! pc.getName().isEmpty()) ps.setName(pc.getName());
        else  ps.setName(jc.getFirstName() + " " + jc.getSurname());
        //Org_id
        if(pc.getOrg_id() != null) ps.setOrg_id(pc.getOrg_id().getValue());
        else ps.setOrg_id(organisationIdMap.get(jc.getOrganisation()));
        //Email
        ps.setEmail(pc.getEmail());//assumes this function is only called from populateContactputAnfPostlist
        //phone
        ps.setPhone(pc.getPhone());
        if(ps.getPhone() != null &&! ps.getPhone().stream()
                .map(ContactDetail::getValue)
                .collect(toSet())
                .contains(jc.getPhone())) {
            ps.getPhone().add(new ContactDetail(jc.getPhone(),false));
        }
        //visible_to
        ps.setVisible_to(3);
        //active
        ps.setActive_flag(ps.getActive_flag());
        //v_id
        ps.setV_id(jc.getObjid());
        //creationTime
        ps.setCreationTime(jc.getPipedriveCreationTime());
        //modified pipedrive takes care of this

        //owner
        ps.setOwner_id(pc.getOwner_id().getId());

        return ps;
    }

    private PDContactSend getPdContactSendVertecMoreRecent(JSONContact jc, PDContactReceived pc) {
        PDContactSend ps = new PDContactSend();
        //id
        ps.setId(pc.getId());
        //Name
        if(!jc.getFirstName().isEmpty() && !jc.getSurname().isEmpty()){
            ps.setName(jc.getFirstName() + " " + jc.getSurname());
        } else {
            ps.setName(pc.getName());
        }
        //Org_id
        if(jc.getOrganisation() != null) ps.setOrg_id(organisationIdMap.get(jc.getOrganisation()));
        else if(pc.getOrg_id() != null) ps.setOrg_id(pc.getOrg_id().getValue());
        //Email
        ps.setEmail(pc.getEmail());//assumes this function is only called from populateContactputAnfPostlist
        //phone
        ps.setPhone(pc.getPhone());
        if(! ps.getPhone().stream()
                .map(ContactDetail::getValue)
                .collect(toSet())
                .contains(jc.getPhone())) {
            ps.getPhone().add(new ContactDetail(jc.getPhone(),false));
        }
        //visible_to
        ps.setVisible_to(3);
        //active
        ps.setActive_flag(ps.getActive_flag());
        //v_id
        ps.setV_id(jc.getObjid());
        //creationTime
        ps.setCreationTime(jc.getPipedriveCreationTime());
        //modified pipedrive takes care of this

        //owner
        if(jc.getOwner() != null && !jc.getOwner().isEmpty()){
            ps.setOwner_id(teamIdMap.get(jc.getOwner()));
        } else {
            pc.getOwner_id().getId();
        }

        return ps;
    }

    public boolean vertecDateMoreRecentThanPdDate(String vDate, String pDate) {
        DateTimeFormatter p = DateTimeFormatter.ofPattern("yyyy-MM-dd' 'HH:mm:ss");
        DateTimeFormatter v = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

        LocalDateTime pt = LocalDateTime.from(p.parse(pDate));
        LocalDateTime vt = LocalDateTime.from(v.parse(vDate));
        return pt.isBefore(vt);
    }

    private PDContactSend createPDContactSend(JSONContact jc) {
        PDContactSend ps = new PDContactSend(jc, teamIdMap.get(jc.getOwner()));
        ps.setOrg_id(organisationIdMap.get(jc.getOrganisation()));

        return ps;
    }

    public void postAndPutContactPostAndPutLists() {
        contactIdMap.putAll(pipedrive.postContactList(contactPostList));
        contactIdMap.putAll(pipedrive.putContactList(contactPutList));
    }

    public void populateFollowerPostList(){
        followerPostList = getVertecContactList().stream()
                .map(contact -> contact.getFollowers().stream()
                        .map(follower -> new PDFollower(contactIdMap.get(contact.getObjid()), teamIdMap.get(follower)))
                        .collect(toList()))
                .flatMap(Collection::stream)
                .collect(toList());
    }

    public void postContactFollowers() {
        followerPostList.stream()
                .forEach(pipedrive::postFollowerToContact);
    }

    public void populateDealPostAndPutList() {
    }

    public void postAndPutDealPostAndPutList() {
    }

    public void populateActivityPostList() {
    }

    public void postActivityPostList() {
    }

    public List<JSONOrganisation> getVertecOrganisationList() {
        return vertecOrganisations.getOrganisationList();
    }

    public List<JSONContact> getVertecContactList() {
        List<JSONContact> contacts = new ArrayList<>();
        vertecOrganisations.getOrganisationList().stream()
                .map(JSONOrganisation::getContacts)
                .forEach(contacts::addAll);
        contacts.addAll(vertecOrganisations.getDanglingContacts());
        return contacts;
    }

    public List<JSONProject> getVertecProjectList() {
       return vertecProjects.getProjects();
    }

    public List<JSONActivity> getVertecActivityList() {
        return vertecActivities.getActivityList();
    }

    public List<PDContactReceived> getPipedriveContactList() {
        return pipedriveContacts;
    }

    public List<PDDealReceived> getPipedriveDealList() {
        return pipedriveDeals.getData();
    }

    @SuppressWarnings("all") //TODO: replace with actual solution
    private void constructTestTeamMap() {
        Map<String,Long> map = new DefaultHashMap<>(1363410L);

        map.put("wolfgang.emmerich@zuhlke.com", 1363410L); //Wolfgang
        map.put("tim.cianchi@zuhlke.com", 1363402L); //Tim
        map.put("neil.moorcroft@zuhlke.com", 1363429L); //Neil
        map.put("mike.hogg@zuhlke.com", 1363424L); //Mike
        map.put("justin.cowling@zuhlke.com", 1363416L); //Justin
        map.put("brewster.barclay@zuhlke.com", 1363403L); //Brewster
        map.put("keith.braithwaite@zuhlke.com", 1363488L); //Keith
        map.put("peter.brown@zuhlke.com", 1415840L); //Peter Brown
        map.put("steve.freeman@zuhlke.com", 1415845L); //Steve Freeman
        map.put("john.seston@zuhlke.com", 1424149L); //John Seston
        map.put("sabine.streuss@zuhlke.com", 1424149L); //Sabine
        map.put("sabine.strauss@zuhlke.com", 1424149L); //Sabine
        map.put("ileana.meehan@zuhlke.com", 1424149L); //Ileana
        map.put("ina.hristova@zuhlke.com", 1424149L); //Ina
        map.put(null, 1363410L);

        this.teamIdMap = map;
    }

    @SuppressWarnings("all")
    public void constructTeamIdMap(Set<String> v_emails, List<PDUser> pd_users) {//TODO: write test for this and complete
        for (String v_email : v_emails) {
            Boolean mapped = false;
            for (PDUser pd_user : pd_users) {
                if (v_email.toLowerCase().equals(pd_user.getEmail().toLowerCase())) {
                    this.teamIdMap.put(v_email, pd_user.getId());
                    mapped = true;
                }
            }
            if (!mapped) {
                this.teamIdMap.put(v_email, 1363410L); //TODO: replace id with appropriate id, wolfgangs or admin?
            }
        }
    }
}
