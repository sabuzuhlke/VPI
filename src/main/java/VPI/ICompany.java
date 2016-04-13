package VPI;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by sabu on 12/04/2016.
 */
@JsonIgnoreProperties(ignoreUnknown = true)

public class ICompany {

    private String longitude;
    private String latitude;
    private String Name;
    private String role;
    private String roleText;
    //private Object[] Services;
    private String industry;
    private Long id;

    public ICompany() {
    }

    public ICompany(String name) {
        Name = name;
    }

    public String getIndustry() {
        return industry;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
    }

    public String getName() {
        return Name;
    }

    @JsonProperty("Name")
    public void setName(String name) {
        this.Name = name;
    }


    public String getLongitude() {
        return longitude;
    }

    @JsonProperty("Longitude")
    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    @JsonProperty("Latitude")
    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getRole() {
        return role;
    }

    @JsonProperty("Role")
    public void setRole(String role) {
        this.role = role;
    }

    public String getRoleText() {
        return roleText;
    }

    @JsonProperty("RoleText")
    public void setRoleText(String roleText) {
        this.roleText = roleText;
    }

   /* public Object[] getServices() {
        return Services;
    }

    public void setServices(Object[] services) {
        Services = services;
    }*/

    public Long getId() {
        return id;
    }

    @JsonProperty("Id")
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String toString(){
        return "ICOMPANY: " + Name + " " + id;
    }
}
