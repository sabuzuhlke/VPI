package VPI.SynchroniserClasses;


import VPI.Entities.Organisation;
import VPI.Entities.util.Utilities;
import VPI.PDClasses.PDService;
import VPI.PDClasses.Users.PDUserItemsResponse;
import VPI.SynchroniserClasses.PipedriveStateClasses.PipedriveState;
import VPI.VertecClasses.VertecService;

import java.util.*;
import java.util.stream.Collectors;

/**
 * This class represents the state of organisations on pipedrive or on vertec
 */

public class OrganisationState {

    //Map of vertecId -> Organisation
    public Map<Long, Organisation> organisationsWithVIDs;

    //List of organisationState that do not have an Vertec_id (will only contain items from pipedriveService)
    public Set<Organisation> organisationsWithoutVIDs;

    private SynchroniserState syncState;
    private PipedriveState pipedriveState;

    private PDService pipedriveService;
    private VertecService vertecService;

    /**
     * Contructor user to create OrganisationState in PipedriveState
     */
    public OrganisationState(PDService pipedriveService, SynchroniserState syncState) {
        this.syncState = syncState;
        this.pipedriveService = pipedriveService;
        refreshFromPipedrive();
    }

    /**
     * Constructor used to create OrganisationState in VertecState
     */
    public OrganisationState(VertecService vertecService, PipedriveState pipedriveState, SynchroniserState syncState) {

        this.syncState = syncState;
        this.vertecService = vertecService;
        this.pipedriveState = pipedriveState;
        refreshFromVertec();
    }


    /**
     * Download all organisations from pipedrive
     */

    public void refreshFromPipedrive() {
        organisationsWithVIDs = new HashMap<>();
        organisationsWithoutVIDs = new HashSet<>();


        PDUserItemsResponse users = pipedriveService.getAllUsers().getBody();

        pipedriveService.getAllOrganisations().getBody().getData()
                .forEach(org -> {
                    Organisation organisation = new Organisation(org, syncState.getOrganisationIdMap());
                    if (organisation.getVertecId() == null) {
                        organisationsWithoutVIDs.add(organisation);
                    } else {
                        organisationsWithVIDs.put(organisation.getVertecId(), organisation);
                    }
                    //dealWithPDOrgModifiedBySynchroniser(organisation);

                });
    }

    /**
     * Download all organisations from vertec
     */
    public void refreshFromVertec() {

        organisationsWithVIDs = new HashMap<>();
        organisationsWithoutVIDs = new HashSet<>();
        vertecService.getAllZUKOrganisations().getBody().getOrganisations()
                .forEach(org -> {
                    if (org.getName().isEmpty() || org.getName() == null) return;
                    Organisation organisation =
                            new Organisation(
                                    org,
                                    syncState.getOrganisationIdMap().get(org.getVertecId()),
                                    syncState.getIdToEmailVertecOwnerMap().get(org.getOwnerId())
                            );
                    if (organisation.getVertecId() == null) {
                        organisationsWithoutVIDs.add(organisation);
                    } else {
                        organisationsWithVIDs.put(organisation.getVertecId(), organisation);
                    }
                    // dealWithVertecOrgModifiedBySynchroniser(org);

                });

        organisationsWithVIDs.putAll(getOrganisationsBasedOnPipedrive(pipedriveState.organisationState, organisationsWithVIDs));
    }

    /**
     * This function will load in any Organisations, that are not owned by our team members, but have been posted to pipedrive
     * Hence solving the problem that There are organisationState that have VIDs, yet are not found
     * in any of our saved lists and/or maps.
     *
     * @param organisationsFromPipedrive: This is a list of organisationState that have VIDs on pipedrive, we are goping to
     *                                    use the list of their VIDs in order to compare them to the previously imported
     *                                    organisationState.
     * @param organisations:              These are the organisationState imported in the calling function
     * @return return a map of VIDs to the corresponding organisationState so that the calling function can easily use the  result
     */
    public Map<Long, Organisation> getOrganisationsBasedOnPipedrive(OrganisationState organisationsFromPipedrive
            , Map<Long, Organisation> organisations) {


        if (organisationsFromPipedrive == null) return new HashMap<>();
        Map<Long, Organisation> orgmap = new HashMap<>();

        //extract list of VIDs
        Collection<Organisation> orgsFromPipedrive = organisationsFromPipedrive.organisationsWithVIDs.values();
        Set<Long> IdsFromVertec = organisations.keySet(); // this Set contains all Ids we have previously imported from vertec

        //get all of them that we haven't imported previously in getVertecOrganisations
        List<Long> unimportedIds = orgsFromPipedrive.stream()
                .filter(org -> !IdsFromVertec.contains(org.getVertecId()))
                .filter(org -> org.getOwnedOnVertecBy().equals("Sales Team"))
                .map(Organisation::getVertecId)
                .collect(Collectors.toList());


        if (unimportedIds.isEmpty()) return new HashMap<>();

        //get them all from vertec
        List<Organisation> vertecOrgsFromPipedrive = vertecService.getOrganisationList(unimportedIds)
                .getBody().getOrganisations().stream()
                .map(org -> new Organisation(org,
                        //To set the pipedriveID of the created organisation we'll have to access the organisationState we got from pipedrive
                        organisationsFromPipedrive.organisationsWithVIDs.get(org.getVertecId()).getPipedriveId(),
                        syncState.getIdToEmailVertecOwnerMap().get(org.getOwnerId())))
                .collect(Collectors.toList());

        //return in appropriate format
        for (Organisation org : vertecOrgsFromPipedrive) {
            orgmap.put(org.getVertecId(), org);
        }

        return orgmap;
    }


    public Organisation getOrganisationByVertecId(Long id) {
        return organisationsWithVIDs.get(id);
    }

    public Organisation getOrganisationByPipedriveId(Long id) {
        List<Organisation> all = getAllOrganisations();

        for (Organisation org : all) {
            if (org.getPipedriveId() == id.longValue()) {
                return org;
            }
        }
        return null;
    }

    public List<Organisation> getAllOrganisations() {
        List<Organisation> all = new ArrayList<>();
        all.addAll(organisationsWithVIDs.values());
        all.addAll(organisationsWithoutVIDs);
        return all;
    }



}


