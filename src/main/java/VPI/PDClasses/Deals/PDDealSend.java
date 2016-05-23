package VPI.PDClasses.Deals;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by sabu on 12/05/2016.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PDDealSend {

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

    @JsonProperty("7513bc1ddf030d16508c593c097537da6d2b5865")
    private String zuhlke_office;
    @JsonProperty("3d133ca0d93126ed643d314ac98f0c8bdb485b1f")
    private String lead_type;
    @JsonProperty("cdb9d7237459a4912b101871d758dcebfdace08f")
    private String project_number;
    @JsonProperty("c340a82a1529b131894bcf743b0b30de963139fb")
    private String phase;
    @JsonProperty("02d60bdc626550f2fa1aaf9d38fbfef20ce18a34")
    private Long cost;
    //@JsonProperty("44105f961d387bc35323ecf4bc6325be3a732c8d_currency")
    @JsonIgnore
    private String cost_currency;
    @JsonProperty("d444c37006b2f7d3b8a0fc41af636f71a6633b42")
    private Long v_id;

    public PDDealSend() {
    }

    public PDDealSend(PDDealReceived d){
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
