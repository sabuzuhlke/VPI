package VPI.SynchroniserClasses;

import VPI.Entities.Organisation;
import VPI.Entities.OrganisationContainer;
import VPI.Entities.util.Utilities;
import VPI.SynchroniserClasses.PipedriveStateClasses.PipedriveState;
import VPI.SynchroniserClasses.VertecStateClasses.VertecState;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class OrganisationDifferences {

    private Set<Organisation> createOnVertec;
    private Set<Organisation> createOnPipedrive;

    private Set<Organisation> deleteFromVertec;
    private Set<Organisation> deleteFromPipedrive;
    private Map<Organisation, Organisation> deletionConflicts;

    private Set<Organisation> updateOnVertec;
    private Set<Organisation> updateOnPipedrive;
    private Set<Organisation> updateConflicts;
    private Set<Organisation> noChanges;


    public OrganisationDifferences(VertecState vertecState, PipedriveState pipedriveState, SynchroniserState synchroniserState) {
        createOnPipedrive = new HashSet<>();
        deleteFromPipedrive = new HashSet<>();
        createOnVertec = new HashSet<>();
        deletionConflicts = new HashMap<>();
        deleteFromVertec = new HashSet<>();
        updateOnVertec = new HashSet<>();
        updateOnPipedrive = new HashSet<>();
        updateConflicts = new HashSet<>();
        noChanges = new HashSet<>();
    }

    /**
     * Populate differences lists based on the states provided
     *
     * @param vertecState
     * @param pipedriveState
     * @param synchroniserState
     */
    public void calculateOrganisationDifferences(VertecState vertecState, PipedriveState pipedriveState, SynchroniserState synchroniserState) {
        //Logic in here to populate each list with appropriate organisations
        findOrganisationsToCreateOnVertec(pipedriveState);
        findOrganisationsToCreateOnPipedrive(vertecState, synchroniserState);

        findOrganisationsToDeleteFromVertec(pipedriveState, synchroniserState);
        findOrganisationsToDeleteFromPipedrive(vertecState, synchroniserState, pipedriveState);

        compareMatchingOrganisations(pipedriveState, vertecState, synchroniserState);
    }

    /**
     * Matches organisations by Vertec_Id, will then compare them and add each entry to one of the following lists:
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
    }

    /**
     * Finds Organisations to delete from Pipedrive, by getting All organisations from pipedrive that have been posted to pipedrive.
     * Conditions for Deletion: 1) set inactive on Vertec since last synch and not modified on pipedrive
     * 2) set inactive on Vertec since last synch, but has been modified on pipedrive -- mark as conflict
     *
     * @param vertecState       all vertec organisations are contained herewithin
     * @param synchroniserState contains all necessary maps etc
     */
    public List<Long> findOrganisationsToDeleteFromPipedrive(VertecState vertecState, SynchroniserState synchroniserState, PipedriveState pipedriveState) {


        List<Long> idsToDel = new ArrayList<>();
        //this is enough since vertecState contains all vertec organisations from vertec that appear on pipedrive(even inactive ones)
        List<Organisation> deletedVertecOrgs = vertecState.organisations.getAll().stream()
                .filter(org -> modifiedSinceLastSync(synchroniserState, org)) // have been modified since last synch
                .filter(org -> org.getOwnedOnVertecBy().equals("Sales Team")) // are owned by zuk
                .filter(org -> !org.getActive()) //are inactive now
                .collect(Collectors.toList());

        OrganisationContainer pipedriveOrgs = pipedriveState.organisations;

        deletedVertecOrgs.forEach(vOrg -> {
            Organisation pOrg = pipedriveOrgs.getByV(vOrg.getVertecId());
            if (pOrg == null) return;
            if (modifiedSinceLastSync(synchroniserState, pOrg)) {
                deletionConflicts.put(vOrg, pOrg);
            } else {
                deleteFromPipedrive.add(vOrg);
                idsToDel.add(vOrg.getVertecId());
            }
        });


        return idsToDel;
    }

    private boolean modifiedSinceLastSync(SynchroniserState synchroniserState, Organisation org) {
        LocalDateTime orgMod = LocalDateTime.parse(Utilities.formatToVertecDate(org.getModified()));
        LocalDateTime syncTime = LocalDateTime.parse(synchroniserState.getSyncTime());
        return (orgMod.isBefore(syncTime));
    }


    /**
     * Finds organisations to delete from vertec by finding which organisations we posted in in the past that are no longer present
     * on pipedrive
     *
     * @param pipedriveState
     * @param synchroniserState
     */
    private void findOrganisationsToDeleteFromVertec(PipedriveState pipedriveState, SynchroniserState synchroniserState) {
    }

    /**
     * Finds organisations to create on pipedrive by finding organisations on vertec that have no Pipedrive_Id
     * (Vertec entries whose Vertec_Id has not been previously mapped to a Pipedrive_Id)
     *
     * @param vertecState
     */
    public List<Long> findOrganisationsToCreateOnPipedrive(VertecState vertecState, SynchroniserState synchroniserState) {
        //Organisations which do not appear in the OrganisationIdMap are the ones that need posting to Pipedrive

        List<Long> idsToCreate = new ArrayList<>();
        createOnPipedrive = new HashSet<>();
        for (Organisation org : vertecState.organisations.getAll()) {
            if (!synchroniserState.getOrganisationMap().keySet().contains(org.getVertecId())) {
                createOnPipedrive.add(org);
                idsToCreate.add(org.getVertecId());
            }
        }
        //TODO posted organisations need to be added to the Organisationmap!!
        return idsToCreate;
    }

    /**
     * Finds organisations to create on vertec by finding organisations on pipedrive that have no Vertec_Id
     *
     * @param pipedriveState
     */
    public List<Long> findOrganisationsToCreateOnVertec(PipedriveState pipedriveState) {
        //All organisations that havent got a vid on pipedrive fall into this category
        //NOT strictly speaking true. organisations might be added to PD that already exist on vertec, but as of yet we have no way of matching those
        List<Long> idsToCreate = new ArrayList<>();
        createOnVertec = new HashSet<>();

        for (Organisation org : pipedriveState.getOrganisations().getAll()) {
            if (org.getVertecId() == null) {
                createOnVertec.add(org);
                idsToCreate.add(org.getPipedriveId());
            }
        }
        //TODO add posted Organisations to orgIdMap!!!!
        return idsToCreate;
    }

}
