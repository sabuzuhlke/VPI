package VPI.PipedriveStateClasses;


import VPI.PDClasses.Activities.PDActivityReceived;

import java.util.List;

public class PipedriveActivities {

    private List<PDActivityReceived> activities;

    public PipedriveActivities() {
    }

    public List<PDActivityReceived> getActivities() {
        return activities;
    }

    public void setActivities(List<PDActivityReceived> activities) {
        this.activities = activities;
    }
}
