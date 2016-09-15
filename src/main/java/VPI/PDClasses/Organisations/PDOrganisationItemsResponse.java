package VPI.PDClasses.Organisations;

import VPI.PDClasses.PDAdditionalData;
import VPI.PDClasses.PDResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

public class PDOrganisationItemsResponse extends PDResponse {

    private List<PDOrganisationReceived> data;
    private PDAdditionalData additional_data;

    public PDOrganisationItemsResponse() {
        //TODO: change to init list and remove null check elsewhere
    }

    public List<PDOrganisationReceived> getData() {
        return data;
    }

    public void setData(List<PDOrganisationReceived> data) {
        this.data = data;
    }

    /**
     * When asking for all Organisations only 500 results are returned with each response, and the additional data field sais,
     * whether there are more organisations on PD that were not returned.
     */

    public PDAdditionalData getAdditional_data() {
        return additional_data;
    }

    public void setAdditional_data(PDAdditionalData additional_data) {
        this.additional_data = additional_data;
    }
    @Override
    public String toString(){
        String retStr = null;
        ObjectMapper m = new ObjectMapper();
        try{

            retStr = m.writerWithDefaultPrettyPrinter().writeValueAsString(this);
        }
        catch(Exception e){
            System.out.println("Could not convert Organisation Items Response to JSON: " + e.toString());
        }
        return retStr;
    }
}
