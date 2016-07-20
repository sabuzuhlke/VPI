package CurrentTests;

import VPI.*;
import VPI.Entities.Organisation;
import VPI.PDClasses.*;
import VPI.PDClasses.Activities.PDActivityReceived;
import VPI.PDClasses.Activities.PDActivityResponse;
import VPI.PDClasses.Activities.PDActivitySend;
import VPI.PDClasses.Contacts.*;
import VPI.Entities.util.ContactDetail;
import VPI.PDClasses.PDFollower;
import VPI.PDClasses.Deals.*;
import VPI.PDClasses.Organisations.PDOrganisationReceived;
import VPI.PDClasses.Organisations.PDOrganisationSend;
import VPI.PDClasses.Organisations.PDOrganisationItemsResponse;
import VPI.PDClasses.Organisations.PDOrganisationResponse;
import VPI.PDClasses.Users.PDUserItemsResponse;
import org.junit.Before;
import org.junit.Ignore;
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

import java.io.FileWriter;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;
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
        this.PS = new PDService(server, apiKey);
        this.idsDeleted = new ArrayList<>();
    }

    /**
     * Deals ===========================================================================================================
     */

    @Test
    public void canPostDeal() {

        PDDealSend deal = new PDDealSend();
        deal.setAdd_time("2005-07-05 12:21:12");
        deal.setVisible_to(3);
        deal.setCost(1000L);
        deal.setCurrency("GBP");
        deal.setLead_type("New Lead");
        deal.setLost_reason("No reason");
        deal.setOrg_id(44265L);
        deal.setPerson_id(40390L);
        deal.setUser_id(1363416L);
        deal.setPhase("Phase");
        deal.setProject_number("PROJ_1");
        deal.setStage_id(1);
        deal.setStatus("open");
        deal.setTitle("TEST DEAL");
        deal.setValue("10000");
        deal.setZuhlke_office("London");

        ResponseEntity<PDDealResponse> postedDeal = PS.postDeal(deal);

        assertTrue(postedDeal.getBody().getSuccess());
        assertTrue(postedDeal.getBody().getData().getActive());
        assertTrue(postedDeal.getBody().getData().getTitle().equals("TEST DEAL"));

        ResponseEntity<PDDeleteResponse> deletedDeal = PS.deleteDeal(postedDeal.getBody().getData().getId());

        assertTrue(deletedDeal.getStatusCode() == HttpStatus.OK);
        assertTrue(deletedDeal.getBody().getSuccess());
    }

    @Test
    public void canPostDealList() {
        List<PDDealSend> deals = getSomeDeals();

        List<Long> idsPosted = new ArrayList<>();
        List<Long> idsDeleted = new ArrayList<>();

        HashMap<Long, Long> map = PS.postDealList(deals);
        System.out.println(map.size());
        System.out.println(map.values().size());
        System.out.println(map.keySet().size());
        assertTrue(map.size() == 2);

        //idsDeleted = PS.deleteDealList(map.values());

        //assertTrue(idsDeleted.equals(new ArrayList<>(map.values())));
    }

    @Test
    public void canGetDeal() {
        PDDealSend deal = new PDDealSend();
        deal.setAdd_time("2005-07-05 12:21:12");
        deal.setVisible_to(3);
        deal.setCost(1000L);
        deal.setCurrency("GBP");
        deal.setLead_type("New Lead");
        deal.setLost_reason("No reason");
        deal.setOrg_id(1L);
        deal.setPerson_id(1L);
        deal.setUser_id(1277584L);
        deal.setPhase("Phase");
        deal.setProject_number("PROJ_1");
        deal.setStage_id(1);
        deal.setStatus("open");
        deal.setTitle("TEST DEAL");
        deal.setValue("10000");
        deal.setZuhlke_office("London");

        ResponseEntity<PDDealResponse> postedDeal = PS.postDeal(deal);

        assertTrue(postedDeal.getBody().getSuccess());
        assertTrue(postedDeal.getBody().getData().getActive());
        assertTrue(postedDeal.getBody().getData().getTitle().equals("TEST DEAL"));

        ResponseEntity<PDDealResponse> gotDeal = PS.getDeal(postedDeal.getBody().getData().getId());
        System.out.println(gotDeal);
        System.out.println(gotDeal.getBody());

        assertTrue(gotDeal.getStatusCode() == HttpStatus.OK);
        assertTrue(gotDeal.getBody() != null);
        assertTrue(gotDeal.getBody().getData() != null);
        assertTrue(gotDeal.getBody().getData().getActive());
        assertTrue(gotDeal.getBody().getData().getId().longValue() == postedDeal.getBody().getData().getId());
        assertTrue(gotDeal.getBody().getData().getCost() == 1000L);
        assertTrue(gotDeal.getBody().getData().getCurrency().equals("GBP"));
        //assertTrue(gotDeal.getBody().getData().getLead_type() == 3);
        assertTrue(gotDeal.getBody().getData().getPhase().equals("Phase"));
        assertTrue(gotDeal.getBody().getData().getProject_number().equals("PROJ_1"));
        assertTrue(gotDeal.getBody().getData().getStatus().equals("open"));
        assertTrue(gotDeal.getBody().getData().getTitle().equals("TEST DEAL"));
        assertTrue(gotDeal.getBody().getData().getZuhlke_office().equals("1"));
        assertTrue(gotDeal.getBody().getData().getOrg_id().getValue() == 1L);
        assertTrue(gotDeal.getBody().getData().getPerson_id().getValue() == 1L);
        assertTrue(gotDeal.getBody().getData().getUser_id().getId() == 1277584L);
        assertTrue(gotDeal.getBody().getData().getStage_id() == 1L);
        assertTrue(gotDeal.getBody().getData().getValue().equals("10000"));

        ResponseEntity<PDDeleteResponse> deletedDeal = PS.deleteDeal(postedDeal.getBody().getData().getId());

        assertTrue(deletedDeal.getStatusCode() == HttpStatus.OK);
        assertTrue(deletedDeal.getBody().getSuccess());
    }

    @Test
    public void canGetAllDeals() {

        ResponseEntity<PDDealItemsResponse> res = PS.getAllDeals();
        System.out.println(res);
        assertEquals(res.getStatusCode(), HttpStatus.OK);
    }

    @Test
    public void canUpdateDeal() {

        PDDealSend deal = new PDDealSend();
        deal.setAdd_time("2005-07-05 12:21:12");
        deal.setVisible_to(3);
        deal.setCost(1000L);
        deal.setCurrency("GBP");
        deal.setLead_type("New Lead");
        deal.setLost_reason("No reason");
        deal.setOrg_id(44265L);
        deal.setPerson_id(40390L);
        deal.setUser_id(1363416L);
        deal.setPhase("Phase");
        deal.setProject_number("PROJ_1");
        deal.setStage_id(1);
        deal.setStatus("open");
        deal.setTitle("TEST DEAL");
        deal.setValue("10000");
        deal.setZuhlke_office("London");

        ResponseEntity<PDDealResponse> postedDeal = PS.postDeal(deal);
        assertTrue(postedDeal.getBody().getData().getTitle().equals("TEST DEAL"));

        System.out.println(postedDeal);
        deal.setId(postedDeal.getBody().getData().getId());
        deal.setTitle("DIFFERENT TITLE");

        ResponseEntity<PDDealResponse> updatedDeal = PS.updateDeal(deal);

        assertTrue(updatedDeal.getBody().getSuccess());
        System.out.println(updatedDeal);
        assertTrue(updatedDeal.
                getBody().
                getData().
                getTitle().
                equals("DIFFERENT TITLE"));

        PS.deleteDeal(updatedDeal.getBody().getData().getId());

    }

    @Test
    public void canPutDealList() {

        List<PDDealSend> deals = getSomeDeals();

        List<Long> postedIds = new ArrayList<>(PS.postDealList(deals).values());

        int index = 0;
        for (PDDealSend deal : deals) {
            deal.setId(postedIds.get(index));
            deal.setTitle("NOT THE SAME TITLE");
            index++;
        }

        Map<Long, Long> puttedIds = PS.updateDealList(deals);

        assertTrue(postedIds.size() == puttedIds.size());

        PS.deleteDealList(postedIds);

    }

    public List<PDDealSend> getSomeDeals(){

        List<PDDealSend> deals = new ArrayList<>();

        PDDealSend deal = new PDDealSend();
        deal.setAdd_time("2005-07-05 12:21:12");
        deal.setVisible_to(3);
        deal.setCost(1000L);
        deal.setCurrency("GBP");
        deal.setLead_type("New Lead");
        deal.setLost_reason("No reason");
        deal.setOrg_id(44265L);
        deal.setPerson_id(40390L);
        deal.setUser_id(1363416L);
        deal.setPhase("Phase");
        deal.setProject_number("PROJ_1");
        deal.setStage_id(1);
        deal.setStatus("lost");
        deal.setTitle("TEST DEAL1");
        deal.setValue("10000");
        deal.setZuhlke_office("London");
        deal.setV_id(1L);
        deal.setLost_time("2007-01-01 00:00:00");

        deals.add(deal);

        deal = new PDDealSend();
        deal.setAdd_time("2005-07-05 12:21:12");
        deal.setVisible_to(3);
        deal.setCurrency("GBP");
        deal.setLead_type("New Lead");
        deal.setLost_reason("No reason");
        deal.setOrg_id(44265L);
        deal.setPerson_id(40390L);
        deal.setUser_id(1363416L);
        deal.setPhase("Phase");
        deal.setProject_number("PROJ_2");
        deal.setStage_id(1);
        deal.setStatus("open");
        deal.setTitle("TEST DEAL 2");
        deal.setValue("99999");
        deal.setZuhlke_office("London");
        deal.setV_id(2L);

        deals.add(deal);

        return deals;
    }


    /**
     * Organisations ===================================================================================================
     */

    @Test
    public void canPostOrganisation() {
        //Post org, check response says success
        String companyName = "TestPostCompany";
        PDOrganisationSend org = new PDOrganisationSend();
        org.setName(companyName);
        org.setVisible_to(3);
        org.setOwner_id(1L);

        ResponseEntity<PDOrganisationResponse> postResponse = PS.postOrganisation(org);
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
    public void canUpdateOrg() {
        ResponseEntity<PDOrganisationResponse> orgr;
        String companyName = "TestAddressCompany";
        Integer visibility = 3;

        PDOrganisationSend o = new PDOrganisationSend();
        o.setV_id(1L);
        o.setName(companyName);
        o.setVisible_to(3);
        o.setCreationTime("1984-01-01 00:00:00");

        //POST
        ResponseEntity<PDOrganisationResponse> postResponse = PS.postOrganisation(o);
        assertTrue(postResponse.getStatusCode() == HttpStatus.CREATED);
        assertTrue(postResponse.getBody().getSuccess());

        PDOrganisationReceived org = postResponse.getBody().getData();

        String newAddress = "test address";

        PDOrganisationSend updateOrg = new PDOrganisationSend(org);

        updateOrg.setAddress(newAddress);
        //PUT
        orgr = PS.updateOrganisation(updateOrg);

        assertTrue(orgr.getStatusCode() == HttpStatus.OK);
        assertTrue(orgr.getBody() != null);
        assertTrue(orgr.getBody().getSuccess());
        assertTrue(orgr.getBody().getData().getAddress().equals(newAddress));
        assertTrue(orgr.getBody().getData().getV_id() == 1L);
        assertTrue(orgr.getBody().getData().getCreationTime().equals("1984-01-01 00:00:00"));

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
        //assertTrue(!organisations.getAdditional_data().getPagination().getMore_items_in_collection());
        //assertTrue(organisations.getData() != null);
        //assertTrue(organisations.getData().get(0) != null);
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
        ResponseEntity<PDContactResponse> contactPostResponse = PS.postContact(contact);

        assertTrue(contactPostResponse.getBody() != null);
        assertTrue(contactPostResponse.getBody().getSuccess());
        assertTrue(contactPostResponse.getBody().getData().getName().equals("Test Name"));
        assertTrue(contactPostResponse.getBody().getData().getEmail().get(0).getValue().equals("Test@Test.test"));
        assertTrue(contactPostResponse.getBody().getData().getPhone().get(0).getValue().equals("0987654321"));

        //get contact for org
        ResponseEntity<PDContactListReceived> contactResponse = PS.getContactsForOrganisation(org_id);

        assertTrue(contactResponse.getBody() != null);
        assertTrue(contactResponse.getBody().getSuccess());

        String name = contactResponse.getBody().getData().get(0).getName();
        String first_name = contactResponse.getBody().getData().get(0).getFirst_name();
        String surname = contactResponse.getBody().getData().get(0).getLast_name();

        String email = contactResponse.getBody().getData().get(0).getEmail().get(0).getValue();
        String phone = contactResponse.getBody().getData().get(0).getPhone().get(0).getValue();

        Long recievedOrgId = contactResponse.getBody().getData().get(0).getOrg_id().getValue();
        //check equal
        assertTrue(name.equals("Test Name"));
        assertTrue(first_name.equals("Test"));
        assertTrue(surname.equals("Name"));

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

    /**
     * Contacts ========================================================================================================
     */

    @Test
    public void canPutContact() {

        //first Test no need to assert, taken care of in prev test
        String companyName = "TestPostCompany";
        Integer visibility = 3;

        ResponseEntity<PDOrganisationResponse> postResponse = PS.postOrganisation(companyName, visibility);

        //post contact for org
        Long org_id = postResponse.getBody().getData().getId();

        PDContactSend contact = new PDContactSend(org_id, "Test Name", "Test@Test.test", "0987654321");
        ResponseEntity<PDContactResponse> contactPostResponse = PS.postContact(contact);

        //modify contact
        contact.changePrimaryEmail("loseweight@kfc.com");
        contact.changePrimaryPhone("1234567890");
        contact.setId(contactPostResponse.getBody().getData().getId());
        contact.setV_id(4L);
        contact.setCreationTime("1984-01-01 00:00:00");

        //putagain
        ResponseEntity<PDContactResponse> contactPutResponse = PS.updateContact(contact);
        assertTrue(contactPutResponse.getStatusCode() == HttpStatus.OK);
        assertTrue(contactPutResponse.getBody().getSuccess());
        assertTrue(contactPutResponse.getBody().getData().getName().equals("Test Name"));
        assertTrue(contactPutResponse.getBody().getData().getEmail().get(0).getValue().equals("loseweight@kfc.com"));
        assertTrue(contactPutResponse.getBody().getData().getPhone().get(0).getValue().equals("1234567890"));
        assertTrue(contactPutResponse.getBody().getData().getV_id() == 4L);
        assertTrue(contactPutResponse.getBody().getData().getCreationTime().equals("1984-01-01 00:00:00"));



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

        Orgres = PS.postOrganisation("Yuuhuuu!",3);
        assertTrue(Orgres.getStatusCode() == HttpStatus.CREATED);

        Long orgid = Orgres.getBody().getData().getId();

        for(int i = 0; i < contacts.size(); i++){
            contacts.get(i).setOrg_id(orgid);
        }

        System.out.println(contacts.size());
        Map<Long, Long> map = PS.postContactList(contacts);

        System.out.println(map.size());
        System.out.println(map.keySet().size());
        System.out.println(map.values().size());

        assertTrue(map.keySet().size() == contacts.size());
        List<Long> contactsDeleted = new ArrayList<>();

        for(Long id : map.values()){
            delres = PS.deleteContact(id);
            assertTrue(delres.getBody().getSuccess());
            contactsDeleted.add(delres.getBody().getData().getId());
        }

        PS.deleteOrganisation(orgid);

        assertEquals(map.values().size(),contactsDeleted.size());
    }

    @Test
    public void canPutContactList() {
        List<PDContactSend> contacts = assignMatchingPDContacts();
        ResponseEntity<PDOrganisationResponse> Orgres;
        ResponseEntity<PDDeleteResponse> delres;
        List<Long> contactsDeleted = new ArrayList<>();


        Orgres = PS.postOrganisation("Gotham City",3);
        assertTrue(Orgres.getStatusCode() == HttpStatus.CREATED);

        Long orgid = Orgres.getBody().getData().getId();
        for(int i = 0; i < contacts.size(); i++){
            contacts.get(i).setOrg_id(orgid);
        }
        Map<Long, Long> map = PS.postContactList(contacts);

        contacts.get(0).getEmail().add(new ContactDetail("BatMobile@batmail.com", true));
        contacts.get(1).getEmail().add(new ContactDetail("Robin@day.com", false));
        contacts.get(2).getEmail().add(new ContactDetail("smile@me.com", false));
        contacts.get(3).getEmail().add(new ContactDetail("rat@inthepack.com",true));

        contacts.get(0).setId(map.get(contacts.get(0).getV_id()));
        contacts.get(1).setId(map.get(contacts.get(1).getV_id()));
        contacts.get(2).setId(map.get(contacts.get(2).getV_id()));
        contacts.get(3).setId(map.get(contacts.get(3).getV_id()));

        int sizeOfMapBeforePut = map.values().size();

        map.putAll( PS.putContactList(contacts) );
        assertTrue(map.keySet().size() == sizeOfMapBeforePut);

        for(Long id : map.values()){
            delres = PS.deleteContact(id);
            assertTrue(delres.getBody().getSuccess());
            contactsDeleted.add(delres.getBody().getData().getId());
        }

        PS.deleteOrganisation(orgid);

        assertEquals(map.values().size(),contactsDeleted.size());

    }

    @Test
    public void canGetAllContacts() {
        ResponseEntity<PDContactListReceived> contacts = PS.getAllContacts();

        assertTrue(contacts.getStatusCode() == HttpStatus.OK);
        assertTrue(contacts.getBody().getSuccess());

        System.out.println(contacts.getBody().toPrettyJSON());
        try {
            FileWriter file = new FileWriter("contactsResponse.txt");
            file.write(contacts.getBody().toPrettyJSON());
            file.close();
        } catch (Exception e) {
            System.out.println("Failed to output to file");
        }
        //assertTrue( ! contacts.getBody().getData().isEmpty());
        //assertTrue(contacts.getBody().getData().get(0) != null);
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
        c1.setV_id(12421L);

        PDContactSend c2 = new PDContactSend();
        c2.setName("Robin");
        ContactDetail e2 = new ContactDetail();
        e2.setPrimary(true);
        e2.setValue("Robin@night.com");
        c2.getEmail().add(e2);
        c2.setV_id(12435L);

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
        c3.setV_id(98754L);

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
        c4.setV_id(3457L);

        List<PDContactSend> pdContactSends = new ArrayList<>();
        pdContactSends.add(c1);
        pdContactSends.add(c2);
        pdContactSends.add(c3);
        pdContactSends.add(c4);

        return pdContactSends;
    }

    /**
     * Users ===========================================================================================================
     */

    @Test //will need to be changed for real pipedrive data
    public void canGetAllUsers(){
        ResponseEntity<PDUserItemsResponse> res = null;

        res = PS.getAllUsers();

        assertTrue(res.getStatusCode() == HttpStatus.OK);
        assertTrue(res.getBody() != null);
        assertTrue(!res.getBody().getData().isEmpty());
        assertTrue(res.getBody().getData().size() == 13);
        assertTrue(res.getBody().getData().get(0).getEmail() != null);
        assertTrue(res.getBody().getData().get(0).getId() != null);

    }

    /**
     * Followers =======================================================================================================
     */

    @Test
    public void canPostFollowers(){
        PDContactSend guy = new PDContactSend();

        guy.setName("Guy Fawkes");
        guy.setActive_flag(true);
        guy.setVisible_to(3);

        Long id = PS.postContact(guy).getBody().getData().getId();

        PDFollower f = new PDFollower(id, 1363410L);

        ResponseEntity<String> res = PS.postFollowerToContact(f);

        assertTrue(res.getStatusCode() == HttpStatus.CREATED);
        assertTrue( ! res.getBody().isEmpty());
        assertTrue(res.getBody().contains("\"success\":true,"));
        assertTrue(res.getBody().contains("\"person_id\":" + id + ","));
        assertTrue(res.getBody().contains("\"user_id\":" + 1363410L + ","));
        PS.deleteContact(id);
    }

    /**
     * Organisation Relationships ======================================================================================
     */

    @Test
    public void canPostOrganisationRelationship() {

        PDOrganisationSend orgParent = new PDOrganisationSend();
        PDOrganisationSend orgChild  = new PDOrganisationSend();

        orgParent.setName("PARENT");
        orgChild.setName("CHILD");

        Long parentId = PS.postOrganisation(orgParent).getBody().getData().getId();
        System.out.println("POSTED: " + parentId);
        Long childId  = PS.postOrganisation(orgChild).getBody().getData().getId();
        System.out.println("POSTED: " + childId);

        PDRelationship rel = new PDRelationship(parentId, childId);

        //parent should always be first argument
        ResponseEntity<String> res = PS.postOrganisationRelationship(rel);

        assertTrue(res.getBody().contains("\"success\":true"));
        assertTrue(res.getBody().contains("\"type\":\"parent\""));
        assertTrue(res.getBody().contains("\"rel_owner_org_id\":{"));
        assertTrue(res.getBody().contains("\"rel_linked_org_id\":{"));
        assertTrue(res.getBody().contains("\"active_flag\":true"));

        PS.deleteOrganisation(parentId);
        PS.deleteOrganisation(childId);

    }
//
//    @Test
//    public void howManyDuplicateContacts() {
//        ResponseEntity<PDContactListReceived> contacts = PS.getAllContacts();
//
//        assertTrue(contacts.getStatusCode() == HttpStatus.OK);
//        assertTrue(contacts.getBody().getSuccess());
//        int iinner = 0;
//        int iouter = 0;
//        int matches = 0;
//
//        List<String> matchingnames = new ArrayList<>();
//
//        for (PDContactReceived a : contacts.getBody().getData()){
//            iinner = 0;
//            iouter++;
//            for(PDContactReceived b : contacts.getBody().getData()){
//                iinner++;
//                if((iinner != iouter)){
//
//                    for(ContactDetail d : a.getEmail()){
//                        for(ContactDetail e : b.getEmail()){
//                            if(d.getValue().equals(e.getValue()) && !d.getValue().isEmpty() && !e.getValue().isEmpty()){
//                                matches++;
//                                matchingnames.add(b.getName());
//                            }
//                        }
//                    }
//                }
//            }
//            for(int i = 0; i < matchingnames.size(); i++){
//                System.out.println(a.getName() + " " + a.getEmail() + " -> " + matchingnames.get(i));
//            }
//            matchingnames.clear();
//        }
//        System.out.println("Found " + matches + " duplicate contacts");
//
//    }
    /**
     * Activities =======================================================================================================
     */

    @Test
    public void canPostActivity(){
        PDActivitySend activity = new PDActivitySend();
        activity.setUser_id(1277584L);
        activity.setDone(false);
        activity.setType("call");
        activity.setDue_date("2142-01-01");
        activity.setDue_time("23:55:12");
        activity.setSubject("PAC Expansion");
        activity.setNote("Be diplomatic");

        PDOrganisationSend o = new PDOrganisationSend();
        o.setActive_flag(true);
        o.setVisible_to(3);
        o.setAddress("10 Downing Street, London");
        o.setName("Private Contractor");
        o.setV_id(1L);

        ResponseEntity<PDOrganisationResponse> res;
        res = PS.postOrganisation(o);
        assertTrue(res.getStatusCode() == HttpStatus.CREATED);
        assertTrue(res.getBody().getSuccess());

        Long orgid = res.getBody().getData().getId();

        PDContactSend c = new PDContactSend();
        c.setOrg_id(orgid);
        c.setActive_flag(true);
        c.setName("Vladimir Butin");
        c.changePrimaryEmail("power@me.ru");

        ResponseEntity<PDContactResponse> resC = PS.postContact(c);

        assertTrue(resC.getStatusCode() == HttpStatus.CREATED);
        assertTrue(resC.getBody().getSuccess());

        Long contid = resC.getBody().getData().getId();

        PDDealSend d = new PDDealSend();
        d.setTitle("EU PAC Peace Talks");
        d.setVisible_to(3);
        d.setCost(0L);
        d.setOrg_id(orgid);
        d.setPerson_id(contid);
        d.setPhase("00_EXTERNAL");
        d.setProject_number("007");
        d.setStatus("open");
        d.setUser_id(1363403L);
        d.setZuhlke_office("London");

        ResponseEntity<PDDealResponse> resD = PS.postDeal(d);

        assertTrue(resD.getStatusCode() == HttpStatus.CREATED);
        assertTrue(resD.getBody().getSuccess());

        Long dealid = resD.getBody().getData().getId();

        activity.setOrg_id(orgid);
        activity.setPerson_id(contid);
        activity.setDeal_id(dealid);

        ResponseEntity<PDActivityResponse> resA = PS.postActivity(activity);

        Long actid = resA.getBody().getData().getId();

        assertTrue(resA.getStatusCode() == HttpStatus.CREATED);
        assertTrue(resA.getBody().getSuccess());
        assertTrue(resA.getBody().getData().getActive_flag());
        assertTrue(resA.getBody().getData().getType().equals("call"));
        assertTrue( ! resA.getBody().getData().getDone());
        assertTrue(resA.getBody().getData().getDue_date().equals("2142-01-01"));
        assertTrue(resA.getBody().getData().getDue_time().equals("23:55"));
        assertTrue(resA.getBody().getData().getId().longValue() == actid);
        assertTrue(resA.getBody().getData().getSubject().equals("PAC Expansion"));
        assertTrue(resA.getBody().getData().getNote().equals("Be diplomatic"));
        assertTrue(resA.getBody().getData().getOrg_id() == orgid.longValue());
        assertTrue(resA.getBody().getData().getPerson_id() == contid.longValue());
        assertTrue(resA.getBody().getData().getDeal_id() == dealid.longValue());
        assertTrue(resA.getBody().getData().getUser_id() == 1277584L);

        ResponseEntity<PDDeleteResponse> delres;

        delres = PS.deleteDeal(dealid);
        assertTrue(delres.getBody().getSuccess());

        delres = PS.deleteContact(contid);
        assertTrue(delres.getBody().getSuccess());

        delres = PS.deleteOrganisation(orgid);
        assertTrue(delres.getBody().getSuccess());

        delres = PS.deleteActivity(actid);
        assertTrue(delres.getBody().getSuccess());

    }

    @Test
    public void canPostActivityList(){
        List<PDActivitySend> acts = getSomeActivities();

        List<Long> idsPosted = PS.postActivityList(acts);

        assertTrue(acts.size() == idsPosted.size());

        List<Long> idsDeleted = PS.deleteActivityList(idsPosted);
        assertTrue(idsDeleted.equals(idsPosted));

    }

    public List<PDActivitySend> getSomeActivities(){
        List<PDActivitySend> activities = new ArrayList<>();

        PDActivitySend activity = new PDActivitySend();
        activity.setUser_id(1277584L);
        activity.setDone(false);
        activity.setType("call");
        activity.setDue_date("2142-01-01");
        activity.setDue_time("23:55:12");
        activity.setSubject("PAC Expansion");
        activity.setNote("Be diplomatic");
        activities.add(activity);

        activity = new PDActivitySend();
        activity.setUser_id(1277584L);
        activity.setDone(false);
        activity.setType("call");
        activity.setDue_date("2142-01-01");
        activity.setDue_time("23:55:12");
        activity.setSubject("TESTACTIVITY");
        activity.setNote("Note");
        activities.add(activity);

        return activities;
    }

    @Test
    public void canGetActivity(){
        PDActivitySend activity = new PDActivitySend();
        activity.setUser_id(1277584L);
        activity.setDone(false);
        activity.setType("call");
        activity.setDue_date("2142-01-01");
        activity.setDue_time("23:55:12");
        activity.setSubject("PAC Expansion");
        activity.setNote("Be diplomatic");

        PDOrganisationSend o = new PDOrganisationSend();
        o.setActive_flag(true);
        o.setVisible_to(3);
        o.setAddress("10 Downing Street, London");
        o.setName("Private Contractor");
        o.setV_id(1L);

        ResponseEntity<PDOrganisationResponse> res;
        res = PS.postOrganisation(o);
        assertTrue(res.getStatusCode() == HttpStatus.CREATED);
        assertTrue(res.getBody().getSuccess());

        Long orgid = res.getBody().getData().getId();

        PDContactSend c = new PDContactSend();
        c.setOrg_id(orgid);
        c.setActive_flag(true);
        c.setName("Vladimir Butin");
        c.changePrimaryEmail("power@me.ru");
        c.setPosition("eagsd");

        ResponseEntity<PDContactResponse> resC = PS.postContact(c);

        assertTrue(resC.getStatusCode() == HttpStatus.CREATED);
        assertTrue(resC.getBody().getSuccess());

        Long contid = resC.getBody().getData().getId();

        PDDealSend d = new PDDealSend();
        d.setTitle("EU PAC Peace Talks");
        d.setVisible_to(3);
        d.setCost(0L);
        d.setOrg_id(orgid);
        d.setPerson_id(contid);
        d.setPhase("00_EXTERNAL");
        d.setProject_number("007");
        d.setStatus("open");
        d.setUser_id(1277584L);
        d.setZuhlke_office("London");

        ResponseEntity<PDDealResponse> resD = PS.postDeal(d);

        assertTrue(resD.getStatusCode() == HttpStatus.CREATED);
        assertTrue(resD.getBody().getSuccess());

        Long dealid = resD.getBody().getData().getId();

        activity.setOrg_id(orgid);
        activity.setPerson_id(contid);
        activity.setDeal_id(dealid);

        ResponseEntity<PDActivityResponse> resA = PS.postActivity(activity);

        Long actid = resA.getBody().getData().getId();

        resA = PS.getActivity(actid);

        PDActivityReceived a = resA.getBody().getData();

        assertTrue(resA.getStatusCode() == HttpStatus.OK);
        assertTrue(resA.getBody().getSuccess());
        assertTrue(a.getActive_flag());
        assertTrue(a.getType().equals("call"));
        assertTrue( ! a.getDone());
        assertTrue(a.getDue_date().equals("2142-01-01"));
        assertTrue(a.getDue_time().equals("23:55"));
        assertTrue(a.getId().longValue() == actid);
        assertTrue(a.getSubject().equals("PAC Expansion"));
        assertTrue(a.getNote().equals("Be diplomatic"));
        assertTrue(a.getOrg_id() == orgid.longValue());
        assertTrue(a.getPerson_id() == contid.longValue());
        assertTrue(a.getDeal_id() == dealid.longValue());
        assertTrue(a.getUser_id() == 1277584L);

        ResponseEntity<PDDeleteResponse> delres;

        delres = PS.deleteDeal(dealid);
        assertTrue(delres.getBody().getSuccess());

        delres = PS.deleteContact(contid);
        assertTrue(delres.getBody().getSuccess());

        delres = PS.deleteOrganisation(orgid);
        assertTrue(delres.getBody().getSuccess());

        delres = PS.deleteActivity(actid);
        assertTrue(delres.getBody().getSuccess());

    }

    @Test
    public void canPutActivity(){

        PDActivitySend activity = new PDActivitySend();
        activity.setUser_id(1277584L);
        activity.setDone(false);
        activity.setType("call");
        activity.setDue_date("2142-01-01");
        activity.setDue_time("23:55:12");
        activity.setSubject("PAC Expansion");
        activity.setNote("Be diplomatic");

        PDOrganisationSend o = new PDOrganisationSend();
        o.setActive_flag(true);
        o.setVisible_to(3);
        o.setAddress("10 Downing Street, London");
        o.setName("Private Contractor");
        o.setV_id(1L);

        ResponseEntity<PDOrganisationResponse> res;
        res = PS.postOrganisation(o);
        assertTrue(res.getStatusCode() == HttpStatus.CREATED);
        assertTrue(res.getBody().getSuccess());

        Long orgid = res.getBody().getData().getId();

        PDContactSend c = new PDContactSend();
        c.setOrg_id(orgid);
        c.setActive_flag(true);
        c.setName("Vladimir Butin");
        c.changePrimaryEmail("power@me.ru");

        ResponseEntity<PDContactResponse> resC = PS.postContact(c);

        assertTrue(resC.getStatusCode() == HttpStatus.CREATED);
        assertTrue(resC.getBody().getSuccess());

        Long contid = resC.getBody().getData().getId();

        PDDealSend d = new PDDealSend();
        d.setTitle("EU PAC Peace Talks");
        d.setVisible_to(3);
        d.setCost(0L);
        d.setOrg_id(orgid);
        d.setPerson_id(contid);
        d.setPhase("00_EXTERNAL");
        d.setProject_number("007");
        d.setStatus("open");
        d.setUser_id(1277584L);
        d.setZuhlke_office("London");

        ResponseEntity<PDDealResponse> resD = PS.postDeal(d);

        assertTrue(resD.getStatusCode() == HttpStatus.CREATED);
        assertTrue(resD.getBody().getSuccess());

        Long dealid = resD.getBody().getData().getId();

        activity.setOrg_id(orgid);
        activity.setPerson_id(contid);
        activity.setDeal_id(dealid);

        ResponseEntity<PDActivityResponse> resA = PS.postActivity(activity);

        Long actid = resA.getBody().getData().getId();

        activity.setSubject("Avoid complications");
        activity.setId(actid);

        resA = PS.putActivity(activity);

        assertTrue(resA.getStatusCode() == HttpStatus.OK);
        assertTrue(resA.getBody().getSuccess());
        assertTrue(resA.getBody().getData().getSubject().equals("Avoid complications"));

        ResponseEntity<PDDeleteResponse> delres;

        delres = PS.deleteDeal(dealid);
        assertTrue(delres.getBody().getSuccess());

        delres = PS.deleteContact(contid);
        assertTrue(delres.getBody().getSuccess());

        delres = PS.deleteOrganisation(orgid);
        assertTrue(delres.getBody().getSuccess());

        delres = PS.deleteActivity(actid);
        assertTrue(delres.getBody().getSuccess());
    }


    @Test
    public void canPutActivityList(){
        List<PDActivitySend> activities = getSomeActivities();

        List<Long> idsPosted = PS.postActivityList(activities);

        for(int i = 0; i < idsPosted.size(); i++){
            activities.get(i).setId(idsPosted.get(i));
            activities.get(i).setSubject("Changed Subject");
        }

        List<Long> idsPut = PS.putActivitesList(activities);

        assertTrue(idsPosted.equals(idsPut));

        List<Long> idsDeleted = PS.deleteActivityList(idsPosted);
        assertTrue(idsDeleted.equals(idsPosted));
    }

    /**
     * Delete ==========================================================================================================
     */

    @Test
    public void deletedAllOrganisations(){
        ResponseEntity<PDOrganisationResponse> org;
        for(Long i : idsDeleted){
            org = PS.getOrganisation(i);
            System.out.println("Is Org " + i + " deleted?" + org.getBody().getData().getActive_flag());
            assertTrue(!org.getBody().getData().getActive_flag());
        }
    }

    @Test @Ignore
    public void canPostExpectedCloseDate() {
        PDDealSend deal = new PDDealSend();
        deal.setTitle("TESTING TIMELINE VIEW SUPPORT");
        deal.setExp_close_date("2015-06-06");

        PS.postDeal(deal);
    }

    @Test @Ignore
    public void canPostActivityAddTime() {
        PDActivitySend activity = new PDActivitySend();
        activity.setSubject("TEST ACTIVITY");
        activity.setType("call");
        activity.setAdd_time("2014-01-01 09:30:00");

        System.out.println(PS.postActivity(activity).getBody().getData().getId());
    }

//    @Test
//    public void fullVsSplitAddress(){
//        PDOrganisationSend pds = new PDOrganisationSend();
//        pds.setName("batco");
//        Long id = PS.postOrganisation(pds).getBody().getData().getId();
//
//        PDOrganisationSend newPds = new PDOrganisationSend();
//        newPds.setId(id);
////        newPds.setAddress_country("Bat country");
////        newPds.setAddress_locality("cave avenue");
////        newPds.setAddress_postal_code("899");
////        newPds.setAddress_route("up n down");
////        newPds.setAddress_street_number("6");
////        newPds.setAddress_subpremise("hole 2");
//
//        newPds.setAddress("145 Fleet St, London EC4A 2BU");
//
//        PS.updateOrganisation(newPds);
//
//        PDOrganisationReceived pdr = PS.getOrganisation(id).getBody().getData();
//
//        System.out.println(pdr.getName());
//        System.out.println(pdr.ge);
//        System.out.println(pdr.getAddress());
//
//        System.out.println(pdr.getId());
//
//        PS.deleteOrganisation(id);
//    }

    @Test
    public void canGetAllActivitiesForOrganisation(){
        PDOrganisationSend org = new PDOrganisationSend();
        org.setName("TestName");
        org.setActive_flag(true);
        org.setVisible_to(3);

        List<Long> controlActivityIds = new ArrayList<>();
        Long orgId = PS.postOrganisation(org).getBody().getData().getId();

        for(int i = 0; i < 20; i++){
            PDActivitySend act = new PDActivitySend();
            act.setSubject(i + "th Org test activity");
            act.setType("call");
            act.setOrg_id(orgId);

            controlActivityIds.add(PS.postActivity(act).getBody().getData().getId());
        }
        
       
    }
    @Test
    public void canGetAllDealsForOrg(){
        PDOrganisationSend org = new PDOrganisationSend();
        org.setName("TestName");
        org.setActive_flag(true);
        org.setVisible_to(3);

        List<Long> controlDealIds = new ArrayList<>();
        Long orgId = PS.postOrganisation(org).getBody().getData().getId();

        for(int i = 0; i < 20; i++){
            PDDealSend deal = new PDDealSend();
            deal.setTitle(i + "th Org test Deal");
            deal.setOrg_id(orgId);

            controlDealIds.add(PS.postDeal(deal).getBody().getData().getId());
        }

        List<Long> dealIds = PS.getAllDealsForOrganisation(orgId).stream()
                .map(PDDealReceived::getId)
                .collect(toList());

        controlDealIds.forEach(id -> assertTrue(dealIds.contains(id)));

        PS.deleteDealList(controlDealIds);
        PS.deleteOrganisation(orgId);
    }

    //TODO: TEST wether first_name and last_name actually get propagated
}
