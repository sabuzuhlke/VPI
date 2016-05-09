package InsightTests;

import VPI.*;
import VPI.InsightClasses.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(Application.class)
@WebIntegrationTest
public class InsightServiceTests {;

    private InsightService IS;

    @Before
    public void setUp() throws Exception {
        String server = "http://insight.zuehlke.com";
        MyCredentials creds = new MyCredentials();
        TestRestTemplate testRestTemplate = new TestRestTemplate();
        this.IS = new InsightService(
                testRestTemplate,
                server,
                creds.getUserName(),
                creds.getPass()
        );
    }

    @Test
    public void canGetOrganisationsFromInsightAPI() {
        ResponseEntity<VOrganisationItems> res = IS.getAllOrganisations();
        assertTrue(res.getStatusCode() == HttpStatus.OK);
        assertTrue(res.getBody() != null);
        assertTrue(res.getBody().getItems().get(0) != null);
        assertTrue(res.getBody().getItems().get(0).getName().equals("Bentley Systems Germany GmbH"));
    }

    @Test
    public void canGetSingleOrganisationFromInsightAPI() {
        Long id = 55L;
        ResponseEntity<VOrganisation> res = IS.getOrganisation(id);
        assertTrue(res.getStatusCode() == HttpStatus.OK);
        assertTrue(res.getBody() != null);
        assertTrue(res.getBody().getName().equals("Interactive Objects Software GmbH"));
    }

    @Test
    public void canGetContactsForOrganisation() {
        Long id = 55L; //check this when we can access the data
        ResponseEntity<VContact[]> res = IS.getContactsForOrganisation(id);
        System.out.println(res);
        assertTrue(res.getStatusCode() == HttpStatus.OK);
        assertTrue(res.getBody() != null);
        assertTrue(res.getBody().length == 3);
        assertTrue(res.getBody()[0].getId() == 240);
        assertTrue(res.getBody()[1].getId() == 241);
        //assertTrue(res.getBody().getItems().get(5/*change this*/).getName().equals("TheName"/*change this to match*/));
    }

//----------------------------------------------------------------PROJECT TESTS--------------

    @Test
    public void canGetProjectList() {
        ResponseEntity<VProjectList> res = IS.getProjectsForZUK();

        assertTrue(res.getStatusCode() == HttpStatus.OK);
        assertTrue(!res.getBody().getItems().isEmpty());
//        assertTrue(res.getBody().getItems().get(0).getName().equals("Parcel Force, Innovation Workshop"));
//        assertTrue(res.getBody().getItems().get(0).getOrganisation().getName().equals("Royal Mail Group"));
//
//        assertTrue(res.getBody().getItems().get(1).getName().equals("AltViz, ListSmart SAAS"));
//        assertTrue(res.getBody().getItems().get(1).getOrganisation().getName().equals("Altviz"));

    }

    @Test
    public void canGetOrganisationList() {
        List<Long> ids = new ArrayList<>();
        ids.add(20683L);
        ids.add(17977L);
        List<VOrganisation> list = IS.getOrganisationList(ids);

        assertTrue(!list.isEmpty());
        assertTrue(list.size() == 2);
        assertTrue(list.get(0).getName().equals("Royal Mail Group"));
        assertTrue(list.get(1).getName().equals("Altviz"));
    }

}
