package VPI.VertecClasses;

import VPI.MyCredentials;
import VPI.VertecClasses.VertecActivities.ZUKActivities;
import VPI.VertecClasses.VertecOrganisations.ZUKOrganisations;
import VPI.VertecClasses.VertecProjects.ZUKProjects;
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
                        return hostname.equals("localhost");
                    }
                });
    }

    private <RES> ResponseEntity<RES> getFromVertec(String uri, Class<RES> responseType) {
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Authorization", username + ':' + pwd);
        return restTemplate.exchange(
                new RequestEntity<>(headers, HttpMethod.GET, URI.create(uri)),
                responseType);
    }

    /**
     * Returns a ZUKOrganisations containing all organisastions relevant to ZUK along with nested contacts
     * and a list of dangling contacts not attached to organisations
     */
    public ResponseEntity<ZUKOrganisations> getZUKOrganisations(){
        return getFromVertec("https://" + server + "/organisations/ZUK", ZUKOrganisations.class);
    }

    /**
     * Returns a ZUKProjects containing all projects relevant to ZUK along with their nested Project Phases
     */
    public ResponseEntity<ZUKProjects> getZUKProjects() {
        return getFromVertec("https://" + server + "/projects/ZUK", ZUKProjects.class);
    }

    /**
     * Returns a list of all activities assigned to members of the ZUK sales team
     */
    public ResponseEntity<ZUKActivities> getZUKActivities() {
        return getFromVertec("https://" + server + "/activities/ZUK", ZUKActivities.class);
    }

    /**
     * Returns "Success!" if request is properly authenticated and access permissions are not limited otherwise returns
     * appropriate error string
     */
    public String ping() {
        return getFromVertec("https://" + server + "/ping", String.class).getBody();
    }
}
