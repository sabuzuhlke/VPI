package VPI.VertecClasses.VertecActivities;

import com.fasterxml.jackson.databind.ObjectMapper;

public class Activity {
    private Long vertecId;
    private Boolean done;

    private String vType;

    private String subject; //Titel on vertec, there is aconversion function called createActivitySubject
    private String text; // note on pd is text prepended with #vid

    private  String dueDate; //yyyy-mm-dd + " " + HH:mm:ss
    private String doneDate; //yyyy-mm-dd + " " + HH:mm:ss
    private String created; //add_time on pd
    private String modified; //update_time on pd

    private Long vertecDealLink;
    private Long vertecProjectLink;
    private Long vertecOrganisationLink;
    private Long vertecContactLink;

    private Long vertecAssignee;


    public Activity() {
    }


    public Long getVertecProjectLink() {
        return vertecProjectLink;
    }

    public void setVertecProjectLink(Long vertecProjectLink) {
        this.vertecProjectLink = vertecProjectLink;
    }

    public Long getVertecId() {
        return vertecId;
    }

    public void setVertecId(Long vertecId) {
        this.vertecId = vertecId;
    }

    public Boolean getDone() {
        return done;
    }

    public void setDone(Boolean done) {
        this.done = done;
    }

    public String getvType() {
        return vType;
    }

    public void setvType(String vType) {
        this.vType = vType;
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

    public Long getVertecDealLink() {
        return vertecDealLink;
    }

    public void setVertecDealLink(Long vertecDealLink) {
        this.vertecDealLink = vertecDealLink;
    }

    public Long getVertecOrganisationLink() {
        return vertecOrganisationLink;
    }

    public void setVertecOrganisationLink(Long vertecOrganisationLink) {
        this.vertecOrganisationLink = vertecOrganisationLink;
    }

    public Long getVertecContactLink() {
        return vertecContactLink;
    }

    public void setVertecContactLink(Long vertecContactLink) {
        this.vertecContactLink = vertecContactLink;
    }

    public Long getVertecAssignee() {
        return vertecAssignee;
    }

    public void setVertecAssignee(Long vertecAssignee) {
        this.vertecAssignee = vertecAssignee;
    }

    @Override
    public String toString(){
        String retStr = null;
        ObjectMapper m = new ObjectMapper();
        try{

            retStr = m.writerWithDefaultPrettyPrinter().writeValueAsString(this);
        }
        catch(Exception e){
            System.out.println("Could not convert to JSON: " + e.toString());
        }
        return retStr;
    }
}
