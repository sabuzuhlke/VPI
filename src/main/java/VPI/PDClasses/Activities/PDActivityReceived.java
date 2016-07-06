package VPI.PDClasses.Activities;

public class PDActivityReceived {

    private Long id;
    private Long user_id;
    private Boolean done;
    private String type;
    private String due_date;
    private String due_time;
    private String duration;
    private String add_time;
    private String marked_as_done_time;
    private String subject;
    private Long deal_id;
    private Long org_id;
    private Long person_id;
    private Boolean active_flag;
    private String update_time;
    private String note;
    private Long assigned_to_user_id;
    private Long created_by_user_id;
    private String done_date;

    public PDActivityReceived() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUser_id() {
        return user_id;
    }

    public void setUser_id(Long user_id) {
        this.user_id = user_id;
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

    public String getAdd_time() {
        return add_time;
    }

    public void setAdd_time(String add_time) {
        this.add_time = add_time;
    }

    public String getMarked_as_done_time() {
        return marked_as_done_time;
    }

    public void setMarked_as_done_time(String marked_as_done_time) {
        this.marked_as_done_time = marked_as_done_time;
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

    public Boolean getActive_flag() {
        return active_flag;
    }

    public void setActive_flag(Boolean active_flag) {
        this.active_flag = active_flag;
    }

    public String getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(String update_time) {
        this.update_time = update_time;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Long getAssigned_to_user_id() {
        return assigned_to_user_id;
    }

    public void setAssigned_to_user_id(Long assigned_to_user_id) {
        this.assigned_to_user_id = assigned_to_user_id;
    }

    public Long getCreated_by_user_id() {
        return created_by_user_id;
    }

    public void setCreated_by_user_id(Long created_by_user_id) {
        this.created_by_user_id = created_by_user_id;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getDone_date() {
        return done_date;
    }

    public void setDone_date(String done_date) {
        this.done_date = done_date;
    }

}
