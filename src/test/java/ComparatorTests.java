import VPI.*;
import VPI.PDClasses.PDOrganisation;
import VPI.VClasses.VContact;
import VPI.VClasses.VOrganisation;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.OutputCapture;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(Application.class)
public class ComparatorTests {

    private Comparator OC;

    @Rule
    public OutputCapture capture = new OutputCapture();

    @Before
    public void setUp() throws Exception {

        this.OC = new Comparator();

    }

    @Test
    public void givenTwoMatchingItemsNoPutorPost(){

        VOrganisation VOrg= new VOrganisation("MatchingName");
        PDOrganisation PDOrg = new PDOrganisation("MatchingName",3);

        List<PDOrganisation> PDorgs = new ArrayList<>();
        List<VOrganisation> VOrgs = new ArrayList<>();

        PDorgs.add(PDOrg);
        VOrgs.add(VOrg);

        OC.setPDOrganisations(PDorgs);
        OC.setVOrganisations(VOrgs);

        OC.compareOrgs();

        assertTrue(OC.getOrganisationPutList().isEmpty());
        assertTrue(OC.getOrganisationPostList().isEmpty());

        OC.clear();
    }

    @Test
    public void givenTwoNonMatchingItemsNoPutButOnePost() {
        VOrganisation VOrg= new VOrganisation("Name");
        PDOrganisation PDOrg = new PDOrganisation("NonMatchingName",3);

        List<PDOrganisation> PDorgs = new ArrayList<>();
        List<VOrganisation> VOrgs = new ArrayList<>();

        PDorgs.add(PDOrg);
        VOrgs.add(VOrg);

        OC.setPDOrganisations(PDorgs);
        OC.setVOrganisations(VOrgs);

        OC.compareOrgs();

        assertTrue(OC.getOrganisationPutList().isEmpty());
        assertTrue(!OC.getOrganisationPostList().isEmpty());
        assertTrue(OC.getOrganisationPostList().get(0) != null);
        assertTrue(OC.getOrganisationPostList().get(0).getName().equals("Name"));

        OC.clear();

    }

    @Test
    public void willPUTButNotPOSTWhenNamesMatchButAddressesDiffer() {
        List<PDOrganisation> PDorgs = new ArrayList<>();
        List<VOrganisation> VOrgs = new ArrayList<>();

        VOrganisation vorg = new VOrganisation("MatchingName");
        vorg.setStreet("15 Road Avenue");
        vorg.setCity("SinCity");
        vorg.setCountry("Murica!");
        vorg.setZip("666");
        VOrgs.add(vorg);

        PDOrganisation pdorg = new PDOrganisation("MatchingName", "42 Answer Street, SinCity, 654, Murica!");
        PDorgs.add(pdorg);

        OC.setPDOrganisations(PDorgs);
        OC.setVOrganisations(VOrgs);

        OC.compareOrgs();

        assertTrue(OC.getOrganisationPostList().isEmpty());
        assertTrue(!OC.getOrganisationPutList().isEmpty());
        assertTrue(OC.getOrganisationPutList().get(0) != null);
        assertTrue(OC.getOrganisationPutList().get(0).getAddress().equals("15 Road Avenue, SinCity, 666, Murica!"));

        OC.clear();
    }

    @Test
    public void OCCorrectlySplitsListIntoAppropriatePutAndPostList() {

        this.setUpLists();


        OC.compareOrgs();

        assertTrue(!OC.getOrganisationPostList().isEmpty());
        assertTrue(OC.getOrganisationPutList().isEmpty());

        assertTrue(OC.getOrganisationPostList().size() == 2);
        assertTrue(OC.getOrganisationPostList().get(0).getName().equals("Name"));
        assertTrue(OC.getOrganisationPostList().get(1).getName().equals("OtherName"));

        OC.clear();
    }

    public void setUpLists() {
        List<PDOrganisation> PDorgs = new ArrayList<>();
        List<VOrganisation> VOrgs = new ArrayList<>();

        VOrganisation VOrg1= new VOrganisation("Name");
        PDOrganisation PDOrg1 = new PDOrganisation("NonMatchingName",3);
        PDorgs.add(PDOrg1);
        VOrgs.add(VOrg1);

        VOrganisation VOrg2= new VOrganisation("MatchingName");
        PDOrganisation PDOrg2 = new PDOrganisation("MatchingName",3);
        PDorgs.add(PDOrg2);
        VOrgs.add(VOrg2);

        VOrganisation VOrg3= new VOrganisation("OtherName");
        VOrgs.add(VOrg3);

        OC.setPDOrganisations(PDorgs);
        OC.setVOrganisations(VOrgs);

    }

    @Test
    public void canClearOCLists() {

        this.setUpLists();

        OC.compareOrgs();

        OC.clear();

        assertTrue(OC.getOrganisationPostList().isEmpty());
        assertTrue(OC.getOrganisationPutList().isEmpty());
        assertTrue(OC.getPDOrganisations().isEmpty());
        assertTrue(OC.getVOrganisations().isEmpty());

    }

    @Test
    public void givenNoMatchWillPostNewOrgWithContacts() {
        //create prerequisites --vorg, NO pdorg, vcontact
        List<PDOrganisation> PDorgs = new ArrayList<>();
        List<VOrganisation> VOrgs = new ArrayList<>();
        List<VContact> VContacts = new ArrayList<>();
        Long orgid = 1L;

        VOrganisation VOrg1= new VOrganisation("Name");
        VOrg1.setId(orgid);
        PDOrganisation PDOrg1 = new PDOrganisation("NonMatchingName",3);
        PDorgs.add(PDOrg1);
        VOrgs.add(VOrg1);

        VContact newContact = new VContact();
        newContact.setName("Peter Griffin");
        newContact.setOrg_id(orgid);
        VContacts.add(newContact);

        OC.setPDOrganisations(PDorgs);
        OC.setVOrganisations(VOrgs);
        OC.setVContacts(VContacts);
        //
        OC.compareOrgs();

        assertTrue(OC.getContactPutList().isEmpty());
        assertTrue(!OC.getContactPostList().isEmpty());
        assertTrue(OC.getContactPostList().get(0) != null);
        assertTrue(OC.getContactPostList().get(0).getName().equals("Peter Griffin"));
        assertTrue(OC.getContactPostList().get(0).getOrg_id() == 1);



    }
 /*
    @Test
    public void givenMatchWillPostNewContactsToOrg() {

    }

    //TESTS FOR WHEN VCONTACT HAS ORGANISATIONS

    @Test
    public void givenMatchAndDifferentContactWillUpdateContact() {

    }*/

}
