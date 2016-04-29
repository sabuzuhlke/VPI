import VPI.Application;
import VPI.PDClasses.ContactDetail;
import VPI.PDClasses.OrgId;
import VPI.PDClasses.PDContactReceived;
import VPI.VertecClasses.JSONContact;
import VPI.VertecSynchroniser;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * Created by sabu on 29/04/2016.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(Application.class)
public class VertecSynchroniserUnitTests {

    private VertecSynchroniser sync;

    @Before
    public void setUp() {
        this.sync = new VertecSynchroniser();
    }

    @Test
    public void correctlyResolvesDanglingContacts() {

        List<JSONContact> dangling = getListOfDanglingVertecContacts();
        List<PDContactReceived> pdContacts = getListOfDanglingPipedriveContacts();

        sync.compareDanglingContacts(dangling, pdContacts);

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

        List<JSONContact> dangling = getListOfDanglingVertecContacts();
        List<PDContactReceived> pdContacts = getListOfDanglingPipedriveContacts();

        JSONContact vc1 = dangling.get(1);
        PDContactReceived pc1 = pdContacts.get(1);

        assertTrue(sync.resolveContactDetails(vc1, pc1));

    }

    public List<PDContactReceived> getListOfDanglingPipedriveContacts() {
        List<PDContactReceived> list = new ArrayList<>();
        //TODO: add modifed date to contact recieved to choose prevelance
        PDContactReceived c1 = new PDContactReceived();
        c1.setName("c1 surname1");
        c1.setOrg_id(null);
        c1.setActive_flag(true);

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




        PDContactReceived c2 = new PDContactReceived();
        c2.setName("c2 surname2");
        c2.setOrg_id(null);
        c2.setActive_flag(true);

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
        c1.setObjid(10L);
        c1.setOwner("owner");
        c1.setPhone("phone");
        c1.setSurname("surname1");


        JSONContact c2 = new JSONContact();
        c2.setEmail("email");
        c2.setFirstName("c2");
        c2.setMobile("mobile");
        c2.setModified("mod");
        c2.setObjid(20L);
        c2.setOwner("owner");
        c2.setPhone("phone");
        c2.setSurname("surname2");


        JSONContact c3 = new JSONContact();
        c3.setEmail("email");
        c3.setFirstName("c3");
        c3.setMobile("mobile");
        c3.setModified("mod");
        c3.setObjid(30L);
        c3.setOwner("owner");
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


}
