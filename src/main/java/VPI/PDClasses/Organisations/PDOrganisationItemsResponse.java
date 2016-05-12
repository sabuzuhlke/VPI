package VPI.PDClasses.Organisations;

import VPI.PDClasses.PDAdditionalData;
import VPI.PDClasses.PDResponse;

import java.util.List;

public class PDOrganisationItemsResponse extends PDResponse {

    private List<PDOrganisationReceived> data;
    private PDAdditionalData additional_data;

    public PDOrganisationItemsResponse() {
        //TODO: change to init list and remove null check elsewhere
        /*
        this.data = new ArrayList<>();
         */
    }

    public List<PDOrganisationReceived> getData() {
        return data;
    }

    public void setData(List<PDOrganisationReceived> data) {
        this.data = data;
    }

    public PDAdditionalData getAdditional_data() {
        return additional_data;
    }

    public void setAdditional_data(PDAdditionalData additional_data) {
        this.additional_data = additional_data;
    }
}
