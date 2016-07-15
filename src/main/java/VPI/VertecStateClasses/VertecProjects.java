package VPI.VertecStateClasses;

import VPI.VertecClasses.VertecProjects.JSONProject;

import java.util.List;

public class VertecProjects {

    private List<JSONProject> projects;

    public VertecProjects() {
    }

    public List<JSONProject> getProjects() {
        return projects;
    }

    public void setProjects(List<JSONProject> projects) {
        this.projects = projects;
    }
}
