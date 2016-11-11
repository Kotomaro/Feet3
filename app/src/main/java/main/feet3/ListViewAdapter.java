package main.feet3;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by David on 02/07/2016.
 * Custom adapter for the listview in stops history(activity main)
 */
public class ListViewAdapter extends BaseAdapter {

    public static final String FIRST_COLUMN = "First";
    public static final String SECOND_COLUMN = "Seccond";

    public ArrayList<HashMap<String, String>> list;
    private Activity activity;
    private TextView txt1, txt2;



    public ListViewAdapter(Activity activity, ArrayList<HashMap<String, String>> list){
        super();
        this.activity=activity;
        this.list=list;



    }



    @Override
    public int getCount() {
        if (list != null){
            return list.size();
        }else{
            return 0;
        }


    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = activity.getLayoutInflater();

        if(convertView == null){
            convertView = inflater.inflate(R.layout.stops_history_table, null);

            txt1=(TextView) convertView.findViewById(R.id.stops_history_column1_textview);
            txt2=(TextView) convertView.findViewById(R.id.stops_history_column2_textview);

        }

        HashMap<String, String> map = list.get(position);

       // System.out.println("strings del layout");
      //  System.out.println(map.get(FIRST_COLUMN) + " ," + map.get(SECOND_COLUMN));
        txt1.setText(map.get(FIRST_COLUMN));
        txt2.setText(map.get(SECOND_COLUMN));

        return convertView;

    }

    @Override
     public int getViewTypeCount(){
        if((getCount() != 0))
        return getCount();
        return 1;

    }

    @Override
    public int getItemViewType(int position){
        return position;
    }
}
