package VPI.PDClasses.Organisations;

import VPI.InsightClasses.VOrganisation;
import VPI.Keys.DevelopmentKeys;
import VPI.Keys.ProductionKeys;
import VPI.VertecClasses.VertecOrganisations.JSONOrganisation;
import com.fasterxml.jackson.annotation.JsonProperty;

public class PDOrganisationSend {

    private String name;
    private Integer visible_to;

    private String address;

    private String address_subpremise;
    private String address_street_number;
    private String address_route;
    private String address_locality;
    private String address_country;
    private String address_postal_code;


    private Boolean active_flag;
    private Long id;
    private Long owner_id;
    @JsonProperty("add_time")
    private String creationTime;
    @JsonProperty( ProductionKeys.orgVID)//"1fdff908db3cffe4c92b93353cfd56219745619e")//"2388ef6b01b0ff49893c6f954ebfb162a70b12d2")
    private Long v_id;
    @JsonProperty( ProductionKeys.orgOwnedBy)//"bf75945461cae2a672c4404b85b1bc8a4d5c1ba9")//"276ed9c14c8766ac63ab668678b779a9b813658b")
    private String ownedBy;
    @JsonProperty( ProductionKeys.orgWebsite)//"87a1835b5151d1bbbe00591f64b7c623f8c4fc30")//"4d320823bca5075a18070cfce737c0d96cc2191b")
    private String website;
    @JsonProperty( ProductionKeys.orgCategory)//"e8d01005d38edc750c79d07adc5694090854a34d")
    private String category;
    @JsonProperty( ProductionKeys.orgBusinessDomain)//"08dcf8f5324898efbf5f886f1dfc3a220bdddf83")
    private String businessDomain;

    /**
     * RestTemplate, and testing
     */
    public PDOrganisationSend() {
        this.visible_to = 3;
        this.active_flag = true;
    }


    /**
     * Used to build organisationState for putlist
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

        //this.website = jo.getWebsite();


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
       // this.website = o.getWebsite();
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

//    public String getWebsite() {
//        return website;
//    }
//
//    public void setWebsite(String website) {
//        this.website = website;
//    }

    public String getAddress_subpremise() {
        return address_subpremise;
    }

    public void setAddress_subpremise(String address_subpremise) {
        this.address_subpremise = address_subpremise;
    }

    public String getAddress_street_number() {
        return address_street_number;
    }

    public void setAddress_street_number(String address__street_number) {
        this.address_street_number = address__street_number;
    }

    public String getAddress_route() {
        return address_route;
    }

    public void setAddress_route(String address_route) {
        this.address_route = address_route;
    }

    public String getAddress_locality() {
        return address_locality;
    }

    public void setAddress_locality(String address_locality) {
        this.address_locality = address_locality;
    }

    public String getAddress_country() {
        return address_country;
    }

    public void setAddress_country(String address_country) {
        this.address_country = address_country;
    }

    public String getAddress_postal_code() {
        return address_postal_code;
    }

    public void setAddress_postal_code(String address_postal_code) {
        this.address_postal_code = address_postal_code;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getBusinessDomain() {
        return businessDomain;
    }

    public void setBusinessDomain(String businessDomain) {
        this.businessDomain = businessDomain;
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

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }
}
