package VPI.PDClasses;

/**
 * Created by gebo on 14/04/2016.
 */
public class PDContactReceived {

    private Long id;
    private OrgId org_id;
    private PDOwner owner_id;
    private String name;
    private Boolean active_flag;
    private ContactDetail[] phone;
    private ContactDetail[] email;
    private Integer visible_to;

    public PDContactReceived() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public OrgId getOrg_id() {
        return org_id;
    }

    public void setOrg_id(OrgId org_id) {
        this.org_id = org_id;
    }

    public PDOwner getOwner_id() {
        return owner_id;
    }

    public void setOwner_id(PDOwner owner_id) {
        this.owner_id = owner_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getActive_flag() {
        return active_flag;
    }

    public void setActive_flag(Boolean active_flag) {
        this.active_flag = active_flag;
    }

    public ContactDetail[] getPhone() {
        return phone;
    }

    public void setPhone(ContactDetail[] phone) {
        this.phone = phone;
    }

    public ContactDetail[] getEmail() {
        return email;
    }

    public void setEmail(ContactDetail[] email) {
        this.email = email;
    }

    public Integer getVisible_to() {
        return visible_to;
    }

    public void setVisible_to(Integer visible_to) {
        this.visible_to = visible_to;
    }


}
