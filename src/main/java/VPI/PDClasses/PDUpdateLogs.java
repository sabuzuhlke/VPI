package VPI.PDClasses;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gebo on 26/08/2016.
 */
public class PDUpdateLogs extends PDResponse {
    private List<PDUpdate> PDUpdates;

    public PDUpdateLogs() {
        PDUpdates = new ArrayList<>();
    }

    public List<PDUpdate> getPDUpdates() {
        return PDUpdates;
    }

    public void setPDUpdates(List<PDUpdate> PDUpdates) {
        this.PDUpdates = PDUpdates;
    }
}
