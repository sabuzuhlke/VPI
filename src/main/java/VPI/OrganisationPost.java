package VPI;

/**
 * Created by sabu on 06/04/2016.
 */

public class OrganisationPost {

    private String name;
    private Integer visible_to;

    public OrganisationPost(String name, Integer visible_to) {
        this.name = name;
        this.visible_to = visible_to;
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
        return "Company Name: " + name;
    }
}
