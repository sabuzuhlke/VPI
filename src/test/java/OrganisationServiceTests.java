import VPI.Application;
import VPI.Organisation;
import VPI.OrganisationService;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.OutputCapture;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;



/**
 * Created by sabu on 06/04/2016.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(Application.class)
@WebIntegrationTest
public class OrganisationServiceTests {

    @Rule
    public OutputCapture capture = new OutputCapture();

    private String server;
    private String apiKey;
    private TestRestTemplate restTemplate;
    private ArrayList<Long> idsDeleted;


    @Before
    public void setUp() throws Exception {
        this.server = "https://api.pipedrive.com/v1/";
        this.apiKey = "?api_token=eefa902bdca498a342552b837663f38b566bce5a";
        restTemplate = new TestRestTemplate();
        this.idsDeleted = new ArrayList<>();
    }

    @Test
    public void canPostOrganisation() {
        //Post org, check response says success
        OrganisationService OS = new OrganisationService((RestTemplate) restTemplate, server, apiKey);
        String companyName = "TestPostCompany";
        Integer visibility = 3;
        Organisation postResponse = (Organisation) OS.post(companyName, visibility);
        assertTrue(postResponse.getSuccess());

        //get same org using returned id, check name is equivalent to supplied name
        Long id = postResponse.getData().getId();
        Organisation o = (Organisation) OS.get(id);
        assertTrue(o.getData().getName().equals(companyName));

        //delete posted organisation
        idsDeleted.add(id);
        OS.delete(id);

        //get and check active flag set to false
        o = (Organisation) OS.get(id);
        assertTrue(!o.getData().getActive_flag());
    }

    @Test
    public void canUpdateOrgAddress() {
        OrganisationService OS = new OrganisationService(restTemplate, server, apiKey);
        Organisation org;
        String companyName = "TestAddressCompany";
        Integer visibility = 3;

        //POST
        Organisation postResponse = (Organisation) OS.post(companyName, visibility);

        Long id = postResponse.getData().getId();
        String newAddress = "test address";
        //PUT
        org = OS.updateAddress(id, newAddress);

        System.out.println("TEST print: " + org.toString());
        assertTrue(org.getData().getAddress().equals(newAddress));

        //DELETE posted organisation
        idsDeleted.add(id);
        OS.delete(id);

        //GET and check active flag set to false
        org = (Organisation) OS.get(id);
        assertTrue(!org.getData().getActive_flag());
        System.out.println("There you go");

    }
}
