package VPI.PDClasses;

import VPI.PDClasses.Activities.PDActivityItemsResponse;
import VPI.PDClasses.Activities.PDActivityResponse;
import VPI.PDClasses.Activities.PDActivitySend;
import VPI.PDClasses.Contacts.*;
import VPI.PDClasses.Deals.*;
import VPI.PDClasses.Organisations.*;
import VPI.PDClasses.Users.PDUserItemsResponse;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

public class PDService {

    private RestTemplate restTemplate;
    private String server;
    private String apiKey;

    public PDService(String server, String apiKey) {
        this.restTemplate = new RestTemplate();
        this.apiKey = apiKey;
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
        return restTemplate.exchange(
                new RequestEntity<>(toPost, HttpMethod.POST, URI.create(uri)),
                returnType);
    }

    private <REQ, RES> ResponseEntity<RES> putToPipedrive(String uri, REQ toPut, Class<RES> returnType){
        return restTemplate.exchange(
                new RequestEntity<>(toPut, HttpMethod.PUT, URI.create(uri)),
                returnType);
    }

    private ResponseEntity<PDDeleteResponse> deleteFromPipedrive(String uri) {
        return restTemplate.exchange(
                new RequestEntity<>(HttpMethod.DELETE, URI.create(uri)),
                PDDeleteResponse.class);
    }

    private ResponseEntity<PDBulkDeleteResponse> deleteFromPipedriveInBulk(String uri, List<Long> idsToDelete) {
        PDBulkDeleteResponse.PDBulkDeletedIdsReq idsForReq = new PDBulkDeleteResponse().new PDBulkDeletedIdsReq();
        idsForReq.setIds(idsToDelete);
        return restTemplate.exchange(
                new RequestEntity<Object>(idsForReq, HttpMethod.DELETE, URI.create(uri)),
                PDBulkDeleteResponse.class);
    }

  //  public <RES> ResponseEntity<RES> getAllFromPipedrive(String entity, Class<RES> returnType) {
//TODO finish template for get all from pipedrive
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
        return postToPipedrive(server + "deals" + apiKey, deal, PDDealResponse.class);
    }

    public List<Long> postDealList(List<PDDealSend> deals) {
        return deals.stream()
                .map(this::postDeal)
                .filter(res -> res.getStatusCode() == HttpStatus.CREATED)
                .map(res -> res.getBody().getData().getId())
                .collect(toList());
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

        while(moreItemsInCollection){

            String uri = server + "deals?start=" + start + "&limit=100000&" + apiKey.substring(1);
            res = getFromPipedrive(uri, PDDealItemsResponse.class);
            if(res.getBody().getData() != null){
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

    public List<Long> updateDealList(List<PDDealSend> pds) {
        return pds.stream()
                .map(this::updateDeal)
                .filter(response -> response.getStatusCode() == HttpStatus.OK)
                .map(deal -> deal.getBody().getData().getId())
                .collect(toList());
    }

//----------------------------------------------------------------------------------------------------------------DELETE

    public ResponseEntity<PDDeleteResponse> deleteDeal(Long id) {
        return deleteFromPipedrive(server + "deals/" + id + apiKey);
    }

    public List<Long> deleteDealList(List<Long> idsToDelete) {
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

    public ResponseEntity<PDOrganisationResponse> updateOrganisation(PDOrganisationSend org){
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

    public ResponseEntity<PDOrganisationItemsResponse> getAllOrganisations(){
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
        return res;
    }

//----------------------------------------------------------------------------------------------------------------DELETE

    public ResponseEntity<PDDeleteResponse> deleteOrganisation(Long id){
        return deleteFromPipedrive(server + "organizations/" + id + apiKey);
    }

    public List<Long> deleteOrganisationList(List<Long> idsToDelete) {
        return deleteFromPipedriveInBulk(server + "organizations/" + apiKey,idsToDelete).getBody().getData()
                .getId().stream()
                .map(Long::parseLong)
                .collect(toList());
    }


    /**
     * CONTACTS
     */
//------------------------------------------------------------------------------------------------------------------POST

    public ResponseEntity<PDContactResponse> postContact(PDContactSend contact) {
        return postToPipedrive(server + "persons" + apiKey, contact, PDContactResponse.class);
    }

    public Map<Long, Long> postContactList(List<PDContactSend> contacts){
        Map<Long, Long> map = new HashMap<>();
        contacts.stream()
                .map(this::postContact)
                .filter(response -> response.getStatusCode() == HttpStatus.CREATED)
                .map(contact -> map.put(
                        contact.getBody().getData().getV_id(),
                        contact.getBody().getData().getId()));
        return map;
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

    public List<Long> deleteContactList(List<Long> idsToDelete){
        return deleteFromPipedriveInBulk(server + "persons/" + apiKey, idsToDelete).getBody().getData().getId()
                .stream()
                .map(Long::parseLong)
                .collect(toList());
    }

//-------------------------------------------------------------------------------------------------------------------PUT

    public ResponseEntity<PDContactResponse> updateContact(PDContactSend contact){
        return putToPipedrive(server + "persons/" + contact.getId() + apiKey, contact, PDContactResponse.class);
    }

    public Map<Long, Long> putContactList(List<PDContactSend> contacts){
        Map<Long, Long> map = new HashMap<>();
        contacts.stream()
                .map(this::updateContact)
                .filter(response -> response.getStatusCode() == HttpStatus.OK)
                .map(response -> response.getBody().getData())
                .map(contact -> map.put(contact.getV_id(), contact.getId()));
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

    @SuppressWarnings("unused")
    public ResponseEntity<PDActivityItemsResponse> getAllActivitiesForDeal(Long dealId) {
        return getFromPipedrive(server + "deals/" + dealId + "/activities" + apiKey, PDActivityItemsResponse.class);
    }

    @SuppressWarnings("unused")
    public ResponseEntity<PDActivityItemsResponse> getAllActivitiesForOrganisation(Long orgId) {
        return getFromPipedrive(server + "organizations/" + orgId + "/activities" + apiKey, PDActivityItemsResponse.class);
    }

    @SuppressWarnings("unused")
    public ResponseEntity<PDActivityItemsResponse> getAllActivitiesForContact(Long contactId) {
        return getFromPipedrive(server + "persons/" + contactId + "/activities" + apiKey, PDActivityItemsResponse.class);
    }

//------------------------------------------------------------------------------------------------------------------POST

    @SuppressWarnings("WeakerAccess")
    public ResponseEntity<PDActivityResponse> postActivity(PDActivitySend activity) {
        return postToPipedrive(server + "activities" + apiKey, activity, PDActivityResponse.class);
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
        return getFromPipedrive(server + "users"+ apiKey, PDUserItemsResponse.class);
    }


    /**
     * Followers
     */
//------------------------------------------------------------------------------------------------------------------POST
    public ResponseEntity<String> postFollower(PDFollower f){
        //TODO: change res to accept new pojo instead of string (followers)
        return postToPipedrive(server + "persons/"+ f.getContactID() + "/followers" + apiKey, f, String.class);
    }


    /**
     * Organisation relationships
     */
//------------------------------------------------------------------------------------------------------------------POST
    public ResponseEntity<String> postOrganisationRelationship(PDRelationship relationship) {
        //TODO: change res to accept new pojo instead of string (org rel)
        return postToPipedrive(server + "organizationRelationships" + apiKey, relationship, String.class);
    }


    /**
     * UTILITIES
     */
//-----------------------------------------------------------------------------------------------------------------CLEAR
    public void clearPD(List<Long> orgsToKeep, List<Long> contsToKeep){

        List<Long> orgsToDel = getAllOrganisations().getBody().getData()
                .stream()
                .filter(org -> !orgsToKeep.contains(org.getId()))
                .map(PDOrganisationReceived::getId)
                .collect(toList());

        List<Long> contsToDel = getAllContacts().getBody().getData()
                .stream()
                .filter(contact -> !contsToKeep.contains(contact.getId()))
                .map(PDContactReceived::getId)
                .collect(toList());

        if(!orgsToDel.isEmpty()) deleteOrganisationList(orgsToDel);
        if(!contsToDel.isEmpty()) deleteContactList(contsToDel);
    }

}


