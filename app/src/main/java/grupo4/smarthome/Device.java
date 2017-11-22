package grupo4.smarthome;

import android.media.Image;
import android.widget.ImageView;

/**
 * Created by estebankramer on 18/11/2017.
 */

public class Device {
    private String id;
    private String name;
    private String state;
    private String typeId;

    public Device(String id, String name, String typeId, String state){
        this.id = id;
        this.name = name;
        this.typeId = typeId;
        this.state = state;
    }

    public String getId(){ return this.id; };

    public String getName() { return this.name; };

    public void setName(String name){ this.name = name; };

    public String getState() { return this.state; };

    public String setState() { return this.state; };

    public String getTypeId() { return this.typeId; };

    public int getImage(){

        switch(typeId){
            case "eu0v2xgprrhhg41g": //blind
                return R.drawable.blind;
            case "go46xmbqeomjrsjr": //lamp
                return R.drawable.lamp;
            case "li6cbv5sdlatti0j": //ac
                return R.drawable.ac;
            case "lsf78ly0eqrjbz91": //door
                return R.drawable.door;
            case "mxztsyjzsrq7iaqc": //alarm
                return R.drawable.alarm;
            case "ofglvd9gqX8yfl3l": //timer
                return R.drawable.timer;
        }
        return R.drawable.foto_no_disponible;
    }
}
