package VPI.PDClasses.Activities;

import VPI.PDClasses.PDResponse;

/**
 * Created by sabu on 17/05/2016.
 */
public class PDActivityResponse extends PDResponse {

    private PDActivity data;

    public PDActivityResponse() {
    }

    public PDActivity getData() {
        return data;
    }

    public void setData(PDActivity data) {
        this.data = data;
    }
}
