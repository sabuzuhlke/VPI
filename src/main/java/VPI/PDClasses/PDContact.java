package VPI.PDClasses;

/**
 * Created by gebo on 14/04/2016.
 */
public class PDContact {
    private Long id;
    private String name;
    private Long org_id;
    private ContactDetail[] email;
    private ContactDetail[] phone;
    private  Boolean visible_to;

    public PDContact() {
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

    public Boolean getVisible_to() {
        return visible_to;
    }

    public void setVisible_to(Boolean visible_to) {
        this.visible_to = visible_to;
    }

    @Override
    public String toString(){
        return "Number " + id + ":" + name;
    }
    //------------------------------------------------------------------ContactDetail
    class ContactDetail {
        private String value;
        private Boolean primary;

        public ContactDetail() {
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public Boolean getPrimary() {
            return primary;
        }

        public void setPrimary(Boolean primary) {
            this.primary = primary;
        }
    }

}
