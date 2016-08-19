package main.feet3;

/**
 * Created by David on 29/06/2016.
 */
public class Device {

    private int id;
    private String mac_address;
    private String name;
    private Feet3DataSource.DeviceType type;

    public Device(){

    }

    public Device(int id){
        this.id = id;
    }

    public Device(int id, String mac_address, String name, Feet3DataSource.DeviceType type){
        this.id = id;
        this. mac_address = mac_address;
        this.name = name;
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMac_address() {
        return mac_address;
    }

    public void setMac_address(String mac_address) {
        this.mac_address = mac_address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Feet3DataSource.DeviceType getType() {
        return type;
    }

    public void setType(Feet3DataSource.DeviceType type) {
        this.type = type;
    }
}
