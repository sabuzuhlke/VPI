package VPI.PDClasses;

import java.util.List;

/**
 * Created by gebo on 14/04/2016.
 */
public class PDContactListReceived extends PDResponse {

    private List<PDContactReceived> data;

    public PDContactListReceived() {
    }

    public List<PDContactReceived> getData() {
        return data;
    }

    public void setData(List<PDContactReceived> data) {
        this.data = data;
    }
}
