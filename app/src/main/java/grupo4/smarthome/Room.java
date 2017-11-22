package grupo4.smarthome;

import android.media.Image;
import android.widget.ImageView;

/**
 * Created by estebankramer on 17/11/2017.
 */

public class Room {
    private String id;
    private String name;
//    private ImageView image;

    public Room(String id, String name) {
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

//    public void setImage(ImageView image) {this.image = image;}
//
//    public ImageView getImage() {return this.image;}

}
