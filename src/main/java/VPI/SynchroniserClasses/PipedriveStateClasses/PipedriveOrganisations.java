package VPI.SynchroniserClasses.PipedriveStateClasses;

import VPI.Entities.Organisation;
import VPI.PDClasses.PDService;

import java.util.List;

public class PipedriveOrganisations {

    private PDService pdService;

    private List<Organisation> organisations;

    public PipedriveOrganisations(PDService pdService) {
        this.pdService = pdService;
        this.refresh();
    }

    private void refresh() {
        //code to get all orgs
        this.organisations = pdService.getAllOrganisations().getBody().getData().stream().map(o -> o.)
    }

}
