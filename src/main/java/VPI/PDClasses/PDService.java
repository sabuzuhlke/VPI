package VPI.PDClasses;

import VPI.PDClasses.Activities.PDActivity;
import VPI.PDClasses.Activities.PDActivityItemsResponse;
import VPI.PDClasses.Activities.PDActivityResponse;
import VPI.PDClasses.Contacts.*;
import VPI.PDClasses.Deals.*;
import VPI.PDClasses.Organisations.*;
import VPI.PDClasses.Users.PDUserItemsResponse;
import com.sun.javafx.collections.MappingChange;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
     * Deals
     */
//------------------------------------------------------------------------------------------------------------------POST

    public ResponseEntity<PDDealResponse> postDeal(PDDealSend deal) {
        RequestEntity<PDDealSend> req;
        ResponseEntity<PDDealResponse> res = null;
        String uri = server + "deals" + apiKey;

        try{
            req = new RequestEntity<>(deal, HttpMethod.POST, new URI(uri));
            res = restTemplate.exchange(req,PDDealResponse.class);

        } catch(Exception e){
            System.out.println("Exception on POSTing PDDeal: " + e);
        }

        return res;
    }

    public List<Long> postDealList(List<PDDealSend> deals) {
        ResponseEntity<PDDealResponse> res;
        List<Long> idsPosted = new ArrayList<>();

        for(PDDealSend d : deals){
            res = postDeal(d);
            if(res.getStatusCode() == HttpStatus.CREATED){
                idsPosted.add(res.getBody().getData().getId());
            } else {
                System.out.println("Could not create deal, server response: " + res.getStatusCode().toString());
            }
        }
        return idsPosted;

    }

//-------------------------------------------------------------------------------------------------------------------GET

    public ResponseEntity<PDDealResponse> getDeal(Long dealId) {
        RequestEntity<String> req;
        ResponseEntity<PDDealResponse> res = null;
        String uri = server + "deals/" + dealId + apiKey;

        try{
            req = new RequestEntity<>(HttpMethod.GET, new URI(uri));
            res = restTemplate.exchange(req,PDDealResponse.class);

        } catch(Exception e){
            System.out.println("Exception on GETing PDDeal: " + e);
        }

        return res;
    }

    public ResponseEntity<PDDealItemsResponse> getAllDeals() {
        int start = 0;
        Boolean moreItemsInCollection = true;

        RequestEntity<String> req;
        ResponseEntity<PDDealItemsResponse> res = null;
        List<PDDealReceived> dealsReceived = new ArrayList<>();

        while(moreItemsInCollection){

            String uri = server + "deals?start=" + start + "&limit=100000&" + apiKey.substring(1);
            try{
                req = new RequestEntity<>(HttpMethod.GET, new URI(uri));
                res = restTemplate.exchange(req,PDDealItemsResponse.class);

                if(res.getBody().getData() != null){
                    dealsReceived.addAll(res.getBody().getData());
                    moreItemsInCollection = res.getBody().getAdditional_data().getPagination().getMore_items_in_collection();

                    start += 500;

                } else {
                    moreItemsInCollection = false;
                }

            } catch(Exception e){
                System.out.println("Excepton in GETing all deals: " + e);
            }
        }

        res.getBody().setData(dealsReceived);
        return res;

    }
//-------------------------------------------------------------------------------------------------------------------PUT

    public ResponseEntity<PDDealResponse> updateDeal(PDDealSend deal) {
        RequestEntity<PDDealSend> req;
        ResponseEntity<PDDealResponse> res = null;
        String uri = server + "deals/" + deal.getId() + apiKey;

        try {

            req = new RequestEntity<>(deal, HttpMethod.PUT, new URI(uri));
            res = restTemplate.exchange(req, PDDealResponse.class);

        } catch (Exception e) {
            System.out.println("EXCEPTION UPDATING DEAL: " + e);
        }

        return res;

    }

    public List<Long> updateDealList(List<PDDealSend> pds) {
        ResponseEntity<PDDealResponse> res;
        List<Long> idsPutted = new ArrayList<>();
        for(PDDealSend deal : pds) {
            res = updateDeal(deal);
            if (res.getStatusCode() == HttpStatus.OK) {
                idsPutted.add(res.getBody().getData().getId());
            } else {
                System.out.println("Could not update deal, server response: " + res.getStatusCode().toString());
            }
        }
        return idsPutted;
    }

//----------------------------------------------------------------------------------------------------------------DELETE

    public ResponseEntity<PDDeleteResponse> deleteDeal(Long id) {
        RequestEntity<String> req;
        ResponseEntity<PDDeleteResponse> res = null;

        String uri = server + "deals/" + id + apiKey;
        try{
            req = new RequestEntity<>(HttpMethod.DELETE, new URI(uri));
            res = restTemplate.exchange(req,PDDeleteResponse.class);

        } catch (Exception e){
            System.out.println("Exception on DELETEing deal: " + e);
        }

        return res;
    }

    public List<Long> deleteDealList(List<Long> idsToDelete) {
        List<Long> idsDeleted = new ArrayList<>();
        List<String> idsDeletedAsString = new ArrayList<>();
        PDBulkDeleteResponse.PDBulkDeletedIdsReq idsForReq = new PDBulkDeleteResponse().new PDBulkDeletedIdsReq();
        idsForReq.setIds(idsToDelete);
        RequestEntity<PDBulkDeleteResponse.PDBulkDeletedIdsReq> req;
        ResponseEntity<PDBulkDeleteResponse> res;

        try {
            String uri = server + "deals/" + apiKey;

            req = new RequestEntity<>(idsForReq, HttpMethod.DELETE, new URI(uri));
            res = restTemplate.exchange(req, PDBulkDeleteResponse.class);
            idsDeletedAsString = res.getBody().getData().getId();
            System.out.println(res.getBody().getData().getId().size());

            for(String s : idsDeletedAsString) {
                idsDeleted.add(Long.parseLong(s));
            }

        } catch (Exception e) {
            System.out.println(e.toString());
        }

        return idsDeleted;
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
        RequestEntity<PDOrganisationSend> req;
        ResponseEntity<PDOrganisationResponse> res = null;
        String uri = server + "organizations" + apiKey;

        try {
            PDOrganisationSend post = new PDOrganisationSend(companyName, visibleTo);

            req = new RequestEntity<>(post, HttpMethod.POST, new URI(uri));
            res = restTemplate.exchange(req, PDOrganisationResponse.class);

        } catch (Exception e) {
            System.out.println(e.toString());
        }
        return res;
    }

    public ResponseEntity<PDOrganisationResponse> postOrganisation(PDOrganisationSend post) {
        RequestEntity<PDOrganisationSend> req = null;
        ResponseEntity<PDOrganisationResponse> res = null;
        String uri = server + "organizations" + apiKey;
        try {

            req = new RequestEntity<>(post, HttpMethod.POST, new URI(uri));
            res = restTemplate.exchange(req, PDOrganisationResponse.class);

        } catch (Exception e) {
            System.out.println(e.toString() + "Respone: " + req);
        }
        return res;
    }

    public List<Long> postOrganisationList(List<PDOrganisationSend> OrgsToPost) {
        ResponseEntity<PDOrganisationResponse> res;
        List<Long> idsPosted = new ArrayList<>();
        for(PDOrganisationSend org : OrgsToPost) {
            res = postOrganisation(org);
            if (res.getStatusCode() == HttpStatus.CREATED) {
                idsPosted.add(res.getBody().getData().getId());
            } else {
                System.out.println("Could not create organisation, server response: " + res.getStatusCode().toString());
            }
        }
        return idsPosted;
    }

//-------------------------------------------------------------------------------------------------------------------PUT

    public ResponseEntity<PDOrganisationResponse> updateOrganisation(PDOrganisationSend org){

        RequestEntity<PDOrganisationSend> req;
        ResponseEntity<PDOrganisationResponse> res = null;
        String uri = server + "organizations/" + org.getId() + apiKey;

        try{
            req = new RequestEntity<>(org,HttpMethod.PUT,new URI(uri));
            res = restTemplate.exchange(req, PDOrganisationResponse.class);

        } catch (Exception e) {
            System.out.println(e.toString());
        }
        return res;
    }

    public List<Long> putOrganisationList(List<PDOrganisationSend> pds) {
        ResponseEntity<PDOrganisationResponse> res;
        List<Long> idsPutted = new ArrayList<>();
        for(PDOrganisationSend org : pds) {
            res = updateOrganisation(org);
            if (res.getStatusCode() == HttpStatus.OK) {
                idsPutted.add(res.getBody().getData().getId());
            } else {
                System.out.println("Could not update organisation, server response: " + res.getStatusCode().toString());
            }
        }
        return idsPutted;
    }

//-------------------------------------------------------------------------------------------------------------------GET

    public ResponseEntity<PDOrganisationResponse> getOrganisation(Long id) {
        RequestEntity<PDOrganisationReceived> req;
        ResponseEntity<PDOrganisationResponse> res = null;

        try {
            String uri = server + "organizations/" + id + apiKey;

            req = new RequestEntity<>(HttpMethod.GET, new URI(uri));
            res = restTemplate.exchange(req, PDOrganisationResponse.class);

        } catch (Exception e) {
            System.out.println(e.toString());
        }
        return res;
    }

    public ResponseEntity<PDOrganisationItemsResponse> getAllOrganisations(){
        int start = 0;

        Boolean moreItems = true;

        ResponseEntity<PDOrganisationItemsResponse> res = null;
        List<PDOrganisationReceived> orgsRecieved = new ArrayList<>();

        while (moreItems) {

            RequestEntity<String> req;
            String uri = server + "organizations?start=" + start + "&limit=100000&" + apiKey.substring(1);
            try{
                req = new RequestEntity<>(HttpMethod.GET, new URI(uri));
                res = restTemplate.exchange(req,PDOrganisationItemsResponse.class);
                if (res.getBody().getData() != null) {


                    orgsRecieved.addAll(res.getBody().getData());
                    moreItems = res.getBody().getAdditional_data().getPagination().getMore_items_in_collection();

                    start += 500;
                } else {
                    moreItems = false;
                }

            }
            catch(Exception e){
                System.out.println("Exception when getting all organisations from PipeDrive: " + e);
            }

        }
        res.getBody().setData(orgsRecieved);
        return res;
    }

//----------------------------------------------------------------------------------------------------------------DELETE

    public ResponseEntity<PDDeleteResponse> deleteOrganisation(Long id){
        RequestEntity<PDOrganisationReceived> req;
        ResponseEntity<PDDeleteResponse> res = null;
        String uri = server + "organizations/" + id + apiKey;

        try {

            req = new RequestEntity<>(HttpMethod.DELETE, new URI(uri));
            res = restTemplate.exchange(req, PDDeleteResponse.class);

        } catch (Exception e) {
            System.out.println("DELETE Exception: " + e.toString());
        }
        return res;
    }

    public List<Long> deleteOrganisationList(List<Long> idsToDelete) {
        List<Long> idsDeleted = new ArrayList<>();
        List<String> idsDeletedAsString;
        PDBulkDeleteResponse.PDBulkDeletedIdsReq idsForReq = new PDBulkDeleteResponse().new PDBulkDeletedIdsReq();
        idsForReq.setIds(idsToDelete);

        RequestEntity<PDBulkDeleteResponse.PDBulkDeletedIdsReq> req;
        ResponseEntity<PDBulkDeleteResponse> res;

        String uri = server + "organizations/" + apiKey;

        try {

            req = new RequestEntity<>(idsForReq, HttpMethod.DELETE, new URI(uri));
            res = restTemplate.exchange(req, PDBulkDeleteResponse.class);
            idsDeletedAsString = res.getBody().getData().getId();
            System.out.println(res.getBody().getData().getId().size());

            for(String s : idsDeletedAsString) {
                idsDeleted.add(Long.parseLong(s));
            }

        } catch (Exception e) {
            System.out.println(e.toString());
        }

        return idsDeleted;
    }


    /**
     * CONTACTS
     */
//------------------------------------------------------------------------------------------------------------------POST

    public ResponseEntity<PDContactResponse> postContact(PDContactSend contact) {

        RequestEntity<PDContactSend> req;
        ResponseEntity<PDContactResponse> res = null;
        String uri = server + "persons" + apiKey;

        try {
            req = new RequestEntity<>(contact, HttpMethod.POST, new URI(uri));
            res = restTemplate.exchange(req, PDContactResponse.class);

            for(Long f : contact.getFollowers()){
                postFollower(new PDFollower(res.getBody().getData().getId(), f));
            }

        } catch (Exception e) {
            System.out.println("OOPS" + e.toString() + " RESPONSE: " + res);
        }

        return res;
    }

    public Map<Long, Long> postContactList(List<PDContactSend> contacts){
        Map<Long, Long> map = new HashMap<>();
        ResponseEntity<PDContactResponse> res;

        for(PDContactSend p : contacts){
            res = postContact(p);
            if (res.getStatusCode() == HttpStatus.CREATED) {

                map.put(p.getV_id(), res.getBody().getData().getId());

            } else {
                System.out.println("Could not create contact, server response: " + res.getStatusCode().toString());
            }
        }
        return map;

    }

//-------------------------------------------------------------------------------------------------------------------GET

    public ResponseEntity<PDContactListReceived> getContactsForOrganisation(Long org_id) {
        RequestEntity<String> req;
        ResponseEntity<PDContactListReceived> res = null;
        String uri = server + "organizations/" + org_id + "/persons?start=0&" + apiKey.substring(1);

        try {

            req = new RequestEntity<>(HttpMethod.GET, new URI(uri));
            res = restTemplate.exchange(req, PDContactListReceived.class);

        } catch (Exception e) {
            System.out.println(e.toString());
        }

        return res;
    }

    public ResponseEntity<PDContactListReceived> getAllContacts() {
        int start = 0;

        Boolean moreItems = true;

        ResponseEntity<PDContactListReceived> res = null;
        List<PDContactReceived> contactsRecieved = new ArrayList<>();

        while (moreItems) {

            RequestEntity<String> req;
            String uri = server + "persons?start=" + start + "&limit=100000&" + apiKey.substring(1);
            try{
                req = new RequestEntity<>(HttpMethod.GET, new URI(uri));
                res = restTemplate.exchange(req, PDContactListReceived.class);

                if (res.getBody().getData() != null) {
                    contactsRecieved.addAll(res.getBody().getData());
                    moreItems = res.getBody().getAdditional_data().getPagination().getMore_items_in_collection();
                    start += 500;
                } else {
                    moreItems = false;
                }

            }
            catch(Exception e){
                System.out.println("Exception when getting all contacts from PipeDrive: " + e);
            }

        }
        res.getBody().setData(contactsRecieved);
        return res;
    }

//----------------------------------------------------------------------------------------------------------------DELETE

    public ResponseEntity<PDDeleteResponse> deleteContact(Long id) {
        RequestEntity<String> req;
        ResponseEntity<PDDeleteResponse> res = null;
        String uri = server + "persons/" + id + apiKey;

        try {

            req = new RequestEntity<>(HttpMethod.DELETE, new URI(uri));
            res = restTemplate.exchange(req, PDDeleteResponse.class);

        } catch (Exception e) {
            System.out.println(e.toString());
        }

        return res;
    }

    public List<Long> deleteContactList(List<Long> idsToDelete){
        List<Long> idsDeleted = new ArrayList<>();
        List<String> idsDeletedAsString;
        PDBulkDeleteResponse.PDBulkDeletedIdsReq idsForReq = new PDBulkDeleteResponse().new PDBulkDeletedIdsReq();
        idsForReq.setIds(idsToDelete);

        RequestEntity<PDBulkDeleteResponse.PDBulkDeletedIdsReq> req = null;
        ResponseEntity<PDBulkDeleteResponse> res;


        try {

            String uri = server + "persons/" + apiKey;
            req = new RequestEntity<>(idsForReq, HttpMethod.DELETE, new URI(uri));
            res = restTemplate.exchange(req, PDBulkDeleteResponse.class);
            idsDeletedAsString = res.getBody().getData().getId();
            System.out.println(res.getBody().getData().getId().size());

            for(String s : idsDeletedAsString) {
                idsDeleted.add(Long.parseLong(s));
            }

        } catch (Exception e) {
            System.out.println(e.toString() + "REQ: " + req);
        }

        return idsDeleted;
    }

//-------------------------------------------------------------------------------------------------------------------PUT

    public ResponseEntity<PDContactResponse> updateContact(PDContactSend contact){
        RequestEntity<PDContactSend> req;
        ResponseEntity<PDContactResponse> res = null;
        Long id = contact.getId();
        String uri = server + "persons/" + id + apiKey;

        try {

            req = new RequestEntity<>(contact, HttpMethod.PUT, new URI(uri));
            res = restTemplate.exchange(req, PDContactResponse.class);

        } catch (Exception e) {
            System.out.println(e.toString());
        }

        return res;
    }

    public Map<Long, Long> putContactList(List<PDContactSend> contacts){
        ResponseEntity<PDContactResponse> res;

        Map<Long, Long> map = new HashMap<>();

        List<Long> idsPut = new ArrayList<>();

        for(PDContactSend c : contacts){
            res = updateContact(c);
            if(res.getStatusCode() == HttpStatus.OK){

                map.put(c.getV_id(), res.getBody().getData().getId());
                idsPut.add(res.getBody().getData().getId());
            }else{
                System.out.println("Could not UPDATE contact, server responded: " + res.getStatusCode());
            }
        }
        return map;
    }


    /**
     * Activites
     */
//-------------------------------------------------------------------------------------------------------------------GET

    public ResponseEntity<PDActivityItemsResponse> getAllActivitiesForDeal(Long dealId) {
        //TODO: implement this + test
        return null;
    }

    public ResponseEntity<PDActivityItemsResponse> getAllActivitiesForOrganisation(Long orgId) {
        //TODO: implement this + test
        return null;
    }

    public ResponseEntity<PDActivityItemsResponse> getAllActivitiesForContact(Long contactId) {
        //TODO: implement this + test
        return null;
    }

//------------------------------------------------------------------------------------------------------------------POST

    public ResponseEntity<PDActivityResponse> postActivity(PDActivity activity) {

        RequestEntity<PDActivity> req;
        ResponseEntity<PDActivityResponse> res = null;
        String uri = server + "activites" + apiKey;

        try {

            req = new RequestEntity<PDActivity>(activity, HttpMethod.POST, new URI(uri));
            res = restTemplate.exchange(req, PDActivityResponse.class);

        } catch (Exception e) {
            System.out.println("EXCEPTION POSTING DEAL: " + e);
        }

        return res;
    }

//-------------------------------------------------------------------------------------------------------------------PUT

    public ResponseEntity<PDActivityResponse> putActivity(PDActivity activity) {
        //TODO: implement this + test
        return null;
    }

//----------------------------------------------------------------------------------------------------------------DELETE

    public ResponseEntity<PDDeleteResponse> deleteActivity(Long activityId) {
        //TODO: implement this + test
        return null;
    }

    /**
     * Users
     */
//-------------------------------------------------------------------------------------------------------------------GET

    public ResponseEntity<PDUserItemsResponse> getAllUsers() {

        RequestEntity<String> req;
        ResponseEntity<PDUserItemsResponse> res = null;
        String uri = server + "users"+ apiKey;

        try {

            req = new RequestEntity<>(HttpMethod.GET, new URI(uri));
            res = restTemplate.exchange(req, PDUserItemsResponse.class);

        } catch (Exception e) {
            System.out.println(e.toString());
        }

        return res;
    }


    /**
     * Followers
     */
//------------------------------------------------------------------------------------------------------------------POST
    public ResponseEntity<String> postFollower(PDFollower f){
        //TODO: change res to accept new pojo instead of string (followers)

        RequestEntity<PDFollower> req;
        ResponseEntity<String> res = null;
        String uri = server + "persons/"+ f.getContactID() + "/followers" + apiKey;

        try{
            req = new RequestEntity<>(f,HttpMethod.POST, new URI(uri));
            res = restTemplate.exchange(req,String.class);

        } catch (HttpClientErrorException e) {
            System.out.println("ERROR POSTING FOLLOWER: " + e
                    + "userID: " + f.getUserID() + ", contactIdP: " + f.getContactID());
        } catch(Exception e){
            System.out.println("ERROR on posting follower " + e);
        }
        return res;
    }


    /**
     * Organisation relationships
     */
//------------------------------------------------------------------------------------------------------------------POST
    public ResponseEntity<String> postOrganisationRelationship(PDRelationship relationship) {
        //TODO: change res to accept new pojo instead of string (org rel)

        RequestEntity<PDRelationship> req;
        ResponseEntity<String> res = null;

        String uri = server + "organizationRelationships" + apiKey;

        try {

            req = new RequestEntity<>(relationship, HttpMethod.POST, new URI(uri));
            res = restTemplate.exchange(req, String.class);

        } catch (Exception e) {
            System.out.println("EXCEPTION IN POSTING ORGANISATION RELATIONSHIP: " + e);
        }

        return res;

    }


    /**
     * UTILITIES
     */
//----------------------------------------------------------------------------------------------------------------DELETE
    public void clearPD(List<Long> orgsToKeep, List<Long> contsToKeep){

        Integer orgsize = 0;
        Integer contsise = 0;

        ResponseEntity<PDOrganisationItemsResponse> resOrg;
        ResponseEntity<PDContactListReceived> resCont;
        List<Long> orgsDeleted;
        List<Long> contsDeleted;

        List<Long> orgsToDel = new ArrayList<>();
        List<Long> contsToDel = new ArrayList<>();


        String orgURL = server + "organisations/" + apiKey;

        try {

            orgsToDel.clear();
            contsToDel.clear();
            resOrg = getAllOrganisations();

            orgsize = resOrg.getBody().getData().size();
            for(PDOrganisationReceived o : resOrg.getBody().getData()){
                if(! orgsToKeep.contains(o.getId())){
                    orgsToDel.add(o.getId());
                }
            }
            System.out.println("Orgs added to del List");

            resCont = getAllContacts();
            contsise = resCont.getBody().getData().size();

            for(PDContactReceived c : resCont.getBody().getData()){
                if( ! contsToKeep.contains(c.getId())){
                    contsToDel.add(c.getId());
                }
            }
            System.out.println("Contacts added to del List");

            if(orgsToDel.size() != 0){

                orgsDeleted = deleteOrganisationList(orgsToDel);
            }
            if(contsToDel.size() != 0){

                contsDeleted = deleteContactList(contsToDel);
            }



        } catch (Exception e) {
            System.out.println("EXCEPTION CLEARING PD: " + e.toString());
        }

    }

}


