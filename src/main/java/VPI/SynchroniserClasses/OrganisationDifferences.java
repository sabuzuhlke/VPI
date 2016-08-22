package VPI.SynchroniserClasses;

import VPI.Entities.Organisation;
import VPI.SynchroniserClasses.PipedriveStateClasses.PipedriveState;
import VPI.SynchroniserClasses.VertecStateClasses.VertecState;

import java.util.Set;

public class OrganisationDifferences {

    private Set<Organisation> organisationsToCreateOnVertec;
    private Set<Organisation> organisationsToCreateOnPipedrive;

    private Set<Organisation> organisationsToDeleteFromVertec;
    private Set<Organisation> organisationsToDeleteFromPipedrive;

    private Set<Organisation> organisationsToUpdateOnVertec;
    private Set<Organisation> organisationsToUpdateOnPipedrive;
    private Set<Organisation> organisationsWithConflict;
    private Set<Organisation> organisationsWithNoChanges;

    public OrganisationDifferences(VertecState vertecState, PipedriveState pipedriveState, SynchroniserState synchroniserState) {
        this.calculateOrganisationDifferences(vertecState, pipedriveState, synchroniserState);
    }

    /**
     * Populate differences lists based on the states provided
     * @param vertecState
     * @param pipedriveState
     * @param synchroniserState
     */
    private void calculateOrganisationDifferences(VertecState vertecState, PipedriveState pipedriveState, SynchroniserState synchroniserState) {
        //Logic in here to populate each list with appropriate organisations
        findOrganisationsToCreateOnVertec(pipedriveState);
        findOrganisationsToCreateOnPipedrive(vertecState);

        findOrganisationsToDeleteFromVertec(pipedriveState, synchroniserState);
        findOrganisationsToDeleteFromPipedrive(vertecState, synchroniserState);

        compareMatchingOrganisations(pipedriveState, vertecState, synchroniserState);
    }

    /**
     * Matches organisations by Vertec_Id, will then compare them and add each entry to one of the following lists:
     * -> toUpdateOnVertec -> different values, no update on vertec, update on pipedrive since last sync
     * -> toUpdateOnPipedrive -> different values, update on vertec since last sync (overwrite pipedrive whether updated or not)
     * -> withConflict -> different values, update on vertec and on pipedrive since last sync
     * -> noChanges -> same values, no change to either or same change made to both.
     * @param pipedriveState
     * @param vertecState
     * @param synchroniserState
     */
    private void compareMatchingOrganisations(PipedriveState pipedriveState, VertecState vertecState, SynchroniserState synchroniserState) {
    }

    /**
     * Finds organisations to delete from pipedrive by finding which organisations we posted to pipedrive in the past
     * that we no longer recieve from vertec
     * @param vertecState
     * @param synchroniserState
     */
    private void findOrganisationsToDeleteFromPipedrive(VertecState vertecState, SynchroniserState synchroniserState) {
    }


    /**
     * Finds organisations to delete from vertec by finding which organisations we posted in in the past that are no longer present
     * on pipedrive
     * @param pipedriveState
     * @param synchroniserState
     */
    private void findOrganisationsToDeleteFromVertec(PipedriveState pipedriveState, SynchroniserState synchroniserState) {
    }

    /**
     * Finds organisations to create on pipedrive by finding organisations on vertec that have no Pipedrive_Id
     * (Vertec entries whose Vertec_Id has not been previously mapped to a Pipedrive_Id)
     * @param vertecState
     */
    private void findOrganisationsToCreateOnPipedrive(VertecState vertecState) {
    }

    /**
     * Finds organisations to create on vertec by finding organisations on pipedrive that have no Vertec_Id
     * @param pipedriveState
     */
    private void findOrganisationsToCreateOnVertec(PipedriveState pipedriveState) {
    }

}
