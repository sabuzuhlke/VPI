package InsightTests;

import VPI.*;
import VPI.PDClasses.PDOrganisation;
import VPI.PDClasses.PDService;
import VPI.InsightClasses.VOrganisation;
import VPI.InsightClasses.InsightSynchroniser;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(Application.class)
@WebIntegrationTest
public class InsightSynchroniserIntegrationTests {

    private InsightSynchroniser insightSynchroniser;
    private PDService PD;

    @Before
    public void setUp() {
        String PDServer = "https://api.pipedrive.com/v1/";
        String VServer = "http://insight.zuehlke.com";
        this.insightSynchroniser = new InsightSynchroniser(PDServer, VServer);
    }


    public void clearSynchroniser() {
        insightSynchroniser.clear();
    }

    @Test
    public void willPostandPutOrgsNotInPDToPD() {
        //this.setUpFakeInsightData();
        //List<PDOrganisation> PDOrgs = .getAllOrganisations().getBody().getData();
        List<Long> ids = new ArrayList<>();
        ids.add(20683L);
        ids.add(17977L);
        ids.add(53L);

        insightSynchroniser.getPDS().postOrganisation("Bentley Systems Germany GmbH", 3);

        List<Long> idsPushed = insightSynchroniser.importOrganisations(ids);
        assertEquals(idsPushed.size(),
                insightSynchroniser.organisations.postList.size() + insightSynchroniser.organisations.putList.size());
        //assertEquals(idsPushed.size(),3);

        assertTrue(insightSynchroniser.organisations.putList.size() == 1);
        assertTrue(insightSynchroniser.organisations.putList.get(0).getName().equals("Bentley Systems Germany GmbH"));

        List<PDOrganisation> pdOrgs = insightSynchroniser.getPDS().getAllOrganisations().getBody().getData();



        int matches = 0;
        for(VOrganisation v : insightSynchroniser.organisations.vOrganisations) {
            for(PDOrganisation p : pdOrgs) {
                if (v.getName().equals(p.getName())) {
                    matches++;
                }
            }
        }

        assertEquals(matches, idsPushed.size());

        List<Long> idsDeleted = insightSynchroniser.getPDS().deleteOrganisationList(idsPushed);
        assertEquals(idsDeleted.size(),
                insightSynchroniser.organisations.postList.size() + insightSynchroniser.organisations.putList.size());
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

        insightSynchroniser.organisations.vOrganisations = VOrgs;
    }

    //assumes none of th eorganisations are already on pipedrive
    @Test
    public void syncDoesNotMakeDuplicateOrganisations(){

        List<Long> ids = new ArrayList<>();
        ids.add(20683L);
        ids.add(17977L);

        List<Long> idsPosted = insightSynchroniser.importOrganisations(ids);
        assertEquals(idsPosted.size(), insightSynchroniser.organisations.postList.size());

        insightSynchroniser.clear();

        insightSynchroniser.importOrganisations(ids);
        assertTrue(insightSynchroniser.organisations.postList.isEmpty());
        assertTrue(insightSynchroniser.organisations.putList.isEmpty());

        List<Long> idsDeleted = insightSynchroniser.getPDS().deleteOrganisationList(idsPosted);
        assertEquals(idsDeleted.size(), idsPosted.size());
        assertEquals(idsDeleted, idsPosted);

        insightSynchroniser.clear();
    }

    @Test
    public void importsZUKOrganisations(){
        List<Long> orgs_pushed = insightSynchroniser.importToPipedrive();
        List<Long> orgs_imported = insightSynchroniser.getProjectsForZUK();
        assertEquals(orgs_pushed.size(),orgs_imported.size());

        insightSynchroniser.getPDS().deleteOrganisationList(orgs_pushed);

        insightSynchroniser.clear();
    }
}
