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


import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;



/**
 * Created by sabu on 06/04/2016.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(Application.class)
@WebIntegrationTest
public class PipedriveOrganisationServiceTests {

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
        OrganisationService OS = new OrganisationService(restTemplate, server, apiKey);
        String companyName = "TestPostCompany";
        Integer visibility = 3;
        PDOrganisationResponse postResponse = (PDOrganisationResponse) OS.post(companyName, visibility);
        assertTrue(postResponse.getSuccess());

        //get same org using returned id, check name is equivalent to supplied name
        Long id = postResponse.getData().getId();
        PDOrganisationResponse o = (PDOrganisationResponse) OS.get(id);
        assertTrue(o.getData().getName().equals(companyName));

        //delete posted organisation
        idsDeleted.add(id);
        OS.delete(id);
    }

    @Test
    public void canDeleteOrganisation(){

        PDDeleteResponse delRes;
        OrganisationService OS = new OrganisationService(restTemplate, server, apiKey);
        String companyName = "TestDeleteCompany";
        Integer visibility = 3;
        PDOrganisationResponse postResponse = (PDOrganisationResponse) OS.post(companyName, visibility);
        assertTrue(postResponse.getSuccess());

        delRes = OS.delete(postResponse.getData().getId());
        assertTrue(delRes.getSuccess());
        assertEquals(delRes.getData().getId(), postResponse.getData().getId());

    }

    @Test
    public void canUpdateOrgAddress() {
        OrganisationService OS = new OrganisationService(restTemplate, server, apiKey);
        PDOrganisationResponse org;
        String companyName = "TestAddressCompany";
        Integer visibility = 3;

        //POST
        PDOrganisationResponse postResponse = (PDOrganisationResponse) OS.post(companyName, visibility);
        assertTrue(postResponse.getSuccess());

        Long id = postResponse.getData().getId();
        String newAddress = "test address";
        //PUT
        org = OS.updateAddress(id, newAddress);

        assertTrue(org.getData().getAddress().equals(newAddress));

        //DELETE posted organisation
        idsDeleted.add(id);
        OS.delete(id);

    }

    @Test
    public void deletedAllOrganisations(){
        OrganisationService OS = new OrganisationService(restTemplate, server, apiKey);
        PDOrganisationResponse org;
        for(Integer i = 0; i < idsDeleted.size(); i++){
            org = (PDOrganisationResponse) OS.get(idsDeleted.get(i));
            System.out.println("Is Org " + idsDeleted.get(i) + " deleted?" + org.getData().getActive_flag());
            assertTrue(!org.getData().getActive_flag());
        }
    }

    @Test
    public void canGetAllOrganisations(){
        OrganisationService OS = new OrganisationService(restTemplate, server, apiKey);
        ResponseEntity<PDOrganisationItemsResponse> res;
        PDOrganisationItemsResponse organisations;
        try{
            res = OS.getAll();
            assertEquals(res.getStatusCode(), HttpStatus.OK);
            organisations = res.getBody();
            //The following asserts that the more_items_in_collection field of the response is false -- Meaning that there are no more organisations to return
            assertTrue(!organisations.getAdditional_data().getPagination().getMore_items_in_collection());
            assertTrue(organisations.getItems().get(1) != null);
            assertEquals("NASA",organisations.getItems().get(1).getName());


        }
        catch(Exception e){
            System.out.println("canGetAllOrganisations encountered exception: " + e.toString());
        }

    }
}
