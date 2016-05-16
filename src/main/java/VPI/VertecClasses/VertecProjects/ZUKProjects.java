package VPI.VertecClasses.VertecProjects;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

/**
 * Created by sabu on 16/05/2016.
 */
public class ZUKProjects {

    @JsonProperty("projects")
    private List<JSONProject> projects;

    public ZUKProjects() {
    }

    public List<JSONProject> getProjects() {
        return projects;
    }

    public void setProjects(List<JSONProject> projects) {
        this.projects = projects;
    }

    @Override
    public String toString() {
        String retStr = null;
        ObjectMapper m = new ObjectMapper();
        try{

            retStr = m.writerWithDefaultPrettyPrinter().writeValueAsString(this);
        }
        catch(Exception e){
            System.out.println("Could not build JSON Projects: " + e.toString());
        }
        return retStr;
    }
}