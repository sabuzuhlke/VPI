package VPI.Entities;

//TODO Once needed add conversion functions to/from vertec and pipedrive

/**
 * there are predifined conversion functions used to convert vertec object into pd objects
 * Fields to watch out for:
 *  -   pTitle
 *  -   pStageId
 */
public class Deal {
    private Long pipedriveId;
    private Long vertecId;

    private String pTitle;

    private String value;
    private String currency;

    private String ownerEmail;
    private String pCreatorEmail;

    private Long pipedriveContactLink;
    private Long vertecContactLink;

    private Long pipedriveOrgLink;
    private Long vertecOrgLink;

    private int pStageId;

    private String lostReason;
    private String status;
    private String creationTime; //"add_time" on pipedrive, "creationDate" from Vertec
    private String modifiedDate;
    private Integer visible_to = 3;

    private String plostTime;
    private String pwonTime;
    private String expectedCloseDate;

    private String vOfferedDate;


    private String lead_type;
    private String projectNumber;
    private String phaseCode;

    private Long v_id;

    private Boolean active;

    private int pipelineId; //might not be used

    private String vProjectTitle; // used for pTitle
    private String vProjectCode;
    private String vAccountManagerEmail;

    private String vPhaseDescription; // used for pTitle
    private String vSalesStatus;
    private String vCompletionDate;
    private String vrejectionDate;

    public Deal(){}

    public Long getPipedriveId() {
        return pipedriveId;
    }

    public void setPipedriveId(Long pipedriveId) {
        this.pipedriveId = pipedriveId;
    }

    public Long getVertecId() {
        return vertecId;
    }

    public void setVertecId(Long vertecId) {
        this.vertecId = vertecId;
    }

    public String getpTitle() {
        return pTitle;
    }

    public void setpTitle(String pTitle) {
        this.pTitle = pTitle;
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

    public String getOwnerEmail() {
        return ownerEmail;
    }

    public void setOwnerEmail(String ownerEmail) {
        this.ownerEmail = ownerEmail;
    }

    public String getpCreatorEmail() {
        return pCreatorEmail;
    }

    public void setpCreatorEmail(String pCreatorEmail) {
        this.pCreatorEmail = pCreatorEmail;
    }

    public Long getPipedriveContactLink() {
        return pipedriveContactLink;
    }

    public void setPipedriveContactLink(Long pipedriveContactLink) {
        this.pipedriveContactLink = pipedriveContactLink;
    }

    public Long getVertecContactLink() {
        return vertecContactLink;
    }

    public void setVertecContactLink(Long vertecContactLink) {
        this.vertecContactLink = vertecContactLink;
    }

    public Long getPipedriveOrgLink() {
        return pipedriveOrgLink;
    }

    public void setPipedriveOrgLink(Long pipedriveOrgLink) {
        this.pipedriveOrgLink = pipedriveOrgLink;
    }

    public Long getVertecOrgLink() {
        return vertecOrgLink;
    }

    public void setVertecOrgLink(Long vertecOrgLink) {
        this.vertecOrgLink = vertecOrgLink;
    }

    public int getpStageId() {
        return pStageId;
    }

    public void setpStageId(int pStageId) {
        this.pStageId = pStageId;
    }

    public String getLostReason() {
        return lostReason;
    }

    public void setLostReason(String lostReason) {
        this.lostReason = lostReason;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(String creationTime) {
        this.creationTime = creationTime;
    }

    public String getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(String modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public Integer getVisible_to() {
        return visible_to;
    }

    public void setVisible_to(Integer visible_to) {
        this.visible_to = visible_to;
    }

    public String getPlostTime() {
        return plostTime;
    }

    public void setPlostTime(String plostTime) {
        this.plostTime = plostTime;
    }

    public String getPwonTime() {
        return pwonTime;
    }

    public void setPwonTime(String pwonTime) {
        this.pwonTime = pwonTime;
    }

    public String getExpectedCloseDate() {
        return expectedCloseDate;
    }

    public void setExpectedCloseDate(String expectedCloseDate) {
        this.expectedCloseDate = expectedCloseDate;
    }

    public String getvOfferedDate() {
        return vOfferedDate;
    }

    public void setvOfferedDate(String vOfferedDate) {
        this.vOfferedDate = vOfferedDate;
    }

    public String getLead_type() {
        return lead_type;
    }

    public void setLead_type(String lead_type) {
        this.lead_type = lead_type;
    }

    public String getProjectNumber() {
        return projectNumber;
    }

    public void setProjectNumber(String projectNumber) {
        this.projectNumber = projectNumber;
    }

    public String getPhaseCode() {
        return phaseCode;
    }

    public void setPhaseCode(String phaseCode) {
        this.phaseCode = phaseCode;
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

    public int getPipelineId() {
        return pipelineId;
    }

    public void setPipelineId(int pipelineId) {
        this.pipelineId = pipelineId;
    }

    public String getvProjectTitle() {
        return vProjectTitle;
    }

    public void setvProjectTitle(String vProjectTitle) {
        this.vProjectTitle = vProjectTitle;
    }

    public String getvProjectCode() {
        return vProjectCode;
    }

    public void setvProjectCode(String vProjectCode) {
        this.vProjectCode = vProjectCode;
    }

    public String getvAccountManagerEmail() {
        return vAccountManagerEmail;
    }

    public void setvAccountManagerEmail(String vAccountManagerEmail) {
        this.vAccountManagerEmail = vAccountManagerEmail;
    }

    public String getvPhaseDescription() {
        return vPhaseDescription;
    }

    public void setvPhaseDescription(String vPhaseDescription) {
        this.vPhaseDescription = vPhaseDescription;
    }

    public String getvSalesStatus() {
        return vSalesStatus;
    }

    public void setvSalesStatus(String vSalesStatus) {
        this.vSalesStatus = vSalesStatus;
    }

    public String getvCompletionDate() {
        return vCompletionDate;
    }

    public void setvCompletionDate(String vCompletionDate) {
        this.vCompletionDate = vCompletionDate;
    }

    public String getVrejectionDate() {
        return vrejectionDate;
    }

    public void setVrejectionDate(String vrejectionDate) {
        this.vrejectionDate = vrejectionDate;
    }
}
