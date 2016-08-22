package VPI.Entities;

import VPI.Entities.util.Utilities;
import VPI.PDClasses.Organisations.PDOrganisationReceived;
import VPI.PDClasses.Organisations.PDOrganisationSend;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Comparator;

public class Organisation implements Comparable<Organisation> {

    private Long vertecId;
    private Long pipedriveId;
    private String ownedOnVertecBy;
    private Boolean active;

    private String supervisingEmail; //will have to be set outside conversion as converter needs access to teamid map

    private String name;
    private String website;
    private String category;
    private String businessDomain;

    private String full_address;
    private String buildingName;
    private String street_no;
    private String street;
    private String city;
    private String country;
    private String zip;

    private String modified;
    private String created;

    private Long vParentOrganisation;

    public Organisation() {
    }

    /**
     *
     * @param relationship has to be got seperately from pipedrive
     */
    public Organisation(PDOrganisationReceived pdr/*, PDRelationshipReceived relationship, BidiMap<Long,Long> orgIdMap*/){
        this.pipedriveId = pdr.getId();
        this.vertecId = pdr.getV_id();
        this.active = true;

        this.supervisingEmail = pdr.getOwner_id().getEmail();
        this.ownedOnVertecBy = pdr.getOwnedBy();
        this.name = pdr.getName();
        this.full_address = pdr.getAddress();
        this.created = pdr.getCreationTime();

//        if(relationship != null){
//
//            this.vParentOrganisation = orgIdMap.getKey(relationship.getParent().getId());
//        }
        this.website = pdr.getWebsite();
        this.category = pdr.getCategory();
        this.businessDomain = pdr.getBusinessDomain();
        //TODO modified Date,
        this.modified = pdr.getUpdate_time(); //""2016-07-22 09:43:57""
        this.created = pdr.getCreationTime();

    }

    /**
     *
     * @param ownerId has to be gotten from Map using supervisingEmail
     */
    public PDOrganisationSend toPDSend(Long ownerId){
        PDOrganisationSend pds = new PDOrganisationSend();

        pds.setAddress(this.full_address);
        pds.setCreationTime(this.created);
        pds.setName(this.name);
        pds.setOwnedBy(this.ownedOnVertecBy);

        pds.setActive_flag(this.active);
        pds.setId(this.pipedriveId);
        pds.setOwner_id(ownerId);
        pds.setWebsite(this.website);
        pds.setCategory(this.category);
        pds.setBusinessDomain(this.businessDomain);

        return pds;
    }

    /**
     *
     * @param ownerEmail has to be got from a map using the ownerId of the vertec organisation
     */
    public Organisation(VPI.VertecClasses.VertecOrganisations.Organisation organisation, Long pipedriveId, String ownerEmail){
        this.vertecId = organisation.getVertecId();
        this.pipedriveId = pipedriveId;
        this.active = organisation.getActive();
        this.website = organisation.getWebsite();
        this.category = organisation.getCategory();
        this.businessDomain = organisation.getBusinessDomain();

        this.buildingName = organisation.getBuildingName();
        this.street_no = organisation.getStreet_no();
        this.street = organisation.getStreet();
        this.city = organisation.getCity();
        this.country = organisation.getCountry();
        this.zip = organisation.getZip();
        this.full_address = Utilities.formatVertecAddress(organisation);

        this.vParentOrganisation = organisation.getParentOrganisation();

        this.modified = Utilities.formatVertecDate(organisation.getModified());
        this.created = Utilities.formatVertecDate(organisation.getCreated());

        this.supervisingEmail = ownerEmail;
        this.ownedOnVertecBy = organisation.getOwnedOnVertecBy();

        this.modified = Utilities.formatVertecDate(organisation.getModified());
        this.created = Utilities.formatVertecDate(organisation.getCreated());
    }


    /**
     *
     * @param ownerId has to be got from map using supervisingEmail
     */
    public VPI.VertecClasses.VertecOrganisations.Organisation toVertecRep(Long ownerId){
        VPI.VertecClasses.VertecOrganisations.Organisation org = new VPI.VertecClasses.VertecOrganisations.Organisation();

        org.setVertecId(vertecId);
        org.setOwnedOnVertecBy(ownedOnVertecBy);
        org.setActive(active);
        org.setOwnerId(ownerId);
        org.setName(name);
        org.setWebsite(website);
        org.setCategory(category);
        org.setBusinessDomain(businessDomain);
        org.setBuildingName(buildingName);
        org.setStreet(street);
        org.setStreet_no(street_no);
        org.setCity(city);
        org.setCountry(country);
        org.setZip(zip);

        org.setParentOrganisation(vParentOrganisation);
        org.setCreated(Utilities.formatToVertecDate(created));
        org.setModified(Utilities.formatToVertecDate(modified));

        return org;
    }

    public String toJSONString(){
        String retStr = null;
        ObjectMapper m = new ObjectMapper();
        try{

            retStr = m.writerWithDefaultPrettyPrinter().writeValueAsString(this);
        }
        catch(Exception e){
            System.out.println("Could not convert XML Envelope to JSON: " + e.toString());
        }
        return retStr;
    }


    public boolean equals(Organisation org) {
        return org != null && vertecId == org.getVertecId().longValue()
                && pipedriveId == org.getPipedriveId().longValue()
                && ownedOnVertecBy.equals(org.getOwnedOnVertecBy())
                && active == org.getActive()
                && vParentOrganisation == org.getvParentOrganisation().longValue()
                && ownedOnVertecBy.equals(org.getOwnedOnVertecBy())
                && supervisingEmail.equals(org.getSupervisingEmail())
                && supervisingEmail.equals(org.getSupervisingEmail())
                && name.equals(org.getName())
                && website.equals(org.getWebsite())
                && category.equals(org.getCategory())
                && businessDomain.equals(org.getBusinessDomain())
                && full_address.equals(org.getFull_address())
                && buildingName.equals(org.getBuildingName())
                && street_no.equals(org.getStreet_no())
                && street.equals(org.getStreet())
                && city.equals(org.getCity())
                && country.equals(org.getCountry())
                && zip.equals(org.getZip());
        //creation and modification dates need not bee checked here

    }
    @Override
    public int compareTo(Organisation org) {
        if(org == null) return -1;
        if(org.getVertecId() == null && vertecId != null) return -1;
        if(vertecId == null && org.getVertecId() != null) return 1; //this and above line will push orgs without v_ids to the end of the list
        if(vertecId == null && org.getVertecId() == null) return 0;
        if(this.vertecId > org.getVertecId()) return 1;
        if(this.vertecId == org.getVertecId()) return 0;
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

    public String getStreet_no() {
        return street_no;
    }

    public void setStreet_no(String street_no) {
        this.street_no = street_no;
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

    public String getFull_address() {
        return full_address;
    }

    public void setFull_address(String full_address) {
        this.full_address = full_address;
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