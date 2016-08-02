package CurrentTests;

import VPI.Entities.Activity;
import VPI.Entities.Contact;
import VPI.Entities.util.ContactDetail;
import VPI.MergerClasses.ContactMerger;
import VPI.MergerClasses.OrganisationMerger;
import VPI.MyCredentials;
import VPI.PDClasses.PDService;
import VPI.VertecClasses.VertecActivities.ActivitiesForOrganisation;
import VPI.VertecClasses.VertecService;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MergerTests {
    private OrganisationMerger organisationMerger;
    private ContactMerger contactMerger;
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
        contactMerger = new ContactMerger(pipedrive, vertec);
    }

    @Test
    public void canClassifyContactMatches(){

    }

    @Test
    public void canFindMatchingEmailPairs() {
        //Given a list of contacts recieved from vertec (default email only),
        //and a list of contacts received from pipedrive (all emails),
        //identify pairs of vertec Id where the email from vertec matches the email from pipedrive

        Contact v1 = new Contact();
        v1.setVertecId(1L);
        v1.setFirstName("f");
        v1.setSurname("s");
        v1.setEmails(Collections.singletonList(new ContactDetail("e", true)));

        Contact p1 = new Contact();
        p1.setVertecId(10L);
        p1.setFirstName("f");
        p1.setSurname("s");
        p1.setEmails(Collections.singletonList(new ContactDetail("e", true)));

        Contact p2 = new Contact();
        p2.setVertecId(100L);
        p2.setFirstName("f");
        p2.setSurname("s");
        p2.setEmails(Collections.singletonList(new ContactDetail("e", true)));

        Contact v2 = new Contact();
        v2.setVertecId(2L);
        v2.setFirstName("f2");
        v2.setSurname("s2");
        v2.setEmails(Collections.singletonList(new ContactDetail("e2", true)));

        Contact v3 = new Contact();
        v3.setVertecId(3L);
        v3.setFirstName("f3");
        v3.setSurname("s3");
        v3.setEmails(Collections.singletonList(new ContactDetail("e2", true)));

        Contact p3 = new Contact();
        p3.setVertecId(20L);
        p3.setFirstName("f2");
        p3.setSurname("s2");
        p3.setEmails(Collections.singletonList(new ContactDetail("e2", true)));

        Contact v4 = new Contact();
        v4.setVertecId(4L);
        v4.setFirstName("f4");
        v4.setSurname("s4");
        v4.setEmails(Collections.singletonList(new ContactDetail("e34565", true)));

        //map should contain mapping of 1L -> 100L as ttwo contacts above match

        List<Contact> vertecContacts = Arrays.asList(v1, v2, v3, v4);
        List<Contact> pipedriveContacts = Arrays.asList(p1, p2, p3);

        HashMap<Long, Long> matches = contactMerger.findVcontactsMergedOnPD(vertecContacts, pipedriveContacts);

        assertTrue(! matches.isEmpty());
        assertEquals("Could not find match when one should be found", 20L, matches.get(2L).longValue());
        assertEquals("Could not find match when one should be found", 20L, matches.get(3L).longValue());

        assertEquals("Match found for contact with no matches", 4L, contactMerger.noMergesFound.get(0).longValue());

        assertEquals("Should have found multiple matches, did not", 1, contactMerger.uncertainMerges.get(0).get(0).longValue());
        assertEquals("Should have found multiple matches, did not", 10, contactMerger.uncertainMerges.get(0).get(1).longValue());

        assertEquals("Should have found multiple matches, did not", 1, contactMerger.uncertainMerges.get(1).get(0).longValue());
        assertEquals("Should have found multiple matches, did not", 100, contactMerger.uncertainMerges.get(1).get(1).longValue());

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
    public void testDoOrganisationMerge() throws IOException {
        organisationMerger.doMerge();
    }

    @Test
    public void testDoContactMerge() throws IOException {
        contactMerger.doMerge();
    }

}
