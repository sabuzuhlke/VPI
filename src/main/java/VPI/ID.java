package VPI;

/**
 * Created by sabu on 07/04/2016.
 */

public class ID {

    private Long id;

    public ID() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return id.toString();
    }
}
