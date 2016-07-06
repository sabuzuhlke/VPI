package VPI;

import VPI.PDClasses.Activities.PDActivitySend;
import VPI.PDClasses.Contacts.ContactDetail;
import VPI.PDClasses.Contacts.PDContactReceived;
import VPI.PDClasses.Contacts.PDContactSend;
import VPI.PDClasses.Contacts.PDFollower;
import VPI.PDClasses.Deals.PDDealItemsResponse;
import VPI.PDClasses.Deals.PDDealReceived;
import VPI.PDClasses.Deals.PDDealSend;
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
import VPI.VertecClasses.VertecTeam.TeamMember;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

public class Importer {

    //Services to access both APIs
    private PDService pipedrive;
    private VertecService vertec;

    public Map<Long, Long> organisationIdMap;
    public Map<Long, Long> contactIdMap;
    public Map<Long, Long> dealIdMap;
    public Map<String, Long> teamIdMap;

    private Map<String, String> activityTypeMap;

    public ZUKOrganisations vertecOrganisations;
    private ZUKProjects vertecProjects;
    private ZUKActivities vertecActivities;

    public Set<Long> missingOrganisationIds;
    public Set<Long> missingContactIds;
    private List<PDContactReceived> pipedriveContacts;
    private PDDealItemsResponse pipedriveDeals;

    public List<PDOrganisationSend> organisationPostList;

    public List<PDContactSend> contactPostList;
    public List<PDContactSend> contactPutList;

    public List<PDFollower> contactFollowerPostList;
    public List<PDFollower> dealFollowerPostList;

    public List<PDDealSend> dealPostList;
    public List<PDDealSend> dealPutList;

    public List<PDActivitySend> activityPostList;
    public Map<Long, List<Long>> projectPhasesMap;
    private String s;

    private final int EXPLORATORY = 1;
    private final int NEW_LEAD_EXTENTSION = 2;
    private final int QUALIFIED_LEAD = 3;
    private final int RFP_RECIEVED = 4;
    private final int OFFERED = 5;
    private final int UNDER_NEGOTIATION = 6;
    private final int VERBALLY_SOLD = 7;

    public final String MAP_PATH = "productionMaps/";

    public Importer(PDService pipedrive, VertecService vertec) {
        this.pipedrive = pipedrive;
        this.vertec    = vertec;

        this.organisationIdMap = new HashMap<>();
        this.contactIdMap      = new HashMap<>();
        this.dealIdMap         = new HashMap<>();

        constructTeamIdMap(getVertecUserEmails(),getPipedriveUsers());

        constructActivityTypeMap();

        //this.vertecOrganisations    = new ZUKOrganisations();
        //this.vertecProjects         = new ZUKProjects();
        this.projectPhasesMap       = new HashMap<>();
        //this.vertecActivities       = new ZUKActivities();

        this.missingOrganisationIds = new HashSet<>();
        this.missingContactIds      = new HashSet<>();

        this.organisationPostList = new ArrayList<>();

        this.contactPutList = new ArrayList<>();
        this.contactPostList = new ArrayList<>();

        this.contactFollowerPostList = new ArrayList<>();
        this.dealFollowerPostList = new ArrayList<>();

        this.dealPostList = new ArrayList<>();
        this.dealPutList = new ArrayList<>();

        this.activityPostList = new ArrayList<>();
    }

    /**
     * Call this function to import all ZUK data from Vertec to Pipedrive
     */
    public void importToPipedrive() {
        //import relevant information from vertec
        saveMap(MAP_PATH + "teamIdMap.txt", teamIdMap);
        System.out.println("Getting Contacts and Organisations from Vertec...");
        //1
        importOrganisationsAndContactsFromVertec();
        System.out.println("Got 'em");
        System.out.println("Getting projects and their phases from vertec...");
        //2
        importDealsFromVertec();
        System.out.println("Got 'em");
        System.out.println("Getting Activities from Vertec ...");
        //3
        importActivitiesFromVertec();
        System.out.println("Got 'em");
        //find missing organisations and contacts linked to deals and activities
        System.out.println("Getting missing organisations from Vertec...");

        //4
        importMissingOrganistationsFromVertec();
        saveSet(MAP_PATH + "missingOrganisations.txt",missingOrganisationIds);

        System.out.println("Getting missing Contacts from Vertec...");
        //5
        importMissingContactsFromVertec();
        saveSet(MAP_PATH + "missingContacts.txt", missingContactIds);

        //import contacts and deals from pipedrive to compare to vertec contacts and deals
        System.out.println("Getting  Contacts from Pipedrive...");
        //6
        importContactsFromPipedrive();
        System.out.println("Getting  Deals from Pipedrive...");
        //7
        importDealsFromPipedrive();
        //create and post organisatations from vertec to pipedrive, extract hierarchy and post
        //8
        populateOrganisationPostList();
        System.out.println("Posting Organisations to Pipedrive...");
        //9
        postOrganisationPostList();

        saveMap(MAP_PATH + "organisationIdMap.txt", organisationIdMap);
        System.out.println("Posting Organisation Hierarchies to Pipedrive...");
        //10
        postOrganisationHierarchies(
                builOrganisationHierarchies());
        //compare contacts by email and populate post and put lists, send to pipedrive and post followers
        //11
        populateContactPostAndPutLists();
        System.out.println("Posting Contacts to Pipedrive...");
        //12
        postAndPutContactPostAndPutLists();

        saveMap(MAP_PATH + "contactIdMap.txt", contactIdMap);
        //compare deals by phase number and project code and populate post and put lists, send to pipedrive
        //13
        populateDealPostAndPutList();
        System.out.println("Posting Deals to Pipedrive...");
        //14
        postAndPutDealPostAndPutList();

        saveMap(MAP_PATH + "dealIdMap.txt", dealIdMap);
        System.out.println("Posting Followers to Pipedrive...");
        //15
        populateFollowerPostList();
        //16
        postContactFollowers();
        //17
        postDealFollowers(); //TODO add tests fro this

        //craete and post activites once we have all vertec to pipedrive id maps populated from posting
        System.out.println("Posting Activities to Pipedrive...");
        //18
        populateActivityPostList();
        //19
        postActivityPostList();
        System.out.println("Import Successful!");

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
        this.vertecProjects = vertec.getZUKProjects().getBody();
        getVertecProjectList().stream()
                .map(project -> {
                    List<Long> phaseIds = project.getPhases().stream().map(JSONPhase::getV_id).collect(toList());
                    if (phaseIds.size() > 0) {
                        projectPhasesMap.put(project.getV_id(), phaseIds);
                    }
                    return project;
                })
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

                        org.setOwnedByTeam(false);
                        // if(org.getObjid() != 7700L) {//Filter out zuhlke engineering AG
                        dealWithContactsofMissingOrg(org);

                        vertecOrganisations.getOrganisationList().add(org);
                        organisationIdMap.put(org.getObjid(), -1L);
                        missingOrganisationIds.add(org.getObjid());
                        //checks for missing parent organisation and retrieves from vertec, then checck parent of newly imported org
                        Long parentID = org.getParentOrganisationId();
                        Boolean parentOrgNeeded = parentID != null && !organisationIdMap.containsKey(parentID);
                        while (parentOrgNeeded) {
                            JSONOrganisation orgParent = vertec.getOrganisation(parentID).getBody();
                            orgParent.setOwnedByTeam(false);
                            dealWithContactsofMissingOrg(orgParent);
                            vertecOrganisations.getOrganisationList().add(orgParent);
                            organisationIdMap.put(orgParent.getObjid(), -1L);
                            missingOrganisationIds.add(orgParent.getObjid());
                            parentID = orgParent.getParentOrganisationId();
                            parentOrgNeeded = parentID != null && !organisationIdMap.containsKey(parentID);
                        }

                    } catch (HttpClientErrorException e) { //TODO: work out how to test this properly
                        if (e.getStatusCode() != HttpStatus.NOT_FOUND) {
                            throw e;
                        }
                    }
                });
    }

    public void dealWithContactsofMissingOrg(JSONOrganisation org){
        org.getContacts().stream()
                .filter(JSONContact::getActive)
                .forEach(contact -> {
                    if(contactIdMap.containsKey(contact.getObjid())){ //contact is amongst dangling contacts
                        vertecOrganisations.setDanglingContacts(vertecOrganisations.getDanglingContacts().stream()
                        .filter(danglingContact -> danglingContact.getObjid() != contact.getObjid().longValue())
                        .collect(toList()));

                    } else { //contact is not amongst dangling contacts

                        contact.setOwnedByTeam(false);
                        contactIdMap.put(contact.getObjid(), -1L);
                        missingContactIds.add(contact.getObjid());
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
                        if (contact.getActive()) {
                            contact.setOwnedByTeam(false);
                            vertecOrganisations.getDanglingContacts().add(contact);
                            contactIdMap.put(contact.getObjid(), -1L);
                            missingContactIds.add(contact.getObjid());
                        }
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
            for(PDContactReceived pc : getPipedriveContactList()) {


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
                    this.contactPutList.add(createPDContactSend(jc, temp));
                    matchedMap.put(temp.getId(), true);
                }
            } else {
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

        setPhone(jc, pc, ps);

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
        ps.setOwner_id(teamIdMap.get(jc.getOwner()));

        //owned by
        if(jc.getOwnedByTeam()) ps.setOwnedBy("ZUK");
        else ps.setOwnedBy("Not ZUK");

        ps.setPosition(pc.getPosition() == null || pc.getPosition().isEmpty() ? jc.getPosition() : pc.getPosition());

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
        setPhone(jc, pc, ps);
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
        ps.setOwner_id(teamIdMap.get(jc.getOwner()));

        //owned by
        if(jc.getOwnedByTeam()) ps.setOwnedBy("ZUK");
        else ps.setOwnedBy("Not ZUK");

        ps.setPosition(jc.getPosition().isEmpty() ? (pc.getPosition() == null ? "" : pc.getPosition()) : jc.getPosition());

        return ps;
    }

    private void setPhone(JSONContact jc, PDContactReceived pc, PDContactSend ps) {

        ps.setPhone(pc.getPhone());
        if (jc.getPhone() != null) {
            if(ps.getPhone() == null){
                ps.getPhone().add(new ContactDetail(jc.getPhone(), true));
            }
            if(ps.getPhone() != null &&! ps.getPhone().stream()
                    .map(ContactDetail::getValue)
                    .collect(toSet())
                    .contains(jc.getPhone())) {
                ps.getPhone().add(new ContactDetail(jc.getPhone(),false));
            }
        }
        if(jc.getMobile() != null){
            if(ps.getPhone() == null) ps.getPhone().add(new ContactDetail(jc.getMobile(), true));
            if(ps.getPhone() != null &&! ps.getPhone().stream()
                    .map(ContactDetail::getValue)
                    .collect(toSet())
                    .contains(jc.getMobile())) {
                ps.getPhone().add(new ContactDetail(jc.getMobile(),false));
            }
        }
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
        contactFollowerPostList = getVertecContactList().stream()
                .map(contact -> {
                    List<PDFollower> followersForContact = new ArrayList<>();
                    if (contact.getFollowers() != null && !contact.getFollowers().isEmpty()){
                       followersForContact = contact.getFollowers().stream()
                                .map(follower -> new PDFollower(contactIdMap.get(contact.getObjid()), teamIdMap.get(follower)))
                                .collect(toList());
                    }
                    return followersForContact;
                })
                .flatMap(Collection::stream)
                .collect(toList());

        //dealFollowerPostlist
        getVertecProjectList().stream()
                .forEach(project
                        -> project.getPhases().stream()
                        .forEach(phase -> {

                            if (teamIdMap.get(project.getLeaderRef()) != null && dealIdMap.get(phase.getV_id()) != null) {
                                dealFollowerPostList.add(new PDFollower(dealIdMap.get(phase.getV_id()), teamIdMap.get(project.getLeaderRef())));
                            }
                            if( project.getAccountManager() != null && teamIdMap.get(project.getAccountManager()) != null
                                    && dealIdMap.get(phase.getV_id()) != null
                                    && ! project.getAccountManager().equals(project.getLeaderRef())){
                                //System.out.println("Project Manager " + project.getAccountManager() + "added as follower to " + phase.getCode());
                                dealFollowerPostList.add(new PDFollower(dealIdMap.get(phase.getV_id()), teamIdMap.get(project.getAccountManager())));
                            }
                        }));
    }

    public void postContactFollowers() {
        contactFollowerPostList.stream()
                .forEach(pipedrive::postFollowerToContact);
    }
    public void postDealFollowers() {
        dealFollowerPostList.stream()
                .forEach(pipedrive::postFollowerToDeal);
    }

    public void populateDealPostAndPutList() {
        for(JSONProject project : getVertecProjectList()){
            for(JSONPhase phase : project.getPhases()){
                Boolean match = false;
                PDDealReceived temp = null;
                for(PDDealReceived deal : getPipedriveDealList()){
                    if(project.getCode().equals(deal.getProject_number()) && phase.getCode().equals(deal.getPhase())) {
                        match = true;
                       temp = deal;
                    }
                }
                //if(project.getCode().charAt(0) != 'I'){ //Filtering out internal projects
                    if(match){
                        dealPutList.add(createDealObject(project,phase, temp));
                    } else {
                        dealPostList.add(createDealObject(project,phase));
                    }
               // }
            }
        }
    }

    //creates a Deal object from a Vertec project and phase, this is added to POST list
    private PDDealSend createDealObject(JSONProject project, JSONPhase phase) {
        //Try get owner of phase, if n/a then owner of project, if n/a then wolfgang will be assigned
        Long userId = teamIdMap.get(phase.getPersonResponsible());
        userId = userId == null ? teamIdMap.get(project.getLeaderRef()) : userId;
        userId = userId == null ? teamIdMap.get("wolfgang.emmerich@zuhlke.com") : userId;

        //Try to get personId from contactId map
        Long personId = contactIdMap.get(project.getClientRef());
        personId = personId == null ? contactIdMap.get(project.getCustomerRef()) : personId;

        //Try to get organisationId from organisationIdMap
        Long orgId = organisationIdMap.get(project.getClientRef());
        orgId = orgId == null ? organisationIdMap.get(project.getCustomerRef()) : orgId;

        //create our deal object
        PDDealSend deal = new PDDealSend(project, phase, userId, personId, orgId);

        setDealTitle(project,phase,deal);

        setStageId(deal, phase);

        return deal;
    }

    //create a Deal object from a Vertec project/phase and matching pipedrive entrym this is added to PUT list
    private PDDealSend createDealObject(JSONProject project, JSONPhase phase, PDDealReceived temp) {
        if(vertecDateMoreRecentThanPdDate(phase.getModifiedDate(), temp.getUpdate_time())){
            return getPdDealSendVertecMoreRecent(project, phase, temp);
        } else {
            return getPdDealSendPipedriveMoreRecent(project, phase, temp);
        }
    }

    private PDDealSend getPdDealSendPipedriveMoreRecent(JSONProject project, JSONPhase phase, PDDealReceived dr) {
        PDDealSend ds = new PDDealSend();
        //ID
        ds.setId(dr.getId());
        //Title

        setDealTitle(project, phase, ds); // if not

        //Value
        ds.setValue(dr.getValue());
        if(ds.getValue() == null) ds.setValue(phase.getExternalValue());
        //Currency
        ds.setCurrency(dr.getCurrency());
        if(ds.getCurrency() == null) ds.setCurrency(project.getCurrency());
        //user_id
        Long userId = teamIdMap.get(phase.getPersonResponsible());
        userId = userId == null ? teamIdMap.get(project.getLeaderRef()) : userId;
        userId = userId == null ? teamIdMap.get("wolfgang.emmerich@zuhlke.com") : userId;
        ds.setUser_id(userId);
        if(ds.getUser_id() == null){ //==> Use vertec as primary
            ds.setUser_id(dr.getUser_id().getId());
        }
        //personId
        ds.setPerson_id(dr.getPerson_id() == null ? null : dr.getPerson_id().getValue());
        if(ds.getPerson_id() == null){
            Long personId = contactIdMap.get(project.getClientRef());
            personId = personId == null ? contactIdMap.get(project.getCustomerRef()) : personId;
            ds.setPerson_id(personId);
        }
        //org_id
        ds.setOrg_id(dr.getOrg_id() == null ? null : dr.getOrg_id().getValue());
        if(ds.getOrg_id() == null){
            Long orgId = organisationIdMap.get(project.getClientRef());
            orgId = orgId == null ? organisationIdMap.get(project.getCustomerRef()) : orgId;
            ds.setOrg_id(orgId);
        }
        //add_time
        try {
            String[] dateTime = phase.getCreationDate().split("T");
            String date = dateTime[0];
            String time = dateTime[1];
            ds.setAdd_time(date + " " + time);
        } catch (Exception e) {
            ds.setAdd_time("2000-01-01 00:00:00");
        }
        ds.setVisible_to(3);

        //Visible_to
        //Phase
        ds.setPhase(dr.getPhase());
        //project number
        ds.setProject_number(dr.getProject_number());
        //vid
        ds.setV_id(phase.getV_id());
        //stageId, status, wonTime,LostTime,LostReason
        ds.setStage_id(dr.getStage_id());
        ds.setStatus(dr.getStatus());
        ds.setWon_time(dr.getWon_time());
        ds.setLost_time(dr.getLost_time());
        ds.setLost_reason(dr.getLost_reason());

        return ds;
    }



    private PDDealSend getPdDealSendVertecMoreRecent(JSONProject project, JSONPhase phase, PDDealReceived dr) {
        PDDealSend ds = new PDDealSend();

        ds.setId(dr.getId());

        //Title
       setDealTitle(project,phase,ds);

        //Value
        ds.setValue(phase.getExternalValue());
        if(ds.getValue() == null) ds.setValue(dr.getValue());
        //currency
        ds.setCurrency(project.getCurrency());
        if(ds.getCost_currency() == null) ds.setCurrency(dr.getCurrency());
        //user_id
        Long userId = teamIdMap.get(phase.getPersonResponsible());
        userId = userId == null ? teamIdMap.get(project.getLeaderRef()) : userId;
        userId = userId == null ? dr.getUser_id().getId() : userId;
        userId = userId == null ? teamIdMap.get("wolfgang.emmerich@zuhlke.com") : userId;
        ds.setUser_id(userId);

        //Try to get personId from contactId map
        Long personId = contactIdMap.get(project.getClientRef());
        personId = personId == null ? contactIdMap.get(project.getCustomerRef()) : personId;
        personId = personId == null ? (dr.getPerson_id() == null ? null : dr.getPerson_id().getValue()) : personId;
        ds.setPerson_id(personId);
        //Try to get organisationId from organisationIdMap
        Long orgId = organisationIdMap.get(project.getClientRef());
        orgId = orgId == null ? organisationIdMap.get(project.getCustomerRef()) : orgId;
        orgId = orgId == null ? (dr.getOrg_id() == null ? null : dr.getOrg_id().getValue()) : orgId;
        ds.setOrg_id(orgId);
        //add_time
        try {
            String[] dateTime = phase.getCreationDate().split("T");
            String date = dateTime[0];
            String time = dateTime[1];
            ds.setAdd_time(date + " " + time);
        } catch (Exception e) {
            ds.setAdd_time("2000-01-01 00:00:00");
        }
        //visible_to
        ds.setVisible_to(3);
        //phase
        ds.setPhase(phase.getCode());
        //project number
        ds.setProject_number(project.getCode());
        //v_id
        ds.setV_id(phase.getV_id());
        //stageId, status, wonTime, LostTime, LostReason
        setStageId(ds, phase);

        return ds;
    }

    private void setDealTitle(JSONProject project, JSONPhase phase, PDDealSend ds) {

        if (project.getTitle() != null && !project.getTitle().equals("")) {
            String title = project.getTitle() + ": " + ((phase.getDescription() == null || phase.getDescription().isEmpty()) ? phase.getCode() : phase.getDescription());
            ds.setTitle(title);
        } else {
            ds.setTitle(project.getCode() + ": " + ((phase.getDescription() == null || phase.getDescription().isEmpty()) ? phase.getCode() : phase.getDescription()));
        }

    }

    @SuppressWarnings("all")
    private void setStageId(PDDealSend deal, JSONPhase phase){

        //TODO: change to correct stage_ids once in production -- get rid of magic numbers ( load all stages from pd)
        String salesStatus = phase.getSalesStatus();
        String code = salesStatus.substring(0, Math.min(salesStatus.length(), 2));
        Integer num = Integer.parseInt(code);

        //for setting status
        String status = "open";

        switch (num) {
            //Exploratory = 1, NewLead/Extension = 2, QualifiedLead = 3
            case 5: deal.setStage_id(NEW_LEAD_EXTENTSION);
                break;
            //Rfp Recieved = 3
            case 10: deal.setStage_id(RFP_RECIEVED);
                break;
            //Offered = 6
            case 11: deal.setStage_id(OFFERED);
                break;
            //UnderNegotiation = 5
            case 12: deal.setStage_id(UNDER_NEGOTIATION);
                break;
            //VerballySold = 7
            case 20: deal.setStage_id(VERBALLY_SOLD);
                break;
            //SOLD = WON
            case 21: status = "won";
                deal.setStage_id(OFFERED);
                String wonTime = phase.getOfferedDate();
                wonTime = wonTime == null ? phase.getCompletion_date() : wonTime;
                deal.setWon_time(wonTime == null ? phase.getPDformatModifiedTime() : wonTime + " 00:00:00");
                deal.setExp_close_date(deal.getWon_time());
                break;
            //LOST = LOST
            case 30: status = "lost";
                deal.setStage_id(OFFERED);
                deal.setLost_reason(phase.getLostReason()); //TODO: add lost reason map/ get lost reason descriptions from vertec
                String lostTime = phase.getRejection_date();
                deal.setLost_time(lostTime == null ? phase.getPDformatModifiedTime() : lostTime + " 00:00:00");
                break;
            //FINISHED = WON OR LOST (Check value)
            case 40:
                deal.setStage_id(OFFERED);
                if (asFloat(deal.getValue()) > 0) {
                    status = "won";
                    String wonTime2 = phase.getOfferedDate();
                    wonTime2 = wonTime2 == null ? phase.getCompletion_date() : wonTime2;
                    deal.setWon_time(wonTime2 == null ? phase.getPDformatModifiedTime() : wonTime2 + " 00:00:00");
                    deal.setExp_close_date(deal.getWon_time());
                } else {
                    status = "lost";
                    deal.setLost_reason(phase.getLostReason()); //TODO: add lost reason map/ get lost reason descriptions from vertec
                    lostTime = phase.getRejection_date();
                    deal.setLost_time(lostTime == null ? phase.getPDformatModifiedTime() : lostTime + " 00:00:00");
                }
                break;
            default: System.out.println(num);
                break;
        }
        //status ('open' = Open, 'won' = Won, 'lost' = Lost, 'deleted' = Deleted)
        //deal.setStatus(status);
        deal.setStatus(status);
    }

    private float asFloat(String value) {
        String[] parts = value.split(",");
        String numberWithoutCommas = "";
        for(String s : parts) {
            numberWithoutCommas += s;
        }
        return Float.parseFloat(numberWithoutCommas);
    }

    public void postAndPutDealPostAndPutList() {
        dealIdMap.putAll(pipedrive.postDealList(dealPostList));
        dealIdMap.putAll(pipedrive.updateDealList(dealPutList));
    }

    public void populateActivityPostList() {
        getVertecActivityList().stream()
                .forEach(activity -> {
                    Long contact_id = contactIdMap.get(activity.getCustomer_link());
                    Long org_id = organisationIdMap.get(activity.getCustomer_link());
                    Long phase_id = dealIdMap.get(activity.getProject_phase_link());
                    if (activity.getProject_link() != null && projectPhasesMap.get(activity.getProject_link()) != null) {
                        List<Long> phases = projectPhasesMap.get(activity.getProject_link());
                        phase_id = phase_id == null ? dealIdMap.get(phases.get(phases.size() - 1)) : phase_id;
                    }
                    String type = activityTypeMap.get(activity.getType());
                    if(phase_id != null
                            || org_id != null
                            || contact_id != null) {
                        activityPostList.add(
                                new PDActivitySend(
                                        activity,
                                        teamIdMap.get(activity.getAssignee()),
                                        contact_id,
                                        org_id,
                                        phase_id,
                                        type));
                    } // else dont add to post list as this will be a floating activity not related to anything else in pipedrive
                });
    }

    public List<Long> postActivityPostList() {
       return pipedrive.postActivityList(activityPostList);
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
        try{
            return vertecProjects.getProjects().stream()
                    .filter(project -> project.getPhases().size() > 0)
                    .filter(project -> project.getCode().charAt(0) != 'I')
                    .collect(toList());
        } catch (Exception e) {
            System.out.println("Exception while trying to get vertec project list, it has probably not been initialised!");
        }
        return new ArrayList<>();
    }

    public List<JSONActivity> getVertecActivityList() {
        try{
            return vertecActivities.getActivityList();
        } catch (Exception e) {
            System.out.println("Exception while trying to get vertec Activities list, its probably not been initialised");
        }
        return new ArrayList<>();
    }

    public List<PDContactReceived>  getPipedriveContactList() {
        return pipedriveContacts;
    }

    public List<PDDealReceived> getPipedriveDealList() {
        return pipedriveDeals.getData();
    }
    public ZUKOrganisations getVertecOrganisations() {
        return vertecOrganisations;
    }

    public void setVertecOrganisations(ZUKOrganisations vertecOrganisations) {
        this.vertecOrganisations = vertecOrganisations;
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
        teamIdMap = new DefaultHashMap<>(1363410L);
        for (String v_email : v_emails) {
            Boolean mapped = false;
            for (PDUser pd_user : pd_users) {
                if (v_email.toLowerCase().equals(pd_user.getEmail().toLowerCase())) {
                    this.teamIdMap.put(v_email, pd_user.getId());
                    mapped = true;
                }
            }
            if (!mapped) {
                this.teamIdMap.put(v_email, 1424149L); //TODO: replace id with appropriate id, wolfgangs or admin?
            }

        }
        teamIdMap.put("sabine.streuss@zuhlke.com", this.teamIdMap.get("sabine.strauss@zuhlke.com"));
    }

    public Set<String> getVertecUserEmails() {
        return vertec.getTeamDetails()
                .getBody()
                .getMembers()
                .stream()
                .map(TeamMember::getEmail)
                .collect(toSet());
    }

    public List<PDUser> getPipedriveUsers() {
        return pipedrive.getAllUsers().getBody().getData();
    }

    private void constructActivityTypeMap() { //TODO: test construction of activity type map
        //if unable to find type link then set to misc type (custom type added to pipedrive dev instance)
        activityTypeMap = new DefaultHashMap<>("misc");

        String vTypes = "{362309 : Vertrag / Contract\n" + //Only header returned
                "{362308 : Organigramm / Organizational Chart\n" + //Filtered
                "{573113 : Auftragsbestätigung / Order Confirmation\n" + //Filtered
                "{362307 : Angebot / Offer\n" + //Filtered
        "{586078 : Kundenfeedback / Customer Feedback\n" +
                "{505823 : Eventteilnahme / Event Participation\n" +
                "{279647 : Dokument / Document\n" + //Filtered out
                "{270569 : Sales"; //TODO: ask if we should filter

        activityTypeMap.put("Meeting", "meeting");
        activityTypeMap.put("Aufgabe / Task", "task");
        activityTypeMap.put("EMail", "email");
        activityTypeMap.put("Kundenfeedback / Customer Feedback", "customer_feedback");
        activityTypeMap.put("Eventteilnahme / Event Participation", "event_participation");
        activityTypeMap.put("Vertrag / Contract", "contract");
        activityTypeMap.put("Sales", "sales");

    }

    public <T> void  saveMap(String filename, Map< T, Long> map)  {
        if(map.isEmpty()){
            System.out.println("Could not write map to file as map was empty");
            return;
        }
        Set<T> keys = map.keySet();
        try{

            PrintWriter writer = new PrintWriter(filename, "UTF-8");
            keys.stream()
                .forEach(key -> writer.println(key + "," + map.get(key)));

            writer.close();
        } catch (Exception e) {
            System.out.println("Could not write map to file: " + map);
            throw new RuntimeException(e);
        }
    }

    public <T> void saveSet(String filename, Set<T> set){
        if(set.isEmpty()){
            System.out.println("Could not save set to file as it was empty");
            return;
        }

        try{
            PrintWriter writer = new PrintWriter(filename, "UTF-8");
            set.stream()
                    .forEach(writer::println);
            writer.close();

        } catch (Exception e){
            System.out.println("Could not Save List: " + set);
            throw new RuntimeException(e);
        }
    }


}
