package VPI.PDClasses;

/**
 * Created by sabu on 14/04/2016.
 */
public class PDContactResponse extends PDResponse {

    private PDContactReceived data;

    public PDContactResponse() {
    }

    public PDContactReceived getData() {
        return data;
    }

    public void setData(PDContactReceived data) {
        this.data = data;
    }

}
