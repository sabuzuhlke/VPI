package VPI.PDClasses.Activities;

import VPI.PDClasses.PDResponse;

import java.util.List;

/**
 * Created by sabu on 17/05/2016.
 */
public class PDActivityItemsResponse extends PDResponse {

    private List<PDActivity> data;

    public PDActivityItemsResponse() {
    }

    public List<PDActivity> getData() {
        return data;
    }

    public void setData(List<PDActivity> data) {
        this.data = data;
    }
}
