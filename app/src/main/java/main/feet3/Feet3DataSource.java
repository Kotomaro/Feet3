package main.feet3;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by David on 28/06/2016.
 * Contract class that specifies the scheme of the database.
 */
public class Feet3DataSource {

    private static Feet3DataSource instance;



    //Useful stuff
    public enum DeviceType{WIFI, MOBILE};

    //BD metadata
    public static final String DATABASE_NAME = "Feet3DB.bd";
    public static final int DATABASE_VERSION = 1;
    public static final String STRING_TYPE = "text";
    public static final String INT_TYPE = "integer";

    //Table Position
    public static final String TABLE_POSITION = "Position";
    public static final String POSITION_COLUMN_LATITUDE = "Latitude";
    public static final String POSITION_COLUMN_LONGITUDE = "Longitude";
    public static final String POSITION_COLUMN_NAME = "Name";
    public static final String POSITION_COLUMN_ADDRESS = "Address";
    public static final String POSITION_COLUMN_NUM = "Num";

    //Table Findings
    public static final String TABLE_FINDINGS = "Findings";
    public static final String FINDINGS_COLUMN_LATITUDE = "Latitude";
    public static final String FINDINGS_COLUMN_LONGITUDE = "Longitude";
   // removed public static final String FINDINGS_COLUMN_DEVICE = "Device";
    public static final String FINDINGS_COLUMN_DATE = "Date";

    //Table Devices
    public static final String TABLE_DEVICES = "Devices";
    public static final String DEVICES_COLUMN_ID = BaseColumns._ID;
    public static final String DEVICES_COLUMN_MAC_ADDRESS = "Mac_address";
    public static final String DEVICES_COLUMN_NAME = "Name";
    public static final String DEVICES_COLUMN__TYPE = "Type"; // integer, 0 if wifi, 1 if mobile device

    //Table Finding_Has_Devices
    public static final String TABLE_NETWORKS = "Networks";
    public static final String NETWORKS_COLUMN_LATITUDE = "Latitude";
    public static final String NETWORKS_COLUMN_LONGITUDE = "Longitude";
    public static final String NETWORKS_COLUMN_DATE = "Date";
    public static final String NETWORKS_COLUMN_MAC_ADDRESS = "Mac_address";



    //Creation Scripts

    //Create table Position
    public static final String CREATE_TABLE_POSITION_SCRIPT = "create table if not exists "
            + TABLE_POSITION + "("
            + POSITION_COLUMN_LATITUDE + " real not null, "
            + POSITION_COLUMN_LONGITUDE + " real not null, "
            + POSITION_COLUMN_NAME + " text, "
            + POSITION_COLUMN_ADDRESS + " text, "
            + POSITION_COLUMN_NUM + " text, "
            + "primary key ( "+POSITION_COLUMN_LATITUDE + ", " + POSITION_COLUMN_LONGITUDE + "));";

    //Create table Findings
    public static final String CREATE_TABLE_FINDINGS_SCRIPT = "create table if not exists "
            + TABLE_FINDINGS + "("
            + FINDINGS_COLUMN_LATITUDE + " real not null, "
            + FINDINGS_COLUMN_LONGITUDE + " real not null, "
          //removed  + FINDINGS_COLUMN_DEVICE + " integer, "
            + FINDINGS_COLUMN_DATE + " text, "
            + "primary key( " + FINDINGS_COLUMN_LATITUDE + ", " + FINDINGS_COLUMN_LONGITUDE + ", " + FINDINGS_COLUMN_DATE + "), "
            + "foreign key(" + FINDINGS_COLUMN_LATITUDE + ") references " + TABLE_POSITION + "(" + POSITION_COLUMN_LATITUDE +"),"
            + "foreign key(" + FINDINGS_COLUMN_LONGITUDE + ") references " + TABLE_POSITION + "(" + POSITION_COLUMN_LONGITUDE +")"
          //removed  + "foreign key(" + FINDINGS_COLUMN_DEVICE + ") references " + TABLE_DEVICES + "(" + DEVICES_COLUMN_MAC_ADDRESS +")"
            + ")"
            + ";";

    //Create table Devices
    public static final String CREATE_TABLE_DEVICES_SCRIPT ="create table if not exists "
            + TABLE_DEVICES + "("
            + DEVICES_COLUMN_ID + " integer primary key autoincrement, "
            + DEVICES_COLUMN_MAC_ADDRESS + " text, "
            + DEVICES_COLUMN_NAME + " text, "
            + DEVICES_COLUMN__TYPE + " integer"
            + ");";

    //Create table Networks
    public static final String CREATE_TABLE_NETWORKS_SCRIPT = "create table if not exists "
            + TABLE_NETWORKS + "("
            + NETWORKS_COLUMN_LATITUDE + " real not null, "
            + NETWORKS_COLUMN_LONGITUDE + " real not null, "
            + NETWORKS_COLUMN_DATE + " text, "
            + NETWORKS_COLUMN_MAC_ADDRESS + " text, "
            + "primary key(" + NETWORKS_COLUMN_LATITUDE + ", " + NETWORKS_COLUMN_LONGITUDE + ", " + NETWORKS_COLUMN_DATE + ", " + NETWORKS_COLUMN_MAC_ADDRESS +"), "
            + "foreign key(" + NETWORKS_COLUMN_LATITUDE + ") references " + TABLE_FINDINGS + "(" + FINDINGS_COLUMN_LATITUDE + "), "
            + "foreign key(" + NETWORKS_COLUMN_LONGITUDE + ") references " + TABLE_FINDINGS + "(" + FINDINGS_COLUMN_LONGITUDE + "), "
            + "foreign key(" + NETWORKS_COLUMN_DATE + ") references " + TABLE_FINDINGS + "(" + FINDINGS_COLUMN_DATE + "), "
            + "foreign key(" + NETWORKS_COLUMN_MAC_ADDRESS + ") references " + TABLE_DEVICES + "(" + DEVICES_COLUMN_MAC_ADDRESS + ")"
            +")"
            + ";";

    //Default insertion script
    public static final String INSERT_FEET3DB_DEFAULTSCRIPT = "";//TODO



    private Feet3DbHelper openHelper;
    private SQLiteDatabase database;

    //todo singleton?
    //Constructor
    public Feet3DataSource(Context context){
        openHelper = new Feet3DbHelper(context);
        open();

    }

    public static Feet3DataSource getInstance(Context context){
        if(instance == null) {
            instance = new Feet3DataSource(context);
        }

        return instance;


    }

    //Gets an usable database
    public void open() throws SQLException{
        database = openHelper.getWritableDatabase();
    }


    public void insertPosition(Position position){
        String sqlQuery;
        Position p = getPosition(position.getLatitude(), position.getLongitude());


       //check if already exists
        if(p.getNum() != 0){
            //update database entry
            int num = p.getNum() +1;

            p.setNum(num);
         //   System.out.println("1* num veces cuando actualizo position: " + p.getNum());

            sqlQuery = "update " + TABLE_POSITION
                    + " set " + POSITION_COLUMN_NUM
                    + " = "
                    + p.getNum()
                    + " where " + POSITION_COLUMN_LATITUDE + " = " + p.getLatitude()
                    + " and " + POSITION_COLUMN_LONGITUDE + " = " + p.getLongitude()
                    + ";";


        }else {//if it doesn't exist, insert
            p = position;
            p.setNum(1);
            sqlQuery = "insert into " + TABLE_POSITION
                    + " values ("
                    + p.getLatitude() + ", "
                    + p.getLongitude() + ", "
                    + "'" + p.getName() + "', "
                    + "'" + p.getAddress() + "', "
                    + p.getNum()
                    +");";


        }
     //   System.out.println("2 *sentencia sql : "+sqlQuery);
        database.execSQL(sqlQuery);



    }

    public Position getPosition(double latitude, double longitude){
        String sqlQuery = "select * from " + TABLE_POSITION
                + " where " + POSITION_COLUMN_LATITUDE
                + " = " + latitude
                + " and " + POSITION_COLUMN_LONGITUDE
                + " = " + longitude
                +";";

        Position p = new Position();
        Cursor cursor = database.rawQuery(sqlQuery, null);
        if(cursor != null && cursor.moveToFirst()){
            p.setLatitude(Double.parseDouble(cursor.getString(0)));
            p.setLongitude(Double.parseDouble(cursor.getString(1)));
            p.setName(cursor.getString(2));
            p.setAddress(cursor.getString(3));
            p.setNum(Integer.parseInt(cursor.getString(4)));

         //   System.out.println(" cursor 4: " + cursor.getString(4));

        }

        cursor.close();
        return p;

    }



    public ArrayList<Position> getAllPositions(){
        String sqlQuery = "select * from " + TABLE_POSITION +";";

        Position p;
        ArrayList<Position> list = new ArrayList<Position>();

        Cursor cursor = database.rawQuery(sqlQuery, null);

        if(cursor.moveToFirst()) {

            while(cursor.isAfterLast() == false){
                p = new Position();
                p.setLatitude(Double.parseDouble(cursor.getString(0)));
                p.setLongitude(Double.parseDouble(cursor.getString(1)));
                p.setName(cursor.getString(2));
                p.setAddress(cursor.getString(3));
                p.setNum(Integer.parseInt(cursor.getString(4)));
             //   System.out.println("getallpositions num: "+cursor.getString(4));


                list.add(p);
                cursor.moveToNext();
            }

        }
        cursor.close();

        return list;

    }

    public Position findPositionByDate(String date){
    /*
        String sqlQuery;
        sqlQuery = "select * from "
                + TABLE_POSITION
                + " inner join " + TABLE_FINDINGS
                + " on " + TABLE_POSITION + "." + POSITION_COLUMN_LATITUDE + " = " + TABLE_FINDINGS + "." + FINDINGS_COLUMN_LATITUDE
                + " and " + TABLE_POSITION + "." + POSITION_COLUMN_LONGITUDE + " = " + TABLE_FINDINGS + "." + FINDINGS_COLUMN_LONGITUDE
                + " where " + FINDINGS_COLUMN_DATE
                + " = '" + date +"';";

        System.out.println("+++ sentencia sql findpositionbydate: "+ sqlQuery);
        Position p = new Position();

        Cursor cursor = database.rawQuery(sqlQuery, null);

        System.out.println("*** cursor? : "+ cursor.moveToFirst());
        if(cursor != null && cursor.moveToFirst()){
            p.setLatitude(Double.parseDouble(cursor.getString(0)));
            p.setLongitude(Double.parseDouble(cursor.getString(1)));
            p.setName(cursor.getString(2));
            p.setAddress(cursor.getString(3));
            p.setNum(Integer.parseInt(cursor.getString(4)));

            System.out.println(" cursor 4: " + cursor.getString(4));

        }


        System.out.println("position obtenida: "+ p.getLatitude());

        cursor.close();

    */
        double lat;
        double lon;

        Finding f = getFindingByDate(date);
        lat = f.getLatitude();
        lon = f.getLongitude();
      //  System.out.println("finding obtenida: "+ f.getLatitude());

        Position p = getPosition(lat,lon);

        return p;




    }


    public void insertFinding(Finding finding){

        String sqlQuery;
        Finding f = getFindingByDate(finding.getDate());

        if(f.getDate() != null && f.getDevice() == finding.getDevice()){
            //if there is a finding with the same date AND device, it already exists, update (don't do anything)

        }else {

            //if it doesn't exist, insert
            f = finding;

            sqlQuery = "insert into " + TABLE_FINDINGS
                    + " VALUES ("
                    + f.getLatitude() + ", "
                    + f.getLongitude() + ", "
                    // + "'" + f.getDevice() + "', "
                    + "'" + f.getDate() + "');";

            database.execSQL(sqlQuery);
        }


    }


    public ArrayList<Finding> getFindingsByPosition(double latitude, double longitude){
        String sqlQuery;
        sqlQuery = "select * from "
                + TABLE_FINDINGS
                + " where "
                + FINDINGS_COLUMN_LATITUDE + " = " + latitude
                + " and " + FINDINGS_COLUMN_LONGITUDE + " = " + longitude
                + ";";

        Finding f;
        ArrayList<Finding> list = new ArrayList<Finding>();

        Cursor cursor = database.rawQuery(sqlQuery, null);
        if(cursor.moveToFirst()) {
            while(cursor.isAfterLast() == false){
                f = new Finding();
                f.setLatitude(Double.parseDouble(cursor.getString(0)));
                f.setLongitude(Double.parseDouble(cursor.getString(1)));

               // f.setDevice(Integer.parseInt(cursor.getString(2)));

                f.setDate(cursor.getString(2));
                list.add(f);
                cursor.moveToNext();
            }
        }
        cursor.close();
        return list;

    }

    public Finding getFindingByDevice(int deviceID){
        //todo
        return null;
    }

    public Finding getFindingByDate(String date){
        String sqlQuery;
        sqlQuery = "select * from "
                + TABLE_FINDINGS
                + " where "
                + FINDINGS_COLUMN_DATE
                + " = "
                + "'" + date + "'"
                + ";";

      //  System.out.println("sentencia sql findingbydate: "+ sqlQuery);
        Finding f = new Finding();
        Cursor cursor = database.rawQuery(sqlQuery, null);

      //  System.out.println("cursor: " + cursor.moveToFirst());
        if(cursor != null && cursor.moveToFirst()){
            f.setLatitude(Double.parseDouble(cursor.getString(0)));
            f.setLongitude(Double.parseDouble(cursor.getString(1)));

            //if(!cursor.getString(2).equals("null"))
          //  f.setDevice(Integer.parseInt(cursor.getString(2)));

            f.setDate(cursor.getString(2));
        }

        cursor.close();
        return f;
    }

    public ArrayList<Finding> getAllFindings(){
        String sqlQuery = "select * from " + TABLE_FINDINGS +";";

        Finding f;
        ArrayList<Finding> list = new ArrayList<Finding>();

        Cursor cursor = database.rawQuery(sqlQuery, null);

        if(cursor.moveToFirst()) {

            while(cursor.isAfterLast() == false){
                f = new Finding();
                f.setLatitude(Double.parseDouble(cursor.getString(0)));
                f.setLongitude(Double.parseDouble(cursor.getString(1)));

             //   f.setDevice(Integer.parseInt(cursor.getString(2)));

                f.setDate(cursor.getString(2));


                list.add(f);
                cursor.moveToNext();
            }

        }
        cursor.close();

        return list;


    }

    public void insertDevice(Device device) {
        String sqlQuery;
        Device d = getDeviceByMac(device.getMac_address());
        if(d.getMac_address() != null) {//already exists, update (don't do anything)

        } else {
            sqlQuery = "insert into " + TABLE_DEVICES
                    + " VALUES (NULL, "//autoincrement requires null value
                    + "'" + device .getMac_address() + "'"
                    + ", '" + device.getName() + "'"
                    + ", " + device.getType().ordinal()
                    + ");";

            database.execSQL(sqlQuery);

        }
    }

    public Device getDeviceByMac(String mac){
        String sqlQuery = "Select * from " + TABLE_DEVICES
                + " where " + DEVICES_COLUMN_MAC_ADDRESS
                + " = " + "'" + mac + "'"
                + ";"
                ;
        Device d = new Device();
        Cursor cursor = database.rawQuery(sqlQuery, null);
        if(cursor != null && cursor.moveToFirst()){
            d.setId(Integer.parseInt(cursor.getString(0)));
            d.setMac_address(cursor.getString(1));
            d.setName(cursor.getString(2));
            d.setType(DeviceType.values()[(Integer.parseInt(cursor.getString(3)))]);
        }

        cursor.close();
        return d;

    }

    public Device getDeviceById(int id){
        String sqlQuery = "Select * from " + TABLE_DEVICES
                + " where " + DEVICES_COLUMN_ID
                + " = " + id
                + ";"
                ;
        Device d = new Device();
        Cursor cursor = database.rawQuery(sqlQuery, null);
        if(cursor != null && cursor.moveToFirst()){
            d.setId(Integer.parseInt(cursor.getString(0)));
            d.setMac_address(cursor.getString(1));
            d.setName(cursor.getString(2));
            d.setType(DeviceType.values()[(Integer.parseInt(cursor.getString(3)))]);
        }

        cursor.close();
        return d;



    }


    public void insertFindingWithDevices(Finding finding, List<Device> devices){

        System.out.println("insertando findings con sus devices");
        insertFinding(finding);

        for(Device d: devices){
            insertDevice(d);
            insertNetwork(finding, d);
            System.out.println("insertado finding con device id: " + finding.getDevice()); //todo remove device id
        }


    }


    public List<Device> getDevicesByFinding(double latitude, double longitude, String date){
        List<Device> result = new ArrayList<>();
        List<String> macs = new ArrayList<>();
        String sqlQuery = "select " + NETWORKS_COLUMN_MAC_ADDRESS + " from " + TABLE_NETWORKS
                + " where " + NETWORKS_COLUMN_LATITUDE + " = " + latitude
                + " and " + NETWORKS_COLUMN_LONGITUDE + " = " + longitude
                + " and " + NETWORKS_COLUMN_DATE
                + " = " + "'" + date + "'"
                + ";";

       // System.out.println("Encontrar device query: " + sqlQuery);
        Cursor cursor = database.rawQuery(sqlQuery, null);
        if(cursor.moveToFirst()) {
            while(cursor.isAfterLast() == false){//Get all the devices ids associated with that position and date
                if(!cursor.getString(0).equals("0")){//&& !ids.contains(Integer.parseInt(cursor.getString(0)))
                    //check if there are networks associated or not (null)
                    //don't add the same network twice?
                    macs.add(cursor.getString(0));



                }
                cursor.moveToNext();
            }
        }
        System.out.println("**macs**: " + macs.size());

        //Find the devices by ids
        for(String mac: macs){
           Device d = getDeviceByMac(mac);

            result.add(d);
        }

       // System.out.println("Results** : " + result.get(0).getName());

        return result;

    }




    public void insertNetwork(Finding finding, Device device){
        //todo check if already in db
        String sqlQuery;

        sqlQuery = "select * from " + TABLE_NETWORKS
                + " where "
                + NETWORKS_COLUMN_LATITUDE + " = " + finding.getLatitude() + " and "
                + NETWORKS_COLUMN_LONGITUDE + " = " + finding.getLongitude() + " and "
                + NETWORKS_COLUMN_DATE + " = '" + finding.getDate() + "' and "
                + NETWORKS_COLUMN_MAC_ADDRESS + " = '" + device.getMac_address() + "';";

        //comprobar si existe
        Cursor cursor = database.rawQuery(sqlQuery, null);
        if(cursor!= null && !cursor.isAfterLast()){
            System.out.println("aqui esta el problema");
            System.out.println("sentencia: " + sqlQuery);

            //already exists, do nothing
        }else {

            sqlQuery = "insert into " + TABLE_NETWORKS
                    + " VALUES ("
                    + finding.getLatitude() + ", "
                    + finding.getLongitude() + ", "
                    + "'" + finding.getDate() + "', "
                    + "'" + device.getMac_address() + "');";

            database.execSQL(sqlQuery);
        }


    }


    public void clearHistory(){
        String sqlQuery = "delete from " + TABLE_POSITION +";";
        database.execSQL(sqlQuery);

        sqlQuery = "delete from " + TABLE_FINDINGS + ";";
        database.execSQL(sqlQuery);

        sqlQuery = "delete from " + TABLE_DEVICES + ";";
        database.execSQL(sqlQuery);

        sqlQuery = "delete from " + TABLE_NETWORKS + ";";
        database.execSQL(sqlQuery);


    }

}
