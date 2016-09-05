package VPI.Entities;


import VPI.Entities.util.Utilities;
import VPI.PDClasses.PDService;
import VPI.PDClasses.Users.PDUser;
import VPI.PDClasses.Users.PDUserItemsResponse;
import VPI.SynchroniserClasses.PipedriveStateClasses.PipedriveState;
import VPI.SynchroniserClasses.SynchroniserState;
import VPI.VertecClasses.VertecService;

import java.util.*;
import java.util.stream.Collectors;

public class OrganisationState {

    //Map of vertecId -> Organisation
    public Map<Long, Organisation> organisationsWithVIDs;

    //List of organisationState that do not have an Vertec_id (will only contain items from pipedriveService)
    public Set<Organisation> organisationsWithoutVIDs;

    public Map<Long, Organisation> syncModifiedOrganisationsWithVIDs;
    public Set<Organisation> syncModifiedOrganisationsWithoutVIDs;

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

//    /**
//     * Generic constructor, it will not be able to do anny communication to pd or vertec
//     */
//    public OrganisationState(SynchroniserState syncState){
//        this.organisationsWithoutVIDs = new HashSet<>();
//        this.organisationsWithVIDs = new HashMap<>();
//
//        this.syncState = syncState;
//
//        this.pipedriveService = null;
//        this.vertecService = null;
//        this. pipedriveState = null;
//    }

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
                        syncState.getVertecOwnerMap().get(org.getOwnerId())))
                .collect(Collectors.toList());

        //return in appropriate format
        for (Organisation org : vertecOrgsFromPipedrive) {
            orgmap.put(org.getVertecId(), org);
        }

        return orgmap;
    }

    public void refreshFromPipedrive() {
        organisationsWithVIDs = new HashMap<>();
        organisationsWithoutVIDs = new HashSet<>();

        syncModifiedOrganisationsWithoutVIDs = new HashSet<>();
        syncModifiedOrganisationsWithVIDs = new HashMap<>();

        PDUserItemsResponse users = pipedriveService.getAllUsers().getBody();

        pipedriveService.getAllOrganisations().getBody().getData()
                .forEach(org -> {
                    Organisation organisation = new Organisation(org, syncState.getOrganisationIdMap());
                    if (organisation.getVertecId() == null) {
                        organisationsWithoutVIDs.add(organisation);
                    } else {
                        organisationsWithVIDs.put(organisation.getVertecId(), organisation);
                    }
                    dealWithPDOrgModifiedBySynchroniser(organisation);

                });
    }

    public void refreshFromVertec() {
        syncModifiedOrganisationsWithoutVIDs = new HashSet<>();
        syncModifiedOrganisationsWithVIDs = new HashMap<>();

        organisationsWithVIDs = new HashMap<>();
        organisationsWithoutVIDs = new HashSet<>();
        vertecService.getAllZUKOrganisations().getBody().getOrganisations()
                .forEach(org -> {
                    if (org.getName().isEmpty() || org.getName() == null) return;
                    Organisation organisation =
                            new Organisation(
                                    org,
                                    syncState.getOrganisationIdMap().get(org.getVertecId()),
                                    syncState.getVertecOwnerMap().get(org.getOwnerId())
                            );
                    if (organisation.getVertecId() == null) {
                        organisationsWithoutVIDs.add(organisation);
                    } else {
                        organisationsWithVIDs.put(organisation.getVertecId(), organisation);
                    }
                    dealWithVertecOrgModifiedBySynchroniser(org);

                });

        organisationsWithVIDs.putAll(getOrganisationsBasedOnPipedrive(pipedriveState.organisationState, organisationsWithVIDs));
    }


    /**
     * returns all organisations that have been modified since the last run of the synchroniser
     * by anyone except the synchroniser itself!
     */


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

    /**
     * Thus funcion places all organisations that have been modified by the synchroniser in two seperate lists
     * We will be able to make assertions based on whether those lists contain
     */
    public void dealWithPDOrgModifiedBySynchroniser(Organisation org) {

        if (org == null) return;

        if (!syncState.isModified(Utilities.formatToVertecDate(org.getModified()))) return;

        Long modifierId = pipedriveService.getUpdateLogsFOrOrganisation(org.getPipedriveId())
                .getBody()
                .getLatestOrganisationChange()
                .getUser_id();

        if (modifierId == SynchroniserState.SYNCHRONISER_PD_USERID.longValue()) {
            if (org.getVertecId() != null) {
                syncModifiedOrganisationsWithVIDs.put(org.getVertecId(), org);
            } else {
                syncModifiedOrganisationsWithoutVIDs.add(org);
            }
        }

    }

    public void dealWithVertecOrgModifiedBySynchroniser(VPI.VertecClasses.VertecOrganisations.Organisation org) {
        if (org == null) return;

        if (!syncState.isModified(Utilities.formatToVertecDate(org.getModified()))) return;

        if (org.getModifier() != SynchroniserState.SYNCHRONISER_VERTEC_USERID.longValue()) return;

        Organisation organisation =
                new Organisation(
                        org,
                        syncState.getOrganisationIdMap().get(org.getVertecId()),
                        syncState.getVertecOwnerMap().get(org.getOwnerId())
                );

        if (organisation.getVertecId() != null) {
            syncModifiedOrganisationsWithVIDs.put(organisation.getVertecId(), organisation);
        } else {
            syncModifiedOrganisationsWithoutVIDs.add(organisation);
        }
    }

}


