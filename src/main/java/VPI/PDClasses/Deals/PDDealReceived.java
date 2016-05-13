package VPI.PDClasses.Deals;

import VPI.PDClasses.Contacts.OrgId;
import VPI.PDClasses.PDOwner;
import VPI.PDClasses.Users.PDUser;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Created by sabu on 12/05/2016.
 */
public class PDDealReceived {

    private Long id;
    private PDOwner user_id;
    private PDOwner creator_user_id;
    private PDPersonId person_id;
    private OrgId org_id;
    private int stage_id;
    private String title;
    private String value;
    private String currency;
    private String update_time;
    private Boolean active;
    private String status;
    private String visible_to;
    private int pipeline_id;
    //won_time
    //lost_time
    //lost_reason
    @JsonProperty("ca0900cc615df148dc968c83c52020b1bfad7798")
    private String zuhlke_office;
    @JsonProperty("a49f4c82c7c44286df3c137791bcda2170c3ae75")
    private Integer lead_type;
    @JsonProperty("361cf6ef6cc225008251d67a6a3fdcbbc8f03d55")
    private String project_number;
    @JsonProperty("7ef8282593f5a552696a36a842b250730c4df8ca")
    private String phase;
    @JsonProperty("44105f961d387bc35323ecf4bc6325be3a732c8d")
    private Long cost;
    @JsonProperty("44105f961d387bc35323ecf4bc6325be3a732c8d_currency")
    private String cost_currency;

    private int stage_order_nr;

    public PDDealReceived() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PDOwner getUser_id() {
        return user_id;
    }

    public void setUser_id(PDOwner user_id) {
        this.user_id = user_id;
    }

    public PDOwner getCreator_user_id() {
        return creator_user_id;
    }

    public void setCreator_user_id(PDOwner creator_user_id) {
        this.creator_user_id = creator_user_id;
    }

    public PDPersonId getPerson_id() {
        return person_id;
    }

    public void setPerson_id(PDPersonId person_id) {
        this.person_id = person_id;
    }

    public OrgId getOrg_id() {
        return org_id;
    }

    public void setOrg_id(OrgId org_id) {
        this.org_id = org_id;
    }

    public int getStage_id() {
        return stage_id;
    }

    public void setStage_id(int stage_id) {
        this.stage_id = stage_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(String update_time) {
        this.update_time = update_time;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getVisible_to() {
        return visible_to;
    }

    public void setVisible_to(String visible_to) {
        this.visible_to = visible_to;
    }

    public int getPipeline_id() {
        return pipeline_id;
    }

    public void setPipeline_id(int pipeline_id) {
        this.pipeline_id = pipeline_id;
    }

    public String getZuhlke_office() {
        return zuhlke_office;
    }

    public void setZuhlke_office(String zuhlke_office) {
        this.zuhlke_office = zuhlke_office;
    }

    public Integer getLead_type() {
        return lead_type;
    }

    public void setLead_type(Integer lead_type) {
        this.lead_type = lead_type;
    }

    public String getProject_number() {
        return project_number;
    }

    public void setProject_number(String project_number) {
        this.project_number = project_number;
    }

    public String getPhase() {
        return phase;
    }

    public void setPhase(String phase) {
        this.phase = phase;
    }

    public Long getCost() {
        return cost;
    }

    public void setCost(Long cost) {
        this.cost = cost;
    }

    public String getCost_currency() {
        return cost_currency;
    }

    public void setCost_currency(String cost_currency) {
        this.cost_currency = cost_currency;
    }

    public int getStage_order_nr() {
        return stage_order_nr;
    }

    public void setStage_order_nr(int stage_order_nr) {
        this.stage_order_nr = stage_order_nr;
    }
    @Override
    public String toString(){
        String retStr = null;
        ObjectMapper m = new ObjectMapper();
        try{

            retStr = m.writerWithDefaultPrettyPrinter().writeValueAsString(this);
        }
        catch(Exception e){
            System.out.println("Could not convert XML Envelope to JSON: " + e.toString());
        }
        return retStr;
    }

}
