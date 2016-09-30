package VPI.Entities.util;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * This class represents an update made to either Vertec or Pipedrive
 * Type: type of the request: e.g PUT, POST, DELETE etc.
 * objectType: Organisation, Activity, etc.
 * Name: name of the opbject
 */
public class SyncLog {
    private String type;
    private String objetType;
    private String name;
    private Long vertecId;
    private Long pipedriveId;
    private String timeStamp;

    public SyncLog(String type, String objectType,  String name, Long vertecId, Long pipedriveId, String timeStamp) {
        this.type = type;
        this.objetType = objectType;
        this.name = name;
        this.vertecId = vertecId;
        this.pipedriveId = pipedriveId;
        this.timeStamp = timeStamp;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getVertecId() {
        return vertecId;
    }

    public void setVertecId(Long vertecId) {
        this.vertecId = vertecId;
    }

    public Long getPipedriveId() {
        return pipedriveId;
    }

    public void setPipedriveId(Long pipedriveId) {
        this.pipedriveId = pipedriveId;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getObjetType() {
        return objetType;
    }

    public void setObjetType(String objetType) {
        this.objetType = objetType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return timeStamp + " :: " + type + " :: " + objetType + " :: " + name + " :: " + vertecId + " :: " + pipedriveId + "\n";
    }
}
