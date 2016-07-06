package VPI.PDClasses.Contacts;

import VPI.PDClasses.PDAdditionalData;
import VPI.PDClasses.PDResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

public class PDContactListReceived extends PDResponse {

    private List<PDContactReceived> data;
    private PDAdditionalData additional_data;

    public PDContactListReceived() {
    }

    public List<PDContactReceived> getData() {
        return data;
    }

    public void setData(List<PDContactReceived> data) {
        this.data = data;
    }

    public PDAdditionalData getAdditional_data() {
        return additional_data;
    }

    public void setAdditional_data(PDAdditionalData additional_data) {
        this.additional_data = additional_data;
    }

    public String toPrettyJSON(){
        String retStr = null;
        ObjectMapper m = new ObjectMapper();
        try{

            retStr = m.writerWithDefaultPrettyPrinter().writeValueAsString(this);
        }
        catch(Exception e){
            System.out.println("Could not print PDActivityReceived: " + e.toString());
        }
        return retStr;
    }
}
