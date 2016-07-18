package VPI.InsightClasses;

import VPI.Entities.util.ContactDetail;
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

    public List<ContactDetail> emailDetail;


    public List<ContactDetail> phoneDetail;

    @JsonProperty("Position")
    private String position;
    @JsonProperty("Phone")
    private String phone;

    @JsonProperty("EMail")
    private String email;

    public VContact() {
        this.emailDetail = new ArrayList<>();
        this.phoneDetail = new ArrayList<>();
    }

    public VContact(ContactDetail primEmail, ContactDetail primPhone) {
        this.emailDetail = new ArrayList<>();
        this.phoneDetail = new ArrayList<>();
        this.emailDetail.add(primEmail);
        this.phoneDetail.add(primPhone);
    }

    public List<ContactDetail> getEmailDetail() {
        List<ContactDetail> ret = new ArrayList<>();
        if (email != null){
            ret.add(new ContactDetail(email,true));
        }
        emailDetail = ret;
        return emailDetail;
    }

    public List<ContactDetail> getPhoneDetail() {

        List<ContactDetail> ret = new ArrayList<>();
        if(phone != null){
            ret.add(new ContactDetail(phone,true));
        }
        phoneDetail = ret;
        return phoneDetail;
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

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
