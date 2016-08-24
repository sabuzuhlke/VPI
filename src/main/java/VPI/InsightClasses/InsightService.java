package VPI.InsightClasses;

import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.net.Authenticator;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

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
            System.out.println("Could not GET all organisationState from insight: " + e.toString());
        }
        return res;
    }

    public ResponseEntity<VOrganisation> getOrganisation(Long Id){

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

    public List<VOrganisation> getOrganisationList(List<Long> ids) {
        List<VOrganisation> output = new ArrayList<>();
        for(Long id: ids) {
            ResponseEntity<VOrganisation> res = getOrganisation(id);
            if (res.getStatusCode() == HttpStatus.OK) {
                output.add(res.getBody());
            } else {
                System.out.println(res.getStatusCode());
            }
        }
        return output;
    }

    public ResponseEntity<VContact[]> getContactsForOrganisation(Long orgId) {

        String apiPath = "/api/v1/customers/" + orgId + "/contacts";
        RequestEntity<String> req;
        ResponseEntity<VContact[]> res = null;

        try {

            req = new RequestEntity<>(HttpMethod.GET, new URI(server + apiPath));

            res = restTemplate.exchange(req, VContact[].class);

        } catch (Exception e) {
            System.out.println(e.toString());
        }

        return res;
    }

    public ResponseEntity<VProjectList> getProjectsForZUK() {

        String apiPath = "/api/v1/projects?projectTypes=C&companies=4";
        RequestEntity<String> req;
        ResponseEntity<VProjectList> res = null;

        try {

            req = new RequestEntity<>(HttpMethod.GET, new URI(server + apiPath));

            res = restTemplate.exchange(req, VProjectList.class);

        } catch (Exception e) {
            System.out.println(e.toString());
        }
        return res;
    }
}
