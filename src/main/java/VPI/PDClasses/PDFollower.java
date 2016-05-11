package VPI.PDClasses;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by gebo on 11/05/2016.
 */
public class PDFollower {
    @JsonProperty("id")
    private Long contactID;

    @JsonProperty("user_id")
    private Long userID;

    public PDFollower() {
    }

    public PDFollower(Long contactID, Long userID) {
        this.contactID = contactID;
        this.userID = userID;
    }

    public Long getUserID() {
        return userID;
    }

    public void setUserID(Long userID) {
        this.userID = userID;
    }

    public Long getContactID() {
        return contactID;
    }

    public void setContactID(Long contactID) {
        this.contactID = contactID;
    }
}
