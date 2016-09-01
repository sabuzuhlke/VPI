package VPI.PDClasses;

/**
 * Created by gebo on 26/08/2016.
 */
public class PDUpdateData {
    private Long id;
    private Long item_id;
    private Long user_id;
    private String log_time;

    public PDUpdateData() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getItem_id() {
        return item_id;
    }

    public void setItem_id(Long item_id) {
        this.item_id = item_id;
    }

    public Long getUser_id() {
        return user_id;
    }

    public void setUser_id(Long user_id) {
        this.user_id = user_id;
    }

    public String getLog_time() {
        return log_time;
    }

    public void setLog_time(String log_time) {
        this.log_time = log_time;
    }
}
