package VPI.SynchroniserClasses;

import VPI.Entities.Organisation;
import VPI.Entities.OrganisationState;
import VPI.Entities.util.Utilities;
import VPI.SynchroniserClasses.PipedriveStateClasses.PipedriveState;
import VPI.SynchroniserClasses.VertecStateClasses.VertecState;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class OrganisationDifferences {

    private Set<Organisation> createOnVertec; //
    private Set<Organisation> createOnPipedrive;

    private Set<Long> deleteFromVertec;//
    private Set<Organisation> deleteFromPipedrive;
    private Map<Organisation, Organisation> deletionFromPipedriveConflicts;
    private Set<Long> deletionFromVertecConflicts; //will contain pipedrive Id of confliction organisation

    private Set<Organisation> updateOnVertec;//
    private Set<Organisation> updateOnPipedrive;
    private Set<Organisation> updateConflicts; //contains PDOrganisation
    private Set<Organisation> updateConflictsReciprocal = new HashSet<>();
    private Set<Organisation> noChanges;

    public OrganisationDifferences(VertecState vertecState, PipedriveState pipedriveState, SynchroniserState synchroniserState) {
        createOnPipedrive = new HashSet<>();
        deleteFromPipedrive = new HashSet<>();
        createOnVertec = new HashSet<>();
        deletionFromPipedriveConflicts = new HashMap<>();
        deletionFromVertecConflicts = new HashSet<>();
        deleteFromVertec = new HashSet<>();
        updateOnVertec = new HashSet<>();
        updateOnPipedrive = new HashSet<>();
        updateConflicts = new HashSet<>();
        noChanges = new HashSet<>();
        calculateOrganisationDifferences(vertecState, pipedriveState, synchroniserState);
    }

    //TODO: When updating organisations if the pipedrive ID of the Vertec and PD org are different, replace the corresponding
    //TODO: entry in the orIdmap to contain the PID of the pipedrive version. (On DISK)!!!!!!!!!!!!!!!!!!!!

    //TODO for update conflicts keep pipedrive version
    /**
     * Populate differences lists based on the states provided
     *
     * @param vertecState
     * @param pipedriveState
     * @param synchroniserState
     */
    public void calculateOrganisationDifferences(VertecState vertecState, PipedriveState pipedriveState, SynchroniserState synchroniserState) {
        //Logic in here to populate each list with appropriate organisationState
        findOrganisationsToCreateOnVertec(pipedriveState);
        findOrganisationsToCreateOnPipedrive(vertecState, synchroniserState, pipedriveState);

        findOrganisationsToDeleteFromVertec(pipedriveState, synchroniserState, vertecState);
        findOrganisationsToDeleteFromPipedrive(vertecState, synchroniserState, pipedriveState);

        compareMatchingOrganisations(pipedriveState, vertecState, synchroniserState);
    }

    /**
     * Matches organisationState by Vertec_Id, will then compare them and add each entry to one of the following lists:
     * -> toUpdateOnVertec -> different values, no update on vertec, update on pipedrive since last sync
     * -> toUpdateOnPipedrive -> different values, update on vertec since last sync (overwrite pipedrive whether updated or not)
     * -> withConflict -> different values, update on vertec and on pipedrive since last sync
     * -> noChanges -> same values, no change to either or same change made to both.
     *
     * @param pipedriveState
     * @param vertecState
     * @param synchroniserState
     */
    private void compareMatchingOrganisations(PipedriveState pipedriveState, VertecState vertecState, SynchroniserState synchroniserState) {

        //only needs to deal with orgs that match on vid, since all other possibilities are dealt with by the other functions
        vertecState.getOrganisationState().organisationsWithVIDs.values().stream()
                .filter(Organisation::getActive)
                .forEach(vOrg -> {
                    Organisation pOrg = pipedriveState.getOrganisationState().organisationsWithVIDs.get(vOrg.getVertecId());
                    if (pOrg == null) return;
                    decideWhereToUpdate(vOrg, pOrg, synchroniserState);

                });
    }

    private void decideWhereToUpdate(Organisation vOrg, Organisation pOrg, SynchroniserState state) {

        Boolean vOrgModifiedSinceLastSync = modifiedSinceLastSync(state, vOrg);
        Boolean pOrgModifiedSinceLastSync = modifiedSinceLastSync(state, pOrg);

        //Case 0: No Changes
        //either if both Organisations every single field equals, or none of them have been modified since the last sync
        if (vOrg.equals(pOrg) || (!vOrgModifiedSinceLastSync && !pOrgModifiedSinceLastSync)) {
            noChanges.add(vOrg);
            return;
        }
        //Case 1: CONFLICT
        if (vOrgModifiedSinceLastSync && pOrgModifiedSinceLastSync) {
            updateConflicts.add(pOrg);
            updateConflictsReciprocal.add((vOrg));
            return;
        }
        //Case 2: update Pipedrive
        if (vOrgModifiedSinceLastSync && !pOrgModifiedSinceLastSync) {
            updateOnPipedrive.add(updatePipedriveFromVertec(pOrg, vOrg));
            return;
        }

        //Case 3: update on Vertec
        if (pOrgModifiedSinceLastSync && !vOrgModifiedSinceLastSync) {
            updateOnVertec.add(updateVertecFromPipedrive(vOrg, pOrg));
            return;
        }


    }

    public Set<Organisation> getUpdateConflictsReciprocal() {
        return updateConflictsReciprocal;
    }

    private Organisation updatePipedriveFromVertec(Organisation pOrg, Organisation vOrg) {
        assert pOrg.getPipedriveId() != null;
        pOrg.updateOrganisationWithFreshValues(vOrg);
        return pOrg;
    }

    private Organisation updateVertecFromPipedrive(Organisation vOrg, Organisation pOrg) {
        vOrg.updateOrganisationWithFreshValues(pOrg);
        return vOrg;

    }

    /**
     * Finds Organisations to delete from Pipedrive, by getting All organisationState from pipedrive that have been posted to pipedrive.
     * Conditions for Deletion: 1) set inactive on Vertec since last synch and not modified on pipedrive
     * 2) set inactive on Vertec since last synch, but has been modified on pipedrive -- mark as conflict
     *
     * @param vertecState       all vertec organisationState are contained herewithin
     * @param synchroniserState contains all necessary maps etc
     */
    public void findOrganisationsToDeleteFromPipedrive(VertecState vertecState, SynchroniserState synchroniserState, PipedriveState pipedriveState) {
        deletionFromPipedriveConflicts = new HashMap<>();
        deleteFromPipedrive = new HashSet<>();
        //get all organisations from vertecState that have been deleted since the last sync and are owned by the sales team
        List<Organisation> deletedVertecOrgs = vertecState.organisationState.getAllOrganisations().stream()
                .filter(org -> !org.getActive()) //are inactive now
                .filter(org -> modifiedSinceLastSync(synchroniserState, org))// have been modified since last synch
                .collect(Collectors.toList());

        OrganisationState pipedriveOrgs = pipedriveState.organisationState;

        //Check for any conflicts
        deletedVertecOrgs.forEach(vOrg -> {
            Organisation pOrg = pipedriveOrgs.getOrganisationByVertecId(vOrg.getVertecId());
            if (pOrg == null) return;
            if (modifiedSinceLastSync(synchroniserState, pOrg)) {
                deletionFromPipedriveConflicts.put(vOrg, pOrg);
            } else if (synchroniserState.modificationMadeByCrashingSync(pOrg.getModified())) {
                //if org would be deleted from vertec, but pipedrive has been modified within the crashWindow, then that means that
                //it was a deletion conflict and we have already dealt with it
                return;
            } else {
                deleteFromPipedrive.add(vOrg);
            }
        });

    }


    /**
     * Finds organisationState to delete from vertec by finding which organisationState we posted in in the past that are no longer present
     * on pipedrive
     * <p>
     * If we identify an organisation as one we should delete from vertec, we:
     * 1) delete it, or
     * 2) if organisation has been modified on vertec since last sync, we re-activate organisation on pipedrive and mark as conflict
     * and replace data with vertecData
     *
     * @param pipedriveState
     * @param synchroniserState
     */
    public void findOrganisationsToDeleteFromVertec(PipedriveState pipedriveState, SynchroniserState synchroniserState, VertecState vertecState) {
        synchroniserState.getOrganisationIdMap().keySet()
                .stream()
                .filter(id -> !pipedriveState.organisationState.organisationsWithVIDs.keySet().contains(id))
                .filter(id -> vertecState.organisationState.organisationsWithVIDs.keySet().contains(id))
                .filter(id -> synchroniserState.getOrganisationIdMap().containsKey(id))
                .forEach(id -> {
                    if (modifiedSinceLastSync(synchroniserState, vertecState.organisationState.organisationsWithVIDs.get(id))) {
                        deletionFromVertecConflicts.add(synchroniserState.getOrganisationIdMap().get(id));
                    } else {
                        deleteFromVertec.add(id);
                    }
                });
    }

    /**
     * Finds organisationState to create on pipedrive by finding organisationState on vertec that have no Pipedrive_Id
     * (Vertec entries whose Vertec_Id has not been previously mapped to a Pipedrive_Id)
     * and that do not appear on pipedrive and are active
     *
     * @param vertecState
     */
    public void findOrganisationsToCreateOnPipedrive(VertecState vertecState, SynchroniserState synchroniserState, PipedriveState pipedriveState) {
        //Organisations which do appear in vertec and are active but do not appear on pipedrive and in the orgid map are the ones that need posting to Pipedrive

        createOnPipedrive = new HashSet<>();
        vertecState.organisationState.getAllOrganisations().stream()
                .filter(org -> !synchroniserState.getOrganisationIdMap().containsKey(org.getVertecId()))
                .filter(org -> !pipedriveState.getOrganisationState().organisationsWithVIDs.containsKey(org.getVertecId()))
                .forEach(org -> createOnPipedrive.add(org));


        //TODO posted organisationState need to be added to the Organisationmap!!
    }

    /**
     * Finds organisationState to create on vertec by finding organisationState on pipedrive that have no Vertec_Id
     *
     * @param pipedriveState
     */
    public void findOrganisationsToCreateOnVertec(PipedriveState pipedriveState) {
        //All organisationState that havent got a vid on pipedrive fall into this category and are owned by sales team
        //NOT strictly speaking true. organisationState might be added to PD that already exist on vertec, but as of yet we have no way of matching those
        createOnVertec = new HashSet<>();
        pipedriveState.getOrganisationState().organisationsWithoutVIDs
                //Todo Ask about posting incomplete entries
                .forEach(org -> createOnVertec.add(org));

        //TODO add posted Organisations to orgIdMap!!!!
    }

    //=================================================Helper functions=====================================================
    private boolean modifiedSinceLastSync(SynchroniserState synchroniserState, Organisation org) {
        LocalDateTime orgMod = LocalDateTime.parse(
                Utilities.formatToVertecDate(org.getModified()));
        LocalDateTime syncTime = LocalDateTime.parse(synchroniserState.getPreviousCompleteSyncEndTime());
        return (orgMod.isAfter(syncTime));
    }

    public Set<Organisation> getCreateOnVertec() {
        return createOnVertec;
    }

    public void setCreateOnVertec(Set<Organisation> createOnVertec) {
        this.createOnVertec = createOnVertec;
    }

    public Set<Organisation> getCreateOnPipedrive() {
        return createOnPipedrive;
    }

    public void setCreateOnPipedrive(Set<Organisation> createOnPipedrive) {
        this.createOnPipedrive = createOnPipedrive;
    }

    public Set<Long> getDeleteFromVertec() {
        return deleteFromVertec;
    }

    public void setDeleteFromVertec(Set<Long> deleteFromVertec) {
        this.deleteFromVertec = deleteFromVertec;
    }

    public Set<Organisation> getDeleteFromPipedrive() {
        return deleteFromPipedrive;
    }

    public void setDeleteFromPipedrive(Set<Organisation> deleteFromPipedrive) {
        this.deleteFromPipedrive = deleteFromPipedrive;
    }

    public Map<Organisation, Organisation> getDeletionFromPipedriveConflicts() {
        return deletionFromPipedriveConflicts;
    }

    public void setDeletionFromPipedriveConflicts(Map<Organisation, Organisation> deletionFromPipedriveConflicts) {
        this.deletionFromPipedriveConflicts = deletionFromPipedriveConflicts;
    }

    public Set<Organisation> getUpdateOnVertec() {
        return updateOnVertec;
    }

    public void setUpdateOnVertec(Set<Organisation> updateOnVertec) {
        this.updateOnVertec = updateOnVertec;
    }

    public Set<Organisation> getUpdateOnPipedrive() {
        return updateOnPipedrive;
    }

    public void setUpdateOnPipedrive(Set<Organisation> updateOnPipedrive) {
        this.updateOnPipedrive = updateOnPipedrive;
    }

    public Set<Organisation> getUpdateConflicts() {
        return updateConflicts;
    }

    public void setUpdateConflicts(Set<Organisation> updateConflicts) {
        this.updateConflicts = updateConflicts;
    }

    public Set<Organisation> getNoChanges() {
        return noChanges;
    }

    public void setNoChanges(Set<Organisation> noChanges) {
        this.noChanges = noChanges;
    }

    public Set<Long> getDeletionFromVertecConflicts() {
        return deletionFromVertecConflicts;
    }

    public void setDeletionFromVertecConflicts(Set<Long> deletionFromVertecConflicts) {
        this.deletionFromVertecConflicts = deletionFromVertecConflicts;
    }
}
