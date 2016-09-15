package VPI.PDClasses.Updates;


import java.util.ArrayList;
import java.util.List;

/**
 * This class is used to contain update information from Pipedrive.
 * Sadly, not all updates make it into this list on pipedrive
 */
public class PDLogList {
    List<PDUpdateLog> logs;

    public PDLogList() {
        this.logs = new ArrayList<>();
    }

    public List<PDUpdateLog> getLogs() {
        return logs;
    }

    public void setLogs(List<PDUpdateLog> logs) {
        this.logs = logs;
    }
}
