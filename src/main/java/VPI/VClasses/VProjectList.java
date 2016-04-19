package VPI.VClasses;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sabu on 19/04/2016.
 */
public class VProjectList {

    @JsonProperty("Items")
    private List<VProject> Items;

    public VProjectList() {
        Items = new ArrayList<>();
    }

    public List<VProject> getItems() {
        return Items;
    }

    public void setItems(List<VProject> items) {
        Items = items;
    }

}
