package Importer;

import VPI.Importer;
import VPI.MyCredentials;
import VPI.PDClasses.Activities.PDActivitySend;
import VPI.Entities.util.ContactDetail;
import VPI.PDClasses.Contacts.PDContactListReceived;
import VPI.PDClasses.Contacts.PDContactReceived;
import VPI.PDClasses.Contacts.PDContactSend;
import VPI.PDClasses.Deals.PDDealItemsResponse;
import VPI.PDClasses.Deals.PDDealSend;
import VPI.PDClasses.HierarchyClasses.PDRelationshipSend;
import VPI.PDClasses.Organisations.PDOrganisationSend;
import VPI.PDClasses.PDService;
import VPI.PDClasses.Users.PDUser;
import VPI.PDClasses.Users.PDUserItemsResponse;
import VPI.VertecClasses.VertecActivities.JSONActivity;
import VPI.VertecClasses.VertecActivities.ZUKActivities;
import VPI.VertecClasses.VertecOrganisations.JSONContact;
import VPI.VertecClasses.VertecOrganisations.JSONOrganisation;
import VPI.VertecClasses.VertecOrganisations.ZUKOrganisations;
import VPI.VertecClasses.VertecProjects.JSONProject;
import VPI.VertecClasses.VertecProjects.ZUKProjects;
import VPI.VertecClasses.VertecService;
import VPI.VertecClasses.VertecTeam.ZUKTeam;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.stubbing.Answer;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class ImporterTest {

    private Importer importer;
    private PDService pipedrive;
    private VertecService vertec;

    private final int EXPLORATORY = 6;
    private final int NEW_LEAD_EXTENTSION = 1;
    private final int QUALIFIED_LEAD = 2;
    private final int RFP_RECIEVED = 3;
    private final int OFFERED = 4;
    private final int UNDER_NEGOTIATION = 5;
    private final int VERBALLY_SOLD = 7;

    @Before
    public void prepareDependancies() throws IOException {

        MockitoAnnotations.initMocks(this);
        pipedrive = mock(PDService.class);
        vertec = mock(VertecService.class);


        when(pipedrive.getAllUsers()).thenReturn(getDummyUsersResponse());
        when(vertec.getTeamDetails()).thenReturn(getDummyVertecTeamResponse());


        importer = new Importer(pipedrive, vertec);
    }

    @Test
    public void ImportToPipedriveCallsCorrectSubFunctions() throws IOException {
        when(vertec.getZUKOrganisations()).thenReturn(getDummyOrganisationsResponse());
        when(vertec.getZUKProjects()).thenReturn(getDummyProjectsResponse());
        when(vertec.getZUKActivities()).thenReturn(getDummyActivitiesResponse());
        when(vertec.getOrganisation(anyLong())).thenAnswer(getOrgResponseEntityAnswer());
        when(vertec.getContact(anyLong())).thenReturn(getDummyMissingContactResponse());
        when(pipedrive.postOrganisationList(anyList()))
                .thenAnswer(getDummyPipedriveOrganisationPostAnswer());
        when(pipedrive.getAllContacts()).thenReturn(getDummyPipedriveContactResponse());
        when(pipedrive.getAllDeals()).thenReturn(getDummyPipedriveDealResponse());

        Importer iSpy = spy(importer);
        iSpy.importToPipedrive();

        InOrder inOrder = inOrder(iSpy);

        inOrder.verify(iSpy).importOrganisationsAndContactsFromVertec();
        inOrder.verify(iSpy).importDealsFromVertec();
        inOrder.verify(iSpy).importActivitiesFromVertec();

        inOrder.verify(iSpy).importMissingOrganistationsFromVertec(); //track original owner of these, as to not overwrite in vertec
        inOrder.verify(iSpy).importMissingContactsFromVertec();

        inOrder.verify(iSpy).importContactsFromPipedrive();
        inOrder.verify(iSpy).importDealsFromPipedrive();

        inOrder.verify(iSpy).populateOrganisationPostList();
        inOrder.verify(iSpy).postOrganisationPostList();
        inOrder.verify(iSpy).builOrganisationHierarchies();
        inOrder.verify(iSpy).postOrganisationHierarchies(anyList());

        inOrder.verify(iSpy).populateContactPostAndPutLists();
        inOrder.verify(iSpy).postAndPutContactPostAndPutLists();

        inOrder.verify(iSpy).populateDealPostAndPutList();
        inOrder.verify(iSpy).postAndPutDealPostAndPutList();

        inOrder.verify(iSpy).populateFollowerPostList();
        inOrder.verify(iSpy).postContactFollowers();

        inOrder.verify(iSpy).populateActivityPostList();
        inOrder.verify(iSpy).postActivityPostList();
    }

    @Test
    public void ImportingOrganisationsAndContactsQueriesVertecService() throws IOException {

        when(vertec.getZUKOrganisations()).thenReturn(getDummyOrganisationsResponse());

        importer.importOrganisationsAndContactsFromVertec();

        verify(vertec).getZUKOrganisations();

        assertTrue("GetVertecOrganisations not returning correctly", ! importer.getVertecOrganisationList().isEmpty());
        assertTrue("Organisation Id Map (Vertec Side) not populated", ! importer.organisationIdMap.isEmpty());
        assertEquals("Organisation list and map size dont match",
                importer.organisationIdMap.size(), importer.getVertecOrganisationList().size());
    }

    @Test
    public void CanSeperateContactsFromOrganisationsFromZUKresponse() throws IOException {
        when(vertec.getZUKOrganisations()).thenReturn(getDummyOrganisationsResponse());

        importer.importOrganisationsAndContactsFromVertec();

        ZUKOrganisations res = getDummyOrganisationsResponse().getBody();
        List<JSONOrganisation> organisations = res.getOrganisationList();
        List<JSONContact> contacts =  importer.getVertecContactList();

        List<JSONContact> referenceContacts = new ArrayList<>();
        organisations.stream()
                .map(JSONOrganisation::getContacts)
                .forEach(referenceContacts::addAll);
        referenceContacts.addAll(res.getDanglingContacts());

        assertEquals("getVertecContacts returned wrongly sized list", referenceContacts.size(), contacts.size());
        assertTrue("Contact Id Map (Vertec Side) not populated", ! importer.contactIdMap.isEmpty());
    }

    private ResponseEntity<ZUKOrganisations> getDummyOrganisationsResponse() throws IOException {
        ObjectMapper m =  new ObjectMapper();
        ZUKOrganisations body = m.readValue(new File("src/test/resources/VRAPI Organisations.json"), ZUKOrganisations.class);
        return new ResponseEntity<>(body, HttpStatus.OK);
    }

    @Test
    public void ImportingDealsQueriesVertecService() throws IOException {
        when(vertec.getZUKProjects()).thenReturn(getDummyProjectsResponse());

        importer.importDealsFromVertec();

        verify(vertec).getZUKProjects();

        assertTrue("Importing Vertec Deals returned empty list", ! importer.getVertecProjectList().isEmpty());
        assertTrue("Deal Id Map (Vertec Side) not populated", ! importer.dealIdMap.isEmpty());
    }

    private ResponseEntity<ZUKProjects> getDummyProjectsResponse() throws IOException {
        ObjectMapper m =  new ObjectMapper();
        ZUKProjects body = m.readValue(new File("src/test/resources/VRAPI projects.json"), ZUKProjects.class);
        return new ResponseEntity<>(body, HttpStatus.OK);
    }

    @Test
    public void ImportingDealsBuildsVertecProjectToPhaseIdsMap() throws IOException {
        when(vertec.getZUKProjects()).thenReturn(getDummyProjectsResponse());

        importer.importDealsFromVertec();

        assertNotNull(importer.projectPhasesMap);
        assertEquals(importer.projectPhasesMap.size(), importer.getVertecProjectList().size());
        importer.getVertecProjectList().stream()
                .forEach(project ->
                        assertEquals(
                                project.getPhases().size(),
                                importer.projectPhasesMap.get(project.getV_id()).size()));

    }

    @Test
    public void ImportingActivitiesQueriesVertecService() throws IOException {
        when(vertec.getZUKActivities()).thenReturn(getDummyActivitiesResponse());

        importer.importActivitiesFromVertec();

        verify(vertec).getZUKActivities();

        assertTrue("Importing Vertec activities returned empty list", ! importer.getVertecActivityList().isEmpty());
    }

    private ResponseEntity<ZUKActivities> getDummyActivitiesResponse() throws IOException {
        ObjectMapper m =  new ObjectMapper();
        ZUKActivities body = m.readValue(new File("src/test/resources/VRAPI Activity Response.json"), ZUKActivities.class);
        return new ResponseEntity<>(body, HttpStatus.OK);
    }

    @Test
    public void givenImportedDataCAnExtractListOfMissingOrganisationIds() throws IOException {
        when(vertec.getZUKOrganisations()).thenReturn(getDummyOrganisationsResponse());
        when(vertec.getZUKProjects()).thenReturn(getDummyProjectsResponse());
        when(vertec.getZUKActivities()).thenReturn(getDummyActivitiesResponse());

        importer.importOrganisationsAndContactsFromVertec();
        importer.importDealsFromVertec();
        importer.importActivitiesFromVertec();

        Set<Long> idsMissing = importer.extractListOfMissingOrganisationIds();
        assertTrue( ! idsMissing.isEmpty());
        List<Long> idsWeHave = importer.getVertecOrganisationList().stream()
                .map(JSONOrganisation::getObjid)
                .collect(toList());

        idsMissing = idsMissing.stream()
                .filter(idsWeHave::contains)
                .collect(toSet());

        assertEquals("We have extracted Organisation ids that we already have imported", idsMissing.size(), 0);
    }

    @Test
    public void ImportingMissingOrganisationsQueriesVertec() throws IOException {
        when(vertec.getZUKOrganisations()).thenReturn(getDummyOrganisationsResponse());
        when(vertec.getZUKProjects()).thenReturn(getDummyProjectsResponse());
        when(vertec.getZUKActivities()).thenReturn(getDummyActivitiesResponse());
        when(vertec.getOrganisation(anyLong())).thenAnswer(getOrgResponseEntityAnswer());

        //Following when statements used to test the while(parentOrgNeeded) loop in importMissingOrganisationsFromVertec
        when(vertec.getOrganisation(7700L)).thenReturn(getOrgWithParent1());
        when(vertec.getOrganisation(7777L)).thenReturn(getOrgWithParent2());
        when(vertec.getOrganisation(9999L)).thenReturn(getOrgWithParent3());
        //Magic number 2  because there are 2 new fake parent organisationState being imported (7777, 9999)
        int numberOfFakeParents = 2;


        importer.importOrganisationsAndContactsFromVertec();
        importer.importDealsFromVertec();
        importer.importActivitiesFromVertec();

        int orgListSize = importer.getVertecOrganisationList().size();
        int orgMapSize  = importer.organisationIdMap.size();
        int contMapSize = importer.contactIdMap.size();

        assertEquals("Organisation List and Map Sizes do not match", orgListSize, orgMapSize);
        assertTrue(importer.missingOrganisationIds.isEmpty());
        assertTrue(importer.missingContactIds.isEmpty());

        int missingOrgSize = importer.extractListOfMissingOrganisationIds().size();

        importer.importMissingOrganistationsFromVertec();

        int orgListSizeAfter = importer.getVertecOrganisationList().size();
        int orgMapSizeAfter  = importer.organisationIdMap.size();
        int contMapSizeAfter = importer.contactIdMap.size();

        assertEquals(orgListSize + missingOrgSize + numberOfFakeParents, orgListSizeAfter);
        assertEquals(orgMapSize + missingOrgSize + numberOfFakeParents, orgMapSizeAfter);
        assertEquals(contMapSize + 1, contMapSizeAfter);

        assertEquals("Missing org id not added to missingorgids List",
                missingOrgSize + numberOfFakeParents, //value is 1 because stubbed call to getOrganisation always returns Organisation with id: 1L
                importer.missingOrganisationIds.size());
        assertEquals("Missing nested contact id not added to missingcontids List",
                1, //value is 1 because stubbed call to getOrganisation always returns Organisation with id: 1L
                importer.missingContactIds.size());

        assertTrue("The while(parentOrgNeeded) loop in importMissingOrganisationsFromVertec FAILED"
                ,(importer.organisationIdMap.containsKey(7777L) && importer.organisationIdMap.containsKey(9999L)));

        verify(vertec, times(missingOrgSize + numberOfFakeParents)).getOrganisation(anyLong());
    }

    @Test
    public void canExtractMissingContactsFromImportedData() throws IOException {
        when(vertec.getZUKOrganisations()).thenReturn(getDummyOrganisationsResponse());
        when(vertec.getZUKProjects()).thenReturn(getDummyProjectsResponse());
        when(vertec.getZUKActivities()).thenReturn(getDummyActivitiesResponse());

        importer.importOrganisationsAndContactsFromVertec();
        importer.importDealsFromVertec();
        importer.importActivitiesFromVertec();

        Set<Long> idsMissing = importer.extractListOfMissingContactIds();

        assertTrue( ! idsMissing.isEmpty());

        List<Long> idsWeHave = importer.getVertecContactList().stream()
                .map(JSONContact::getObjid)
                .collect(toList());

        idsMissing = idsMissing.stream()
                .filter(idsWeHave::contains)
                .collect(toSet());

        assertEquals("We have extracted Contact ids that we already have imported", idsMissing.size(), 0);
    }

    @Test
    public void importingMissingContactQueriesVertec() throws IOException {
        when(vertec.getZUKOrganisations()).thenReturn(getDummyOrganisationsResponse());
        when(vertec.getZUKProjects()).thenReturn(getDummyProjectsResponse());
        when(vertec.getZUKActivities()).thenReturn(getDummyActivitiesResponse());
        when(vertec.getContact(anyLong())).thenReturn(getDummyMissingContactResponse());

        importer.importOrganisationsAndContactsFromVertec();
        importer.importDealsFromVertec();
        importer.importActivitiesFromVertec();

        int contListSize = importer.getVertecContactList().size();
        int contMapSize = importer.contactIdMap.size();

        assertEquals("Contact List and Map size do nt match", contListSize, contMapSize);
        assertTrue(importer.missingContactIds.isEmpty());

        importer.importMissingContactsFromVertec();

        int contListSizeAfter = importer.getVertecContactList().size();
        int contMapSizeAfter = importer.contactIdMap.size();

        assertEquals("missing contact not added to list",
                contListSize + importer.extractListOfMissingContactIds().size(),
                contListSizeAfter);
        assertEquals("missing contact id not added to map", contMapSize + 1, contMapSizeAfter);
        assertEquals("missing contact id not added to missingContactIdsList", 1, importer.missingContactIds.size());

        verify(vertec, times(importer.extractListOfMissingContactIds().size())).getContact(anyLong());
    }

    private ResponseEntity<JSONContact> getDummyMissingContactResponse() {
        JSONContact contact = new JSONContact();
        contact.setObjid(666L);
        contact.setActive(true);
        return  new ResponseEntity<>(contact, HttpStatus.OK);
    }

    @Test
    public void importContactsFromPipedriveQueriesPipedrive() throws IOException {
        when(pipedrive.getAllContacts()).thenReturn(getDummyPipedriveContactResponse());

        importer.importContactsFromPipedrive();

        verify(pipedrive).getAllContacts();

        assertTrue("getting pipedrive contacts failing", ! importer.getPipedriveContactList().isEmpty());
    }

    private ResponseEntity<PDContactListReceived> getDummyPipedriveContactResponse() throws IOException {
        ObjectMapper m =  new ObjectMapper();
        PDContactListReceived body = m.readValue(new File("src/test/resources/PipedriveProduction contacts.json"), PDContactListReceived.class);
        return new ResponseEntity<>(body, HttpStatus.OK);
    }

    @Test
    public void importDealsFromPipedriveQueriesPipedrive() throws IOException {
        when(pipedrive.getAllDeals()).thenReturn(getDummyPipedriveDealResponse());

        importer.importDealsFromPipedrive();

        verify(pipedrive).getAllDeals();

        assertTrue("getting pipedrive deals failing", ! importer.getPipedriveDealList().isEmpty());
    }

    private ResponseEntity<PDDealItemsResponse> getDummyPipedriveDealResponse() throws IOException {
        ObjectMapper m =  new ObjectMapper();
        PDDealItemsResponse body = m.readValue(new File("src/test/resources/PipedriveProduction deals.json"), PDDealItemsResponse.class);
        return new ResponseEntity<>(body, HttpStatus.OK);
    }

    @Test
    public void populateOrganisationPostListFillsPostList() throws IOException {
        when(vertec.getZUKOrganisations()).thenReturn(getDummyOrganisationsResponse());
        
        Importer iSpy = spy(importer);

        assertTrue(iSpy.organisationPostList.isEmpty());

        iSpy.importOrganisationsAndContactsFromVertec();
        iSpy.populateOrganisationPostList();

        verify(iSpy, times(iSpy.getVertecOrganisationList().size())).convertToPDSend(anyObject());

        assertEquals("Did not create a PipedriveOrganisation for every Vertec Organisation",
                iSpy.getVertecOrganisationList().size(),
                iSpy.organisationPostList.size());

        assertEquals("Did not create a unique PipedriveOrganisation for every Vertec Organisation",
                iSpy.getVertecOrganisationList().stream()
                    .map(JSONOrganisation::getObjid)
                .collect(toSet()).size(),
                iSpy.organisationPostList.stream()
                    .map(PDOrganisationSend::getV_id)
                .collect(toSet()).size());


    }
    
    @Test
    public void postOrganisationPostListWillPostAllMembers() throws IOException {
        when(vertec.getZUKOrganisations()).thenReturn(getDummyOrganisationsResponse());
        when(vertec.getZUKProjects()).thenReturn(getDummyProjectsResponse());
        when(vertec.getZUKActivities()).thenReturn(getDummyActivitiesResponse());
        when(vertec.getOrganisation(anyLong())).thenAnswer(getOrgResponseEntityAnswer());

        importer.importOrganisationsAndContactsFromVertec();
        importer.importDealsFromVertec();
        importer.importActivitiesFromVertec();

        importer.importMissingOrganistationsFromVertec();

        assertTrue(! importer.teamIdMap.isEmpty());

        importer.populateOrganisationPostList();

        when(pipedrive.postOrganisationList(anyList()))
                .thenAnswer(getDummyPipedriveOrganisationPostAnswer());

        when(vertec.getOrganisation(anyLong())).thenAnswer(getOrgResponseEntityAnswer());
        importer.postOrganisationPostList();

        verify(pipedrive).postOrganisationList(anyList());

        importer.organisationPostList.stream()
                .map(org -> importer.organisationIdMap.get(org.getV_id()))
                .forEach(id -> assertTrue(id != -1L));

        importer.getVertecOrganisationList().stream()
                .map(org -> importer.organisationIdMap.get(org.getObjid()))
                .forEach(id -> {
                    assertTrue(id != -1L);
                });

        long numContsWithWebsites = importer.getVertecOrganisationList().stream()
                .filter(org -> !org.getWebsite().isEmpty())
                .collect(toList())
                .size();
        System.out.println(numContsWithWebsites);

        long numContsInPostListWithWebsite = importer.organisationPostList.stream()
               // .filter(org -> !org.getWebsite().isEmpty())
                .collect(toList())
                .size();

        System.out.println(numContsInPostListWithWebsite);

        assertEquals("not all orgs with websites have made it into postlist", numContsWithWebsites, numContsInPostListWithWebsite);

        assertTrue(! importer.organisationIdMap.containsValue(-1L));

        importer.organisationPostList.stream()
                .forEach(org -> {
                    assertNotNull(org.getName());
                    assertNotNull(org.getVisible_to());
                    assertNotNull(org.getV_id());
                    assertNotNull(org.getOwner_id());
                    assertNotNull(org.getOwnedBy());
                    assertNotNull(org.getCreationTime());
                });

        List<Integer> before = new ArrayList<>();
        List<Integer> after = new ArrayList<>();

        importer.organisationPostList.stream()
                .forEach(org -> {
                    if (isBefore2008(org.getCreationTime())) {
                        before.add(1);
                    } else {
                        after.add(1);
                    }
                });

        System.out.println("Before: " + before.size());
        System.out.println("After: " + after.size());
    }

    private boolean isBefore2008(String creationTime) {
        try {
            LocalDate date = LocalDate.parse(creationTime.substring(0, 10));
            LocalDate date08 = LocalDate.parse("2008-01-01");
            return date.isBefore(date08);
        } catch (Exception e) {
            System.out.println(creationTime);
        }
return false;
    }

    private Answer<ResponseEntity<JSONOrganisation>> getOrgResponseEntityAnswer() {
        return invocation -> {
            Object[] args = invocation.getArguments();

            JSONOrganisation org = new JSONOrganisation();
            org.setObjid((Long) args[0]);
            org.setName("blah");
            org.setOwner("wolfgang.emmerich@zuhlke.com");
            org.setCreationTime("2006-01-01T00:00:00");
            JSONContact contact = new JSONContact();
            contact.setActive(true);
            contact.setObjid(6996L);
            List<JSONContact> contacts = new ArrayList<>();
            contacts.add(contact);
            org.setContacts(contacts);
            return new ResponseEntity<>(org, HttpStatus.OK);
        };
    }

    private Answer<List<Long>> getDummyPipedriveOrganisationPostAnswer() {
        return invocation -> {
            Object[] args = invocation.getArguments();

            return ((List<PDOrganisationSend>) args[0]).stream()
                    .map(PDOrganisationSend::getV_id)
                    .map(id -> 5L).collect(toList());
        };
    }

    @Test
    public void canBuildOrganisationHierarchyMap() throws IOException {
        when(vertec.getZUKOrganisations()).thenReturn(getDummyOrganisationsResponse());
        when(vertec.getZUKProjects()).thenReturn(getDummyProjectsResponse());
        when(vertec.getZUKActivities()).thenReturn(getDummyActivitiesResponse());
        //This forces the id passed into getOrganisation to be returned in the dummy response entity
        when(vertec.getOrganisation(anyLong())).thenAnswer(getOrgResponseEntityAnswer());

        importer.importOrganisationsAndContactsFromVertec();
        importer.importDealsFromVertec();
        importer.importActivitiesFromVertec();

        importer.importMissingOrganistationsFromVertec();

        assertTrue(! importer.teamIdMap.isEmpty());

        importer.populateOrganisationPostList();

        when(pipedrive.postOrganisationList(anyList()))
                .thenAnswer(getDummyPipedriveOrganisationPostAnswer());

        importer.postOrganisationPostList();

        List<PDRelationshipSend> relationships = importer.builOrganisationHierarchies();

        int parentrelCount = importer.getVertecOrganisationList().stream()
                .filter(organisation -> organisation.getParentOrganisationId() != null)
                .collect(toList())
                .size();

        int numberOfCorretlyBuiltRelationships = relationships.stream()
                .filter(rel -> rel.getRel_owner_org_id() == 5L)
                .collect(toList()).size();

        importer.postOrganisationHierarchies(relationships);

        verify(pipedrive, times(parentrelCount)).postOrganisationRelationship(anyObject());
        assertEquals("Not all relationships were resolved correctly",parentrelCount, numberOfCorretlyBuiltRelationships);

    }

    private ResponseEntity<JSONOrganisation> getOrgWithParent1() {
        JSONOrganisation org = new JSONOrganisation();
        org.setObjid(7700L);
        JSONContact contact = new JSONContact();
        contact.setActive(true);
        contact.setObjid(6996L);
        List<JSONContact> contacts = new ArrayList<>();
        contacts.add(contact);
        org.setContacts(contacts);
        org.setParentOrganisationId(7777L);
        return new ResponseEntity<>(org, HttpStatus.OK);
    }

    private ResponseEntity<JSONOrganisation> getOrgWithParent2() {
        JSONOrganisation org = new JSONOrganisation();
        org.setObjid(7777L);
        JSONContact contact = new JSONContact();
        contact.setActive(true);
        contact.setObjid(6996L);
        org.setParentOrganisationId(9999L);
        return new ResponseEntity<>(org, HttpStatus.OK);
    }

    private ResponseEntity<JSONOrganisation> getOrgWithParent3() {
        JSONOrganisation org = new JSONOrganisation();
        org.setObjid(9999L);
        JSONContact contact = new JSONContact();
        contact.setActive(true);
        contact.setObjid(6996L);
        List<JSONContact> contacts = new ArrayList<>();
        contacts.add(contact);
        org.setContacts(contacts);
        return new ResponseEntity<>(org, HttpStatus.OK);
    }

    @Test
    public void canPopulateContactPostAndPutListsCorrectly() throws IOException {
        when(vertec.getZUKOrganisations()).thenReturn(getDummyOrganisationsResponse());
        when(pipedrive.getAllContacts()).thenReturn(getDummyPipedriveContactResponse());

        importer.importOrganisationsAndContactsFromVertec();
        importer.importContactsFromPipedrive();

        assertTrue(importer.contactPostList.isEmpty());
        assertTrue(importer.contactPutList.isEmpty());

        importer.populateContactPostAndPutLists();

        assertTrue(! importer.contactPostList.isEmpty());
        assertTrue(! importer.contactPutList.isEmpty());

        assertEquals("Post and Put list dont match correct size",
                importer.getVertecContactList().stream()
                .filter(JSONContact::getActive).collect(toList())
                        .size(),
                importer.contactPostList.size() + importer.contactPutList.size());

        assertTrue(importer.contactPutList.size() <= importer.getPipedriveContactList().size());

        List<String> vertecEmailList = importer.getVertecContactList().stream()
                .map(JSONContact::getEmail)
                .filter(email -> !email.equals(""))
                .collect(toList());

        List<String> pipedriveEmailList = importer.getPipedriveContactList().stream()
                .map(PDContactReceived::getEmail)
                .flatMap(Collection::stream)
                .map(ContactDetail::getValue)
                .filter(email -> !email.equals(""))
                .collect(toList());

        //Case where there is one pipedrive contact containing two emails which are split accross two vertec contact entries
        int NUMBER_OF_CONTACTS_THAT_FALL_INTO_ROWAN_CASE = 1;
        System.out.println("Names printed below fall into rowan case:");

        //checks that each contact
        Set<String> emailsFoundInBoth = importer.contactPutList.stream()
                .map(contact -> {
                    assertNotNull(contact.getId());
                    assertNotNull(contact.getName());
                    assertNotNull(contact.getVisible_to());
                    assertNotNull(contact.getActive_flag());
                    assertNotNull(contact.getV_id());
                    assertNotNull(contact.getCreationTime());
                    assertTrue(! contact.getEmail().isEmpty());
                    return contact;
                })
                .map(contact -> {
                   Set<String> emailsForContact = contact.getEmail().stream()
                           .map(ContactDetail::getValue)
                            .filter((email -> vertecEmailList.contains(email) && pipedriveEmailList.contains(email)))
                            .collect(toSet());
                    assertTrue( ! emailsForContact.isEmpty());
                    if (emailsForContact.size() > 1) {
                        System.out.println(contact.getName());
                    }
                    if (contact.getName().equals("Carl Powell")) {
                        System.out.println(contact.getId());
                    }if (contact.getName().equals("Mark Smith")) {
                        System.out.println(contact.getId());
                    }
                    return emailsForContact;
                }) //at this point we have a stream of lists of emails found in both (one per contact)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
        //checks that each member of put list corresponds to an email found in both
        assertEquals(emailsFoundInBoth.size(), importer.contactPutList.size() + NUMBER_OF_CONTACTS_THAT_FALL_INTO_ROWAN_CASE);
//
//        importer.contactPostList.stream()
//                .map(PDContactSend::getEmail)
//                .flatMap(Collection::stream)
//                .map(ContactDetail::getValue)
//                .forEach(email -> assertTrue( ! pipedriveEmailList.contains(email)));

        importer.contactPostList.stream()
                .forEach(contact -> {
                    assertNull(contact.getId());
                    assertNotNull(contact.getName());
                    assertNotNull(contact.getVisible_to());
                    assertNotNull(contact.getActive_flag());
                    assertNotNull(contact.getV_id());
                    assertNotNull(contact.getCreationTime());
                    assertTrue(! contact.getEmail().isEmpty());
                });

        List<Integer> before = new ArrayList<>();
        List<Integer> after = new ArrayList<>();

        importer.contactPostList.stream()
                .forEach(c -> {
                    if (isBefore2008(c.getCreationTime())) {
                        before.add(1);
                    } else {
                        after.add(1);
                    }
                });

        System.out.println("Before: " + before.size());
        System.out.println("After: " + after.size());

        int numContactsWithPosition = importer.getVertecContactList().stream()
                .filter(cont -> !cont.getPosition().isEmpty())
                .collect(toList())
                .size();

        System.out.println(numContactsWithPosition);

        int numContactsWithPositionInPostOrPut = importer.contactPostList.stream()
                .filter(cont -> !cont.getPosition().isEmpty())
                .collect(toList())
                .size();

        numContactsWithPositionInPostOrPut += importer.contactPutList.stream()
                .filter(cont -> !cont.getPosition().isEmpty())
                .collect(toList())
                .size();
        System.out.println(numContactsWithPositionInPostOrPut);

        //assertEquals("positions not correctly set", numContactsWithPosition, numContactsWithPositionInPostOrPut);
    }

    @Test
    public void CanCorrectlyCompareDate() {

        String pdTime = "2013-03-27 13:20:08";
        String vTime  = "2015-05-20T12:06:19";

        assertTrue(importer.vertecDateMoreRecentThanPdDate(vTime, pdTime));

    }

    @Test
    public void canPostAndPutContactPostAndPutlist() throws IOException {
        when(vertec.getZUKOrganisations()).thenReturn(getDummyOrganisationsResponse());
        when(vertec.getZUKProjects()).thenReturn(getDummyProjectsResponse());
        when(vertec.getZUKActivities()).thenReturn(getDummyActivitiesResponse());
        when(vertec.getOrganisation(anyLong())).thenAnswer(getOrgResponseEntityAnswer());
        when(vertec.getContact(anyLong())).thenAnswer(getContResponseEntityAnswer());

        when(pipedrive.getAllContacts()).thenReturn(getDummyPipedriveContactResponse());
        when(pipedrive.postOrganisationList(anyList()))
                .thenAnswer(getDummyPipedriveOrganisationPostAnswer());
        when(pipedrive.postContactList(anyList()))
                .thenAnswer(getDummyPipedriveContactPostResponse());
        when(pipedrive.putContactList(anyList()))
                .thenAnswer(getDummyPipedriveContactPutResponse());

        importer.importOrganisationsAndContactsFromVertec();
        importer.importDealsFromVertec();
        importer.importActivitiesFromVertec();

        importer.importMissingOrganistationsFromVertec();
        importer.importMissingContactsFromVertec();

        importer.importContactsFromPipedrive();

        assertTrue(! importer.teamIdMap.isEmpty());

        importer.populateOrganisationPostList();
        importer.postOrganisationPostList();

        importer.populateContactPostAndPutLists();

        importer.postAndPutContactPostAndPutLists();

        verify(pipedrive).postContactList(anyList());
        verify(pipedrive).putContactList(anyList());

        importer.contactPostList.stream()
                .map(contact -> importer.contactIdMap.get(contact.getV_id()))
                .forEach(id -> assertTrue(id != -1L));


        importer.contactPutList.stream()
                .map(contact -> importer.contactIdMap.get(contact.getV_id()))
                .forEach(id -> assertTrue(id != -1L));

        assertTrue(! importer.contactIdMap.containsValue(-1L));
        //Pipedrive id of contact added to putlist
        assertTrue(importer.contactIdMap.containsValue(720L)); //for production

    }

    private Answer<Map<Long, Long>> getDummyPipedriveContactPostResponse() {
        return invocation -> {
            Object[] args = invocation.getArguments();
            Map<Long,Long> map = new HashMap<>();

            ((List<PDContactSend>) args[0]).stream()
                    .forEach(cont -> map.put(cont.getV_id(), 5L));
            return map;
        };
    }

    private Answer<Map<Long, Long>> getDummyPipedriveContactPutResponse() {
        return invocation -> {
            Object[] args = invocation.getArguments();
            Map<Long,Long> map = new HashMap<>();

            ((List<PDContactSend>) args[0]).stream()
                    .forEach(cont -> map.put(cont.getV_id(), cont.getId()));
            return map;
        };
    }

    private Answer<ResponseEntity<JSONContact>> getContResponseEntityAnswer() {
        return invocation -> {
            Object[] args = invocation.getArguments();

            JSONContact cont = new JSONContact();

            cont.setActive(true);
            cont.setObjid((Long) args[0]);

            return new ResponseEntity<>(cont, HttpStatus.OK);
        };
    }

    @Test
    public void postsContactFollowers() throws IOException {
        when(vertec.getZUKOrganisations()).thenReturn(getDummyOrganisationsResponse());
        when(pipedrive.getAllContacts()).thenReturn(getDummyPipedriveContactResponse());

        importer.importOrganisationsAndContactsFromVertec();

        importer.importContactsFromPipedrive();


        assertTrue(!importer.teamIdMap.isEmpty());

        importer.populateContactPostAndPutLists();

        importer.postAndPutContactPostAndPutLists();

        importer.populateFollowerPostList();

        importer.postContactFollowers();

        int nrFollowers = importer.getVertecContactList().stream()
                .map(JSONContact::getFollowers)
                .flatMap(Collection::stream)
                .collect(toList())
                .size();

        assertEquals("Not all followers added to post list", nrFollowers, importer.contactFollowerPostList.size());


        importer.contactFollowerPostList.stream()
                .forEach(follower -> {
                    assertTrue("Contact " + follower.getObjectID() + " is not part of contactIdMap, but got added to followers"
                            , importer.contactIdMap.containsValue(follower.getObjectID()));

                    assertTrue("User " + follower.getUserID() + " is not part of team, but got added to foillowers"
                            , importer.teamIdMap.containsValue(follower.getUserID()));
                });

        verify(pipedrive, times(importer.contactFollowerPostList.size())).postFollowerToContact(anyObject());
    }


    @Test
    public void postsDealFollowers() throws IOException {

        when(vertec.getZUKOrganisations()).thenReturn(getDummyOrganisationsResponse());
        when(vertec.getZUKProjects()).thenReturn(getDummyProjectsResponse());
        when(vertec.getZUKActivities()).thenReturn(getDummyActivitiesResponse());
        when(vertec.getOrganisation(anyLong())).thenAnswer(getOrgResponseEntityAnswer());
        when(vertec.getContact(anyLong())).thenReturn(getDummyMissingContactResponse());
        when(pipedrive.postOrganisationList(anyList()))
                .thenAnswer(getDummyPipedriveOrganisationPostAnswer());
        when(pipedrive.getAllContacts()).thenReturn(getDummyPipedriveContactResponse());
        when(pipedrive.getAllDeals()).thenReturn(getDummyPipedriveDealResponse());

        when(pipedrive.postDealList(anyList())).thenAnswer(getDummyPipedriveDealPostOrPutResponse());
        when(pipedrive.updateDealList(anyList())).thenAnswer(getDummyPipedriveDealPostOrPutResponse());
        when(pipedrive.postActivityList(anyList())).thenAnswer(getDummyPipedriveActivityPOSTResponse());

        importer.importOrganisationsAndContactsFromVertec();
        importer.importDealsFromVertec();
        importer.importActivitiesFromVertec();
        importer.importMissingOrganistationsFromVertec();
        importer.importMissingContactsFromVertec();

        importer.importContactsFromPipedrive();
        importer.importDealsFromPipedrive();

        importer.populateOrganisationPostList();
        importer.postOrganisationPostList();

        importer.populateContactPostAndPutLists();
        importer.postAndPutContactPostAndPutLists();

        importer.populateDealPostAndPutList();
        importer.postAndPutDealPostAndPutList();

        assertTrue(! importer.teamIdMap.isEmpty());

        importer.populateContactPostAndPutLists();

        importer.postAndPutContactPostAndPutLists();

        importer.populateDealPostAndPutList();
        importer.postAndPutDealPostAndPutList();

        importer.populateFollowerPostList();

        importer.postDealFollowers();



        int nrPhases = importer.getVertecProjectList().stream()
                .map(JSONProject::getPhases)
                .flatMap(Collection::stream)
                .collect(toList())
                .size();

        int nrAccountManagers = importer.getVertecProjectList().stream()
                .filter(proj -> proj.getAccountManager() != null)
                .map(JSONProject::getPhases)
                .flatMap(Collection::stream)
                .collect(toList())
                .size();

        int nrOfBothAccountManagerAndProjectLeader = importer.getVertecProjectList().stream()
                .filter(proj -> proj.getAccountManager() != null)
                .filter(proj -> proj.getLeaderRef() != null)
                .filter((proj -> proj.getLeaderRef().equals(proj.getAccountManager())))
                .map(JSONProject::getPhases)
                .flatMap(Collection::stream)
                .collect(toList())
                .size();

        //nrPhases+ nrAccountmanagers is how many dealfollowers we post as for each deal the owner gets posted as a follower and the account manager as well, where its specified
        assertEquals("Not all followers added to post list", nrPhases + nrAccountManagers - nrOfBothAccountManagerAndProjectLeader,importer.dealFollowerPostList.size());


        importer.dealFollowerPostList.stream()
                .forEach(follower -> {
                    assertTrue("Deal " + follower.getObjectID() + " is not part of DealIdMap, but got added to followers"
                            , importer.dealIdMap.containsValue(follower.getObjectID()));

                    assertTrue("User " + follower.getUserID() + " is not part of team, but got added to foillowers"
                            ,importer.teamIdMap.containsValue(follower.getUserID()));
                });

        verify(pipedrive, times(importer.dealFollowerPostList.size())).postFollowerToDeal(anyObject());
    }

    @Test
    public void canPopulateDealPutAndPostLists() throws IOException {
        when(vertec.getZUKOrganisations()).thenReturn(getDummyOrganisationsResponse());
        when(vertec.getZUKProjects()).thenReturn(getDummyProjectsResponse());
        when(vertec.getZUKActivities()).thenReturn(getDummyActivitiesResponse());

        when(vertec.getOrganisation(anyLong())).thenAnswer(getOrgResponseEntityAnswer());
        when(vertec.getContact(anyLong())).thenReturn(getDummyMissingContactResponse());

        when(pipedrive.postOrganisationList(anyList()))
                .thenAnswer(getDummyPipedriveOrganisationPostAnswer());

        when(pipedrive.getAllContacts()).thenReturn(getDummyPipedriveContactResponse());
        when(pipedrive.getAllDeals()).thenReturn(getDummyPipedriveDealResponse());

        importer.importOrganisationsAndContactsFromVertec();
        importer.importDealsFromVertec();
        importer.importActivitiesFromVertec();

        importer.importMissingOrganistationsFromVertec();
        importer.importMissingContactsFromVertec();

        importer.importContactsFromPipedrive();
        importer.importDealsFromPipedrive();

        importer.populateOrganisationPostList();
        importer.postOrganisationPostList();
        importer.builOrganisationHierarchies();
        importer.postOrganisationHierarchies(anyList());

        importer.populateContactPostAndPutLists();
        importer.postAndPutContactPostAndPutLists();

        assertTrue(importer.dealPostList.isEmpty());
        assertTrue(importer.dealPutList.isEmpty());

        importer.populateDealPostAndPutList();

        assertTrue( ! importer.dealPostList.isEmpty());
        assertTrue( ! importer.dealPutList.isEmpty());

        int numOfPhases = importer.getVertecProjectList().stream()
                .filter(proj -> proj.getCode().charAt(0) != 'I')
                .map(JSONProject::getPhases)
                .flatMap(Collection::stream)
                .collect(toList()).size();

        System.out.println("dealputlist size : " + importer.dealPutList.size());
        System.out.println("dealpostlist size : " + importer.dealPostList.size());

        assertEquals("Not every deal received has been sent to pipedrive",
                numOfPhases,
                importer.dealPostList.size() + importer.dealPutList.size());

        List<String> vertecCodePhasePairs = importer.getVertecProjectList().stream()
                .map(project -> project.getPhases().stream()
                        .map(phase -> project.getCode() + phase.getCode()).collect(toList()))
                .flatMap(Collection::stream)
                .collect(toList());

        List<String> pipedriveCodePhasePairs = importer.getPipedriveDealList().stream()
                .map(project -> project.getProject_number() + project.getPhase())
                .collect(toList());

        List<String> foundInBoth = importer.dealPutList.stream()
                .map(deal -> {
                    assertNotNull(deal.getId());
                    assertNotNull(deal.getVisible_to());
                    assertNotNull(deal.getStatus());
                    assertNotNull(deal.getAdd_time());
                    assertNotNull(deal.getPhase());
                    assertNotNull(deal.getProject_number());
                    assertNotNull(deal.getTitle());
                    assertNotNull(deal.getStage_id());
                    assertNotNull(deal.getValue());
                    assertNotNull(deal.getAdd_time());
                    assertNotNull(deal.getV_id());
                    return deal;
                })
                .map(deal -> {
                    String codePhase = deal.getProject_number()+deal.getPhase();
                    assertTrue(pipedriveCodePhasePairs.contains(codePhase));
                    assertTrue(vertecCodePhasePairs.contains(codePhase));
                    return codePhase;
                }).collect(Collectors.toList());

        assertEquals(foundInBoth.size(), importer.dealPutList.size());

        importer.dealPostList.stream()
                .forEach(deal -> {
                    assertNull(deal.getId());
                    assertNotNull(deal.getVisible_to());
                    assertNotNull(deal.getStatus());
                    if (deal.getStatus().equals("won")) {
                        assertNotNull(deal.getWon_time());
                        assertNotNull(deal.getExp_close_date());
                        assertTrue(deal.getStage_id() == OFFERED);
                    }
                    if (deal.getStatus().equals("lost")) {
                        assertNotNull(deal.getLost_time());
                        assertNotNull(deal.getLost_reason());
                        assertTrue(deal.getStage_id() == OFFERED);
                    }
                    assertNotNull(deal.getAdd_time());
                    assertNotNull(deal.getPhase());
                    assertNotNull(deal.getProject_number());
                    assertNotNull(deal.getTitle());
                    assertNotEquals(deal.getTitle(), "");
                    String[] bothNameParts = deal.getTitle().split(": ");
                    assertTrue(bothNameParts.length >= 2);
                    assertTrue(!bothNameParts[0].isEmpty());
                    assertTrue(!bothNameParts[1].isEmpty());
                    assertNotNull(deal.getStage_id());
                    assertNotNull(deal.getValue());
                    assertNotNull(deal.getAdd_time());
                    assertNotNull(deal.getV_id());
                });

        List<Integer> before = new ArrayList<>();
        List<Integer> after = new ArrayList<>();

        importer.dealPostList.stream()
                .forEach(org -> {
                    if (isBefore2008(org.getAdd_time())) {
                        before.add(1);
                    } else {
                        after.add(1);
                    }
                });

        System.out.println("Before: " + before.size());
        System.out.println("After: " + after.size());
    }

    @Test
    public void canPostAndPutDealPostAndPutLists() throws IOException {
        when(vertec.getZUKOrganisations()).thenReturn(getDummyOrganisationsResponse());
        when(vertec.getZUKProjects()).thenReturn(getDummyProjectsResponse());
        when(vertec.getZUKActivities()).thenReturn(getDummyActivitiesResponse());

        when(vertec.getOrganisation(anyLong())).thenAnswer(getOrgResponseEntityAnswer());
        when(vertec.getContact(anyLong())).thenReturn(getDummyMissingContactResponse());

        when(pipedrive.postOrganisationList(anyList()))
                .thenAnswer(getDummyPipedriveOrganisationPostAnswer());

        when(pipedrive.getAllContacts()).thenReturn(getDummyPipedriveContactResponse());
        when(pipedrive.getAllDeals()).thenReturn(getDummyPipedriveDealResponse());

        when(pipedrive.postDealList(anyList())).thenAnswer(getDummyPipedriveDealPostOrPutResponse());
        when(pipedrive.updateDealList(anyList())).thenAnswer(getDummyPipedriveDealPostOrPutResponse());

        importer.importOrganisationsAndContactsFromVertec();
        importer.importDealsFromVertec();
        importer.importActivitiesFromVertec();

        importer.importMissingOrganistationsFromVertec();
        importer.importMissingContactsFromVertec();

        importer.importContactsFromPipedrive();
        importer.importDealsFromPipedrive();

        importer.populateOrganisationPostList();
        importer.postOrganisationPostList();
        importer.builOrganisationHierarchies();
        importer.postOrganisationHierarchies(anyList());

        importer.populateContactPostAndPutLists();
        importer.postAndPutContactPostAndPutLists();

        importer.populateDealPostAndPutList();
        importer.postAndPutDealPostAndPutList();

        verify(pipedrive).postDealList(anyList());
        verify(pipedrive).updateDealList(anyList());

        importer.dealPostList.stream()
                .forEach(deal -> assertTrue(importer.dealIdMap.get(deal.getV_id()) != -1L));

        importer.dealPutList.stream()
                .forEach(deal -> assertTrue(importer.dealIdMap.get(deal.getV_id()) != -1L));

    }

    private Answer<Map<Long, Long>> getDummyPipedriveDealPostOrPutResponse() {
        return invocation -> {
            Object[] args = invocation.getArguments();
            Map<Long,Long> map = new HashMap<>();

            ((List<PDDealSend>) args[0]).stream()
                    .forEach(deal -> map.put(deal.getV_id(), 5L));
            return map;
        };
    }

    @Test
    public void canPopulateActivityPostList() throws IOException {
        when(vertec.getZUKOrganisations()).thenReturn(getDummyOrganisationsResponse());
        when(vertec.getZUKProjects()).thenReturn(getDummyProjectsResponse());
        when(vertec.getZUKActivities()).thenReturn(getDummyActivitiesResponse());
        when(vertec.getOrganisation(anyLong())).thenAnswer(getOrgResponseEntityAnswer());
        when(vertec.getContact(anyLong())).thenReturn(getDummyMissingContactResponse());
        when(pipedrive.postOrganisationList(anyList()))
                .thenAnswer(getDummyPipedriveOrganisationPostAnswer());
        when(pipedrive.getAllContacts()).thenReturn(getDummyPipedriveContactResponse());
        when(pipedrive.getAllDeals()).thenReturn(getDummyPipedriveDealResponse());

        when(pipedrive.postDealList(anyList())).thenAnswer(getDummyPipedriveDealPostOrPutResponse());
        when(pipedrive.updateDealList(anyList())).thenAnswer(getDummyPipedriveDealPostOrPutResponse());

        importer.importOrganisationsAndContactsFromVertec();
        importer.importDealsFromVertec();
        importer.importActivitiesFromVertec();
        importer.importMissingOrganistationsFromVertec();
        importer.importMissingContactsFromVertec();

        importer.importContactsFromPipedrive();
        importer.importDealsFromPipedrive();

        importer.populateOrganisationPostList();
        importer.postOrganisationPostList();

        importer.populateContactPostAndPutLists();
        importer.postAndPutContactPostAndPutLists();

        importer.populateDealPostAndPutList();
        importer.postAndPutDealPostAndPutList();

        assertTrue("Activity postlist not empty to start with", importer.activityPostList.isEmpty());

        importer.populateActivityPostList();
//
//        assertEquals("ActivitypostList does not contain all activities",
//                importer.getVertecActivityList(),
//                importer.activityPostList.size());

        importer.activityPostList.stream()
                .forEach(activity -> {
                    assertNull(activity.getId());
                    assertNotNull(activity.getType());
                    assertNotNull(activity.getSubject());
                    assertNotNull(activity.getDue_date());
                    assertNotNull(activity.getDone());
                    if(isInThePast(activity.getAdd_time()) || isInThePast(activity.getDue_date()) || isInThePast(activity.getDone_date())) {
                        assertTrue(activity.getDone());
                    }
                    if (activity.getDone()) {
                        assertNotNull(activity.getDone_date());
                    }
                    assertNotNull(activity.getNote());
                    assertNotNull(activity.getAdd_time());
                    assertTrue(activity.getDeal_id() != null
                            || activity.getOrg_id() != null
                            || activity.getPerson_id() != null);
                    assertNotNull(activity.getSubject());
                });

        List<Integer> before = new ArrayList<>();
        List<Integer> after = new ArrayList<>();

        importer.activityPostList.stream()
                .forEach(org -> {
                    if (isBefore2008(org.getAdd_time())) {
                        before.add(1);
                    } else {
                        after.add(1);
                    }
                });

        System.out.println("Before: " + before.size());
        System.out.println("After: " + after.size());

    }

    @Test
    public void isInThePastWorks() {
        String d1 = "2016-07-29";
        String d2 = "2016-07-29 09:00:00";
        String d3 = "2016-04-20";
        String d4 = "2016-04-20 09:00:00";
        String empty = "";

        assertFalse(isInThePast(d1));
        assertFalse(isInThePast(d2));
        assertTrue(isInThePast(d3));
        assertTrue(isInThePast(d4));

        assertFalse(isInThePast(empty));

    }

    @SuppressWarnings("all")
    private boolean isInThePast(String dateTime) {
        if (dateTime != null &&  dateTime.length() >= 10) {
            String date = dateTime.substring(0, 10);

            LocalDate d = LocalDate.parse(date);
            LocalDate now = LocalDate.now();
            return d.isBefore(now);
        } else {
            return false;
        }

    }

    @Test
    public void postActivityListWillPostActivityList() throws IOException {

        when(vertec.getZUKOrganisations()).thenReturn(getDummyOrganisationsResponse());
        when(vertec.getZUKProjects()).thenReturn(getDummyProjectsResponse());
        when(vertec.getZUKActivities()).thenReturn(getDummyActivitiesResponse());
        when(vertec.getOrganisation(anyLong())).thenAnswer(getOrgResponseEntityAnswer());
        when(vertec.getContact(anyLong())).thenReturn(getDummyMissingContactResponse());
        when(pipedrive.postOrganisationList(anyList()))
                .thenAnswer(getDummyPipedriveOrganisationPostAnswer());
        when(pipedrive.getAllContacts()).thenReturn(getDummyPipedriveContactResponse());
        when(pipedrive.getAllDeals()).thenReturn(getDummyPipedriveDealResponse());

        when(pipedrive.postDealList(anyList())).thenAnswer(getDummyPipedriveDealPostOrPutResponse());
        when(pipedrive.updateDealList(anyList())).thenAnswer(getDummyPipedriveDealPostOrPutResponse());
        when(pipedrive.postActivityList(anyList())).thenAnswer(getDummyPipedriveActivityPOSTResponse());

        importer.importOrganisationsAndContactsFromVertec();
        importer.importDealsFromVertec();
        importer.importActivitiesFromVertec();
        importer.importMissingOrganistationsFromVertec();
        importer.importMissingContactsFromVertec();

        importer.importContactsFromPipedrive();
        importer.importDealsFromPipedrive();

        importer.populateOrganisationPostList();
        importer.postOrganisationPostList();

        importer.populateContactPostAndPutLists();
        importer.postAndPutContactPostAndPutLists();

        importer.populateDealPostAndPutList();
        importer.postAndPutDealPostAndPutList();

        importer.populateActivityPostList();

        importer.postActivityPostList();

        verify(pipedrive).postActivityList(anyList());

    }

    private Answer<List<Long>> getDummyPipedriveActivityPOSTResponse() {
        return invocation -> {
            Object[] args = invocation.getArguments();
           List<Long> list  = new ArrayList<>();

            ((List<PDActivitySend>) args[0]).stream()
                    .forEach(activity -> list.add( 5L));
            return list;
        };
    }

    @Test
    public void canSaveStringLongMap() throws IOException {
        Map<String, Long> map = new HashMap<>();
        map.put("habba@babba.ed", 990990L);
        map.put("hubba@bubba.hu", 990998L);

        importer.saveMap(importer.MAP_PATH + "testmap.txt", map);


            File file = new File(importer.MAP_PATH + "testmap.txt");
            String line;


            FileReader reader = new FileReader(file.getAbsolutePath());
            BufferedReader breader = new BufferedReader(reader);
            while ((line = breader.readLine()) != null) {
                String[] dateFormatter = line.split(",");
                String email= dateFormatter[0];
                Long id = Long.parseLong(dateFormatter[1]);
                assertEquals("Entry either does not exist in map or has got wrong key", map.get(email), id);
            }
    }

    @Test
    public void canSaveLongLongMap() throws IOException {
        Map<Long, Long> map = new HashMap<>();
        map.put(999L,888L);
        map.put(234L,2344L);

        importer.saveMap(importer.MAP_PATH + "testmap.txt", map);


        File file = new File(importer.MAP_PATH + "testmap.txt");
        String line;


        FileReader reader = new FileReader(file.getAbsolutePath());
        BufferedReader breader = new BufferedReader(reader);
        while ((line = breader.readLine()) != null) {
            String[] dateFormatter = line.split(",");
            Long id1 = Long.parseLong(dateFormatter[0]);
            Long id = Long.parseLong(dateFormatter[1]);
            assertEquals("Entry either does not exist in map or has got wrong key",map.get(id1), id);
        }

    }

    @Test
    public void canSaveSet() throws IOException {
        Set<Long> idsToSave = new HashSet<>();
        idsToSave.add(5L);
        idsToSave.add(6L);

        importer.saveSet("testList.txt", idsToSave);


        File file = new File("testList.txt");
        Long id;
        String line;

        FileReader reader = new FileReader(file.getAbsolutePath());
        BufferedReader bufferedReader = new BufferedReader(reader);

        while((line = bufferedReader.readLine()) != null){
            id = Long.parseLong(line);
            assertTrue("Id not saved or saved in wrong order", idsToSave.contains(id));
        }
    }

    @Test
    public void canreformatEmail() throws IOException {
        ResponseEntity<ZUKActivities> activitiesres = getDummyActivitiesResponse();
        String email = null;

        List<JSONActivity> activities = activitiesres.getBody().getActivityList();

        for(JSONActivity a : activities) {
            if (a.getId() == 23768770L) {
                PDActivitySend as = new PDActivitySend(a,1403429L ,null,null,null,"email");
                email = as.getNote();
            }
        }

        System.out.println(email);

        email = PDActivitySend.reformat(email);

        int newlineSplitSize = email.split("\n").length;

        assertEquals("Some newlines left", 1, newlineSplitSize);

        int tabSplitSize = email.split("\t").length;

        assertEquals("Some tabs left", 1, tabSplitSize);

 }
    @Test
    public void canRecogniseTab(){
        String s = "haba\tbaba";

        System.out.println(s);
        System.out.println("");

        String rs = PDActivitySend.reformat(s);

        System.out.println(rs);
        assertTrue( ! rs.contains("\t"));

    }

    @Test
    public void canDealWithContactsofMissingOrg() throws IOException {

        Long danglingContact = 762805L;
        when(vertec.getZUKOrganisations()).thenReturn(getDummyOrganisationsResponse());
        when(vertec.getZUKProjects()).thenReturn(getDummyProjectsResponse());
        when(vertec.getZUKActivities()).thenReturn(getDummyActivitiesResponse());

        when(vertec.getOrganisation(anyLong())).thenAnswer(getOrgResponseEntityAnswer());
        when(vertec.getOrganisation(1317625L)).thenAnswer(getSpecificMissingOrg());

        when(vertec.getContact(anyLong())).thenReturn(getDummyMissingContactResponse());


        importer.importOrganisationsAndContactsFromVertec();

        importer.importMissingOrganistationsFromVertec();

        importer.getVertecOrganisations().getDanglingContacts().stream()
                .forEach(c -> assertNotEquals("Contact didnt get deleted from dangling contacts",
                        danglingContact, c.getObjid()));

        importer.getVertecOrganisations().getOrganisationList().stream()
                .filter(facility -> facility.getObjid() == 1317625L)
                .map(JSONOrganisation::getContacts)
                .flatMap(Collection::stream)
                .filter(cont -> cont.getObjid() == 1L)
                .forEach(cont -> {
                    assertFalse("Guy/Gal should not be owned by team", cont.getOwnedByTeam());
                });

        List<JSONContact> nonDanglingContIdList = importer.getVertecOrganisations().getOrganisationList().stream()
                .map(JSONOrganisation::getContacts)
                .flatMap(Collection::stream)
                .filter(cont -> cont.getObjid().longValue() == danglingContact)
                    .map(cont -> {
                    assertTrue("Guy/Gal should be owned by team", cont.getOwnedByTeam());
                    return cont;
                })
                .collect(toList());


        assertEquals("Failed to put contact amongst non-dongling contacts", 1, nonDanglingContIdList.size());
    }

    private Answer<ResponseEntity<JSONOrganisation>> getSpecificMissingOrg() {
        return invocation -> {

            JSONOrganisation org = new JSONOrganisation();
            org.setOwnedByTeam(false);
            org.setObjid(1317625L);
            org.setParentOrganisationId(null);

            JSONContact contact = new JSONContact();
            contact.setFirstName("Contact in id map");
            contact.setObjid(762805L);
            contact.setActive(true);
            contact.setEmail("adamcarney@lookers.co.uk");

            org.getContacts().add(contact);

            contact = new JSONContact();

            contact.setActive(true);
            contact.setFirstName("Contact not in id map");
            contact.setObjid(1L);
            contact.setEmail("hahbv@ha.bla");

            org.getContacts().add(contact);

            return new ResponseEntity<>(org, HttpStatus.OK);
        };
    }
    @Test @Ignore("takes too long")
    public void doesNotImportInactiveMissingContacts() throws IOException {


        MyCredentials creds = new MyCredentials();
        VertecService VS = new VertecService("localhost:9999");
        VertecService kgb = spy(VS);

        PDService PD = new PDService("https://api.pipedrive.com/v1/", creds.getApiKey());
        PDService cia = spy(PD);

        Importer newImporter = new Importer(cia, kgb);

        Mockito.doReturn(getDummyOrganisationsResponse()).when(kgb).getZUKOrganisations();
        Mockito.doReturn(getDummyProjectsResponse()).when(kgb).getZUKProjects();
        Mockito.doReturn(getDummyActivitiesResponse()).when(kgb).getZUKActivities();

        Mockito.doAnswer(getDummyPipedriveOrganisationPostAnswer()).when(cia).postOrganisationList(anyList());
        Mockito.doAnswer(getDummyPipedriveContactPostResponse()).when(cia).postContactList(anyList());
        Mockito.doReturn(getDummyPipedriveContactResponse()).when(cia).getAllContacts();


        newImporter.importOrganisationsAndContactsFromVertec();
        newImporter.importDealsFromVertec();
        newImporter.importActivitiesFromVertec();

        newImporter.importContactsFromPipedrive();

        newImporter.importMissingOrganistationsFromVertec();

        newImporter.importMissingContactsFromVertec();

        newImporter.getVertecContactList().stream()
                .forEach(contact -> {
//                    System.out.println(contact.getFirstName()+ " " + contact.getSurname() +  " active?: " + contact.getActive());
                    if(contact.getActive() != null) {
                        assertTrue("Found inactive contact in zuk list", contact.getActive());
                    } else {
                        System.out.println(contact.toPrettyJSON());
                    }
                });

        newImporter.populateOrganisationPostList();

        newImporter.populateContactPostAndPutLists();

        newImporter.contactPostList.stream()
                .forEach(contact ->{
                    assertTrue("Found inactive contact in post list", contact.getActive_flag());
                });

        newImporter.contactPutList.stream()
                .forEach(contact -> assertTrue("Found inactive contact in put list", contact.getActive_flag()));

    }

    @Test
    public void doesNotTryToPostDealWithoutTitle() throws IOException {
        when(vertec.getZUKOrganisations()).thenReturn(getDummyOrganisationsResponse());
        when(vertec.getZUKProjects()).thenReturn(getDummyProjectsResponse());
        when(vertec.getZUKActivities()).thenReturn(getDummyActivitiesResponse());

        when(vertec.getOrganisation(anyLong())).thenAnswer(getOrgResponseEntityAnswer());
        when(vertec.getContact(anyLong())).thenReturn(getDummyMissingContactResponse());

        when(pipedrive.postOrganisationList(anyList()))
                .thenAnswer(getDummyPipedriveOrganisationPostAnswer());

        when(pipedrive.getAllContacts()).thenReturn(getDummyPipedriveContactResponse());
        when(pipedrive.getAllDeals()).thenReturn(getDummyPipedriveDealResponse());

        importer.importOrganisationsAndContactsFromVertec();
        importer.importDealsFromVertec();

        importer.importContactsFromPipedrive();
        importer.importDealsFromPipedrive();

        importer.populateOrganisationPostList();
        importer.postOrganisationPostList();

        importer.populateContactPostAndPutLists();
        importer.postAndPutContactPostAndPutLists();

        importer.populateDealPostAndPutList();

        importer.dealPutList.stream()
                .forEach(deal -> {
                    assertTrue("No title for deal with v_id : " + deal.getV_id(), ! deal.getTitle().isEmpty());
                    assertTrue("No project title not set : " + deal.getV_id(), ! deal.getProject_number().isEmpty());
                });

        importer.dealPostList.stream()
                .forEach(deal -> {
                    assertTrue("No title for deal with v_id : " + deal.getV_id(), ! deal.getTitle().isEmpty());
                    assertTrue("No project title not set : " + deal.getV_id(), ! deal.getProject_number().isEmpty());
                });
    }


    @Test
    public void constructTeamMapDoesSo() throws IOException {

        when(pipedrive.getAllUsers()).thenReturn(getDummyUsersResponse());
        when(vertec.getTeamDetails()).thenReturn(getDummyVertecTeamResponse());

        importer.constructTeamIdMap(importer.getVertecUserEmails(), importer.getPipedriveUsers());

        importer.getPipedriveUsers().stream()
                .filter(PDUser::getActive_flag)
                .forEach(user -> {
                    assertEquals(importer.teamIdMap.get(user.getEmail()), user.getId());
                });

        assertEquals("Sabines duplicate emails not handled",
                importer.teamIdMap.get("sabine.streuss@zuhlke.com"),
                importer.teamIdMap.get("sabine.strauss@zuhlke.com"));
        assertTrue("Bryan Thal ok", importer.teamIdMap.get("bryan.thal@zuhlke.com") != null);

    }

    private ResponseEntity<PDUserItemsResponse> getDummyUsersResponse() throws IOException {
        ObjectMapper m =  new ObjectMapper();
        PDUserItemsResponse u =  m.readValue(new File("src/test/resources/Pipedrive users.json"), PDUserItemsResponse.class);
        return new ResponseEntity<>(u, HttpStatus.OK);

    }



    @Test
    public void findActivitiesLinkedToOrganisations() throws IOException {
        final ZUKActivities activitiesResponse = getDummyActivitiesResponse().getBody();
        final ZUKOrganisations zukOrgs = getDummyOrganisationsResponse().getBody();

        List<Long> orgids = zukOrgs.getOrganisationList().stream()
                .map(JSONOrganisation::getObjid)
                .collect(toList());

        List<JSONActivity> activities = new ArrayList<>();

        activitiesResponse.getActivityList().forEach(activity -> {
            if (orgids.contains(activity.getCustomer_link())) {
                activities.add(activity);
            }
        });

        System.out.println("Activities pointed to by organisationState (" + activities.size() + "): ");

        activities.forEach(acttivity -> System.out.println(acttivity.getId()));
    }

    private ResponseEntity<ZUKTeam> getDummyVertecTeamResponse() throws IOException {
        ObjectMapper m =  new ObjectMapper();
        ZUKTeam u =  m.readValue(new File("src/test/resources/Old/VRAPI Team.json"), ZUKTeam.class);
        return new ResponseEntity<>(u, HttpStatus.OK);

    }
}
