package VPI.VertecClasses.VertecActivities;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

public class ZUKActivities {

    @JsonProperty("activities")
    private List<JSONActivity> activityList;

    public ZUKActivities() {
    }

    public List<JSONActivity> getActivityList() {
        return activityList;
    }

    public void setActivityList(List<JSONActivity> activityList) {
        this.activityList = activityList;
    }

    public String toPrettyJSON() {
        String retStr = null;
        ObjectMapper m = new ObjectMapper();
        try{
            retStr = m.writerWithDefaultPrettyPrinter().writeValueAsString(this);
        }
        catch(Exception e){
            System.out.println("Could not convert ZUKActivities to JSON: " + e.toString());
        }
        return retStr;
    }
}
