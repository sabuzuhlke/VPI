package VPI.VertecClasses.VertecOrganisations;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * THIS CLASS IS RECEIVED FROM VERTEC
 * Dates are represented in the vertec format /Date separated by a 'T' from day-time
 */
public class Organisation {

    private Long vertecId;
    private String ownedOnVertecBy;
    private Boolean active;

    private Long ownerId;

    private String name;
    private String website;
    private String category;
    private String businessDomain;
    private String buildingName;
    private String street_no;
    private String street;
    private String city;
    private String country;
    private String zip;
    private String fullAddress;

    private Long parentOrganisation;
    private Long modifier;

    private String modified;
    private String created;


    public Organisation(){
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

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
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
        return category;
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

    public Long getParentOrganisation() {
        return parentOrganisation;
    }

    public void setParentOrganisation(Long parentOrganisation) {
        this.parentOrganisation = parentOrganisation;
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

    public Long getModifier() {
        return modifier;
    }

    public void setModifier(Long modifier) {
        this.modifier = modifier;
    }

    public String getFullAddress() {
        if(this.fullAddress != null && !this.fullAddress.isEmpty()) return fullAddress;
        else{
            return buildFullAddress(this,true);
        }

    }

    static public String buildFullAddress(Organisation org, boolean setFullAdress){
        String address = "";
        if(org.getBuildingName() != null && !org.getBuildingName().isEmpty()){
            address += org.getBuildingName() + ", ";
        }

        if (org.getStreet_no() != null && !org.getStreet_no().isEmpty()) {
            address += org.getStreet_no() + " ";
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

    @Override
    public String toString(){
        String retStr = null;
        ObjectMapper m = new ObjectMapper();
        try{

            retStr = m.writerWithDefaultPrettyPrinter().writeValueAsString(this);
        }
        catch(Exception e){
            System.out.println("Could not convert to JSON: " + e.toString());
        }
        return retStr;
    }
}
