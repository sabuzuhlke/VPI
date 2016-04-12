package VPI;

import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.net.Authenticator;
import java.net.URI;
import java.util.List;


/**
 * Created by gebo on 12/04/2016.
 */
public class InsightService {

    private RestTemplate restTemplate;
    private String server;
    private String userName;
    private String pwd;

    public InsightService(RestTemplate restTemplate, String server, String userName, String pwd) {
        this.restTemplate = restTemplate;
        this.server = server;
        this.userName = userName;
        this.pwd = pwd;
        //Authenticate with NTLM as Insight API uses that
        NTLMAuthenticator authenticator = new NTLMAuthenticator(userName,pwd);
        Authenticator.setDefault(authenticator);
    }


    public ResponseEntity<ICompanyItems> getAllOrganisations(){

        String apiPath = "/api/v1/customers";
        String body = "{}";
        RequestEntity<String> req;
        ResponseEntity<ICompanyItems> res = null;

        try {

            req = new RequestEntity<>(body, HttpMethod.GET, new URI(server + apiPath));

            res = restTemplate.exchange(req, ICompanyItems.class);

        }
        catch (Exception e){
            System.out.println("Could not GETALL organisations from insight: " + e.toString());
        }
        return res;
    }

    public ResponseEntity<ICompany> getOrganisation(Integer Id){

        String apiPath = "/api/v1/customers/" + Id.toString();
        String body = "{}";
        RequestEntity<String> req;
        ResponseEntity<ICompany> res = null;

        try {

            req = new RequestEntity<>(body,HttpMethod.GET,new URI(server + apiPath));

            res = restTemplate.exchange(req, ICompany.class);

        }
        catch (Exception e){
            System.out.println("Could not GET organisation" + Id + " from insight: " + e.toString());
        }
        return res;

    }
}
