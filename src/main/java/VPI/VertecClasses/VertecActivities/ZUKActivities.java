package VPI.VertecClasses.VertecActivities;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

/**
 * Created by sabu on 24/05/2016.
 */
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

    @Override
    public String toString() {
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
