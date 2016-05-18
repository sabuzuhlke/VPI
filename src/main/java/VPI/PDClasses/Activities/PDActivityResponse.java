package VPI.PDClasses.Activities;

import VPI.PDClasses.PDResponse;

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
}
