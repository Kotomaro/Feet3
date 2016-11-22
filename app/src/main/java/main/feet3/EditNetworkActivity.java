package main.feet3;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import main.feet3.data.Feet3DataSource;

public class EditNetworkActivity extends AppCompatActivity {

    private TextView title_name_textView, title_mac_address_textView, mac_address_textView;
    private EditText name_editText;
    private Button save_button;
    private Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_network);

        title_name_textView = (TextView) findViewById(R.id.title_name_textView);
        title_mac_address_textView = (TextView) findViewById(R.id.title_mac_address_textView);
        name_editText = (EditText) findViewById(R.id.name_editText);
        mac_address_textView = (TextView) findViewById(R.id.mac_adress_textView);
        save_button = (Button) findViewById(R.id.networks_save_button);


        context = this.getApplicationContext();


        Bundle b = getIntent().getExtras();
        final String name = b.getString("name");
        name_editText.setText(b.getString("name"));

        mac_address_textView.setText(b.getString("mac_address"));


        save_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (
                        !(name_editText.getText().toString().equals(""))//not empty
                        && !(name.equals(name_editText.getText().toString()))
                        ){//name changed

                    Feet3DataSource dataSource = Feet3DataSource.getInstance(context);
                    dataSource.updateDeviceName(name, mac_address_textView.getText().toString(), name_editText.getText().toString());
                    finish();


                }
            }
        });
    }
}
