package VPI.PDClasses.HierarchyClasses;


import com.fasterxml.jackson.annotation.JsonProperty;


public class LinkedOrg {
    @JsonProperty("name")
    private String name;
    @JsonProperty("owner_id")
    private Long ownerId;
    @JsonProperty("address")
    private String address;
    @JsonProperty("value")
    private Long id;

    public LinkedOrg() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}

