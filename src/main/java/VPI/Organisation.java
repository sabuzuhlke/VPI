package VPI;

/**
 * Created by sabu on 06/04/2016.
 */

public class Organisation extends PDResponse {

    private OrgData data;

    public Organisation(OrgData data) {
        this.data = data;

    }

    public Organisation() {
    }

    public OrgData getData() {
        return data;
    }

    public void setData(OrgData data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Our " + super.getSuccess() + " organisation: " + data;
    }
}
