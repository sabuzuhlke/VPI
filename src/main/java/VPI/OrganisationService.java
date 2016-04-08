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
        RequestEntity<Organisation> req;
        ResponseEntity<PDOrganisationResponse> res;

        try {
            Organisation post = new Organisation(companyName, visibleTo);
            String uri = server + "organizations" + apiKey;

            req = new RequestEntity<>(post, HttpMethod.POST, new URI(uri));
            res = restTemplate.exchange(req, PDOrganisationResponse.class);
            org = res.getBody();


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
        RequestEntity<Organisation> req;
        ResponseEntity<PDOrganisationResponse> res;


        try {
            //GET organisation From Pipedrive
            org = (PDOrganisationResponse) this.get(id);

            //Update with new Address
            Organisation newOrg = new Organisation(id, org.getData().getName(),org.getData().getVisible_to()
                                                , address, true, org.getData().getCompany_id()
                                                , org.getData().getOwner_id()
            );

            //PUT Org with new address to PipeDrive
            String uri = server + "organizations/" + id + apiKey;

            req = new RequestEntity<>(newOrg, HttpMethod.PUT, new URI(uri));

            res = restTemplate.exchange(req, PDOrganisationResponse.class);
            resOrganisation = res.getBody();

        } catch (Exception e) {
            System.out.println(e.toString());
        }
        return resOrganisation;
    }
//----------------------------------------------------------------------------------GET
    public PDResponse get(Long id) {
        PDOrganisationResponse org = null;
        RequestEntity<Organisation> req;
        ResponseEntity<PDOrganisationResponse> res;

        try {
            String uri = server + "organizations/" + id + apiKey;

            req = new RequestEntity<>(HttpMethod.GET, new URI(uri));
            res = restTemplate.exchange(req, PDOrganisationResponse.class);

            org = res.getBody();

        } catch (Exception e) {
            System.out.println(e.toString());
        }
        return org;
    }
//----------------------------------------------------------------------------------DELETE
    public PDDeleteResponse delete(Long id){
        RequestEntity<Organisation> req;
        ResponseEntity<PDDeleteResponse> res;
        PDDeleteResponse delRes = null;

        try {
            String uri = server + "organizations/" + id + apiKey;
            //restTemplate.delete(server + "organizations/" + id + apiKey);

            req = new RequestEntity<>(HttpMethod.DELETE, new URI(uri));
            res = restTemplate.exchange(req, PDDeleteResponse.class);

            delRes = res.getBody();

        } catch (Exception e) {
            System.out.println("DELETE Exception: " + e.toString());
        }
        return delRes;
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
