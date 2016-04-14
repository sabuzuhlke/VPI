package VPI.PDClasses;

public class ContactDetail {
    private String value;
    private Boolean primary;

    public ContactDetail() {
    }

    public ContactDetail(String value, Boolean primary) {
        this.value = value;
        this.primary = primary;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Boolean getPrimary() {
        return primary;
    }

    public void setPrimary(Boolean primary) {
        this.primary = primary;
    }
}
