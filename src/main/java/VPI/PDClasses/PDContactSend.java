package VPI.PDClasses;

/**
 * Created by gebo on 14/04/2016.
 */
public class PDContactSend {

    private Long id;
    private String name;
    private Long org_id;
    private ContactDetail[] email;
    private ContactDetail[] phone;
    private Integer visible_to;

    public PDContactSend() {
    }

    public PDContactSend(Long org_id, String name, String email, String phone) {
        this.org_id = org_id;
        this.name = name;
        ContactDetail emailDetail = new ContactDetail(email, true);
        ContactDetail phoneDetail = new ContactDetail(phone, true);
        this.email = new ContactDetail[1];
        this.email[0] = emailDetail;
        this.phone = new ContactDetail[1];
        this.phone[0] = phoneDetail;
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

    public ContactDetail[] getEmail() {
        return email;
    }

    public void setEmail(ContactDetail[] email) {
        this.email = email;
    }

    public ContactDetail[] getPhone() {
        return phone;
    }

    public void setPhone(ContactDetail[] phone) {
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
