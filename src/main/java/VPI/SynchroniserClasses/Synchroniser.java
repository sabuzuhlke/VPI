package VPI.SynchroniserClasses;

import VPI.Entities.Organisation;
import VPI.Entities.util.SyncLogList;
import VPI.PDClasses.PDService;
import VPI.StateDifference;
import VPI.SynchroniserClasses.PipedriveStateClasses.PipedriveState;
import VPI.SynchroniserClasses.VertecStateClasses.VertecState;
import VPI.VertecClasses.VertecService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static VPI.Entities.util.Utilities.saveList;
import static VPI.Entities.util.Utilities.saveMap;

    /**
     * This class handles the automatic propagation of updates between the the Pipedrive API and VRAPI.
     * It assumes that both Vertec and Pipedrive contain correct information(e.g. all data has been de-duplicated).
     * Should that not be the case, the behaviour is not guaranteed to be correct, and it might propagate mistakes
     * from one side to the other.
     * It also assumes, that links have been established between all corresponding data. If not, then it is going
     * to create new entries for both parts of the unlinked data.
     *
     * The Synchroniser loads in all data on construction, and resolves what needs to be done to with each object, based
     * on the last modified dates. The downloaded data is kept in the respective 'State' objects. And the actions that
     * need to be taken in the 'stateDifference' object. The former are responsible for the download, while the latter
     * is responsible for data resolution.
     *
     * The 'SynchroniserState' object holds mappings between objects on Vertec and Pipedrive. Currently, users and
     * organisations.
     *
     * The Synchroniser is only to apply changes made since its previous completion. Should the application crash, while
     * making updates, we would loose track of the updates we have already made, and they might get propagated back.
     * A useful solution would be to log all updates made by the system (these are written to disk, if an error occurs
     * and when the synchronisation completes). This is currently implemented. However, the system
     * does not use this log as of yet in any way. Also this does not deal with e.g. a power outage.
     */
public class Synchroniser {

    private VertecState vertecState;
    private PipedriveState pipedriveState;
    private SynchroniserState synchroniserState;

    private StateDifference stateDifference;

    private SyncLogList vertecLog;
    private SyncLogList pipedriveLog;


    /**
     * Construction will run synchronisation
     */
    public Synchroniser(PDService pipedrive, VertecService vertec) throws IOException {

        this.vertecLog = new SyncLogList("logs/","VertecLog");
        this.pipedriveLog = new SyncLogList("logs","/PipedriveLog");
        this.synchroniserState = new SynchroniserState(vertec, pipedrive);
        System.out.println("Created Sync state");


        this.pipedriveState = new PipedriveState(pipedrive, this.synchroniserState);
        System.out.println("Created PD state");
        this.vertecState = new VertecState(vertec, pipedriveState, this.synchroniserState);
        System.out.println("Created Vertec state");
        this.stateDifference = new StateDifference(vertecState, pipedriveState, synchroniserState);
        performFirstSync();
        //TODO delete merged conact mappings from contact id map
    }

    /**
     * Function will be called every 24 hours to perform sync
     * Will refresh states to get up to date information then calculate difference object
     */
    public void performSync() throws IOException {
        //possibly refresh synchroniser state
        pipedriveState.refresh();
        vertecState.refresh();

        this.stateDifference = new StateDifference(vertecState, pipedriveState, synchroniserState);
        extractDifferencesAndApply();
    }

        /**
         * Called on construction
         * @throws IOException if it could not write logs or maps to file
         */
    public void performFirstSync() throws IOException {
        System.out.println("Performing first sync");
        extractDifferencesAndApply();
    }

    public void extractDifferencesAndApply() throws IOException {

        printIntentions();
        //extraractDifferencesAndApplyForOrganisatiozns();
        //saveLogs();
        //synchroniserState.setPreviousCompleteSyncTime();
    }

    private void extraractDifferencesAndApplyForOrganisatiozns() throws IOException {

        try {

            updateConflictiongOrganisationsOnVertec(); //update maps inline
            updatePdOrganisations(); //update maps inline
            updateVertecOrganisations(); //update maps inline
            Map<Long, Long> orgsCreatedOnVertec = createVertecOrganisations();
            Map<Long, Long> orgsCreatedOnPipedrive = createPipedriveOrganisations();
            this.synchroniserState.organisationsDeletedFromVertec = deleteVertecOrganisations();
            this.synchroniserState.organisationsDeletedFromPipedrive = deletePipedriveOrganisations();
            this.synchroniserState.organisationsDeletedFromVertec.addAll(dealWithDeletionFromVertecConflicts());
            dealWithDeletionFromPipedriveConflicts();

            synchroniserState.updateMapWith(orgsCreatedOnVertec);
            synchroniserState.updateMapWith(orgsCreatedOnPipedrive);

            saveList("TESTorgsDeletedFromPipedrive", this.synchroniserState.organisationsDeletedFromPipedrive , true);
            saveList("TESTorgsDeletedFromVertec",  this.synchroniserState.organisationsDeletedFromVertec , true);
            saveMap(synchroniserState.getOrganisationIdMap(), "TESTorgIdMap");

        } catch (Exception e) {
            if (e instanceof IOException) throw e;
            else {
                saveLogs();

                saveList("TESTorgsDeletedFromPipedrive", this.synchroniserState.organisationsDeletedFromPipedrive , true);
                saveList("TESTorgsDeletedFromVertec",  this.synchroniserState.organisationsDeletedFromVertec , true);
                saveMap(synchroniserState.getOrganisationIdMap(), "TESTorgIdMap");
                saveMap(synchroniserState.getOrganisationIdMap(), "TESTorgIdMap");
                throw e;
            }
        }

    }

    private void printIntentions() {
        System.out.println(this.getStateDifference().getOrganisationDifferences().getCreateOnVertec().size() + " organisations will be added to vertec:\n\n\n");
        this.getStateDifference().getOrganisationDifferences().getCreateOnVertec().forEach(org -> {
            System.out.println(org.getName() + " created by " + org.getSupervisingEmail() + " PID: " + org.getPipedriveId());
        });
        System.out.println("\n\n=================================================\n\n");
        System.out.println(this.getStateDifference().getOrganisationDifferences().getUpdateOnVertec().size() + " organisations will be updated on vertec\n\n\n");
        this.getStateDifference().getOrganisationDifferences().getUpdateOnVertec().forEach(org -> {
            System.out.println(org.toJSONString() + "\n");

            System.out.println("Modified by: \n" + this.getPipedriveState().getOrganisationState().organisationsWithVIDs.get(org.getVertecId()));
        });
        System.out.println("\n\n=================================================\n\n");
        System.out.println(this.getStateDifference().getOrganisationDifferences().getDeleteFromVertec().size() + " organisations will be deleted from vertec");
        this.getStateDifference().getOrganisationDifferences().getDeleteFromVertec().forEach(org -> {
            System.out.println(this.getVertecState().getOrganisationState().organisationsWithVIDs.get(org).toJSONString());
        });
        System.out.println("\n\n=================================================\n\n");
        System.out.println(this.getStateDifference().getOrganisationDifferences().getDeletionFromVertecConflicts().size() + " organisations have been deleted on pipedrive but have been updated on vertec, so they are 'deletion conflicts' (This is fake example for testing)");
        System.out.println("Vertec IDs: " + this.getStateDifference().getOrganisationDifferences().getDeletionFromVertecConflicts());


        System.out.println("\n\n=================================================\n\n");
        System.out.println(this.getStateDifference().getOrganisationDifferences().getCreateOnPipedrive().size() + " organisations will be posted to pipedrive");
        this.getStateDifference().getOrganisationDifferences().getCreateOnPipedrive().forEach(org -> {
            System.out.println(org.getName() + " created by " + org.getSupervisingEmail() + " VID " + org.getVertecId());
        });
        System.out.println("\n\n=================================================\n\n");
        System.out.println(this.getStateDifference().getOrganisationDifferences().getUpdateOnPipedrive().size() + " organisations will be updated on pipedrive\n\n\n");
        this.getStateDifference().getOrganisationDifferences().getUpdateOnPipedrive().forEach(org -> {
            System.out.println(org.toJSONString());
        });

        System.out.println("\n\n=================================================\n\n");
        System.out.println(this.getStateDifference().getOrganisationDifferences().getDeleteFromPipedrive().size() + " organisations will be deleted from pipedrive (This fake again)");
        this.getStateDifference().getOrganisationDifferences().getDeleteFromPipedrive().forEach(org -> {
            System.out.println(org.toJSONString());
        });

        System.out.println("\n\n=================================================\n\n");
        System.out.println(this.getStateDifference().getOrganisationDifferences().getDeletionFromPipedriveConflicts().size() + " organisations have been deleted on vertec but have been updated on pipedrive, so they are 'deletion conflicts' (This is fake example for testing)");
        System.out.println("Vertec IDs: " + this.getStateDifference().getOrganisationDifferences().getDeletionFromPipedriveConflicts());


        System.out.println("\n\n=================================================\n\n");
        System.out.println(this.getStateDifference().getOrganisationDifferences().getUpdateConflicts().size() + " organisations have both been updated so will be marked as an 'update conflict'");
        this.getStateDifference().getOrganisationDifferences().getUpdateConflicts().forEach(org -> {
            this.getStateDifference().getOrganisationDifferences().getUpdateConflictsReciprocal().forEach(org2 -> {
                if (org.getVertecId().longValue() == org2.getVertecId()) {
                    System.out.println("Vertec Version: ");
                    System.out.println("\n");
                    System.out.println(org2.toJSONString());
                    System.out.println("\n\n");
                    System.out.println("Pipedrive Version: \n\n");
                    System.out.println(org.toJSONString());
                }
            });
        });


//        System.out.println("\n\n=================================================\n\n");
//        System.out.println("These organisations recieved no updates");
//        this.getStateDifference().getOrganisationDifferences().getNoChanges().forEach(org -> {
//            System.out.println(org.toJSONString());
//        });
        System.out.println("\n\n\n" + this.getStateDifference().getOrganisationDifferences().getNoChanges().size() + " Organisations did not change");

    }

    public void updateConflictiongOrganisationsOnVertec() {
        this.getStateDifference().getOrganisationDifferences().getUpdateConflicts().stream()
                .filter(org -> org.getPipedriveId() != null) //Organisation has to have a PID to be posted
                .filter(org -> org.getVertecId() != null)
                .forEach(org -> {

                    this.getVertecState().vertec.updateOrganisation(org.getVertecId(), org.toVertecRep(synchroniserState.getVertecOwnerMap().get(org.getSupervisingEmail())));
                    vertecLog.add("PUT", "Organisation", org.getName(), org.getVertecId(), org.getPipedriveId());
                    this.getSynchroniserState().getOrganisationIdMap().replace(org.getVertecId(), org.getPipedriveId());
                });

    }

    public void updateVertecOrganisations() {
        this.getStateDifference().getOrganisationDifferences().getUpdateOnVertec().stream()
                .filter(org -> org.getPipedriveId() != null) //Organisation has to have a PID to be posted
                .filter(org -> org.getVertecId() != null)
                .forEach(org -> {
                    this.getVertecState().vertec.updateOrganisation(org.getVertecId(), org.toVertecRep(synchroniserState.getVertecOwnerMap().get(org.getSupervisingEmail())));
                    vertecLog.add("PUT", "Organisation", org.getName(), org.getVertecId(), org.getPipedriveId());
                    this.getSynchroniserState().getOrganisationIdMap().replace(org.getVertecId(), org.getPipedriveId());
                });
    }

    /**
     * The Vertec IDs of newly created vertec organisations needs to be probagated back to Pipedrive
     * @return
     */
    public Map<Long, Long> createVertecOrganisations() {
        Map<Long, Long> orgIdMap = new HashMap<>();
        List<Long> errorPIDs = new ArrayList<>();
        Map<Long, Long> incompletedCreations = new HashMap<>();
        this.getStateDifference().getOrganisationDifferences().getCreateOnVertec().stream()
                .filter(org -> org.getPipedriveId() != null)
                .forEach(org -> {
                    ResponseEntity<Long> res = this.getVertecState().vertec.createOrganisation(org.toVertecRep(synchroniserState.getVertecOwnerMap().get(org.getSupervisingEmail())));

                    if (res.getStatusCode().equals(HttpStatus.ACCEPTED) || res.getStatusCode().equals(HttpStatus.CREATED)) {
                        orgIdMap.put(res.getBody(), org.getPipedriveId());
                        org.setVertecId(res.getBody());
                        vertecLog.add("POST", "Organisation", org.getName(), org.getVertecId(), org.getPipedriveId());

                        this.getPipedriveState().pipedrive.updateOrganisation(org.toPDSend(synchroniserState.getPipedriveOwnerMap().get(org.getSupervisingEmail())));
                        pipedriveLog.add("PUT", "Organisation", org.getName(), org.getVertecId(), org.getPipedriveId());

                        if (res.getStatusCode().equals(HttpStatus.ACCEPTED)) {
                            System.out.println("Some problem has occured with org Vid:" + res.getBody() + ", PDid: " + org.getPipedriveId());
                            incompletedCreations.put(res.getBody(), org.getPipedriveId());
                        }
                    } else {
                        System.out.println("Error returned: " + res);
                        errorPIDs.add(org.getPipedriveId());
                    }

                });
        System.out.println("Successful: =========================================");
        System.out.println(orgIdMap);
        System.out.println("Incomplete: =========================================");
        System.out.println(incompletedCreations);
        System.out.println("Erroneous: =========================================");
        System.out.println(errorPIDs);
        return orgIdMap;
    }

    public List<Long> deleteVertecOrganisations() {
        List<Long> idsDeleted = new ArrayList<>();
        this.getStateDifference().getOrganisationDifferences().getDeleteFromVertec()
                .forEach(id -> {
                    idsDeleted.add(vertecState.vertec.deleteOrganisation(id));
                    vertecLog.add("DELETE", "Organisation", " ", id, -1L);
                });
        System.out.println("Deleted: " + idsDeleted);
        return idsDeleted;
    }

    public Map<Long, Long> createPipedriveOrganisations() {
        Map<Long, Long> orgIdMap = new HashMap<>();

        this.getStateDifference().getOrganisationDifferences().getCreateOnPipedrive().stream()
                .filter(org -> org.getPipedriveId() == null)
                .filter(org -> org.getVertecId() != null)
                .forEach(org -> {
                    Long pId = this.getPipedriveState().pipedrive.postOrganisation(org.toPDSend(this
                            .getSynchroniserState()
                            .getPipedriveOwnerMap()
                            .get(org.getSupervisingEmail())))
                            .getBody().getData().getId();
                    orgIdMap.put(org.getVertecId(), pId);
                    pipedriveLog.add("POST", "Organisation", org.getName(), org.getVertecId(), orgIdMap.get(org.getVertecId()));
                });
        System.out.println("Posted:\n" + orgIdMap);
        return orgIdMap;
    }

    public void updatePdOrganisations() {
        this.getStateDifference().getOrganisationDifferences().getUpdateOnPipedrive().stream()
                .filter(org -> org.getPipedriveId() != null)
                .filter(org -> org.getVertecId() != null)
                .forEach(org -> {
                    this.pipedriveState.pipedrive.updateOrganisation(org.toPDSend(synchroniserState.getPipedriveOwnerMap().get(org.getSupervisingEmail())));
                    synchroniserState.getOrganisationIdMap().replace(org.getVertecId(), org.getPipedriveId());
                    pipedriveLog.add("PUT", "Organisation", org.getName(), org.getVertecId(), org.getPipedriveId());
                });
    }

    public List<Long> deletePipedriveOrganisations() {
        List<Long> idsToDel = new ArrayList<>();
        this.getStateDifference().getOrganisationDifferences().getDeleteFromPipedrive().stream()
                .filter(org -> org.getPipedriveId() != null)
                .forEach(org -> {
                    this.getPipedriveState().pipedrive.deleteOrganisation(org.getPipedriveId());
                    idsToDel.add(org.getPipedriveId());
                    pipedriveLog.add("DELETE", "Organisation", org.getName(), org.getVertecId(), org.getPipedriveId());

                });
        return idsToDel;
    }
    
    public void dealWithDeletionFromPipedriveConflicts(){
        for(Organisation vOrg : this.getStateDifference().getOrganisationDifferences().getDeletionFromPipedriveConflicts()){
            Organisation pOrg =  this.getPipedriveState().getOrganisationState().organisationsWithVIDs.
                    get(this.synchroniserState.getOrganisationIdMap().get(vOrg.getVertecId()));
            
            if(pOrg == null) continue;

            vOrg.updateOrganisationWithFreshValues(pOrg);

            this.getVertecState().vertec.updateOrganisation(vOrg.getVertecId(), vOrg.toVertecRep(synchroniserState.getVertecOwnerMap().get(vOrg.getSupervisingEmail())));

            vertecLog.add("PUT", "Organisation", vOrg.getName(), vOrg.getVertecId(), vOrg.getPipedriveId());
            this.getSynchroniserState().getOrganisationIdMap().replace(vOrg.getVertecId(), vOrg.getPipedriveId());
        }
    }

    public List<Long> dealWithDeletionFromVertecConflicts(){
        List<Long> idsDeleted = new ArrayList<>();
        this.getStateDifference().getOrganisationDifferences().getDeletionFromVertecConflicts()
                .forEach(id -> {
                    idsDeleted.add(this.synchroniserState.getOrganisationIdMap().getKey(id));
                    vertecLog.add("DELETE", "Organisation", " ", id, -1L);
                });
        System.out.println("Deleted: " + idsDeleted);
        return idsDeleted;
    }


    //=================================HELPER FUNCTIONS==========================================================

    public void saveLogs() throws IOException {
        vertecLog.save();
        pipedriveLog.save();
    }

    public VertecState getVertecState() {
        return vertecState;
    }

    public PipedriveState getPipedriveState() {
        return pipedriveState;
    }

    public SynchroniserState getSynchroniserState() {
        return synchroniserState;
    }

    public StateDifference getStateDifference() {
        return stateDifference;
    }

}


