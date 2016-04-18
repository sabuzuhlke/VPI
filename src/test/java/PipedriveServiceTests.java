import VPI.*;
import VPI.PDClasses.*;
import VPI.VClasses.VContact;
import com.sun.org.apache.regexp.internal.RESyntaxException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.OutputCapture;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(Application.class)
@WebIntegrationTest
public class PipedriveServiceTests {

    @Rule
    public OutputCapture capture = new OutputCapture();

    private PDService PS;
    private ArrayList<Long> idsDeleted;


    @Before
    public void setUp() throws Exception {
        String server = "https://api.pipedrive.com/v1/";
        MyCredentials creds = new MyCredentials();
        String apiKey = creds.getApiKey();
        TestRestTemplate testRestTemplate = new TestRestTemplate();
        this.PS = new PDService(testRestTemplate, server, apiKey);
        this.idsDeleted = new ArrayList<>();
    }

    @Test
    public void canPostOrganisation() {
        //Post org, check response says success
        String companyName = "TestPostCompany";
        Integer visibility = 3;
        ResponseEntity<PDOrganisationResponse> postResponse = PS.postOrganisation(companyName, visibility);
        assertTrue(postResponse.getStatusCode() == HttpStatus.CREATED);
        assertTrue(postResponse.getBody().getSuccess());

        //getOrganisation same org using returned id, check name is equivalent to supplied name
        Long id = postResponse.getBody().getData().getId();
        ResponseEntity<PDOrganisationResponse> o = PS.getOrganisation(id);
        assertTrue(o.getBody().getData().getName().equals(companyName));

        //deleteOrganisation posted organisation
        idsDeleted.add(id);
        PS.deleteOrganisation(id);
    }

    @Test
    public void canDeleteOrganisation(){

        ResponseEntity<PDDeleteResponse> delRes;
        String companyName = "TestDeleteCompany";
        Integer visibility = 3;
        ResponseEntity<PDOrganisationResponse> postResponse = PS.postOrganisation(companyName, visibility);
        assertTrue(postResponse.getStatusCode() == HttpStatus.CREATED);
        assertTrue(postResponse.getBody().getSuccess());

        delRes = PS.deleteOrganisation(postResponse.getBody().getData().getId());
        assertTrue(delRes.getBody().getSuccess());
        assertEquals(delRes.getBody().getData().getId(), postResponse.getBody().getData().getId());

    }

    @Test
    public void canUpdateOrgAddress() {
        ResponseEntity<PDOrganisationResponse> org;
        String companyName = "TestAddressCompany";
        Integer visibility = 3;

        //POST
        ResponseEntity<PDOrganisationResponse> postResponse = PS.postOrganisation(companyName, visibility);
        assertTrue(postResponse.getStatusCode() == HttpStatus.CREATED);
        assertTrue(postResponse.getBody().getSuccess());

        Long id = postResponse.getBody().getData().getId();
        String newAddress = "test address";
        //PUT
        org = PS.updateOrganisationAddress(id, newAddress);

        assertTrue(org.getBody().getData().getAddress().equals(newAddress));

        //DELETE posted organisation
        idsDeleted.add(id);
        PS.deleteOrganisation(id);

    }

    @Test
    public void canUpdateOrg() {
        ResponseEntity<PDOrganisationResponse> orgr;
        String companyName = "TestAddressCompany";
        Integer visibility = 3;

        //POST
        ResponseEntity<PDOrganisationResponse> postResponse = PS.postOrganisation(companyName, visibility);
        assertTrue(postResponse.getStatusCode() == HttpStatus.CREATED);
        assertTrue(postResponse.getBody().getSuccess());

        PDOrganisation org = postResponse.getBody().getData();

        String newAddress = "test address";

        org.setAddress(newAddress);
        //PUT
        orgr = PS.updateOrganisation(org);

        assertTrue(orgr.getStatusCode() == HttpStatus.OK);
        assertTrue(orgr.getBody() != null);
        assertTrue(orgr.getBody().getSuccess());
        assertTrue(orgr.getBody().getData().getAddress().equals(newAddress));

        //DELETE posted organisation
        idsDeleted.add(org.getId());
        PS.deleteOrganisation(org.getId());

    }



    @Test
    public void canGetAllOrganisations(){
        ResponseEntity<PDOrganisationItemsResponse> res;
        PDOrganisationItemsResponse organisations;
        res = PS.getAllOrganisations();
        assertEquals(res.getStatusCode(), HttpStatus.OK);
        organisations = res.getBody();
        //The following asserts that the more_items_in_collection field of the response is false -- Meaning that there are no more organisations to return
        assertTrue(!organisations.getAdditional_data().getPagination().getMore_items_in_collection());
        assertTrue(organisations.getData() != null);
        assertTrue(organisations.getData().get(1) != null);
    }

    @Test
    public void canGetAndPostContactsForOrganisation() {
        //post org
        String companyName = "TestPostCompany";
        Integer visibility = 3;

        ResponseEntity<PDOrganisationResponse> postResponse = PS.postOrganisation(companyName, visibility);
        assertTrue(postResponse.getStatusCode() == HttpStatus.CREATED);
        assertTrue(postResponse.getBody().getSuccess());
        //post contact for org
        Long org_id = postResponse.getBody().getData().getId();

        PDContactSend contact = new PDContactSend(org_id, "Test Name", "Test@Test.test", "0987654321");
        ResponseEntity<PDContactResponse> contactPostResponse = PS.postContactForOrganisation(contact);

        assertTrue(contactPostResponse.getBody() != null);
        assertTrue(contactPostResponse.getBody().getSuccess());
        assertTrue(contactPostResponse.getBody().getData().getName().equals("Test Name"));
        assertTrue(contactPostResponse.getBody().getData().getEmail().get(0).getValue().equals("Test@Test.test"));
        assertTrue(contactPostResponse.getBody().getData().getPhone().get(0).getValue().equals("0987654321"));

        //get contact for org
        ResponseEntity<PDContactsForOrganisation> contactResponse = PS.getContactsForOrganisation(org_id);

        assertTrue(contactResponse.getBody() != null);
        assertTrue(contactResponse.getBody().getSuccess());

        String name = contactResponse.getBody().getData().get(0).getName();
        String email = contactResponse.getBody().getData().get(0).getEmail().get(0).getValue();
        String phone = contactResponse.getBody().getData().get(0).getPhone().get(0).getValue();

        Long recievedOrgId = contactResponse.getBody().getData().get(0).getOrg_id().getValue();
        //check equal
        assertTrue(name.equals("Test Name"));
        assertTrue(email.equals("Test@Test.test"));
        assertTrue(phone.equals("0987654321"));
        assertEquals(recievedOrgId, org_id);

        //delete org and person
        Long contact_id = contactPostResponse.getBody().getData().getId();

        ResponseEntity<PDDeleteResponse> delContRes = PS.deleteContact(contact_id);
        idsDeleted.add(org_id);
        ResponseEntity<PDDeleteResponse> delRes = PS.deleteOrganisation(org_id);

        assertTrue(delContRes.getStatusCode() == HttpStatus.OK);
        assertTrue(delContRes.getBody().getSuccess());
        assertTrue(delRes.getStatusCode() == HttpStatus.OK);
        assertTrue(delRes.getBody().getSuccess());

        assertTrue(delContRes.getBody().getData().getId().equals(contact_id));
        assertTrue(delRes.getBody().getData().getId().equals(org_id));
    }



    @Test
    public void canPutContact() {

        //first Test no need to assert, taken care of in prev test
        String companyName = "TestPostCompany";
        Integer visibility = 3;

        ResponseEntity<PDOrganisationResponse> postResponse = PS.postOrganisation(companyName, visibility);

        //post contact for org
        Long org_id = postResponse.getBody().getData().getId();

        PDContactSend contact = new PDContactSend(org_id, "Test Name", "Test@Test.test", "0987654321");
        ResponseEntity<PDContactResponse> contactPostResponse = PS.postContactForOrganisation(contact);

        //modify contact
        contact.changePrimaryEmail("loseweight@kfc.com");
        contact.changePrimaryPhone("1234567890");
        contact.setId(contactPostResponse.getBody().getData().getId());

        //putagain
        ResponseEntity<PDContactResponse> contactPutResponse = PS.updateContact(contact);
        assertTrue(contactPutResponse.getStatusCode() == HttpStatus.OK);
        assertTrue(contactPutResponse.getBody().getSuccess());
        assertTrue(contactPutResponse.getBody().getData().getName().equals("Test Name"));
        assertTrue(contactPutResponse.getBody().getData().getEmail().get(0).getValue().equals("loseweight@kfc.com"));
        assertTrue(contactPutResponse.getBody().getData().getPhone().get(0).getValue().equals("1234567890"));


        //delete org and person
        Long contact_id = contactPostResponse.getBody().getData().getId();

        ResponseEntity<PDDeleteResponse> delContRes = PS.deleteContact(contact_id);
        idsDeleted.add(org_id);
        ResponseEntity<PDDeleteResponse> delRes = PS.deleteOrganisation(org_id);

        assertTrue(delContRes.getStatusCode() == HttpStatus.OK);
        assertTrue(delContRes.getBody().getSuccess());
        assertTrue(delRes.getStatusCode() == HttpStatus.OK);
        assertTrue(delRes.getBody().getSuccess());

        assertTrue(delContRes.getBody().getData().getId().equals(contact_id));
        assertTrue(delRes.getBody().getData().getId().equals(org_id));


    }

    @Test
    public void canPostContactList(){

        List<PDContactSend> contacts = assignMatchingPDContacts();
        ResponseEntity<PDOrganisationResponse> Orgres;
        ResponseEntity<PDDeleteResponse> delres;
        List<Long> idsPosted;

        Orgres = PS.postOrganisation("Yuuhuuu!",3);
        assertTrue(Orgres.getStatusCode() == HttpStatus.CREATED);

        Long orgid = Orgres.getBody().getData().getId();

        for(int i = 0; i < contacts.size(); i++){
            contacts.get(i).setOrg_id(orgid);
        }

        idsPosted = PS.postContactList(contacts);

        assertTrue(idsPosted.size() == contacts.size());
        List<Long> contactsDeleted = new ArrayList<>();

        for(Long id : idsPosted){
            delres = PS.deleteContact(id);
            assertTrue(delres.getBody().getSuccess());
            contactsDeleted.add(delres.getBody().getData().getId());
        }

        PS.deleteOrganisation(orgid);

        assertEquals(idsPosted.size(),contactsDeleted.size());


    }

    public List<PDContactSend> assignMatchingPDContacts() {

        PDContactSend c1 = new PDContactSend();
        c1.setName("Batman");
        ContactDetail e1 = new ContactDetail();
        e1.setPrimary(true);
        e1.setValue("BatSignal@night.com");
        c1.getEmail().add(e1);
        ContactDetail p1 = new ContactDetail();
        p1.setPrimary(true);
        p1.setValue("0987654321");
        c1.getPhone().add(p1);
        ContactDetail e12 = new ContactDetail();
        e12.setPrimary(false);
        e12.setValue("Bruce@wayne.com");
        c1.getEmail().add(e12);
        ContactDetail p12 = new ContactDetail();
        p12.setPrimary(false);
        p12.setValue("1234567890");
        c1.getPhone().add(p12);

        PDContactSend c2 = new PDContactSend();
        c2.setName("Robin");
        ContactDetail e2 = new ContactDetail();
        e2.setPrimary(true);
        e2.setValue("Robin@night.com");
        c2.getEmail().add(e2);

        PDContactSend c3 = new PDContactSend();
        c3.setName("Joker");
        ContactDetail e3 = new ContactDetail();
        e3.setPrimary(true);
        e3.setValue("joke@you.com");
        c3.getEmail().add(e3);
        ContactDetail p3 = new ContactDetail();
        p3.setPrimary(true);
        p3.setValue("123123");
        c3.getPhone().add(p3);

        PDContactSend c4 = new PDContactSend();
        c4.setName("Penguin");
        ContactDetail e4 = new ContactDetail();
        e4.setPrimary(true);
        e4.setValue("Penguin@large.com");
        c4.getEmail().add(e4);
        ContactDetail p4 = new ContactDetail();
        p4.setPrimary(true);
        p4.setValue("321321");
        c4.getPhone().add(p4);

        List<PDContactSend> pdContactSends = new ArrayList<>();
        pdContactSends.add(c1);
        pdContactSends.add(c2);
        pdContactSends.add(c3);
        pdContactSends.add(c4);

        return pdContactSends;
    }

    @Test
    public void deletedAllOrganisations(){
        ResponseEntity<PDOrganisationResponse> org;
        for(Long i : idsDeleted){
            org = PS.getOrganisation(i);
            System.out.println("Is Org " + i + " deleted?" + org.getBody().getData().getActive_flag());
            assertTrue(!org.getBody().getData().getActive_flag());
        }
    }

}
