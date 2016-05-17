package VPI.VertecClasses.VertecOrganisations;

import VPI.VertecClasses.VertecOrganisations.JSONContact;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sabu on 27/04/2016.
 */
public class  JSONOrganisation {

    @JsonProperty("name")
    private String name;

    @JsonProperty("streetAddress")
    private String streetAddress; //possibly change to separate address fields

    @JsonProperty("additonalAdress")
    private String additionalAdress;

    @JsonProperty("zip")
    private String zip;

    @JsonProperty("city")
    private String city;

    @JsonProperty("country")
    private String country;

    @JsonProperty("owner")
    private String owner;

    @JsonProperty("objid")
    private Long objid; //unsure if needed

    @JsonProperty("modified")
    private String modified;

    @JsonProperty("contacts")
    private List<JSONContact> contacts;

    @JsonProperty("creationTime")
    private String creationTime;

    @JsonProperty("parentOrganisationId")
    private Long parentOrganisationId;

    @JsonProperty("childOrganisationList")
    private List<Long> childOrganisationList;

    public Long getParentOrganisationId() {
        return parentOrganisationId;
    }

    public void setParentOrganisationId(Long parentOrganisationId) {
        this.parentOrganisationId = parentOrganisationId;
    }

    public List<Long> getChildOrganisationList() {
        return childOrganisationList;
    }

    public void setChildOrganisationList(List<Long> childOrganisationList) {
        this.childOrganisationList = childOrganisationList;
    }

    public String getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(String creationTime) {
        this.creationTime = creationTime;
    }

    public JSONOrganisation() {
        this.contacts = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public Long getObjid() {
        return objid;
    }

    public void setObjid(Long objid) {
        this.objid = objid;
    }

    public String getModified() {
        return modified;
    }

    public void setModified(String modified) {
        this.modified = modified;
    }

    public List<JSONContact> getContacts() {
        return contacts;
    }

    public void setContacts(List<JSONContact> contacts) {
        this.contacts = contacts;
    }

    public String getStreetAddress() {
        return streetAddress;
    }

    public void setStreetAddress(String streetAddress) {
        this.streetAddress = streetAddress;
    }

    public String getAdditionalAdress() {
        return additionalAdress;
    }

    public void setAdditionalAdress(String additionalAdress) {
        this.additionalAdress = additionalAdress;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
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

    public String getFormattedAddress(){
        String address = "";
        if( ! this.getAdditionalAdress().isEmpty()){
            address += this.getAdditionalAdress() + ", ";
        }
        address += this.getStreetAddress() + ", ";
        address += this.getCity() + ", ";
        address += this.getZip() + ", ";
        address += this.getCountry();
        return address;
    }
}