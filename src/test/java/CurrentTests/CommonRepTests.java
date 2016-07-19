package CurrentTests;

import VPI.Entities.Contact;
import VPI.Entities.util.ContactDetail;
import VPI.Entities.Organisation;
import VPI.Entities.util.Formatter;
import VPI.PDClasses.Contacts.PDContactReceived;
import VPI.PDClasses.Contacts.PDContactSend;
import VPI.PDClasses.Contacts.util.OrgId;
import VPI.PDClasses.Organisations.PDOrganisationReceived;
import VPI.PDClasses.Organisations.PDOrganisationSend;
import VPI.PDClasses.PDOwner;
import VPI.PDClasses.PDRelationship;
import org.junit.Test;


import java.text.Format;
import java.util.ArrayList;
import java.util.List;

import static VPI.Entities.util.Formatter.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CommonRepTests {

    @Test
    public void canConvertOrgToPDSend() {
        Organisation org = new Organisation();
        org.setActive(true);

        org.setPipedriveId(1L);
        org.setFull_address("10, Downig street, London, UK");
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
        //org.setStreet_no("NO");
        org.setSupervisingEmail("wolfgang.emmerich@zuhlke.com");
        org.setVertecId(1L);
        org.setWebsite("www.com");
        //org.setZip("666");

        Long pipedriveUserIdFromMap = 55L;

        PDOrganisationSend pds = org.toPDSend(pipedriveUserIdFromMap);

        assertEquals(pds.getActive_flag(), org.getActive());

        assertTrue(pds.getAddress().equals(org.getFull_address()));
        assertTrue(pds.getCreationTime().equals(org.getCreated()));
        assertTrue(pds.getName().equals(org.getName()));
        assertTrue(pds.getOwnedBy().equals(org.getOwnedOnVertecBy()));

        assertEquals(pds.getId(), org.getPipedriveId());
        assertEquals(pds.getOwner_id(), pipedriveUserIdFromMap);

        //TODO: category and business
    }

    @Test
    public void canConvertOrgReceivedtoCommon(){
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
        PDRelationship rel = new PDRelationship(parentId,1L);

        Organisation org = new Organisation(pdr, rel);

        assertEquals(pdr.getId(), org.getPipedriveId());
        assertEquals(pdr.getV_id(), org.getVertecId());
        assertTrue(org.getActive());

        assertTrue(pdr.getOwner_id().getEmail().equals(org.getSupervisingEmail()));
        assertTrue(pdr.getOwnedBy().equals(org.getOwnedOnVertecBy()));
        assertTrue(pdr.getName().equals(org.getName()));
        assertTrue(pdr.getAddress().equals(org.getFull_address()));
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
        org.setOwner_id(666L);
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

        String ownerEmailFromMap = "fed@us.com";

        Organisation organisation = new Organisation(org, ownerEmailFromMap);

        assertEquals(org.getVertecId(), organisation.getVertecId());

        assertTrue(org.getOwnedOnVertecBy().equals(organisation.getOwnedOnVertecBy()));
        assertTrue((org.getActive().equals(organisation.getActive())));
        assertTrue(org.getWebsite().equals(organisation.getWebsite()));
        assertTrue(org.getCategory().equals(organisation.getCategory()));
        assertTrue(org.getBusinessDomain().equals(organisation.getBusinessDomain()));
        assertTrue(org.getBuildingName().equals(organisation.getBuildingName()));
        assertTrue(org.getStreet_no().equals(organisation.getStreet_no()));
        assertTrue(org.getStreet().equals(organisation.getStreet()));
        assertTrue(org.getCity().equals(organisation.getCity()));
        assertTrue(org.getCountry().equals(organisation.getCountry()));
        assertTrue(org.getZip().equals(organisation.getZip()));
        assertTrue(org.getParentOrganisation().equals(organisation.getvParentOrganisation()));
        assertTrue(formatVertecDate(org.getModified()).equals(organisation.getModified()));
        assertTrue(formatVertecDate(org.getCreated()).equals(organisation.getCreated()));

        assertTrue(formatVertecAddress(org).equals(organisation.getFull_address()));

        assertTrue(ownerEmailFromMap.equals(organisation.getSupervisingEmail()));

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
        cOrg.setStreet_no("2");
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
        assertTrue(cOrg.getStreet_no().equals(org.getStreet_no()));
        assertTrue(cOrg.getStreet().equals(org.getStreet()));
        assertTrue(cOrg.getStreet().equals(org.getStreet()));
        assertTrue(cOrg.getOwnedOnVertecBy().equals(org.getOwnedOnVertecBy()));
        assertTrue(cOrg.getZip().equals(org.getZip()));
        assertTrue(cOrg.getZip().equals(org.getZip()));

        assertTrue(formatToVertecDate(cOrg.getModified()).equals(org.getModified()));
        assertTrue(formatToVertecDate(cOrg.getCreated()).equals(org.getCreated()));


        assertEquals(cOrg.getVertecId(), org.getVertecId());
        assertEquals(cOrg.getActive(), org.getActive());
        assertEquals(ownerIdFromMap, org.getOwner_id());
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


}


