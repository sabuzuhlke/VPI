package VPI.InsightClasses;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)

public class VOrganisationItems {

    private List<VOrganisation> Items;

    public VOrganisationItems() {
    }

    @JsonProperty("Items")
    public void setItems(List<VOrganisation> items) {
        Items = items;
    }

    public List<VOrganisation> getItems() {
        return Items;
    }

    @Override
    public String toString(){
        String retString = "";
        for(VOrganisation i : Items){
            retString += i.toString();
        }
        return retString;
    }
}
