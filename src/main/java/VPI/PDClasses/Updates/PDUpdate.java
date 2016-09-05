package VPI.PDClasses.Updates;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;

@JsonIgnoreProperties(ignoreUnknown = true)

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
