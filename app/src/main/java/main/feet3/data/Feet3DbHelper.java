package main.feet3.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by David on 28/06/2016.
 * Database manager class
 */

//TODO don't forget to create datasource on main!
public class Feet3DbHelper extends SQLiteOpenHelper {

    public Feet3DbHelper(Context context) {
        super(context, Feet3DataSource.DATABASE_NAME, null, Feet3DataSource.DATABASE_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //Create tables
        db.execSQL(Feet3DataSource.CREATE_TABLE_POSITION_SCRIPT);
        db.execSQL(Feet3DataSource.CREATE_TABLE_FINDINGS_SCRIPT);
        db.execSQL(Feet3DataSource.CREATE_TABLE_DEVICES_SCRIPT);
        db.execSQL(Feet3DataSource.CREATE_TABLE_NETWORKS_SCRIPT);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            //Changes to scheme if necessary
        db.execSQL("drop table " + Feet3DataSource.TABLE_POSITION);
        db.execSQL(Feet3DataSource.CREATE_TABLE_POSITION_SCRIPT);
    }
}
