package VPI.PDClasses.Contacts;

import VPI.PDClasses.PDAdditionalData;
import VPI.PDClasses.PDResponse;

import java.util.List;

/**
 * Created by gebo on 14/04/2016.
 */
public class PDContactListReceived extends PDResponse {

    private List<PDContactReceived> data;
    private PDAdditionalData additional_data;

    public PDContactListReceived() {
    }

    public List<PDContactReceived> getData() {
        return data;
    }

    public void setData(List<PDContactReceived> data) {
        this.data = data;
    }

    public PDAdditionalData getAdditional_data() {
        return additional_data;
    }

    public void setAdditional_data(PDAdditionalData additional_data) {
        this.additional_data = additional_data;
    }
}
