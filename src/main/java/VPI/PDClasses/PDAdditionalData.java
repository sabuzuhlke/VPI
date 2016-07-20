package VPI.PDClasses;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PDAdditionalData {

    private PDPagination pagination;

    public PDAdditionalData() {
    }

    public PDPagination getPagination() {
        return pagination;
    }

    public void setPagination(PDPagination pagination) {
        this.pagination = pagination;
    }

    /**
     * Nested class pagination only used here
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public class PDPagination {

        private Integer start;
        private Integer limit;
        private Boolean more_items_in_collection;

        public PDPagination() {
        }

        public Integer getStart() {
            return start;
        }

        public void setStart(Integer start) {
            this.start = start;
        }

        public Integer getLimit() {
            return limit;
        }

        public void setLimit(Integer limit) {
            this.limit = limit;
        }

        public Boolean getMore_items_in_collection() {
            return more_items_in_collection;
        }

        public void setMore_items_in_collection(Boolean more_items_in_collection) {
            this.more_items_in_collection = more_items_in_collection;
        }

        @Override
        public String toString(){
            String retStr = null;
            ObjectMapper m = new ObjectMapper();
            try{

                retStr = m.writerWithDefaultPrettyPrinter().writeValueAsString(this);
            }
            catch(Exception e){
                System.out.println("Could not convert XML Envelope to JSON: " + e.toString());
            }
            return retStr;
        }
    }

    @Override
    public String toString(){
        return pagination.toString();
    }

}
