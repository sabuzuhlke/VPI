package VPI.VertecStateClasses;

import VPI.VertecClasses.VertecOrganisations.JSONOrganisation;

import java.util.List;

public class VertecOrganisations {

    private List<JSONOrganisation> organisations;

    public VertecOrganisations() {
    }

    public List<JSONOrganisation> getOrganisations() {
        return organisations;
    }

    public void setOrganisations(List<JSONOrganisation> organisations) {
        this.organisations = organisations;
    }
}
