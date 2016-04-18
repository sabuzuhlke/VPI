package VPI.VClasses;

import VPI.PDClasses.ContactDetail;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gebo on 14/04/2016.
 */
public class VContact {
    @JsonProperty("Id")
    private Long id;

    @JsonProperty("Full Name")
    private String name;

    public List<ContactDetail> email;
    public List<ContactDetail> phone;

    public VContact() {
        this.email = new ArrayList<>();
        this.phone = new ArrayList<>();
    }

    public VContact(ContactDetail primEmail, ContactDetail primPhone) {
        this.email = new ArrayList<>();
        this.phone = new ArrayList<>();
        this.email.add(primEmail);
        this.phone.add(primPhone);
    }

    public List<ContactDetail> getEmail() {
        return email;
    }

    public void setEmail(List<ContactDetail> email) {
        this.email = email;
    }

    public List<ContactDetail> getPhone() {
        return phone;
    }

    public void setPhone(List<ContactDetail> phone) {
        this.phone = phone;
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
}
