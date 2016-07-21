package VPI.VertecClasses;

import VPI.Entities.Organisation;
import VPI.VertecClasses.VertecActivities.ActivitiesForOrganisation;
import VPI.VertecClasses.VertecOrganisations.OrganisationList;
import VPI.MyCredentials;
import VPI.VertecClasses.VertecActivities.ZUKActivities;
import VPI.VertecClasses.VertecOrganisations.JSONContact;
import VPI.VertecClasses.VertecOrganisations.JSONOrganisation;
import VPI.VertecClasses.VertecOrganisations.ZUKOrganisations;
import VPI.VertecClasses.VertecProjects.JSONProject;
import VPI.VertecClasses.VertecProjects.ZUKProjects;
import VPI.VertecClasses.VertecTeam.ZUKTeam;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.List;

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
     * Returns the project with the specified projectCode
     */
    public ResponseEntity<JSONProject> getProject(String code){
        return getFromVertec("https://" + server + "/projects/" + code, JSONProject.class);
    }

    /**
     * Returns the project with the specified id
     */
    public ResponseEntity<JSONProject> getProject(Long id){
        return getFromVertec("https://" + server + "/projects/" + id, JSONProject.class);
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

    public ResponseEntity<JSONOrganisation> getOrganisation(Long v_id){
        return getFromVertec("https://" + server + "/organisation/" + v_id, JSONOrganisation.class);
    }

    public ResponseEntity<OrganisationList> getOrganisationList(List<Long> ids){
        String idsAsString = "";
        for(int i = 0; i < ids.size(); i++) {
            if (i < ids.size() -1) {
                idsAsString += ids.get(i) + ",";
            } else {
                idsAsString += ids.get(i);
            }
        }
        return getFromVertec("https://" + server + "/organisations/" + idsAsString, OrganisationList.class);
    }

    public ResponseEntity<ActivitiesForOrganisation> getActivitiesForOrganisation(Long id){
        return getFromVertec("https://" + server + "/organisation/" + id +"/activities", ActivitiesForOrganisation.class);
    }

    public ResponseEntity<JSONContact> getContact(long v_id){
        return getFromVertec("https://" + server + "/contacts/" + v_id, JSONContact.class);
    }

    public ResponseEntity<String> getAddressEntry(Long v_id) {
        return getFromVertec("https://" + server + "/addressEntry/" + v_id, String.class);
    }

    public ResponseEntity<ZUKTeam> getTeamDetails() {
        return getFromVertec("https://" + server + "/ZUKTeam", ZUKTeam.class);
    }

    //TODO: test
    public ResponseEntity<String> mergeTwoOrganisations(Long mergeId, Long survivorId) {
        return getFromVertec("https://" + server + "/organisation/" + mergeId + "/mergeInto/" + survivorId, String.class);
    }

    //TODO: test
    public ResponseEntity<VPI.VertecClasses.VertecOrganisations.Organisation> getOrganisationCommonRep(Long id) {
        return getFromVertec("https://" + server + "/organisation/" + id, VPI.VertecClasses.VertecOrganisations.Organisation.class);
    }
}
