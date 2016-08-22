package VPI;

import VPI.Entities.Organisation;
import VPI.Entities.OrganisationContainer;
import VPI.Entities.util.Utilities;
import VPI.PDClasses.HierarchyClasses.PDRelationshipReceived;
import VPI.PDClasses.PDService;
import VPI.SynchroniserClasses.PipedriveStateClasses.PipedriveState;
import VPI.VertecClasses.VertecService;
import VPI.VertecClasses.VertecTeam.Employee;
import VPI.SynchroniserClasses.VertecStateClasses.VertecState;
import org.apache.commons.collections4.BidiMap;

import java.io.IOException;
import java.util.*;

public class Synchroniser {

    private VertecState vertecState;
    private PipedriveState pipedriveState;

    private PDService pipedrive;
    private VertecService vertec;
    private Importer importer;
    private Map<Long, String> pdOwnerMap;
    private Map<Long, String> vertecOwnerMap;
    List<Organisation> pdOrgPostList;

    public Synchroniser(PDService pipedrive, VertecService vertec) {
        this.pipedrive = pipedrive;
        this.vertec = vertec;
        this.importer = new Importer(pipedrive, vertec);

        this.pdOwnerMap = constructReverseMap(importer.teamIdMap);
        this.vertecOwnerMap = constructReverseMap(constructMap(vertec.getSalesTeam()));
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

        OrganisationContainer vertecOrganisations = getVertecOrganisations();

        OrganisationContainer pipedrivePOrganisations = getPipedriveOrganisations();

        for(Organisation vOrg : vertecOrganisations.organisationMap.values()){
            Organisation pOrg = vertecOrganisations.getByV(vOrg.getVertecId());

            compareOrgansiations(vOrg, pOrg);

        }

        for(Organisation org : pipedrivePOrganisations.orgsWithoutVID){
            //Todo Try to find a matching organisation on vertec (Not necessarily in vertecOrganisations)
            //if match found, call compareOrganisations
            //else, post to Vertec
        }

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
    public OrganisationContainer getVertecOrganisations() throws IOException {

        Map<Long, Organisation> organisations = new HashMap<>();
        List<Organisation> nonVIDOrgs = new ArrayList<>();
        BidiMap<Long, Long> orgIdmap = Utilities.loadIdMap("productionMaps/productionOrganisationMap");

        vertec.getAllZUKOrganisations().getBody().getOrganisations()
                .forEach(org -> {
                    Organisation organisation = new Organisation(org, orgIdmap.get(org.getVertecId()), vertecOwnerMap.get(org.getOwnerId()));
                    if (organisation.getVertecId() == null) {
                        nonVIDOrgs.add(organisation);
                    } else {
                        organisations.put(organisation.getVertecId(), organisation);
                    }

                });
        return new OrganisationContainer(organisations, nonVIDOrgs);
    }

    public OrganisationContainer getPipedriveOrganisations() throws IOException {
        BidiMap<Long, Long> orgIdmap = Utilities.loadIdMap("productionMaps/productionOrganisationMap");
        Map<Long, Organisation> orgs = new HashMap<>();
        List<Organisation> nonVidOrgs = new ArrayList<>();

        pipedrive.getAllOrganisations().getBody().getData()
                .forEach(org -> {
                    List<PDRelationshipReceived> relList = pipedrive.getRelationships(org.getId());
                    PDRelationshipReceived pdr = null;
                    for (PDRelationshipReceived rel : relList) {
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

    public Map<Long, String> constructReverseMap(Map<String, Long> normalMap) {


        Map<Long, String> reverseMap = new HashMap<>();

        for (String email : normalMap.keySet()) {
            reverseMap.put(normalMap.get(email), email);
        }

        return reverseMap;
    }

    public Map<String, Long> constructMap(List<Employee> employees) {
        Map<String, Long> teamIdMap = new DefaultHashMap<>(5295L);
        for (Employee e : employees) {
            if (e.getEmail() != null && !e.getEmail().isEmpty())
                teamIdMap.put(e.getEmail(), e.getId());
        }
        return teamIdMap;
    }
}
