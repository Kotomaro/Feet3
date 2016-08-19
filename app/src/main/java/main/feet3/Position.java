package main.feet3;

import java.util.Date;

/**
 * Created by David on 29/06/2016.

 * Model of entity Position
 */
public class Position {

    private double latitude;
    private double longitude;
    private String name;
    private String address;

    private int num;


    public Position(){
        num = 0;
    };

    public Position(double latitude, double longitude){
        this.latitude = latitude;
        this.longitude = longitude;
        num = 0;
    }

    public Position(double latitude, double longitude, String name, String address,int num){
        this.latitude = latitude;
        this.longitude = longitude;
        this.name = name;
        this.address = address;

        this.num = num;
    }

    //Getters
    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }



    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getNum() {
        return num;
    }

    //Setters

    public void setName(String name) {
        this.name = name;
    }

    public void setAddress(String address) {
        this.address = address;
    }


    public void setNum(int num) {
        this.num = num;
    }
}
