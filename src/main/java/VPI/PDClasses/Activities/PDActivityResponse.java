package VPI.PDClasses.Activities;

import VPI.PDClasses.PDResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Created by sabu on 17/05/2016.
 */
public class PDActivityResponse extends PDResponse {

    private PDActivityReceived data;

    public PDActivityResponse() {
    }

    public PDActivityReceived getData() {
        return data;
    }

    public void setData(PDActivityReceived data) {
        this.data = data;
    }

    public String toPrettyString() {
        String retStr = null;
        ObjectMapper m = new ObjectMapper();
        try{
            retStr = m.writerWithDefaultPrettyPrinter().writeValueAsString(this);
        }
        catch(Exception e){
            System.out.println("Could not convert XML Envelope to JSON: " + e.toString());
        }
        return retStr;
    }
}
