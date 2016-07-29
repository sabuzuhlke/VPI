package VPI.PDClasses.Deals;

import VPI.Keys.DevelopmentKeys;
import VPI.Keys.ProductionKeys;
import VPI.PDClasses.Contacts.util.OrgId;
import VPI.PDClasses.Deals.util.PDPersonId;
import VPI.PDClasses.PDOwner;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Created by sabu on 12/05/2016.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PDDealReceived {

    private Long id;//
    private PDOwner user_id;//
    private PDOwner creator_user_id;
    private PDPersonId person_id;//
    private OrgId org_id;//
    private int stage_id;//
    private String title;//
    private String value;//
    private String currency;//
    private String update_time;
    private Boolean active;
    private String status;
    private String visible_to;
    private int pipeline_id;
    private String lost_reason;
    private String won_time;
    private String lost_time;
    //won_time
    //lost_time
    @JsonProperty(DevelopmentKeys.dealZuhlkeOffice)//"b68fd3995ae7d6b35b13a0dc6b523ddd6fa86f0a")//"7513bc1ddf030d16508c593c097537da6d2b5865")
    private String zuhlke_office;
    @JsonProperty(DevelopmentKeys.dealLeadType)//"842fd4eefb962a23bd29244d92eafb158df839c7")//"3d133ca0d93126ed643d314ac98f0c8bdb485b1f")
    private String lead_type;
    @JsonProperty(DevelopmentKeys.dealProjectNumber)//"063365c54334ba785c7890ceabee21363a7aee20")//"cdb9d7237459a4912b101871d758dcebfdace08f")
    private String project_number;
    @JsonProperty(DevelopmentKeys.dealPhase)//"f400d14ff791db170a08f163a33445681ecc3c0a")//"c340a82a1529b131894bcf743b0b30de963139fb")
    private String phase;
    @JsonProperty(DevelopmentKeys.dealCost)//"f7dbfe350bd28318d6775a73988ee1ddcbeeb8f3")//"02d60bdc626550f2fa1aaf9d38fbfef20ce18a34")
    private Long cost;
    @JsonProperty(DevelopmentKeys.dealVID)//"09c5371529d476b7d3d6b22475238f447605ad47")//"d444c37006b2f7d3b8a0fc41af636f71a6633b42")
    private Long v_id;

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

    public String getLost_reason() {
        return lost_reason;
    }

    public void setLost_reason(String lost_reason) {
        this.lost_reason = lost_reason;
    }

    public Long getV_id() {
        return v_id;
    }

    public void setV_id(Long v_id) {
        this.v_id = v_id;
    }

    public String getWon_time() {
        return won_time;
    }

    public void setWon_time(String won_time) {
        this.won_time = won_time;
    }

    public String getLost_time() {
        return lost_time;
    }

    public void setLost_time(String lost_time) {
        this.lost_time = lost_time;
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
