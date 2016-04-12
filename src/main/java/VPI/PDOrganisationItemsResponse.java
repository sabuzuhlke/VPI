package VPI;

import java.util.List;

/**
 * Created by gebo on 12/04/2016.
 */
public class PDOrganisationItemsResponse {
    private List<Organisation> items;
    private PDAdditionalData additional_data;

    public PDOrganisationItemsResponse() {
    }

    public List<Organisation> getItems() {
        return items;
    }

    public void setItems(List<Organisation> items) {
        this.items = items;
    }

    public PDAdditionalData getAdditional_data() {
        return additional_data;
    }

    public void setAdditional_data(PDAdditionalData additional_data) {
        this.additional_data = additional_data;
    }
}
