package VPI;

import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import java.net.URI;

/**
 * Created by sabu on 07/04/2016.
 */
public class PDService {

    private RestTemplate restTemplate;
    private String server;
    private String apiKey;

    public PDService(RestTemplate restTemplate, String server, String apiKey) {
        this.restTemplate = restTemplate;
        this.apiKey = apiKey;
        this.server = server;
    }
//----------------------------------------------------------------------------------POST
    public ResponseEntity<PDOrganisationResponse> postOrganisation(String companyName, Integer visibleTo) {
        RequestEntity<PDOrganisation> req;
        ResponseEntity<PDOrganisationResponse> res = null;

        try {
            PDOrganisation post = new PDOrganisation(companyName, visibleTo);
            String uri = server + "organizations" + apiKey;

            req = new RequestEntity<>(post, HttpMethod.POST, new URI(uri));
            res = restTemplate.exchange(req, PDOrganisationResponse.class);

        } catch (Exception e) {
            System.out.println(e.toString());
        }
        return res;
    }

    public ResponseEntity<PDOrganisationResponse> postOrganisationWithAddress(String companyName, String address, Integer visibleTo) {

        RequestEntity<PDOrganisation> req;
        ResponseEntity<PDOrganisationResponse> res = null;

        try {
            PDOrganisation post = new PDOrganisation(companyName,address, visibleTo);
            String uri = server + "organizations" + apiKey;

            req = new RequestEntity<>(post, HttpMethod.POST, new URI(uri));
            res = restTemplate.exchange(req, PDOrganisationResponse.class);

        } catch (Exception e) {
            System.out.println(e.toString());
        }
        return res;
    }

//----------------------------------------------------------------------------------PUT
    public ResponseEntity<PDOrganisationResponse> updateOrganisationAddress(Long id, String address) {
        PDOrganisationResponse org;
        PDOrganisationResponse resOrganisation = new PDOrganisationResponse();
        RequestEntity<PDOrganisation> req;
        ResponseEntity<PDOrganisationResponse> res = null;


        try {
            //GET organisation From Pipedrive
            org = this.getOrganisation(id).getBody();

            //Update with new Address
            PDOrganisation newOrg = new PDOrganisation(
                    id,
                    org.getData().getName(),
                    org.getData().getVisible_to(),
                    address,
                    true,
                    org.getData().getCompany_id(),
                    org.getData().getOwner_id()
            );

            //PUT Org with new address to PipeDrive
            String uri = server + "organizations/" + id + apiKey;

            req = new RequestEntity<>(newOrg, HttpMethod.PUT, new URI(uri));

            res = restTemplate.exchange(req, PDOrganisationResponse.class);

        } catch (Exception e) {
            System.out.println(e.toString());
        }
        return res;
    }
//----------------------------------------------------------------------------------GET
    public ResponseEntity<PDOrganisationResponse> getOrganisation(Long id) {
        RequestEntity<PDOrganisation> req;
        ResponseEntity<PDOrganisationResponse> res = null;

        try {
            String uri = server + "organizations/" + id + apiKey;

            req = new RequestEntity<>(HttpMethod.GET, new URI(uri));
            res = restTemplate.exchange(req, PDOrganisationResponse.class);

        } catch (Exception e) {
            System.out.println(e.toString());
        }
        return res;
    }
    public ResponseEntity<PDOrganisationItemsResponse> getAllOrganisations(){
        RequestEntity<String> req;
        ResponseEntity<PDOrganisationItemsResponse> res = null;
        String uri = server + "organizations/" + apiKey;
        try{
            req = new RequestEntity<String>(HttpMethod.GET, new URI(uri));
            res = restTemplate.exchange(req,PDOrganisationItemsResponse.class);

        }
        catch(Exception e){
            System.out.println("Exception when getting all organisations from PipeDrive: " + e);
        }
        return res;
    }
//----------------------------------------------------------------------------------DELETE
    public ResponseEntity<PDDeleteResponse> deleteOrganisation(Long id){
        RequestEntity<PDOrganisation> req;
        ResponseEntity<PDDeleteResponse> res = null;

        try {
            String uri = server + "organizations/" + id + apiKey;
            //restTemplate.deleteOrganisation(server + "organizations/" + id + apiKey);

            req = new RequestEntity<>(HttpMethod.DELETE, new URI(uri));
            res = restTemplate.exchange(req, PDDeleteResponse.class);

        } catch (Exception e) {
            System.out.println("DELETE Exception: " + e.toString());
        }
        return res;
    }

}
