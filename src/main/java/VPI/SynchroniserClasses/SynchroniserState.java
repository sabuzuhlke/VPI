package VPI.SynchroniserClasses;

import VPI.DefaultHashMap;
import VPI.Entities.util.Utilities;
import VPI.PDClasses.PDService;
import VPI.PDClasses.Users.PDUser;
import VPI.VertecClasses.VertecService;
import VPI.VertecClasses.VertecTeam.Employee;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

/**
 * Class for storing all maps of vertec_id <-> pipederive_id, posted previously
 */
public class SynchroniserState {

    private DualHashBidiMap<Long, Long> organisationMap;

    private List<Long> externalOrganisations;
    private String syncTime;

    private Map<Long, String> pdOwnerMap;
    private Map<Long, String> vertecOwnerMap;


    public SynchroniserState(VertecService vertec, PDService pipedrive) throws IOException {
        this.organisationMap = loadOrganisationIdMap();
        this.pdOwnerMap = constructReverseMap(constructTeamIdMap(getVertecUserEmails(vertec), getPipedriveUsers(pipedrive)));
        this.vertecOwnerMap = constructReverseMap(constructMap(vertec.getSalesTeam()));
        this.externalOrganisations = loadExternalOrganisations();
        this.syncTime = loadSyncTime();
    }




    private DualHashBidiMap<Long, Long> loadOrganisationIdMap() throws IOException {
        return Utilities.loadIdMap("productionMaps/productionOrganisationMap");
    }
    private List<Long> loadExternalOrganisations() throws IOException {
        return Utilities.loadIdList("productionMaps/productionMissingOrganisations15-07-16");
    }
    private String loadSyncTime() throws IOException {
        String line;
        File file = new File("synctime");
        FileReader reader = new FileReader(file.getAbsolutePath());
        BufferedReader breader = new BufferedReader(reader);
        line = breader.readLine();
        return line;
    }


    public Map<Long, String> constructReverseMap(Map<String, Long> normalMap) {


        Map<Long, String> reverseMap = new HashMap<>();

        for (String email : normalMap.keySet()) {
            reverseMap.put(normalMap.get(email), email);
        }

        return reverseMap;
    }

    public Map<String, Long> constructMap(List<Employee> employees) {
        Map<String, Long> teamIdMap = new DefaultHashMap<>(5295L);
        for (Employee e : employees) {
            if (e.getEmail() != null && !e.getEmail().isEmpty())
                teamIdMap.put(e.getEmail(), e.getId());
        }
        return teamIdMap;
    }

    public Set<String> getVertecUserEmails(VertecService vertec) {
        return vertec.getTeamDetails()
                .getBody()
                .getMembers()
                .stream()
                .map(Employee::getEmail)
                .collect(toSet());
    }
    public List<PDUser> getPipedriveUsers(PDService pipedrive ) {
        return pipedrive.getAllUsers().getBody().getData();
    }

    public Map<String, Long> constructTeamIdMap(Set<String> v_emails, List<PDUser> pd_users) {//TODO: write test for this and complete
        Map<String, Long> teamIdMap = new DefaultHashMap<>(1533390L);
        for (String v_email : v_emails) {
            Boolean mapped = false;
            for (PDUser pd_user : pd_users) {
                if (pd_user.getActive_flag() && v_email.toLowerCase().equals(pd_user.getEmail().toLowerCase())) {
                    teamIdMap.put(v_email, pd_user.getId());
                    mapped = true;
                }
            }
            if (!mapped) {
                teamIdMap.put(v_email, 1533390L); //TODO: replace id with appropriate id, wolfgangs or admin?
            }

        }
        teamIdMap.put("sabine.streuss@zuhlke.com", teamIdMap.get("sabine.strauss@zuhlke.com"));
        teamIdMap.put("adam.cole@zuhlke.com", 1272849L);
        return teamIdMap;
    }



    public DualHashBidiMap<Long, Long> getOrganisationMap() {
        return organisationMap;
    }

    public void setOrganisationMap(DualHashBidiMap<Long, Long> organisationMap) {
        this.organisationMap = organisationMap;
    }

    public Map<Long, String> getPdOwnerMap() {
        return pdOwnerMap;
    }

    public void setPdOwnerMap(Map<Long, String> pdOwnerMap) {
        this.pdOwnerMap = pdOwnerMap;
    }

    public Map<Long, String> getVertecOwnerMap() {
        return vertecOwnerMap;
    }

    public void setVertecOwnerMap(Map<Long, String> vertecOwnerMap) {
        this.vertecOwnerMap = vertecOwnerMap;
    }

    public List<Long> getExternalOrganisations() {
        return externalOrganisations;
    }

    public void setExternalOrganisations(List<Long> externalOrganisations) {
        this.externalOrganisations = externalOrganisations;
    }

    public String getSyncTime() {
        return syncTime;
    }

    public void setSyncTime(String syncTime) {
        this.syncTime = syncTime;
    }
}
