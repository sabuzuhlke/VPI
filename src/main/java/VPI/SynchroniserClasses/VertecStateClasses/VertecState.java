package VPI.SynchroniserClasses.VertecStateClasses;

import VPI.Entities.OrganisationState;
import VPI.SynchroniserClasses.PipedriveStateClasses.PipedriveState;
import VPI.SynchroniserClasses.SynchroniserState;
import VPI.VertecClasses.VertecService;

/**
 * This class is used to contain all data downloaded from Vertec when synchronisation begins
 */

public class VertecState {

    public VertecService vertec;

    public OrganisationState organisationState;

    public VertecState(VertecService vertecService, PipedriveState pipedriveState, SynchroniserState synchroniserState) {
        this.vertec = vertecService;

        this.organisationState = new OrganisationState(vertecService, pipedriveState, synchroniserState);
    }

    public void refresh() {
        organisationState.refreshFromVertec();
    }

    public OrganisationState getOrganisationState() {
        return organisationState;
    }

    public void setOrganisationState(OrganisationState organisationState) {
        this.organisationState = organisationState;
    }

}
