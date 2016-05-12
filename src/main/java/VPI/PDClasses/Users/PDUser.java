package VPI.PDClasses.Users;

/**
 * Created by gebo on 10/05/2016.
 */
public class PDUser {

    private String email;
    private Long id;

    public PDUser() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {

        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
