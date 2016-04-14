package VPI.PDClasses;

import java.util.List;

/**
 * Created by gebo on 14/04/2016.
 */
public class PDContactsForOrganisation extends PDResponse {

    private List<PDContactResponse> data;

    public PDContactsForOrganisation() {
    }

    public List<PDContactResponse> getData() {
        return data;
    }

    public void setData(List<PDContactResponse> data) {
        this.data = data;
    }
}
