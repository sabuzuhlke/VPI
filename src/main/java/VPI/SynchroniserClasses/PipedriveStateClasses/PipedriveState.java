package VPI.SynchroniserClasses.PipedriveStateClasses;

import VPI.Entities.Organisation;
import VPI.Entities.OrganisationContainer;
import VPI.PDClasses.HierarchyClasses.PDRelationshipReceived;
import VPI.PDClasses.PDService;
import VPI.SynchroniserClasses.SynchroniserState;
import org.apache.commons.collections4.BidiMap;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PipedriveState {

    public PDService pipedrive;
    public OrganisationContainer organisations;

    private SynchroniserState synchroniserState;

    public PipedriveState(PDService pipedrive, SynchroniserState synchroniserState) {
        this.pipedrive = pipedrive;
        this.synchroniserState = synchroniserState;
    }


    public OrganisationContainer loadPipedriveOrganisations() throws IOException {

        BidiMap<Long, Long> orgIdmap = synchroniserState.getOrganisationMap();
        Map<Long, Organisation> orgs = new HashMap<>();
        List<Organisation> nonVidOrgs = new ArrayList<>();

        pipedrive.getAllOrganisations().getBody().getData()
                .forEach(org -> {
                    List<PDRelationshipReceived> relList = pipedrive.getRelationships(org.getId());
                    PDRelationshipReceived pdr = null;
                    for (PDRelationshipReceived rel : relList) {
                        //below line filters all relationships and keeps the one that contains the parent of the given organisation
                        if (rel.getType().equals("parent") && !rel.getParent().getName().equals(org.getName())) {
                            pdr = rel;
                        }
                    }
                    Organisation organisation = new Organisation(org, pdr, orgIdmap);
                    if(organisation.getVertecId() == null){
                        nonVidOrgs.add(organisation);
                    } else {
                        orgs.put(organisation.getVertecId(), organisation);
                    }
                });
        return new OrganisationContainer(orgs,nonVidOrgs);
    }

    public OrganisationContainer getOrganisations() {
        return organisations;
    }

    public void setOrganisations(OrganisationContainer organisations) {
        this.organisations = organisations;
    }

    public PDService getPipedrive() {
        return pipedrive;
    }

    public void setPipedrive(PDService pipedrive) {
        this.pipedrive = pipedrive;
    }

    public SynchroniserState getSynchroniserState() {
        return synchroniserState;
    }

    public void setSynchroniserState(SynchroniserState synchroniserState) {
        this.synchroniserState = synchroniserState;
    }
}
