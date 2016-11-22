package main.feet3;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import main.feet3.data.Feet3DataSource;

/**
 * Created by David on 20/11/2016.
 */


public class EditPositionActivity extends AppCompatActivity {

    private TextView title_name_textView;
    private EditText name_editText;
    private Button save_button;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_name);

        title_name_textView = (TextView) findViewById(R.id.title_name_textView);
        name_editText = (EditText) findViewById(R.id.name_editText);
        save_button = (Button) findViewById(R.id.networks_save_button);

        context=this.getApplicationContext();

        Bundle b = getIntent().getExtras();
        final String name = b.getString("name");
        final double latitude = b.getDouble("latitude");
        final double longitude = b.getDouble("longitude");
        name_editText.setText(name);


        save_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(             !(name_editText.getText().toString().equals(""))//not empty
                                && !(name.equals(name_editText.getText().toString()))
                        ) {//name changed
                    Feet3DataSource dataSource = Feet3DataSource.getInstance(context);
                    dataSource.updatePositionName(name_editText.getText().toString(), latitude, longitude);
                    finish();
                }
            }
        });
    }
}
