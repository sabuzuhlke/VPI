package VPI.Entities;

import VPI.PDClasses.Organisations.PDOrganisationReceived;
import VPI.PDClasses.Organisations.PDOrganisationSend;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Organisation {

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

    public Organisation() {
    }

    public Organisation(PDOrganisationReceived pdr){
        this.pipedriveId = pdr.getId();
        this.vertecId = pdr.getV_id();
        this.active = true;

        this.supervisingEmail = pdr.getOwner_id().getEmail();
        this.ownedOnVertecBy = pdr.getOwnedBy();
        this.name = pdr.getName();
        this.full_address = pdr.getAddress();
        this.created = pdr.getCreationTime();
        //TODO modified Date,

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

    public Long getVertecId() {
        return vertecId;
    }

    public void setVertecId(Long vertecId) {
        this.vertecId = vertecId;
    }

    public String getOwnedOnVertecBy() {
        return ownedOnVertecBy;
    }

   public PDOrganisationSend to_PDSend(Long ownerId){
       PDOrganisationSend pds = new PDOrganisationSend();

       pds.setAddress(this.full_address);
       pds.setCreationTime(this.created);
       pds.setName(this.name);
       pds.setOwnedBy(this.ownedOnVertecBy);

       pds.setActive_flag(this.active);
       pds.setId(this.pipedriveId);
       pds.setOwner_id(ownerId);

       return pds;
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


}