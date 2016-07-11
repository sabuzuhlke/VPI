package VPI.PDClasses;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

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
        private Integer limitd;
        private Boolean more_items_in_collection;

        public PDPagination() {
        }

        public Integer getStart() {
            return start;
        }

        public void setStart(Integer start) {
            this.start = start;
        }

        public Integer getLimitd() {
            return limitd;
        }

        public void setLimitd(Integer limitd) {
            this.limitd = limitd;
        }

        public Boolean getMore_items_in_collection() {
            return more_items_in_collection;
        }

        public void setMore_items_in_collection(Boolean more_items_in_collection) {
            this.more_items_in_collection = more_items_in_collection;
        }
    }

}
