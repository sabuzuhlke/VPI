package VPI;

/**
 * Created by sabu on 06/04/2016.
 */

public class OrganisationPost {

    private String name;
    private Integer visible_to;
    private String address;

    public OrganisationPost(String name, String address, Integer visible_to) {
        this.name = name;
        this.visible_to = visible_to;
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getVisible_to() {
        return visible_to;
    }

    public void setVisible_to(Integer i) {
        this.visible_to = i;
    }

    @Override
    public String toString() {
        return "Company Name: " + name + ", Address: " + address;
    }
}
