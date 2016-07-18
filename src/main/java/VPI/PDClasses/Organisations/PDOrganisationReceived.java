package VPI.PDClasses.Organisations;

import VPI.PDClasses.PDOwner;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public class PDOrganisationReceived {

    private String name;
    private Integer visible_to;
    private String address;

    private String address_subpremise;
    private String address_street_number;
    private String address_route;
    private String address_locality;
    @JsonProperty("address_country")
    private String address_country;
    private String address_postal_code;


    private Boolean active_flag;
    private Long id;
    private Long company_id;
    private PDOwner owner_id;
    private String update_time;
    private Integer people_count;
    @JsonProperty("add_time")
    private String creationTime;
    //Custom fields
    @JsonProperty("1fdff908db3cffe4c92b93353cfd56219745619e")//"2388ef6b01b0ff49893c6f954ebfb162a70b12d2")
    private Long v_id;
    @JsonProperty("bf75945461cae2a672c4404b85b1bc8a4d5c1ba9")//"276ed9c14c8766ac63ab668678b779a9b813658b")
    private String ownedBy;
//    @JsonProperty("4d320823bca5075a18070cfce737c0d96cc2191b")
//    private String website;

    /**
     * Used in old code
     */
    public PDOrganisationReceived(String name, String address) {
        this.name = name;
        this.address = address;
        this.active_flag = true;
    }

    /**
     * Used by RestTemplate and for testing
     */
    public PDOrganisationReceived() {
    }

    //use this function when reading update time from object imported from pipedrive
    public LocalDateTime readDateFromPDOrganisation() {
        String[] dAndT = this.update_time.split(" ");
        return LocalDateTime.parse(dAndT[0] + "T" + dAndT[1]);
    }

    public Long getV_id() {
        return v_id;
    }

    public void setV_id(Long v_id) {
        this.v_id = v_id;
    }

    public String getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(String update_time) {
        this.update_time = update_time;
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

    public Long getCompany_id() {
        return company_id;
    }

    public void setCompany_id(Long company_id) {
        this.company_id = company_id;
    }

    public PDOwner getOwner_id() {
        return owner_id;
    }

    public void setOwner_id(PDOwner owner_id) {
        this.owner_id = owner_id;
    }

    public Integer getPeople_count() {
        return people_count;
    }

    public void setPeople_count(Integer people_count) {
        this.people_count = people_count;
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

    public String getAddress_subpremise() {
        return address_subpremise;
    }

    public void setAddress_subpremise(String address_subpremise) {
        this.address_subpremise = address_subpremise;
    }

    public String getAddress_street_number() {
        return address_street_number;
    }

    public void setAddress_street_number(String address_street_number) {
        this.address_street_number = address_street_number;
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

    @Override
    public String toString() {
        return "Company: ID: " + id + " Name: " + name  + " visible to: " + visible_to + ", Address: " + address + " Active: " + active_flag + " Compani_id: " + company_id;
    }
}
