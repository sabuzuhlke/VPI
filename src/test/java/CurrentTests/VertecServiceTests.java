package CurrentTests;

import VPI.VertecClasses.VertecActivities.ActivitiesForAddressEntry;
import VPI.VertecClasses.VertecOrganisations.JSONContact;
import VPI.VertecClasses.VertecOrganisations.JSONOrganisation;
import VPI.VertecClasses.VertecOrganisations.Organisation;
import VPI.VertecClasses.VertecProjects.JSONProject;
import VPI.VertecClasses.VertecProjects.ZUKProjects;
import VPI.VertecClasses.VertecService;
import VPI.VertecClasses.VertecOrganisations.ZUKOrganisations;
import VPI.VertecClasses.VertecTeam.Employee;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.junit.Assert.*;


public class VertecServiceTests {

    private VertecService VS;
    private  Long TESTVertecOrganisation1 = 28055040L;

    @Before
    public void setUp() {
        this.VS = new VertecService("localhost:9999");
    }

    @Test
    public void canAccessVertecService() {
        String success = VS.ping();

        assertTrue(success.equals("Success!"));
    }


    @Test
    public void canCorrectlyUnmarshallServerResponseToPOJO() {

        String response = getJSONResultString();

        StringReader reader = new StringReader(response);

        ObjectMapper m = new ObjectMapper();

        ZUKOrganisations res = new ZUKOrganisations();

        try {

            res = m.readValue(response, ZUKOrganisations.class);
        } catch (Exception e) {
            System.out.println("Error in unmarshalling ZUK response: " + e);
        }

        assertTrue(res.getDanglingContacts().size() == 2);
        assertTrue(res.getDanglingContacts().get(0).getFirstName().equals("The King"));
        assertTrue(res.getDanglingContacts().get(0).getSurname().equals("Burger"));
        assertTrue(res.getDanglingContacts().get(0).getPhone().equals("999"));
        assertTrue(res.getDanglingContacts().get(0).getMobile().equals("07999"));
        assertTrue(res.getDanglingContacts().get(0).getEmail().equals("whopper@star.com"));
        assertTrue(res.getDanglingContacts().get(0).getModified().equals("12:12:2012"));
        assertTrue(res.getDanglingContacts().get(0).getObjid() == 3L);
        assertTrue(res.getDanglingContacts().get(1).getSurname().equals("Waga"));
        assertTrue(res.getDanglingContacts().get(1).getFirstName().equals("Mama"));

        System.out.println(res.toPrettyJSON());
        assertTrue(!res.getOrganisationList().isEmpty());
        assertTrue(res.getOrganisationList().size() == 2);
        assertTrue(res.getOrganisationList().get(1).getObjid() == 2L);
        assertTrue(res.getOrganisationList().get(1).getContacts().size() == 2);
        assertTrue(res.getOrganisationList().get(1).getContacts().get(0).getFirstName().equals("Ronald"));
        assertTrue(res.getOrganisationList().get(1).getContacts().get(0).getSurname().equals("McDonald"));
        assertTrue(res.getOrganisationList().get(1).getContacts().get(0).getPhone().equals("999"));
        assertTrue(res.getOrganisationList().get(1).getContacts().get(0).getMobile().equals("07999"));
        assertTrue(res.getOrganisationList().get(1).getContacts().get(0).getEmail().equals("childrenwelcome@me.com"));
        assertTrue(res.getOrganisationList().get(1).getContacts().get(0).getObjid() == 1L);

        assertTrue(res.getOrganisationList().get(1).getContacts().get(1).getFirstName().equals("The Colonel"));
        assertTrue(res.getOrganisationList().get(1).getContacts().get(1).getObjid() == 2L);

        assertTrue(res.getOrganisationList().get(0).getObjid() == 1L);
        assertTrue(res.getOrganisationList().get(0).getContacts().isEmpty());


    }

    @Test
    @Ignore
    public void canGetAllProjectsAndPhasesFromVertec() {

        ResponseEntity<ZUKProjects> res = VS.getZUKProjects();

        assertTrue(res.getBody().getProjects() != null);
        System.out.println(res.getBody().toPrettyJSON());

    }


    public String getJSONResultString() {
        return "{\n" +
                "  \"organisationState\" : [ {\n" +
                "    \"name\" : \"Association of good Fast Food Chains\",\n" +
                "    \"streetAddress\" : \"666 Highway To Hell\",\n" +
                "    \"additonalAdress\" : \" no!\",\n" +
                "    \"zip\" : \"666\",\n" +
                "    \"city\" : \"Sin City\",\n" +
                "    \"country\" : \"Murica!\",\n" +
                "    \"owner\" : \"1\",\n" +
                "    \"objid\" : 1,\n" +
                "    \"modified\" : \"23:23:1876\",\n" +
                "    \"contacts\" : [ ]\n" +
                "  }, {\n" +
                "    \"name\" : \"The healthy options\",\n" +
                "    \"streetAddress\" : \"667 Stairway To Heaven\",\n" +
                "    \"additonalAdress\" : \" no!\",\n" +
                "    \"zip\" : \"777\",\n" +
                "    \"city\" : \"Ouahog\",\n" +
                "    \"country\" : \"Murica!\",\n" +
                "    \"owner\" : \"1\",\n" +
                "    \"objid\" : 2,\n" +
                "    \"modified\" : \"23:23:1876\",\n" +
                "    \"contacts\" : [ {\n" +
                "      \"firstName\" : \"Ronald\",\n" +
                "      \"surname\" : \"McDonald\",\n" +
                "      \"email\" : \"childrenwelcome@me.com\",\n" +
                "      \"phone\" : \"999\",\n" +
                "      \"mobile\" : \"07999\",\n" +
                "      \"owner\" : \"1\",\n" +
                "      \"modified\" : \"12:12:2012\",\n" +
                "      \"objid\" : 1\n" +
                "    }, {\n" +
                "      \"firstName\" : \"The Colonel\",\n" +
                "      \"surname\" : \"Sanders\",\n" +
                "      \"email\" : \"chicken@chicken.com\",\n" +
                "      \"phone\" : \"999\",\n" +
                "      \"mobile\" : \"07999\",\n" +
                "      \"owner\" : \"1\",\n" +
                "      \"modified\" : \"12:12:2012\",\n" +
                "      \"objid\" : 2\n" +
                "    } ]\n" +
                "  } ],\n" +
                "  \"danglingContacts\" : [ {\n" +
                "    \"firstName\" : \"The King\",\n" +
                "    \"surname\" : \"Burger\",\n" +
                "    \"email\" : \"whopper@star.com\",\n" +
                "    \"phone\" : \"999\",\n" +
                "    \"mobile\" : \"07999\",\n" +
                "    \"owner\" : \"1\",\n" +
                "    \"modified\" : \"12:12:2012\",\n" +
                "    \"objid\" : 3\n" +
                "  }, {\n" +
                "    \"firstName\" : \"Mama\",\n" +
                "    \"surname\" : \"Waga\",\n" +
                "    \"email\" : \"bad@service.com\",\n" +
                "    \"phone\" : \"999\",\n" +
                "    \"mobile\" : \"07999\",\n" +
                "    \"owner\" : \"1\",\n" +
                "    \"modified\" : \"12:12:2012\",\n" +
                "    \"objid\" : 4\n" +
                "  } ]\n" +
                "}\n";
    }

    @Test
    public void canGetOrgById() {
        Long id = 709814L;

        JSONOrganisation org = VS.getOrganisation(id).getBody();

        assertTrue(org.getName().equals("Deutsche Telekom"));

    }

    @Test
    public void canCetProjectByCode() {
        String code = "c15823";

        JSONProject p = VS.getProject(code).getBody();

        assertEquals(p.getTitle(), "HSBC HSS off-line demo");

    }

    @Test
    public void canGetProjectById() {
        Long id = 12065530L;

        JSONProject p = VS.getProject(id).getBody();

        assertEquals(p.getTitle(), "HSBC HSS off-line demo");
    }

    @Test
    public void cangetAddressEntryById() {
        JSONContact contact = VS.getContact(20027532L).getBody();

        System.out.println(contact);
        assertTrue("inactive contact", contact.getActive());

        JSONOrganisation org = VS.getOrganisation(3059404L).getBody();

        assertTrue("inactive contact in org ", org.getContacts().get(0).getActive()); //Douglas I. Lewis

    }

    @Test
    public void canGetOrganisationsById() {//TODO make pass
        List<Long> orgids = new ArrayList<>();
        orgids.add(28055040L);
        orgids.add(28055047L);
        orgids.add(709814L);

        List<VPI.VertecClasses.VertecOrganisations.Organisation> orgs = VS.getOrganisationList(orgids).getBody().getOrganisations();

        assertEquals(orgs.size(), 3);

        VPI.VertecClasses.VertecOrganisations.Organisation org = orgs.get(0);
        System.out.println(org.toString());

        //Following assertions only hold for this specific organisation
        assertTrue(org.getOwnedOnVertecBy().equals("Sales Team"));
        assertTrue(org.getName().equals("Deutsche Telekom"));
        assertTrue(org.getStreet().equals("Hatfield Business Park"));
        assertTrue(org.getCity().equals("Hatfield"));
        assertTrue(org.getCountry().equals("United Kingdom"));
        assertTrue(org.getZip().equals("AL10 9BW"));

        assertEquals(5295L, org.getOwnerId().longValue());
    }

    @Test
    public void canGetActivitiesForOrganisation() {
        Long id = 711840L;//an org with adressAktivitaeten

        ActivitiesForAddressEntry aFO = VS.getActivitiesForAddressEntry(id).getBody();
        List<VPI.VertecClasses.VertecActivities.Activity> activities = aFO.getActivities();

        //Following tests only work for given organisation
        assertTrue(6 <= activities.size());

        VPI.VertecClasses.VertecActivities.Activity activity = activities.get(5);

        assertEquals(711840L, aFO.getId().longValue());

        assertEquals(1106982L, activity.getVertecId().longValue());
        assertEquals(711840L, activity.getVertecOrganisationLink().longValue());
        assertEquals(5726, activity.getVertecAssignee().longValue());

        assertTrue(activity.getvType().equals("Aufgabe / Task"));
        assertTrue(activity.getSubject().equals("Info/Memo"));
        assertTrue(activity.getText().equals("Call to Claudia, Valerio's PA.  She has received PR from the UK and processing.  WIll let Amanda know when ready so liase with Amanda."));
        assertTrue(activity.getDueDate().equals("2003-11-18"));
        assertTrue(activity.getDoneDate().equals("2003-11-18"));

    }


    @Test
    public void canGetZUKTeam() {
        List<Employee> employees = VS.getSalesTeam();

        assertTrue("Tim not in team, that can't be!", employees.stream()
                .map(Employee::getEmail)
                .collect(toList())
                .contains("tim.cianchi@zuhlke.com"));
    }

    @Test
    public void canGetModifierOforg(){
        Long id = 28055040L; //TESTvertecOrg1

        Organisation org = VS.getOrganisationCommonRep(id).getBody();
        System.out.println(org.getModifier());
        assertNotNull(org.getModifier());

    }

//    @Test
//    public void canCreateOrganisation(){
//        VPI.Entities.Organisation org = new VPI.Entities.Organisation();
//        org.setName("VertecService creation test");
//        org.se
//    }

    @Test
    public void canUpdateOrganisation(){
        VPI.Entities.Organisation org = new VPI.Entities.Organisation();
        org.setName("VertecService update Test Org");
        org.setVertecId(28055040L); // Organisation on test instance of Vertec
        org.setFullAddress("f, u , ll, add, ress, 2233");
        org.setvParentOrganisation(28055326L); // another test organisation
        org.setActive(true);

        assertTrue(VS.updateOrganisation(org.getVertecId(), org.toVertecRep(5295L)));
    }

    @Test
    public void canCreateOrganisation() {
        VPI.Entities.Organisation org = new VPI.Entities.Organisation();
        org.setName("VertecService create test && special characters present");
        org.setFullAddress("f&, u , ll, add, ress, 2233");
        org.setvParentOrganisation(28055326L); // another test organisation
        org.setActive(true);

        ResponseEntity<Long> res = VS.createOrganisation(org.toVertecRep(5295L));
        assertEquals(HttpStatus.CREATED, res.getStatusCode());

        System.out.println( VS.getOrganisationCommonRep(res.getBody()).getBody().toString());


    }

    @Test
    public void canDeleteAndActivateOrganisation(){
        VS.activateOrganisation(TESTVertecOrganisation1);

        Organisation org = VS.getOrganisationCommonRep(TESTVertecOrganisation1).getBody();
        assertTrue(org.getActive());

        VS.deleteOrganisation(TESTVertecOrganisation1);
        org = VS.getOrganisationCommonRep(TESTVertecOrganisation1).getBody();
        assertFalse(org.getActive());


        VS.activateOrganisation(TESTVertecOrganisation1);
    }


}
