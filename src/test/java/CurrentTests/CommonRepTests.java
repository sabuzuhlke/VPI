package CurrentTests;

import VPI.Entities.Organisation;
import VPI.PDClasses.Organisations.PDOrganisationReceived;
import VPI.PDClasses.Organisations.PDOrganisationSend;
import VPI.PDClasses.PDOwner;
import org.junit.Test;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CommonRepTests {

    @Test
    public void canConvertOrgToPDSend() {
        Organisation org = new Organisation();
        org.setActive(true);

        org.setPipedriveId(1L);
        org.setFull_address("10, Downig street, London, UK");
        //org.setBuildingName("this building");
        org.setBusinessDomain("business");
        org.setCategory("arms trade");
        //org.setCity("London");
        //org.setCountry("UK");
        org.setCreated("1999-12-12 00:00:00");
        org.setModified("2222-12-12 00:00:00");
        org.setName("Organisation Co Ltd");
        org.setOwnedOnVertecBy("ZUK");
        //org.setPipedriveId(null);
        //org.setStreet("street");
        //org.setStreet_no("NO");
        org.setSupervisingEmail("wolfgang.emmerich@zuhlke.com");
        org.setVertecId(1L);
        org.setWebsite("www.com");
        //org.setZip("666");

        PDOrganisationSend pds = org.to_PDSend(55L);

        assertEquals(pds.getActive_flag(), org.getActive());

        assertTrue(pds.getAddress().equals(org.getFull_address()));
        assertTrue(pds.getCreationTime().equals(org.getCreated()));
        assertTrue(pds.getName().equals(org.getName()));
        assertTrue(pds.getOwnedBy().equals(org.getOwnedOnVertecBy()));

        assertEquals(pds.getId(), org.getPipedriveId());
        assertEquals(pds.getOwner_id().longValue(), 55);

        //TODO: category and business
    }

    @Test
    public void canConvertOrgReceivedtoCommon(){
        PDOrganisationReceived pdr = new PDOrganisationReceived();
        pdr.setAddress("10, Downig street, London, UK");
        pdr.setId(1L);
        pdr.setActive_flag(true);
        pdr.setName("name");
        pdr.setOwnedBy("ZUK");
        pdr.setCreationTime("1999-12-12 00:00:00");

        PDOwner pdo = new PDOwner();
        pdo.setId(1L);
        pdo.setEmail("me@only.com");
        pdr.setOwner_id(pdo);

        pdr.setV_id(2L);

        Organisation org = new Organisation(pdr);


        assertEquals(pdr.getId(), org.getPipedriveId());
        assertEquals(pdr.getV_id(), org.getVertecId());
        assertTrue(org.getActive());

        assertTrue(pdr.getOwner_id().getEmail().equals(org.getSupervisingEmail()));
        assertTrue(pdr.getOwnedBy().equals(org.getOwnedOnVertecBy()));
        assertTrue(pdr.getName().equals(org.getName()));
        assertTrue(pdr.getAddress().equals(org.getFull_address()));
        assertTrue(pdr.getCreationTime().equals(org.getCreated()));

        //TODO: category and business
    }


}


