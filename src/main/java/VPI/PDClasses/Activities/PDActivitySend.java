package VPI.PDClasses.Activities;

import VPI.VertecClasses.VertecActivities.JSONActivity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

public class PDActivitySend {

    @JsonIgnore
    private Long id;
    private Boolean done;
    private String type;
    private String due_date;
    private String due_time;
    private String duration;
    private String subject;
    private Long deal_id;
    private Long org_id;
    private Long person_id;
    private String note;
    private Long user_id;
    @JsonProperty("marked_as_done_time")
    private String done_date;

    public PDActivitySend() {
    }

    public PDActivitySend(JSONActivity a, Long user_id, Long contact_id, Long ord_id, Long deal_id, String type) {
        this.done = a.getDone();
        this.type = type;
        this.subject = a.getTitle();
        this.deal_id = deal_id;
        this.org_id = ord_id;
        this.person_id = contact_id;
        this.note = "V_ID:" + a.getId() + "#\n" + a.getText(); //TODO: make this hack known
        this.user_id = user_id;
        this.done_date = a.getDone_date();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getDone() {
        return done;
    }

    public void setDone(Boolean done) {
        this.done = done;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDue_date() {
        return due_date;
    }

    public void setDue_date(String due_date) {
        this.due_date = due_date;
    }

    public String getDue_time() {
        return due_time;
    }

    public void setDue_time(String due_time) {
        this.due_time = due_time;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public Long getDeal_id() {
        return deal_id;
    }

    public void setDeal_id(Long deal_id) {
        this.deal_id = deal_id;
    }

    public Long getOrg_id() {
        return org_id;
    }

    public void setOrg_id(Long org_id) {
        this.org_id = org_id;
    }

    public Long getPerson_id() {
        return person_id;
    }

    public void setPerson_id(Long person_id) {
        this.person_id = person_id;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Long getUser_id() {
        return user_id;
    }

    public String getDone_date() {
        return done_date;
    }

    public void setDone_date(String done_date) {
        this.done_date = done_date;
    }

    public void setUser_id(Long user_id) {
        this.user_id = user_id;
    }

    public String toPrettyJSON() {
        ObjectMapper m = new ObjectMapper();
        try {
            return m.writerWithDefaultPrettyPrinter().writeValueAsString(this);
        } catch (Exception e) {
            System.out.println("Couldnt marshall activity send to json for printing");
            return subject + note + done + user_id +deal_id +org_id +person_id;
        }
    }
}
