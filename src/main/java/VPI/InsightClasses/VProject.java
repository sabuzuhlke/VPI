package VPI.InsightClasses;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by sabu on 19/04/2016.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class VProject {

    @JsonProperty("Code")
    private String code;

    @JsonProperty("Name")
    private String name;

    @JsonProperty("Customer")
    private VOrganisation organisation;

    @JsonProperty("Id")
    private Long id;

    public VProject() {
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public VOrganisation getOrganisation() {
        return organisation;
    }

    public void setOrganisation(VOrganisation organisation) {
        this.organisation = organisation;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
