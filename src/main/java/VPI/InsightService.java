package VPI;

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.net.Authenticator;
import java.net.URI;



/**
 * Created by gebo on 12/04/2016.
 */
public class InsightService {

    private RestTemplate restTemplate;
    private String server;
    String userName;
    String pwd;

    public InsightService(RestTemplate restTemplate, String server, String userName, String pwd) {
        this.restTemplate = restTemplate;
        this.server = server;
        this.userName = userName;
        this.pwd = pwd;
        NTLMAuthenticator authenticator = new NTLMAuthenticator(userName,pwd);
        Authenticator.setDefault(authenticator);
    }


    public void getAllOrganisations(){

        String apiPath = "/api/v1/customers";
        String body = "{}";
        RequestEntity<String> req = null;
        ResponseEntity<String> res = null;

        try {

            req = new RequestEntity<String>(body, HttpMethod.GET, new URI(server + apiPath));
            System.out.println("GETALL req headers: " + req.getHeaders().toString());

            res = restTemplate.exchange(req,String.class);


            System.out.println("GETALL insight orgs returned: " + res.toString());
        }
        catch (Exception e){
            System.out.println("Could not GETALL organisations from insight: " + e.toString());
        }

    }
}
