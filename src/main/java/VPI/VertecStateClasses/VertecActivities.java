package VPI.VertecStateClasses;


import VPI.VertecClasses.VertecActivities.JSONActivity;

import java.util.List;

public class VertecActivities {

    private List<JSONActivity> activities;

    public VertecActivities() {
    }

    public List<JSONActivity> getActivities() {
        return activities;
    }

    public void setActivities(List<JSONActivity> activities) {
        this.activities = activities;
    }
}
