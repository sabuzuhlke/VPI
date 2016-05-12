package VPI.PDClasses.Deals;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by sabu on 12/05/2016.
 */
public class PDDealSend {

    private String title;
    private String value;
    private String currency;
    private Long user_id;
    private Long person_id;
    private Long org_id;
    private Long stage_id;
    private String lost_reason;
    private Integer status;
    private String add_time;
    private Integer visible_to = 3;

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

    public PDDealSend() {
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

    public Long getStage_id() {
        return stage_id;
    }

    public void setStage_id(Long stage_id) {
        this.stage_id = stage_id;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
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
}
