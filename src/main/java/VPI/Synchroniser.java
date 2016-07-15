package VPI;

import VPI.PDClasses.PDService;
import VPI.PipedriveStateClasses.PipedriveState;
import VPI.VertecClasses.VertecService;
import VPI.VertecStateClasses.VertecState;

public class Synchroniser {

    private VertecState vertecState;
    private PipedriveState pipedriveState;

    private PDService pipedrive;
    private VertecService vertec;

    public Synchroniser(PDService pipedrive, VertecService vertec) {
        this.pipedrive = pipedrive;
        this.vertec = vertec;
    }
}
