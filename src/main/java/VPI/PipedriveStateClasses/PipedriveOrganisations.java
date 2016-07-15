package VPI.PipedriveStateClasses;

import VPI.PDClasses.Organisations.PDOrganisationReceived;

import java.util.List;

public class PipedriveOrganisations {

    private List<PDOrganisationReceived> organisations;

    public PipedriveOrganisations() {
    }

    public List<PDOrganisationReceived> getOrganisations() {
        return organisations;
    }

    public void setOrganisations(List<PDOrganisationReceived> organisations) {
        this.organisations = organisations;
    }
}
