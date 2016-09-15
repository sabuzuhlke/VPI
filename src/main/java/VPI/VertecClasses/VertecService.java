package VPI.VertecClasses;

import VPI.Entities.*;
import VPI.Entities.util.ContactList;
import VPI.Entities.util.SyncLogList;
import VPI.Keys.TestVertecKeys;
import VPI.VertecClasses.VertecActivities.ActivitiesForAddressEntry;
import VPI.VertecClasses.VertecOrganisations.*;
import VPI.MyCredentials;
import VPI.VertecClasses.VertecActivities.ZUKActivities;
import VPI.VertecClasses.VertecOrganisations.Organisation;
import VPI.VertecClasses.VertecProjects.JSONProject;
import VPI.VertecClasses.VertecProjects.ZUKProjects;
import VPI.VertecClasses.VertecTeam.Employee;
import VPI.VertecClasses.VertecTeam.EmployeeList;
import VPI.VertecClasses.VertecTeam.ZUKTeam;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.List;

import static VPI.Entities.util.Utilities.*;

/**
 * This class handles all communication with VRAPI
 */
public class VertecService {
    private RestTemplate restTemplate;
    private String server;
    private String username;
    private String pwd;
    private SyncLogList log;

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
        try {

            return restTemplate.exchange(
                    new RequestEntity<>(headers, HttpMethod.GET, URI.create(uri)),
                    responseType);
        } catch (HttpClientErrorException e) {
            System.out.println(new RequestEntity<>(headers, HttpMethod.GET, URI.create(uri)));
            throw e;
        }
    }

    <RES> ResponseEntity<RES> putToVertec(String uri, Class<RES> responseType) {
//        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
//        headers.add("Authorization", username + ':' + pwd);
//        try {
//
//            return restTemplate.exchange(
//                    new RequestEntity<>(headers, HttpMethod.PUT, URI.create(uri)),
//                    responseType);
//        } catch (Exception e) {
//            System.out.println(e);
//            throw e;
//        }
        return null;
    }

    <RES, REQ> ResponseEntity<RES> putToVertec(REQ payload, String uri, Class<RES> responseType) {
//        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
//        headers.add("Authorization", username + ':' + pwd);
//        return restTemplate.exchange(
//                new RequestEntity<>(payload, headers, HttpMethod.PUT, URI.create(uri)),
//                responseType);
        return null;
    }

    <RES> ResponseEntity<RES> deleteFromVertec(String uri, Class<RES> responseType) {
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Authorization", username + ':' + pwd);
        return restTemplate.exchange(
                new RequestEntity<>(headers, HttpMethod.DELETE, URI.create(uri)),
                responseType);
    }

    <REQ, RES> ResponseEntity<RES> postToVertec(REQ payload, String uri, Class<RES> responseType) {
//        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
//        headers.add("Authorization", username + ':' + pwd);
//        return restTemplate.exchange(
//                new RequestEntity<>(payload, headers, HttpMethod.POST, URI.create(uri)),
//                responseType);
        return null;
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
        return getFromVertec("https://" + server + "/organisations/all", OrganisationList.class);
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

        return getFromVertec("https://" + server + "/organisations/" + idsAsString(ids), OrganisationList.class);
    }

    /**
     * Both organisations and contacts extend addressentries on vertec
     * @param id
     * @return
     */
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

    public ResponseEntity<Long> createOrganisation(Organisation organisation) {
        return postToVertec(organisation, "https://" + server + "/organisation", Long.class);
    }

    public boolean updateOrganisation(Long vId, Organisation organisation) {
        ResponseEntity<String> res = putToVertec(organisation, "https://" + server + "/organisation/" + vId, String.class);
        boolean updated = res.getStatusCode().equals(HttpStatus.OK);
        if(!updated) System.out.println("ERROR!!\n" + res.getBody());
        return updated;
    }

    public Long deleteOrganisation(Long vId){
        ResponseEntity<Long> res = deleteFromVertec("https://" + server + "/organisation/" + vId, Long.class);
        return res.getBody();
    }
    public Long activateOrganisation(Long vId){
        ResponseEntity<Long> res = putToVertec("https://" + server + "/organisation/" + vId + "/activate", Long.class);
        return res.getBody();
    }
}
