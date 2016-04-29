package VPI.VertecClasses;

import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

/**
 * Created by sabu on 29/04/2016.
 */
public class VertecService {
    private RestTemplate restTemplate;
    private String server;

    public VertecService(String server){
        this.restTemplate = new RestTemplate();
        this.server = server;
    }

    public ResponseEntity<ZUKResponse> getZUKinfo(){
        String apiPath = "/organisations/ZUK";
        RequestEntity<String> req;
        ResponseEntity<ZUKResponse> res = null;

        try{
            req = new RequestEntity<>(HttpMethod.GET, new URI("http://" + server + apiPath));
            res = restTemplate.exchange(req, ZUKResponse.class);
        }
        catch(Exception e){
            System.out.println("Exception in Veertec Service, trying to get ZUKInfo: " + e);
        }
        return res;
    }

    public Boolean ping() {
        String apiPath = "/ping";
        RequestEntity<String> req;
        ResponseEntity<String> res = null;
        String answer = "notping";

        try{
            req = new RequestEntity<>(HttpMethod.GET, new URI("http://" + server + apiPath));
            res = restTemplate.exchange(req, String.class);
            answer = res.getBody();
        }
        catch(Exception e){
            System.out.println("Exception in Veertec Service, trying to get ZUKInfo: " + e);
        }

        return answer.equals("ping");
    }
}
