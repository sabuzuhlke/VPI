//import VPI.*;
//import VPI.PDClasses.ContactDetail;
//import VPI.PDClasses.OrgId;
//import VPI.PDClasses.PDContactReceived;
//import VPI.PDClasses.PDOrganisation;
//import VPI.InsightClasses.VContact;
//import VPI.InsightClasses.VOrganisation;
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.boot.test.SpringApplicationConfiguration;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
//
//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertTrue;
//
//@RunWith(SpringJUnit4ClassRunner.class)
//@SpringApplicationConfiguration(Application.class)
//public class InsightSynchroniserUnitTests {
//
//    private InsightSynchroniser insightSynchroniser;
//
//    @Before
//    public void setUp() throws Exception {
//        String PDServer = "";
//        String VServer  = "";
//        this.insightSynchroniser = new InsightSynchroniser(PDServer, VServer);
//    }
//
//    @Test
//    public void canClearSynchroniser() {
//        assignJustVOrgList();
//
//        PDOrganisation p = new PDOrganisation("Peter Griffin.co", "20 a Street, Bikini Bottom, 4343, Under the Sea");
//        insightSynchroniser.organisations.pdOrganisations.add(p);
//
//        p = new PDOrganisation("Charlies Chocolate Factory", "5 End Street, Factory A, 0000, Neverland");
//        insightSynchroniser.organisations.pdOrganisations.add(p);
//
//        insightSynchroniser.compareOrganisations();
//        insightSynchroniser.compareContacts(
//                insightSynchroniser.contacts.vContacts,
//                insightSynchroniser.contacts.pdContacts
//        );
//
//        clearSynchroniser();
//
//        assertTrue(insightSynchroniser.organisations.pdOrganisations.isEmpty());
//        assertTrue(insightSynchroniser.organisations.vOrganisations.isEmpty());
//
//        assertTrue(insightSynchroniser.organisations.postList.isEmpty());
//        assertTrue(insightSynchroniser.organisations.putList.isEmpty());
//
//        assertTrue(insightSynchroniser.contacts.pdContacts.isEmpty());
//        assertTrue(insightSynchroniser.contacts.vContacts.isEmpty());
//
//        assertTrue(insightSynchroniser.contacts.postList.isEmpty());
//        assertTrue(insightSynchroniser.contacts.putList.isEmpty());
//
//    }
//
////--------------------------ORGANISATIONS-TESTS--------------------------------
//
//    public int assignMatchingPDOrgList() {
//        PDOrganisation p = new PDOrganisation();
//        p.setName("Peter Griffin.co");
//        p.setAddress("13 Family Street, Quahog, 6727, Murica!");
//        insightSynchroniser.organisations.pdOrganisations.add(p);
//
//        p = new PDOrganisation();
//        p.setName("Peter Quagmire.co");
//        p.setAddress("17 Family Street, Quahog, 6722, Murica!");
//        insightSynchroniser.organisations.pdOrganisations.add(p);
//
//        return insightSynchroniser.organisations.pdOrganisations.size();
//    }
//
//    public int assignJustVOrgList() {
//        VOrganisation v = new VOrganisation();
//        v.setName("Peter Griffin.co");
//        v.setCity("Quahog");
//        v.setCountry("Murica!");
//        v.setStreet("13 Family Street");
//        v.setZip("6727");
//        insightSynchroniser.organisations.vOrganisations.add(v);
//
//        v = new VOrganisation();
//        v.setName("Peter Quagmire.co");
//        v.setCity("Quahog");
//        v.setCountry("Murica!");
//        v.setStreet("17 Family Street");
//        v.setZip("6722");
//        insightSynchroniser.organisations.vOrganisations.add(v);
//
//        return insightSynchroniser.organisations.vOrganisations.size();
//
//    }
//
//    public void clearSynchroniser() {
//        insightSynchroniser.clear();
//    }
////Unit tests
//    @Test
//    public void emptyOrgListsReturnEmptyPostAndPutLists() {
//        assertTrue(insightSynchroniser.organisations.pdOrganisations.isEmpty());
//        assertTrue(insightSynchroniser.organisations.vOrganisations.isEmpty());
//
//        insightSynchroniser.compareOrganisations();
//
//        assertTrue(insightSynchroniser.organisations.postList.isEmpty());
//        assertTrue(insightSynchroniser.organisations.putList.isEmpty());
//    }
//
//    @Test
//    public void emptyPDOrgListReturnsAllItemsOfVOrgListToPostList() {
//
//        int numInList = assignJustVOrgList();
//        assertTrue(!insightSynchroniser.organisations.vOrganisations.isEmpty());
//        assertTrue(insightSynchroniser.organisations.pdOrganisations.isEmpty());
//
//        insightSynchroniser.compareOrganisations();
//
//        assertTrue(insightSynchroniser.organisations.putList.isEmpty());
//        assertTrue(!insightSynchroniser.organisations.postList.isEmpty());
//        assertEquals(insightSynchroniser.organisations.postList.size(), numInList);
//
//        //some check on matching internal values
//
//        clearSynchroniser();
//    }
//
//    @Test
//    public void emptyVlistNoPostNoPut(){
//        assignMatchingPDOrgList();
//        assertTrue(insightSynchroniser.organisations.vOrganisations.isEmpty());
//        assertTrue(!insightSynchroniser.organisations.pdOrganisations.isEmpty());
//
//        insightSynchroniser.compareOrganisations();
//
//        assertTrue(insightSynchroniser.organisations.putList.isEmpty());
//        assertTrue(insightSynchroniser.organisations.postList.isEmpty());
//
//        clearSynchroniser();
//    }
//
//    @Test
//    public void givenCaBa_CaBa(){
//        assignJustVOrgList();
//        assignMatchingPDOrgList();
//
//        assertTrue(!insightSynchroniser.organisations.vOrganisations.isEmpty());
//        assertTrue(!insightSynchroniser.organisations.pdOrganisations.isEmpty());
//
//        insightSynchroniser.compareOrganisations();
//
//        assertTrue(insightSynchroniser.organisations.putList.isEmpty());
//        assertTrue(insightSynchroniser.organisations.postList.isEmpty());
//
//        clearSynchroniser();
//    }
//
//    @Test
//    public void given_CaBa_AaDa_POSTCaBa_emptyPUT(){
//        assignJustVOrgList();
//
//        PDOrganisation p = new PDOrganisation("The Crusty Crab", "13 a Street, Bikini Bottom, 4343, Under the Sea");
//        insightSynchroniser.organisations.pdOrganisations.add(p);
//
//        p = new PDOrganisation("Charlies Chocolate Factory", "5 End Street, Factory A, 0000, Neverland");
//        insightSynchroniser.organisations.pdOrganisations.add(p);
//
//        assertTrue(!insightSynchroniser.organisations.vOrganisations.isEmpty());
//        assertTrue(insightSynchroniser.organisations.pdOrganisations.size() == 2);
//
//        insightSynchroniser.compareOrganisations();
//
//        assertTrue(insightSynchroniser.organisations.postList.size() == 2);
//        assertTrue(insightSynchroniser.organisations.postList.get(0).getName().equals("Peter Griffin.co"));
//        assertTrue(insightSynchroniser.organisations.postList.get(1).getName().equals("Peter Quagmire.co"));
//
//        assertTrue(insightSynchroniser.organisations.putList.isEmpty());
//
//        clearSynchroniser();
//    }
//
//    @Test
//    public void given_CaBa_CbDa_CaInPUT_BaInPost() {
//        assignJustVOrgList();
//
//        VOrganisation v = new VOrganisation();
//        v.setName("Joe's wheelchair co");
//        v.setCity("Quahog");
//        v.setCountry("Murica!");
//        v.setStreet("20 Family Street");
//        v.setZip("6727");
//        insightSynchroniser.organisations.vOrganisations.add(v);
//
//        v = new VOrganisation();
//        v.setName("Clevelands Throwback co");
//        v.setCity("Quahog");
//        v.setCountry("Murica!");
//        v.setStreet("Opposite 15 Family Street");
//        v.setZip("6722");
//        insightSynchroniser.organisations.vOrganisations.add(v);
//
//        PDOrganisation p = new PDOrganisation("Peter Quagmire.co", "20 a Street, Bikini Bottom, 4343, Under the Sea");
//        insightSynchroniser.organisations.pdOrganisations.add(p);
//
//        p = new PDOrganisation("Clevelands Throwback co", "5 End Street, Factory A, 0000, Neverland");
//        insightSynchroniser.organisations.pdOrganisations.add(p);
//
//
//        assertTrue(!insightSynchroniser.organisations.vOrganisations.isEmpty());
//        assertTrue(insightSynchroniser.organisations.pdOrganisations.size() == 2);
//
//        insightSynchroniser.compareOrganisations();
//
//        assertTrue(insightSynchroniser.organisations.postList.size() == 2);
//        assertTrue(insightSynchroniser.organisations.postList.get(0).getName().equals("Peter Griffin.co"));
//        assertTrue(insightSynchroniser.organisations.postList.get(0).getAddress().equals("13 Family Street, Quahog, 6727, Murica!"));
//        assertTrue(insightSynchroniser.organisations.postList.get(1).getName().equals("Joe's wheelchair co"));
//        assertTrue(insightSynchroniser.organisations.postList.get(1).getAddress().equals("20 Family Street, Quahog, 6727, Murica!"));
//
//        assertTrue(insightSynchroniser.organisations.putList.size() == 2);
//        assertTrue(insightSynchroniser.organisations.putList.get(0).getName().equals("Peter Quagmire.co"));
//        assertTrue(insightSynchroniser.organisations.putList.get(0).getAddress().equals("17 Family Street, Quahog, 6722, Murica!"));
//        assertTrue(insightSynchroniser.organisations.putList.get(1).getName().equals("Clevelands Throwback co"));
//        assertTrue(insightSynchroniser.organisations.putList.get(1).getAddress().equals("Opposite 15 Family Street, Quahog, 6722, Murica!"));
//
//        clearSynchroniser();
//    }
//
////--------------------------CONTACT-TESTS--------------------------------
//
////TODO: FINISH Contact Test when unblocked
//    @Test
//    public void emptyContactListsReturnsEmptyPostAndPutLists() {
//        assertTrue(insightSynchroniser.contacts.pdContacts.isEmpty());
//        assertTrue(insightSynchroniser.contacts.vContacts.isEmpty());
//
//        insightSynchroniser.compareContacts(
//                insightSynchroniser.contacts.vContacts,
//                insightSynchroniser.contacts.pdContacts
//        );
//
//        assertTrue(insightSynchroniser.contacts.postList.isEmpty());
//        assertTrue(insightSynchroniser.contacts.putList.isEmpty());
//    }
//
//    @Test
//    public void emptyPDContactListReturnsAllItemsOfVContactToPostList() {
//        int numInList = assignJustVContactList();
//        assertTrue(!insightSynchroniser.contacts.vContacts.isEmpty());
//        assertTrue(insightSynchroniser.contacts.pdContacts.isEmpty());
//
//        insightSynchroniser.compareContacts(
//                insightSynchroniser.contacts.vContacts,
//                insightSynchroniser.contacts.pdContacts
//        );
//
//        assertTrue(insightSynchroniser.contacts.putList.isEmpty());
//        assertTrue(!insightSynchroniser.contacts.postList.isEmpty());
//
//        assertTrue(numInList == 4);
//        assertTrue(insightSynchroniser.contacts.postList.get(0).getName().equals("Batman"));
//        assertTrue(insightSynchroniser.contacts.postList.get(0)
//                .getEmail().get(0).getValue().equals("BatSignal@night.com"));
//        assertTrue(insightSynchroniser.contacts.postList.get(0)
//                .getPhone().get(0).getValue().equals("0987654321"));
//
//        assertTrue(insightSynchroniser.contacts.postList.get(1).getName().equals("Robin"));
//        assertTrue(insightSynchroniser.contacts.postList.get(1)
//                .getEmail().get(0).getValue().equals("Robin@night.com"));
//
//        assertTrue(insightSynchroniser.contacts.postList.get(2).getName().equals("Joker"));
//        assertTrue(insightSynchroniser.contacts.postList.get(2).getPhone().get(0).getValue().equals("123123"));
//        assertTrue(insightSynchroniser.contacts.postList.get(2).getEmail().get(0).getValue().equals("joke@you.com"));
//
//        assertTrue(insightSynchroniser.contacts.postList.get(3).getName().equals("Penguin"));
//
//        clearSynchroniser();
//    }
//
//    @Test
//    public void matchingContactListsReturnsEmptyPostAndPut() {
//        assignJustVContactList();
//        assignMatchingPDContacts();
//
//        insightSynchroniser.compareContacts(
//                insightSynchroniser.contacts.vContacts,
//                insightSynchroniser.contacts.pdContacts
//        );
//
//        assertTrue(insightSynchroniser.contacts.postList.isEmpty());
//        assertTrue(insightSynchroniser.contacts.putList.isEmpty());
//
//        clearSynchroniser();
//    }
//
//    @Test
//    public void missingInfoAndUnresolvedInfoReturnsCorrectPostAndPut() {
//        assignJustVContactList();
//        assignPDContacts();
//
//        insightSynchroniser.compareContacts(
//                insightSynchroniser.contacts.vContacts,
//                insightSynchroniser.contacts.pdContacts
//        );
//
//        assertTrue(insightSynchroniser.contacts.postList.size() == 3);
//        assertTrue(insightSynchroniser.contacts.postList.get(0).getName().equals("Robin"));
//        assertTrue(insightSynchroniser.contacts.postList.get(1).getName().equals("Joker"));
//        assertTrue(insightSynchroniser.contacts.postList.get(2).getName().equals("Penguin"));
//
//        assertTrue(insightSynchroniser.contacts.putList.size() == 1);
//        assertTrue(insightSynchroniser.contacts.putList.get(0).getName().equals("Batman"));
//        assertTrue(insightSynchroniser.contacts.putList.get(0).getPhone().get(0).getValue().equals("0987654321"));
//        assertTrue(insightSynchroniser.contacts.putList.get(0).getPhone().get(0).getPrimary());
//        assertTrue(insightSynchroniser.contacts.putList.get(0).getPhone().get(1).getValue().equals("11111111"));
//        assertTrue(!insightSynchroniser.contacts.putList.get(0).getPhone().get(1).getPrimary());
//        assertTrue(insightSynchroniser.contacts.putList.get(0).getEmail().get(0).getValue().equals("BatSignal@night.com"));
//        assertTrue(insightSynchroniser.contacts.putList.get(0).getEmail().get(0).getPrimary());
//        assertTrue(insightSynchroniser.contacts.putList.get(0).getEmail().get(1).getValue().equals("notBruce@wayne.com"));
//        assertTrue(!insightSynchroniser.contacts.putList.get(0).getEmail().get(1).getPrimary());
//
//        clearSynchroniser();
//    }
//
////-----ResolveContactDetailsTests----------------------------------------------------------------------
//
//    @Test
//    public void emptyPDCDemptyVCDreturnsEmptyPDCD() {
//        VContact v = new VContact();
//        PDContactReceived p = new PDContactReceived();
//
//        Boolean mod = insightSynchroniser.resolveContactDetails(v,p);
//        assertTrue(!mod);
//        assertTrue(p.getPhone().isEmpty());
//        assertTrue(p.getEmail().isEmpty());
//    }
//
//    @Test
//    public void emptyPDCDnonEmptyVReturnsVCDInP() {
//        assignJustVContactList();
//        VContact v = insightSynchroniser.contacts.vContacts.get(0);
//        PDContactReceived p = new PDContactReceived();
//
//        Boolean mod = insightSynchroniser.resolveContactDetails(v,p);
//        assertTrue(mod);
//        assertTrue(p.getEmail().get(0).getValue().equals("BatSignal@night.com"));
//
//        assertTrue(p.getPhone().get(0).getValue().equals("0987654321"));
//
//        clearSynchroniser();
//    }
//
//    @Test
//    public void differentPDCD_VCDreturnsAllIntoPWithCorrectPrimary() {
//        assignJustVContactList();
//        VContact v = insightSynchroniser.contacts.vContacts.get(0);
//        assignPDContacts();
//        PDContactReceived p = insightSynchroniser.contacts.pdContacts.get(0);
//
//        Boolean mod = insightSynchroniser.resolveContactDetails(v,p);
//
//        assertTrue(mod);
//        assertTrue(p.getEmail().size() == 3);
//        assertTrue(p.getPhone().size() == 3);
//        assertTrue(p.getEmail().get(2).getValue().equals("BatSignal@night.com"));
//        assertTrue(p.getEmail().get(2).getPrimary());
//
//        assertTrue(p.getEmail().get(0).getValue().equals("Middle@field.gotham"));
//        assertTrue(!p.getEmail().get(0).getPrimary());
//
//
//        assertTrue(p.getPhone().get(2).getValue().equals("0987654321"));
//        assertTrue(p.getPhone().get(2).getPrimary());
//
//        assertTrue(p.getPhone().get(0).getValue().equals("777222777"));
//        assertTrue(!p.getPhone().get(0).getPrimary());
//
//        clearSynchroniser();
//    }
//
//    @Test
//    public void emptyVCDnonEmptyPDCDreturnsPDinP() {
//        assignPDContacts();
//        VContact v = new VContact();
//        PDContactReceived p = insightSynchroniser.contacts.pdContacts.get(1);
//
//        Boolean mod = insightSynchroniser.resolveContactDetails(v,p);
//
//        assertTrue(!mod);
//        assertTrue(p.getEmail().size() == 2);
//        assertTrue(p.getEmail().get(0).getValue().equals("BatSignal@night.com"));
//        assertTrue(p.getEmail().get(1).getValue().equals("notBruce@wayne.com"));
//        assertTrue(p.getPhone().size() == 2);
//        assertTrue(p.getPhone().get(0).getValue().equals("0987654321"));
//        assertTrue(p.getPhone().get(1).getValue().equals("11111111"));
//
//        clearSynchroniser();
//    }
//
//    @Test
//    public void differentPDCD_VCDvaluesreturnsAllIntoP() {
//        assignPDContacts();
//        assignJustVContactList();
//
//        VContact v = insightSynchroniser.contacts.vContacts.get(0);
//        PDContactReceived p = insightSynchroniser.contacts.pdContacts.get(1);
//
//        Boolean mod = insightSynchroniser.resolveContactDetails(v,p);
//
//        assertTrue(mod);
//
//        assertTrue(p.getPhone().size() == 2);
//        assertTrue(p.getEmail().size() == 2);
//
//        assertTrue(p.getEmail().get(0).getValue().equals("BatSignal@night.com"));
//        assertTrue(p.getEmail().get(0).getPrimary());
//        assertTrue(p.getEmail().get(1).getValue().equals("notBruce@wayne.com"));
//        assertTrue(!p.getEmail().get(1).getPrimary());
//
//        assertTrue(p.getPhone().get(0).getValue().equals("0987654321"));
//        assertTrue(p.getPhone().get(0).getPrimary());
//        assertTrue(p.getPhone().get(1).getValue().equals("11111111"));
//        assertTrue(!p.getPhone().get(1).getPrimary());
//
//        clearSynchroniser();
//    }
//
//    public int assignPDContacts() {
//        PDContactReceived c1 = new PDContactReceived();
//        PDContactReceived c2 = new PDContactReceived();
//
//        OrgId org_id = new OrgId();
//        org_id.setValue(15L);
//
//        c1.setName("Scarecrow");
//        c1.setOrg_id(org_id);
//        c1.getEmail().add(new ContactDetail("Middle@field.gotham", true));
//        c1.getPhone().add(new ContactDetail("777222777", true));
//        c1.getEmail().add(new ContactDetail("senior@arkham.com", false));
//        c1.getPhone().add(new ContactDetail("666444666", false));
//
//        insightSynchroniser.contacts.pdContacts.add(c1);
//
//        c2.setName("Batman");
//        c2.setOrg_id(org_id);
//        c2.getEmail().add(new ContactDetail("BatSignal@night.com", false));
//        c2.getEmail().add(new ContactDetail("notBruce@wayne.com", true));
//        c2.getPhone().add(new ContactDetail("0987654321", false));
//        c2.getPhone().add(new ContactDetail("11111111", true));
//
//        insightSynchroniser.contacts.pdContacts.add(c2);
//
//        return insightSynchroniser.contacts.pdContacts.size();
//    }
//
//    public int assignMatchingPDContacts() {
//        OrgId org_id = new OrgId();
//        org_id.setValue(15L);
//
//        PDContactReceived c1 = new PDContactReceived();
//        c1.setOrg_id(org_id);
//        c1.setName("Batman");
//        ContactDetail e1 = new ContactDetail();
//        e1.setPrimary(true);
//        e1.setValue("BatSignal@night.com");
//        c1.getEmail().add(e1);
//        ContactDetail p1 = new ContactDetail();
//        p1.setPrimary(true);
//        p1.setValue("0987654321");
//        c1.getPhone().add(p1);
//        ContactDetail e12 = new ContactDetail();
//        e12.setPrimary(false);
//        e12.setValue("Bruce@wayne.com");
//        c1.getEmail().add(e12);
//        ContactDetail p12 = new ContactDetail();
//        p12.setPrimary(false);
//        p12.setValue("1234567890");
//        c1.getPhone().add(p12);
//
//        PDContactReceived c2 = new PDContactReceived();
//        c2.setOrg_id(org_id);
//        c2.setName("Robin");
//        ContactDetail e2 = new ContactDetail();
//        e2.setPrimary(true);
//        e2.setValue("Robin@night.com");
//        c2.getEmail().add(e2);
//
//        PDContactReceived c3 = new PDContactReceived();
//        c3.setOrg_id(org_id);
//        c3.setName("Joker");
//        ContactDetail e3 = new ContactDetail();
//        e3.setPrimary(true);
//        e3.setValue("joke@you.com");
//        c3.getEmail().add(e3);
//        ContactDetail p3 = new ContactDetail();
//        p3.setPrimary(true);
//        p3.setValue("123123");
//        c3.getPhone().add(p3);
//
//        PDContactReceived c4 = new PDContactReceived();
//        c4.setOrg_id(org_id);
//        c4.setName("Penguin");
//        ContactDetail e4 = new ContactDetail();
//        e4.setPrimary(true);
//        e4.setValue("Penguin@large.com");
//        c4.getEmail().add(e4);
//        ContactDetail p4 = new ContactDetail();
//        p4.setPrimary(true);
//        p4.setValue("321321");
//        c4.getPhone().add(p4);
//
//        insightSynchroniser.contacts.pdContacts.add(c1);
//        insightSynchroniser.contacts.pdContacts.add(c2);
//        insightSynchroniser.contacts.pdContacts.add(c3);
//        insightSynchroniser.contacts.pdContacts.add(c4);
//
//        return insightSynchroniser.contacts.vContacts.size();
//
//    }
//
//    public int assignJustVContactList() {
//        VContact c1 = new VContact();
//        c1.setName("Batman");
//        c1.setEmail("BatSignal@night.com");
//
//        c1.setPhone("0987654321");
//
//        VContact c2 = new VContact();
//        c2.setName("Robin");
//        c2.setEmail("Robin@night.com");
//
//
//        VContact c3 = new VContact();
//        c3.setName("Joker");
//        c3.setEmail("joke@you.com");
//        c3.setPhone("123123");
//
//        VContact c4 = new VContact();
//        c4.setName("Penguin");
//        c4.setEmail("Penguin@large.com");
//        c4.setPhone("321321");
//
//        insightSynchroniser.contacts.vContacts.add(c1);
//        insightSynchroniser.contacts.vContacts.add(c2);
//        insightSynchroniser.contacts.vContacts.add(c3);
//        insightSynchroniser.contacts.vContacts.add(c4);
//
//        return insightSynchroniser.contacts.vContacts.size();
//    }
//
//}
