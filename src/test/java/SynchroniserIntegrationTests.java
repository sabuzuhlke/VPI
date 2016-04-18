import VPI.*;
import VPI.PDClasses.PDOrganisation;
import VPI.PDClasses.PDService;
import VPI.VClasses.InsightService;
import VPI.VClasses.VOrganisation;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(Application.class)
@WebIntegrationTest
public class SynchroniserIntegrationTests {

    private Synchroniser synchroniser;
    private PDService PD;

    @Before
    public void setUp() {
        String PDServer = "https://api.pipedrive.com/v1/";
        String VServer = "http://insight.zuehlke.com";
        this.synchroniser = new Synchroniser(PDServer, VServer);
    }


    public void clearSynchroniser() {
        synchroniser.clear();
    }

    @Test
    public void willPostandPutOrgsNotInPDToPD() {
        //this.setUpFakeInsightData();
        //List<PDOrganisation> PDOrgs = .getAllOrganisations().getBody().getData();

        synchroniser.getPDS().postOrganisation("Bentley Systems Germany GmbH", 3);

        List<Long> idsPushed = synchroniser.importOrganisations();
        assertEquals(idsPushed.size(),
                synchroniser.organisations.postList.size() + synchroniser.organisations.putList.size());

        assertTrue(synchroniser.organisations.putList.size() == 1);
        assertTrue(synchroniser.organisations.putList.get(0).getName().equals("Bentley Systems Germany GmbH"));

        List<PDOrganisation> pdOrgs = synchroniser.getPDS().getAllOrganisations().getBody().getData();

        int matches = 0;
        for(VOrganisation v : synchroniser.organisations.vOrganisations) {
            for(PDOrganisation p : pdOrgs) {
                if (v.getName().equals(p.getName())) {
                    matches++;
                }
            }
        }
        assertEquals(matches, idsPushed.size());

        List<Long> idsDeleted = synchroniser.getPDS().deleteOrganisationList(idsPushed);
        assertEquals(idsDeleted.size(),
                synchroniser.organisations.postList.size() + synchroniser.organisations.putList.size());
        assertEquals(idsDeleted, idsPushed);

        clearSynchroniser();
    }

    public void setUpFakeInsightData() {
        List<VOrganisation> VOrgs = new ArrayList<>();

        VOrganisation VOrg1= new VOrganisation("Name");
        VOrganisation VOrg2= new VOrganisation("SomeOtherName");
        VOrganisation VOrg3= new VOrganisation("OtherName");
        VOrganisation VOrg4= new VOrganisation("AntherName");

        VOrgs.add(VOrg1);
        VOrgs.add(VOrg2);
        VOrgs.add(VOrg3);
        VOrgs.add(VOrg4);

        synchroniser.organisations.vOrganisations = VOrgs;
    }
/*
    @Test
    public void syncDoesNotMakeDuplicateOrganisations() {

        //run synchronisation (GET ALL, Compare, POST NEW (Assumes no PUT))
        List<VOrganisation> VOrgs = IS.getAllOrganisations().getBody().getItems();
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

    }*/
}
