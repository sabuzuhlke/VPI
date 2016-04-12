package VPI;

/**
 * Created by sabu on 12/04/2016.
 */
public class ICompany {

    private String Name;
    private String Longitude;
    private String Latitude;
    private String Role;
    private String RoleText;
    private Object[] Services;
    private String Industry;
    private Long Id;

    public ICompany() {
    }

    public String getIndustry() {
        return Industry;
    }

    public void setIndustry(String industry) {
        Industry = industry;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getLongitude() {
        return Longitude;
    }

    public void setLongitude(String longitude) {
        Longitude = longitude;
    }

    public String getLatitude() {
        return Latitude;
    }

    public void setLatitude(String latitude) {
        Latitude = latitude;
    }

    public String getRole() {
        return Role;
    }

    public void setRole(String role) {
        Role = role;
    }

    public String getRoleText() {
        return RoleText;
    }

    public void setRoleText(String roleText) {
        RoleText = roleText;
    }

    public Object[] getServices() {
        return Services;
    }

    public void setServices(Object[] services) {
        Services = services;
    }

    public Long getId() {
        return Id;
    }

    public void setId(Long id) {
        Id = id;
    }
}
