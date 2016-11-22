package main.feet3;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import main.feet3.data.Feet3DataSource;

public class PositionInfoActivity extends AppCompatActivity {


    //Interface elements
    private Button map_button;
    private TextView title_textView;
    private ListView stops_history_listView, networks_history_listView;
    private ArrayAdapter<Finding> stops_history_arrayAdapter;
    private ArrayAdapter<Device> networks_history_arrayAdapter;
    private ArrayList<HashMap<String, String>> stops_history_arrayList;
    private ArrayList<HashMap<String, String>> networks_history_arrayList;
    private String date;
    private Feet3DataSource fDataSource;
    private Context context;
    private Position position;

    //Position is the physical location we are showing
    //Findings are all the registered stops we have
    //for each finding, there is a list of wifis


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_position_info);
        this.context = getApplicationContext();


        //Utilities
        Bundle b = getIntent().getExtras();
        date = b.getString("date");
        //  System.out.println("Date del bundle: "+date);
        fDataSource = Feet3DataSource.getInstance(this);
        stops_history_arrayList = new ArrayList<>();
        networks_history_arrayList = new ArrayList<>();


        //Initialize elements
        map_button = (Button) findViewById(R.id.posinfo_map_button);
        title_textView = (TextView) findViewById(R.id.posInfo_title);
        stops_history_listView = (ListView) findViewById(R.id.posInfo_stopHistory_list);
        networks_history_listView = (ListView) findViewById(R.id.posInfo_networks_list);

        //listview click function
        stops_history_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {

                //initiate activity positionInfo


                HashMap<String, String> item = (HashMap<String, String>) stops_history_listView.getItemAtPosition(position);
                date = item.get(ListViewAdapter.FIRST_COLUMN);//get the item from the list

                populateNetworkList(date);


            }
        });


        //map button function
        map_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MapsActivity.class);

                startActivity(intent);
            }
        });

        //populate the lists
        populateStopHistoryList();
        populateNetworkList(date);


        networks_history_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                //todo actions when click on an item
                //initiate activity positionInfo


                 HashMap<String, String> item = (HashMap<String, String>) networks_history_listView.getItemAtPosition(position);
                String name = item.get(ListViewAdapter.FIRST_COLUMN);
                String mac_address = item.get(ListViewAdapter.SECOND_COLUMN);//get the item from the list
               // Device device = fDataSource.getDeviceByMac(mac_address);

                Intent intent = new Intent(context, EditNetworkActivity.class);
                intent.putExtra("name", name);
                intent.putExtra("mac_address", mac_address);
                startActivityForResult(intent, 0);
            }
        });


        title_textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, EditPositionActivity.class);
                intent.putExtra("name", title_textView.getText().toString());
                intent.putExtra("latitude", position.getLatitude());
                intent.putExtra("longitude", position.getLongitude());

                startActivityForResult(intent, 1);
            }
        });
    }



    private void setTitle(){
        if(position.getName() != null && !position.getName().equals("")
                && !position.getName().equals("null")){
            title_textView.setText(position.getName());

        }else{
            String title = position.getLatitude() + ", " + position.getLongitude();
            title_textView.setText(title);
        }
    }




    private void populateStopHistoryList() {
        position = fDataSource.findPositionByDate(date);

        stops_history_arrayList = new ArrayList<>();

        //    System.out.println("position devuelta: " + p.getLongitude());

        List<Finding> findings = new ArrayList<>();
        findings = fDataSource.getFindingsByPosition(position.getLatitude(), position.getLongitude());
        //   System.out.println("tamaño de lista findings: "+findings.size());

        for (Finding f : findings) {
            HashMap<String, String> temp = new HashMap<String, String>();

            temp.put(ListViewAdapter.FIRST_COLUMN, f.getDate());
            temp.put(ListViewAdapter.SECOND_COLUMN, "");

            stops_history_arrayList.add(temp);

            //    System.out.println("añadida fecha: " +f.getDate());
        }

        ListViewAdapter adapter = new ListViewAdapter(this, stops_history_arrayList);
        stops_history_listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        setTitle();
    }


    private void populateNetworkList(String date) {
        networks_history_arrayList = new ArrayList<>(); //clear the list

        List<Device> deviceList;


        deviceList = fDataSource.getDevicesByFinding(position.getLatitude(), position.getLongitude(), date);
       // System.out.println("Pasa de getdevicesByfinding, tamaño de lista: "+deviceList.size());

        for(Device d: deviceList){
          //  System.out.println("Iterando devices");
            HashMap<String, String> temp = new HashMap<>();
            temp.put(ListViewAdapter.FIRST_COLUMN, d.getName());
        //    System.out.println("Nombre de device: " +d.getName());
            temp.put(ListViewAdapter.SECOND_COLUMN, d.getMac_address());
            networks_history_arrayList.add(temp);
        }
       // System.out.println("Salgo de la iteracion");
        ListViewAdapter adapter = new ListViewAdapter(this, networks_history_arrayList);
        networks_history_listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        populateNetworkList(date);
        setTitle();
    };
}




