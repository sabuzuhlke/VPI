package VPI.VertecClasses.VertecProjects;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by sabu on 16/05/2016.
 */
public class JSONPhase {

    @JsonProperty("v_id")
    private Long v_id;
    @JsonProperty("active")
    private Boolean active;
    @JsonProperty("description")
    private String description;
    @JsonProperty("code")
    private String code;
    @JsonProperty("status")
    private int status;
    @JsonProperty("sales_status")
    private String salesStatus;
    @JsonProperty("external_value")
    private String externalValue;
    @JsonProperty("internal_value")
    private String internalValue;
    @JsonProperty("person_responsible")
    private String personResponsible;
    @JsonProperty("start_date")
    private String startDate;
    @JsonProperty("end_date")
    private String endDate;
    @JsonProperty("offered_date")
    private String offeredDate;
    @JsonProperty("granted_date")
    private String grantedDate;
    @JsonProperty("lost_reason")
    private String lostReason;
    @JsonProperty("last_modified")
    private String modifiedDate;
    @JsonProperty("created")
    private String creationDate;
    @JsonProperty("completion_date")
    private String completion_date;
    @JsonProperty("rejection_date")
    private String rejection_date;

//    @JsonProperty("lost_time")
//    private String lost_time;
//    @JsonProperty("won_time")
//    private String won_time;

    public JSONPhase() {

    }

    public Long getV_id() {
        return v_id;
    }

    public void setV_id(Long v_id) {
        this.v_id = v_id;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getInternalValue() {
        return internalValue;
    }

    public void setInternalValue(String internalValue) {
        this.internalValue = internalValue;
    }

    public String getExternalValue() {

        return externalValue;
    }

    public void setExternalValue(String externalValue) {
        this.externalValue = externalValue;
    }

    public String getPersonResponsible() {
        return personResponsible;
    }

    public void setPersonResponsible(String personResponsible) {
        this.personResponsible = personResponsible;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getOfferedDate() {
        return offeredDate;
    }

    public void setOfferedDate(String offeredDate) {
        this.offeredDate = offeredDate;
    }

    public String getSalesStatus() {
        return salesStatus;
    }

    public void setSalesStatus(String salesStatus) {
        this.salesStatus = salesStatus;
    }

    public String getGrantedDate() {
        return grantedDate;
    }

    public void setGrantedDate(String grantedDate) {
        this.grantedDate = grantedDate;
    }

    public String getLostReason() {
        return lostReason;
    }

    public void setLostReason(String lostReason) {
        this.lostReason = lostReason;
    }

    public String getModifiedDate() {
        return modifiedDate;
    }

    public String getPDformatModifiedTime(){
        try {
            String[] dateTime = this.modifiedDate.split("T");
            String date = dateTime[0];
            String time = dateTime[1];
            return date + " " + time;
        } catch (Exception e) {
            return "2000-01-01 00:00:00";
        }
    }

    public void setModifiedDate(String modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

//    public String getLost_time() {
//        return lost_time;
//    }
//
//    public void setLost_time(String lost_time) {
//        this.lost_time = lost_time;
//    }
//
//    public String getWon_time() {
//        return won_time;
//    }
//
//    public void setWon_time(String won_time) {
//        this.won_time = won_time;
//    }

    public String getCompletion_date() {
        return completion_date;
    }

    public void setCompletion_date(String completion_date) {
        this.completion_date = completion_date;
    }

    public String getRejection_date() {
        return rejection_date;
    }

    public void setRejection_date(String rejection_date) {
        this.rejection_date = rejection_date;
    }
}
