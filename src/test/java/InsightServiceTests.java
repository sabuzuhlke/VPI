/**
 * Created by gebo on 12/04/2016.
 */
import VPI.*;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.OutputCapture;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(Application.class)
@WebIntegrationTest
public class InsightServiceTests {

    @Rule
    public OutputCapture capture = new OutputCapture();

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
        ResponseEntity<ICompanyItems> res = IS.getAllOrganisations();
        assertTrue(res.getStatusCode() == HttpStatus.OK);
        assertTrue(res.getBody() != null);
        assertTrue(res.getBody().getItems().get(0) != null);
        assertTrue(res.getBody().getItems().get(0).getName().equals("Bentley Systems Germany GmbH"));
    }

    @Test
    public void canGetSingleOrganisationFromInsightAPI() {
        Integer id = 55;
        ResponseEntity<ICompany> res = IS.getOrganisation(id);
        assertTrue(res.getStatusCode() == HttpStatus.OK);
        assertTrue(res.getBody() != null);
        assertTrue(res.getBody().getName().equals("Interactive Objects Software GmbH"));
    }

}
