package VPI;

/**
 * Created by sabu on 07/04/2016.
 */
public class ErrorResponse extends PDResponse {

    private String error;

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public ErrorResponse() {
    }

    @Override
    public String toString() {
        return "Error response: sucsess=" + super.getSuccess() + ", error=" + error;
    }
}
