package VPI.PDClasses.Deals;

import VPI.PDClasses.PDAdditionalData;
import VPI.PDClasses.PDResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sabu on 12/05/2016.
 */
public class PDDealItemsResponse extends PDResponse {

    private List<PDDealReceived> data;
    private PDAdditionalData additional_data;

    public PDDealItemsResponse() {
        this.data = new ArrayList<>();
    }

    public List<PDDealReceived> getData() {
        return data;
    }

    public void setData(List<PDDealReceived> data) {
        this.data = data;
    }

    public PDAdditionalData getAdditional_data() {
        return additional_data;
    }

    public void setAdditional_data(PDAdditionalData additional_data) {
        this.additional_data = additional_data;
    }
}
