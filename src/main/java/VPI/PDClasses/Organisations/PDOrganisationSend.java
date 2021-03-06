package VPI.PDClasses.Organisations;

import VPI.InsightClasses.VOrganisation;
import VPI.VertecClasses.VertecOrganisations.JSONOrganisation;
import com.fasterxml.jackson.annotation.JsonProperty;

public class PDOrganisationSend {

    private String name;
    private Integer visible_to;
    private String address;
    private Boolean active_flag;
    private Long id;
    private Long owner_id;
    @JsonProperty("add_time")
    private String creationTime;
    @JsonProperty("8bbe384141fa3df043ae029d8a15fdad1dbf3ea0")//"1fdff908db3cffe4c92b93353cfd56219745619e")//"2388ef6b01b0ff49893c6f954ebfb162a70b12d2")
    private Long v_id;
    @JsonProperty("0fac9a781075943b61cf730f5434a835d0c68a2a")//"bf75945461cae2a672c4404b85b1bc8a4d5c1ba9")//"276ed9c14c8766ac63ab668678b779a9b813658b")
    private String ownedBy;
    @JsonProperty("4d320823bca5075a18070cfce737c0d96cc2191b")
    private String website;

    /**
     * RestTemplate, and testing
     */
    public PDOrganisationSend() {
        this.visible_to = 3;
        this.active_flag = true;
    }


    /**
     * Used to build organisations for putlist
     * @param jo is org recieved from vertec
     * @param po id org received from pipedrive
     * @param ownerId is pipedrive owner id as from external map
     */
    public PDOrganisationSend(JSONOrganisation jo, PDOrganisationReceived po, Long ownerId){
        this.name = jo.getName();
        this.visible_to = 3;
        this.v_id = jo.getObjid();
        this.id = po.getId();
        this.active_flag = true;
        this.owner_id = ownerId;

        this.website = jo.getWebsite();


        this.address = jo.getFormattedAddress();

        try{
            if(jo.getCreationTime() != null){
                if (jo.getCreationTime().contains("1900-01-01")) {
                    this.creationTime = "1900-01-01 00:00:00";
                } else {
                    String[] dateFormatter = jo.getCreationTime().split("T");
                    String date = dateFormatter[0];
                    String time = dateFormatter[1];
                    this.creationTime = date + " " + time;
                }
            }
        } catch (Exception e){
            System.out.println("Exception while creating pdorgsend from JSONorg: " + e);
            System.out.println("Name: " + this.name + " VId: " + this.v_id);
            System.out.println(jo.getCreationTime());
        }

        if(jo.getOwnedByTeam()) this.ownedBy = "ZUK";
        else this.ownedBy = "Not ZUK";
    }

    /**
     * Used to build organistations for postList
     * @param o is org recieved from vertec
     * @param owner_id is pipedrive owner id as from external map
     */
    public PDOrganisationSend(JSONOrganisation o, Long owner_id){
        this.name = o.getName();
        if (name == null || name.isEmpty() || name.equals(" ")) {
            name = "Anonymous co";
        }
        this.visible_to = 3;
        this.v_id = o.getObjid();
        this.active_flag = true;
        this.address = o.getFormattedAddress();
        this.website = o.getWebsite();
        if(o.getCreationTime() != null){
            if (o.getCreationTime().contains("1900-01-01")) {
                this.creationTime = "1900-01-01 00:00:00";
            } else {
                String[] dateFormatter = o.getCreationTime().split("T");
                String date = dateFormatter[0];
                String time = dateFormatter[1];
                this.creationTime = date + " " + time;
            }
        } else {
            System.out.println(o.toPrettyJSON());
        }

        this.owner_id = owner_id;

        if(o.getOwnedByTeam()) this.ownedBy = "ZUK";
        else this.ownedBy = "Not ZUK";
    }

    public Long getV_id() {
        return v_id;
    }

    public void setV_id(Long v_id) {
        this.v_id = v_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getVisible_to() {
        return visible_to;
    }

    public void setVisible_to(Integer visible_to) {
        this.visible_to = visible_to;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Boolean getActive_flag() {
        return active_flag;
    }

    public void setActive_flag(Boolean active_flag) {
        this.active_flag = active_flag;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOwner_id() {
        return owner_id;
    }

    public void setOwner_id(Long owner_id) {
        this.owner_id = owner_id;
    }

    public String getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(String creationTime) {
        this.creationTime = creationTime;
    }

    public String getOwnedBy() {
        return ownedBy;
    }

    public void setOwnedBy(String ownedBy) {
        this.ownedBy = ownedBy;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    @Override
    public String toString() {
        return "Company: ID: " + id + " Name: " + name  + " visible to: " + visible_to + ", Address: " + address + " Active: " + active_flag;
    }

    /**
     * Only used in testing, should remove and replace with different constructor
     */
    public PDOrganisationSend(String name, Integer visible_to) {
        this.name = name;
        this.visible_to = visible_to;
        this.active_flag = true;
    }


    /**
     * Only used in old code
     */
    public PDOrganisationSend(PDOrganisationReceived o) {
        this.name = o.getName();
        this.visible_to = o.getVisible_to();
        this.address = o.getAddress();
        this.active_flag = true;
        this.id = o.getId();
        this.owner_id = o.getOwner_id().getId();
        this.v_id = o.getV_id();
        this.creationTime = o.getCreationTime();
    }

    /**
     * Only used in old code
     */
    public PDOrganisationSend(VOrganisation c) {
        this.name = c.getName();
        this.visible_to = 3;
        this.active_flag = true;
        this.address = c.getFormattedAddress();
        this.v_id = c.getId();
    }

}
