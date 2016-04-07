package VPI;

/**
 * Created by sabu on 06/04/2016.
 */
public class Data {

    private Long id;
    private String name;
    private String email;

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "Id: " + id +
                ", Name: " + name +
                ", Email: " + email;
    }
}
