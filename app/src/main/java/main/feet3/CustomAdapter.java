package main.feet3;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

/**
 * Created by David on 26/07/2016.
 */
public class CustomAdapter extends ArrayAdapter<String> {

    private final Context context;
    private final int resourceID;

    public CustomAdapter(Context context, int resource, ArrayList<String> list){
        super(context, resource, list);

        this.context = context;
        this.resourceID = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(resourceID,parent, false);

        return rowView;

    }


}
