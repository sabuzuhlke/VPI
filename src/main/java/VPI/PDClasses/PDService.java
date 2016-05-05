package VPI.PDClasses;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.sun.org.apache.xpath.internal.operations.Bool;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PDService {

    private RestTemplate restTemplate;
    private String server;
    private String apiKey;

    public PDService(RestTemplate restTemplate, String server, String apiKey) {
        this.restTemplate = restTemplate;
        this.apiKey = apiKey;
        this.server = server;
    }
//---ORGANISATIONS-----------------------------------------------------------------POST
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

    public ResponseEntity<PDOrganisationResponse> postOrganisationWithAddress(String companyName, String address, Integer visibleTo) {

        RequestEntity<PDOrganisationSend> req;
        ResponseEntity<PDOrganisationResponse> res = null;

        try {
            PDOrganisationSend post = new PDOrganisationSend(companyName,address, visibleTo);
            String uri = server + "organizations" + apiKey;

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

//----------------------------------------------------------------------------------PUT

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
//----------------------------------------------------------------------------------GET
    public ResponseEntity<PDOrganisationResponse> getOrganisation(Long id) {
        RequestEntity<PDOrganisation> req;
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
        List<PDOrganisation> orgsRecieved = new ArrayList<>();

        while (moreItems) {

            RequestEntity<String> req;
            String uri = server + "organizations?start=" + start + "&limit=100000&" + apiKey.substring(1);
            try{
                req = new RequestEntity<>(HttpMethod.GET, new URI(uri));
                res = restTemplate.exchange(req,PDOrganisationItemsResponse.class);

                orgsRecieved.addAll(res.getBody().getData());
                moreItems = res.getBody().getAdditional_data().getPagination().getMore_items_in_collection();
                start += 500;
            }
            catch(Exception e){
                System.out.println("Exception when getting all organisations from PipeDrive: " + e);
            }

        }
        res.getBody().setData(orgsRecieved);
        return res;
    }
//----------------------------------------------------------------------------------DELETE
    public ResponseEntity<PDDeleteResponse> deleteOrganisation(Long id){
        RequestEntity<PDOrganisation> req;
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

//---CONTACTS-----------------------------------------------------------------------POST
    public ResponseEntity<PDContactResponse> postContact(PDContactSend contact) {
        RequestEntity<PDContactSend> req;
        ResponseEntity<PDContactResponse> res = null;
        String uri = server + "persons" + apiKey;

        try {
            //System.out.println("Posting Contact: ");
            //System.out.println("    name: " + contact.getName());
            req = new RequestEntity<>(contact, HttpMethod.POST, new URI(uri));
            //System.out.println("REQUEST:  " + req);
            res = restTemplate.exchange(req, PDContactResponse.class);
            //System.out.println("RESPONSE: " + res);

        } catch (Exception e) {
            System.out.println("OOPS" + e.toString() + " RESPONSE: " + res);
        }

        return res;
    }

    public List<Long> postContactList(List<PDContactSend> contacts){
        List<Long> idsPosted = new ArrayList<>();
        ResponseEntity<PDContactResponse> res = null;

        for(PDContactSend p : contacts){
            res = postContact(p);
            if (res.getStatusCode() == HttpStatus.CREATED) {
                idsPosted.add(res.getBody().getData().getId());
            } else {
                System.out.println("Could not create contact, server response: " + res.getStatusCode().toString());
            }
        }
        return idsPosted;

    }

//----------------------------------------------------------------------------------GET
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

                contactsRecieved.addAll(res.getBody().getData());
                moreItems = res.getBody().getAdditional_data().getPagination().getMore_items_in_collection();
                start += 500;
            }
            catch(Exception e){
                System.out.println("Exception when getting all organisations from PipeDrive: " + e);
            }

        }
        res.getBody().setData(contactsRecieved);
        return res;
    }

//-----------------------------------------------------------------------------------DELETE
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

        String uri = server + "persons/" + apiKey;

        try {

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

//-----------------------------------------------------------------------------------PUT
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

    public List<Long> putContactList(List<PDContactSend> contacts){
        ResponseEntity<PDContactResponse> res;
        List<Long> idsPut = new ArrayList<>();

        for(PDContactSend c : contacts){
            res = updateContact(c);
            if(res.getStatusCode() == HttpStatus.OK){

                idsPut.add(res.getBody().getData().getId());
            }else{
                System.out.println("Could not UPDATE contact, server responded: " + res.getStatusCode());
            }
        }
        return idsPut;
    }

    public void clearPD(List<Long> orgsToKeep, List<Long> contsToKeep){
        //TODO: Modify this
        orgsToKeep.add(206L);
        orgsToKeep.add(207L);
        orgsToKeep.add(209L);
        orgsToKeep.add(211L);
        orgsToKeep.add(208L);
        orgsToKeep.add(1L);
        orgsToKeep.add(3L);

        contsToKeep.add(1L);
        contsToKeep.add(4L);
        contsToKeep.add(5L);
        contsToKeep.add(6L);
        contsToKeep.add(7L);
        contsToKeep.add(8L);
        contsToKeep.add(8L);
        contsToKeep.add(11L);
        contsToKeep.add(12L);
        contsToKeep.add(13L);
        contsToKeep.add(845L);

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

            while(orgsize != orgsToKeep.size() && contsise != contsToKeep.size()){
                orgsToDel.clear();
                contsToDel.clear();
                resOrg = getAllOrganisations();

                orgsize = resOrg.getBody().getData().size();
                for(PDOrganisation o : resOrg.getBody().getData()){
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
            }



        } catch (Exception e) {
            System.out.println(e.toString());
        }

    }
}


