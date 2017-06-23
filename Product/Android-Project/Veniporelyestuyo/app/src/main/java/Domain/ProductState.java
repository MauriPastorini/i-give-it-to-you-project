package Domain;

/**
 * Created by Mauri on 11-May-17.
 */

public class ProductState {
    public String name;
    public int id;

    public ProductState(String name, int idd) {
        this.name = name;
        this.id = idd;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
