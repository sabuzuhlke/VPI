package VPI;

/**
 * Created by sabu on 06/04/2016.
 */

public class PDOrganisationResponse extends PDResponse {

    private PDOrganisation data;


    public PDOrganisationResponse(PDOrganisation data) {
        this.data = data;

    }

    public PDOrganisationResponse() {
    }

    public PDOrganisation getData() {
        return data;
    }

    public void setData(PDOrganisation data) {
        this.data = data;
    }


    @Override
    public String toString() {
        return "Our " + super.getSuccess() + " organisation: " + data;
    }


}
