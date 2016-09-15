package VPI.PDClasses;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * All information returned by pipedrive extend this class. All the PDxxxReceived and PDxxxSend classes
 * serve as containers, so that Springs rest template's build in JSON parser can easily convert these objects to JSON.
 * We need to differenciate between PDxxxReceived and PDxxxSend as, Pipedrive's response structure differs from the
 * requests' it expects.
 * All PDResponses contain a success field, as well as a data field, that has a different type for each type of request
 * /Organisation, Contact etc./
 * They also contain an additionalData field, that we ose only in certain circumstances.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PDResponse {

    private Boolean success;

    public PDResponse() {}

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }
}
