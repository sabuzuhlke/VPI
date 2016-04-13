/**
 * Created by gebo on 13/04/2016.
 */
import VPI.*;
import com.sun.tools.javac.util.Pair;
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

    private OrganisationComparator OC;

    @Rule
    public OutputCapture capture = new OutputCapture();

    @Before
    public void setUp() throws Exception {

        this.OC = new OrganisationComparator();

    }

    @Test
    public void givenTwoMatchingItemsNoPutorPost(){

        ICompany VOrg= new ICompany("MatchingName");
        PDOrganisation PDOrg = new PDOrganisation("MatchingName",3);

        List<PDOrganisation> PDorgs = new ArrayList<>();
        List<ICompany> VOrgs = new ArrayList<>();

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
        ICompany VOrg= new ICompany("Name");
        PDOrganisation PDOrg = new PDOrganisation("NonMatchingName",3);

        List<PDOrganisation> PDorgs = new ArrayList<>();
        List<ICompany> VOrgs = new ArrayList<>();

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

        Pair<List<PDOrganisation>,List<ICompany>> lists = this.setUpLists();

        OC.setPDOrganisations(lists.fst);
        OC.setVOrganisations(lists.snd);

        OC.compareOrgs();

        assertTrue(!OC.getPostList().isEmpty());
        assertTrue(OC.getPutList().isEmpty());

        assertTrue(OC.getPostList().size() == 2);
        assertTrue(OC.getPostList().get(0).getName().equals("Name"));
        assertTrue(OC.getPostList().get(1).getName().equals("OtherName"));

        OC.clear();
    }

    public Pair<List<PDOrganisation>,List<ICompany>> setUpLists() {
        List<PDOrganisation> PDorgs = new ArrayList<>();
        List<ICompany> VOrgs = new ArrayList<>();

        ICompany VOrg1= new ICompany("Name");
        PDOrganisation PDOrg1 = new PDOrganisation("NonMatchingName",3);
        PDorgs.add(PDOrg1);
        VOrgs.add(VOrg1);

        ICompany VOrg2= new ICompany("MatchingName");
        PDOrganisation PDOrg2 = new PDOrganisation("MatchingName",3);
        PDorgs.add(PDOrg2);
        VOrgs.add(VOrg2);

        ICompany VOrg3= new ICompany("OtherName");
        VOrgs.add(VOrg3);

        return new Pair(PDorgs,VOrgs);

    }

    @Test
    public void canClearOCLists() {

        Pair<List<PDOrganisation>,List<ICompany>> lists = this.setUpLists();

        OC.setPDOrganisations(lists.fst);
        OC.setVOrganisations(lists.snd);

        OC.compareOrgs();

        OC.clear();

        assertTrue(OC.getPostList().isEmpty());
        assertTrue(OC.getPutList().isEmpty());
        assertTrue(OC.getPDOrganisations().isEmpty());
        assertTrue(OC.getVOrganisations().isEmpty());

    }
}
