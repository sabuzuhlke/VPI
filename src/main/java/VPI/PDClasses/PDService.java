package VPI.PDClasses;

import VPI.Keys.DevelopmentKeys;
import VPI.Keys.ProductionKeys;
import VPI.PDClasses.Activities.PDActivityItemsResponse;
import VPI.PDClasses.Activities.PDActivityReceived;
import VPI.PDClasses.Activities.PDActivityResponse;
import VPI.PDClasses.Activities.PDActivitySend;
import VPI.PDClasses.Contacts.*;
import VPI.PDClasses.Deals.*;
import VPI.PDClasses.HierarchyClasses.PDRelationshipReceived;
import VPI.PDClasses.HierarchyClasses.PDRelationshipResopnse;
import VPI.PDClasses.HierarchyClasses.PDRelationshipSend;
import VPI.PDClasses.Organisations.*;
import VPI.PDClasses.Updates.PDUpdateLog;
import VPI.PDClasses.Users.PDUser;
import VPI.PDClasses.Users.PDUserItemsResponse;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.*;

import static java.util.stream.Collectors.toList;

public class PDService {

    private RestTemplate restTemplate;
    private String server;
    private String apiKey;

    public PDService(String server, String apiKey) {
        this.restTemplate = new RestTemplate();
        this.apiKey = ProductionKeys.key; //= apiKey;
        this.server = server;
    }

    /**
     * Templates
     */

    private <REQ> ResponseEntity<REQ> getFromPipedrive(String uri, Class<REQ> returnType) {
        return restTemplate.exchange(
                new RequestEntity<>(HttpMethod.GET, URI.create(uri)),
                returnType);
    }

    private <REQ, RES> ResponseEntity<RES> postToPipedrive(String uri, REQ toPost, Class<RES> returnType) {
//        return restTemplate.exchange(
//                new RequestEntity<>(toPost, HttpMethod.POST, URI.create(uri)),
//                returnType);
        return null;
    }

    private <REQ, RES> ResponseEntity<RES> putToPipedrive(String uri, REQ toPut, Class<RES> returnType) {
//        return restTemplate.exchange(
//                new RequestEntity<>(toPut, HttpMethod.PUT, URI.create(uri)),
//                returnType);
        return null;
    }

    private ResponseEntity<PDDeleteResponse> deleteFromPipedrive(String uri) {
//        return restTemplate.exchange(
//                new RequestEntity<>(HttpMethod.DELETE, URI.create(uri)),
//                PDDeleteResponse.class);
        return null;
    }

    private ResponseEntity<PDBulkDeleteResponse> deleteFromPipedriveInBulk(String uri, Collection<Long> idsToDelete) {
//        PDBulkDeleteResponse.PDBulkDeletedIdsReq idsForReq = new PDBulkDeleteResponse().new PDBulkDeletedIdsReq();
//        idsForReq.setIds(idsToDelete);
//        return restTemplate.exchange(
//                new RequestEntity<Object>(idsForReq, HttpMethod.DELETE, URI.create(uri)),
//                PDBulkDeleteResponse.class);
        return null;
    }

//    public <RES> ResponseEntity<RES> getAllFromPipedrive(String entity, Class<RES> returnType) {
////TODO finish template for get all from pipedrive
//        int start = 0;
//
//        Boolean moreItems = true;
//
//        ResponseEntity<RES> res = null;
//        List<PDContactReceived> contactsRecieved = new ArrayList<>();
//
//        while (moreItems) {
//
//            res = getFromPipedrive(
//                    server + entity +"?start=" + start + "&limit=100000&" + apiKey.substring(1),
//                    returnType);
//
//
//            if (res.getBody().getData() != null) {
//                contactsRecieved.addAll(res.getBody().getData());
//                moreItems = res.getBody().getAdditional_data().getPagination().getMore_items_in_collection();
//                start += 500;
//            } else {
//                moreItems = false;
//            }
//
//        }
//        res.getBody().setData(contactsRecieved);
//        return res;
//
//
//    }

    /**
     * Deals
     */
//------------------------------------------------------------------------------------------------------------------POST
    public ResponseEntity<PDDealResponse> postDeal(PDDealSend deal) {
        try {
            return postToPipedrive(server + "deals" + apiKey, deal, PDDealResponse.class);
        } catch (Exception e) {
            System.out.println("Exception posting deal: " + deal.toPrettyJSON() + ", excpetion: " + e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    public HashMap<Long, Long> postDealList(List<PDDealSend> deals) {
        HashMap<Long, Long> dealIdMap = new HashMap<>();
        List<Long> ids = deals.stream()
                .map(this::postDeal)
                .filter(res -> res.getStatusCode() == HttpStatus.CREATED)
                .map(res -> dealIdMap.put(
                        res.getBody().getData().getV_id(),
                        res.getBody().getData().getId())
                ).collect(toList());
        return dealIdMap;
    }

//-------------------------------------------------------------------------------------------------------------------GET

    public ResponseEntity<PDDealResponse> getDeal(Long dealId) {
        return getFromPipedrive(server + "deals/" + dealId + apiKey, PDDealResponse.class);
    }

    public ResponseEntity<PDDealItemsResponse> getAllDeals() {
        int start = 0;
        Boolean moreItemsInCollection = true;

        ResponseEntity<PDDealItemsResponse> res = null;
        List<PDDealReceived> dealsReceived = new ArrayList<>();

        while (moreItemsInCollection) {

            String uri = server + "deals?start=" + start + "&limit=100000&" + apiKey.substring(1);
            res = getFromPipedrive(uri, PDDealItemsResponse.class);
            if (res.getBody().getData() != null) {
                dealsReceived.addAll(res.getBody().getData());
                moreItemsInCollection = res.getBody().getAdditional_data().getPagination().getMore_items_in_collection();

                start += 500;

            } else {
                moreItemsInCollection = false;
            }
        }

        res.getBody().setData(dealsReceived);
        return res;

    }
//-------------------------------------------------------------------------------------------------------------------PUT

    public ResponseEntity<PDDealResponse> updateDeal(PDDealSend deal) {
        return putToPipedrive(server + "deals/" + deal.getId() + apiKey, deal, PDDealResponse.class);
    }

    public Map<Long, Long> updateDealList(List<PDDealSend> pds) {
        Map<Long, Long> map = new HashMap<>();
        pds.stream()
                .map(this::updateDeal)
                .filter(response -> response.getStatusCode() == HttpStatus.OK)
                .forEach(deal -> map.put(deal.getBody().getData().getV_id(), deal.getBody().getData().getId()));
        return map;
    }

//----------------------------------------------------------------------------------------------------------------DELETE

    public ResponseEntity<PDDeleteResponse> deleteDeal(Long id) {
        return deleteFromPipedrive(server + "deals/" + id + apiKey);
    }

    public List<Long> deleteDealList(Collection<Long> idsToDelete) {
        return deleteFromPipedriveInBulk(server + "deals/" + apiKey, idsToDelete).getBody().getData().getId()
                .stream()
                .map(Long::parseLong)
                .collect(toList());
    }

    /**
     * Organisations
     */
//------------------------------------------------------------------------------------------------------------------POST
    /*
    TESTING PURPOSES ONLY
    TODO: delete and adjust tests
     */
    public ResponseEntity<PDOrganisationResponse> postOrganisation(String companyName, Integer visibleTo) {
        return postToPipedrive(
                server + "organizations" + apiKey,
                new PDOrganisationSend(companyName, visibleTo),
                PDOrganisationResponse.class);
    }

    public ResponseEntity<PDOrganisationResponse> postOrganisation(PDOrganisationSend post) {
        return postToPipedrive(server + "organizations" + apiKey, post, PDOrganisationResponse.class);
    }

    public List<Long> postOrganisationList(List<PDOrganisationSend> OrgsToPost) {
        return OrgsToPost.stream()
                .map(this::postOrganisation)
                .filter(org -> org.getStatusCode() == HttpStatus.CREATED)
                .map(org -> org.getBody().getData().getId())
                .collect(toList());
    }

//-------------------------------------------------------------------------------------------------------------------PUT

    public ResponseEntity<PDOrganisationResponse> updateOrganisation(PDOrganisationSend org) {
        return putToPipedrive(server + "organizations/" + org.getId() + apiKey, org, PDOrganisationResponse.class);
    }

    public List<Long> putOrganisationList(List<PDOrganisationSend> pds) {
        return pds.stream()
                .map(this::updateOrganisation)
                .filter(response -> response.getStatusCode() == HttpStatus.OK)
                .map(org -> org.getBody().getData().getId())
                .collect(toList());
    }

//-------------------------------------------------------------------------------------------------------------------GET

    public ResponseEntity<PDOrganisationResponse> getOrganisation(Long id) {
        return getFromPipedrive(server + "organizations/" + id + apiKey, PDOrganisationResponse.class);
    }

    public ResponseEntity<PDOrganisationItemsResponse> getAllOrganisations() {
        int start = 0;

        Boolean moreItems = true;

        ResponseEntity<PDOrganisationItemsResponse> res = null;
        List<PDOrganisationReceived> orgsRecieved = new ArrayList<>();

        while (moreItems) {
            res = getFromPipedrive(
                    server + "organizations?start=" + start + "&limit=100000&" + apiKey.substring(1),
                    PDOrganisationItemsResponse.class);

            if (res.getBody().getData() != null) {

                orgsRecieved.addAll(res.getBody().getData());
                moreItems = res.getBody().getAdditional_data().getPagination().getMore_items_in_collection();

                start += 500;
            } else {
                moreItems = false;
            }


        }
        res.getBody().setData(orgsRecieved);
        System.out.println(res.getBody());
        return res;
    }

//----------------------------------------------------------------------------------------------------------------DELETE

    public ResponseEntity<PDDeleteResponse> deleteOrganisation(Long id) {
        return deleteFromPipedrive(server + "organizations/" + id + apiKey);
    }

    public List<Long> deleteOrganisationList(List<Long> idsToDelete) {
        return deleteFromPipedriveInBulk(server + "organizations/" + apiKey, idsToDelete).getBody().getData()
                .getId().stream()
                .map(Long::parseLong)
                .collect(toList());
    }

    public List<PDDealReceived> getAllDealsForOrganisation(Long orgId) {
        int start = 0;
        int limit = 500;
        boolean moreInfo = true;

        List<PDDealReceived> deals = new ArrayList<>();

        while (moreInfo) {
            ResponseEntity<PDDealItemsResponse> res = getFromPipedrive(server + "organizations/" + orgId + "/deals" + "?start=" + start + "&limit=" + limit + "&" + apiKey.substring(1), PDDealItemsResponse.class);
            if (res.getStatusCode() == HttpStatus.OK) {
                moreInfo = res.getBody().getAdditional_data().getPagination().getMore_items_in_collection();

                deals.addAll(res.
                        getBody().
                        getData());
                start = res.getBody().getAdditional_data().getPagination().getLimit();
                limit += 500;
            }
        }
        return deals;

    }


    /**
     * CONTACTS
     */
//------------------------------------------------------------------------------------------------------------------POST
    public ResponseEntity<PDContactResponse> postContact(PDContactSend contact) {
        return postToPipedrive(server + "persons" + apiKey, contact, PDContactResponse.class);
    }

    public Map<Long, Long> postContactList(List<PDContactSend> contacts) {
        Map<Long, Long> mymap = new HashMap<>();
        List<Long> idsPosted = contacts.stream()
                .map(this::postContact)
                .filter(response -> response.getStatusCode() == HttpStatus.CREATED)
                .map(contact -> mymap.put(
                        contact.getBody().getData().getV_id(),
                        contact.getBody().getData().getId())
                )
                .collect(toList());

        return mymap;
    }

//-------------------------------------------------------------------------------------------------------------------GET

    public ResponseEntity<PDContactListReceived> getContactsForOrganisation(Long org_id) {
        return getFromPipedrive(
                server + "organizations/" + org_id + "/persons?start=0&" + apiKey.substring(1),
                PDContactListReceived.class);
    }

    public ResponseEntity<PDContactListReceived> getAllContacts() {
        int start = 0;

        Boolean moreItems = true;

        ResponseEntity<PDContactListReceived> res = null;
        List<PDContactReceived> contactsRecieved = new ArrayList<>();

        while (moreItems) {

            res = getFromPipedrive(
                    server + "persons?start=" + start + "&limit=100000&" + apiKey.substring(1),
                    PDContactListReceived.class);


            if (res.getBody().getData() != null) {
                contactsRecieved.addAll(res.getBody().getData());
                moreItems = res.getBody().getAdditional_data().getPagination().getMore_items_in_collection();
                start += 500;
            } else {
                moreItems = false;
            }

        }
        res.getBody().setData(contactsRecieved);
        return res;
    }

//----------------------------------------------------------------------------------------------------------------DELETE

    public ResponseEntity<PDDeleteResponse> deleteContact(Long id) {
        return deleteFromPipedrive(server + "persons/" + id + apiKey);
    }

    public List<Long> deleteContactList(List<Long> idsToDelete) {
        return deleteFromPipedriveInBulk(server + "persons/" + apiKey, idsToDelete).getBody().getData().getId()
                .stream()
                .map(Long::parseLong)
                .collect(toList());
    }

//-------------------------------------------------------------------------------------------------------------------PUT

    public ResponseEntity<PDContactResponse> updateContact(PDContactSend contact) {
        return putToPipedrive(server + "persons/" + contact.getId() + apiKey, contact, PDContactResponse.class);
    }

    public Map<Long, Long> putContactList(List<PDContactSend> contacts) {
        Map<Long, Long> map = new HashMap<>();
        contacts.stream()
                .map(this::updateContact)
                .filter(response -> response.getStatusCode() == HttpStatus.OK)
                .map(response -> response.getBody().getData())
                .forEach(contact -> map.put(contact.getV_id(), contact.getId()));
        return map;
    }


    /**
     * Activites
     */
//-------------------------------------------------------------------------------------------------------------------GET
    @SuppressWarnings("unused")
    public ResponseEntity<PDActivityResponse> getActivity(Long id) {
        return getFromPipedrive(server + "activities/" + id + apiKey, PDActivityResponse.class);
    }

    public List<PDActivityReceived> getAllActivitiesForUser(Long id) {
        int start = 0;
        int limit = 500;
        boolean moreInfo = true;

        List<PDActivityReceived> activities = new ArrayList<>();

        while (moreInfo) {
            ResponseEntity<PDActivityItemsResponse> res = getFromPipedrive(server + "activities?user_id=" + id + "&start=" + start + "&limit=" + limit + "&" + apiKey.substring(1), PDActivityItemsResponse.class);
            if (res.getStatusCode() == HttpStatus.OK) {
                moreInfo = res.getBody().getAdditional_data().getPagination().getMore_items_in_collection();

                if (res.getBody().getData() == null) break;

                activities.addAll(res.getBody().getData());
                start = res.getBody().getAdditional_data().getPagination().getLimit();
                limit += 500;
            }
        }
        return activities;
    }

    @SuppressWarnings("unused")
    public ResponseEntity<PDActivityItemsResponse> getAllActivitiesForDeal(Long dealId) {
        return getFromPipedrive(server + "deals/" + dealId + "/activities" + apiKey, PDActivityItemsResponse.class);
    }


    public List<PDActivityReceived> getAllActivitiesForOrganisation(Long orgId) {
        int start = 0;
        int limit = 500;
        boolean moreInfo = true;

        List<PDActivityReceived> activities = new ArrayList<>();

        while (moreInfo) {
            ResponseEntity<PDActivityItemsResponse> res = getFromPipedrive(server + "organizations/" + orgId + "/activities" + "?start=" + start + "&limit=" + limit + "&" + apiKey.substring(1), PDActivityItemsResponse.class);
            if (res.getStatusCode() == HttpStatus.OK) {
                moreInfo = res.getBody().getAdditional_data().getPagination().getMore_items_in_collection();


                activities.addAll(res.
                        getBody().
                        getData());
                start = res.getBody().getAdditional_data().getPagination().getLimit();
                limit += 500;
            }
        }
        return activities;
    }

    @SuppressWarnings("unused")
    public List<PDActivityReceived> getAllActivitiesForContact(Long contactId) {
        int start = 0;
        int limit = 500;
        boolean moreInfo = true;

        List<PDActivityReceived> activities = new ArrayList<>();

        while (moreInfo) {
            ResponseEntity<PDActivityItemsResponse> res = getFromPipedrive(server + "persons/" + contactId + "/activities" + "?start=" + start + "&limit=" + limit + "&" + apiKey.substring(1), PDActivityItemsResponse.class);
            if (res.getStatusCode() == HttpStatus.OK) {
                moreInfo = res.getBody().getAdditional_data().getPagination().getMore_items_in_collection();

                activities.addAll(res.
                        getBody().
                        getData());
                start = res.getBody().getAdditional_data().getPagination().getLimit();
                limit += 500;
            }
        }
        return activities;
    }

    public List<PDActivityReceived> getAllActivities() {
        return getAllUsers().getBody().getData()
                .stream()
                .map(PDUser::getId)
                .map(id -> {
                    int start = 0;
                    Boolean moreItemsInCollection = true;
                    ResponseEntity<PDActivityItemsResponse> res = null;
                    List<PDActivityReceived> dealsReceived = new ArrayList<>();
                    while (moreItemsInCollection) {
                        String uri = server + "activities?user_id=" + id + "&start=" + start + "&limit=100000&" + apiKey.substring(1);
                        res = getFromPipedrive(uri, PDActivityItemsResponse.class);
                        if (res.getBody().getData() != null) {
                            dealsReceived.addAll(res.getBody().getData());
                            moreItemsInCollection = res.getBody().getAdditional_data().getPagination().getMore_items_in_collection();
                            start += 500;
                        } else {
                            moreItemsInCollection = false;
                        }
                    }
                    res.getBody().setData(dealsReceived);
                    return res;
                })
                .map(ResponseEntity::getBody)
                .map(PDActivityItemsResponse::getData)
                .flatMap(Collection::stream)
                .collect(toList());


    }

//------------------------------------------------------------------------------------------------------------------POST

    @SuppressWarnings("WeakerAccess")
    public ResponseEntity<PDActivityResponse> postActivity(PDActivitySend activity) {
        try {
            return postToPipedrive(server + "activities" + apiKey, activity, PDActivityResponse.class);
        } catch (Exception e) {
            System.out.println("Couldnt post activity: " + e);
            System.out.println(activity.toPrettyJSON());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @SuppressWarnings("unused")
    public List<Long> postActivityList(List<PDActivitySend> activities) {
        return activities.stream()
                .map(this::postActivity)
                .filter(response -> response.getStatusCode() == HttpStatus.CREATED)
                .map(res -> res.getBody().getData().getId())
                .collect(toList());
    }

//-------------------------------------------------------------------------------------------------------------------PUT

    @SuppressWarnings({"unused", "WeakerAccess"})
    public ResponseEntity<PDActivityResponse> putActivity(PDActivitySend activity) {
        return putToPipedrive(
                server + "activities/" + activity.getId() + apiKey,
                activity,
                PDActivityResponse.class);
    }

    @SuppressWarnings("unused")
    public List<Long> putActivitesList(List<PDActivitySend> activities) {
        return activities.stream()
                .map(this::putActivity)
                .filter(response -> response.getStatusCode() == HttpStatus.OK)
                .map(response -> response.getBody().getData().getId())
                .collect(toList());
    }

//----------------------------------------------------------------------------------------------------------------DELETE

    @SuppressWarnings("unused")
    public ResponseEntity<PDDeleteResponse> deleteActivity(Long activityId) {
        return deleteFromPipedrive(server + "activities/" + activityId + apiKey);
    }

    @SuppressWarnings("unused")
    public List<Long> deleteActivityList(List<Long> activityIds) {
        return deleteFromPipedriveInBulk(server + "activities/" + apiKey, activityIds).getBody().getData().getId()
                .stream()
                .map(Long::parseLong)
                .collect(toList());
    }

    /**
     * Users
     */
//-------------------------------------------------------------------------------------------------------------------GET
    public ResponseEntity<PDUserItemsResponse> getAllUsers() {
        return getFromPipedrive(server + "users" + apiKey, PDUserItemsResponse.class);
    }


    /**
     * Followers
     */
//------------------------------------------------------------------------------------------------------------------POST
    public ResponseEntity<String> postFollowerToContact(PDFollower f) {
        //TODO: change res to accept new pojo instead of string (followers)
        try {
            return postToPipedrive(server + "persons/" + f.getObjectID() + "/followers" + apiKey, f, String.class);
        } catch (Exception e) {
            System.out.println("Caught Exception posting follower: " + f.toPrettyJSON());
            return new ResponseEntity<>(HttpStatus.OK);
        }
    }

    public ResponseEntity<String> postFollowerToOrganisation(PDFollower f) {
        //TODO: change res to accept new pojo instead of string (followers)
        return postToPipedrive(server + "organizations/" + f.getObjectID() + "/followers" + apiKey, f, String.class);
    }

    public ResponseEntity<String> postFollowerToDeal(PDFollower f) {
        //TODO: change res to accept new pojo instead of string (followers)
        try {
            return postToPipedrive(server + "deals/" + f.getObjectID() + "/followers" + apiKey, f, String.class);
        } catch (Exception e) {
            System.out.println("Caught Exception posting follower: " + f.toPrettyJSON());
            return new ResponseEntity<>(HttpStatus.OK);
        }
    }


    /**
     * Organisation relationships
     */
//------------------------------------------------------------------------------------------------------------------POST
    public ResponseEntity<String> postOrganisationRelationship(PDRelationshipSend relationship) {
        //TODO: change res to accept new pojo instead of string (org rel)
        try {
            return postToPipedrive(server + "organizationRelationships" + apiKey, relationship, String.class);
        } catch (Exception e) {
            System.out.println("Unable to post org rel: " + relationship.toString());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    public List<PDRelationshipReceived> getRelationships(Long Id) {
        try {
            return getFromPipedrive(server + "organizationRelationships?org_id=" + Id + "&" + apiKey.substring(1), PDRelationshipResopnse.class).getBody().getData();

        } catch (Exception e) {
            System.out.println("Unable get relationships for organisation: " + Id);
            throw new RuntimeException("Unable get relationships for organisation:" + Id);
        }
    }

    /**
     * UpdateLogs
     */
    public ResponseEntity<PDUpdateLog> getUpdateLogsFOrOrganisation(Long id) {
        ResponseEntity<PDUpdateLog> res = getFromPipedrive(server + "organizations/" + id + "/flow" + apiKey, PDUpdateLog.class);
        res.getBody().setOrgid(id);
        return  res;
    }

    public List<PDUpdateLog> getAllOrganisationUpdates() {

        List<PDUpdateLog> logsRecieved = new ArrayList<>();

        getAllOrganisations().getBody().getData().forEach(org -> {
            int start = 0;

            Boolean moreItems = true;

            ResponseEntity<PDUpdateLog> res = null;


            while (moreItems) {
                res = getFromPipedrive(
                        server + "organizations/" + org.getId() + "/flow" + apiKey,
                        PDUpdateLog.class);

                if (res.getBody().getData() != null) {
                    res.getBody().setOrgid(org.getId());
                    logsRecieved.add(res.getBody());
                    moreItems = res.getBody().getAdditional_data().getPagination().getMore_items_in_collection();

                    start += 500;
                } else {
                    moreItems = false;
                }


            }
        });

        return logsRecieved;

    }


    /**
     * UTILITIES
     */
//-----------------------------------------------------------------------------------------------------------------CLEAR
    public void clearPD(/*List<Long> orgsToKeep, List<Long> contsToKeep, List<Long>  dealsToKeep*/) {

        /**
         * ORganisations do not need to be cleared atm as they work correctly
         */

        List<Long> orgsToDel = getAllOrganisations().getBody().getData()
                .stream()
                //.filter(res -> res.getV_id() != null)
                //.filter(org -> !orgsToKeep.contains(org.getId()))
                .map(PDOrganisationReceived::getId)
                .collect(toList());
        System.out.println("Found " + orgsToDel.size() + " organisationState to delete");

        /**
         * Keep contacts as they were already matched up and wed loose a lot of data
         */
        List<Long> contsToDel = getAllContacts().getBody().getData()
                .stream()
                //.filter(res -> res.getV_id() != null )
                //.filter(contact -> !contsToKeep.contains(contact.getId()))
                .map(PDContactReceived::getId)
                .collect(toList());
        System.out.println("Found " + contsToDel.size() + " contacts to delete");

        List<Long> dealsToDel = getAllDeals().getBody().getData()
                .stream()
                //.filter(res -> res.getV_id() != null)
                //.filter(deal -> !dealsToKeep.contains(deal.getId()))
                .map(PDDealReceived::getId)
                .collect(toList());
        System.out.println("Found " + dealsToDel.size() + " deals to delete");

        List<Long> activitiesToDel = getAllActivities().stream()
                //.filter(act -> VertecSynchroniser.extractVID(act.getNote()) != -1L)
                .map(PDActivityReceived::getId)
                .collect(toList());
        System.out.println("Found " + activitiesToDel.size() + " Activities to delete");


        List<Long> currentDelList;

        int i = orgsToDel.size();
        System.out.println("Deleting organisationState...");
        while (i >= 100) {
            currentDelList = new ArrayList<>();

            for (int j = 0; j < 100; j++) {
                currentDelList.add(orgsToDel.get(i - j - 1));
            }
            deleteOrganisationList(currentDelList);
            i = i - 100;
        }
        if (orgsToDel.size() > 0) {
            currentDelList = new ArrayList<>();
            for (int j = 0; j < orgsToDel.size(); j++) {
                currentDelList.add(orgsToDel.get(j));
            }
            deleteOrganisationList(orgsToDel);
        }

        System.out.println("Deleting contacts...");

        i = contsToDel.size();
        while (i >= 100) {
            currentDelList = new ArrayList<>();

            for (int j = 0; j < 100; j++) {
                currentDelList.add(contsToDel.get(i - j - 1));
            }
            deleteContactList(currentDelList);
            i = i - 100;
        }
        if (contsToDel.size() > 0) {
            currentDelList = new ArrayList<>();
            for (int j = 0; j < contsToDel.size(); j++) {
                currentDelList.add(contsToDel.get(j));
            }
            deleteContactList(contsToDel);
        }

        System.out.println("Deleting deals...");
        i = dealsToDel.size();
        while (i >= 100) {
            currentDelList = new ArrayList<>();

            for (int j = 0; j < 100; j++) {
                currentDelList.add(dealsToDel.get(i - j - 1));
            }
            deleteDealList(currentDelList);
            i = i - 100;
        }
        if (dealsToDel.size() > 0) {
            currentDelList = new ArrayList<>();
            for (int j = 0; j < dealsToDel.size(); j++) {
                currentDelList.add(dealsToDel.get(j));
            }
            deleteDealList(dealsToDel);
        }


        System.out.println("Deleting Activities...");

        i = activitiesToDel.size();
        while (i >= 100) {
            currentDelList = new ArrayList<>();

            for (int j = 0; j < 100; j++) {
                currentDelList.add(activitiesToDel.get(i - j - 1));
            }
            deleteActivityList(currentDelList);
            i = i - 100;
        }
        if (activitiesToDel.size() > 0) {
            currentDelList = new ArrayList<>();
            for (int j = 0; j < activitiesToDel.size(); j++) {
                currentDelList.add(activitiesToDel.get(j));
            }
            deleteActivityList(activitiesToDel);
        }
    }




}


