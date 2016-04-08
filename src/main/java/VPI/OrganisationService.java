package VPI;

import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import java.net.URI;

/**
 * Created by sabu on 07/04/2016.
 */
public class OrganisationService {

    private RestTemplate restTemplate;
    private String server;
    private String apiKey;

    public OrganisationService(RestTemplate restTemplate, String server, String apiKey) {
        this.restTemplate = restTemplate;
        this.apiKey = apiKey;
        this.server = server;
    }
//----------------------------------------------------------------------------------POST
    public PDResponse post(String companyName, Integer visibleTo) {
        PDOrganisationResponse org = null;
        try {
            Organisation post = new Organisation(companyName, visibleTo);
            String uri = server + "organizations" + apiKey;
            org = restTemplate.postForObject(uri, post, PDOrganisationResponse.class);
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        return org;
    }

    public PDResponse post(String companyName, String address, Integer visibleTo) {
        PDOrganisationResponse org = null;
        try {
            Organisation post = new Organisation(companyName,address, visibleTo);
            String uri = server + "organizations" + apiKey;
            org = restTemplate.postForObject(uri, post, PDOrganisationResponse.class);
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        return org;
    }

//----------------------------------------------------------------------------------PUT
    public PDOrganisationResponse updateAddress(Long id, String address) {
        PDOrganisationResponse org;
        PDOrganisationResponse resOrganisation = new PDOrganisationResponse();


        try {
            //GET organisation From Pipedrive
            org = (PDOrganisationResponse) this.get(id);
            //Update with new Address
            System.out.println("org: " + org.toString());
            Organisation newOrg = new Organisation(id, org.getData().getName(),org.getData().getVisible_to()
                                                , address, true, org.getData().getCompany_id()
                                                , org.getData().getOwner_id());
            System.out.println(newOrg.toString());

            //PUT Org with new address to PipeDrive
            String uri = server + "organizations/" + id + apiKey;

            RequestEntity<Organisation> req = new RequestEntity<>(newOrg, HttpMethod.PUT, new URI(uri));

            System.out.println("Request: " + req.toString());

            ResponseEntity<PDOrganisationResponse> res = restTemplate.exchange(req, PDOrganisationResponse.class);
            System.out.println("PUT response code: " + res.getStatusCode().toString());
            System.out.println("PUT response: " + res.toString());
            resOrganisation = res.getBody();

        } catch (Exception e) {
            System.out.println(e.toString());
        }
        return resOrganisation;
    }
//----------------------------------------------------------------------------------GET
    public PDResponse get(Long id) {
        PDOrganisationResponse org = null;
        try {
            String uri = server + "organizations/" + id + apiKey;
            org = restTemplate.getForObject(uri, PDOrganisationResponse.class);
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        return org;
    }
//----------------------------------------------------------------------------------DELETE
    public void delete(Long id){
        try {
            restTemplate.delete(server + "organizations/" + id + apiKey);
        } catch (Exception e) {
            System.out.println("DELETE Exception: " + e.toString());
        }
    }


    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }
}
