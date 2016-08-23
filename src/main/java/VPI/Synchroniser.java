package VPI;

import VPI.Entities.Organisation;
import VPI.PDClasses.PDService;
import VPI.SynchroniserClasses.PipedriveStateClasses.PipedriveState;
import VPI.SynchroniserClasses.SynchroniserState;
import VPI.VertecClasses.VertecService;
import VPI.SynchroniserClasses.VertecStateClasses.VertecState;

import java.io.IOException;

public class Synchroniser {

    private VertecState vertecState;
    private PipedriveState pipedriveState;
    private SynchroniserState synchroniserState;

    private StateDifference stateDifference;


    public Synchroniser(PDService pipedrive, VertecService vertec) throws IOException {
        this.synchroniserState = new SynchroniserState(vertec, pipedrive);

        this.pipedriveState = new PipedriveState(pipedrive, this.synchroniserState);
        this.vertecState = new VertecState(vertec, this.synchroniserState);

        this.stateDifference = new StateDifference(vertecState, pipedriveState, synchroniserState);
    }

    /**
     * TODO Find good representation for owner email to id maps
     * Todo find out how to best use state difference model to use for comparing and updating elements
     * Todo State cases are: 1) new on Vertec,  2) new on PD, 3) has already been imported
     * Case 1) All Organisations fall into this category that do not exist on pipedrive && that have never been imported to PD -- might be able to reduce it to last contition
     * Case 2) All Organisattions fall  into this category that are in the PD orgList, and have no VID
     * Case 3) All organisations fall into this category that match on VID from the pd and vertec  org Lists
     */

    public void synchOrganisations() throws IOException {


        pipedriveState.setOrganisations(pipedriveState.loadPipedriveOrganisations());
        //vertecState.setOrganisations(vertecState.getVertecOrganisations((pipedriveState.organisations.orgsWithVIDs)));

//        for(Organisation vOrg : vertecOrganisations.orgsWithVIDs.values()){
//            Organisation pOrg = vertecOrganisations.getByV(vOrg.getVertecId());
//
//            compareOrgansiations(vOrg, pOrg);
//
//        }
//
//        for(Organisation org : pipedrivePOrganisations.orgsWithoutVID){
//            //Todo Try to find a matching organisation on vertec (Not necessarily in vertecOrganisations)
//            //if match found, call compareOrganisations
//            //else, post to Vertec
//        }

        //These sorts are not necessary, but on average they are going to double the speed of the below code
//        Collections.sort(vertecOrganisations);
//        Collections.sort(pipedrivePOrganisations);
//
//        for (Organisation vOrg : vertecOrganisations) {
//            Boolean matched = false;
//
//            for (Organisation pOrg : pipedrivePOrganisations) {
//                //No vertec Id found for organisation on pipedrive (might be a  new Organisation, or might have a pair on Vertec)
//                if (pOrg.getVertecId() == null) {
//                    //log
//                    GlobalClass.log.info("Found Organisation of pipedrive without a Vertec ID : " + pOrg.toJSONString());
//                    //TODO figure out what to do here
//                    //go to next
//                    continue;
//                }
//                // if no changes detected, move on
//                if (pOrg.equals(vOrg)) {
//                    matched = true;
//                    break;
//
//                }
//                //else if the organisations match on vid then one of them has been modified
//                else if (vOrg.getVertecId() == pOrg.getVertecId().longValue()) {
//                    //TODO compare and keep the correct organisation
//
//                    matched = true;
//                    break;
//                }
//                //TODO find out whether the PDOrg is the same as vOrg by other means
//            }
//            //if below condition evaluates to true, that means vertec organsiation does not exist on pipedrive yet, so post
//            if (!matched) {
//                pdOrgPostList.add(vOrg);
//            }
//        }
    }

    private void compareOrgansiations(Organisation vOrg, Organisation pOrg) {
        //Todo complete
    }


    //=================================HELPER FUNCTIONS==========================================================


    public VertecState getVertecState() {
        return vertecState;
    }

    public void setVertecState(VertecState vertecState) {
        this.vertecState = vertecState;
    }

    public PipedriveState getPipedriveState() {
        return pipedriveState;
    }

    public void setPipedriveState(PipedriveState pipedriveState) {
        this.pipedriveState = pipedriveState;
    }

    public SynchroniserState getSynchroniserState() {
        return synchroniserState;
    }

    public void setSynchroniserState(SynchroniserState synchroniserState) {
        this.synchroniserState = synchroniserState;
    }

    public StateDifference getStateDifference() {
        return stateDifference;
    }

    public void setStateDifference(StateDifference stateDifference) {
        this.stateDifference = stateDifference;
    }
}


