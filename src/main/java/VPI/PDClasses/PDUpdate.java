package VPI.PDClasses;

/**
 * Created by gebo on 26/08/2016.
 */
public class PDUpdate {
    private String object;
    private String timestamp;
    private PDUpdateData data;

    public PDUpdate() {
    }

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public PDUpdateData getData() {
        return data;
    }

    public void setData(PDUpdateData data) {
        this.data = data;
    }
}
