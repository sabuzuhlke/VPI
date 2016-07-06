package VPI.PDClasses.Users;

import VPI.PDClasses.PDResponse;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PDUserItemsResponse extends PDResponse {

    private List<PDUser> data;

    public PDUserItemsResponse() {
    }

    public List<PDUser> getData() {
        return data;
    }

    public void setData(List<PDUser> data) {
        this.data = data;
    }
}
