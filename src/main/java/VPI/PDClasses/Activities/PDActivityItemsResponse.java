package VPI.PDClasses.Activities;

import VPI.PDClasses.PDResponse;

import java.util.List;

/**
 * Created by sabu on 17/05/2016.
 */
public class PDActivityItemsResponse extends PDResponse {

    private List<PDActivityReceived> data;

    public PDActivityItemsResponse() {
    }

    public List<PDActivityReceived> getData() {
        return data;
    }

    public void setData(List<PDActivityReceived> data) {
        this.data = data;
    }
}
