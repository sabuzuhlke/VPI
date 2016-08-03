package VPI.VertecClasses.VertecActivities;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

public class ActivitiesForAddressEntry {

    private Long id;
    private String name;
    private List<VPI.VertecClasses.VertecActivities.Activity> activities;

    public ActivitiesForAddressEntry() {
    }

    public ActivitiesForAddressEntry(Long organisationId, String name) {
        this.id = organisationId;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<VPI.VertecClasses.VertecActivities.Activity> getActivities() {
        return activities;
    }

    public void setActivities(List<VPI.VertecClasses.VertecActivities.Activity> activities) {
        this.activities = activities;
    }

    public String toJSONString() {
        String retStr = null;
        ObjectMapper m = new ObjectMapper();
        try {

            retStr = m.writerWithDefaultPrettyPrinter().writeValueAsString(this);
        } catch (Exception e) {
            System.out.println("Could not convert XML Envelope to JSON: " + e.toString());
        }
        return retStr;
    }
}
