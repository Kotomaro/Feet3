package main.feet3;

import android.Manifest;
import android.app.Activity;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

import java.util.List;

/**
 * Created by D on 21/10/2016.
 */

public class Feet3ActivityRecognizeManager extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    private int defaultTime = 1000 * 60 * 3; //time in miliseconds to request updates (default time)
    private int time; //time to request updates (will get drawn from sharedpreferences)
    private Context context;
    private Activity activity;
    private PendingIntent callbackIntent;
    private GoogleApiClient mGoogleApiClient;

    private static boolean stopped = false;
    private static boolean registered = false;
    private static boolean moving = false;

    private static MainActivity mainActivity;



    public Feet3ActivityRecognizeManager(){

    }


    public Feet3ActivityRecognizeManager(Context context, Activity activity){

        this.context = context;
        this.activity = activity;
        mainActivity = (MainActivity) activity;

        SharedPreferences preferences = context.getSharedPreferences(Feet3DataSource.PREF_NAME, MODE_PRIVATE);
        time = preferences.getInt("stop_detection_time", defaultTime);

        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addApi(ActivityRecognition.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

      //  System.out.println("Creado activityrecog");

       //recognizedService = new ActivityRecognizedService();

        startConnection();


    }

    private void startConnection(){
        mGoogleApiClient.connect();
      //  System.out.println("Conectado activityrecog");
    }



    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {



        Intent intent = new Intent(context, ActivityRecognizedService.class);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates(mGoogleApiClient, time, pendingIntent);

       // System.out.println("On connected ");
    }


    public void onPause(){
        if(mGoogleApiClient.isConnected()){
          //  mGoogleApiClient.disconnect();
            Intent intent = new Intent(context, ActivityRecognizedService.class);
            PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            ActivityRecognition.ActivityRecognitionApi.removeActivityUpdates(mGoogleApiClient, pendingIntent);

        }
    }

    public void onDestroy(){
        if(mGoogleApiClient.isConnected()) {
             mGoogleApiClient.disconnect();
            Intent intent = new Intent(context, ActivityRecognizedService.class);
            PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            ActivityRecognition.ActivityRecognitionApi.removeActivityUpdates(mGoogleApiClient, pendingIntent);
        }

    }


    public void onResume(){
        startConnection();
    }


    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        System.out.println("Connection failed!");
    }

    private static void handleDetectedActivities(List<DetectedActivity> activityList){

        for(DetectedActivity activity: activityList){
            if(activity.getType() == DetectedActivity.STILL && activity.getConfidence() >= 90){
                if(stopped){//if we detect twice in a row being stopped, mark as stop

                    moving = false; //we are not moving
                    if(!registered) {//check if we already registered the stop. If not, register it
                        System.out.println("crear parada");
                        mainActivity.registerStop();
                        registered = true;//mark this stop as registered
                    }else{
                        //if it was already registered, don't register again
                      //  System.out.println("parada ya registrada");
                    }
                }else{
                    //we detected a stop for the first time, change the variable to true
                    //so we can see if we were stopped for a while
                stopped = true;
                }

           // System.out.println("toy parao, stopped: "+stopped);
        }else{
                if(moving = true) {//If we detected we were moving twice, we are moving, reset
                    //the user is not stopped, so reset every variable
                    //todo maybe make two checks in a row before in case the user is using his phone
                    stopped = false;
                    registered = false;
                }else{//we detected movement once, set moving to true
                    moving = true;
                }
              //  System.out.println("no toy parao, stopped: "+stopped);
          //  System.out.println("Activity: " + activity.getType());
          //  System.out.println("Confidence: " + activity.getConfidence());

          }
        }
    }




    public static class ActivityRecognizedService extends IntentService {

        public ActivityRecognizedService(){
            super("ActivityRecognizedService");
        }

        public ActivityRecognizedService(String name){
            super(name);
        }

        @Override
        protected void onHandleIntent(Intent intent) {
            //System.out.println("Llega a onhandleintent");
            if(ActivityRecognitionResult.hasResult(intent)){
                ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);
                handleDetectedActivities(result.getProbableActivities());
            }

        }
    }
}
