package CurrentTests;

import VPI.Entities.Contact;
import VPI.Entities.util.ContactDetail;
import VPI.Entities.Organisation;
import VPI.PDClasses.Contacts.PDContactReceived;
import VPI.PDClasses.Contacts.PDContactSend;
import VPI.PDClasses.Contacts.util.OrgId;
import VPI.PDClasses.Organisations.PDOrganisationReceived;
import VPI.PDClasses.Organisations.PDOrganisationSend;
import VPI.PDClasses.PDOwner;
import org.junit.Test;


import java.util.ArrayList;
import java.util.List;

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

        Organisation org = new Organisation(pdr);


        assertEquals(pdr.getId(), org.getPipedriveId());
        assertEquals(pdr.getV_id(), org.getVertecId());
        assertTrue(org.getActive());

        assertTrue(pdr.getOwner_id().getEmail().equals(org.getSupervisingEmail()));
        assertTrue(pdr.getOwnedBy().equals(org.getOwnedOnVertecBy()));
        assertTrue(pdr.getName().equals(org.getName()));
        assertTrue(pdr.getAddress().equals(org.getFull_address()));
        assertTrue(pdr.getCreationTime().equals(org.getCreated()));

        //TODO: category and business
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


