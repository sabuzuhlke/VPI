package VPI.PDClasses.Deals.util;

import VPI.PDClasses.Contacts.util.ContactDetail;

import java.util.List;

/**
 * Created by sabu on 12/05/2016.
 */
public class PDPersonId {

    private String name;
    private List<ContactDetail> email;
    private List<ContactDetail> phone;
    private Long value;

    public PDPersonId() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public Long getValue() {
        return value;
    }

    public void setValue(Long value) {
        this.value = value;
    }
}
