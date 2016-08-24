package VPI.SynchroniserClasses.PipedriveStateClasses;

import VPI.Entities.OrganisationState;
import VPI.PDClasses.PDService;
import VPI.SynchroniserClasses.SynchroniserState;

public class PipedriveState {

    public PDService pipedrive;

    public OrganisationState organisationState;

    public PipedriveState(PDService pipedrive, SynchroniserState synchroniserState) {
        this.pipedrive = pipedrive;
        this.organisationState = new OrganisationState(pipedrive, synchroniserState);
    }

    public void refresh() {
        organisationState.refreshFromPipedrive();
    }


    //====================================== Helper Methods ============================================================

    public OrganisationState getOrganisationState() {
        return organisationState;
    }

    public void setOrganisationState(OrganisationState organisationState) {
        this.organisationState = organisationState;
    }

    public PDService getPipedrive() {
        return pipedrive;
    }

    public void setPipedrive(PDService pipedrive) {
        this.pipedrive = pipedrive;
    }
}

//
//    public OrganisationState loadPipedriveOrganisations() throws IOException {
//
//        BidiMap<Long, Long> orgIdmap = synchroniserState.getOrganisationIdMap();
//        Map<Long, Organisation> orgs = new HashMap<>();
//        List<Organisation> nonVidOrgs = new ArrayList<>();
//
//        pipedrive.getAllOrganisations().getBody().getData()
//                .forEach(org -> {
//                    List<PDRelationshipReceived> relList = pipedrive.getRelationships(org.getId());
//                    PDRelationshipReceived pdr = null;
//                    for (PDRelationshipReceived rel : relList) {
//                        //below line filters all relationships and keeps the one that contains the parent of the given organisation
//                        if (rel.getType().equals("parent") && !rel.getParent().getName().equals(org.getName())) {
//                            pdr = rel;
//                        }
//                    }
//                    Organisation organisation = new Organisation(org, pdr, orgIdmap);
//                    if(organisation.getVertecId() == null){
//                        nonVidOrgs.add(organisation);
//                    } else {
//                        orgs.put(organisation.getVertecId(), organisation);
//                    }
//                });
//        return new OrganisationState(orgs,nonVidOrgs);
//    }