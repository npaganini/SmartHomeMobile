package grupo4.smarthome;

/**
 * Created by Esteban on 11/21/2017.
 */

public class Action {
    private String devId;
    private String name;
//    private ImageView image;

    public Action(String devId, String name) {
        this.devId = devId;
        this.name = name;
//        this.image = image;
    }

    public void setDevId(String id) {
        this.devId = devId;
    }

    public String getDevId() {
        return this.devId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public String getDescription () { return devId + "/" + name;}
}