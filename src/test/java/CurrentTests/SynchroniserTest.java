package CurrentTests;

import VPI.Entities.Organisation;
import VPI.Entities.OrganisationContainer;
import VPI.Importer;
import VPI.PDClasses.Contacts.PDContactListReceived;
import VPI.PDClasses.Deals.PDDealItemsResponse;
import VPI.PDClasses.HierarchyClasses.LinkedOrg;
import VPI.PDClasses.HierarchyClasses.PDRelationshipReceived;
import VPI.PDClasses.Organisations.PDOrganisationItemsResponse;
import VPI.PDClasses.Organisations.PDOrganisationSend;
import VPI.PDClasses.PDService;
import VPI.PDClasses.Users.PDUserItemsResponse;
import VPI.Synchroniser;
import VPI.VertecClasses.VertecActivities.ZUKActivities;
import VPI.VertecClasses.VertecOrganisations.JSONContact;
import VPI.VertecClasses.VertecOrganisations.JSONOrganisation;
import VPI.VertecClasses.VertecOrganisations.OrganisationList;
import VPI.VertecClasses.VertecProjects.ZUKProjects;
import VPI.VertecClasses.VertecService;
import VPI.VertecClasses.VertecTeam.EmployeeList;
import VPI.VertecClasses.VertecTeam.ZUKTeam;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.mockito.stubbing.Answer;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.mockito.Matchers.anyLong;
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

    private Importer importer;

    @Before
    public void setUp() throws IOException {
        MockitoAnnotations.initMocks(this);
        pipedrive = mock(PDService.class);
        vertec = mock(VertecService.class);


        when(pipedrive.getAllUsers()).thenReturn(getDummyUsersResponse());
        when(vertec.getTeamDetails()).thenReturn(getOldDummyTeamResponse()); // for initialisaton of importer

        when(vertec.getSalesTeam()).thenReturn(getDummyVertecTeamResponse().getBody().getEmployees());

        synchroniser = new Synchroniser(pipedrive, vertec);

        importer = new Importer(pipedrive, vertec);
    }

    @Test
    public void canGetVertecOrganisations() throws IOException {
        when(vertec.getAllZUKOrganisations()).thenReturn(getDummyVertecOrganisationsResponse());
       OrganisationContainer orgs = synchroniser.getVertecOrganisations();

        Organisation organisation = orgs.getByV(1910117L);

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
        assertEquals("2016-05-15 04:48:34", organisation.getModified());
        assertEquals("2008-07-07 12:52:49", organisation.getCreated());

        assertTrue(organisation.getFull_address().contains("Gray's Inn Road"));
        assertTrue(organisation.getStreet().contains("Gray's Inn Road"));

        assertEquals(0, orgs.orgsWithoutVID.size());

    }

    @Test
    public void canGetPipedtiveOrganisations() throws IOException {
        when(pipedrive.getAllOrganisations()).thenReturn(getDummyPipedriveOrganisationsResponse());
        when(pipedrive.getRelationships(anyLong())).thenAnswer(getPDRelationshipAnswer());

        OrganisationContainer orgs = synchroniser.getPipedriveOrganisations();

        Organisation testOrg = orgs.getByP(184L);

        System.out.println(testOrg);
        assertTrue(testOrg.getFull_address().contains("Schmelzhütterstraße 26, Dornbirn, 6850, Österreich"));
        assertEquals("justin.cowling@zuhlke.com", testOrg.getSupervisingEmail());
        assertEquals(21768524L, testOrg.getvParentOrganisation().longValue());
        assertEquals(668483L, testOrg.getVertecId().longValue());



        System.out.println(testOrg);
        assertEquals("News UK", testOrg.getName());
        assertEquals(null, testOrg.getVertecId());
        assertEquals(null, testOrg.getVertecId());
        assertEquals(21768524L, testOrg.getvParentOrganisation().longValue());


    }


    //=================================MOCKITO DUMMY RESPONSES==========================================================

    private ResponseEntity<PDUserItemsResponse> getDummyUsersResponse() throws IOException {
        ObjectMapper m = new ObjectMapper();
        PDUserItemsResponse u = m.readValue(new File("src/test/resources/Pipedrive users.json"), PDUserItemsResponse.class);
        return new ResponseEntity<>(u, HttpStatus.OK);

    }

    private ResponseEntity<EmployeeList> getDummyVertecTeamResponse() throws IOException {
        ObjectMapper m = new ObjectMapper();
        EmployeeList u = m.readValue(new File("src/test/resources/ZUKTeam.json"), EmployeeList.class);
        return new ResponseEntity<>(u, HttpStatus.OK);

    }

    private ResponseEntity<OrganisationList> getDummyVertecOrganisationsResponse() throws IOException {
        ObjectMapper m = new ObjectMapper();
        OrganisationList body = m.readValue(new File("src/test/resources/AllVertecOrganisations.json"), OrganisationList.class);
        return new ResponseEntity<>(body, HttpStatus.OK);
    }

    private ResponseEntity<PDOrganisationItemsResponse> getDummyPipedriveOrganisationsResponse() throws IOException {
        ObjectMapper m = new ObjectMapper();
        PDOrganisationItemsResponse body = m.readValue(new File("src/test/resources/AllPipedriveOrganisations.json"), PDOrganisationItemsResponse.class);
        return new ResponseEntity<>(body, HttpStatus.OK);
    }

    private ResponseEntity<ZUKProjects> getDummyProjectsResponse() throws IOException {
        ObjectMapper m = new ObjectMapper();
        ZUKProjects body = m.readValue(new File("src/test/resources/VRAPI projects.json"), ZUKProjects.class);
        return new ResponseEntity<>(body, HttpStatus.OK);
    }

    private ResponseEntity<ZUKActivities> getDummyActivitiesResponse() throws IOException {
        ObjectMapper m = new ObjectMapper();
        ZUKActivities body = m.readValue(new File("src/test/resources/VRAPI Activity Response.json"), ZUKActivities.class);
        return new ResponseEntity<>(body, HttpStatus.OK);
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

    private ResponseEntity<JSONContact> getDummyMissingContactResponse() {
        JSONContact contact = new JSONContact();
        contact.setObjid(666L);
        contact.setActive(true);
        return new ResponseEntity<>(contact, HttpStatus.OK);
    }

    private ResponseEntity<PDContactListReceived> getDummyPipedriveContactResponse() throws IOException {
        ObjectMapper m = new ObjectMapper();
        PDContactListReceived body = m.readValue(new File("src/test/resources/PipedriveProduction contacts.json"), PDContactListReceived.class);
        return new ResponseEntity<>(body, HttpStatus.OK);
    }

    private ResponseEntity<PDDealItemsResponse> getDummyPipedriveDealResponse() throws IOException {
        ObjectMapper m = new ObjectMapper();
        PDDealItemsResponse body = m.readValue(new File("src/test/resources/PipedriveProduction deals.json"), PDDealItemsResponse.class);
        return new ResponseEntity<>(body, HttpStatus.OK);
    }

    private ResponseEntity<ZUKTeam> getOldDummyTeamResponse() throws IOException {
        ObjectMapper m = new ObjectMapper();
        ZUKTeam u = m.readValue(new File("src/test/resources/Old/VRAPI Team.json"), ZUKTeam.class);
        return new ResponseEntity<>(u, HttpStatus.OK);

    }

    private Answer<List<PDRelationshipReceived>> getPDRelationshipAnswer() {
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
            relationshipSubject.setId(2L);
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

}

