package VPI.SynchroniserClasses;

import VPI.DefaultHashMap;
import VPI.Entities.util.Utilities;
import VPI.Keys.DevelopmentKeys;
import VPI.Keys.ProductionKeys;
import VPI.PDClasses.PDService;
import VPI.PDClasses.Users.PDUser;
import VPI.VertecClasses.VertecService;
import VPI.VertecClasses.VertecTeam.Employee;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;
import org.apache.tomcat.jni.Time;

import java.io.*;
import java.time.LocalDateTime;
import java.util.*;

import static java.util.stream.Collectors.toSet;

/**
 * Class for storing all maps of vertec_id <-> pipederive_id, posted previously
 * Also provides a mapping of the users across the systems
 */
public class SynchroniserState {

    public static Long SYNCHRONISER_PD_USERID = 1533390L;
    public static Long SYNCHRONISER_VERTEC_USERID = 23560788L;


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
    private Map<String, Long> pipedriveOwnerMap;
    //Map of Vertec UserId -> User email
    private Map<String, Long> vertecOwnerMap;

    //Map of vertecuserID to pdUserId

    //Organisation items

    //Map of Vertec_d <-> Pipedrive_Id for organisationState we have posted to pipedrive
    private DualHashBidiMap<Long, Long> organisationIdMap;

    //List of Vertec Ids that we imported from vertec but are not owned by ZUK sales team members
    private List<Long> vertecIdsOfNonZUKOrganisations;


    public SynchroniserState(VertecService vertec, PDService pipedrive) throws IOException {
        this.organisationIdMap = loadOrganisationIdMap();
        this.pipedriveOwnerMap = constructPipedriveUserEmailToIdMap(getVertecUserEmails(vertec), getPipedriveUsers(pipedrive));
        this.vertecOwnerMap = constructVertecUserEmailToIdMap(vertec.getSalesTeam());
        this.vertecIdsOfNonZUKOrganisations = loadExternalOrganisations();
        this.previousCompleteSyncEndTime = loadPreviousCompleteSyncEndTime();
        this.crashWindows = new HashSet<>();
        //TODO: finish setting up times/ crashWindows -- or  not
    }

    /**
     * Constructor used purely for testing purposes, sets PATH_TO_FAULT_TOLERANT_FILES to filepath so we can test
     * different scenarios
     */
    public SynchroniserState(VertecService vertec, PDService pipedrive, String filepath) throws IOException {
        this.PATH_TO_FAULT_TOLERANCE_FILES = filepath;
        this.organisationIdMap = loadOrganisationIdMap();
        this.pipedriveOwnerMap = constructPipedriveUserEmailToIdMap(getVertecUserEmails(vertec), getPipedriveUsers(pipedrive));
        this.vertecOwnerMap = constructVertecUserEmailToIdMap(vertec.getSalesTeam());
        this.vertecIdsOfNonZUKOrganisations = loadExternalOrganisations();
        this.previousCompleteSyncEndTime = loadPreviousCompleteSyncEndTime();
        this.crashWindows = new HashSet<>();
        //TODO: finish setting up times/ crashWindows
    }

    /**
     * Loads latest organisation Id Map from file
     */
    private DualHashBidiMap<Long, Long> loadOrganisationIdMap() throws IOException {
        return Utilities.loadIdMap(ProductionKeys.MAPSPATH + "/productionOrganisationMap");
    }

    /**
     * Loads latest list of external organisationState from file
     */
    private List<Long> loadExternalOrganisations() throws IOException {
        return Utilities.loadIdList("productionMaps/productionMissingOrganisations15-07-16");
    }


    /**
     *Function to create the reverse of a map, used to create common representation of entities by storing email of owner rather than vertec and pipedrive ids
     * Needed so we can access the map by values instead of keys
     */
    public Map<Long, String> constructReverseMap(Map<String, Long> normalMap) {
        //TODO: 'constructReverseMap' check defualt value is correct person
        Map<Long, String> reverseMap = new DefaultHashMap<>("sabine.strauss@zuhlke.com");
        for (String email : normalMap.keySet()) {
            reverseMap.put(normalMap.get(email), email);
        }


        reverseMap.put(5726L, "wolfgang.emmerich@zuhlke.com"); //Vertec id of David Levin
        reverseMap.put(18010762L, "sabine.strauss@zuhlke.com"); //Vertec id of allana poleon
        reverseMap.put(21741030L, "sabine.strauss@zuhlke.com"); //Vertec id of kathryn fletcher
        reverseMap.put(504419L, "sabine.strauss@zuhlke.com"); //Vertec id of maria burley
        reverseMap.put(18635504L, "sabine.strauss@zuhlke.com"); //Vertec id of hayley syms
        reverseMap.put(10301189L, "justin.cowling@zuhlke.com"); //Vertec id of julia volland
        reverseMap.put(1795374L, "justin.cowling@zuhlke.com"); //Vertec id of rod cobain
        reverseMap.put(8904906L, "justin.cowling@zuhlke.com"); //Vertec id of afzar haider
        reverseMap.put(15948308L, "justin.cowling@zuhlke.com"); //Vertec id of peter mcmanus
        reverseMap.put(24807265L, "sabine.strauss@zuhlke.com"); //Vertec id of ileana Meehan
        reverseMap.put(16400137L, "sabine.strauss@zuhlke.com"); //Peter Brown
        reverseMap.put(17739496L, "sabine.strauss@zuhlke.com"); //Steve Freeman
        reverseMap.put(22501574L, "sabine.strauss@zuhlke.com"); //John Seston
        reverseMap.put(24907657L, "sabine.strauss@zuhlke.com"); //Ina

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
    public Map<String, Long> constructPipedriveUserEmailToIdMap(Set<String> v_emails, List<PDUser> pd_users) {
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

    /**
     * This is from importer used in tests- real map is dynamically constructed from the web interfaces
     */
    private Map<String, Long> constructTestTeamMap() {
        Map<String, Long> map = new DefaultHashMap<>(1424149L);

        map.put("wolfgang.emmerich@zuhlke.com", 1363410L); //Wolfgang
        map.put("tim.cianchi@zuhlke.com", 1363402L); //Tim
        map.put("neil.moorcroft@zuhlke.com", 1363429L); //Neil
        map.put("mike.hogg@zuhlke.com", 1363424L); //Mike
        map.put("justin.cowling@zuhlke.com", 1363416L); //Justin
        map.put("brewster.barclay@zuhlke.com", 1363403L); //Brewster
        map.put("keith.braithwaite@zuhlke.com", 1363488L); //Keith
        map.put("peter.brown@zuhlke.com", 1415840L); //Peter Brown
        map.put("steve.freeman@zuhlke.com", 1415845L); //Steve Freeman
        map.put("john.seston@zuhlke.com", 1424149L); //John Seston
        map.put("sabine.streuss@zuhlke.com", 1424149L); //Sabine
        map.put("sabine.strauss@zuhlke.com", 1424149L); //Sabine
        map.put("ileana.meehan@zuhlke.com", 1424149L); //Ileana
        map.put("ina.hristova@zuhlke.com", 1424149L); //Ina
        map.put("adam.cole@zuhlke.com", 1709153L); //adam
        map.put("bryan.thal@zuhlke.com", 1532142L); //bryan
        map.put(null, 1363410L);

        return map;
    }

    public Map<Long, Long> buildPipedriveProductionToTestUserIdMap() {

        Map<Long, Long> map = new HashMap<>();

        map.put(1199544L, 1363410L);//wolf
        map.put(1533398L, 1363402L);//tim
        map.put(1214871L, 1363429L);//neil
        map.put(1214873L, 1363424L);//mike
        map.put(1160176L, 1363416L);//just
        map.put(1199532L, 1363403L);//brew
        map.put(1211554L, 1363488L);//keith
        //peter brown has no companys
        //steve freeman has no companies
        map.put(1533390L, 1424149L);
        map.put(1272849L, 1709153L);
        //bryan, ina, iliean have none
        return map;

    }

    public Map<String, Long> getPipedriveOwnerMap() {
        return pipedriveOwnerMap;
    }

    public void setPipedriveOwnerMap(Map<String, Long> pipedriveOwnerMap) {
        this.pipedriveOwnerMap = pipedriveOwnerMap;
    }


    //============================================ Helper Functions ====================================================

    public DualHashBidiMap<Long, Long> getOrganisationIdMap() {
        return organisationIdMap;
    }

    public Map<Long, String> getIdToEmailVertecOwnerMap() {
        return constructReverseMap(vertecOwnerMap);
    }

    public Map<String, Long> getVertecOwnerMap() {
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

    /**
     * Determines whether supplied datetime has been modified since last sync completion
     */
    public boolean isModified(String modificationDateTime){
        if (modificationDateTime == null) return false;
        LocalDateTime mDT = LocalDateTime.parse(modificationDateTime);
        LocalDateTime sFT = LocalDateTime.parse(previousCompleteSyncEndTime);

        return mDT.isAfter(sFT);
    }



    public Set<TimeInterval> getCrashWindows() {
        return crashWindows;
    }

    public void updateMapWith(Map<Long, Long> vertecIdsToPipedriveIds) {
        organisationIdMap.putAll(vertecIdsToPipedriveIds);
    }

    public void saveDeletedListToFile(List<Long> deletedIds, String vertecOrPipedrive) throws IOException {

        Utilities.saveList(vertecOrPipedrive + "DeletedIDsList", deletedIds, true);

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
