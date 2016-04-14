package VPI.PDClasses;

import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import java.net.URI;
import java.util.ArrayList;
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
        RequestEntity<PDOrganisation> req;
        ResponseEntity<PDOrganisationResponse> res = null;
        String uri = server + "organizations" + apiKey;

        try {
            PDOrganisation post = new PDOrganisation(companyName, visibleTo);

            req = new RequestEntity<>(post, HttpMethod.POST, new URI(uri));
            res = restTemplate.exchange(req, PDOrganisationResponse.class);

        } catch (Exception e) {
            System.out.println(e.toString());
        }
        return res;
    }

    public ResponseEntity<PDOrganisationResponse> postOrganisationWithAddress(String companyName, String address, Integer visibleTo) {

        RequestEntity<PDOrganisation> req;
        ResponseEntity<PDOrganisationResponse> res = null;

        try {
            PDOrganisation post = new PDOrganisation(companyName,address, visibleTo);
            String uri = server + "organizations" + apiKey;

            req = new RequestEntity<>(post, HttpMethod.POST, new URI(uri));
            res = restTemplate.exchange(req, PDOrganisationResponse.class);

        } catch (Exception e) {
            System.out.println(e.toString());
        }
        return res;
    }

    public ResponseEntity<PDOrganisationResponse> postOrganisation(PDOrganisation post) {
        RequestEntity<PDOrganisation> req;
        ResponseEntity<PDOrganisationResponse> res = null;
        String uri = server + "organizations" + apiKey;
        try {

            req = new RequestEntity<>(post, HttpMethod.POST, new URI(uri));
            res = restTemplate.exchange(req, PDOrganisationResponse.class);

        } catch (Exception e) {
            System.out.println(e.toString());
        }
        return res;
    }

    public List<Long> postOrganisationList(List<PDOrganisation> OrgsToPost) {
        ResponseEntity<PDOrganisationResponse> res;
        List<Long> idsPosted = new ArrayList<>();
        for(PDOrganisation org : OrgsToPost) {
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
    public ResponseEntity<PDOrganisationResponse> updateOrganisationAddress(Long id, String address) {
        PDOrganisationResponse org;
        PDOrganisationResponse resOrganisation = new PDOrganisationResponse();
        RequestEntity<PDOrganisation> req;
        ResponseEntity<PDOrganisationResponse> res = null;


        try {
            //GET organisation From Pipedrive
            org = this.getOrganisation(id).getBody();

            //Update with new Address
            PDOrganisation newOrg = new PDOrganisation(
                    id,
                    org.getData().getName(),
                    org.getData().getVisible_to(),
                    address,
                    true,
                    org.getData().getCompany_id(),
                    org.getData().getOwner_id()
            );

            //PUT Org with new address to PipeDrive
            String uri = server + "organizations/" + id + apiKey;

            req = new RequestEntity<>(newOrg, HttpMethod.PUT, new URI(uri));

            res = restTemplate.exchange(req, PDOrganisationResponse.class);

        } catch (Exception e) {
            System.out.println(e.toString());
        }
        return res;
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
        RequestEntity<String> req;
        ResponseEntity<PDOrganisationItemsResponse> res = null;
        String uri = server + "organizations?start=0&limit=1000&" + apiKey.substring(1);
        try{
            req = new RequestEntity<>(HttpMethod.GET, new URI(uri));
            res = restTemplate.exchange(req,PDOrganisationItemsResponse.class);

        }
        catch(Exception e){
            System.out.println("Exception when getting all organisations from PipeDrive: " + e);
        }
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
    public ResponseEntity<PDContactResponse> postContactForOrganisation(PDContactSend contact) {
        RequestEntity<PDContactSend> req;
        ResponseEntity<PDContactResponse> res = null;
        String uri = server + "persons" + apiKey;

        try {

            req = new RequestEntity<>(contact, HttpMethod.POST, new URI(uri));
            res = restTemplate.exchange(req, PDContactResponse.class);

        } catch (Exception e) {
            System.out.println(e.toString());
        }

        return res;
    }

//----------------------------------------------------------------------------------GET
    public ResponseEntity<PDContactsForOrganisation> getContactsForOrganisation(Long org_id) {
        RequestEntity<String> req;
        ResponseEntity<PDContactsForOrganisation> res = null;
        String uri = server + "organizations/" + org_id + "/persons?start=0&" + apiKey.substring(1);

        try {

            req = new RequestEntity<>(HttpMethod.GET, new URI(uri));
            res = restTemplate.exchange(req, PDContactsForOrganisation.class);

        } catch (Exception e) {
            System.out.println(e.toString());
        }

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
}


