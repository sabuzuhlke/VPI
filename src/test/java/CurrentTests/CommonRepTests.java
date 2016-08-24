package CurrentTests;

import VPI.Entities.Activity;
import VPI.Entities.Contact;
import VPI.Entities.util.ContactDetail;
import VPI.Entities.Organisation;
import VPI.Entities.util.Utilities;
import VPI.PDClasses.Activities.PDActivityReceived;
import VPI.PDClasses.Activities.PDActivitySend;
import VPI.PDClasses.Contacts.PDContactReceived;
import VPI.PDClasses.Contacts.PDContactSend;
import VPI.PDClasses.Contacts.util.OrgId;
import VPI.PDClasses.HierarchyClasses.LinkedOrg;
import VPI.PDClasses.HierarchyClasses.PDRelationshipReceived;
import VPI.PDClasses.Organisations.PDOrganisationReceived;
import VPI.PDClasses.Organisations.PDOrganisationSend;
import VPI.PDClasses.PDOwner;
import org.apache.commons.collections4.BidiMap;
import org.junit.Test;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static VPI.Entities.util.Utilities.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CommonRepTests {

    @Test
    public void canConvertOrgToPDSend() {
        Organisation org = new Organisation();
        org.setActive(true);

        org.setPipedriveId(1L);
        org.setFullAddress("10, Downig street, London, UK");
        //org.setBuildingName("this building");
        org.setBusinessDomain("business");
        org.setCategory("arms trade");
        //org.setCity("London");
        //org.setCountry("UK");
        org.setCreated("1999-12-12 00:00:00");
        org.setModified("2222-12-12 00:00:00");
        org.setName("Organisation Co Ltd");
        org.setOwnedOnVertecBy("ZUK");
        //org.setPipedriveId(null);
        //org.setStreet("street");
        //org.setStreetNo("NO");
        org.setSupervisingEmail("wolfgang.emmerich@zuhlke.com");
        org.setVertecId(1L);
        org.setWebsite("www.com");
        //org.setZip("666");

        Long pipedriveUserIdFromMap = 55L;

        PDOrganisationSend pds = org.toPDSend(pipedriveUserIdFromMap);

        assertEquals(pds.getActive_flag(), org.getActive());

        assertTrue(pds.getAddress().equals(org.getFullAddress()));
        assertTrue(pds.getCreationTime().equals(org.getCreated()));
        assertTrue(pds.getName().equals(org.getName()));
        assertTrue(pds.getOwnedBy().equals(org.getOwnedOnVertecBy()));

        assertEquals(pds.getId(), org.getPipedriveId());
        assertEquals(pds.getOwner_id(), pipedriveUserIdFromMap);

        //TODO: category and business
    }

    @Test
    public void canConvertOrgReceivedtoCommon() throws IOException {
        BidiMap<Long, Long> orgIdmap = Utilities.loadIdMap("productionMaps/productionOrganisationMap");
        PDOrganisationReceived pdr = new PDOrganisationReceived();
        pdr.setAddress("10, Downig street, London, UK");
        pdr.setId(1L);
        pdr.setActive_flag(true);
        pdr.setName("name");
        pdr.setOwnedBy("ZUK");
        pdr.setCreationTime("1999-12-12 00:00:00");

        PDOwner pdo = new PDOwner();
        pdo.setId(1L);
        pdo.setEmail("me@only.com");
        pdr.setOwner_id(pdo);

        pdr.setV_id(2L);

        Long parentId = 5L;
        LinkedOrg parent = new LinkedOrg();
        parent.setName("Hola");
        parent.setId(9L);

        LinkedOrg child = new LinkedOrg();
        child.setName(pdr.getName());
        child.setId(pdr.getId());
        PDRelationshipReceived rel = new PDRelationshipReceived(parent,child);

        Organisation org = new Organisation(pdr, rel, orgIdmap);

        assertEquals(pdr.getId(), org.getPipedriveId());
        assertEquals(pdr.getV_id(), org.getVertecId());
        assertTrue(org.getActive());

        assertTrue(pdr.getOwner_id().getEmail().equals(org.getSupervisingEmail()));
        assertTrue(pdr.getOwnedBy().equals(org.getOwnedOnVertecBy()));
        assertTrue(pdr.getName().equals(org.getName()));
        assertTrue(pdr.getAddress().equals(org.getFullAddress()));
        assertTrue(pdr.getCreationTime().equals(org.getCreated()));
        assertEquals(parentId, org.getvParentOrganisation());

        //TODO: category and business
    }

    @Test
    public void canConvertVOrgToCommon(){
        VPI.VertecClasses.VertecOrganisations.Organisation org = new VPI.VertecClasses.VertecOrganisations.Organisation();

        org.setVertecId(1L);
        org.setOwnedOnVertecBy("hahah");
        org.setActive(true);
        org.setOwnerId(666L);
        org.setName("GMO Alliance");
        org.setWebsite("gmo@health.com");
        org.setCategory("agricultural");
        org.setBusinessDomain("Organised Crime");
        org.setBuildingName("buildiing1");
        org.setStreet_no("2");
        org.setStreet("Cornfield");
        org.setCity("Paradise City");
        org.setCountry("USA");
        org.setZip("TDL 454");
        org.setParentOrganisation(3L); //Add representation into common
        org.setModified("1998-12-12T00:00:00");
        org.setCreated("1999-12-12T00:00:00");

        Long pipedriveOrgIdfromMap = 4L;
        String ownerEmailFromMap = "fed@us.com";

        Organisation organisation = new Organisation(org, pipedriveOrgIdfromMap, ownerEmailFromMap);

        assertEquals(org.getVertecId(), organisation.getVertecId());

        assertTrue(org.getOwnedOnVertecBy().equals(organisation.getOwnedOnVertecBy()));
        assertTrue((org.getActive().equals(organisation.getActive())));
        assertTrue(org.getWebsite().equals(organisation.getWebsite()));
        assertTrue(org.getCategory().equals(organisation.getCategory()));
        assertTrue(org.getBusinessDomain().equals(organisation.getBusinessDomain()));
        assertTrue(org.getBuildingName().equals(organisation.getBuildingName()));
        assertTrue(org.getStreet_no().equals(organisation.getStreetNo()));
        assertTrue(org.getStreet().equals(organisation.getStreet()));
        assertTrue(org.getCity().equals(organisation.getCity()));
        assertTrue(org.getCountry().equals(organisation.getCountry()));
        assertTrue(org.getZip().equals(organisation.getZip()));
        assertTrue(org.getParentOrganisation().equals(organisation.getvParentOrganisation()));
        assertTrue(formatVertecDate(org.getModified()).equals(organisation.getModified()));
        assertTrue(formatVertecDate(org.getCreated()).equals(organisation.getCreated()));

        assertTrue(formatVertecAddress(org).equals(organisation.getFullAddress()));

        assertTrue(ownerEmailFromMap.equals(organisation.getSupervisingEmail()));

        assertEquals(pipedriveOrgIdfromMap, organisation.getPipedriveId());

    }

    @Test
    public void canConvertOrganisationtoVorg(){
        Organisation cOrg = new Organisation();
        cOrg.setName("GMO Alliance");
        cOrg.setPipedriveId(1L);
        cOrg.setVertecId(2L);
        cOrg.setActive(true);
        cOrg.setBuildingName("buil");
        cOrg.setBusinessDomain("Organised Crime");
        cOrg.setCategory("Agricurtural");
        cOrg.setSupervisingEmail("fed@us.com");
        cOrg.setCity("city");
        cOrg.setCountry("THE USA!");
        cOrg.setWebsite("gmoforsale@mygarden.com");
        cOrg.setCreated("1889-11-32 00:00:00");

        cOrg.setStreet("Street");
        cOrg.setStreetNo("2");
        cOrg.setModified("1999-12-12 00:00:00");
        cOrg.setOwnedOnVertecBy("zuk");
        cOrg.setZip("7878");

        cOrg.setvParentOrganisation(3L);
    //fullAddress does not need to be set here

        Long ownerIdFromMap = 4L;

        VPI.VertecClasses.VertecOrganisations.Organisation org = cOrg.toVertecRep(ownerIdFromMap);

        assertTrue(cOrg.getName().equals(org.getName()));
        assertTrue(cOrg.getBuildingName().equals(org.getBuildingName()));
        assertTrue(cOrg.getBusinessDomain().equals(org.getBusinessDomain()));
        assertTrue(cOrg.getCategory().equals(org.getCategory()));
        assertTrue(cOrg.getCity().equals(org.getCity()));
        assertTrue(cOrg.getCountry().equals(org.getCountry()));
        assertTrue(cOrg.getWebsite().equals(org.getWebsite()));
        assertTrue(cOrg.getCreated().equals(org.getCreated()));
        assertTrue(cOrg.getStreetNo().equals(org.getStreet_no()));
        assertTrue(cOrg.getStreet().equals(org.getStreet()));
        assertTrue(cOrg.getStreet().equals(org.getStreet()));
        assertTrue(cOrg.getOwnedOnVertecBy().equals(org.getOwnedOnVertecBy()));
        assertTrue(cOrg.getZip().equals(org.getZip()));
        assertTrue(cOrg.getZip().equals(org.getZip()));

        assertTrue(formatToVertecDate(cOrg.getModified()).equals(org.getModified()));
        assertTrue(formatToVertecDate(cOrg.getCreated()).equals(org.getCreated()));


        assertEquals(cOrg.getVertecId(), org.getVertecId());
        assertEquals(cOrg.getActive(), org.getActive());
        assertEquals(ownerIdFromMap, org.getOwnerId());
        assertEquals(cOrg.getvParentOrganisation(), org.getParentOrganisation());





    }

    @Test
    public void canConvertContactRecToCommon(){
        PDContactReceived pdr = new PDContactReceived();
        pdr.setVisible_to(3);
        pdr.setV_id(1L);
        pdr.setId(2L);
        pdr.setActive_flag(true);
        pdr.setOwnedBy("ZUK");

        PDOwner pdo = new PDOwner();
        pdo.setEmail("mrRobot@fsociety.org");

        pdr.setOwner_id(pdo);

        pdr.setName("Elliot Unknown");
        pdr.setLast_name("Unknown");
        pdr.setFirst_name("Elliot");
        pdr.setCreationTime("1999-12-12 00:00:00");
        pdr.setModifiedTime("1998-12-12 00:00:00");

        List<ContactDetail> emails = new ArrayList<>();
        ContactDetail dc = new ContactDetail("elliot@fsociety.org", true);
        emails.add(dc);
        dc = new ContactDetail("mrRobot@fsociety.org", false);
        emails.add(dc);
        pdr.setEmail(emails);

        List<ContactDetail> phones = new ArrayList<>();
        ContactDetail phone = new ContactDetail("099", true);
        phones.add(phone);
        phone = new ContactDetail("00", false);
        phones.add(phone);
        pdr.setPhone(phones);

        OrgId oid = new OrgId();
        oid.setValue(1L);
        pdr.setOrg_id(oid);

        pdr.setPosition("Security Engineer");

        List<String> followers = new ArrayList<>();
        String follower = "darlene@fsociety.org";
        followers.add(follower);


        Long vertecOrgIDFromMap = 2L;

        Contact contact = new Contact(pdr,vertecOrgIDFromMap, followers);

        assertEquals(3, contact.getVisible_to().longValue());
        assertEquals(pdr.getV_id(),contact.getVertecId());
        assertEquals(pdr.getId(), contact.getPipedriveId());
        assertEquals(pdr.getActive_flag(), contact.getActive());

        assertTrue(pdr.getName().equals(contact.getFullName()));
        assertTrue(pdr.getOwnedBy().equals(contact.getOwnedOnVertecBy()));
        assertTrue(pdr.getCreationTime().equals(contact.getCreationTime()));
        assertTrue(pdr.getModifiedTime().equals(contact.getModifiedTime()));
        assertTrue(pdr.getOwner_id().getEmail().equals(contact.getOwnerEmail()));

        assertTrue(pdr.getEmail().get(0).getPrimary().equals(contact.getEmails().get(0).getPrimary()));
        assertTrue(pdr.getEmail().get(0).getValue().equals(contact.getEmails().get(0).getValue()));
        assertTrue(pdr.getEmail().get(1).getPrimary().equals(contact.getEmails().get(1).getPrimary()));
        assertTrue(pdr.getEmail().get(1).getValue().equals(contact.getEmails().get(1).getValue()));
        assertTrue(pdr.getPhone().get(0).getPrimary().equals(contact.getPhones().get(0).getPrimary()));
        assertTrue(pdr.getPhone().get(0).getValue().equals(contact.getPhones().get(0).getValue()));
        assertTrue(pdr.getPhone().get(1).getPrimary().equals(contact.getPhones().get(1).getPrimary()));
        assertTrue(pdr.getPhone().get(1).getValue().equals(contact.getPhones().get(1).getValue()));

        assertTrue(contact.getFollowers().get(0).equals("darlene@fsociety.org"));

        assertEquals(pdr.getOrg_id().getValue(), contact.getPipedriveOrgLink());
        assertEquals(vertecOrgIDFromMap, contact.getVertecOrgLink());

        assertTrue(pdr.getPosition().equals(contact.getPosition()));
    }
    
    @Test
    public void canConvertContactToPDSend(){
        Contact contact = new Contact();

        contact.setVisible_to(3);
        contact.setVertecId(1L);
        contact.setPipedriveId(2L);
        contact.setActive(true);
        contact.setOwnedOnVertecBy("ZUK");

        contact.setOwnerEmail("mrRobot@fsociety.org");
;
        contact.setSurname("Unknown");
        contact.setFirstName("Elliot");
        contact.setCreationTime("1999-12-12 00:00:00");
        contact.setModifiedTime("1998-12-12 00:00:00");

        List<ContactDetail> emails = new ArrayList<>();
        ContactDetail dc = new ContactDetail("elliot@fsociety.org", true);
        emails.add(dc);
        dc = new ContactDetail("mrRobot@fsociety.org", false);
        emails.add(dc);
        contact.setEmails(emails);

        List<ContactDetail> phones = new ArrayList<>();
        ContactDetail phone = new ContactDetail("099", true);
        phones.add(phone);
        phone = new ContactDetail("00", false);
        phones.add(phone);
        contact.setPhones(phones);

        contact.setPipedriveOrgLink(1L);
        contact.setVertecOrgLink(2L);

        contact.setPosition("Security Engineer");

        List<String> followers = new ArrayList<>();
        String follower = "darlene@fsociety.org";
        followers.add(follower);
        contact.setFollowers(followers);

        Long pipedriveOwnerIdFromMap = 1L;

        PDContactSend pds = contact.toPDSend(pipedriveOwnerIdFromMap);

        assertEquals(3, contact.getVisible_to().longValue());
        assertEquals(pds.getV_id(),contact.getVertecId());
        assertEquals(pds.getId(), contact.getPipedriveId());
        assertEquals(pds.getActive_flag(), contact.getActive());

        assertTrue(pds.getName().equals(contact.getFullName()));

        assertTrue(pds.getOwnedBy().equals(contact.getOwnedOnVertecBy()));
        assertTrue(pds.getCreationTime().equals(contact.getCreationTime()));
        assertTrue(pds.getModifiedTime().equals(contact.getModifiedTime()));
        assertEquals(pds.getOwner_id(), pipedriveOwnerIdFromMap);

        assertTrue(pds.getEmail().get(0).getPrimary().equals(contact.getEmails().get(0).getPrimary()));
        assertTrue(pds.getEmail().get(0).getValue().equals(contact.getEmails().get(0).getValue()));
        assertTrue(pds.getEmail().get(1).getPrimary().equals(contact.getEmails().get(1).getPrimary()));
        assertTrue(pds.getEmail().get(1).getValue().equals(contact.getEmails().get(1).getValue()));
        assertTrue(pds.getPhone().get(0).getPrimary().equals(contact.getPhones().get(0).getPrimary()));
        assertTrue(pds.getPhone().get(0).getValue().equals(contact.getPhones().get(0).getValue()));
        assertTrue(pds.getPhone().get(1).getPrimary().equals(contact.getPhones().get(1).getPrimary()));
        assertTrue(pds.getPhone().get(1).getValue().equals(contact.getPhones().get(1).getValue()));

        assertEquals(pds.getOrg_id(), contact.getPipedriveOrgLink());

        assertTrue(pds.getPosition().equals(contact.getPosition()));
    }

    @Test
    public void canConvertPdReceivedtoActivity() {

        PDActivityReceived pActivity = new PDActivityReceived();

        pActivity.setId(1L);
        pActivity.setDone(true);
        pActivity.setType("b");
        pActivity.setDue_date("2222-22-22");
        pActivity.setDue_time("11:11:11");
        pActivity.setDuration("3min");
        pActivity.setAdd_time("1512-04-04 00:00:00");
        pActivity.setMarked_as_done_time("");
        pActivity.setSubject("World Peace");
        pActivity.setDeal_id(3L);
        pActivity.setOrg_id(4L);
        pActivity.setPerson_id(5L);
        pActivity.setActive_flag(true);
        pActivity.setUpdate_time("1999-99-99 00:00:00");
        pActivity.setNote("V_ID:123#<br> remember the following:");
        pActivity.setAssigned_to_user_id(6L);
        pActivity.setCreated_by_user_id(7L);
        pActivity.setDone_date("1777-23-23");

        Long vOrgFromMap = 12L;
        Long vertecDealLinkfromMap = 8L;
        Long vertecProjectFromMap = 13L;
        Long vContactFromMap = 9L;
        Long vAssigneeFromMap = 10L;
        Long vCreatorFromMap = 11L;

        String vTypeFromMap = "hab";


        Activity activity = new Activity(pActivity, vOrgFromMap, vertecDealLinkfromMap, vertecProjectFromMap, vContactFromMap, vAssigneeFromMap, vTypeFromMap);

        assertTrue(pActivity.getType().equals(activity.getpType()));
        assertTrue(vTypeFromMap.equals(activity.getvType()));


        assertTrue(pActivity.getDuration().equals(activity.getpDuration()));
        assertTrue(pActivity.getSubject().equals(activity.getSubject()));
        assertTrue(extractNoteFromNoteWithVID(pActivity.getNote()).equals(activity.getText()));

        assertTrue((pActivity.getDue_date() + " " + pActivity.getDue_time()).equals(activity.getDueDate()));

        assertTrue(pActivity.getAdd_time().equals(activity.getCreated()));

        assertTrue(pActivity.getMarked_as_done_time().equals(activity.getDoneDate()));

        assertEquals(pActivity.getId(), activity.getPipedriveId());
        assertEquals(extractVID(pActivity.getNote()), activity.getVertecId());


        assertEquals(pActivity.getDeal_id(), activity.getPipedriveDealLink());
        assertEquals(vertecDealLinkfromMap, activity.getVertecDealLink());
        assertEquals(vertecProjectFromMap, activity.getVertecProjectLink());

        assertEquals(pActivity.getPerson_id(), activity.getPipedriveContactLink());
        assertEquals(vContactFromMap, activity.getVertecContactLink());

        assertEquals(pActivity.getAssigned_to_user_id(), activity.getPipedriveAssignee());
        assertEquals(vAssigneeFromMap, activity.getVertecAssignee());

        assertEquals(pActivity.getOrg_id(), activity.getPipedriveOrganisationLink());
        assertEquals(vOrgFromMap, activity.getVertecOrganisationLink());

        }

    @Test
    public void canCreatePdSendFromActivity(){
        Activity activity = new Activity();

        activity.setPipedriveId(1L);
        activity.setVertecId(2L);

        activity.setDoneDate("1222-12-12 00:00:00");
        activity.setActive(true);
        activity.setDone(true);
        activity.setpType("call");
        activity.setpDuration("7min");
        activity.setSubject("World Domination");
        activity.setText("notext");
        activity.setDueDate("1222-12-10 00:00:02");
        activity.setDoneDate("1222-12-19 00:00:03");
        activity.setCreated("1222-12-12 00:00:04");
        activity.setModified("1222-12-12 00:00:05");

        activity.setPipedriveOrganisationLink(3L);
        activity.setPipedriveDealLink(4L);
        activity.setPipedriveContactLink(5L);
        activity.setPipedriveAssignee(6L);

        PDActivitySend pdActivity = activity.toPDSend();

        assertTrue(activity.getDoneDate().equals(pdActivity.getDone_date()));
        assertTrue(activity.getCreated().equals(pdActivity.getAdd_time()));

        assertTrue(activity.getDueDate().equals(pdActivity.getDue_date() + " " + pdActivity.getDue_time()));

        assertTrue(activity.getpType().equals(pdActivity.getType()));
        assertTrue(activity.getpDuration().equals(pdActivity.getDuration()));
        assertTrue(activity.getSubject().equals(pdActivity.getSubject()));

        assertTrue(pdActivity.getNote().equals("V_ID:" + activity.getVertecId() + "#<br>" + reformatToHtml(activity.getText())));

        assertEquals(activity.getPipedriveId(), pdActivity.getId());
        assertEquals(activity.getPipedriveOrganisationLink(), pdActivity.getOrg_id());
        assertEquals(activity.getPipedriveDealLink(), pdActivity.getDeal_id());
        assertEquals(activity.getPipedriveContactLink(), pdActivity.getPerson_id());
        assertEquals(activity.getPipedriveAssignee(), pdActivity.getUser_id());
        assertEquals(activity.getDone(), pdActivity.getDone());
    }

}
















