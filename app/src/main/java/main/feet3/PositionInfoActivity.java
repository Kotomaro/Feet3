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

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
        stops_history_listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                //todo actions when click on an item
                //initiate activity positionInfo


                HashMap<String, String> item = (HashMap<String, String>) stops_history_listView.getItemAtPosition(position);
                String date = item.get(ListViewAdapter.FIRST_COLUMN);//get the item from the list

                populateNetworkList(date);


            }
        });


        //populate the lists
        populateStopHistoryList();
        populateNetworkList(date);


        //cuando pulse un elemento del historial de paradas, buscar todas las wifis de esa parada y mostrarlas
        //por defecto poner las wifis encontradas en la fecha de esa parada


        //When another finding is clicked, repopulate network list with the ones of that finding.


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

        String title = position.getLatitude() + ", " + position.getLongitude();
        title_textView.setText(title);
    }


    private void populateNetworkList(String date) {
        networks_history_arrayList = new ArrayList<>(); //clear the list

        List<Device> deviceList;


        deviceList = fDataSource.getDevicesByFinding(position.getLatitude(), position.getLongitude(), date);
       // System.out.println("Pasa de getdevicesByfinding, tamaño de lista: "+deviceList.size());

        for(Device d: deviceList){
            System.out.println("Iterando devices");
            HashMap<String, String> temp = new HashMap<>();
            temp.put(ListViewAdapter.FIRST_COLUMN, d.getName());
            System.out.println("Nombre de device: " +d.getName());
            temp.put(ListViewAdapter.SECOND_COLUMN, d.getMac_address());
            networks_history_arrayList.add(temp);
        }
        System.out.println("Salgo de la iteracion");
        ListViewAdapter adapter = new ListViewAdapter(this, networks_history_arrayList);
        networks_history_listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

    }
}




