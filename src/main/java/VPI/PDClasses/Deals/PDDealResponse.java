package VPI.PDClasses.Deals;

import VPI.PDClasses.PDResponse;

/**
 * Created by sabu on 12/05/2016.
 */
public class PDDealResponse extends PDResponse{

    private PDDealReceived data;

    public PDDealResponse() {
    }

    public PDDealReceived getData() {
        return data;
    }

    public void setData(PDDealReceived data) {
        this.data = data;
    }

    @Override
    public String toString(){
        return data.toString();
    }
}

