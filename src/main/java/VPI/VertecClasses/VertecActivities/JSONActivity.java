package VPI.VertecClasses.VertecActivities;

/**
 * Created by sabu on 24/05/2016.
 */
public class JSONActivity {

    private Long id;//
    private String title;//
    private String text;//
    private String assignee;//
    private Long customer_link;// //org or contact
    private Boolean done;//
    private Long project_phase_link;//
    private Long project_link;
    private String date;
    private String type;//
    private String done_date;
    private String creation_date_time;

    public JSONActivity() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getAssignee() {
        return assignee;
    }

    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }

    public Long getCustomer_link() {
        return customer_link;
    }

    public void setCustomer_link(Long customer_link) {
        this.customer_link = customer_link;
    }

    public Boolean getDone() {
        return done;
    }

    public void setDone(Boolean done) {
        this.done = done;
    }

    public Long getProject_phase_link() {
        return project_phase_link;
    }

    public void setProject_phase_link(Long project_phase_link) {
        this.project_phase_link = project_phase_link;
    }

    public Long getProject_link() {
        return project_link;
    }

    public void setProject_link(Long project_link) {
        this.project_link = project_link;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDone_date() {
        return done_date;
    }

    public void setDone_date(String done_date) {
        this.done_date = done_date;
    }

    public String getCreation_date_time() {
        return creation_date_time;
    }

    public void setCreation_date_time(String creation_date_time) {
        this.creation_date_time = creation_date_time;
    }

    public String getFormattedDone_date(){
        try{
            if(this.done_date != null){
                if (this.done_date.contains("1900-01-01")) {
                    return "1900-01-01 00:00:00";
                } else {
                    String[] dateFormatter = this.done_date.split("T");
                    String date = dateFormatter[0];
                    String time = dateFormatter[1];
                    return date + " " + time;
                }
            }
        } catch (Exception e){
            System.out.println("Could not split done date of activity: " + this.getTitle());
        }
        return null;
    }
}
