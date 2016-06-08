package VPI;

import VPI.PDClasses.Contacts.PDContactListReceived;
import VPI.PDClasses.Contacts.PDContactReceived;
import VPI.PDClasses.Deals.PDDealItemsResponse;
import VPI.PDClasses.Deals.PDDealReceived;
import VPI.PDClasses.PDService;
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

import java.util.*;

import static java.util.stream.Collectors.toSet;

/**
 * Created by gebo on 07/06/2016.
 */
public class Importer {

    //Services to access both APIs
    private PDService pipedrive;
    private VertecService vertec;

    private Map<String, Long> teamEmailToIdMap;
    public Map<Long, Long> organisationIdMap;
    public Map<Long, Long> contactIdMap;
    public Map<Long, Long> dealIdMap;

    private Map<String, String> activityTypeMap;

    private ZUKOrganisations vertecOrganisations;
    private ZUKProjects vertecProjects;
    private ZUKActivities vertecActivities;

    public Set<Long> missingOrganisationIds;
    public Set<Long> missingContactIds;
    private PDContactListReceived pipedriveContacts;
    private PDDealItemsResponse pipedriveDeals;

    public Importer(PDService pipedrive, VertecService vertec) {
        this.pipedrive = pipedrive;
        this.vertec    = vertec;

        this.teamEmailToIdMap  = new HashMap<>();
        this.organisationIdMap = new HashMap<>();
        this.contactIdMap      = new HashMap<>();
        this.dealIdMap         = new HashMap<>();

        this.activityTypeMap   = new HashMap<>();

        this.vertecOrganisations    = new ZUKOrganisations();
        this.vertecProjects         = new ZUKProjects();
        this.vertecActivities       = new ZUKActivities();

        this.missingOrganisationIds = new HashSet<>();
        this.missingContactIds      = new HashSet<>();
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
        buildOrganisationHierarchyMap();
        //compare contacts by email and populate post and put lists, send to pipedrive and post followers
        populateContactPostAndPutLists();
        postAndPutContactPostAndPutLists();
        postContactFollowers();
        //compare deals by phase number and project code and populate post and put lists, send to pipedrive
        populateDealPostAndPutList();
        postAndPutDealPostAndPutList();
        //craete and post activites once we have all vertec to pipedrive id maps populated from posting
        populateActivityPostList();
        postActivityPostList();
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
                        org.getContacts().stream()
                                .map(JSONContact::getObjid)
                                .forEach(contactID -> {
                                    contactIdMap.put(contactID, -1L);
                                    missingContactIds.add(contactID);
                                });
                    } catch (HttpClientErrorException e) {
                        System.out.println("Caught Exception getting organisation from Vertec"); //TODO: work out how to test this properly
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
        this.pipedriveContacts = pipedrive.getAllContacts().getBody();
    }

    public void importDealsFromPipedrive() {
        this.pipedriveDeals = pipedrive.getAllDeals().getBody();
    }

    public void populateOrganisationPostList() {
    }

    public void postOrganisationPostList() {
    }

    public void buildOrganisationHierarchyMap() {
    }

    public void populateContactPostAndPutLists() {
    }

    public void postAndPutContactPostAndPutLists() {
    }

    public void postContactFollowers() {
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
        return pipedriveContacts.getData();
    }

    public List<PDDealReceived> getPipedriveDealList() {
        return pipedriveDeals.getData();
    }
}
