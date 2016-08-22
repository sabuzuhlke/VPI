package VPI.SynchroniserClasses.VertecStateClasses;

import VPI.VertecClasses.VertecService;

public class VertecState {

    private VertecOrganisations vertecOrganisations;

    public VertecState(VertecService vertecService) {
        this.vertecOrganisations = new VertecOrganisations(vertecService);
    }



}
