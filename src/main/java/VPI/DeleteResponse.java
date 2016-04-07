package VPI;

/**
 * Created by sabu on 07/04/2016.
 */
public class DeleteResponse extends PDResponse{

    private ID data;

    public DeleteResponse() {

    }

    public ID getData() {
        return data;
    }

    public void setData(ID data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "DELETE RESPONSE: Success " + super.getSuccess() + ", id " + data;
    }
}
