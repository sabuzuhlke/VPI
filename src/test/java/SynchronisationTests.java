import VPI.*;
import com.sun.org.apache.xpath.internal.operations.Bool;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.OutputCapture;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


/**
 * Created by sabu on 13/04/2016.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(Application.class)
@WebIntegrationTest
public class SynchronisationTests {

    private PDService PS;
    private InsightService IS;
    private OrganisationComparator C;

    @Before
    public void setUp() {
        TestRestTemplate testRestTemplate = new TestRestTemplate();

        //set up PDService
        String server = "https://api.pipedrive.com/v1/";
        String apiKey = "?api_token=eefa902bdca498a342552b837663f38b566bce5a";
        this.PS = new PDService(testRestTemplate, server, apiKey);

        //set up InsightService
        String iServer = "http://insight.zuehlke.com";
        MyCredentials creds = new MyCredentials();
        this.IS = new InsightService(
                testRestTemplate,
                iServer,
                creds.getUserName(),
                creds.getPass()
        );

        //create Comparator
        C = new OrganisationComparator();
    }

    @Test
    public void willPostOrgsNotInPDToPD() {
        this.setUpFakeInsightData();
        List<PDOrganisation> PDOrgs = PS.getAllOrganisations().getBody().getData();

        C.setPDOrganisations(PDOrgs);

        C.compareOrgs();

        assertTrue(C.getPutList().isEmpty());
        assertTrue(!C.getPostList().isEmpty());

        List<Long> idsPosted = PS.postOrganisationList(C.getPostList());
        assertEquals(idsPosted.size(), C.getPostList().size());

        int index = 0;
        for(Long id : idsPosted) {
            String name = PS.getOrganisation(id).getBody().getData().getName();
            assertTrue(name.equals(C.getPostList().get(index).getName()));
            index++;
        }

        List<Long> idsDeleted = PS.deleteOrganisationList(idsPosted);
        assertEquals(idsDeleted.size(), C.getPostList().size());
        assertEquals(idsDeleted, idsPosted);

        C.clear();
    }

    public void setUpFakeInsightData() {
        List<ICompany> VOrgs = new ArrayList<>();

        ICompany VOrg1= new ICompany("Name");
        ICompany VOrg2= new ICompany("SomeOtherName");
        ICompany VOrg3= new ICompany("OtherName");
        ICompany VOrg4= new ICompany("AntherName");

        VOrgs.add(VOrg1);
        VOrgs.add(VOrg2);
        VOrgs.add(VOrg3);
        VOrgs.add(VOrg4);

        C.setVOrganisations(VOrgs);
    }

    @Test
    public void syncDoesNotMakeDuplicateOrganisations() {

        //run synchronisation (GET ALL, Compare, POST NEW (Assumes no PUT))
        List<ICompany> VOrgs = IS.getAllOrganisations().getBody().getItems();
        List<PDOrganisation> PDOrgs = PS.getAllOrganisations().getBody().getData();

        C.setPDOrganisations(PDOrgs);
        C.setVOrganisations(VOrgs);

        C.compareOrgs();

        int postListSize = C.getPostList().size();

        List<Long> idsPosted = PS.postOrganisationList(C.getPostList());
        assertEquals(idsPosted.size(), postListSize);

        C.clear();
        //re run synchronisation without posting
        PDOrgs = PS.getAllOrganisations().getBody().getData();
        VOrgs = IS.getAllOrganisations().getBody().getItems();

        C.setPDOrganisations(PDOrgs);
        C.setVOrganisations(VOrgs);

        C.compareOrgs();

        assertTrue(C.getPutList().isEmpty());
        assertTrue(C.getPostList().isEmpty());

        //delete posted organisations
        List<Long> idsDeleted = PS.deleteOrganisationList(idsPosted);
        assertEquals(idsDeleted.size(), postListSize);
        assertEquals(idsDeleted, idsPosted);

        C.clear();

    }
}
