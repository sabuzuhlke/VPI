import VPI.*;
import VPI.PDClasses.ContactDetail;
import VPI.PDClasses.OrgId;
import VPI.PDClasses.PDContactReceived;
import VPI.PDClasses.PDOrganisation;
import VPI.VClasses.VContact;
import VPI.VClasses.VOrganisation;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(Application.class)
public class SynchroniserUnitTests {

    private Synchroniser synchroniser;

    @Before
    public void setUp() throws Exception {
        String PDServer = "";
        String VServer  = "";
        this.synchroniser = new Synchroniser(PDServer, VServer);
    }

    @Test
    public void canClearSynchroniser() {
        assignJustVOrgList();

        PDOrganisation p = new PDOrganisation("Peter Griffin.co", "20 a Street, Bikini Bottom, 4343, Under the Sea");
        synchroniser.organisations.pdOrganisations.add(p);

        p = new PDOrganisation("Charlies Chocolate Factory", "5 End Street, Factory A, 0000, Neverland");
        synchroniser.organisations.pdOrganisations.add(p);

        synchroniser.compareOrganisations();
        synchroniser.compareContacts(
                synchroniser.contacts.vContacts,
                synchroniser.contacts.pdContacts
        );

        clearSynchroniser();

        assertTrue(synchroniser.organisations.pdOrganisations.isEmpty());
        assertTrue(synchroniser.organisations.vOrganisations.isEmpty());

        assertTrue(synchroniser.organisations.postList.isEmpty());
        assertTrue(synchroniser.organisations.putList.isEmpty());

        assertTrue(synchroniser.contacts.pdContacts.isEmpty());
        assertTrue(synchroniser.contacts.vContacts.isEmpty());

        assertTrue(synchroniser.contacts.postList.isEmpty());
        assertTrue(synchroniser.contacts.putList.isEmpty());

    }

//--------------------------ORGANISATIONS-TESTS--------------------------------

    public int assignMatchingPDOrgList() {
        PDOrganisation p = new PDOrganisation();
        p.setName("Peter Griffin.co");
        p.setAddress("13 Family Street, Quahog, 6727, Murica!");
        synchroniser.organisations.pdOrganisations.add(p);

        p = new PDOrganisation();
        p.setName("Peter Quagmire.co");
        p.setAddress("17 Family Street, Quahog, 6722, Murica!");
        synchroniser.organisations.pdOrganisations.add(p);

        return synchroniser.organisations.pdOrganisations.size();
    }

    public int assignJustVOrgList() {
        VOrganisation v = new VOrganisation();
        v.setName("Peter Griffin.co");
        v.setCity("Quahog");
        v.setCountry("Murica!");
        v.setStreet("13 Family Street");
        v.setZip("6727");
        synchroniser.organisations.vOrganisations.add(v);

        v = new VOrganisation();
        v.setName("Peter Quagmire.co");
        v.setCity("Quahog");
        v.setCountry("Murica!");
        v.setStreet("17 Family Street");
        v.setZip("6722");
        synchroniser.organisations.vOrganisations.add(v);

        return synchroniser.organisations.vOrganisations.size();

    }

    public void clearSynchroniser() {
        synchroniser.clear();
    }
//Unit tests
    @Test
    public void emptyOrgListsReturnEmptyPostAndPutLists() {
        assertTrue(synchroniser.organisations.pdOrganisations.isEmpty());
        assertTrue(synchroniser.organisations.vOrganisations.isEmpty());

        synchroniser.compareOrganisations();

        assertTrue(synchroniser.organisations.postList.isEmpty());
        assertTrue(synchroniser.organisations.putList.isEmpty());
    }

    @Test
    public void emptyPDOrgListReturnsAllItemsOfVOrgListToPostList() {

        int numInList = assignJustVOrgList();
        assertTrue(!synchroniser.organisations.vOrganisations.isEmpty());
        assertTrue(synchroniser.organisations.pdOrganisations.isEmpty());

        synchroniser.compareOrganisations();

        assertTrue(synchroniser.organisations.putList.isEmpty());
        assertTrue(!synchroniser.organisations.postList.isEmpty());
        assertEquals(synchroniser.organisations.postList.size(), numInList);

        //some check on matching internal values

        clearSynchroniser();
    }

    @Test
    public void emptyVlistNoPostNoPut(){
        assignMatchingPDOrgList();
        assertTrue(synchroniser.organisations.vOrganisations.isEmpty());
        assertTrue(!synchroniser.organisations.pdOrganisations.isEmpty());

        synchroniser.compareOrganisations();

        assertTrue(synchroniser.organisations.putList.isEmpty());
        assertTrue(synchroniser.organisations.postList.isEmpty());

        clearSynchroniser();
    }

    @Test
    public void givenCaBa_CaBa(){
        assignJustVOrgList();
        assignMatchingPDOrgList();

        assertTrue(!synchroniser.organisations.vOrganisations.isEmpty());
        assertTrue(!synchroniser.organisations.pdOrganisations.isEmpty());

        synchroniser.compareOrganisations();

        assertTrue(synchroniser.organisations.putList.isEmpty());
        assertTrue(synchroniser.organisations.postList.isEmpty());

        clearSynchroniser();
    }

    @Test
    public void given_CaBa_AaDa_POSTCaBa_emptyPUT(){
        assignJustVOrgList();

        PDOrganisation p = new PDOrganisation("The Crusty Crab", "13 a Street, Bikini Bottom, 4343, Under the Sea");
        synchroniser.organisations.pdOrganisations.add(p);

        p = new PDOrganisation("Charlies Chocolate Factory", "5 End Street, Factory A, 0000, Neverland");
        synchroniser.organisations.pdOrganisations.add(p);

        assertTrue(!synchroniser.organisations.vOrganisations.isEmpty());
        assertTrue(synchroniser.organisations.pdOrganisations.size() == 2);

        synchroniser.compareOrganisations();

        assertTrue(synchroniser.organisations.postList.size() == 2);
        assertTrue(synchroniser.organisations.postList.get(0).getName().equals("Peter Griffin.co"));
        assertTrue(synchroniser.organisations.postList.get(1).getName().equals("Peter Quagmire.co"));

        assertTrue(synchroniser.organisations.putList.isEmpty());

        clearSynchroniser();
    }

    @Test
    public void given_CaBa_CbDa_CaInPUT_BaInPost() {
        assignJustVOrgList();

        VOrganisation v = new VOrganisation();
        v.setName("Joe's wheelchair co");
        v.setCity("Quahog");
        v.setCountry("Murica!");
        v.setStreet("20 Family Street");
        v.setZip("6727");
        synchroniser.organisations.vOrganisations.add(v);

        v = new VOrganisation();
        v.setName("Clevelands Throwback co");
        v.setCity("Quahog");
        v.setCountry("Murica!");
        v.setStreet("Opposite 15 Family Street");
        v.setZip("6722");
        synchroniser.organisations.vOrganisations.add(v);

        PDOrganisation p = new PDOrganisation("Peter Quagmire.co", "20 a Street, Bikini Bottom, 4343, Under the Sea");
        synchroniser.organisations.pdOrganisations.add(p);

        p = new PDOrganisation("Clevelands Throwback co", "5 End Street, Factory A, 0000, Neverland");
        synchroniser.organisations.pdOrganisations.add(p);


        assertTrue(!synchroniser.organisations.vOrganisations.isEmpty());
        assertTrue(synchroniser.organisations.pdOrganisations.size() == 2);

        synchroniser.compareOrganisations();

        assertTrue(synchroniser.organisations.postList.size() == 2);
        assertTrue(synchroniser.organisations.postList.get(0).getName().equals("Peter Griffin.co"));
        assertTrue(synchroniser.organisations.postList.get(0).getAddress().equals("13 Family Street, Quahog, 6727, Murica!"));
        assertTrue(synchroniser.organisations.postList.get(1).getName().equals("Joe's wheelchair co"));
        assertTrue(synchroniser.organisations.postList.get(1).getAddress().equals("20 Family Street, Quahog, 6727, Murica!"));

        assertTrue(synchroniser.organisations.putList.size() == 2);
        assertTrue(synchroniser.organisations.putList.get(0).getName().equals("Peter Quagmire.co"));
        assertTrue(synchroniser.organisations.putList.get(0).getAddress().equals("17 Family Street, Quahog, 6722, Murica!"));
        assertTrue(synchroniser.organisations.putList.get(1).getName().equals("Clevelands Throwback co"));
        assertTrue(synchroniser.organisations.putList.get(1).getAddress().equals("Opposite 15 Family Street, Quahog, 6722, Murica!"));

        clearSynchroniser();
    }

//--------------------------CONTACT-TESTS--------------------------------

//TODO: FINISH Contact Test
    @Test
    public void emptyContactListsReturnsEmptyPostAndPutLists() {
        assertTrue(synchroniser.contacts.pdContacts.isEmpty());
        assertTrue(synchroniser.contacts.vContacts.isEmpty());

        synchroniser.compareContacts(
                synchroniser.contacts.vContacts,
                synchroniser.contacts.pdContacts
        );

        assertTrue(synchroniser.contacts.postList.isEmpty());
        assertTrue(synchroniser.contacts.putList.isEmpty());
    }

    @Test
    public void emptyPDContactListReturnsAllItemsOfVContactToPostList() {
        int numInList = assignJustVContactList();
        assertTrue(!synchroniser.contacts.vContacts.isEmpty());
        assertTrue(synchroniser.contacts.pdContacts.isEmpty());

        synchroniser.compareContacts(
                synchroniser.contacts.vContacts,
                synchroniser.contacts.pdContacts
        );

        assertTrue(synchroniser.contacts.putList.isEmpty());
        assertTrue(!synchroniser.contacts.postList.isEmpty());

        assertTrue(numInList == 4);
        assertTrue(synchroniser.contacts.postList.get(0).getName().equals("Batman"));
        assertTrue(synchroniser.contacts.postList.get(0)
                .getEmail().get(0).getValue().equals("BatSignal@night.com"));
        assertTrue(synchroniser.contacts.postList.get(0)
                .getPhone().get(0).getValue().equals("0987654321"));

        assertTrue(synchroniser.contacts.postList.get(1).getName().equals("Robin"));
        assertTrue(synchroniser.contacts.postList.get(1)
                .getEmail().get(0).getValue().equals("Robin@night.com"));

        assertTrue(synchroniser.contacts.postList.get(2).getName().equals("Joker"));
        assertTrue(synchroniser.contacts.postList.get(2).getPhone().get(0).getValue().equals("123123"));
        assertTrue(synchroniser.contacts.postList.get(2).getEmail().get(0).getValue().equals("joke@you.com"));

        assertTrue(synchroniser.contacts.postList.get(3).getName().equals("Penguin"));

        clearSynchroniser();
    }

    @Test
    public void matchingContactListsReturnsEmptyPostAndPut() {
        assignJustVContactList();
        assignMatchingPDContacts();

        synchroniser.compareContacts(
                synchroniser.contacts.vContacts,
                synchroniser.contacts.pdContacts
        );

        assertTrue(synchroniser.contacts.postList.isEmpty());
        assertTrue(synchroniser.contacts.putList.isEmpty());

        clearSynchroniser();
    }

    @Test
    public void missingInfoAndUnresolvedInfoReturnsCorrectPostAndPut() {
        assignJustVContactList();
        assignPDContacts();

        synchroniser.compareContacts(
                synchroniser.contacts.vContacts,
                synchroniser.contacts.pdContacts
        );

        assertTrue(synchroniser.contacts.postList.size() == 3);
        assertTrue(synchroniser.contacts.postList.get(0).getName().equals("Robin"));
        assertTrue(synchroniser.contacts.postList.get(1).getName().equals("Joker"));
        assertTrue(synchroniser.contacts.postList.get(2).getName().equals("Penguin"));

        assertTrue(synchroniser.contacts.putList.size() == 1);
        assertTrue(synchroniser.contacts.putList.get(0).getName().equals("Batman"));
        assertTrue(synchroniser.contacts.putList.get(0).getPhone().get(0).getValue().equals("0987654321"));
        assertTrue(synchroniser.contacts.putList.get(0).getPhone().get(0).getPrimary());
        assertTrue(synchroniser.contacts.putList.get(0).getPhone().get(1).getValue().equals("11111111"));
        assertTrue(!synchroniser.contacts.putList.get(0).getPhone().get(1).getPrimary());
        assertTrue(synchroniser.contacts.putList.get(0).getEmail().get(0).getValue().equals("BatSignal@night.com"));
        assertTrue(synchroniser.contacts.putList.get(0).getEmail().get(0).getPrimary());
        assertTrue(synchroniser.contacts.putList.get(0).getEmail().get(1).getValue().equals("notBruce@wayne.com"));
        assertTrue(!synchroniser.contacts.putList.get(0).getEmail().get(1).getPrimary());

        clearSynchroniser();
    }

//-----ResolveContactDetailsTests----------------------------------------------------------------------

    @Test
    public void emptyPDCDemptyVCDreturnsEmptyPDCD() {
        VContact v = new VContact();
        PDContactReceived p = new PDContactReceived();

        Boolean mod = synchroniser.resolveContactDetails(v,p);
        assertTrue(!mod);
        assertTrue(p.getPhone().isEmpty());
        assertTrue(p.getEmail().isEmpty());
    }

    @Test
    public void emptyPDCDnonEmptyVReturnsVCDInP() {
        assignJustVContactList();
        VContact v = synchroniser.contacts.vContacts.get(0);
        PDContactReceived p = new PDContactReceived();

        Boolean mod = synchroniser.resolveContactDetails(v,p);
        assertTrue(mod);
        assertTrue(p.getEmail().get(0).getValue().equals("BatSignal@night.com"));

        assertTrue(p.getPhone().get(0).getValue().equals("0987654321"));

        clearSynchroniser();
    }

    @Test
    public void differentPDCD_VCDreturnsAllIntoPWithCorrectPrimary() {
        assignJustVContactList();
        VContact v = synchroniser.contacts.vContacts.get(0);
        assignPDContacts();
        PDContactReceived p = synchroniser.contacts.pdContacts.get(0);

        Boolean mod = synchroniser.resolveContactDetails(v,p);

        assertTrue(mod);
        assertTrue(p.getEmail().size() == 3);
        assertTrue(p.getPhone().size() == 3);
        assertTrue(p.getEmail().get(2).getValue().equals("BatSignal@night.com"));
        assertTrue(p.getEmail().get(2).getPrimary());

        assertTrue(p.getEmail().get(0).getValue().equals("Middle@field.gotham"));
        assertTrue(!p.getEmail().get(0).getPrimary());


        assertTrue(p.getPhone().get(2).getValue().equals("0987654321"));
        assertTrue(p.getPhone().get(2).getPrimary());

        assertTrue(p.getPhone().get(0).getValue().equals("777222777"));
        assertTrue(!p.getPhone().get(0).getPrimary());

        clearSynchroniser();
    }

    @Test
    public void emptyVCDnonEmptyPDCDreturnsPDinP() {
        assignPDContacts();
        VContact v = new VContact();
        PDContactReceived p = synchroniser.contacts.pdContacts.get(1);

        Boolean mod = synchroniser.resolveContactDetails(v,p);

        assertTrue(!mod);
        assertTrue(p.getEmail().size() == 2);
        assertTrue(p.getEmail().get(0).getValue().equals("BatSignal@night.com"));
        assertTrue(p.getEmail().get(1).getValue().equals("notBruce@wayne.com"));
        assertTrue(p.getPhone().size() == 2);
        assertTrue(p.getPhone().get(0).getValue().equals("0987654321"));
        assertTrue(p.getPhone().get(1).getValue().equals("11111111"));

        clearSynchroniser();
    }

    @Test
    public void differentPDCD_VCDvaluesreturnsAllIntoP() {
        assignPDContacts();
        assignJustVContactList();

        VContact v = synchroniser.contacts.vContacts.get(0);
        PDContactReceived p = synchroniser.contacts.pdContacts.get(1);

        Boolean mod = synchroniser.resolveContactDetails(v,p);

        assertTrue(mod);

        assertTrue(p.getPhone().size() == 2);
        assertTrue(p.getEmail().size() == 2);

        assertTrue(p.getEmail().get(0).getValue().equals("BatSignal@night.com"));
        assertTrue(p.getEmail().get(0).getPrimary());
        assertTrue(p.getEmail().get(1).getValue().equals("notBruce@wayne.com"));
        assertTrue(!p.getEmail().get(1).getPrimary());

        assertTrue(p.getPhone().get(0).getValue().equals("0987654321"));
        assertTrue(p.getPhone().get(0).getPrimary());
        assertTrue(p.getPhone().get(1).getValue().equals("11111111"));
        assertTrue(!p.getPhone().get(1).getPrimary());

        clearSynchroniser();
    }

    public int assignPDContacts() {
        PDContactReceived c1 = new PDContactReceived();
        PDContactReceived c2 = new PDContactReceived();

        OrgId org_id = new OrgId();
        org_id.setValue(15L);

        c1.setName("Scarecrow");
        c1.setOrg_id(org_id);
        c1.getEmail().add(new ContactDetail("Middle@field.gotham", true));
        c1.getPhone().add(new ContactDetail("777222777", true));
        c1.getEmail().add(new ContactDetail("senior@arkham.com", false));
        c1.getPhone().add(new ContactDetail("666444666", false));

        synchroniser.contacts.pdContacts.add(c1);

        c2.setName("Batman");
        c2.setOrg_id(org_id);
        c2.getEmail().add(new ContactDetail("BatSignal@night.com", false));
        c2.getEmail().add(new ContactDetail("notBruce@wayne.com", true));
        c2.getPhone().add(new ContactDetail("0987654321", false));
        c2.getPhone().add(new ContactDetail("11111111", true));

        synchroniser.contacts.pdContacts.add(c2);

        return synchroniser.contacts.pdContacts.size();
    }

    public int assignMatchingPDContacts() {
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
        c2.setOrg_id(org_id);
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
        c4.setOrg_id(org_id);
        c4.setName("Penguin");
        ContactDetail e4 = new ContactDetail();
        e4.setPrimary(true);
        e4.setValue("Penguin@large.com");
        c4.getEmail().add(e4);
        ContactDetail p4 = new ContactDetail();
        p4.setPrimary(true);
        p4.setValue("321321");
        c4.getPhone().add(p4);

        synchroniser.contacts.pdContacts.add(c1);
        synchroniser.contacts.pdContacts.add(c2);
        synchroniser.contacts.pdContacts.add(c3);
        synchroniser.contacts.pdContacts.add(c4);

        return synchroniser.contacts.vContacts.size();

    }

    public int assignJustVContactList() {
        VContact c1 = new VContact();
        c1.setName("Batman");
        c1.setEmail("BatSignal@night.com");

        c1.setPhone("0987654321");

        VContact c2 = new VContact();
        c2.setName("Robin");
        c2.setEmail("Robin@night.com");


        VContact c3 = new VContact();
        c3.setName("Joker");
        c3.setEmail("joke@you.com");
        c3.setPhone("123123");

        VContact c4 = new VContact();
        c4.setName("Penguin");
        c4.setEmail("Penguin@large.com");
        c4.setPhone("321321");

        synchroniser.contacts.vContacts.add(c1);
        synchroniser.contacts.vContacts.add(c2);
        synchroniser.contacts.vContacts.add(c3);
        synchroniser.contacts.vContacts.add(c4);

        return synchroniser.contacts.vContacts.size();
    }

}
