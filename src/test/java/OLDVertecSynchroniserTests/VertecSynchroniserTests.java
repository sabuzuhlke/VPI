package OLDVertecSynchroniserTests;

import VPI.Application;
import VPI.PDClasses.*;
import VPI.PDClasses.Contacts.ContactDetail;
import VPI.PDClasses.Contacts.OrgId;
import VPI.PDClasses.Contacts.PDContactReceived;
import VPI.PDClasses.Deals.PDDealReceived;
import VPI.PDClasses.Deals.PDDealSend;
import VPI.PDClasses.Deals.PDPersonId;
import VPI.PDClasses.Organisations.PDOrganisationReceived;
import VPI.PDClasses.Organisations.PDRelationship;
import VPI.PDClasses.Users.PDUser;
import VPI.VertecClasses.VertecOrganisations.JSONContact;
import VPI.VertecClasses.VertecOrganisations.JSONOrganisation;
import VPI.VertecClasses.VertecProjects.JSONProject;
import VPI.VertecSynchroniser;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static VPI.VertecSynchroniser.extractVID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by sabu on 29/04/2016.
 */

@RunWith(SpringJUnit4ClassRunner.class)
public class VertecSynchroniserTests {

    private VertecSynchroniser sync;

    @Before
    public void setUp() {
        this.sync = new VertecSynchroniser();
    }


    @Test
    public void correctlyResolvesOrganisations(){

        Map<String, Long> t = new HashMap<>();
        t.put("a@eat.com", 1L);

        sync.setTeamIdMap(t);

        List<JSONOrganisation> vOrgs = getMockVOrgs();

        List<PDOrganisationReceived> pOrgs = getMockPOrgs();


        this.sync.testresolveOrganisationsAndNestedContacts(vOrgs,pOrgs);


        assertTrue(sync.organisationPostList.size() == 1);
        assertTrue(sync.organisationPutList.size() == 1);

        assertTrue(sync.organisationPostList.get(0).getName().equals(vOrgs.get(2).getName()));
        assertTrue(sync.organisationPutList.get(0).getName().equals(vOrgs.get(1).getName()));
        sync.organisationPostList.clear();
        sync.organisationPutList.clear();

    }

    @Test
    public void canPostOrgsCorrectly(){

        List<List<Long>> Ids = new ArrayList<>();
        List<Long> orgsDel = null;
        List<Long> contsDel = null;

        sync.organisationPostList = getMockVOrgs();
        Ids = sync.postVOrganisations();

        assertTrue(Ids.get(0).size() == 3);
        assertTrue(Ids.get(1).size() == 6);

        orgsDel = sync.getPDS().deleteOrganisationList(Ids.get(0));
        contsDel = sync.getPDS().deleteContactList(Ids.get(1));

        assertTrue(orgsDel.size() == Ids.get(0).size());
        assertTrue(contsDel.size() == Ids.get(1).size());

        sync.clear();

    }


    public List<JSONOrganisation> getMockVOrgs(){
        JSONOrganisation o = new JSONOrganisation();
        List<JSONOrganisation> orgs = new ArrayList<>();

        JSONContact c = new JSONContact();
        List<JSONContact> cnts = new ArrayList<>();

        c.setOwner("a@eat.com");
        c.setModified("NOW");
        c.setObjid(1L);
        c.setEmail("habbababba@babba.com");
        c.setFirstName("John");
        c.setSurname("Wayne");
        c.setPhone("37925");
        c.setMobile("8974");
        cnts.add(c);
        c = new JSONContact();

        c.setOwner("a@eat.com");
        c.setModified("NOW");
        c.setObjid(2L);
        c.setEmail("MOJOJOJO@babba.com");
        c.setFirstName("Mojo");
        c.setSurname("Jojo");
        c.setPhone("234079");
        c.setMobile("75612");
        cnts.add(c);


        o.setObjid(5L);
        o.setName("SAME ORG");
        o.setAdditionalAdress("No");
        o.setCity("Murica City");
        o.setCountry("Murica!");
        o.setStreetAddress("11 Here");
        o.setModified("NOW");
        o.setOwner("a@eat.com");
        o.setZip("9938");
        o.setContacts(cnts);
        o.setParentOrganisationId(6L);
        orgs.add(o);

        o = new JSONOrganisation();
        o.setObjid(6L);
        o.setName("SAME NAME, DIFF DETAILS");
        o.setAdditionalAdress("No");
        o.setCity("Murica City");
        o.setCountry("Murica!");
        o.setStreetAddress("11 Here");
        o.setModified("NOW");
        o.setOwner("a@eat.com");
        o.setZip("9938");
        o.setContacts(cnts);
        o.setParentOrganisationId(7L);
        orgs.add(o);

        o = new JSONOrganisation();
        o.setObjid(7L);
        o.setName("NEW ORG");
        o.setAdditionalAdress("No");
        o.setCity("Murica City");
        o.setCountry("Murica!");
        o.setStreetAddress("11 Here");
        o.setModified("NOW");
        o.setOwner("a@eat.com");
        o.setZip("9938");
        o.setContacts(cnts);
        orgs.add(o);

        return orgs;
    }

    public List<PDOrganisationReceived> getMockPOrgs(){
        PDOrganisationReceived o = new PDOrganisationReceived();
        List<PDOrganisationReceived> orgs = new ArrayList<>();

        o.setName("SAME ORG");
        o.setActive_flag(true);
        o.setAddress("No, 11 Here, Murica City, 9938, Murica!");
        o.setCompany_id(1L);
        o.setV_id(5L);
        o.setOwner_id(new PDOwner());
        o.getOwner_id().setId(1L);
        orgs.add(o);

        o = new PDOrganisationReceived();
        o.setName("SAME NAME, DIFF DETAILS");
        o.setActive_flag(true);
        o.setAddress("YES, 12 Here, NOT Murica City, 9938, NOT Murica!");
        o.setCompany_id(2L);
        o.setV_id(6L);
        o.setOwner_id(new PDOwner());
        o.getOwner_id().setId(1L);
        orgs.add(o);


        return orgs;
    }

    @Test
    public void correctlyResolvesDanglingContacts() {
        Map<String, Long> t = new HashMap<>();
        t.put("a@eat.com", 1L);

        sync.setTeamIdMap(t);

        List<JSONContact> dangling = getListOfDanglingVertecContacts();
        List<PDContactReceived> pdContacts = getListOfDanglingPipedriveContacts();

        sync.compareContacts(dangling, pdContacts);

        System.out.println("ContactPutlist.size: " + sync.contactPutList.size());

        assertTrue(sync.contactPutList.size() == 1);
        assertTrue(sync.contactPutList.get(0).getName().equals("c2 surname2"));
        assertTrue(sync.contactPutList.get(0).getEmail().size() == 2);
        assertTrue(sync.contactPutList.get(0).getEmail().get(1).getPrimary());
        assertTrue(sync.contactPutList.get(0).getEmail().get(1).getValue().equals("email"));
        assertTrue( ! sync.contactPutList.get(0).getEmail().get(0).getPrimary());
        assertTrue(sync.contactPutList.get(0).getEmail().get(0).getValue().equals("differentemail"));
        assertTrue(sync.contactPutList.get(0).getPhone().size() == 4);
        assertTrue( ! sync.contactPutList.get(0).getPhone().get(0).getPrimary());
        assertTrue(sync.contactPutList.get(0).getPhone().get(0).getValue().equals("differentphone"));
        assertTrue( ! sync.contactPutList.get(0).getPhone().get(1).getPrimary());
        assertTrue(sync.contactPutList.get(0).getPhone().get(1).getValue().equals("differentmobile"));
        assertTrue(sync.contactPutList.get(0).getPhone().get(3).getPrimary());
        assertTrue(sync.contactPutList.get(0).getPhone().get(3).getValue().equals("phone"));
        assertTrue( ! sync.contactPutList.get(0).getPhone().get(2).getPrimary());
        assertTrue(sync.contactPutList.get(0).getPhone().get(2).getValue().equals("mobile"));
        assertTrue(sync.contactPutList.get(0).getOwner_id() == 1L);


        assertTrue(sync.contactPostList.size() == 1);
        assertTrue(sync.contactPostList.get(0).getName().equals("c3 surname3"));
        assertTrue(sync.contactPostList.get(0).getEmail().size() == 1);
        assertTrue(sync.contactPostList.get(0).getEmail().get(0).getPrimary());
        assertTrue(sync.contactPostList.get(0).getEmail().get(0).getValue().equals("email"));
        assertTrue(sync.contactPostList.get(0).getPhone().size() == 2);
        assertTrue(sync.contactPostList.get(0).getPhone().get(0).getPrimary());
        assertTrue(sync.contactPostList.get(0).getPhone().get(0).getValue().equals("phone"));
        assertTrue( ! sync.contactPostList.get(0).getPhone().get(1).getPrimary());
        assertTrue(sync.contactPostList.get(0).getPhone().get(1).getValue().equals("mobile"));

        sync.contactPutList.clear();
        sync.contactPostList.clear();

    }

    @Test
    public void correctlyResovlesContactDetails() {
        Map<String, Long> t = new HashMap<>();
        t.put("a@eat.com", 1L);

        sync.setTeamIdMap(t);

        List<JSONContact> dangling = getListOfDanglingVertecContacts();
        List<PDContactReceived> pdContacts = getListOfDanglingPipedriveContacts();

        JSONContact vc1 = dangling.get(1);
        PDContactReceived pc1 = pdContacts.get(1);

        assertTrue(sync.resolveContactDetails(vc1, pc1));

    }

    public List<PDContactReceived> getListOfDanglingPipedriveContacts() {
        List<PDContactReceived> list = new ArrayList<>();
        PDContactReceived c1 = new PDContactReceived();
        c1.setName("c1 surname1");
        c1.setOrg_id(null);
        c1.setActive_flag(true);
        c1.setV_id(1L);

        List<ContactDetail> emails = new ArrayList<>();
        ContactDetail email1 = new ContactDetail("email", true);
        emails.add(email1);
        c1.setEmail(emails);

        List<ContactDetail> phones = new ArrayList<>();
        ContactDetail phone1 = new ContactDetail("phone", true);
        ContactDetail phone2 = new ContactDetail("mobile", false);
        phones.add(phone1);
        phones.add(phone2);
        c1.setPhone(phones);
        c1.setVisible_to(3);
        c1.setOwner_id(new PDOwner());
        c1.getOwner_id().setId(1L);




        PDContactReceived c2 = new PDContactReceived();
        c2.setName("c2 surname2");
        c2.setOrg_id(null);
        c2.setActive_flag(true);
        c2.setV_id(2L);
        c2.setOwner_id(new PDOwner());
        c2.getOwner_id().setId(1L);

        emails = new ArrayList<>();
        email1 = new ContactDetail("differentemail", true);
        emails.add(email1);
        c2.setEmail(emails);

        phones = new ArrayList<>();
        phone1 = new ContactDetail("differentphone", true);
        phone2 = new ContactDetail("differentmobile", false);
        phones.add(phone1);
        phones.add(phone2);
        c2.setPhone(phones);
        c2.setVisible_to(3);





        PDContactReceived c4 = new PDContactReceived();
        c4.setName("c4 surname4");
        c4.setOrg_id(null);
        c4.setActive_flag(true);
        c4.setV_id(4L);

        emails = new ArrayList<>();
        email1 = new ContactDetail("email4", true);
        emails.add(email1);
        c4.setEmail(emails);

        phones = new ArrayList<>();
        phone1 = new ContactDetail("phone4", true);
        phone2 = new ContactDetail("mobile4", false);
        phones.add(phone1);
        phones.add(phone2);
        c4.setPhone(phones);
        c4.setVisible_to(3);


        c4.setOwner_id(new PDOwner());
        c4.getOwner_id().setId(1L);



        list.add(c1);
        list.add(c2);
        list.add(c4);

        return list;
    }

    public List<JSONContact> getListOfDanglingVertecContacts() {
        List<JSONContact> list = new ArrayList<>();

        JSONContact c1 = new JSONContact();
        c1.setEmail("email");
        c1.setFirstName("c1");
        c1.setMobile("mobile");
        c1.setModified("mod");
        c1.setObjid(1L);
        c1.setOwner("a@eat.com");
        c1.setPhone("phone");
        c1.setSurname("surname1");


        JSONContact c2 = new JSONContact();
        c2.setEmail("email");
        c2.setFirstName("c2");
        c2.setMobile("mobile");
        c2.setModified("mod");
        c2.setObjid(2L);
        c2.setOwner("a@eat.com");
        c2.setPhone("phone");
        c2.setSurname("surname2");
        c2.setOwner("a@eat.com");


        JSONContact c3 = new JSONContact();
        c3.setEmail("email");
        c3.setFirstName("c3");
        c3.setMobile("mobile");
        c3.setModified("mod");
        c3.setObjid(3L);
        c3.setOwner("a@eat.com");
        c3.setPhone("phone");
        c3.setSurname("surname3");

        list.add(c1);
        list.add(c2);
        list.add(c3);

        return list;
    }

    @Test
    public void willCorrectlyFilterContactList() {
        List<PDContactReceived> list = getListOfContacts();

        List<PDContactReceived> filteredList = sync.filterContactsWithOrg(list);

        assertTrue(filteredList.size() == 2);
        assertTrue(filteredList.get(0).getName().equals("Robin"));
        assertTrue(filteredList.get(1).getName().equals("Penguin"));

    }

    public List<PDContactReceived> getListOfContacts() {
        OrgId org_id = new OrgId();
        org_id.setValue(15L);

        PDContactReceived c1 = new PDContactReceived();
        c1.setOrg_id(org_id);
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

        PDContactReceived c2 = new PDContactReceived();
        c2.setName("Robin");
        ContactDetail e2 = new ContactDetail();
        e2.setPrimary(true);
        e2.setValue("Robin@night.com");
        c2.getEmail().add(e2);

        PDContactReceived c3 = new PDContactReceived();
        c3.setOrg_id(org_id);
        c3.setName("Joker");
        ContactDetail e3 = new ContactDetail();
        e3.setPrimary(true);
        e3.setValue("joke@you.com");
        c3.getEmail().add(e3);
        ContactDetail p3 = new ContactDetail();
        p3.setPrimary(true);
        p3.setValue("123123");
        c3.getPhone().add(p3);

        PDContactReceived c4 = new PDContactReceived();
        c4.setName("Penguin");
        ContactDetail e4 = new ContactDetail();
        e4.setPrimary(true);
        e4.setValue("Penguin@large.com");
        c4.getEmail().add(e4);
        ContactDetail p4 = new ContactDetail();
        p4.setPrimary(true);
        p4.setValue("321321");
        c4.getPhone().add(p4);

        List<PDContactReceived> list = new ArrayList<>();
        list.add(c1);
        list.add(c2);
        list.add(c3);
        list.add(c4);


        return list;
    }

    @Test
    public void canConstructTeamIdMap(){
        Set<String> v_emails = new HashSet<>();

        v_emails.add("a@eat.com");
        v_emails.add("b@eat.com");
        v_emails.add("a@eat.com");

        List<PDUser> pd_users = new ArrayList<>();
        PDUser p = new PDUser();
        p.setId(1L);
        p.setEmail("a@eat.com");
        pd_users.add(p);

        p = new PDUser();
        p.setEmail("b@eat.com");
        p.setId(2L);
        pd_users.add(p);

        p = new PDUser();
        p.setEmail("c@eat.com");
        p.setId(3L);
        pd_users.add(p);

        sync.constructTeamIdMap(v_emails,pd_users);

        assertTrue(!sync.getTeamIdMap().isEmpty());
        assertTrue(sync.getTeamIdMap().size() == 2);
        assertTrue(sync.getTeamIdMap().get("a@eat.com") == 1L);
        assertTrue(sync.getTeamIdMap().get("b@eat.com") == 2L);
    }

    @Test
    public void canGetOrganisationHeirarchy() {

        List<JSONOrganisation> orgs = getMockVOrgs();

        Map<Long, Long> map = new HashMap<>();
        map.put(5L, 15L);
        map.put(6L, 16L);
        map.put(7L, 17L);

        sync.setOrgIdMap(map);

        List<PDRelationship> rels = sync.getOrganistionHeirarchy(orgs);

        assertTrue(rels.size() == 2);
        assertTrue(rels.get(0).getType().equals("parent"));
        assertTrue(rels.get(0).getRel_owner_org_id() == 16L);
        assertTrue(rels.get(0).getRel_linked_org_id() == 15L);


        assertTrue(rels.get(1).getType().equals("parent"));
        assertTrue(rels.get(1).getRel_owner_org_id() == 17L);
        assertTrue(rels.get(1).getRel_linked_org_id() == 16L);


    }

    @Test
    public void canCorrectlyExtractVidFromNoteWithItSet() {
        String note = "V_ID:132412312542#seoibfl ernglbnreipiqt naerdb aaowri hnd";
        Long id = extractVID(note);
        assertTrue(id == 132412312542L);

        String note2 = "wiephsfgpaorejfpboamfbdp";
        Long id2 = extractVID(note2);
        assertTrue(id2 == -1L);

        String note3 = "oiadfniaodnfbpn#oianfbindfb";
        Long id3 = extractVID(note3);
        assertTrue(id3 == -1L);
    }

    @Test @Ignore
    public void canCompareDealDetails() {


        PDDealSend deal = new PDDealSend();
        PDDealReceived deal2 = new PDDealReceived();
        sync.compareDealDetails(deal, deal2);

        deal2.setPerson_id(new PDPersonId());
        sync.compareDealDetails(deal, deal2);

        PDPersonId person = new PDPersonId();
        person.setValue(1L);
        deal2.setPerson_id(person);
        sync.compareDealDetails(deal, deal2);

        deal.setPerson_id(1L);
        PDDealReceived deal3 = new PDDealReceived();
        sync.compareDealDetails(deal, deal3);

        deal3.setPerson_id(new PDPersonId());
        sync.compareDealDetails(deal, deal3);

        deal3.setPerson_id(person);
        sync.compareDealDetails(deal, deal2);



    }

    @Test
    public void canParseTime() {

        DateTimeFormatter p = DateTimeFormatter.ofPattern("yyyy-MM-dd' 'HH:mm:ss");

        LocalDateTime pt = LocalDateTime.from(p.parse("2016-05-20 14:02:40"));

        System.out.println(pt);
    }

    @Test
    public void nullQueryMap(){
        sync.constructTestTeamMap();
        Long habba  = sync.getTeamIdMap().get(null);
        assertTrue(habba == null);
        habba = sync.getTeamIdMap().get("");
        assertTrue(habba == null);
    }


    @Test
    public void canCreateDealObjectsWhenMapsNotInitialised(){
        String Code = "c15600";

        sync.constructTestTeamMap();
        JSONProject project = sync.getVS().getProject(Code).getBody();


        assertEquals(project.getPhases().size(),1);
        assertEquals(project.getTitle(), "Alstom, Resourcing");
        assertEquals(project.getClientRef().longValue(), 1679954);
        assertEquals(project.getCurrency(),"GBP");
        assertEquals(project.getType(),"BU DSI");
        List<JSONProject> ps = new ArrayList<>();
        ps.add(project);

        List<PDDealSend> deals = sync.createDealObjects(ps);
        PDDealSend deal = deals.get(0);


        assertEquals(deals.size(),1);

        assertTrue(deal.getTitle().contains("Alstom, Resourcing: Software engineers for Solar"));
        assertEquals(deal.getValue(), "0.00");
        assertEquals(deal.getCurrency(), "GBP");
        System.out.println(deal.getUser_id());
        assertEquals(deal.getUser_id().longValue(), 1363410);
        assertEquals(deal.getAdd_time(), "2012-05-17 16:19:03");
        assertEquals(deal.getProject_number(), "C15600");
        assertEquals(deal.getPhase(), "10_RESCOURCING_METHODOLOGIES_FOR_TOOLS");
        assertEquals(deal.getStatus(), "lost");

        System.out.println(deal);


        assertTrue(sync.getPDS().deleteOrganisation(deals.get(0).getOrg_id()).getBody().getSuccess());
        if(deal.getPerson_id() != null){
            assertTrue(sync.getPDS().deleteOrganisation(deals.get(0).getPerson_id()).getBody().getSuccess());
        }
    }
}
