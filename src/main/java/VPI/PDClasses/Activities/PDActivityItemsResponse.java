package VPI.PDClasses.Activities;

import VPI.PDClasses.PDAdditionalData;
import VPI.PDClasses.PDResponse;

import java.util.List;

/**
 * Created by sabu on 17/05/2016.
 */
public class PDActivityItemsResponse extends PDResponse {

    private List<PDActivityReceived> data;
    private PDAdditionalData additional_data;

    public PDActivityItemsResponse() {
    }

    public List<PDActivityReceived> getData() {
        return data;
    }

    public void setData(List<PDActivityReceived> data) {
        this.data = data;
    }

    public PDAdditionalData getAdditional_data() {
        return additional_data;
    }

    public void setAdditional_data(PDAdditionalData additional_data) {
        this.additional_data = additional_data;
    }
}
