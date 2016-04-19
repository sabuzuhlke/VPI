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
//TODO: edit tests to account for new method of import
//    @Test
//    public void willPostandPutOrgsNotInPDToPD() {
//        //this.setUpFakeInsightData();
//        //List<PDOrganisation> PDOrgs = .getAllOrganisations().getBody().getData();
//
//        synchroniser.getPDS().postOrganisation("Bentley Systems Germany GmbH", 3);
//
//        List<Long> idsPushed = synchroniser.importOrganisations();
//        assertEquals(idsPushed.size(),
//                synchroniser.organisations.postList.size() + synchroniser.organisations.putList.size());
//
//        assertTrue(synchroniser.organisations.putList.size() == 1);
//        assertTrue(synchroniser.organisations.putList.get(0).getName().equals("Bentley Systems Germany GmbH"));
//
//        List<PDOrganisation> pdOrgs = synchroniser.getPDS().getAllOrganisations().getBody().getData();
//
//
//
//        int matches = 0;
//        for(VOrganisation v : synchroniser.organisations.vOrganisations) {
//            for(PDOrganisation p : pdOrgs) {
//                if (v.getName().equals(p.getName())) {
//                    matches++;
//                }
//            }
//        }
//
//        assertEquals(matches, idsPushed.size());
//
//        List<Long> idsDeleted = synchroniser.getPDS().deleteOrganisationList(idsPushed);
//        assertEquals(idsDeleted.size(),
//                synchroniser.organisations.postList.size() + synchroniser.organisations.putList.size());
//        assertEquals(idsDeleted, idsPushed);
//
//        clearSynchroniser();
//    }

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

    //assumes none of th eorganisations are already on pipedrive
//    @Test
//    public void syncDoesNotMakeDuplicateOrganisations(){
//
//        List<Long> idsPosted = synchroniser.importOrganisations();
//        assertEquals(idsPosted.size(),synchroniser.organisations.postList.size());
//
//        synchroniser.clear();
//
//        synchroniser.importOrganisations();
//        assertTrue(synchroniser.organisations.postList.isEmpty());
//        assertTrue(synchroniser.organisations.putList.isEmpty());
//
//        List<Long> idsDeleted = synchroniser.getPDS().deleteOrganisationList(idsPosted);
//        assertEquals(idsDeleted.size(), idsPosted.size());
//        assertEquals(idsDeleted, idsPosted);
//
//        synchroniser.clear();
//    }
}
