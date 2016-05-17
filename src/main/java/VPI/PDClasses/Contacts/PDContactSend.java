package VPI.PDClasses.Contacts;

import VPI.InsightClasses.VContact;
import VPI.VertecClasses.VertecOrganisations.JSONContact;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gebo on 14/04/2016.
 */
public class PDContactSend {

    private Long id;
    private String name;
    private Long org_id;
    private List<ContactDetail> email;
    private List<ContactDetail> phone;
    private Integer visible_to;
    private Boolean active_flag;
    @JsonProperty("77f255155d7a104848c88ded9043c593ea1fbba3")
    private Long v_id;

    @JsonProperty("add_time")
    private String creationTime;

    private Long owner_id;
    @JsonIgnore
    private List<Long> followers;

    public PDContactSend() {
        this.email = new ArrayList<>();
        this.phone = new ArrayList<>();
        this.active_flag = true;
        this.followers = new ArrayList<>();
    }

    public PDContactSend(Long org_id, String name, String email, String phone) {
        this.org_id = org_id;
        this.name = name;
        ContactDetail emailDetail = new ContactDetail(email, true);
        ContactDetail phoneDetail = new ContactDetail(phone, true);
        this.email = new ArrayList<>();
        this.email.add(emailDetail);
        this.phone = new ArrayList<>();
        this.phone.add(phoneDetail);
        this.active_flag = true;

        this.followers = new ArrayList<>();
    }

    //insight
    public PDContactSend(VContact vc) {
        this.name = vc.getName();
        this.email = vc.getEmailDetail();
        this.phone = vc.getPhoneDetail();
        this.visible_to = 3;
        this.active_flag = true;

        this.followers = new ArrayList<>();
    }

    public PDContactSend(JSONContact c, Long owner) {
        this.name = c.getFirstName() + " " + c.getSurname();
        if(name.equals(" ")) name = "Anonymous";
        this.active_flag = true;
        this.visible_to = 3;
        ContactDetail emaild = new ContactDetail(c.getEmail(), true);
        this.email = new ArrayList<>();
        this.email.add(emaild);
        ContactDetail phoned = new ContactDetail(c.getPhone(), true);
        ContactDetail mobiled = new ContactDetail(c.getMobile(), false);
        this.phone = new ArrayList<>();
        this.phone.add(phoned);
        this.phone.add(mobiled);
        this.v_id = c.getObjid();

        if(c.getCreationTime() != null){
            String[] dateFormatter = c.getCreationTime().split("T");
            String date = dateFormatter[0];
            String time = dateFormatter[1];
            this.creationTime = date + " " + time;
        }

        this.owner_id = owner;

        this.followers = new ArrayList<>();

    }

    //for Contact put
    public PDContactSend(PDContactReceived pc) {
        this.name = pc.getName();
        this.id = pc.getId();
        if(pc.getOrg_id() != null) {
            this.org_id = pc.getOrg_id().getValue();
        }
        this.email = pc.getEmail();
        this.phone = pc.getPhone();
        this.visible_to = pc.getVisible_to();
        this.active_flag = pc.getActive_flag();
        this.v_id = pc.getV_id();
        this.creationTime = pc.getCreationTime();
        if(pc.getOwner_id() != null){
            this.owner_id = pc.getOwner_id().getId();
        }
        this.followers = new ArrayList<>();

    }

    public void changePrimaryEmail(String newEmail){
        for(ContactDetail e : email){
            if(e.getPrimary()){
                e.setValue(newEmail);
            }
        }
    }

    public void changePrimaryPhone(String newPhone){
        for(ContactDetail p : phone){
            if(p.getPrimary()){
                p.setValue(newPhone);
            }
        }
    }

    public Boolean getActive_flag() {
        return active_flag;
    }

    public void setActive_flag(Boolean active_flag) {
        this.active_flag = active_flag;
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

    public Long getOrg_id() {
        return org_id;
    }

    public void setOrg_id(Long org_id) {
        this.org_id = org_id;
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

    public Integer getVisible_to() {
        return visible_to;
    }

    public void setVisible_to(Integer visible_to) {
        this.visible_to = visible_to;
    }

    public Long getV_id() {
        return v_id;
    }

    public void setV_id(Long v_id) {
        this.v_id = v_id;
    }

    public String getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(String creationTime) {
        this.creationTime = creationTime;
    }

    public Long getOwner_id() {
        return owner_id;
    }

    public void setOwner_id(Long owner_id) {
        this.owner_id = owner_id;
    }

    public List<Long> getFollowers() {
        return followers;
    }

    public void setFollowers(List<Long> followers) {
        this.followers = followers;
    }

    @Override
    public String toString(){
        return "Number " + id + ":" + name;
    }
    //------------------------------------------------------------------ContactDetail

}