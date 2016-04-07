package VPI;

/**
 * Created by sabu on 07/04/2016.
 */
public class Owner {

    private Long id;
    private String name;
    private String email;

    public Owner() {
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
        return "Owner_id: " + id + ", name: " + name + ", email: " + email;
    }
}
