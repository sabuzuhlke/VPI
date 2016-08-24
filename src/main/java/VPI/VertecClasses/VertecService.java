package VPI.VertecClasses;

import VPI.Entities.Contact;
import VPI.Entities.util.ContactList;
import VPI.Keys.TestVertecKeys;
import VPI.VertecClasses.VertecActivities.ActivitiesForAddressEntry;
import VPI.VertecClasses.VertecOrganisations.OrganisationList;
import VPI.MyCredentials;
import VPI.VertecClasses.VertecActivities.ZUKActivities;
import VPI.VertecClasses.VertecOrganisations.JSONContact;
import VPI.VertecClasses.VertecOrganisations.JSONOrganisation;
import VPI.VertecClasses.VertecOrganisations.ZUKOrganisations;
import VPI.VertecClasses.VertecProjects.JSONProject;
import VPI.VertecClasses.VertecProjects.ZUKProjects;
import VPI.VertecClasses.VertecTeam.Employee;
import VPI.VertecClasses.VertecTeam.EmployeeList;
import VPI.VertecClasses.VertecTeam.ZUKTeam;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.List;

import static VPI.Entities.util.Utilities.*;

public class VertecService {
    private RestTemplate restTemplate;
    private String server;
    private String username;
    private String pwd;

    public VertecService(String server) {
        this.restTemplate = new RestTemplate();
        this.server = server;

        MyCredentials creds = new MyCredentials();
        this.username = TestVertecKeys.usr; // = creds.getUserName();
        this.pwd = TestVertecKeys.pwd; //= creds.getPass();
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
        try{

            return restTemplate.exchange(
                new RequestEntity<>(headers, HttpMethod.GET, URI.create(uri)),
                responseType);
        } catch (HttpClientErrorException e){
            System.out.println(new RequestEntity<>(headers, HttpMethod.GET, URI.create(uri)));
            throw e;
        }
    }

    /**
     * Returns a ZUKOrganisations containing all organisastions relevant to ZUK along with nested contacts
     * and a list of dangling contacts not attached to organisationState
     */
    public ResponseEntity<ZUKOrganisations> getZUKOrganisations() {
        return getFromVertec("https://" + server + "/organisationState/ZUK", ZUKOrganisations.class);
    }

    /**
     * Returns a ZUKOrganisations containing all organisastions relevant to ZUK in the new representation
     * /Entities.Organisation/
     */
    public ResponseEntity<OrganisationList> getAllZUKOrganisations() {
        return getFromVertec("https://" + server + "/organisationState/all", OrganisationList.class);
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
    public ResponseEntity<JSONProject> getProject(String code) {
        return getFromVertec("https://" + server + "/projects/" + code, JSONProject.class);
    }

    /**
     * Returns the project with the specified id
     */
    public ResponseEntity<JSONProject> getProject(Long id) {
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

    public ResponseEntity<JSONOrganisation> getOrganisation(Long v_id) {
        return getFromVertec("https://" + server + "/organisation/" + v_id, JSONOrganisation.class);
    }

    public ResponseEntity<OrganisationList> getOrganisationList(List<Long> ids) {

        return getFromVertec("https://" + server + "/organisationState/" + idsAsString(ids), OrganisationList.class);
    }

    public ResponseEntity<ActivitiesForAddressEntry> getActivitiesForAddressEntry(Long id) {
        return getFromVertec("https://" + server + "/organisation/" + id + "/activities", ActivitiesForAddressEntry.class);
    }

    public ResponseEntity<JSONContact> getContact(long v_id) {
        return getFromVertec("https://" + server + "/contacts/" + v_id, JSONContact.class);
    }

    public ResponseEntity<String> getAddressEntry(Long v_id) {
        return getFromVertec("https://" + server + "/addressEntry/" + v_id, String.class);
    }

    public ResponseEntity<ZUKTeam> getTeamDetails() {
        return getFromVertec("https://" + server + "/ZUKTeam", ZUKTeam.class);
    }

    public ResponseEntity<String> mergeTwoOrganisations(Long mergeId, Long survivorId) {
        return getFromVertec("https://" + server + "/organisation/" + mergeId + "/mergeInto/" + survivorId, String.class);
    }

    public ResponseEntity<VPI.VertecClasses.VertecOrganisations.Organisation> getOrganisationCommonRep(Long id) {
        return getFromVertec("https://" + server + "/organisation/" + id, VPI.VertecClasses.VertecOrganisations.Organisation.class);
    }

    public List<Contact> getContactList(List<Long> ids) {

        return getFromVertec("https://" + server + "/contact/" + idsAsString(ids), ContactList.class).getBody().getContacts();
    }

    public List<Employee> getSalesTeam() {

        return getFromVertec("https://" + server + "/employees/pipedrive", EmployeeList.class).getBody().getEmployees();
    }
}
