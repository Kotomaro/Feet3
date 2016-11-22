package main.feet3.utils;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;

import java.util.ArrayList;
import java.util.List;

import main.feet3.Device;
import main.feet3.data.Feet3DataSource;

/**
 * Created by David on 07/08/2016.
 */
public class Feet3WifiManager {

   private WifiManager mWifiManager;
   private List<ScanResult> wifiScanList;
   private WifiScanReceiver wifiReceiver;
   private List<Device> resultsList;
   private Activity activity;
   private Boolean scanFinished;



    public Feet3WifiManager(Activity activity) {

        this.activity = activity;
        mWifiManager = (WifiManager) activity.getSystemService(Context.WIFI_SERVICE);

        wifiReceiver = new WifiScanReceiver();
        activity.registerReceiver(wifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

    }





    public void startScan(){
        //TODO permissions
        scanFinished = false;
        mWifiManager.startScan();
        System.out.println("Empieza el scan");


    }

    public void onPause(){
        //todo check if necessary
        activity.unregisterReceiver(wifiReceiver);


    }

    public void onResume(){
        activity.registerReceiver(wifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));    }


    //todo testing
    public List<Device> getWifiList() throws InterruptedException {
      //todo wait until the list gets populated
        resultsList = new ArrayList<>();



            if(wifiScanList != null) {

                for (int i = 0; i < wifiScanList.size(); i++) {
                    Device d = new Device();
                    d.setName(wifiScanList.get(i).SSID);
                    d.setMac_address(wifiScanList.get(i).BSSID);
                    d.setType(Feet3DataSource.DeviceType.WIFI);
                    resultsList.add(d);
                }
            }

        return resultsList;
    }



    class WifiScanReceiver extends BroadcastReceiver {
        public void onReceive(Context c, Intent intent) {
            wifiScanList = mWifiManager.getScanResults();
            scanFinished = true;
            System.out.println("Pasa por onreceive");
        }

        ;
    }

    public boolean isScanFinished(){
        System.out.println("scanfinished: "+ scanFinished);
        return scanFinished;
    }
}
