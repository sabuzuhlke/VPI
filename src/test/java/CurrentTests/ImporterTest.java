package CurrentTests;

import VPI.Importer;
import VPI.PDClasses.Contacts.PDContactListReceived;
import VPI.PDClasses.Deals.PDDealItemsResponse;
import VPI.PDClasses.Organisations.PDOrganisationSend;
import VPI.PDClasses.PDService;
import VPI.VertecClasses.VertecActivities.ZUKActivities;
import VPI.VertecClasses.VertecOrganisations.JSONContact;
import VPI.VertecClasses.VertecOrganisations.JSONOrganisation;
import VPI.VertecClasses.VertecOrganisations.ZUKOrganisations;
import VPI.VertecClasses.VertecProjects.ZUKProjects;
import VPI.VertecClasses.VertecService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
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
        when(vertec.getOrganisation(anyLong())).thenReturn(getDummyMissingOrganisationResponse());
        when(vertec.getContact(anyLong())).thenReturn(getDummyMissingContactResponse());

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
        inOrder.verify(iSpy).buildOrganisationHierarchyMap();

        inOrder.verify(iSpy).populateContactPostAndPutLists();
        inOrder.verify(iSpy).postAndPutContactPostAndPutLists();
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
        when(vertec.getOrganisation(anyLong())).thenReturn(getDummyMissingOrganisationResponse());

        importer.importOrganisationsAndContactsFromVertec();
        importer.importDealsFromVertec();
        importer.importActivitiesFromVertec();

        int orgListSize = importer.getVertecOrganisationList().size();
        int orgMapSize  = importer.organisationIdMap.size();
        int contMapSize = importer.contactIdMap.size();

        assertEquals("Organisation List and Map Sizes do not match", orgListSize, orgMapSize);
        assertTrue(importer.missingOrganisationIds.isEmpty());
        assertTrue(importer.missingContactIds.isEmpty());

        importer.importMissingOrganistationsFromVertec();

        int orgListSizeAfter = importer.getVertecOrganisationList().size();
        int orgMapSizeAfter  = importer.organisationIdMap.size();
        int contMapSizeAfter = importer.contactIdMap.size();

        assertEquals(orgListSize + importer.extractListOfMissingOrganisationIds().size(), orgListSizeAfter);
        assertEquals(orgMapSize + 1, orgMapSizeAfter);
        assertEquals(contMapSize + 1, contMapSizeAfter);

        assertEquals("Missing org id not added to missingorgids List",
                1, //value is 1 because stubbed call to getOrganisation always returns Organisation with id: 1L
                importer.missingOrganisationIds.size());
        assertEquals("Missing nested contact id not added to missingcontids List",
                1, //value is 1 because stubbed call to getOrganisation always returns Organisation with id: 1L
                importer.missingContactIds.size());

        verify(vertec, times(importer.extractListOfMissingOrganisationIds().size())).getOrganisation(anyLong());
    }

    private ResponseEntity<JSONOrganisation> getDummyMissingOrganisationResponse() {
        JSONOrganisation org = new JSONOrganisation();
        org.setObjid(1L);
        JSONContact contact = new JSONContact();
        contact.setObjid(6996L);
        List<JSONContact> contacts = new ArrayList<>();
        contacts.add(contact);
        org.setContacts(contacts);
        return new ResponseEntity<>(org, HttpStatus.OK);
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

}
