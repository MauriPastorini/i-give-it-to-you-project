package Domain;

/**
 * Created by Mauri on 11-May-17.
 */

public class Category {
    public String name;
    public int Id;

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
