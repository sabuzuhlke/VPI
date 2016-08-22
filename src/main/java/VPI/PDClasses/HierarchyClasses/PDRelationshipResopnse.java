package VPI.PDClasses.HierarchyClasses;

import VPI.PDClasses.PDResponse;

import java.util.ArrayList;
import java.util.List;


public class PDRelationshipResopnse extends PDResponse {
    private List<PDRelationshipReceived> data;

    public PDRelationshipResopnse() {
        data = new ArrayList<>();
    }

    public List<PDRelationshipReceived> getData() {
        return data;
    }

    public void setData(List<PDRelationshipReceived> data) {
        this.data = data;
    }
}
