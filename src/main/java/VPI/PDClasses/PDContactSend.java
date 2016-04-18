package VPI.PDClasses;

import VPI.VClasses.VContact;

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

    public PDContactSend() {
        this.email = new ArrayList<>();
        this.phone = new ArrayList<>();
        this.active_flag = true;
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
    }

    public PDContactSend(VContact vc) {
        this.name = vc.getName();
        this.email = vc.getEmail();
        this.phone = vc.getPhone();
        this.visible_to = 3;
        this.active_flag = true;
    }

    public PDContactSend(PDContactReceived pc) {
        this.name = pc.getName();
        this.id = pc.getId();
        this.org_id = pc.getOrg_id().getValue();
        this.email = pc.getEmail();
        this.phone = pc.getPhone();
        this.visible_to = pc.getVisible_to();
        this.active_flag = pc.getActive_flag();
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

    @Override
    public String toString(){
        return "Number " + id + ":" + name;
    }
    //------------------------------------------------------------------ContactDetail

}
