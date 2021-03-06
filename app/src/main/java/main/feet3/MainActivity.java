package main.feet3;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import main.feet3.data.Feet3DataSource;
import main.feet3.utils.Feet3ActivityRecognizeManager;
import main.feet3.utils.Feet3LocationManager;
import main.feet3.utils.Feet3WifiManager;

public class MainActivity extends AppCompatActivity {

    //interface elements
    private Button save_location_button;
    private Button delete_history_button;

    private ListView stops_history_listView;
    private ArrayList<HashMap<String, String>> list;

    private  Finding f;
    private List<Device> wifis;

    private Feet3LocationManager fLocationManager;
    private Feet3DataSource fDataSource;
    private Feet3WifiManager wifiManager;
    private Feet3ActivityRecognizeManager activityManager;
    Context context;




    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        //Initialize settings values by default
        PreferenceManager.setDefaultValues(this, R.xml.app_preferences, false);
        //---

        setContentView(R.layout.activity_main);

        //init the utilities
        fLocationManager = new Feet3LocationManager(this.getApplicationContext(), this);
        fDataSource = Feet3DataSource.getInstance(this);
        wifiManager = new Feet3WifiManager(this);
        activityManager = new Feet3ActivityRecognizeManager(this.getApplicationContext(), this);

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


                HashMap<String, String> item = (HashMap<String, String>) stops_history_listView.getItemAtPosition(position);
                String date = item.get(ListViewAdapter.SECOND_COLUMN);//get the item from the list


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

                registerStop();

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.headermenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        Intent intent = new Intent(context, Feet3Preferences.class);

        startActivityForResult(intent, 1);
        return true;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 1){
            //activityManager.
            System.out.println("activity result");
        }
    };

    protected void onPause(){
      //  wifiManager.onPause();
       // fLocationManager.onPause();
      //  activityManager.onPause();
        super.onPause();

    }

    protected void onResume(){
        wifiManager.onResume();
        fLocationManager.onResume();
       // activityManager.onResume();
        populateList();
        super.onResume();
    }

    public void registerStop(){
        Position position = fLocationManager.getCurrentPosition();
        if (position == null){
            Toast.makeText(MainActivity.this, R.string.no_location_detected, Toast.LENGTH_SHORT).show();
        }else {

            //check if location is close to an already registered one and merge
            ArrayList<Position> positions =  fDataSource.getAllPositions();

            Location location1, location2;
            location1 = new Location("");//initialize thefirst location to the new location detected
            location1.setLatitude(position.getLatitude());
            location1.setLongitude(position.getLongitude());
            location2 = new Location("");

            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);


            for(Position p: positions){//compare new location to stored location to see if they are close
                location2.setLatitude(p.getLatitude());
                location2.setLongitude(p.getLongitude());
                String aux = preferences.getString(getResources().getString(R.string.stop_detection_distance), String.valueOf(Feet3DataSource.MIN_DISTANCE));

                int min_distance = Integer.parseInt(aux);
             //   System.out.println("key: "+ getResources().getString(R.string.stop_detection_distance));
              //  System.out.println("Distancia minima:" + aux);

                if(location1.distanceTo(location2) < min_distance){
                    //they are close enough, consider them the same
                    position=p;
                    break;
                }
                //else introduce the new position (do nothing)
            }

            f = new Finding();

            f.setLatitude(position.getLatitude());
            f.setLongitude(position.getLongitude());

            Calendar c = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy, hh;mm a");
            String date = sdf.format(c.getTime());

            f.setDate(date);


            fDataSource.insertPosition(position);

            //Wifi handling
            wifiManager.startScan();
            /*
            try {
                Thread.sleep(2000);//wait until wifi list gets populated
                //todo check sleep time
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            */
            wifis = null;
            try {
                wifis = wifiManager.getWifiList();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            new Thread(new Runnable(){//this operation is done in a thread, so while we wait
                //for the list to populate, the UI is not frozen
                public void run(){
                    while(wifiManager.isScanFinished() == false){
                        System.out.println("a dormir");

                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    if(wifis == null || wifis.size() == 0){

                        System.out.println("No wifi detected");
                        fDataSource.insertFinding(f);
                    }else {
                        //inserting a finding for each wifi detected
                        System.out.println("tamaño de wifis: " + wifis.size());
                        for (int i = 0; i < wifis.size(); i++) {

                            System.out.println("Wifi " + i + ": " + wifis.get(i).getName());
                        }
                        fDataSource.insertFindingWithDevices(f, wifis);
                    }


                    runOnUiThread(new Runnable() {//needed to be on main thread to modify UI
                        @Override
                        public void run() {

                            //stuff that updates ui
                            populateList();//

                        }
                    });
                }
            }).start();


        }

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
