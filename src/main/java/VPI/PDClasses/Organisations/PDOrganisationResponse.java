package VPI.PDClasses.Organisations;

import VPI.PDClasses.PDResponse;

public class PDOrganisationResponse extends PDResponse {

    private PDOrganisationReceived data;

    public PDOrganisationResponse() {
    }

    public PDOrganisationReceived getData() {
        return data;
    }

    public void setData(PDOrganisationReceived data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Our " + super.getSuccess() + " organisation: " + data;
    }


}
