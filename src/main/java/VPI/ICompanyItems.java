package VPI;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by sabu on 12/04/2016.
 */
public class ICompanyItems {

    private List<ICompany> Items;

    public ICompanyItems() {
    }

    public List<ICompany> getItems() {
        return Items;
    }

    @JsonProperty("Items")
    public void setItems(List<ICompany> items) {
        Items = items;
    }

    @Override
    public String toString(){
        String retString = "";
        for(int i = 0; i < Items.size(); i++ ){
            retString += Items.get(i).toString();
        }
        return retString;
    }
}
