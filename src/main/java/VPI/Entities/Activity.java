package VPI.Entities;

import VPI.PDClasses.Activities.PDActivityReceived;
import VPI.PDClasses.Activities.PDActivitySend;

import static VPI.Entities.util.Utilities.*;

/**
 * Done field shows weird behaviour due to it being false on vertec even though the activity is done.
 * As a consequence ensure that toPDSend is only called on Activities created from Vertec entries.
 * This does not result in loss of functionality.
 */
public class Activity {
    private Long pipedriveId;
    private Long vertecId;

    private Boolean Done;
    private Boolean active;

    private String pType;
    private String vType;

    private String pDuration;

    private String subject; //Titel on vertec, there is aconversion function called createActivitySubject
    private String text; // note on pd is text prepended with #vid

    private  String dueDate; //yyyy-mm-dd + " " + HH:mm:ss
    private String doneDate; //yyyy-mm-dd + " " + HH:mm:ss
    private String created; //add_time on pd
    private String modified; //update_time on pd


    private Long pipedriveDealLink;
    private Long vertecDealLink;
    private Long vertecProjectLink;

    private Long pipedriveOrganisationLink;
    private Long vertecOrganisationLink;

    private Long pipedriveContactLink;
    private Long vertecContactLink;

    private Long pipedriveAssignee;
    private Long vertecAssignee;

    public Activity(PDActivityReceived act, Long vertecOrganisationLink, Long vertecDealLink, Long vertecProjectLink, Long vertecContactLink, Long vertecAssignee, Long vertecCreator, String vType){

        this.pipedriveId = act.getId();
        this.vertecId = extractVID(act.getNote());

        this.Done = act.getDone();

        this.active = act.getActive_flag();

        this.pType = act.getType();
        this.vType = vType;

        this.pDuration = act.getDuration();
        this.subject = act.getSubject();
        this.text = extractNoteFromNoteWithVID(act.getNote());

        this.dueDate = act.getDue_date() + " " + act.getDue_time();
        this.doneDate = act.getMarked_as_done_time();
        this.created = act.getAdd_time();
        this.modified = act.getUpdate_time();

        this.pipedriveOrganisationLink = act.getOrg_id();
        this.vertecOrganisationLink = vertecOrganisationLink;

        this.pipedriveDealLink = act.getDeal_id();
        this.vertecDealLink = vertecDealLink;
        this.vertecProjectLink = vertecProjectLink;

        this.pipedriveContactLink = act.getPerson_id();
        this.vertecContactLink = vertecContactLink;

        this.pipedriveAssignee = act.getAssigned_to_user_id();
        this.vertecAssignee = vertecAssignee;

    }


    public PDActivitySend toPDSend(){
        PDActivitySend pActivity = new PDActivitySend();

        pActivity.setId(pipedriveId);
        pActivity.setUser_id(pipedriveAssignee);

        pActivity.setType(pType);

        //Set due_date and time
        try{
            if(this.dueDate != null){

                String[] dateFormatter = this.dueDate.split(" ");
                pActivity.setDue_date(dateFormatter[0]); //due_date
                pActivity.setDue_time(dateFormatter[1]); //due_time
            }
        } catch (Exception e){
            System.out.println("Could not split date: " + this.dueDate);
        }

        pActivity.setDuration(pDuration);
        pActivity.setSubject(subject);
        pActivity.setDeal_id(pipedriveDealLink);
        pActivity.setOrg_id(pipedriveOrganisationLink);
        pActivity.setPerson_id(pipedriveContactLink);
        pActivity.setUser_id(pipedriveAssignee);

        pActivity.setNote("V_ID:" + vertecId + "#<br>" + reformatToHtml(text));

        pActivity.setAdd_time(created);


        pActivity.setDone_date(doneDate);
        pActivity.setDone(Done
                || isInThePast(dueDate)
                || isInThePast(doneDate)
                || isInThePast(created));

        return pActivity;
    }


    public Activity() {
    }

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

    public Boolean getDone() {
        return Done;
    }

    public void setDone(Boolean done) {
        this.Done = done;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getpType() {
        return pType;
    }

    public void setpType(String pType) {
        this.pType = pType;
    }

    public String getvType() {
        return vType;
    }

    public void setvType(String vType) {
        this.vType = vType;
    }

    public String getpDuration() {
        return pDuration;
    }

    public void setpDuration(String pDuration) {
        this.pDuration = pDuration;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public String getDoneDate() {
        return doneDate;
    }

    public void setDoneDate(String doneDate) {
        this.doneDate = doneDate;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getModified() {
        return modified;
    }

    public void setModified(String modified) {
        this.modified = modified;
    }

    public Long getPipedriveDealLink() {
        return pipedriveDealLink;
    }

    public void setPipedriveDealLink(Long pipedriveDealLink) {
        this.pipedriveDealLink = pipedriveDealLink;
    }

    public Long getVertecDealLink() {
        return vertecDealLink;
    }

    public void setVertecDealLink(Long vertecDealLink) {
        this.vertecDealLink = vertecDealLink;
    }

    public Long getPipedriveOrganisationLink() {
        return pipedriveOrganisationLink;
    }

    public void setPipedriveOrganisationLink(Long pipedriveOrganisationLink) {
        this.pipedriveOrganisationLink = pipedriveOrganisationLink;
    }

    public Long getVertecOrganisationLink() {
        return vertecOrganisationLink;
    }

    public void setVertecOrganisationLink(Long vertecOrganisationLink) {
        this.vertecOrganisationLink = vertecOrganisationLink;
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

    public Long getPipedriveAssignee() {
        return pipedriveAssignee;
    }

    public void setPipedriveAssignee(Long pipedriveAssignee) {
        this.pipedriveAssignee = pipedriveAssignee;
    }

    public Long getVertecAssignee() {
        return vertecAssignee;
    }

    public void setVertecAssignee(Long vertecAssignee) {
        this.vertecAssignee = vertecAssignee;
    }

    public Long getVertecProjectLink() {
        return vertecProjectLink;
    }

    public void setVertecProjectLink(Long vertecProjectLink) {
        this.vertecProjectLink = vertecProjectLink;
    }
}
