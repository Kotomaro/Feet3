package main.feet3;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    //interface elements
    private Button save_location_button;
    private Button delete_history_button;

    private ListView stops_history_listView;
    private ArrayList<HashMap<String, String>> list;



    private Feet3LocationManager fLocationManager;
    private Feet3DataSource fDataSource;
    private Feet3WifiManager wifiManager;
    Context context;




    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_main);

        //init the utilities
        fLocationManager = new Feet3LocationManager(this.getApplicationContext(), this);
        fDataSource = Feet3DataSource.getInstance(this);
        wifiManager = new Feet3WifiManager(this);

        context = getApplicationContext()  ;



        //Create the database?

        //Initialize buttons
        save_location_button = (Button) findViewById(R.id.save_location_button);
        delete_history_button = (Button) findViewById(R.id.delete_history_button);

        //Initialize listView
        stops_history_listView = (ListView) findViewById(R.id.stops_history_listView);



        //listView function
        stops_history_listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                //todo actions when click on an item
                //initiate activity positionInfo

                /*
                int pos = position+1;
                Toast.makeText(MainActivity.this, Integer.toString(pos)+ " Clicked", Toast.LENGTH_LONG).show();
                */

                HashMap<String, String> item = (HashMap<String, String>) stops_history_listView.getItemAtPosition(position);
                String date = item.get(ListViewAdapter.SECOND_COLUMN);//get the item from the list
              //  date = date.substring(7); // remove unnecessary chars

                Intent intent = new Intent(context, PositionInfoActivity.class);
                intent.putExtra("date", date);//get
                startActivity(intent);
            }
        });


        //Populate the historial
        populateList();



        //Button functions

        //Save location button
        save_location_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Position position = fLocationManager.getCurrentPosition();
                if (position == null){

                    Toast.makeText(MainActivity.this, R.string.no_location_detected, Toast.LENGTH_SHORT).show();

                }else {

                    Finding f = new Finding();


                    f.setLatitude(position.getLatitude());
                    f.setLongitude(position.getLongitude());

                    Calendar c = Calendar.getInstance();
                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy, hh;mm a");
                    String date = sdf.format(c.getTime());

                    f.setDate(date);



                    fDataSource.insertPosition(position);

                    //todo scan for wifi and add them
                    wifiManager.startScan();

                    List<Device> wifis = null;
                    try {
                        wifis = wifiManager.getWifiList();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    if(wifis == null || wifis.size() == 0){
                        ; //inserting finding without wifis
                        System.out.println("No wifi detected");
                        fDataSource.insertFinding(f);
                    }else {
                        //inserting a finding for each wifi detected
                        System.out.println("tamaño de wifis: " + wifis.size());
                        for (int i = 0; i < wifis.size(); i++) {
                            //todo how to associate findings with devices?
                            //maybe create a method on datasource with position and list of devices and
                            //do all the inserts there?
                            System.out.println("Wifi " + i + ": " + wifis.get(i).getName());
                        }
                        fDataSource.insertFindingDevices(f, wifis);
                    }


                    populateList();
                }

            }
        });


        //Delete history button
        delete_history_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fDataSource.clearHistory();
                populateList();
                Toast.makeText(MainActivity.this, R.string.deleted_history_toast, Toast.LENGTH_LONG).show();
            }
        });

    }

    protected void onPause(){
        wifiManager.onPause();
        super.onPause();

    }

    protected void onResume(){
        wifiManager.onResume();
        super.onResume();
    }



    private void populateList(){

        ArrayList<Finding> findingsList;
        list = new ArrayList<HashMap<String, String>>();

        findingsList = fDataSource.getAllFindings();
        //System.out.println(positionsList.size() + "se puebla la lista");

        for(Finding f: findingsList){
            HashMap<String,String> temp = new HashMap<String, String>();
            String coordinates = getResources().getString(R.string.latitude) +": " + f.getLatitude()
                    + " ," + getResources().getString(R.string.longitude) + ":" + f.getLongitude();
            temp.put(ListViewAdapter.FIRST_COLUMN, coordinates);

            temp.put(ListViewAdapter.SECOND_COLUMN, f.getDate());
            //temp.put(ListViewAdapter.SECOND_COLUMN, getResources().getString(R.string.date_string) + ": " + f.getDate());




            list.add(temp);

            //temp.put(ListViewAdapter.SECOND_COLUMN, p.getDate().toString());

        }

        ListViewAdapter adapter = new ListViewAdapter(this, list);
        stops_history_listView.setAdapter(adapter);
       // adapter.notifyDataSetChanged();



    }


}
