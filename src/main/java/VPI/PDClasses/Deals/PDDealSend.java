package VPI.PDClasses.Deals;

import VPI.VertecClasses.VertecProjects.JSONPhase;
import VPI.VertecClasses.VertecProjects.JSONProject;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

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
    @JsonProperty("lost_time")
    private String lost_time;
    @JsonProperty("won_time")
    private String won_time;
    @JsonProperty("expected_close_date")
    private String exp_close_date;

    //Custom fields
    @JsonProperty("475b8fbb629c9c8926cbb51f18408853f492bf88")//"b68fd3995ae7d6b35b13a0dc6b523ddd6fa86f0a")//"7513bc1ddf030d16508c593c097537da6d2b5865")
    private String zuhlke_office;
    @JsonProperty("1e03a967f5ebcae35addc0eecdb8303f545d3b58")//"842fd4eefb962a23bd29244d92eafb158df839c7")//"3d133ca0d93126ed643d314ac98f0c8bdb485b1f")
    private String lead_type;
    @JsonProperty("b8edc6487d51a48677bb88122d30dd3aed392f8b")//"063365c54334ba785c7890ceabee21363a7aee20")//"cdb9d7237459a4912b101871d758dcebfdace08f")
    private String project_number;
    @JsonProperty("4b712fa58b02227b485c4a158d6bfde2ec91a82d")//"f400d14ff791db170a08f163a33445681ecc3c0a")//"c340a82a1529b131894bcf743b0b30de963139fb")
    private String phase;
    @JsonProperty("b714523f271d2f358287619f8a51822ba706c2d6")//"f7dbfe350bd28318d6775a73988ee1ddcbeeb8f3")//"02d60bdc626550f2fa1aaf9d38fbfef20ce18a34")
    private Long cost;
    @JsonProperty("5ddb4252140b1eed30d76b03b4c99af200b1356d")//"09c5371529d476b7d3d6b22475238f447605ad47")//"d444c37006b2f7d3b8a0fc41af636f71a6633b42")
    private Long v_id;

    /**
     * Used by Importer to build deals for put
     */
    public PDDealSend() {
    }

    /**
     * Used to create deals for posting to pipedrive
     * @param project is project recieved from vertec
     * @param phase is phase recieved from vertec
     * @param user_id is pipedrive id of user that owns it (from external map)
     * @param person_id is pipedrive id of associated contact (from external map)
     * @param org_id is pipedrive id of associated organisations (from external map)
     */
    public PDDealSend(JSONProject project, JSONPhase phase, Long user_id, Long person_id, Long org_id){
        //Title
        if (project.getTitle() != null && !project.getTitle().equals("")) {
            String title = project.getTitle() + ": " + phase.getDescription();
            if(phase.getDescription() == null) title += phase.getCode();
           this.title = title;
        } else {
            this.title = phase.getDescription();
        }
        //Value
        this.value = phase.getExternalValue();
        //currency
        this.currency = project.getCurrency();
        //User_id
        this.user_id = user_id;
        //Person_id
        this.person_id = person_id;
        //org_id
        this.org_id = org_id;
        //add_time
        try {
            String[] dateTime = phase.getCreationDate().split("T");
            String date = dateTime[0];
            String time = dateTime[1];
            this.add_time = date + " " + time;
        } catch (Exception e) {
            this.add_time = "2000-01-01 00:00:00";
        }
        //visible_to (1 = owner and followers, 3 = everyone)
        this.visible_to = 3;
        //v_id
        this. v_id = phase.getV_id();
        //project number
       this.project_number = project.getCode();
        //phase
        this.phase = phase.getCode();
        //stageId --- SET OUTSIDE

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

    public String getExp_close_date() {
        return exp_close_date;
    }

    public void setExp_close_date(String exp_close_date) {
        this.exp_close_date = exp_close_date;
    }

    public String toPrettyJSON() {
        String retStr = null;
        ObjectMapper m = new ObjectMapper();
        try{
            retStr = m.writerWithDefaultPrettyPrinter().writeValueAsString(this);
        }
        catch(Exception e){
            System.out.println("Could not convert PDDealSend to JSON: " + e.toString());
        }
        return retStr;
    }
}
