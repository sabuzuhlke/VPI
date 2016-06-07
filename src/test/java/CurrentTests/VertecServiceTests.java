package CurrentTests;

import VPI.VertecClasses.VertecOrganisations.JSONOrganisation;
import VPI.VertecClasses.VertecProjects.JSONProject;
import VPI.VertecClasses.VertecProjects.ZUKProjects;
import VPI.VertecClasses.VertecService;
import VPI.VertecClasses.VertecOrganisations.ZUKOrganisations;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.http.ResponseEntity;


import java.io.StringReader;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by sabu on 29/04/2016.
 */
public class VertecServiceTests {

    private VertecService VS;

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

        try{

            res = m.readValue(response,ZUKOrganisations.class);
        }
        catch (Exception e){
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

        System.out.println(res.toPrettyString());
        assertTrue( ! res.getOrganisationList().isEmpty());
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

    @Test @Ignore
    public void canGetAllProjectsAndPhasesFromVertec() {

        ResponseEntity<ZUKProjects> res = VS.getZUKProjects();

        assertTrue(res.getBody().getProjects() != null);
        System.out.println(res.getBody().toString());

    }


    public String getJSONResultString() {
        return "{\n" +
                "  \"organisations\" : [ {\n" +
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
    public void canGetOrgById(){
        Long id = 709814L;

        JSONOrganisation org = VS.getOrganisation(id).getBody();

        assertTrue(org.getName().equals("Deutsche Telekom"));

    }

    @Test
    public void canCetProjectByCode(){
        String code = "c15823";

        JSONProject p = VS.getProject(code).getBody();

        assertEquals(p.getTitle(), "HSBC HSS off-line demo");

    }

    @Test
    public void canGetProjectById(){
        Long id = 12065530L;

        JSONProject p = VS.getProject(id).getBody();

        assertEquals(p.getTitle(), "HSBC HSS off-line demo");
    }




}
