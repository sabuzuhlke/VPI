package VPI.SynchroniserClasses;

import VPI.PDClasses.PDService;
import VPI.StateDifference;
import VPI.SynchroniserClasses.PipedriveStateClasses.PipedriveState;
import VPI.SynchroniserClasses.SynchroniserState;
import VPI.SynchroniserClasses.VertecStateClasses.VertecState;
import VPI.VertecClasses.VertecService;

import java.io.IOException;

public class Synchroniser {

    private VertecState vertecState;
    private PipedriveState pipedriveState;
    private SynchroniserState synchroniserState;

    private StateDifference stateDifference;

    public Synchroniser(PDService pipedrive, VertecService vertec) throws IOException {
        this.synchroniserState = new SynchroniserState(vertec, pipedrive);

        this.pipedriveState = new PipedriveState(pipedrive, this.synchroniserState);
        this.vertecState = new VertecState(vertec, pipedriveState, this.synchroniserState);

        this.stateDifference = new StateDifference(vertecState, pipedriveState, synchroniserState);
        performFirstSync();
        //TODO delete merged conact mappings from contact id map
    }

    /**
     * Function will be called every 24 hours to perform sync
     * Will refresh states to get up to date information then calculate difference object
     */
    public void performSync() {
        //possibly refresh synchroniser state
        pipedriveState.refresh();
        vertecState.refresh();

        this.stateDifference = new StateDifference(vertecState, pipedriveState, synchroniserState);
        extractDifferencesAndApply();
    }

    public void performFirstSync() {
        extractDifferencesAndApply();
    }

    public void extractDifferencesAndApply() {

        extraractDifferencesAndApplyForOrganisatiozns();

    }

    private void extraractDifferencesAndApplyForOrganisatiozns() {

    }

//    public void updateConflictiongOrganisationsOnVertec(){
//        this.getStateDifference().getOrganisationDifferences().getUpdateConflicts().stream()
//                .filter(org -> org.getPipedriveId() != null) //Organisation has to have a PID to be posted
//                .map(org -> {
//                    Long id = this.getVertecState().vertec.
//                })
//    }
    //=================================HELPER FUNCTIONS==========================================================

    public VertecState getVertecState() {
        return vertecState;
    }

    public PipedriveState getPipedriveState() {
        return pipedriveState;
    }

    public SynchroniserState getSynchroniserState() {
        return synchroniserState;
    }

    public StateDifference getStateDifference() {
        return stateDifference;
    }

}


