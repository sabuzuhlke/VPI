package VPI.VClasses;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)

public class VOrganisation {

    private String name;
    private String street;
    private String city;
    private String zip;
    private String country;
    private String role;
    private String roleText;
    private Long id;

    public VOrganisation() {
    }

    public String getFormattedAddress(){
        return street + ", " + city + ", " + zip + ", " + country;
    }

    public VOrganisation(String name) {
        this.name = name;
    }

    @JsonProperty("Name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("Role")
    public void setRole(String role) {
        this.role = role;
    }

    @JsonProperty("RoleText")
    public void setRoleText(String roleText) {
        this.roleText = roleText;
    }

    @JsonProperty("Id")
    public void setId(Long id) {
        this.id = id;
    }

    @JsonProperty("Street")
    public void setStreet(String street) {
        this.street = street;
    }

    @JsonProperty("City")
    public void setCity(String city) {
        this.city = city;
    }

    @JsonProperty("Zip")
    public void setZip(String zip) {
        this.zip = zip;
    }

    @JsonProperty("Country")
    public void setCountry(String country) {
        this.country = country;
    }

    public String getName() {
        return name;
    }

    public String getStreet() {
        return street;
    }

    public String getCity() {
        return city;
    }

    public String getZip() {
        return zip;
    }

    public String getCountry() {
        return country;
    }

    public String getRole() {
        return role;
    }

    public String getRoleText() {
        return roleText;
    }

    public Long getId() {
        return id;
    }

    @Override
    public String toString(){
        return "ICOMPANY: " + name + " " + id;
    }
}
