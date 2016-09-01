package VPI.SynchroniserClasses;

import VPI.DefaultHashMap;
import VPI.Entities.util.Utilities;
import VPI.PDClasses.PDService;
import VPI.PDClasses.Users.PDUser;
import VPI.VertecClasses.VertecService;
import VPI.VertecClasses.VertecTeam.Employee;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;
import org.apache.tomcat.jni.Time;

import java.io.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

/**
 * Class for storing all maps of vertec_id <-> pipederive_id, posted previously
 */
public class SynchroniserState {

    //TODO get updateLogs to be able to decide who made the last change , needed for fault tolerance
    /**
     * Crash tolerant logic:
     * If program ceases execution part of the way though applying changes to pipedrive/vertec
     * On the next run we must not treat those changes as being made by Sales Team.
     * We can do this by only including changes made between the previousCompleteSyncEndTime and the previousSyncStartTime
     * and changes made between possibleCrashTime and the current time.
     * If the application had not crashed in the previous sync then the previousCompleteSyncEndTime == possibleCrashTime == previousSyncStartTime
     */
    public String PATH_TO_FAULT_TOLERANCE_FILES = "/Users/gebo/IdeaProjects/VPI/src/main/resources/";

    //String representing the date/time at which the sync last finished running
    private String previousCompleteSyncEndTime;
    //String representing the date/time at which the we last started applying changes, on Sync complete we set this equal to previousCompleteSyncStartTime == possibleCrashTime
    private String previousSyncStartTime;
    //String representing the last date/time at which we successfully applied a change
    private String possibleCrashTime;
    //List of TimeIntervals representing the times between which we started applying changes and the time the application ceased execution
    private Set<TimeInterval> crashWindows;

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
        this.previousCompleteSyncEndTime = loadPreviousCompleteSyncEndTime();
        this.crashWindows = new HashSet<>();
        //TODO: finish setting up times/ crashWindows
    }

    /**
     * Constructor used purely for testing purposes, sets PATH_TO_FAULT_TOLERANT_FILES to filepath so we can test
     * different scenarios
     */
    public SynchroniserState(VertecService vertec, PDService pipedrive, String filepath) throws IOException {
        this.PATH_TO_FAULT_TOLERANCE_FILES = filepath;
        this.organisationIdMap = loadOrganisationIdMap();
        this.pipedriveOwnerMap = constructReverseMap(constructPipedriveUserEmailToIdMap(getVertecUserEmails(vertec), getPipedriveUsers(pipedrive)));
        this.vertecOwnerMap = constructReverseMap(constructVertecUserEmailToIdMap(vertec.getSalesTeam()));
        this.vertecIdsOfNonZUKOrganisations = loadExternalOrganisations();
        this.previousCompleteSyncEndTime = loadPreviousCompleteSyncEndTime();
        this.crashWindows = new HashSet<>();
        //TODO: finish setting up times/ crashWindows
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

    public String getPreviousCompleteSyncEndTime() {
        return previousCompleteSyncEndTime;
    }

    public void setPATH_TO_FAULT_TOLERANCE_FILES(String PATH_TO_FAULT_TOLERANCE_FILES) {
        this.PATH_TO_FAULT_TOLERANCE_FILES = PATH_TO_FAULT_TOLERANCE_FILES;
    }

    public String getPATH_TO_FAULT_TOLERANCE_FILES() {

        return PATH_TO_FAULT_TOLERANCE_FILES;
    }
    // FAULT TOLERANCE FUNCTIONS =======================================================================================
    /**
     * Called when synchroniser successfully completes applying changes
     * Sets previousCompletionTime == possibleCrashTime == previousSyncStartTime == currentTime
     * and clears Set of time intervals during which sync made changes then crashed
     */
    public void finishSync() throws IOException {
        setPreviousCompleteSyncTime();
        clearCrashWindows();
        Utilities.clearFile(PATH_TO_FAULT_TOLERANCE_FILES + "previousSyncStartTime");
        Utilities.clearFile(PATH_TO_FAULT_TOLERANCE_FILES + "possibleCrashTime");
    }

    /**
     * Function to test if a dateTime is within the any of the time intervals that the program was making modifications since the previousCompleteSync
     * (These time intervals represent times at which the program was attempting a sync but failed)
     */
    public Boolean modificationMadeByCrashingSync(String time) {
        String vertecFormat = Utilities.formatToVertecDate(time);
        LocalDateTime ldt;
        ldt = vertecFormat == null ? LocalDateTime.parse(time) :  LocalDateTime.parse(vertecFormat);

        return crashWindows.stream().anyMatch(timeInterval -> timeInterval.isTimeWithinInterval(ldt));
    }

    public void addCrashWindow() throws IOException {
        String previousStartTime = loadPreviousSyncStartTime();
        String possibleCrashTime = loadPossibleCrashTime();

        if(previousStartTime == null && possibleCrashTime == null) return; //Program did not crash previously, so we are fine.
        TimeInterval crashWindow = new TimeInterval(previousStartTime, possibleCrashTime);

        new File(PATH_TO_FAULT_TOLERANCE_FILES + "crashWindows").createNewFile();
        FileWriter file = new FileWriter(PATH_TO_FAULT_TOLERANCE_FILES + "crashWindows", true); // do not overwrite
        file.write(crashWindow.toString());
        file.close();

        Utilities.clearFile(PATH_TO_FAULT_TOLERANCE_FILES + "previousSyncStartTime");
        Utilities.clearFile(PATH_TO_FAULT_TOLERANCE_FILES + "possibleCrashTime");

    }

    public void clearCrashWindows() throws IOException {
        crashWindows.clear();
        Utilities.clearFile(PATH_TO_FAULT_TOLERANCE_FILES + "crashWindows");
    }

    /**
     *
     */
    public void loadCrashWindows() throws IOException {
        boolean emptyFileCreeated = new File(PATH_TO_FAULT_TOLERANCE_FILES + "crashWindows").createNewFile();
        if(emptyFileCreeated) return; // file is empty as it has just been created

        FileReader file = new FileReader(PATH_TO_FAULT_TOLERANCE_FILES + "crashWindows");
        BufferedReader reader = new BufferedReader(file);
        String line;

        while((line = reader.readLine()) != null){
            String[] times = line.split(" ");
            String t1 = times[0];
            String t2 = times[1];
            crashWindows.add(new TimeInterval(t1, t2));
        }
        file.close();
    }

    /**
     * Loads latest time that sync was run
     */
    private String loadPreviousCompleteSyncEndTime() throws IOException {
        return Utilities.loadSingleLineFileToString(PATH_TO_FAULT_TOLERANCE_FILES + "previousCompleteSyncEndTime");
        //return LocalDateTime previousFinishTime = LocalDateTime.parse(s);
    }

    /**
     * Set file: previousCompleteSyncEndTime to contain currentTime
     */
    private void setPreviousCompleteSyncTime() throws IOException {
        setFileToCurrentTime(PATH_TO_FAULT_TOLERANCE_FILES + "previousCompleteSyncEndTime");
    }

    /**
     * Loads last time time that sync was started
     */
    private String loadPreviousSyncStartTime() throws IOException {
        return Utilities.loadSingleLineFileToString(PATH_TO_FAULT_TOLERANCE_FILES + "previousSyncStartTime");
    }

    /**
     * Set file: previousSyncStartTime to contain currentTime
     */
    private void setPreviousSyncStartTime() throws IOException {
        //TODO call when posting starts
        setFileToCurrentTime(PATH_TO_FAULT_TOLERANCE_FILES + "previousSyncStartTime");
    }

    private String loadPossibleCrashTime() throws IOException {
        return Utilities.loadSingleLineFileToString(PATH_TO_FAULT_TOLERANCE_FILES + "possibleCrashTime");
    }

    /**
     * Set file: possibleCrashTime to contain currentTime
     */
    private void setPossibleCrashTime() throws IOException {
        //TODO call after each post
        setFileToCurrentTime(PATH_TO_FAULT_TOLERANCE_FILES + "possibleCrashTime");
    }

    /**
     * Given Filename, sets file to contain currentTime --> overwrites
     */
    public void setFileToCurrentTime(String filepath) throws IOException {
        boolean b = new File(filepath).createNewFile();
        FileWriter file = new FileWriter(filepath);
        file.write(Utilities.getCurrentTime());
        file.close();
    }


    public Set<TimeInterval> getCrashWindows() {
        return crashWindows;
    }

    public class TimeInterval {
        private LocalDateTime iS;
        private LocalDateTime iE;

        public TimeInterval(String iS, String iE) {
            LocalDateTime t1 = LocalDateTime.parse(iS);
            LocalDateTime t2 = LocalDateTime.parse(iE);
            if (t1.isBefore(t2)) {
                this.iS = t1;
                this.iE = t2;
            } else {
                this.iE = t1;
                this.iS = t2;
            }
        }

        public Boolean isTimeWithinInterval(LocalDateTime time) {
            return time.isAfter(iS) && time.isBefore(iE);
        }

        @Override
        public String toString() {
            return iS + " " + iE.toString();
        }

        @Override
        public boolean equals(Object o){
            if(! (o instanceof TimeInterval)) return false;
            TimeInterval ti = (TimeInterval) o;
            return this.iS.toString().equals(ti.iS.toString()) && this.iE.toString().equals(ti.iE.toString());
        }
    }
}
