package VPI.PDClasses.Contacts;

import com.fasterxml.jackson.annotation.JsonProperty;

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
}
