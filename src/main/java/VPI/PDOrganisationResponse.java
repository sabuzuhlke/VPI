package VPI;

/**
 * Created by sabu on 06/04/2016.
 */

public class PDOrganisationResponse extends PDResponse {

    private Organisation data;


    public PDOrganisationResponse(Organisation data) {
        this.data = data;

    }

    public PDOrganisationResponse() {
    }

    public Organisation getData() {
        return data;
    }

    public void setData(Organisation data) {
        this.data = data;
    }


    @Override
    public String toString() {
        return "Our " + super.getSuccess() + " organisation: " + data;
    }


}
