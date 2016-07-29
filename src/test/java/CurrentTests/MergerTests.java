package CurrentTests;

import VPI.Entities.Activity;
import VPI.MergerClasses.OrganisationMerger;
import VPI.MyCredentials;
import VPI.PDClasses.PDService;
import VPI.VertecClasses.VertecActivities.ActivitiesForOrganisation;
import VPI.VertecClasses.VertecService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.mock;

public class MergerTests {
    private OrganisationMerger organisationMerger;
    private PDService pipedrive;
    private VertecService vertec;


    @Before
    public void setup(){
        MockitoAnnotations.initMocks(this);
//        pipedrive = mock(PDService.class);
//        vertec = mock(VertecService.class);
        MyCredentials creds = new MyCredentials();
        pipedrive = new PDService("https://api.pipedrive.com/v1/", creds.getApiKey());
        vertec = new VertecService("localhost:9999");
        organisationMerger = new OrganisationMerger(pipedrive, vertec);
    }

    @Test
    public void canFindMergedOrganisationPair(){
        Activity pipedriveActivity = new Activity();
        List<Activity> pdActivities = new ArrayList<>();

        pipedriveActivity.setVertecId(1L);
        pipedriveActivity.setVertecOrganisationLink(2L);
        pdActivities.add(pipedriveActivity);

        pipedriveActivity = new Activity();
        pipedriveActivity.setVertecId(2L);
        pipedriveActivity.setVertecOrganisationLink(4L);
        pdActivities.add(pipedriveActivity);

        pipedriveActivity = new Activity();
        pipedriveActivity.setVertecId(3L);
        pipedriveActivity.setVertecOrganisationLink(2L);
        pdActivities.add(pipedriveActivity);

        VPI.VertecClasses.VertecActivities.Activity vertecActivity = new VPI.VertecClasses.VertecActivities.Activity();
        List<VPI.VertecClasses.VertecActivities.Activity> vActivities = new ArrayList<>();

        vertecActivity.setVertecId(1L);
        vActivities.add(vertecActivity);

        ActivitiesForOrganisation afo = new ActivitiesForOrganisation();
        afo.setOrganisationId(5L);
        afo.setActivitiesForOrganisation(vActivities);
        afo.setName("Certain Match Org & co");

        List<Long> mergedOrgs = organisationMerger.findMergedOrganisationPair(afo,pdActivities,1);

        assertEquals(2, mergedOrgs.size());
        assertEquals(afo.getOrganisationId(),mergedOrgs.get(0));
        assertEquals(pdActivities.get(0).getVertecOrganisationLink(),mergedOrgs.get(1));

        //We keep the previous afo, for simplicity's sake
        vertecActivity = new VPI.VertecClasses.VertecActivities.Activity();
        vertecActivity.setVertecId(2L);

        afo.getActivitiesForOrganisation().add(vertecActivity);
        afo.setName("50-50 org");
        afo.setOrganisationId(6L);

        mergedOrgs = organisationMerger.findMergedOrganisationPair(afo,pdActivities,2);

        assertTrue(mergedOrgs.isEmpty());

        afo = new ActivitiesForOrganisation();
        vActivities = new ArrayList<>();
        vertecActivity = new VPI.VertecClasses.VertecActivities.Activity();
        vertecActivity.setVertecId(3535245L);
        vActivities.add(vertecActivity);
        afo.setOrganisationId(7L);
        afo.setName("No MatchO");
        afo.setActivitiesForOrganisation(vActivities);

        mergedOrgs = organisationMerger.findMergedOrganisationPair(afo,pdActivities,3);
        assertTrue(mergedOrgs.isEmpty());

    }

    @Test
    public void testDoMerge() throws IOException {
        organisationMerger.doMerge();
    }

}
