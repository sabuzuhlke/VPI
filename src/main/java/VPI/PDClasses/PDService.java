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
import java.util.stream.Collectors;

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
//
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
//
//        ResponseEntity<PDDealResponse> res;
//        List<Long> idsPosted = new ArrayList<>();
//
//        for(PDDealSend d : deals){
//            res = postDeal(d);
//            if(res.getStatusCode() == HttpStatus.CREATED){
//                idsPosted.add(res.getBody().getData().getId());
//            } else {
//                System.out.println("Could not create deal, server response: " + res.getStatusCode().toString());
//            }
//        }
//        return idsPosted;

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


//        ResponseEntity<PDDealResponse> res;
//        List<Long> idsPutted = new ArrayList<>();
//        for(PDDealSend deal : pds) {
//            res = updateDeal(deal);
//            if (res.getStatusCode() == HttpStatus.OK) {
//                idsPutted.add(res.getBody().getData().getId());
//            } else {
//                System.out.println("Could not update deal, server response: " + res.getStatusCode().toString());
//            }
//        }
//        return idsPutted;
    }

//----------------------------------------------------------------------------------------------------------------DELETE

    public ResponseEntity<PDDeleteResponse> deleteDeal(Long id) {
        return deleteFromPipedrive(server + "deals/" + id + apiKey);
//
//        RequestEntity<String> req;
//        ResponseEntity<PDDeleteResponse> res = null;
//
//        try{
//            req = new RequestEntity<>(HttpMethod.DELETE, new URI(uri));
//            res = restTemplate.exchange(req,PDDeleteResponse.class);
//
//        } catch (Exception e){
//            System.out.println("Exception on DELETEing deal: " + e);
//        }
//
//        return res;
    }

    public List<Long> deleteDealList(List<Long> idsToDelete) {


        String uri = server + "deals/" + apiKey;

        return deleteFromPipedriveInBulk(uri, idsToDelete).getBody().getData().getId()
                .stream()
                .map(Long::parseLong)
                .collect(toList());

//
//
//        List<Long> idsDeleted = new ArrayList<>();
//        List<String> idsDeletedAsString = new ArrayList<>();
//        RequestEntity<PDBulkDeleteResponse.PDBulkDeletedIdsReq> req;
//        ResponseEntity<PDBulkDeleteResponse> res;
//
//        try {
//
//            req = new RequestEntity<>(idsForReq, HttpMethod.DELETE, new URI(uri));
//            res = restTemplate.exchange(req, PDBulkDeleteResponse.class);
//            idsDeletedAsString = res.getBody().getData().getId();
//            System.out.println(res.getBody().getData().getId().size());
//
//            for(String s : idsDeletedAsString) {
//                idsDeleted.add(Long.parseLong(s));
//            }
//
//        } catch (Exception e) {
//            System.out.println(e.toString());
//        }
//
//        return idsDeleted;
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
        return postToPipedrive(server + "organizations" + apiKey, new PDOrganisationSend(companyName, visibleTo), PDOrganisationResponse.class);


//        try {
//            PDOrganisationSend post = new PDOrganisationSend(companyName, visibleTo);
//
//            req = new RequestEntity<>(post, HttpMethod.POST, new URI(uri));
//            res = restTemplate.exchange(req, PDOrganisationResponse.class);
//
//        } catch (Exception e) {
//            System.out.println(e.toString());
//        }
//        return res;
    }

    public ResponseEntity<PDOrganisationResponse> postOrganisation(PDOrganisationSend post) {
        return postToPipedrive(server + "organizations" + apiKey, post, PDOrganisationResponse.class);
//        try {
//
//            req = new RequestEntity<>(post, HttpMethod.POST, new URI(uri));
//            res = restTemplate.exchange(req, PDOrganisationResponse.class);
//
//        } catch (Exception e) {
//            System.out.println(e.toString() + "Respone: " + req);
//        }
//        return res;
    }

    public List<Long> postOrganisationList(List<PDOrganisationSend> OrgsToPost) {

        return OrgsToPost.stream()
                .map(this::postOrganisation)
                .filter(org -> org.getStatusCode() == HttpStatus.CREATED)
                .map(org -> org.getBody().getData().getId())
                .collect(toList());



//        ResponseEntity<PDOrganisationResponse> res;
//        List<Long> idsPosted = new ArrayList<>();
//        for(PDOrganisationSend org : OrgsToPost) {
//            res = postOrganisation(org);
//            if (res.getStatusCode() == HttpStatus.CREATED) {
//                idsPosted.add(res.getBody().getData().getId());
//            } else {
//                System.out.println("Could not create organisation, server response: " + res.getStatusCode().toString());
//            }
//        }
//        return idsPosted;
    }

//-------------------------------------------------------------------------------------------------------------------PUT

    public ResponseEntity<PDOrganisationResponse> updateOrganisation(PDOrganisationSend org){
        return putToPipedrive(server + "organizations/" + org.getId() + apiKey, org, PDOrganisationResponse.class);
//
//        RequestEntity<PDOrganisationSend> req;
//        ResponseEntity<PDOrganisationResponse> res = null;
//
//        try{
//            req = new RequestEntity<>(org,HttpMethod.PUT,new URI(uri));
//            res = restTemplate.exchange(req, PDOrganisationResponse.class);
//
//        } catch (Exception e) {
//            System.out.println(e.toString());
//        }
//        return res;
    }

    public List<Long> putOrganisationList(List<PDOrganisationSend> pds) {
        return pds.stream()
                .map(this::updateOrganisation)
                .filter(response -> response.getStatusCode() == HttpStatus.OK)
                .map(org -> org.getBody().getData().getId())
                .collect(toList());
//
//
//        ResponseEntity<PDOrganisationResponse> res;
//        List<Long> idsPutted = new ArrayList<>();
//        for(PDOrganisationSend org : pds) {
//            res = updateOrganisation(org);
//            if (res.getStatusCode() == HttpStatus.OK) {
//                idsPutted.add(res.getBody().getData().getId());
//            } else {
//                System.out.println("Could not update organisation, server response: " + res.getStatusCode().toString());
//            }
//        }
//        return idsPutted;
    }

//-------------------------------------------------------------------------------------------------------------------GET

    public ResponseEntity<PDOrganisationResponse> getOrganisation(Long id) {
        return getFromPipedrive(server + "organizations/" + id + apiKey, PDOrganisationResponse.class);
//
//        RequestEntity<PDOrganisationReceived> req;
//        ResponseEntity<PDOrganisationResponse> res = null;
//
//        try {
//
//            req = new RequestEntity<>(HttpMethod.GET, new URI(uri));
//            res = restTemplate.exchange(req, PDOrganisationResponse.class);
//
//        } catch (Exception e) {
//            System.out.println(e.toString());
//        }
//        return res;
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
//
//        RequestEntity<PDOrganisationReceived> req;
//        ResponseEntity<PDDeleteResponse> res = null;
//
//        try {
//
//            req = new RequestEntity<>(HttpMethod.DELETE, new URI(uri));
//            res = restTemplate.exchange(req, PDDeleteResponse.class);
//
//        } catch (Exception e) {
//            System.out.println("DELETE Exception: " + e.toString());
//        }
//        return res;
    }

    public List<Long> deleteOrganisationList(List<Long> idsToDelete) {
        return deleteFromPipedriveInBulk(server + "organizations/" + apiKey,idsToDelete).getBody().getData()
                .getId().stream()
                .map(Long::parseLong)
                .collect(toList());

//        List<Long> idsDeleted = new ArrayList<>();
//        List<String> idsDeletedAsString;
//        PDBulkDeleteResponse.PDBulkDeletedIdsReq idsForReq = new PDBulkDeleteResponse().new PDBulkDeletedIdsReq();
//        idsForReq.setIds(idsToDelete);
//
//        RequestEntity<PDBulkDeleteResponse.PDBulkDeletedIdsReq> req;
//        ResponseEntity<PDBulkDeleteResponse> res;
//
//
//        try {
//
//            req = new RequestEntity<>(idsForReq, HttpMethod.DELETE, new URI(uri));
//            res = restTemplate.exchange(req, PDBulkDeleteResponse.class);
//            idsDeletedAsString = res.getBody().getData().getId();
//            System.out.println(res.getBody().getData().getId().size());
//
//            for(String s : idsDeletedAsString) {
//                idsDeleted.add(Long.parseLong(s));
//            }
//
//        } catch (Exception e) {
//            System.out.println(e.toString());
//        }
//
//        return idsDeleted;
    }


    /**
     * CONTACTS
     */
//------------------------------------------------------------------------------------------------------------------POST

    public ResponseEntity<PDContactResponse> postContact(PDContactSend contact) {
        return postToPipedrive(server + "persons" + apiKey, contact, PDContactResponse.class);
//        RequestEntity<PDContactSend> req;
//        ResponseEntity<PDContactResponse> res = null;
//
//        try {
//            req = new RequestEntity<>(contact, HttpMethod.POST, new URI(uri));
//            res = restTemplate.exchange(req, PDContactResponse.class);
//
//            for(Long f : contact.getFollowers()){
//                postFollower(new PDFollower(res.getBody().getData().getId(), f));
//            }
//
//        } catch (Exception e) {
//            System.out.println("OOPS" + e.toString() + " RESPONSE: " + res);
//        }
//
//        return res;
    }

    public Map<Long, Long> postContactList(List<PDContactSend> contacts){
        Map<Long, Long> map = new HashMap<>();
        //ResponseEntity<PDContactResponse> res;

        contacts.stream()
                .map(this::postContact)
                .filter(response -> response.getStatusCode() == HttpStatus.CREATED)
                .map(contact -> map.put(
                        contact.getBody().getData().getV_id(),
                        contact.getBody().getData().getId()));
//
//        for(PDContactSend p : contacts){
//            res = postContact(p);
//            if (res.getStatusCode() == HttpStatus.CREATED) {
//
//                map.put(p.getV_id(), res.getBody().getData().getId());
//
//            } else {
//                System.out.println("Could not create contact, server response: " + res.getStatusCode().toString());
//            }
//        }
        return map;

    }

//-------------------------------------------------------------------------------------------------------------------GET

    public ResponseEntity<PDContactListReceived> getContactsForOrganisation(Long org_id) {
        return getFromPipedrive(
                server + "organizations/" + org_id + "/persons?start=0&" + apiKey.substring(1),
                PDContactListReceived.class);
//
//        try {
//
//            req = new RequestEntity<>(HttpMethod.GET, new URI(uri));
//            res = restTemplate.exchange(req, PDContactListReceived.class);
//
//        } catch (Exception e) {
//            System.out.println(e.toString());
//        }
//
//        return res;
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
//        RequestEntity<String> req;
//        ResponseEntity<PDDeleteResponse> res = null;
//        String uri;
//
        return deleteFromPipedrive(server + "persons/" + id + apiKey);
//
//        try {
//
//            req = new RequestEntity<>(HttpMethod.DELETE, new URI(uri));
//            res = restTemplate.exchange(req, PDDeleteResponse.class);
//
//        } catch (Exception e) {
//            System.out.println(e.toString());
//        }
//
//        return res;
    }

    public List<Long> deleteContactList(List<Long> idsToDelete){
//        List<Long> idsDeleted = new ArrayList<>();
//        List<String> idsDeletedAsString;
//        PDBulkDeleteResponse.PDBulkDeletedIdsReq idsForReq = new PDBulkDeleteResponse().new PDBulkDeletedIdsReq();
//        idsForReq.setIds(idsToDelete);
//
//        RequestEntity<PDBulkDeleteResponse.PDBulkDeletedIdsReq> req = null;
//        ResponseEntity<PDBulkDeleteResponse> res;
//
//
//        try {

        String uri = server + "persons/" + apiKey;

        return deleteFromPipedriveInBulk(uri, idsToDelete).getBody().getData().getId()
                .stream()
                .map(Long::parseLong)
                .collect(toList());
//
//            req = new RequestEntity<>(idsForReq, HttpMethod.DELETE, new URI(uri));
//            res = restTemplate.exchange(req, PDBulkDeleteResponse.class);
//            idsDeletedAsString = res.getBody().getData().getId();
//            System.out.println(res.getBody().getData().getId().size());
//
//            for(String s : idsDeletedAsString) {
//                idsDeleted.add(Long.parseLong(s));
//            }
//
//        } catch (Exception e) {
//            System.out.println(e.toString() + "REQ: " + req);
//        }
//
//        return idsDeleted;
    }

//-------------------------------------------------------------------------------------------------------------------PUT

    public ResponseEntity<PDContactResponse> updateContact(PDContactSend contact){
//        RequestEntity<PDContactSend> req;
//        ResponseEntity<PDContactResponse> res = null;
//        Long id = contact.getId();

        return putToPipedrive(server + "persons/" + contact.getId() + apiKey, contact, PDContactResponse.class);
//
//        try {
//
//            req = new RequestEntity<>(contact, HttpMethod.PUT, new URI(uri));
//            res = restTemplate.exchange(req, PDContactResponse.class);
//
//        } catch (Exception e) {
//            System.out.println(e.toString());
//        }
//
//        return res;
    }

    public Map<Long, Long> putContactList(List<PDContactSend> contacts){
//        ResponseEntity<PDContactResponse> res;

        Map<Long, Long> map = new HashMap<>();

        contacts.stream()
                .map(this::updateContact)
                .filter(response -> response.getStatusCode() == HttpStatus.OK)
                .map(response -> response.getBody().getData())
                .map(contact -> map.put(contact.getV_id(), contact.getId()));
//
//        for(PDContactSend c : contacts){
//            res = updateContact(c);
//            if(res.getStatusCode() == HttpStatus.OK){
//
//                map.put(c.getV_id(), res.getBody().getData().getId());
//            }else{
//                System.out.println("Could not UPDATE contact, server responded: " + res.getStatusCode());
//            }
//        }
        return map;
    }


    /**
     * Activites
     */
//-------------------------------------------------------------------------------------------------------------------GET

    @SuppressWarnings("unused")
    public ResponseEntity<PDActivityResponse> getActivity(Long id) {
        return getFromPipedrive(server + "activities/" + id + apiKey, PDActivityResponse.class);
//        RequestEntity<String> req;
//        ResponseEntity<PDActivityResponse> res = null;
//
//        try {
//
//            req = new RequestEntity<>(HttpMethod.GET, new URI(uri));
//            res = restTemplate.exchange(req, PDActivityResponse.class);
//
//
//        } catch (Exception e) {
//            System.out.println("EXCEPTION GETTING ACTIVITY: " + e);
//        }
//
//        return res;
    }

    @SuppressWarnings("unused")
    public ResponseEntity<PDActivityItemsResponse> getAllActivitiesForDeal(Long dealId) {
        return getFromPipedrive(server + "deals/" + dealId + "/activities" + apiKey, PDActivityItemsResponse.class);
//        RequestEntity<String> req;
//        ResponseEntity<PDActivityItemsResponse> res = null;
//
//        try {
//
//            req = new RequestEntity<>(HttpMethod.GET, new URI(uri));
//            res = restTemplate.exchange(req, PDActivityItemsResponse.class);
//        } catch (Exception e) {
//            System.out.println("ERROR GETTING ACTIVITIES FOR DEAL: " + e);
//        }
//
//        return res;
    }

    @SuppressWarnings("unused")
    public ResponseEntity<PDActivityItemsResponse> getAllActivitiesForOrganisation(Long orgId) {
        return getFromPipedrive(server + "organizations/" + orgId + "/activities" + apiKey, PDActivityItemsResponse.class);
//        RequestEntity<String> req;
//        ResponseEntity<PDActivityItemsResponse> res = null;
//
//
//        try {
//
//            req = new RequestEntity<>(HttpMethod.GET, new URI(uri));
//            res = restTemplate.exchange(req, PDActivityItemsResponse.class);
//        } catch (Exception e) {
//            System.out.println("ERROR GETTING ACTIVITIES FOR ORGANISATION: " + e);
//        }
//
//        return res;
    }

    @SuppressWarnings("unused")
    public ResponseEntity<PDActivityItemsResponse> getAllActivitiesForContact(Long contactId) {

        return getFromPipedrive(server + "persons/" + contactId + "/activities" + apiKey, PDActivityItemsResponse.class);
//        RequestEntity<String> req;
//        ResponseEntity<PDActivityItemsResponse> res = null;
//
//        try {
//
//            req = new RequestEntity<>(HttpMethod.GET, new URI(uri));
//            res = restTemplate.exchange(req, PDActivityItemsResponse.class);
//        } catch (Exception e) {
//            System.out.println("ERROR GETTING ACTIVITIES FOR CONTACT: " + e);
//        }
//
//        return res;
    }

//------------------------------------------------------------------------------------------------------------------POST

    @SuppressWarnings("WeakerAccess")
    public ResponseEntity<PDActivityResponse> postActivity(PDActivitySend activity) {

        return postToPipedrive(server + "activities" + apiKey, activity, PDActivityResponse.class);
//        RequestEntity<PDActivitySend> req;
//        ResponseEntity<PDActivityResponse> res = null;
//
//        try {
//
//            req = new RequestEntity<>(activity, HttpMethod.POST, new URI(uri));
//            res = restTemplate.exchange(req, PDActivityResponse.class);
//
//        } catch (Exception e) {
//            System.out.println("EXCEPTION POSTING ACTIVITY: " + e);
//        }
//
//        return res;
    }

    @SuppressWarnings("unused")
    public List<Long> postActivityList(List<PDActivitySend> activities) {

        return activities.stream()
                .map(this::postActivity)
                .filter(response -> response.getStatusCode() == HttpStatus.CREATED)
                .map(res -> res.getBody().getData().getId())
                .collect(toList());
//        List<Long> idsPosted = new ArrayList<>();
//
//        for (PDActivitySend activity : activities) {
//
//            Long id = postActivity(activity).getBody().getData().getId();
//            idsPosted.add(id);
//
//        }
//
//        return idsPosted;
    }

//-------------------------------------------------------------------------------------------------------------------PUT

    @SuppressWarnings({"unused", "WeakerAccess"})
    public ResponseEntity<PDActivityResponse> putActivity(PDActivitySend activity) {
//
//        RequestEntity<PDActivitySend> req;
//        ResponseEntity<PDActivityResponse> res = null;

        return putToPipedrive(
                server + "activities/" + activity.getId() + apiKey,
                activity,
                PDActivityResponse.class);
//
//        try {
//
//            req = new RequestEntity<>(activity, HttpMethod.PUT, new URI(uri));
//            res = restTemplate.exchange(req, PDActivityResponse.class);
//
//        } catch (Exception e) {
//            System.out.println("EXCEPTION PUTTING DEAL: " + e);
//        }
//
//        return res;
    }

    @SuppressWarnings("unused")
    public List<Long> putActivitesList(List<PDActivitySend> activities) {

        return activities.stream()
                .map(this::putActivity)
                .filter(response -> response.getStatusCode() == HttpStatus.OK)
                .map(response -> response.getBody().getData().getId())
                .collect(toList());
//
//        List<Long> idsPutted = new ArrayList<>();
//
//        for (PDActivitySend activity : activities) {
//
//            Long id = putActivity(activity).getBody().getData().getId();
//            idsPutted.add(id);
//        }
//
//        return idsPutted;
    }

//----------------------------------------------------------------------------------------------------------------DELETE

    @SuppressWarnings("unused")
    public ResponseEntity<PDDeleteResponse> deleteActivity(Long activityId) {
//        RequestEntity<String> req;
//        ResponseEntity<PDDeleteResponse> res = null;
//        String uri;
//
        return deleteFromPipedrive(server + "activities/" + activityId + apiKey);

//        try {
//
//            req = new RequestEntity<>(HttpMethod.DELETE, new URI(uri));
//            res = restTemplate.exchange(req, PDDeleteResponse.class);
//
//
//        } catch (Exception e) {
//            System.out.println("EXCEPTION DELETING ACTIVITY: " + e);
//        }
//
//        return res;
    }

    @SuppressWarnings("unused")
    public List<Long> deleteActivityList(List<Long> activityIds) {
//        List<Long> idsDeleted = new ArrayList<>();
//        List<String> idsDeletedAsString;
//        PDBulkDeleteResponse.PDBulkDeletedIdsReq idsForReq = new PDBulkDeleteResponse().new PDBulkDeletedIdsReq();
//        idsForReq.setIds(activityIds);
//
//        RequestEntity<PDBulkDeleteResponse.PDBulkDeletedIdsReq> req = null;
//        ResponseEntity<PDBulkDeleteResponse> res;
//
//
//        try {

        return deleteFromPipedriveInBulk(server + "activities/" + apiKey, activityIds).getBody().getData().getId()
                .stream()
                .map(Long::parseLong)
                .collect(toList());

//
//            req = new RequestEntity<>(idsForReq, HttpMethod.DELETE, new URI(uri));
//            res = restTemplate.exchange(req, PDBulkDeleteResponse.class);
//            idsDeletedAsString = res.getBody().getData().getId();
//
//            for(String s : idsDeletedAsString) {
//                idsDeleted.add(Long.parseLong(s));
//            }
//
//        } catch (Exception e) {
//            System.out.println("ERROR BULK DELETING ACTIVITIES" + e.toString() + "REQ: " + req);
//        }
//
//        return idsDeleted;
    }

    /**
     * Users
     */
//-------------------------------------------------------------------------------------------------------------------GET

    public ResponseEntity<PDUserItemsResponse> getAllUsers() {

//        RequestEntity<String> req;
//        ResponseEntity<PDUserItemsResponse> res = null;

        return getFromPipedrive(server + "users"+ apiKey, PDUserItemsResponse.class);
//
//        try {
//
//            req = new RequestEntity<>(HttpMethod.GET, new URI(uri));
//            res = restTemplate.exchange(req, PDUserItemsResponse.class);
//
//        } catch (Exception e) {
//            System.out.println(e.toString());
//        }
//
//        return res;
    }


    /**
     * Followers
     */
//------------------------------------------------------------------------------------------------------------------POST
    public ResponseEntity<String> postFollower(PDFollower f){
        //TODO: change res to accept new pojo instead of string (followers)
//
//        RequestEntity<PDFollower> req;
//        ResponseEntity<String> res = null;

        return postToPipedrive(server + "persons/"+ f.getContactID() + "/followers" + apiKey, f, String.class);

//        try{
//            req = new RequestEntity<>(f,HttpMethod.POST, new URI(uri));
//            res = restTemplate.exchange(req,String.class);
//
//        } catch (HttpClientErrorException e) {
//            System.out.println("ERROR POSTING FOLLOWER: " + e
//                    + "userID: " + f.getUserID() + ", contactIdP: " + f.getContactID());
//        } catch(Exception e){
//            System.out.println("ERROR on posting follower " + e);
//        }
//        return res;
    }


    /**
     * Organisation relationships
     */
//------------------------------------------------------------------------------------------------------------------POST
    public ResponseEntity<String> postOrganisationRelationship(PDRelationship relationship) {
        //TODO: change res to accept new pojo instead of string (org rel)
//
//        RequestEntity<PDRelationship> req;
//        ResponseEntity<String> res = null;
//
//        String uri;

        return postToPipedrive(server + "organizationRelationships" + apiKey, relationship, String.class);

//        try {
//
//            req = new RequestEntity<>(relationship, HttpMethod.POST, new URI(uri));
//            res = restTemplate.exchange(req, String.class);
//
//        } catch (Exception e) {
//            System.out.println("EXCEPTION IN POSTING ORGANISATION RELATIONSHIP: " + e);
//        }
//
//        return res;

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


