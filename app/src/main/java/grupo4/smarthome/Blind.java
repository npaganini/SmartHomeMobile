package grupo4.smarthome;

/**
 * Created by estebankramer on 18/11/2017.
 */

public class Blind extends Device {
    private int level;

    public Blind(String id, String name, String typeId, String state,int level){
        super(id,name,typeId,state);
        this.level = level;
    }

    public int getLevel(){ return this.level;};

    public void setlevel(int level){ this.level = level;};
}
