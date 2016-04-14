package VPI.VClasses;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by gebo on 14/04/2016.
 */
public class VContact {
    @JsonProperty("Id")
    private Long id;

    @JsonProperty("Full Name")
    private String name;

    public VContact() {
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
