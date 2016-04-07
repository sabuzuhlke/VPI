package VPI;

/**
 * Created by sabu on 06/04/2016.
 */

public class User {

    //private Boolean success;
    private Data data;
    //private AdditionalData additional_data;

    public User() {

    }

    /*public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }*/

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

   /* public AdditionalData getAddData() {
        return additional_data;
    }

    public void setAddData(AdditionalData additionalData) {
        this.additional_data = additionalData;
    }*/

    @Override
    public String toString() {
        return "We got: " + /*success +*/ "  and  "  + data + " and "/* + additional_data*/;
    }
}
