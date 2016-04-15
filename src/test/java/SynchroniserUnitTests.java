import VPI.*;
import VPI.PDClasses.PDOrganisation;
import VPI.VClasses.VOrganisation;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(Application.class)
public class SynchroniserUnitTests {

    private Synchroniser synchroniser;

    @Before
    public void setUp() throws Exception {
        String PDServer = "";
        String VServer  = "";
        this.synchroniser = new Synchroniser(PDServer, VServer);
    }

    @Test
    public void canClearSynchroniser() {
        assignJustVOrgList();

        PDOrganisation p = new PDOrganisation("Peter Griffin.co", "20 a Street, Bikini Bottom, 4343, Under the Sea");
        synchroniser.organisations.pdOrganisations.add(p);

        p = new PDOrganisation("Charlies Chocolate Factory", "5 End Street, Factory A, 0000, Neverland");
        synchroniser.organisations.pdOrganisations.add(p);

        synchroniser.compareOrganisations();

        clearSynchroniser();

        assertTrue(synchroniser.organisations.pdOrganisations.isEmpty());
        assertTrue(synchroniser.organisations.vOrganisations.isEmpty());

        assertTrue(synchroniser.organisations.postList.isEmpty());
        assertTrue(synchroniser.organisations.putList.isEmpty());

    }

    public int assignMatchingPDOrgList() {
        PDOrganisation p = new PDOrganisation();
        p.setName("Peter Griffin.co");
        p.setAddress("13 Family Street, Quahog, 6727, Murica!");
        synchroniser.organisations.pdOrganisations.add(p);

        p = new PDOrganisation();
        p.setName("Peter Quagmire.co");
        p.setAddress("17 Family Street, Quahog, 6722, Murica!");
        synchroniser.organisations.pdOrganisations.add(p);

        return synchroniser.organisations.pdOrganisations.size();
    }

    public int assignJustVOrgList() {
        VOrganisation v = new VOrganisation();
        v.setName("Peter Griffin.co");
        v.setCity("Quahog");
        v.setCountry("Murica!");
        v.setStreet("13 Family Street");
        v.setZip("6727");
        synchroniser.organisations.vOrganisations.add(v);

        v = new VOrganisation();
        v.setName("Peter Quagmire.co");
        v.setCity("Quahog");
        v.setCountry("Murica!");
        v.setStreet("17 Family Street");
        v.setZip("6722");
        synchroniser.organisations.vOrganisations.add(v);

        return synchroniser.organisations.vOrganisations.size();

    }

    public void clearSynchroniser() {
        synchroniser.clear();
    }
//Unit tests
    @Test
    public void emptyOrgListsReturnEmptyPostAndPutLists() {
        assertTrue(synchroniser.organisations.pdOrganisations.isEmpty());
        assertTrue(synchroniser.organisations.vOrganisations.isEmpty());

        synchroniser.compareOrganisations();

        assertTrue(synchroniser.organisations.postList.isEmpty());
        assertTrue(synchroniser.organisations.putList.isEmpty());
    }

    @Test
    public void emptyPDOrgListReturnsAllItemsOfVOrgListToPostList() {

        int numInList = assignJustVOrgList();
        assertTrue(!synchroniser.organisations.vOrganisations.isEmpty());
        assertTrue(synchroniser.organisations.pdOrganisations.isEmpty());

        synchroniser.compareOrganisations();

        assertTrue(synchroniser.organisations.putList.isEmpty());
        assertTrue(!synchroniser.organisations.postList.isEmpty());
        assertEquals(synchroniser.organisations.postList.size(), numInList);

        //some check on matching internal values

        clearSynchroniser();
    }

    @Test
    public void emptyVlistNoPostNoPut(){
        assignMatchingPDOrgList();
        assertTrue(synchroniser.organisations.vOrganisations.isEmpty());
        assertTrue(!synchroniser.organisations.pdOrganisations.isEmpty());

        synchroniser.compareOrganisations();

        assertTrue(synchroniser.organisations.putList.isEmpty());
        assertTrue(synchroniser.organisations.postList.isEmpty());

        clearSynchroniser();
    }

    @Test
    public void givenCaBa_CaBa(){
        assignJustVOrgList();
        assignMatchingPDOrgList();

        assertTrue(!synchroniser.organisations.vOrganisations.isEmpty());
        assertTrue(!synchroniser.organisations.pdOrganisations.isEmpty());

        synchroniser.compareOrganisations();

        assertTrue(synchroniser.organisations.putList.isEmpty());
        assertTrue(synchroniser.organisations.postList.isEmpty());

        clearSynchroniser();
    }

    @Test
    public void given_CaBa_AaDa_POSTCaBa_emptyPUT(){
        assignJustVOrgList();

        PDOrganisation p = new PDOrganisation("The Crusty Crab", "13 a Street, Bikini Bottom, 4343, Under the Sea");
        synchroniser.organisations.pdOrganisations.add(p);

        p = new PDOrganisation("Charlies Chocolate Factory", "5 End Street, Factory A, 0000, Neverland");
        synchroniser.organisations.pdOrganisations.add(p);

        assertTrue(!synchroniser.organisations.vOrganisations.isEmpty());
        assertTrue(synchroniser.organisations.pdOrganisations.size() == 2);

        synchroniser.compareOrganisations();

        assertTrue(synchroniser.organisations.postList.size() == 2);
        assertTrue(synchroniser.organisations.postList.get(0).getName().equals("Peter Griffin.co"));
        assertTrue(synchroniser.organisations.postList.get(1).getName().equals("Peter Quagmire.co"));

        assertTrue(synchroniser.organisations.putList.isEmpty());

        clearSynchroniser();
    }

    @Test
    public void given_CaBa_CbDa_CaInPUT_BaInPost() {
        assignJustVOrgList();

        VOrganisation v = new VOrganisation();
        v.setName("Joe's wheelchair co");
        v.setCity("Quahog");
        v.setCountry("Murica!");
        v.setStreet("20 Family Street");
        v.setZip("6727");
        synchroniser.organisations.vOrganisations.add(v);

        v = new VOrganisation();
        v.setName("Clevelands Throwback co");
        v.setCity("Quahog");
        v.setCountry("Murica!");
        v.setStreet("Opposite 15 Family Street");
        v.setZip("6722");
        synchroniser.organisations.vOrganisations.add(v);

        PDOrganisation p = new PDOrganisation("Peter Quagmire.co", "20 a Street, Bikini Bottom, 4343, Under the Sea");
        synchroniser.organisations.pdOrganisations.add(p);

        p = new PDOrganisation("Clevelands Throwback co", "5 End Street, Factory A, 0000, Neverland");
        synchroniser.organisations.pdOrganisations.add(p);


        assertTrue(!synchroniser.organisations.vOrganisations.isEmpty());
        assertTrue(synchroniser.organisations.pdOrganisations.size() == 2);

        synchroniser.compareOrganisations();

        assertTrue(synchroniser.organisations.postList.size() == 2);
        assertTrue(synchroniser.organisations.postList.get(0).getName().equals("Peter Griffin.co"));
        assertTrue(synchroniser.organisations.postList.get(0).getAddress().equals("13 Family Street, Quahog, 6727, Murica!"));
        assertTrue(synchroniser.organisations.postList.get(1).getName().equals("Joe's wheelchair co"));
        assertTrue(synchroniser.organisations.postList.get(1).getAddress().equals("20 Family Street, Quahog, 6727, Murica!"));

        assertTrue(synchroniser.organisations.putList.size() == 2);
        assertTrue(synchroniser.organisations.putList.get(0).getName().equals("Peter Quagmire.co"));
        assertTrue(synchroniser.organisations.putList.get(0).getAddress().equals("17 Family Street, Quahog, 6722, Murica!"));
        assertTrue(synchroniser.organisations.putList.get(1).getName().equals("Clevelands Throwback co"));
        assertTrue(synchroniser.organisations.putList.get(1).getAddress().equals("Opposite 15 Family Street, Quahog, 6722, Murica!"));

        clearSynchroniser();
    }

}
