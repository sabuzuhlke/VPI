package VPI;

/**
 * Created by gebo on 08/04/2016.
 */
public class PDDeleteResponse extends PDResponse{

    private PDDeleteResponseId data;

    public PDDeleteResponse(PDDeleteResponseId data) {
        this.data = data;
    }

    public PDDeleteResponse() {
    }


    public PDDeleteResponseId getData() {
        return data;
    }

    public void setData(PDDeleteResponseId data) {
        this.data = data;
    }
}
