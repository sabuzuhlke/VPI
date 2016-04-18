package VPI.VClasses;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by gebo on 14/04/2016.
 */
public class VContact {
    @JsonProperty("Id")
    private Long id;

    @JsonProperty("Full Name")
    private String name;

    @JsonIgnore
    private Long org_id;

    public VContact() {
    }

    public Long getOrg_id() {
        return org_id;
    }

    public void setOrg_id(Long org_id) {
        this.org_id = org_id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
