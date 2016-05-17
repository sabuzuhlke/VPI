package VPI.PDClasses.Deals;

/**
 * Created by sabu on 17/05/2016.
 */

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by sabu on 12/05/2016.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PDDealPut {

    @JsonIgnore
    private Long id;//only set if part of PUT
    @JsonProperty("title")
    private String title;
    @JsonProperty("value")
    private String value;
    @JsonProperty("currency")
    private String currency;
    @JsonProperty("user_id")
    private Long user_id;
    @JsonProperty("person_id")
    private Long person_id;
    @JsonProperty("org_id")
    private Long org_id;
    @JsonProperty("stage_id")
    private int stage_id;
    @JsonProperty("lost_reason")
    private String lost_reason;
    @JsonProperty("status")
    private String status;
    @JsonProperty("add_time")
    private String add_time;
    @JsonProperty("visible_to")
    private Integer visible_to = 3;

    @JsonIgnore
    private String modified;

    @JsonProperty("ca0900cc615df148dc968c83c52020b1bfad7798")
    private String zuhlke_office;
    @JsonProperty("a49f4c82c7c44286df3c137791bcda2170c3ae75")
    private String lead_type;
    @JsonProperty("361cf6ef6cc225008251d67a6a3fdcbbc8f03d55")
    private String project_number;
    @JsonProperty("7ef8282593f5a552696a36a842b250730c4df8ca")
    private String phase;
    @JsonProperty("44105f961d387bc35323ecf4bc6325be3a732c8d")
    private Long cost;
    @JsonProperty("44105f961d387bc35323ecf4bc6325be3a732c8d_currency")
    private String cost_currency;
    @JsonProperty("a604365b4dc4fbe6c736f02efd82ea41ace64595")
    private Long v_id;

    public PDDealPut() {
    }

    public PDDealPut(PDDealReceived d){
        this.id = d.getId();
        this.title = d.getTitle();
        this.value = d.getValue();
        this.currency = d.getCurrency();
        this.user_id = d.getUser_id().getId();
        this.person_id = d.getPerson_id().getValue();
        this.org_id = d.getOrg_id().getValue();
        this.stage_id = d.getStage_id();
        this.lost_reason = d.getLost_reason();
        this.status = d.getStatus();
        this.visible_to = 3;
        this.modified = d.getUpdate_time();
        this.zuhlke_office = d.getZuhlke_office();
        this.lead_type = d.getLead_type();
        this.project_number = d.getProject_number();
        this.phase = d.getPhase();
        this.cost = d.getCost();
        this.cost_currency = d.getCost_currency();
        this.v_id = d.getV_id();

    }

    public String getTitle() {

        return title;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Long getUser_id() {
        return user_id;
    }

    public void setUser_id(Long user_id) {
        this.user_id = user_id;
    }

    public Long getPerson_id() {
        return person_id;
    }

    public void setPerson_id(Long person_id) {
        this.person_id = person_id;
    }

    public Long getOrg_id() {
        return org_id;
    }

    public void setOrg_id(Long org_id) {
        this.org_id = org_id;
    }

    public int getStage_id() {
        return stage_id;
    }

    public void setStage_id(int stage_id) {
        this.stage_id = stage_id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAdd_time() {
        return add_time;
    }

    public void setAdd_time(String add_time) {
        this.add_time = add_time;
    }

    public Integer getVisible_to() {
        return visible_to;
    }

    public void setVisible_to(Integer visible_to) {
        this.visible_to = visible_to;
    }

    public String getLost_reason() {
        return lost_reason;
    }

    public void setLost_reason(String lost_reason) {
        this.lost_reason = lost_reason;
    }

    public String getZuhlke_office() {
        return zuhlke_office;
    }

    public void setZuhlke_office(String zuhlke_office) {
        this.zuhlke_office = zuhlke_office;
    }

    public String getLead_type() {
        return lead_type;
    }

    public void setLead_type(String lead_type) {
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

    public Long getV_id() {
        return v_id;
    }

    public void setV_id(Long v_id) {
        this.v_id = v_id;
    }

    public String getModified() {
        return modified;
    }

    public void setModified(String modified) {
        this.modified = modified;
    }
}
