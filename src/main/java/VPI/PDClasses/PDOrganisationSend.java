package VPI.PDClasses;

import VPI.InsightClasses.VOrganisation;
import VPI.VertecClasses.JSONOrganisation;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public class PDOrganisationSend {

    private String name;
    private Integer visible_to;
    private String address;
    private Boolean active_flag;
    private Long id;
    private Long company_id;
    private Long owner_id;
    //Last Update Fields
    private String update_time;
    private Integer people_count;
    @JsonProperty("791dd01384b035443350aa5f1939a598766154cc")
    private Long v_id;
    @JsonProperty("add_time")
    private String creationTime;

    public PDOrganisationSend(String name, String address, Integer visible_to) {
        this.name = name;
        this.visible_to = visible_to;
        this.address = address;
        this.active_flag = true;
    }
    public PDOrganisationSend(String name, String address) {
        this.name = name;
        this.address = address;
        this.active_flag = true;
    }
    public PDOrganisationSend(String name, Integer visible_to) {
        this.name = name;
        this.visible_to = visible_to;
        this.active_flag = true;
    }

    public PDOrganisationSend() {
    }

    //insight
    public PDOrganisationSend(PDOrganisation o) {
        this.name = o.getName();
        this.visible_to = o.getVisible_to();
        this.address = o.getAddress();
        this.active_flag = true;
        this.id = o.getId();
        this.company_id = o.getCompany_id();
        this.owner_id = o.getOwner_id().getId();
        this.update_time = o.getUpdate_time();
        this.v_id = o.getV_id();
        this.creationTime = o.getCreationTime();
    }

    //insight
    public PDOrganisationSend(VOrganisation c) {
        this.name = c.getName();
        this.visible_to = 3;
        this.active_flag = true;
        this.address = c.getFormattedAddress();
        this.v_id = c.getId();
    }

    public PDOrganisationSend(Long id, String name, Integer visible_to, String address, Boolean active_flag, Long company_id, Long owner_id) {
        this.name = name;
        this.visible_to = visible_to;
        this.address = address;
        this.active_flag = active_flag;
        this.id = id;
        this.company_id = company_id;
        this.owner_id = owner_id;
    }

    public PDOrganisationSend(JSONOrganisation jo, PDOrganisation po, Long ownerId){
        this.name = jo.getName();
        this.visible_to = 3;
        this.v_id = jo.getObjid();
        this.id = po.getId();
        this.active_flag = true;
        this.company_id = po.getCompany_id();
        this.owner_id = ownerId;

        this.address = jo.getFormattedAddress();

        try{
            if(jo.getCreationTime() != null){
                if (jo.getCreationTime().equals("1900-01-01")) {
                    this.creationTime = "1900-01-01 00:00:00";
                } else {
                    String[] dateFormatter = jo.getCreationTime().split("T");
                    String date = dateFormatter[0];
                    String time = dateFormatter[1];
                    this.creationTime = date + " " + time;
                }
            }
        } catch (Exception e){
            System.out.println("Exception while creating pdorgsend from JSONorg: " + e);
            System.out.println("Name: " + this.name + " VId: " + this.v_id);
            System.out.println(jo.getCreationTime());
        }
    }

    public PDOrganisationSend(JSONOrganisation o, Long owner_id){
        this.name = o.getName();
        if(name == null || name.isEmpty() || name.equals(" ")) name = "Anonymous co";
        this.visible_to = 3;
        this.v_id = o.getObjid();
        this.active_flag = true;
        this.address = o.getFormattedAddress();
        try{
            if(o.getCreationTime() != null){
                String[] dateFormatter = o.getCreationTime().split("T");
                String date = dateFormatter[0];
                String time = dateFormatter[1];
                this.creationTime = date + " " + time;
            }
        } catch (Exception e){
            System.out.println("Exception while creating pdorgsend from JSONorg: " + e);
            System.out.println("Name: " + this.name + " VId: " + this.v_id);
            System.out.println(o.getCreationTime());
        }

        this.owner_id = owner_id;
    }

    //use this function when reading update time from object imported from pipedrive
    public LocalDateTime readDateFromPDOrganisation() {
        String[] dAndT = this.update_time.split(" ");
        return LocalDateTime.parse(dAndT[0] + "T" + dAndT[1]);
    }

    public Long getV_id() {
        return v_id;
    }

    public void setV_id(Long v_id) {
        this.v_id = v_id;
    }

    public String getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(String update_time) {
        this.update_time = update_time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getVisible_to() {
        return visible_to;
    }

    public void setVisible_to(Integer visible_to) {
        this.visible_to = visible_to;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    public Long getCompany_id() {
        return company_id;
    }

    public void setCompany_id(Long company_id) {
        this.company_id = company_id;
    }

    public Long getOwner_id() {
        return owner_id;
    }

    public void setOwner_id(Long owner_id) {
        this.owner_id = owner_id;
    }

    public Integer getPeople_count() {
        return people_count;
    }

    public void setPeople_count(Integer people_count) {
        this.people_count = people_count;
    }

    public String getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(String creationTime) {
        this.creationTime = creationTime;
    }

    @Override
    public String toString() {
        return "Company: ID: " + id + " Name: " + name  + " visible to: " + visible_to + ", Address: " + address + " Active: " + active_flag + " Compani_id: " + company_id;
    }
}
