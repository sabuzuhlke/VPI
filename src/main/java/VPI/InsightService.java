package VPI;

import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.net.Authenticator;
import java.net.URI;

public class InsightService {

    private RestTemplate restTemplate;
    private String server;

    public InsightService(RestTemplate restTemplate, String server, String userName, String pwd) {
        this.restTemplate = restTemplate;
        this.server = server;
        //Authenticate with NTLM as Insight API uses that
        NTLMAuthenticator authenticator = new NTLMAuthenticator(userName,pwd);
        Authenticator.setDefault(authenticator);
    }

    public ResponseEntity<VOrganisationItems> getAllOrganisations(){

        String apiPath = "/api/v1/customers";
        RequestEntity<String> req;
        ResponseEntity<VOrganisationItems> res = null;

        try {

            req = new RequestEntity<>(HttpMethod.GET, new URI(server + apiPath));

            res = restTemplate.exchange(req, VOrganisationItems.class);

        }
        catch (Exception e){
            System.out.println("Could not GET all organisations from insight: " + e.toString());
        }
        return res;
    }

    public ResponseEntity<VOrganisation> getOrganisation(Integer Id){

        String apiPath = "/api/v1/customers/" + Id.toString();
        RequestEntity<String> req;
        ResponseEntity<VOrganisation> res = null;

        try {

            req = new RequestEntity<>(HttpMethod.GET,new URI(server + apiPath));

            res = restTemplate.exchange(req, VOrganisation.class);

        }
        catch (Exception e){
            System.out.println("Could not GET organisation " + Id + " from insight: " + e.toString());
        }
        return res;

    }
}
