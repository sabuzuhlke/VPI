package VPI.PDClasses;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Created by gebo on 11/05/2016.
 */
public class PDFollower {

    @JsonProperty("id")
    private Long objectID;
    @JsonProperty("user_id")
    private Long userID;

    public PDFollower() {
    }

    public PDFollower(Long objectID, Long userID) {
        this.objectID = objectID;
        this.userID = userID;
    }

    public Long getUserID() {
        return userID;
    }

    public void setUserID(Long userID) {
        this.userID = userID;
    }

    public Long getObjectID() {
        return objectID;
    }

    public void setObjectID(Long objectID) {
        this.objectID = objectID;
    }

    public String toPrettyJSON() {
        String retStr = null;
        ObjectMapper m = new ObjectMapper();
        try{
            retStr = m.writerWithDefaultPrettyPrinter().writeValueAsString(this);
        }
        catch(Exception e){
            System.out.println("Could not display Follower Relation as a pretty string: " + e.toString());
        }
        return retStr;
    }
}
