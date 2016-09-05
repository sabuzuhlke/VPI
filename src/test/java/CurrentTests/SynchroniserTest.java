package CurrentTests;

import VPI.Entities.Organisation;
import VPI.Entities.OrganisationState;
import VPI.PDClasses.Contacts.PDContactListReceived;
import VPI.PDClasses.Deals.PDDealItemsResponse;
import VPI.PDClasses.HierarchyClasses.LinkedOrg;
import VPI.PDClasses.HierarchyClasses.PDRelationshipReceived;
import VPI.PDClasses.Organisations.PDOrganisationItemsResponse;
import VPI.PDClasses.Organisations.PDOrganisationSend;
import VPI.PDClasses.PDService;
import VPI.PDClasses.Updates.PDLogList;
import VPI.PDClasses.Updates.PDUpdate;
import VPI.PDClasses.Updates.PDUpdateLog;
import VPI.PDClasses.Users.PDUserItemsResponse;
import VPI.SynchroniserClasses.Synchroniser;
import VPI.SynchroniserClasses.SynchroniserState;
import VPI.VertecClasses.VertecActivities.ZUKActivities;
import VPI.VertecClasses.VertecOrganisations.JSONContact;
import VPI.VertecClasses.VertecOrganisations.JSONOrganisation;
import VPI.VertecClasses.VertecOrganisations.OrganisationList;
import VPI.VertecClasses.VertecProjects.ZUKProjects;
import VPI.VertecClasses.VertecService;
import VPI.VertecClasses.VertecTeam.EmployeeList;
import VPI.VertecClasses.VertecTeam.ZUKTeam;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.org.apache.regexp.internal.RE;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertFalse;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * These tests require the ProductionKeys to be used to pass.
 * As the Dummy Pipedrive Responses have been taken from the Production instance
 */
public class SynchroniserTest {
    private Synchroniser synchroniser;

    private VertecService vertec;
    private PDService pipedrive;


    @Before
    public void setUp() throws IOException {
        MockitoAnnotations.initMocks(this);
        pipedrive = mock(PDService.class);
        vertec = mock(VertecService.class);


        when(pipedrive.getAllUsers()).thenReturn(getDummyUsersResponse());
        when(vertec.getTeamDetails()).thenReturn(getOldDummyTeamResponse()); // for initialisaton of importer

        when(vertec.getSalesTeam()).thenReturn(getDummyVertecTeamResponse().getBody().getEmployees());


        when(pipedrive.getAllOrganisations()).thenReturn(getDummyPipedriveOrganisationsResponse());
        when(pipedrive.getRelationships(anyLong())).thenAnswer(getPDRelationshipAnswer());

        when(vertec.getAllZUKOrganisations()).thenReturn(getDummyVertecOrganisationsResponse());
        when(vertec.getOrganisationList(anyList())).thenReturn(getDummyVertecOrganisationsFromPipedriveResponse());
        doAnswer(getOrganisationUpdateLogAnswer()).when(pipedrive).getUpdateLogsFOrOrganisation(anyLong());

        synchroniser = new Synchroniser(pipedrive, vertec);
    }

    @Test
    public void canGetVertecOrganisations() throws IOException {
        when(vertec.getAllZUKOrganisations()).thenReturn(getDummyVertecOrganisationsResponse());
        when(pipedrive.getAllOrganisations()).thenReturn(getDummyPipedriveOrganisationsResponse());
        when(vertec.getOrganisationList(anyList())).thenReturn(getDummyVertecOrganisationsFromPipedriveResponse());

        OrganisationState porgs = synchroniser.getPipedriveState().getOrganisationState();
        OrganisationState orgs = synchroniser.getVertecState().getOrganisationState();


        Organisation organisation = orgs.getOrganisationByVertecId(1910117L);

        assertTrue(organisation.getActive());

        assertEquals(1910117L, organisation.getVertecId().longValue());
        assertEquals(771L, organisation.getPipedriveId().longValue());

        assertEquals("Sales Team", organisation.getOwnedOnVertecBy());
        assertEquals("wolfgang.emmerich@zuhlke.com", organisation.getSupervisingEmail());
        assertEquals("CATEGORY PLACEHOLDER", organisation.getCategory());
        assertEquals("BUSINESS DOMAIN PLACEHOLDER", organisation.getBusinessDomain());
        assertEquals("London", organisation.getCity());
        assertEquals("United Kingdom", organisation.getCountry());
        assertEquals("WC1X 8HL", organisation.getZip());
        assertEquals("WC1X 8HL", organisation.getZip());
        assertEquals("2008-07-07 12:52:49", organisation.getCreated());

        assertTrue(organisation.getFullAddress().contains("Gray's Inn Road"));
        assertTrue(organisation.getStreet().contains("Gray's Inn Road"));

        //System.out.println(orgs.getAllOrganisations());

        organisation = orgs.getOrganisationByVertecId(10001010101010L);
        System.out.println(organisation);
        assertEquals("Gebo TEST ORG -- fake data does not actually exist on pipedrive or vertec", organisation.getName());

        assertEquals(0, orgs.organisationsWithoutVIDs.size());

    }

    @Test
    public void canGetPipedriveOrganisations() throws IOException {
        when(pipedrive.getAllOrganisations()).thenReturn(getDummyPipedriveOrganisationsResponse());
        when(pipedrive.getRelationships(anyLong())).thenAnswer(getPDRelationshipAnswer());

        OrganisationState orgs = synchroniser.getPipedriveState().getOrganisationState();

        Organisation testOrg = orgs.getOrganisationByPipedriveId(2051L);

        System.out.println(testOrg);
        assertTrue(testOrg.getName().contains("General Electric (Switzerland) GmbH"));
        assertEquals("sabine.strauss@zuhlke.com", testOrg.getSupervisingEmail());
        assertEquals(1359817L, testOrg.getVertecId().longValue());


        testOrg = orgs.getOrganisationByPipedriveId(460L);
        System.out.println(testOrg);
        assertEquals("News UK", testOrg.getName());
        assertEquals(null, testOrg.getVertecId());
        assertEquals(null, testOrg.getVertecId());


    }

    @Test
    public void canRecogniseOrgsToCreateOnPipedrive() throws IOException {
        when(vertec.getAllZUKOrganisations()).thenReturn(getDummyVertecOrganisationsResponse());
        when(pipedrive.getAllOrganisations()).thenReturn(getDummyPipedriveOrganisationsResponse());
        when(vertec.getOrganisationList(anyList())).thenReturn(getDummyVertecOrganisationsFromPipedriveResponse());

        synchroniser.getStateDifference()
                .getOrganisationDifferences()
                .findOrganisationsToCreateOnPipedrive(synchroniser.getVertecState(), synchroniser.getSynchroniserState(), synchroniser.getPipedriveState());

        List<Long> idsSelected = synchroniser.getStateDifference().getOrganisationDifferences().getCreateOnPipedrive().stream()
                .map(Organisation::getVertecId)
                .collect(toList());
        System.out.println("Ids added to postlist: ");
        System.out.println(idsSelected);

        List<Long> controlList = synchroniser.getVertecState().getOrganisationState().getAllOrganisations().stream()
                .filter(org -> !synchroniser.getSynchroniserState().getOrganisationIdMap().containsKey(org.getVertecId()))
                .filter(org -> !synchroniser.getPipedriveState().getOrganisationState().organisationsWithVIDs.containsKey(org.getVertecId()))
                .map(Organisation::getVertecId)
                .collect(toList());
        assertEquals("wrong creation", controlList.size(), idsSelected.size());
    }

    @Test
    public void canRecogniseOrgsToCreateOnVertec() throws IOException {
        when(pipedrive.getAllOrganisations()).thenReturn(getDummyPipedriveOrganisationsResponse());
        when(pipedrive.getRelationships(anyLong())).thenAnswer(getPDRelationshipAnswer());

        synchroniser.getPipedriveState().organisationState = synchroniser.getPipedriveState().getOrganisationState();

        synchroniser.getStateDifference()
                .getOrganisationDifferences()
                .findOrganisationsToCreateOnVertec(synchroniser.getPipedriveState());

        List<Long> idsSelected = synchroniser.getStateDifference().getOrganisationDifferences().getCreateOnVertec().stream()
                .map(Organisation::getPipedriveId)
                .collect(toList());

        List<Long> controlList = pipedrive.getAllOrganisations().getBody().getData().stream()
                .filter(org -> org.getV_id() == null)
                .map(org -> org.getId())
                .collect(toList());
        System.out.println("ids selected:");
        System.out.println(idsSelected);
        assertEquals("Not all organisationState to create on vertec were found in  test set", controlList.size(), idsSelected.size());


    }

    @Test
    public void canRecogniseorgsToDelFromPipedrive() throws IOException {

        Long orgToDelFromPD = 3016658L;
        Long orgToDelFromPDPID = 978L;

        Long orgtoConflictDel = 3616716L;
        Long orgtoConflictDelPID = 1184L;




        when(pipedrive.getAllOrganisations()).thenReturn(getDummyPipedriveOrganisationsResponse());
        when(pipedrive.getRelationships(anyLong())).thenAnswer(getPDRelationshipAnswer());

        when(vertec.getAllZUKOrganisations()).thenReturn(getDummyVertecOrganisationsResponse());
        when(vertec.getOrganisationList(anyList())).thenReturn(getDummyVertecOrganisationsFromPipedriveResponse());

        List<Long> idsToDel = synchroniser.getStateDifference()
                .getOrganisationDifferences()
                .getDeleteFromPipedrive().stream()
                .map(Organisation::getVertecId)
                .collect(toList());


        Map<Organisation, Organisation> conflicts = synchroniser.getStateDifference().getOrganisationDifferences().getDeletionFromPipedriveConflicts();
        System.out.println(idsToDel);

        assertEquals("Would delete more orgs than necessary", 1, idsToDel.size());
        assertEquals("More delete conflicts than actually", 1, conflicts.size());

        assertEquals("Would del wrong org", orgToDelFromPD, idsToDel.get(0));
        assertEquals("wrong orgdel conflict", orgtoConflictDel, conflicts.keySet().stream()
                .map(Organisation::getVertecId)
                .collect(toList())
                .get(0)
        );


    }


    @Test
    public void canRecogniseOrgsToDelFromVertec() throws IOException {

        Long conflicVertecId = 1976289L;
        Long conflictPDId = 962L;

        Long deletionVid = 7927685L;
        Long deletionPID = 1654L;
        when(pipedrive.getAllOrganisations()).thenReturn(getDummyPipedriveOrganisationsResponse());

        when(vertec.getAllZUKOrganisations()).thenReturn(getDummyVertecOrganisationsResponse());
        when(vertec.getOrganisationList(anyList())).thenReturn(getDummyVertecOrganisationsFromPipedriveResponse());


        //check result
        System.out.println("Ids to del from vertec");
        Set<Long> idsTodel = synchroniser.getStateDifference().getOrganisationDifferences().getDeleteFromVertec();
        System.out.println(idsTodel);
        System.out.println(idsTodel.size());

        assertEquals(1, idsTodel.size());
        assertTrue(idsTodel.contains(7927685L));

        System.out.println("\n DeletionConflicts");
        Set<Long> deletionconflicts = synchroniser.getStateDifference().getOrganisationDifferences().getDeletionFromVertecConflicts();
        System.out.println(deletionconflicts);
        assertEquals(2, deletionconflicts.size());
        assertTrue(deletionconflicts.contains(250L)); // will not actually be deleted, as problem has been fixed
        assertTrue(deletionconflicts.contains(962L));

        assertEquals(2, deletionconflicts.size());
        assertTrue(deletionconflicts.contains(conflictPDId));

    }

    @Test
    public void canDealWithPDOrgModifiedBySync(){

        doAnswer(getOrganisationUpdateLogAnswer()).when(pipedrive).getUpdateLogsFOrOrganisation(anyLong());

        Organisation org = new Organisation();
        org.setName("Test org without VID"); //imitates org with pid 3
        org.setPipedriveId(3L);
        org.setModified("2016-09-01 00:00:00");

        Organisation org2 = new Organisation();
        org2.setName("Test org With VID"); //imitates org with pid 22 orgChange on line 202
        org2.setPipedriveId(22L);
        org2.setVertecId(1L);
        org2.setModified("2016-09-01 00:00:00"); //orgchange on line 278

        Organisation org3 = new Organisation();
        org3.setName("TestOrg without VID that should not be in any of the lists at the end of the test based on modifierID");
        org3.setPipedriveId(25L);
        org3.setModified("2016-09-01 00:00:00"); //orgchange on line 488

        Organisation org4 = new Organisation(); //orgChange on line 532
        org4.setName("TestOrg without VID that should not be in any of the lists at the end of the test based on modification date");
        org4.setPipedriveId(31L);
        org4.setModified("2016-03-09 00:00:00");

        synchroniser.getPipedriveState().getOrganisationState().syncModifiedOrganisationsWithVIDs = new HashMap<>();
        synchroniser.getPipedriveState().getOrganisationState().syncModifiedOrganisationsWithoutVIDs = new HashSet<>();

        synchroniser.getPipedriveState().getOrganisationState().dealWithPDOrgModifiedBySynchroniser(org);
        synchroniser.getPipedriveState().getOrganisationState().dealWithPDOrgModifiedBySynchroniser(org2);
        synchroniser.getPipedriveState().getOrganisationState().dealWithPDOrgModifiedBySynchroniser(org3);
        synchroniser.getPipedriveState().getOrganisationState().dealWithPDOrgModifiedBySynchroniser(org4);

        assertEquals(1, synchroniser.getPipedriveState().getOrganisationState().syncModifiedOrganisationsWithVIDs.size());
        assertEquals(22L,
                synchroniser
                        .getPipedriveState()
                        .getOrganisationState()
                        .syncModifiedOrganisationsWithVIDs
                        .get(1L)
                        .getPipedriveId()
                        .longValue());

        assertEquals(1, synchroniser.getPipedriveState().getOrganisationState().syncModifiedOrganisationsWithoutVIDs.size());
        assertEquals(3L,
                synchroniser.getPipedriveState().getOrganisationState().syncModifiedOrganisationsWithoutVIDs.stream()
                        .map(Organisation::getPipedriveId)
        .collect(toList())
                .get(0).longValue());
    }

    @Test
    public void canDealWithVertecOrgModifiedBySynchroniser(){

        VPI.VertecClasses.VertecOrganisations.Organisation org2 = new VPI.VertecClasses.VertecOrganisations.Organisation();
        org2.setName("Test org With VID");
        org2.setVertecId(2L);
        org2.setModified("2016-09-01T00:00:00");
        org2.setModifier(SynchroniserState.SYNCHRONISER_VERTEC_USERID);

        VPI.VertecClasses.VertecOrganisations.Organisation org3 = new VPI.VertecClasses.VertecOrganisations.Organisation();
        org3.setName("TestOrg that should not be in any of the lists at the end of the test based on modifierID");
        org3.setVertecId(3L);
        org3.setModified("2016-09-01T00:00:00");
        org3.setModifier(5295L);

        VPI.VertecClasses.VertecOrganisations.Organisation org4 = new VPI.VertecClasses.VertecOrganisations.Organisation();
        org4.setName("TestOrg that should not be in any of the lists at the end of the test based on modification date");
        org4.setVertecId(4L);
        org4.setModified("2016-03-09T00:00:00");
        org4.setModifier(SynchroniserState.SYNCHRONISER_VERTEC_USERID);

        synchroniser.getVertecState().getOrganisationState().syncModifiedOrganisationsWithVIDs = new HashMap<>();
        synchroniser.getVertecState().getOrganisationState().syncModifiedOrganisationsWithoutVIDs = new HashSet<>();

        synchroniser.getVertecState().getOrganisationState().dealWithVertecOrgModifiedBySynchroniser(org2);
        synchroniser.getVertecState().getOrganisationState().dealWithVertecOrgModifiedBySynchroniser(org3);
        synchroniser.getVertecState().getOrganisationState().dealWithVertecOrgModifiedBySynchroniser(org4);

        assertEquals(0, synchroniser.getVertecState().getOrganisationState().syncModifiedOrganisationsWithoutVIDs.size());
        assertEquals(1, synchroniser.getVertecState().getOrganisationState().syncModifiedOrganisationsWithVIDs.size());
        assertEquals("Test org With VID", synchroniser.getVertecState().getOrganisationState().syncModifiedOrganisationsWithVIDs.get(2L).getName());


    }

    @Test
    public void canDecideWhereToUpdate() throws IOException {
        when(pipedrive.getAllOrganisations()).thenReturn(getDummyPipedriveOrganisationsResponse());
        when(pipedrive.getRelationships(anyLong())).thenAnswer(getPDRelationshipAnswer());

        when(vertec.getAllZUKOrganisations()).thenReturn(getDummyVertecOrganisationsResponse());
        when(vertec.getOrganisationList(anyList())).thenReturn(getDummyVertecOrganisationsFromPipedriveResponse());

        when(pipedrive.getUpdateLogsFOrOrganisation(anyLong())).thenAnswer(getOrganisationUpdateLogAnswer());

//        System.out.println("No changes: ");
//        System.out.println(synchroniser.getStateDifference().getOrganisationDifferences().getNoChanges());
        System.out.println(synchroniser.getStateDifference().getOrganisationDifferences().getCreateOnVertec().size() + " organisations will be added to vertec:\n\n\n");
        synchroniser.getStateDifference().getOrganisationDifferences().getCreateOnVertec().forEach(org -> {
            System.out.println(org.getName() + " created by " + org.getSupervisingEmail());
        });
        System.out.println("\n\n=================================================\n\n");
        System.out.println(synchroniser.getStateDifference().getOrganisationDifferences().getUpdateOnVertec().size() + " organisations will be updated on vertec\n\n\n");
        synchroniser.getStateDifference().getOrganisationDifferences().getUpdateOnVertec().forEach(org -> {
            System.out.println(org.toJSONString());
        });
        System.out.println("\n\n=================================================\n\n");
        System.out.println(synchroniser.getStateDifference().getOrganisationDifferences().getDeleteFromVertec().size()+ " organisations will be deleted from vertec");
        synchroniser.getStateDifference().getOrganisationDifferences().getDeleteFromVertec().forEach(org -> {
            System.out.println(synchroniser.getVertecState().getOrganisationState().organisationsWithVIDs.get(org).toJSONString());
        });
        System.out.println("\n\n=================================================\n\n");
        System.out.println(synchroniser.getStateDifference().getOrganisationDifferences().getDeletionFromVertecConflicts().size() + " organisations have been deleted on pipedrive but have been updated on vertec, so they are 'deletion conflicts' (This is fake example for testing)");
        System.out.println("Vertec IDs: " + synchroniser.getStateDifference().getOrganisationDifferences().getDeletionFromVertecConflicts());


        System.out.println("\n\n=================================================\n\n");
        System.out.println(synchroniser.getStateDifference().getOrganisationDifferences().getCreateOnPipedrive().size() + " organisations will be posted to pipedrive");
        synchroniser.getStateDifference().getOrganisationDifferences().getCreateOnPipedrive().forEach(org -> {
            System.out.println(org.getName() + " created by " + org.getSupervisingEmail());
        });
        System.out.println("\n\n=================================================\n\n");
        System.out.println(synchroniser.getStateDifference().getOrganisationDifferences().getUpdateOnPipedrive().size() + " organisations will be updated on pipedrive\n\n\n");
        synchroniser.getStateDifference().getOrganisationDifferences().getUpdateOnPipedrive().forEach(org -> {
            System.out.println(org.toJSONString());
        });

        System.out.println("\n\n=================================================\n\n");
        System.out.println(synchroniser.getStateDifference().getOrganisationDifferences().getDeleteFromPipedrive().size() + " organisations will be deleted from pipedrive (This fake again)");
        synchroniser.getStateDifference().getOrganisationDifferences().getDeleteFromPipedrive().forEach(org -> {
            System.out.println(org.toJSONString());
        });

        System.out.println("\n\n=================================================\n\n");
        System.out.println(synchroniser.getStateDifference().getOrganisationDifferences().getDeletionFromPipedriveConflicts().size() + " organisations have been deleted on vertec but have been updated on pipedrive, so they are 'deletion conflicts' (This is fake example for testing)");
        System.out.println("Vertec IDs: " + synchroniser.getStateDifference().getOrganisationDifferences().getDeletionFromPipedriveConflicts());



        System.out.println("\n\n=================================================\n\n");
        System.out.println(synchroniser.getStateDifference().getOrganisationDifferences().getUpdateConflicts().size() + " organisations have both been updated so will be marked as an 'update conflict'");
        synchroniser.getStateDifference().getOrganisationDifferences().getUpdateConflicts().forEach(org -> {
            synchroniser.getStateDifference().getOrganisationDifferences().getUpdateConflictsReciprocal().forEach(org2 -> {
                if (org.getVertecId().longValue() == org2.getVertecId()) {
                    System.out.println("Vertec Version: ");
                    System.out.println("\n");
                    System.out.println(org2.toJSONString());
                    System.out.println("\n\n");
                    System.out.println("Pipedrive Version: \n\n");
                    System.out.println(org.toJSONString());
                }
            });
        });


//        System.out.println("\n\n=================================================\n\n");
//        System.out.println("These organisations recieved no updates");
//        synchroniser.getStateDifference().getOrganisationDifferences().getNoChanges().forEach(org -> {
//            System.out.println(org.toJSONString());
//        });
        System.out.println("\n\n\n" + synchroniser.getStateDifference().getOrganisationDifferences().getNoChanges().size() + " Organisations did not change");


//        System.out.println("OrgsToPutToVertec:");
//        System.out.println(synchroniser.getStateDifference().getOrganisationDifferences().getUpdateOnVertec());
//        System.out.println("Listsize: " + synchroniser.getStateDifference().getOrganisationDifferences().getUpdateOnVertec().size());
//        System.out.println("OrgsToPutToPD:");
//        System.out.println(synchroniser.getStateDifference().getOrganisationDifferences().getUpdateOnPipedrive());
//        System.out.println("PDUpdate conflicts:");

//        System.out.println(synchroniser.getStateDifference().getOrganisationDifferences().getUpdateConflicts());
//        System.out.println(synchroniser.getStateDifference().getOrganisationDifferences().getUpdateConflicts().size());

    }

    @Test
    public void dateIsComparedOnTimeAsWell(){
        String d1 = "2016-07-06T16:25:23";
        String d2 = "2016-07-06T18:00:00";

        LocalDateTime ldt1 = LocalDateTime.parse(d1);
        LocalDateTime ldt2 = LocalDateTime.parse(d2);

        assertTrue(ldt2.isAfter(ldt1));
        assertFalse(ldt1.isAfter(ldt2));

    }

    //=================================MOCKITO DUMMY RESPONSES==========================================================

    public static ResponseEntity<PDUserItemsResponse> getDummyUsersResponse() throws IOException {
        ObjectMapper m = new ObjectMapper();
        PDUserItemsResponse u = m.readValue(new File("src/test/resources/Pipedrive users.json"), PDUserItemsResponse.class);
        return new ResponseEntity<>(u, HttpStatus.OK);

    }

    public static ResponseEntity<EmployeeList> getDummyVertecTeamResponse() throws IOException {
        ObjectMapper m = new ObjectMapper();
        EmployeeList u = m.readValue(new File("src/test/resources/ZUKTeam.json"), EmployeeList.class);
        return new ResponseEntity<>(u, HttpStatus.OK);

    }

    public static ResponseEntity<OrganisationList> getDummyVertecOrganisationsResponse() throws IOException {
        ObjectMapper m = new ObjectMapper();
        OrganisationList body = m.readValue(new File("src/test/resources/AllVertecOrganisations.json"), OrganisationList.class);
        return new ResponseEntity<>(body, HttpStatus.OK);
    }

    public static ResponseEntity<OrganisationList> getDummyVertecOrganisationsFromPipedriveResponse() throws IOException {
        ObjectMapper m = new ObjectMapper();
        OrganisationList body = m.readValue(new File("/Users/gebo/IdeaProjects/VPI/src/test/resources/PostedNonTeamOrganisations.json"), OrganisationList.class);
        return new ResponseEntity<>(body, HttpStatus.OK);
    }

    public static ResponseEntity<PDOrganisationItemsResponse> getDummyPipedriveOrganisationsResponse() throws IOException {
        ObjectMapper m = new ObjectMapper();
        PDOrganisationItemsResponse body = m.readValue(new File("src/test/resources/AllPipedriveOrganisations.json"), PDOrganisationItemsResponse.class);
        return new ResponseEntity<>(body, HttpStatus.OK);
    }

    public static ResponseEntity<ZUKProjects> getDummyProjectsResponse() throws IOException {
        ObjectMapper m = new ObjectMapper();
        ZUKProjects body = m.readValue(new File("src/test/resources/VRAPI projects.json"), ZUKProjects.class);
        return new ResponseEntity<>(body, HttpStatus.OK);
    }

    public static ResponseEntity<ZUKActivities> getDummyActivitiesResponse() throws IOException {
        ObjectMapper m = new ObjectMapper();
        ZUKActivities body = m.readValue(new File("src/test/resources/VRAPI Activity Response.json"), ZUKActivities.class);
        return new ResponseEntity<>(body, HttpStatus.OK);
    }

    public static ResponseEntity<PDUpdateLog> getDummyPipedriveOrganisationUpdateLog(Long id) throws IOException {
        ObjectMapper m = new ObjectMapper();
        PDLogList loglist = m.readValue(new File("src/test/resources/pdOrganisationUpdateLog"), PDLogList.class);

        for (PDUpdateLog log : loglist.getLogs()){
            if(log.getOrgid() == id.longValue()) return new ResponseEntity<PDUpdateLog>(log, HttpStatus.OK);
        }

        return null;
    }
    public static Answer<ResponseEntity<PDUpdateLog>> getOrganisationUpdateLogAnswer(){

        return invocation -> {
            Object[] args = invocation.getArguments();

           ResponseEntity<PDUpdateLog> logs = getDummyPipedriveOrganisationUpdateLog((Long) args[0]);
            return logs;
        };

    }



    public static Answer<ResponseEntity<JSONOrganisation>> getOrgResponseEntityAnswer() {
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

    public static Answer<List<Long>> getDummyPipedriveOrganisationPostAnswer() {
        return invocation -> {
            Object[] args = invocation.getArguments();

            return ((List<PDOrganisationSend>) args[0]).stream()
                    .map(PDOrganisationSend::getV_id)
                    .map(id -> 5L).collect(toList());
        };
    }

    public static ResponseEntity<JSONContact> getDummyMissingContactResponse() {
        JSONContact contact = new JSONContact();
        contact.setObjid(666L);
        contact.setActive(true);
        return new ResponseEntity<>(contact, HttpStatus.OK);
    }

    public ResponseEntity<PDContactListReceived> getDummyPipedriveContactResponse() throws IOException {
        ObjectMapper m = new ObjectMapper();
        PDContactListReceived body = m.readValue(new File("src/test/resources/PipedriveProduction contacts.json"), PDContactListReceived.class);
        return new ResponseEntity<>(body, HttpStatus.OK);
    }

    public static ResponseEntity<PDDealItemsResponse> getDummyPipedriveDealResponse() throws IOException {
        ObjectMapper m = new ObjectMapper();
        PDDealItemsResponse body = m.readValue(new File("src/test/resources/PipedriveProduction deals.json"), PDDealItemsResponse.class);
        return new ResponseEntity<>(body, HttpStatus.OK);
    }

    public static ResponseEntity<ZUKTeam> getOldDummyTeamResponse() throws IOException {
        ObjectMapper m = new ObjectMapper();
        ZUKTeam u = m.readValue(new File("src/test/resources/Old/VRAPI Team.json"), ZUKTeam.class);
        return new ResponseEntity<>(u, HttpStatus.OK);

    }

    /**
     * This function serves to emulate what pipedrive would return when asked for relationships between organisationState
     * Setting the ids of the returned relationship subjects should not matter as long as they exist in the test dataset
     * Care should be taken to test appropriate values of organisation fields according to supplied test ids
     */

    public static Answer<List<PDRelationshipReceived>> getPDRelationshipAnswer() {
        return invocation -> {
            List<PDRelationshipReceived> rels = new ArrayList<>();
            PDRelationshipReceived pdr = new PDRelationshipReceived();
            LinkedOrg parentOrg = new LinkedOrg();
            parentOrg.setId(1089L);
            parentOrg.setName("ParentOrg");
            parentOrg.setAddress("42, Parenthood road");
            parentOrg.setOwnerId(1363410L);
            pdr.setParent(parentOrg);

            LinkedOrg relationshipSubject = new LinkedOrg(); //this org is to emulate the organisation the relationship is gottern for
            relationshipSubject.setName("Zumtobel Lighting");//DO NOT change this name, so that the tests will test the correct behaviour
            relationshipSubject.setId(2023L);
            relationshipSubject.setAddress("22 Jump Street");
            relationshipSubject.setOwnerId(1363402L);
            pdr.setDaughter(relationshipSubject);
            pdr.setType("parent");
            rels.add(pdr);

            pdr = new PDRelationshipReceived();
            LinkedOrg daughter = new LinkedOrg();
            daughter.setName("Daughter.co");
            daughter.setAddress("barbieville");
            daughter.setId(1292L);
            daughter.setOwnerId(1363410L);
            pdr.setDaughter(daughter);
            pdr.setParent(relationshipSubject);
            pdr.setType("parent");
            rels.add(pdr);


            return rels;
        };
    }





    @Test @Ignore
    public void listOfNonTeamOrgs() throws IOException {
        when(pipedrive.getAllOrganisations()).thenReturn(getDummyPipedriveOrganisationsResponse());
        when(pipedrive.getRelationships(anyLong())).thenAnswer(getPDRelationshipAnswer());

        when(vertec.getAllZUKOrganisations()).thenReturn(getDummyVertecOrganisationsResponse());
        when(vertec.getOrganisationList(anyList())).thenReturn(getDummyVertecOrganisationsFromPipedriveResponse());
        OrganisationState organisationsFromPipedrive = synchroniser.getPipedriveState().getOrganisationState();


        Map<Long, Organisation> orgmap = new HashMap<>();

        Map<Long, Organisation> organisations = synchroniser.getVertecState().getOrganisationState().organisationsWithVIDs;

        //extract list of VIDs
        Collection<Organisation> orgsFromPipedrive = organisationsFromPipedrive.organisationsWithVIDs.values();
        Set<Long> IdsFromVertec = organisations.keySet(); // this Set contains all Ids we have previously imported from vertec

        //get all of them that we haven't imported previously in getVertecOrganisations
        List<Long> unimportedIds = orgsFromPipedrive.stream()
                .filter(org -> !IdsFromVertec.contains(org.getVertecId()))
                .filter(org -> org.getOwnedOnVertecBy().equals("Sales Team"))
                .map(Organisation::getVertecId)
                .collect(Collectors.toList());

        System.out.println(unimportedIds);

    VertecService vertecService = new VertecService("localhost:9999");
        SynchroniserState syncState = synchroniser.getSynchroniserState();
        //get them all from vertec
        List<Organisation> vertecOrgsFromPipedrive = vertecService.getOrganisationList(unimportedIds)
                .getBody().getOrganisations().stream()
                .map(org -> {
                    System.out.println(org);
                    return org;
                })
                .map(org -> new Organisation(org,
                        //To set the pipedriveID of the created organisation we'll have to access the organisationState we got from pipedrive
                        organisationsFromPipedrive.organisationsWithVIDs.get(org.getVertecId()).getPipedriveId(),
                        syncState.getVertecOwnerMap().get(org.getOwnerId())))
                .collect(Collectors.toList());

        //return in appropriate format
        for (Organisation org : vertecOrgsFromPipedrive) {
            orgmap.put(org.getVertecId(), org);
        }
    }
    @Test
    public void canGetDummyPipedriveOrganisationUpdateLog() throws IOException {
        PDUpdateLog pul = getDummyPipedriveOrganisationUpdateLog(22L).getBody(); // example log in file
        assertEquals(22L, pul.getOrgid().longValue());
        System.out.println(pul);
    }

}

