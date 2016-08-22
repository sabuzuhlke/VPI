package VPI.SynchroniserClasses.PipedriveStateClasses;

import VPI.PDClasses.PDService;

public class PipedriveState {

    private PipedriveOrganisations pDOrganisation;

    public PipedriveState(PDService pdService) {
        this.pDOrganisation = new PipedriveOrganisations(pdService);
    }

    public PipedriveOrganisations getpDOrganisation() {
        return pDOrganisation;
    }

}
