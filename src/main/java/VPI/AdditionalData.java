package VPI;

/**
 * Created by sabu on 06/04/2016.
 */

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AdditionalData {

    private Long companyId;

    public AdditionalData() {

    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    @Override
    public String toString() {
        return "additional data: companyId = " + companyId;
    }

}
