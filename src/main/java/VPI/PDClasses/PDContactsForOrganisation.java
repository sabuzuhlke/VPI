package VPI.PDClasses;

import java.util.List;

/**
 * Created by gebo on 14/04/2016.
 */
public class PDContactsForOrganisation extends PDResponse {

    private List<PDContactReceived> data;

    public PDContactsForOrganisation() {
    }

    public List<PDContactReceived> getData() {
        return data;
    }

    public void setData(List<PDContactReceived> data) {
        this.data = data;
    }
}
