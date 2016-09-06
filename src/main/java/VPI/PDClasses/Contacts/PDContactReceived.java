package VPI.PDClasses.Contacts;

import VPI.Entities.util.ContactDetail;
import VPI.Keys.DevelopmentKeys;
import VPI.Keys.ProductionKeys;
import VPI.PDClasses.Contacts.util.OrgId;
import VPI.PDClasses.PDOwner;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PDContactReceived {

    //Standard Fields
    private Long id;
    private OrgId org_id;
    private PDOwner owner_id;

    private String name;
    private String first_name;
    private String last_name;

    private Boolean active_flag;
    private List<ContactDetail> phone;
    private List<ContactDetail> email;
    private Integer visible_to;
    @JsonProperty("add_time")
    private String creationTime;
    @JsonProperty("update_time")
    private String modifiedTime;
    //Custom fields
    @JsonProperty( DevelopmentKeys.contactVID)//"097010f4aaf7a80b625fbdc935776b7eda8ee7d9")//"174a3d80c1a33b8d645448ae75c9c9aec00d4d8f")
    private Long v_id;
    @JsonProperty( DevelopmentKeys.contactOwnedBy)//"c6502da83d3caff3be297fd2082f49c883f08374")//"2dee7a68d8d02226be8f5d95eb5c26aebd4012c0")
    private String ownedBy;
    @JsonProperty( DevelopmentKeys.contactPosition)//"2045a0bc69aa638567d8b75bbcb5bb5063246456")
    private String position;

    /**
     * Used by RestTemplate and for testing
     */
    public PDContactReceived() {
        this.phone = new ArrayList<>();
        this.email = new ArrayList<>();
    }

    public String getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(String creationTime) {
        this.creationTime = creationTime;
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

    public List<ContactDetail> getPhone() {
        return phone;
    }

    public void setPhone(List<ContactDetail> phone) {
        this.phone = phone;
    }

    public List<ContactDetail> getEmail() {
        return email;
    }

    public void setEmail(List<ContactDetail> email) {
        this.email = email;
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

    public String getModifiedTime() {
        return modifiedTime;
    }

    public void setModifiedTime(String modifiedTime) {
        this.modifiedTime = modifiedTime;
    }
//
//    public String getPosition() {
//        return position;
//    }
//
//    public void setPosition(String position) {
//        this.position = position;
//    }

    public String getOwnedBy() {
        return ownedBy;
    }

    public void setOwnedBy(String ownedBy) {
        this.ownedBy = ownedBy;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }
}
