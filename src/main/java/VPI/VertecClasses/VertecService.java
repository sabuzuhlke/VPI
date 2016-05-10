package VPI.VertecClasses;

import VPI.MyCredentials;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

/**
 * Created by sabu on 29/04/2016.
 */
public class VertecService {
    private RestTemplate restTemplate;
    private String server;
    private String username;
    private String pwd;

    public VertecService(String server){
        this.restTemplate = new RestTemplate();
        this.server = server;

        MyCredentials creds = new MyCredentials();
        this.username = creds.getUserName();
        this.pwd = creds.getPass();
    }

    static {
        //for localhost testing only
        javax.net.ssl.HttpsURLConnection.setDefaultHostnameVerifier(
                new javax.net.ssl.HostnameVerifier() {

                    public boolean verify(String hostname,
                                          javax.net.ssl.SSLSession sslSession) {
                        if (hostname.equals("localhost")) {
                            return true;
                        }
                        return false;
                    }
                });
    }

    public ResponseEntity<ZUKResponse> getZUKinfo(){

        //request path
        String apiPath = "/organisations/ZUK";

        //declare request and response
        RequestEntity<String> req;
        ResponseEntity<ZUKResponse> res = null;

        //add authentication header to headers object
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Authorization", username + ':' + pwd);

        try{

            req = new RequestEntity<>(headers, HttpMethod.GET, new URI("https://" + server + apiPath));
            res = restTemplate.exchange(req, ZUKResponse.class);

        }  catch (Exception e) {
            System.out.println("Exception in Vertec Service, trying to get ZUKInfo: " + e);
        }

        return res;
    }

    public String ping() {

        //request path
        String apiPath = "/ping";

        //declare request, response and answer string
        RequestEntity<String> req;
        ResponseEntity<String> res = null;
        String answer = "";

        //add authentication header to headers object
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Authorization", username + ':' + pwd);

        try{

            req = new RequestEntity<>(headers, HttpMethod.GET, new URI("https://" + server + apiPath));
            res = restTemplate.exchange(req, String.class);
            answer = res.getBody();

        } catch (Exception e) {
            System.out.println("Exception in Vertec Service, trying to ping " + e);
        }

        return answer;
    }
}
