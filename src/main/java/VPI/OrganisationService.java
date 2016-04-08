package VPI;

import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import java.net.URI;

import java.util.HashMap;
import java.util.Map;

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

    public PDResponse post(String companyName, Integer visibleTo) {
        Organisation org = null;
        try {
            OrganisationPost post = new OrganisationPost(companyName, visibleTo);
            String uri = server + "organizations" + apiKey;
            org = restTemplate.postForObject(uri, post, Organisation.class);
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        return org;
    }
    public PDResponse post(String companyName, String address, Integer visibleTo) {
        Organisation org = null;
        try {
            OrganisationPost post = new OrganisationPost(companyName,address, visibleTo);
            String uri = server + "organizations" + apiKey;
            org = restTemplate.postForObject(uri, post, Organisation.class);
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        return org;
    }

    public PDResponse get(Long id) {
        Organisation org = null;
        try {
            String uri = server + "organizations/" + id + apiKey;
            org = restTemplate.getForObject(uri, Organisation.class);
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        return org;
    }

    public void delete(Long id){
        try {
            restTemplate.delete(server + "organizations/" + id + apiKey);
        } catch (Exception e) {
            System.out.println("DELETE Exception: " + e.toString());
        }
    }

    public Organisation updateAddress(Long id, String address) {
        Organisation org;
        Organisation resOrganisation = new Organisation();


        try {
            //GET organisation From Pipedrive
            org = (Organisation) this.get(id);
            //Update with new Address
            OrganisationPost newOrg = new OrganisationPost(org.getData().getName(), address);
            System.out.println(newOrg.toString());

            //PUT Org with new address to PipeDrive
            String uri = server + "organizations/" + id + apiKey;

            RequestEntity<OrganisationPost> req = new RequestEntity<>(newOrg, HttpMethod.PUT, new URI(uri));

            ResponseEntity<Organisation> res = restTemplate.exchange(req,Organisation.class);

            resOrganisation = res.getBody();

        } catch (Exception e) {
            System.out.println(e.toString());
        }
        return resOrganisation;
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
