package VPI;

/**
 * Created by gebo on 14/04/2016.
 */
public class PDContactResponse {
    private Long id;
    private OrgId org_id;
    private PDOwner owner_id;
    private String name;
    private Boolean active_flag;
    private PDContact.ContactDetail phone;
    private PDContact.ContactDetail email;
    private Integer visible_to;

    public PDContactResponse() {
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

    public PDContact.ContactDetail getPhone() {
        return phone;
    }

    public void setPhone(PDContact.ContactDetail phone) {
        this.phone = phone;
    }

    public PDContact.ContactDetail getEmail() {
        return email;
    }

    public void setEmail(PDContact.ContactDetail email) {
        this.email = email;
    }

    public Integer getVisible_to() {
        return visible_to;
    }

    public void setVisible_to(Integer visible_to) {
        this.visible_to = visible_to;
    }

    class OrgId{
        private String name;
        private Integer people_count;
        private Long owner_id;
        private String address;
        private Long value;

        public OrgId() {
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Integer getPeople_count() {
            return people_count;
        }

        public void setPeople_count(Integer people_count) {
            this.people_count = people_count;
        }

        public Long getOwner_id() {
            return owner_id;
        }

        public void setOwner_id(Long owner_id) {
            this.owner_id = owner_id;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public Long getValue() {
            return value;
        }

        public void setValue(Long value) {
            this.value = value;
        }
    }
}
