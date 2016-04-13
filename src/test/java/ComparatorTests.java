import VPI.*;
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

        assertTrue(OC.getPutList().isEmpty());
        assertTrue(OC.getPostList().isEmpty());

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

        assertTrue(OC.getPutList().isEmpty());
        assertTrue(!OC.getPostList().isEmpty());
        assertTrue(OC.getPostList().get(0) != null);
        assertTrue(OC.getPostList().get(0).getName().equals("Name"));

        OC.clear();

    }

    @Test
    public void OCCorrectlySplitsListIntoAppropriatePutAndPostList() {

        this.setUpLists();


        OC.compareOrgs();

        assertTrue(!OC.getPostList().isEmpty());
        assertTrue(OC.getPutList().isEmpty());

        assertTrue(OC.getPostList().size() == 2);
        assertTrue(OC.getPostList().get(0).getName().equals("Name"));
        assertTrue(OC.getPostList().get(1).getName().equals("OtherName"));

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

        assertTrue(OC.getPostList().isEmpty());
        assertTrue(OC.getPutList().isEmpty());
        assertTrue(OC.getPDOrganisations().isEmpty());
        assertTrue(OC.getVOrganisations().isEmpty());

    }
}
