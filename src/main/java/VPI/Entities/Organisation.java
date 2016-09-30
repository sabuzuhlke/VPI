package VPI.Entities;

import VPI.Entities.util.Utilities;
import VPI.Keys.ProductionKeys;
import VPI.Keys.DevelopmentKeys;
import VPI.PDClasses.HierarchyClasses.PDRelationshipReceived;
import VPI.PDClasses.Organisations.PDOrganisationReceived;
import VPI.PDClasses.Organisations.PDOrganisationSend;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.org.apache.xpath.internal.operations.Bool;
import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * This Class is designed to bridge the representation gap between Pipedrive and Vertec Organisations.
 * Hence it contains all relevant fields, so that processing of organisations is made simpler
 * All orgasniations got from Vertec and Pipedrive are converted to this format, before being processed
 */
public class Organisation implements Comparable<Organisation> {

    private Long vertecId;
    private Long pipedriveId;
    private String ownedOnVertecBy;
    private Boolean active;

    //This field references the owner of the organisation
    private String supervisingEmail; //will have to be set outside conversion as converter needs access to teamid map

    private String name;
    private String website;
    private String category;
    private String businessDomain;

    private String fullAddress;
    private String buildingName;
    private String streetNo;
    private String street;
    private String city;
    private String country;
    private String zip;

    private String modified;
    private String created;

    private Long vParentOrganisation; //Vertec ID of parent Organisation

    public Organisation() {
    }

    /**
     * Constructor used to create organisation from PDOrganisationRecieved
     */
    public Organisation(PDOrganisationReceived pdr, BidiMap<Long, Long> orgIdMap) {
        this.pipedriveId = pdr.getId();
        this.vertecId = pdr.getV_id();
        this.active = true;

        this.supervisingEmail = pdr.getOwner_id().getEmail();
        this.ownedOnVertecBy = readPdOwnedOnVertecBy(pdr.getOwnedBy());
        this.name = pdr.getName();
        this.fullAddress = pdr.getAddress();
        this.created = pdr.getCreationTime();
        this.buildingName = pdr.getAddress_subpremise();
        this.streetNo = pdr.getAddress_street_number();
        this.street = pdr.getAddress_route();
        this.city = pdr.getAddress_locality();
        this.country = pdr.getAddress_country();
        this.zip = pdr.getAddress_postal_code();

        this.created = pdr.getCreationTime();

        this.website = pdr.getWebsite();
        this.category = pdr.getCategory();
        this.businessDomain = pdr.getBusinessDomain();
        this.modified = pdr.getUpdate_time(); //""2016-07-22 09:43:57""
        this.created = pdr.getCreationTime();

    }

    /**
     * @param relationship has to be got seperately from pipedrive
     */
    public Organisation(PDOrganisationReceived pdr, PDRelationshipReceived relationship, BidiMap<Long, Long> orgIdMap) {
        this.pipedriveId = pdr.getId();
        this.vertecId = pdr.getV_id();
        this.active = true;

        this.supervisingEmail = pdr.getOwner_id().getEmail();
        this.ownedOnVertecBy = readPdOwnedOnVertecBy(pdr.getOwnedBy());
        this.name = pdr.getName();
        this.fullAddress = pdr.getAddress();

        if (relationship != null) {

            this.vParentOrganisation = orgIdMap.getKey(relationship.getParent().getId());
        }

        this.website = pdr.getWebsite();
        this.category = pdr.getCategory();
        this.businessDomain = pdr.getBusinessDomain();
        this.modified = pdr.getUpdate_time(); //""2016-07-22 09:43:57""
        this.created = pdr.getCreationTime();
    }

    /**
     * owned on vertec by is kept track of as a number on pipedrive.
     * This number is different for each Pipedrive company account
     */
    private String readPdOwnedOnVertecBy(String ownedBy) {
        if (ownedBy == null) return "No Owner";
        else if (ownedBy.equals(DevelopmentKeys.OWNED_BY_SALES_TEAM )) return "Sales Team";
        else if (ownedBy.equals(DevelopmentKeys.OWNED_BY_NOT_ZUK)) return "Not ZUK";
        else return "unrecognised";
    }

    /**
     * Convert to object, that can be sent via PDService
     * @param ownerId has to be gotten from Map using supervisingEmail
     */
    public PDOrganisationSend toPDSend(Long ownerId) {
        PDOrganisationSend pds = new PDOrganisationSend();

        pds.setAddress(this.fullAddress);
        pds.setCreationTime(this.created);
        pds.setName(this.name);
        pds.setOwnedBy(this.ownedOnVertecBy);
        pds.setV_id(this.vertecId);

        pds.setActive_flag(this.active);
        pds.setId(this.pipedriveId);
        pds.setOwner_id(ownerId);
        pds.setWebsite(this.website);
        pds.setCategory(this.category);
        pds.setBusinessDomain(this.businessDomain);

        return pds;
    }

//    public static BidiMap<String, String> getPdTestEmailMap(){
//        BidiMap<String, String> emailMap = new DualHashBidiMap<>();
//        emailMap.put("ss@notreal.com", "sabine.strauss@zuhlke.com");
//        emailMap.put("bb@notreal.com", "brewster.barclay@zuhlke.com");
//        emailMap.put("we@notreal.com", "wolfgang.emmerich@zuhlke.com");
//        emailMap.put("mh@notreal.com", "mike.hogg@zuhlke.com");
//        emailMap.put("nm@notreal.com", "neil.moorcroft@zuhlke.com");
//        emailMap.put("kb@notreal.com", "keith.braithwaite@zuhlke.com");
//        emailMap.put("bt@notreal.com", "bryan.thal@zuhlke.com");
//        emailMap.put("jc@notreal.com", "justin.cowling@zuhlke.com");
//        emailMap.put("tc@notreal.com", "tim.cianchi@zuhlke.com");
//        emailMap.put("ac@notreal.com", "adam.cole@zuhlke.com");
//        return emailMap;
//    }

    /**
     * This function updates all fields of an organisation, to the values of the freshOrgansiation wherever necessary.
     * @param freshOrganisation is an organisation, that has been modified more recenty, then this
     */
    public void updateOrganisationWithFreshValues(Organisation freshOrganisation) {
        //Vertec Id will be the same for both
        //Pipedrive id is not affected

        boolean fullAddressModified = false;

        //below line should repoint orgs that have been merged on pipedrive

        this.pipedriveId = freshOrganisation.getPipedriveId() == null ? this.pipedriveId : freshOrganisation.getPipedriveId();
        //this.pipedriveId = freshOrganisation.getVertecId() == null ? this.vertecId : freshOrganisation.getVertecId();

        this.ownedOnVertecBy = (freshOrganisation.ownedOnVertecBy != null && !freshOrganisation.ownedOnVertecBy.equals("")) ?
                freshOrganisation.ownedOnVertecBy :
                this.ownedOnVertecBy;
        this.active = freshOrganisation.active != null ? freshOrganisation.active : this.active;
        this.supervisingEmail = (freshOrganisation.supervisingEmail != null && !freshOrganisation.supervisingEmail.equals("")) ?
                freshOrganisation.supervisingEmail :
                this.supervisingEmail;
        //for the test PDinstance
        if(emailMapForPDTestInstance().getKey(freshOrganisation.getSupervisingEmail()) != null){
            this.setSupervisingEmail(emailMapForPDTestInstance().getKey(freshOrganisation.getSupervisingEmail()));
        }
        this.name = (freshOrganisation.name != null && !freshOrganisation.name.equals("")) ?
                freshOrganisation.name :
                this.name;
        this.website = (freshOrganisation.website != null && !freshOrganisation.website.equals("")) ?
                freshOrganisation.website :
                this.website;
        this.category = (freshOrganisation.category != null && !freshOrganisation.category.equals("")) ?
                freshOrganisation.category :
                this.category;
        this.businessDomain = (freshOrganisation.businessDomain != null && !freshOrganisation.businessDomain.equals("")) ?
                freshOrganisation.businessDomain :
                this.businessDomain;
        if (freshOrganisation.fullAddress != null && !freshOrganisation.fullAddress.equals("")) {
            this.fullAddress = freshOrganisation.getFullAddress();
            fullAddressModified = true;
        } // else no change

        if (fullAddressModified) {

            this.buildingName = (Objects.equals(freshOrganisation.buildingName, buildingName)) ?
                    "" :
                    freshOrganisation.buildingName ;

            this.streetNo = (Objects.equals(freshOrganisation.streetNo, streetNo)) ?
                    "" :
                    freshOrganisation.streetNo;
            this.street = (Objects.equals(freshOrganisation.street, street)) ?
                    "" :
                    freshOrganisation.street;
            this.city = (Objects.equals(freshOrganisation.city, city)) ?
                    "" :
                    freshOrganisation.city;
            this.country = (Objects.equals(freshOrganisation.country, country)) ?
                    "" :
                    freshOrganisation.country;
            this.zip = (Objects.equals(freshOrganisation.zip, zip)) ?
                    "" :
                    freshOrganisation.zip;
        }
    }

    /**
     * Construct from organisation received from vertec
     * @param ownerEmail has to be got from a map using the ownerId of the vertec organisation
     */
    public Organisation(VPI.VertecClasses.VertecOrganisations.Organisation organisation, Long pipedriveId, String ownerEmail) {
        this.vertecId = organisation.getVertecId();
        this.pipedriveId = pipedriveId;
        this.active = organisation.getActive();
        this.website = organisation.getWebsite();
        this.category = organisation.getCategory();
        this.businessDomain = organisation.getBusinessDomain();
        this.name = organisation.getName();

        this.buildingName = organisation.getBuildingName();
        this.streetNo = organisation.getStreet_no();
        this.street = organisation.getStreet();
        this.city = organisation.getCity();
        this.country = organisation.getCountry();
        this.zip = organisation.getZip();
        this.fullAddress = organisation.getFullAddress();

        this.vParentOrganisation = organisation.getParentOrganisation();

        this.modified = Utilities.formatVertecDate(organisation.getModified());
        this.created = Utilities.formatVertecDate(organisation.getCreated());

        this.supervisingEmail = ownerEmail;
        this.ownedOnVertecBy = organisation.getOwnedOnVertecBy();

        this.modified = Utilities.formatVertecDate(organisation.getModified());
        this.created = Utilities.formatVertecDate(organisation.getCreated());
    }


    /**
     * Convert to format VertecService can deal with
     * @param ownerId has to be got from map using supervisingEmail
     */
    public VPI.VertecClasses.VertecOrganisations.Organisation toVertecRep(Long ownerId) {
        VPI.VertecClasses.VertecOrganisations.Organisation org = new VPI.VertecClasses.VertecOrganisations.Organisation();

        org.setVertecId(vertecId);
        org.setOwnedOnVertecBy(ownedOnVertecBy);
        org.setActive(active == null ? false : active);
        org.setOwnerId(ownerId);
        org.setName(name == null ? "" : name);
        org.setWebsite(website == null ? ""  : website);
        org.setCategory(category == null ? "" : category);
        org.setBusinessDomain(businessDomain == null ? "" : businessDomain);
        org.setBuildingName(buildingName == null ? "" : buildingName);
        org.setStreet(street == null ? "" : street);
        org.setStreet_no(streetNo == null ? "" : streetNo);
        org.setCity(city == null ? "" : city);
        org.setZip(zip == null ? "" : zip);
        org.setCountry(country == null ? "" : country);
        org.setFullAddress(fullAddress);



        org.setParentOrganisation(vParentOrganisation);
        org.setCreated(Utilities.formatToVertecDate(created));
        org.setModified(Utilities.formatToVertecDate(modified));

        return org;
    }

    /**
     * pretty printer
     * @return
     */
    public String toJSONString() {
        String retStr = null;
        ObjectMapper m = new ObjectMapper();
        try {

            retStr = m.writerWithDefaultPrettyPrinter().writeValueAsString(this);
        } catch (Exception e) {
            System.out.println("Could not convert XML Envelope to JSON: " + e.toString());
        }
        return retStr;
    }

    /**
     * Compares two organisations.
     * Addresses are tricky as Pipedrive used a supplied full address to query a google maps api, that splits
     * the address into parts. Sometimes this is incomplete, or split up in a wrong way. However the full address
     * field returned from pipedrive is always correct.
     * From Vertec we get the full address in two ways
     * one, by concatenating the sub-address fields. These come from organisations previously entered into vertec
     * two, when we post to vertec, we store the full address in the street address field, as splitting it up reliably is not possible
     *
     * The dilemma here is that on Vertec we want to keep all sub address fields as accurate as possible, however the ones
     * got from Pipedrive are not always complete. So if the full-Address dud not change, we keep the subAddress fields in Vertec
     */
    public boolean equals(Organisation org) {
        boolean retval = true;
        if(org == null) return false;

        if(vertecId == null || org.getVertecId() == null) return false;
        else retval = retval && vertecId == org.getVertecId().longValue();

        if(pipedriveId == null || org.getPipedriveId() == null) return false;
        else retval = retval && pipedriveId == org.getPipedriveId().longValue();

        if(active == null ^ org.getActive() == null) return false;
        else if(active != null && org.getActive() != null) retval = retval && active == org.getActive();

        if(name == null ^ org.getName() == null) return false;
        else if(name != null && org.getName() != null) retval = retval && name.equals(org.getName());

        if(website == null ^ org.getWebsite() == null) return false;
        else if(website != null && org.getWebsite() != null) retval = retval && website.equals(org.getWebsite());

        if(supervisingEmail == null ^ org.getSupervisingEmail() == null) return false;
        else if(supervisingEmail != null && org.getSupervisingEmail() != null) retval = retval && (supervisingEmail.equals(org.getSupervisingEmail())
                                                                                        || emailMapForPDTestInstance().get(supervisingEmail).equals(org.getSupervisingEmail()));


        if(fullAddress == null ^ org.getFullAddress() == null) return false;
        else if(this.getFullAddress() != null && org.getFullAddress() != null) retval = retval && this.getFullAddress().equals(org.getFullAddress());

        return retval;

//        return org != null && vertecId == org.getVertecId().longValue()
//                && pipedriveId == org.getPipedriveId().longValue()
//                && ownedOnVertecBy.equals(org.getOwnedOnVertecBy())
//                && active == org.getActive()
//                && ownedOnVertecBy.equals(org.getOwnedOnVertecBy())
//                && supervisingEmail.equals(
//                org.getSupervisingEmail())
//                && name.equals(org.getName())
//                && website.equals(org.getWebsite())
//                //&& category.equals(org.getCategory()) //Not yet implemented
//                //&& businessDomain.equals(org.getBusinessDomain()) //Not yet implemented
//                && fullAddress.equals(org.getFullAddress());
//               // && buildingName.equals(org.getBuildingName()) //Seperate adress fields are difficult to parse, but full address should always be the same
//                //&& streetNo.equals(org.getStreetNo())
//                //&& street.equals(org.getStreet())
//                //&& city.equals(org.getCity())
//                //&& country.equals(org.getCountry())
//                //&& zip.equals(org.getZip());
//        //creation and modification dates need not bee checked here

    }

    private BidiMap<String, String> emailMapForPDTestInstance(){
        BidiMap<String, String> map = new DualHashBidiMap<>();
        map.put("wolfgang.emmerich@zuhlke.com", "we@notreal.com"); //Wolfgang
        map.put("tim.cianchi@zuhlke.com", "tc@notreal.com"); //Tim
        map.put("neil.moorcroft@zuhlke.com", "nm@notreal.com"); //Neil
        map.put("mike.hogg@zuhlke.com", "mh@notreal.com"); //Mike
        map.put("justin.cowling@zuhlke.com", "jc@notreal.com"); //Justin
        map.put("brewster.barclay@zuhlke.com", "bb@notreal.com"); //Brewster
        map.put("keith.braithwaite@zuhlke.com", "kb@notreal.com"); //Keith
        map.put("peter.brown@zuhlke.com", "pb@notreal.com"); //Peter Brown
        map.put("steve.freeman@zuhlke.com", "sf@notreal.com"); //Steve Freeman
        map.put("john.seston@zuhlke.com", "js@notreal.com"); //John Seston
        map.put("sabine.streuss@zuhlke.com", "ss@notreal.com"); //Sabine
        map.put("sabine.strauss@zuhlke.com", "ss@notreal.com"); //Sabine
        map.put("ileana.meehan@zuhlke.com", "im@notreal.com"); //Ileana
        map.put("ina.hristova@zuhlke.com", "ih@notreal.com"); //Ina
        map.put("adam.cole@zuhlke.com", "ac@notreal.com"); //adam
        map.put("bryan.thal@zuhlke.com", "by@notreal.com"); //bryan
        map.put(null, "");

        return map;
    }

    @Override
    public int compareTo(Organisation org) {
        if (org == null) return -1;
        if (org.getVertecId() == null && vertecId != null) return -1;
        if (vertecId == null && org.getVertecId() != null)
            return 1; //this and above line will push orgs without v_ids to the end of the list
        if (vertecId == null && org.getVertecId() == null) return 0;
        if (this.vertecId > org.getVertecId()) return 1;
        if (this.vertecId == org.getVertecId()) return 0;
        else return -1;
    }

    public Long getVertecId() {
        return vertecId;
    }

    public void setVertecId(Long vertecId) {
        this.vertecId = vertecId;
    }

    public String getOwnedOnVertecBy() {
        return ownedOnVertecBy;
    }

    public void setOwnedOnVertecBy(String ownedOnVertecBy) {
        this.ownedOnVertecBy = ownedOnVertecBy;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getSupervisingEmail() {
        return supervisingEmail;
    }

    public void setSupervisingEmail(String supervisingEmail) {
        this.supervisingEmail = supervisingEmail;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getCategory() {
        return this.category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getBusinessDomain() {
        return businessDomain;
    }

    public void setBusinessDomain(String businessDomain) {
        this.businessDomain = businessDomain;
    }

    public String getBuildingName() {
        return buildingName;
    }

    public void setBuildingName(String buildingName) {
        this.buildingName = buildingName;
    }

    public String getStreetNo() {
        return streetNo;
    }

    public void setStreetNo(String streetNo) {
        this.streetNo = streetNo;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getModified() {
        return modified;
    }

    public void setModified(String modified) {
        this.modified = modified;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public Long getPipedriveId() {
        return pipedriveId;
    }

    public void setPipedriveId(Long pipedriveId) {
        this.pipedriveId = pipedriveId;
    }

    public String getFullAddress() {
        if(this.fullAddress != null && !this.fullAddress.isEmpty()) return fullAddress;
        else{
            return buildFullAddress(this,true);
        }

    }

    /**
     * This function builds the full address from its parts, for purposes mentioned above
     * @param org target organisation
     * @param setFullAdress FALSE if function should be read-only, TRUE if it should modify the fullAddress
     * @return
     */
    static public String buildFullAddress(Organisation org, boolean setFullAdress){
        String address = "";
        if(org.getBuildingName() != null && !org.getBuildingName().isEmpty()){
            address += org.getBuildingName() + ", ";
        }

        if (org.getStreetNo() != null && !org.getStreetNo().isEmpty()) {
            address += org.getStreetNo() + " ";
        }
        if (org.getStreet() != null && !org.getStreet().isEmpty()) {
            address += org.getStreet() + ", ";
        }

        if (org.getCity() != null && !org.getCity().isEmpty()) {
            address += org.getCity() + ", ";
        }
        if (org.getZip() != null && !org.getZip().isEmpty()) {
            address += org.getZip() + ", ";
        }
        if (org.getCountry() != null && !org.getCountry().isEmpty()) {
            address += org.getCountry();
        }
        if(setFullAdress) org.setFullAddress(address);
        return address;
    }

    public void setFullAddress(String fullAddress) {
        this.fullAddress = fullAddress;
    }

    public Long getvParentOrganisation() {
        return vParentOrganisation;
    }

    public void setvParentOrganisation(Long vParentOrganisation) {
        this.vParentOrganisation = vParentOrganisation;
    }


    @Override
    public String toString() {
        String retStr = null;
        ObjectMapper m = new ObjectMapper();
        try {

            retStr = m.writerWithDefaultPrettyPrinter().writeValueAsString(this);
        } catch (Exception e) {
            System.out.println("Could not convert Entities.Organisation to JSON: " + e.toString());
        }
        return retStr;
    }

    public static class OrganisationComparator implements Comparator<Organisation> {
        @Override
        public int compare(Organisation o1, Organisation o2) {
            return o1.compareTo(o2);
        }
    }

}