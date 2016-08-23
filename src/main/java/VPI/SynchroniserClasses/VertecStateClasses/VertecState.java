package VPI.SynchroniserClasses.VertecStateClasses;

import VPI.Entities.Organisation;
import VPI.Entities.OrganisationContainer;
import VPI.SynchroniserClasses.SynchroniserState;
import VPI.VertecClasses.VertecService;
import org.apache.commons.collections4.BidiMap;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class VertecState {
    public VertecService vertec;
    private SynchroniserState synchroniserState;

    public OrganisationContainer organisations;

    public VertecState(VertecService vertecService, SynchroniserState synchroniserState) {
        this.vertec = vertecService;
        this.synchroniserState = synchroniserState;
    }


    public OrganisationContainer getOrganisations() {
        return organisations;
    }

    public void setOrganisations(OrganisationContainer organisations) {
        this.organisations = organisations;
    }
    public OrganisationContainer loadVertecOrganisations(OrganisationContainer organisationsFromPipedrive) throws IOException {

        Map<Long, Organisation> organisations = new HashMap<>();
        List<Organisation> nonVIDOrgs = new ArrayList<>();
        BidiMap<Long, Long> orgIdmap = synchroniserState.getOrganisationMap();

        vertec.getAllZUKOrganisations().getBody().getOrganisations()
                .forEach(org -> {
                    if(org.getName().isEmpty() || org.getName() == null) return;
                    Organisation organisation = new Organisation(org, orgIdmap.get(org.getVertecId()),  synchroniserState.getVertecOwnerMap().get(org.getOwnerId()));
                    if (organisation.getVertecId() == null) {
                        nonVIDOrgs.add(organisation);
                    } else {
                        organisations.put(organisation.getVertecId(), organisation);
                    }

                });

        organisations.putAll(getOrganisationsBasedOnPipedrive(organisationsFromPipedrive, organisations));
        return new OrganisationContainer(organisations, nonVIDOrgs);
    }

    /**
     * This function will load in any Organisations, that are not owned by our team members, but have been posted to pipedrive
     * Hence solving the problem that There are organisations that have VIDs, yet are not found
     * in any of our saved lists and/or maps.
     * @param organisationsFromPipedrive: This is a list of organisations that have VIDs on pipedrive, we are goping to
     *                                   use the list of their VIDs in order to compare them to the previously imported
     *                                   organisations.
     * @param organisations: These are the organisations imported in the calling function
     * @return return a map of VIDs to the corresponding organisations so that the calling function can easily use the  result
     */
    public Map<Long, Organisation> getOrganisationsBasedOnPipedrive(OrganisationContainer organisationsFromPipedrive
            , Map<Long, Organisation> organisations) {

        if(organisationsFromPipedrive == null) return new HashMap<>();

        //TODO test
        Map<Long, Organisation> orgmap = new HashMap<>();

        //extract list of VIDs
        List<Long> vidsFromPipedrive = organisationsFromPipedrive.orgsWithVIDs.keySet().stream().collect(Collectors.toList());
        Set<Long> IdsFromVertec = organisations.keySet(); // this Set contains all Ids we have previously imported from vertec

        //get all of them that we haven't imported previously in getVertecOrganisations
        List<Long> unimportedIds = vidsFromPipedrive.stream()
                .filter(orgId -> !IdsFromVertec.contains(orgId))
                .collect(Collectors.toList());

        //For testing:
        System.out.println("unimportedIds:");
        System.out.println(unimportedIds);
        System.out.println(unimportedIds.size());

        //get them all from vertec
        List<Organisation> orgsFromPipedrive = vertec.getOrganisationList(unimportedIds)
                .getBody().getOrganisations().stream()
                .map(org ->  new Organisation(org,
                            //To set the pipedriveID of the created organisation we'll have to access the organisations we got from pipedrive
                            organisationsFromPipedrive.orgsWithVIDs.get(org.getVertecId()).getPipedriveId(),
                            synchroniserState.getVertecOwnerMap().get(org.getOwnerId())))
                .collect(Collectors.toList());

        //return in appropriate format
        for(Organisation org : orgsFromPipedrive){
            orgmap.put(org.getVertecId(), org);
        }
        return orgmap;
    }
}
