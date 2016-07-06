package VPI.PDClasses;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by sabu on 12/05/2016.
 */
public class PDRelationship {

    @JsonProperty("rel_owner_org_id")
    private Long rel_owner_org_id;
    @JsonProperty("rel_linked_org_id")
    private Long rel_linked_org_id;
    @JsonProperty("type")
    private String type;
    @JsonProperty("org_id")
    private Long org_id;

    public PDRelationship(Long parentId, Long childId) {
        this.rel_owner_org_id = parentId;
        this.rel_linked_org_id = childId;
        this.org_id = parentId;
        this.type = "parent";
    }

    public PDRelationship() {
        this.type = "parent";
    }

    public Long getRel_owner_org_id() {
        return rel_owner_org_id;
    }

    public void setRel_owner_org_id(Long rel_owner_org_id) {
        this.rel_owner_org_id = rel_owner_org_id;
    }

    public Long getRel_linked_org_id() {
        return rel_linked_org_id;
    }

    public void setRel_linked_org_id(Long rel_linked_org_id) {
        this.rel_linked_org_id = rel_linked_org_id;
    }

    public String getType() {
        return type;
    }

    public Long getOrg_id() {
        return org_id;
    }

    public void setOrg_id(Long org_id) {
        this.org_id = org_id;
    }

    @Override
    public String toString() {
        return ("REl: " + rel_linked_org_id + ", " + rel_owner_org_id + ", " + type + ", " + org_id);
    }
}
