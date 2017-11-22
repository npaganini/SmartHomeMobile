package grupo4.smarthome;

/**
 * Created by estebankramer on 18/11/2017.
 */

public class Lamp extends Device {
    private String color;
    private int brightness;

    public Lamp(String id, String name, String typeId, String state,String color, int brightness) {
        super(id, name, typeId, state);
        this.color = color;
        this.brightness = brightness;
    }

    public String getColor(){ return this.color; };

    public void setColor(String color) { this.color = color;};

    public int getBrightness(){ return this.brightness; };

    public void setBrightness() { this.brightness = brightness; };

}
