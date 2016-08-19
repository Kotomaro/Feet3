package main.feet3;

import java.sql.Date;

/**
 * Created by David on 29/06/2016.
 */
public class Finding {


    private double latitude;
    private double longitude;
    private int device;
    private String date;



    public Finding(){

    }

    public Finding(int latitude, int longitude){
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Finding(int latitude, int longitude, int device, String date){

        this.latitude = latitude;
        this.longitude = longitude;
        this.device = device;
        this.date = date;

    }



    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getDevice() {
        return device;
    }

    public void setDevice(int device) {
        this.device = device;
    }



    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
