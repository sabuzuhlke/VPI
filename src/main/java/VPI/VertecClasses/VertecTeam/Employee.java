package VPI.VertecClasses.VertecTeam;

import com.fasterxml.jackson.databind.ObjectMapper;

public class Employee {

    private Long id;
    private String email;
    private String name;
    private Boolean active;

    public Employee() {

    }

    public Employee(String email, Long id) {
        this.email = email;
        this.id = id;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
    @Override
    public String toString(){
        String retStr = null;
        ObjectMapper m = new ObjectMapper();
        try{

            retStr = m.writerWithDefaultPrettyPrinter().writeValueAsString(this);
        }
        catch(Exception e){
            System.out.println("Could not convert to JSON: " + e.toString());
        }
        return retStr;
    }
}