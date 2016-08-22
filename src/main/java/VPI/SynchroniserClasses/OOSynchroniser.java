package VPI.SynchroniserClasses;

import VPI.PDClasses.PDService;
import VPI.SynchroniserClasses.PipedriveStateClasses.PipedriveState;
import VPI.StateDifference;
import VPI.VertecClasses.VertecService;
import VPI.SynchroniserClasses.VertecStateClasses.VertecState;

/**
 * Created by sabu on 15/08/2016.
 */
public class OOSynchroniser {

    private String lastRunTime;

    private VertecState vState;
    private PipedriveState pdState;
    private SynchroniserState synchroniserState;

    private StateDifference differences;

    private VertecService vService;
    private PDService pdService;

    private OOSynchroniser(VertecService vertecService, PDService pdService) {
        //Set up Vertec and Pipedrive API Services and pass them in constructor to State Objects
        this.vService = vertecService; //new VertecService(TestVertecKeys.devVRAPIAdress);
        this.pdService = pdService; //new PDService("https://api.pipedrive.com/v1/", DevelopmentKeys.key);
        this.synchroniserState = new SynchroniserState();
        this.vState = new VertecState(this.vService);
        this.pdState = new PipedriveState(this.pdService);
    }

    private void calculateDifferences() {
        this.differences = new StateDifference(this.vState, this.pdState, this.synchroniserState);
    }

}
