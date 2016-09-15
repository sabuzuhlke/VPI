package VPI.PDClasses;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * All objects on pipedrive have an Owner (user) associated with them. That owner is returned in this format.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class PDOwner {

    private Long id;
    private String name;
    private String email;

    public PDOwner() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "Owner_id: " + id + ", name: " + name + ", emailDetail: " + email;
    }
}
