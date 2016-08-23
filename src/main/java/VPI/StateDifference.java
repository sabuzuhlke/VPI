package VPI;

import VPI.SynchroniserClasses.OrganisationDifferences;
import VPI.SynchroniserClasses.PipedriveStateClasses.PipedriveState;
import VPI.SynchroniserClasses.SynchroniserState;
import VPI.SynchroniserClasses.VertecStateClasses.VertecState;

public class StateDifference {

    private OrganisationDifferences organisationDifferences;

    public StateDifference(VertecState vertecState, PipedriveState pipedriveState, SynchroniserState synchroniserState) {
        this.organisationDifferences = new OrganisationDifferences(vertecState, pipedriveState, synchroniserState);
    }

    public OrganisationDifferences getOrganisationDifferences() {
        return organisationDifferences;
    }
}
