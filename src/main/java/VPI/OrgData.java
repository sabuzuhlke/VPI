package VPI;

/**
 * Created by sabu on 06/04/2016.
 */

public class OrgData {

    private Long id;
    private Long company_id;
    private Owner owner_id;
    private String name;
    private Boolean active_flag;
    private String address;

    public OrgData(Long id, Long company_id, Owner owner_id, Boolean active_flag, String name, String address) {
        this.id = id;
        this.company_id = company_id;
        this.owner_id = owner_id;
        this.active_flag = active_flag;
        this.name = name;
        this.address = address;
    }

    public OrgData() {
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

    public String getName() {
        return name;
    }

    public Long getCompany_id() {
        return company_id;
    }

    public Owner getOwner_id() {
        return owner_id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCompany_id(Long companyId) {
        this.company_id = companyId;
    }

    public void setOwner_id(Owner user) {
        this.owner_id = user;
    }

    public Boolean getActive_flag() {
        return active_flag;
    }

    public void setActive_flag(Boolean active_flag) {
        this.active_flag = active_flag;
    }

    @Override
    public String toString() {
        if (active_flag) {
            return "Id: " + id +
                    ", Name: " + name +
                    ", User: " + owner_id + " and company id: " + company_id + " and address @ " + address;
        } else {
            return "DEleted org " + id;
        }
    }
}
