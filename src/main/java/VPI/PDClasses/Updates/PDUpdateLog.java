package VPI.PDClasses.Updates;

import VPI.PDClasses.PDAdditionalData;
import VPI.PDClasses.PDResponse;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PDUpdateLog extends PDResponse {
    private List<PDUpdate> data;
    private PDAdditionalData additional_data;
    private Long orgid;

    public PDUpdateLog() {
        data = new ArrayList<>();
    }

    public List<PDUpdate> getData() {
        return data;
    }

    public void setData(List<PDUpdate> data) {
        this.data = data;
    }

    public PDAdditionalData getAdditional_data() {
        return additional_data;
    }

    public void setAdditional_data(PDAdditionalData additional_data) {
        this.additional_data = additional_data;
    }

    public Long getOrgid() {
        return orgid;
    }

    public void setOrgid(Long orgid) {
        this.orgid = orgid;
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

    public PDUpdateData getLatestOrganisationChange(){
        for(PDUpdate pu : data){
            if(pu.getObject().equals("organizationChange")){
                return pu.getData();
            }
        }
        return null;
    }


}
