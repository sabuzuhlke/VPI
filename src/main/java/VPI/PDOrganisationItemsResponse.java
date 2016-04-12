package VPI;

import java.util.List;

/**
 * Created by gebo on 12/04/2016.
 */
public class PDOrganisationItemsResponse extends PDResponse{

    private List<PDOrganisation> data;
    private PDAdditionalData additional_data;

    public PDOrganisationItemsResponse() {
    }

    public List<PDOrganisation> getData() {
        return data;
    }

    public void setData(List<PDOrganisation> data) {
        this.data = data;
    }

    public PDAdditionalData getAdditional_data() {
        return additional_data;
    }

    public void setAdditional_data(PDAdditionalData additional_data) {
        this.additional_data = additional_data;
    }
}
