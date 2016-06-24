package CurrentTests;

import VPI.Importer;
import VPI.PDClasses.Activities.PDActivitySend;
import VPI.PDClasses.Contacts.ContactDetail;
import VPI.PDClasses.Contacts.PDContactListReceived;
import VPI.PDClasses.Contacts.PDContactReceived;
import VPI.PDClasses.Contacts.PDContactSend;
import VPI.PDClasses.Deals.PDDealItemsResponse;
import VPI.PDClasses.Deals.PDDealSend;
import VPI.PDClasses.Organisations.PDOrganisationSend;
import VPI.PDClasses.Organisations.PDRelationship;
import VPI.PDClasses.PDService;
import VPI.VertecClasses.VertecActivities.ZUKActivities;
import VPI.VertecClasses.VertecOrganisations.JSONContact;
import VPI.VertecClasses.VertecOrganisations.JSONOrganisation;
import VPI.VertecClasses.VertecOrganisations.ZUKOrganisations;
import VPI.VertecClasses.VertecProjects.JSONProject;
import VPI.VertecClasses.VertecProjects.ZUKProjects;
import VPI.VertecClasses.VertecService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.MockitoAnnotations;
import org.mockito.stubbing.Answer;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
/**
 * Created by gebo on 07/06/2016.
 */
public class ImporterTest {

    private Importer importer;
    private PDService pipedrive;
    private VertecService vertec;

    @Before
    public void prepareDependancies() {
        MockitoAnnotations.initMocks(this);
        pipedrive = mock(PDService.class);
        vertec = mock(VertecService.class);
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
        inOrder.verify(iSpy).populateFollowerPostList();
        inOrder.verify(iSpy).postContactFollowers();

        inOrder.verify(iSpy).populateDealPostAndPutList();
        inOrder.verify(iSpy).postAndPutDealPostAndPutList();

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
        //Magic number 2  because there are 2 new fake parent organisations being imported (7777, 9999)
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
                missingOrgSize, //value is 1 because stubbed call to getOrganisation always returns Organisation with id: 1L
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
        PDContactListReceived body = m.readValue(new File("src/test/resources/Pipedrive getAllContactsResponse.json"), PDContactListReceived.class);
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
        PDDealItemsResponse body = m.readValue(new File("src/test/resources/Pipedrive getAllDealsResponse.json"), PDDealItemsResponse.class);
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

        assertTrue(! importer.organisationIdMap.containsValue(-1L));
    }

    private Answer<ResponseEntity<JSONOrganisation>> getOrgResponseEntityAnswer() {
        return invocation -> {
            Object[] args = invocation.getArguments();

            JSONOrganisation org = new JSONOrganisation();
            org.setObjid((Long) args[0]);
            JSONContact contact = new JSONContact();
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

        List<PDRelationship> relationships = importer.builOrganisationHierarchies();

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
        contact.setObjid(6996L);
        org.setParentOrganisationId(9999L);
        return new ResponseEntity<>(org, HttpStatus.OK);
    }

    private ResponseEntity<JSONOrganisation> getOrgWithParent3() {
        JSONOrganisation org = new JSONOrganisation();
        org.setObjid(9999L);
        JSONContact contact = new JSONContact();
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

        assertEquals(importer.getVertecContactList().size(), importer.contactPostList.size() + importer.contactPutList.size());

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
        assertTrue(importer.contactIdMap.containsValue(18194L));

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
            cont.setObjid((Long) args[0]);

            return new ResponseEntity<>(cont, HttpStatus.OK);
        };
    }

    @Test
    public void postsAllFollowers() throws IOException {
        when(vertec.getZUKOrganisations()).thenReturn(getDummyOrganisationsResponse());
        when(pipedrive.getAllContacts()).thenReturn(getDummyPipedriveContactResponse());

        importer.importOrganisationsAndContactsFromVertec();

        importer.importContactsFromPipedrive();

        assertTrue(! importer.teamIdMap.isEmpty());

        importer.populateContactPostAndPutLists();

        importer.postAndPutContactPostAndPutLists();

        importer.populateFollowerPostList();

        importer.postContactFollowers();

        int nrFollowers = importer.getVertecContactList().stream()
                .map(JSONContact::getFollowers)
                .flatMap(Collection::stream)
                .collect(toList())
                .size();

        assertEquals("Not all followers added to post list", nrFollowers,importer.followerPostList.size());


        importer.followerPostList.stream()
                .forEach(follower -> {
                 assertTrue("Contact " + follower.getObjectID() + " is not part of contactIdMap, but got added to followers"
                         , importer.contactIdMap.containsValue(follower.getObjectID()));

                 assertTrue("User " + follower.getUserID() + " is not part of team, but got added to foillowers"
                         ,importer.teamIdMap.containsValue(follower.getUserID()));
                });

        verify(pipedrive, times(importer.followerPostList.size())).postFollowerToContact(anyObject());
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
        importer.populateFollowerPostList();
        importer.postContactFollowers();

        assertTrue(importer.dealPostList.isEmpty());
        assertTrue(importer.dealPutList.isEmpty());

        importer.populateDealPostAndPutList();

        assertTrue( ! importer.dealPostList.isEmpty());
        assertTrue( ! importer.dealPutList.isEmpty());

        int numOfPhases = importer.getVertecProjectList().stream()
                .map(JSONProject::getPhases)
                .flatMap(Collection::stream)
                .collect(toList()).size();

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
                    }
                    if (deal.getStatus().equals("lost")) {
                        assertNotNull(deal.getLost_time());
                        assertNotNull(deal.getLost_reason());
                    }
                    assertNotNull(deal.getAdd_time());
                    assertNotNull(deal.getPhase());
                    assertNotNull(deal.getProject_number());
                    assertNotNull(deal.getTitle());
                    assertNotNull(deal.getStage_id());
                    assertNotNull(deal.getValue());
                    assertNotNull(deal.getAdd_time());
                    assertNotNull(deal.getV_id());
                });
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
        importer.populateFollowerPostList();
        importer.postContactFollowers();

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
                    if (activity.getDone()) {
                        assertNotNull(activity.getDone_date());
                    }
                    assertNotNull(activity.getNote());
                    assertTrue(activity.getDeal_id() != null
                            || activity.getOrg_id() != null
                            || activity.getPerson_id() != null);
                });

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


}
