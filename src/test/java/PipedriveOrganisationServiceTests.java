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

    private PDService PS;
    private ArrayList<Long> idsDeleted;


    @Before
    public void setUp() throws Exception {
        String server = "https://api.pipedrive.com/v1/";
        String apiKey = "?api_token=eefa902bdca498a342552b837663f38b566bce5a";
        TestRestTemplate testRestTemplate = new TestRestTemplate();
        this.PS = new PDService(testRestTemplate, server, apiKey);
        this.idsDeleted = new ArrayList<>();
    }

    @Test
    public void canPostOrganisation() {
        //Post org, check response says success
        String companyName = "TestPostCompany";
        Integer visibility = 3;
        ResponseEntity<PDOrganisationResponse> postResponse = PS.postOrganisation(companyName, visibility);
        assertTrue(postResponse.getBody().getSuccess());

        //get same org using returned id, check name is equivalent to supplied name
        Long id = postResponse.getBody().getData().getId();
        ResponseEntity<PDOrganisationResponse> o = PS.get(id);
        assertTrue(o.getBody().getData().getName().equals(companyName));

        //delete posted organisation
        idsDeleted.add(id);
        PS.delete(id);
    }

    @Test
    public void canDeleteOrganisation(){

        ResponseEntity<PDDeleteResponse> delRes;
        String companyName = "TestDeleteCompany";
        Integer visibility = 3;
        ResponseEntity<PDOrganisationResponse> postResponse = PS.postOrganisation(companyName, visibility);
        assertTrue(postResponse.getBody().getSuccess());

        delRes = PS.delete(postResponse.getBody().getData().getId());
        assertTrue(delRes.getBody().getSuccess());
        assertEquals(delRes.getBody().getData().getId(), postResponse.getBody().getData().getId());

    }

    @Test
    public void canUpdateOrgAddress() {
        ResponseEntity<PDOrganisationResponse> org;
        String companyName = "TestAddressCompany";
        Integer visibility = 3;

        //POST
        ResponseEntity<PDOrganisationResponse> postResponse = PS.postOrganisation(companyName, visibility);
        assertTrue(postResponse.getBody().getSuccess());

        Long id = postResponse.getBody().getData().getId();
        String newAddress = "test address";
        //PUT
        org = PS.updateAddress(id, newAddress);

        assertTrue(org.getBody().getData().getAddress().equals(newAddress));

        //DELETE posted organisation
        idsDeleted.add(id);
        PS.delete(id);

    }

    @Test
    public void deletedAllOrganisations(){
        ResponseEntity<PDOrganisationResponse> org;
        for(Integer i = 0; i < idsDeleted.size(); i++){
            org = PS.get(idsDeleted.get(i));
            System.out.println("Is Org " + idsDeleted.get(i) + " deleted?" + org.getBody().getData().getActive_flag());
            assertTrue(!org.getBody().getData().getActive_flag());
        }
    }

    @Test
    public void canGetAllOrganisations(){
        ResponseEntity<PDOrganisationItemsResponse> res;
        PDOrganisationItemsResponse organisations;
        res = PS.getAll();
        assertEquals(res.getStatusCode(), HttpStatus.OK);
        organisations = res.getBody();
        //The following asserts that the more_items_in_collection field of the response is false -- Meaning that there are no more organisations to return
        assertTrue(!organisations.getAdditional_data().getPagination().getMore_items_in_collection());
        assertTrue(organisations.getData() != null);
        assertTrue(organisations.getData().get(1) != null);
    }
}