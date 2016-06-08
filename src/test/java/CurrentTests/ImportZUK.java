package CurrentTests;

import VPI.Application;
import VPI.MyCredentials;
import VPI.PDClasses.PDService;
import VPI.VertecClasses.VertecProjects.ZUKProjects;
import VPI.VertecSynchroniser;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertTrue;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(Application.class)
public class ImportZUK {

    private VertecSynchroniser sync;

    @Before
    public void setUp() {
        this.sync = new VertecSynchroniser();
    }

//    @Test
//    public void canImportToPipedrive() {
//        //ids[0]=orgs posted, ids[1]=orgsPut, ids[2]=contsPostedToOrgs ids[3]=contsPosted ids[4]=contsPut
//        List<List<Long>> ids = sync.importToPipedrive();
//        List<Long> orgsToDel = new ArrayList<>();
//        orgsToDel.addAll(ids.get(0));
//        orgsToDel.addAll(ids.get(1));
//        List<Long> contsToDel = new ArrayList<>();
//        contsToDel.addAll(ids.get(2));
//        contsToDel.addAll(ids.get(3));
//        contsToDel.addAll(ids.get(4));
//
//        //assertTrue(sync.contactPutList.get(1).getName().equals("Aaron Rachamim") || sync.contactPutList.get(0).getName().equals("Aaron Rachamim") );
//        //assertTrue(sync.contactPutList.get(1).getName().equals("Aaron Bell") || sync.contactPutList.get(0).getName().equals("Aaron Bell") );
//        //assertTrue(sync.organisationPutList.get(0).getName().equals("Ab Initio"));
//        //List<Long> orgsDel = sync.getPDS().deleteOrganisationList(orgsToDel);
//        //List<Long> contsDel = sync.getPDS().deleteContactList(contsToDel);
//
////        System.out.println("orgs to del: " + orgsToDel.size());
////        System.out.println("orgs deleted: " + orgsDel.size());
////        System.out.println("conts to  del: " + contsToDel.size());
////        System.out.println("conts del: " + contsDel.size());
////        assertTrue(orgsDel.equals(orgsToDel));
////        assertTrue(contsDel.equals(contsToDel));
//
//        //sync.getPDS().clearPD(new ArrayList<>(), new ArrayList<>());
//
//        sync.clear();
//
//    }

    @Test
    public void canImportOrganisationsContactsAndProjectsAndActivities() {

        VertecSynchroniser VS = new VertecSynchroniser();
        MyCredentials creds = new MyCredentials();
        PDService PD = new PDService("https://api.pipedrive.com/v1/", creds.getApiKey());

        List<Long> dealsToKeep = new ArrayList<>();

        for (int i = 0; i < 75; i++) {
            dealsToKeep.add((long) i);
        }

        PD.clearPD(new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
        System.out.println("Importing orgs and contacts");
        VS.importOrganisationsAndContactsToPipedrive();
        System.out.println("Imported orgs n conts, importing deals");
        VS.importProjectsAndPhasesToPipedrive();
        System.out.println("Imported deals, importing activities");
        VS.importActivitiesToPipedrive();

    }

    @Test @Ignore("Just do")
    public void clearPD(){
        VertecSynchroniser VS = new VertecSynchroniser();
        MyCredentials creds = new MyCredentials();
        PDService PD = new PDService("https://api.pipedrive.com/v1/", creds.getApiKey());

        List<Long> dealsToKeep = new ArrayList<>();

        for (int i = 0; i < 75; i++) {
            dealsToKeep.add((long) i);
        }
        PD.clearPD(new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
    }
}