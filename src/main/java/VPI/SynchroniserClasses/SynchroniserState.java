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
    //String representing the date/time at which the sync last finished running
    private String syncTime;

    //Map of Pipedrive UserId -> User email
    private Map<Long, String> pipedriveOwnerMap;
    //Map of Vertec UserId -> User email
    private Map<Long, String> vertecOwnerMap;

    //Organisation items

    //Map of Vertec_Id <-> Pipedrive_Id for organisationState we have posted to pipedrive
    private DualHashBidiMap<Long, Long> organisationIdMap;

    //List of Vertec Ids that we imported from vertec but are not owned by ZUK sales team members
    private List<Long> vertecIdsOfNonZUKOrganisations;


    public SynchroniserState(VertecService vertec, PDService pipedrive) throws IOException {
        this.organisationIdMap = loadOrganisationIdMap();
        this.pipedriveOwnerMap = constructReverseMap(constructPipedriveUserEmailToIdMap(getVertecUserEmails(vertec), getPipedriveUsers(pipedrive)));
        this.vertecOwnerMap = constructReverseMap(constructVertecUserEmailToIdMap(vertec.getSalesTeam()));
        this.vertecIdsOfNonZUKOrganisations = loadExternalOrganisations();
        this.syncTime = loadSyncTime();
    }

    /**
     * Loads latest organisation Id Map from file
     */
    private DualHashBidiMap<Long, Long> loadOrganisationIdMap() throws IOException {
        return Utilities.loadIdMap("productionMaps/productionOrganisationMap");
    }

    /**
     * Loads latest list of external organisationState from file
     */
    private List<Long> loadExternalOrganisations() throws IOException {
        return Utilities.loadIdList("productionMaps/productionMissingOrganisations15-07-16");
    }

    /**
     * Loads latest time that sync was run
     */
    private String loadSyncTime() throws IOException {
        String line;
        File file = new File("synctime");
        FileReader reader = new FileReader(file.getAbsolutePath());
        BufferedReader breader = new BufferedReader(reader);
        line = breader.readLine();
        return line;
    }

    /**
     *Function to create the reverse of a map, used to create common representation of entities by storing email of owner rather than vertec and pipedrive ids
     */
    public Map<Long, String> constructReverseMap(Map<String, Long> normalMap) {
        //TODO: 'constructReverseMap' check defualt value is correct person
        Map<Long, String> reverseMap = new DefaultHashMap<>("sabine.strauss§@zuhlke.com");
        for (String email : normalMap.keySet()) {
            reverseMap.put(normalMap.get(email), email);
        }
        return reverseMap;
    }

    /**
     * Uses list of Vertec Employees to build map of Employee email -> Employee Id
     */
    public Map<String, Long> constructVertecUserEmailToIdMap(List<Employee> employees) {
        //TODO: 'constructMap' check defualt value is correct person
        Map<String, Long> teamIdMap = new DefaultHashMap<>(23560788L);
        for (Employee e : employees) {
            if (e.getEmail() != null && !e.getEmail().isEmpty())
                teamIdMap.put(e.getEmail(), e.getId());
        }
        return teamIdMap;
    }

    /**
     * Gets a list of ZUK Employee emails
     */
    public Set<String> getVertecUserEmails(VertecService vertec) {
        return vertec.getTeamDetails()
                .getBody()
                .getMembers()
                .stream()
                .map(Employee::getEmail)
                .collect(toSet());
    }

    /**
     * Gets a list of Users from pipedrive
     */
    public List<PDUser> getPipedriveUsers(PDService pipedrive ) {
        return pipedrive.getAllUsers().getBody().getData();
    }

    /**
     * Matches vertec employee emails to pipedrive user emails and creates map entry for user email -> pipedriveId
     */
    public Map<String, Long> constructPipedriveUserEmailToIdMap(Set<String> v_emails, List<PDUser> pd_users) {//TODO: write test for this and complete
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


    //============================================ Helper Functions ====================================================

    public DualHashBidiMap<Long, Long> getOrganisationIdMap() {
        return organisationIdMap;
    }

    public Map<Long, String> getVertecOwnerMap() {
        return vertecOwnerMap;
    }

    public String getSyncTime() {
        return syncTime;
    }
}
