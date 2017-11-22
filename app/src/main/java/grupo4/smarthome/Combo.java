package grupo4.smarthome;

/**
 * Created by Esteban on 11/21/2017.
 */

public class Combo {
    private String id;
    private String name;
//    private ImageView image;

    public Combo(String id, String name) {
        this.id = id;
        this.name = name;
//        this.image = image;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return this.id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

}