package com.luneraremote;

import android.Manifest;
import android.app.AlertDialog;
import android.bluetooth.BluetoothManager;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.BeaconTransmitter;

import java.util.Collections;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    LinearLayout occ, occ_lost, dim, blink, con_wifi;

    public BeaconTransmitter beaconTransmitter;
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;

    Timer timer;
    TimerTask timerTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BluetoothManager mBluetoothManager = (BluetoothManager) this.getSystemService(BLUETOOTH_SERVICE);
        if (!mBluetoothManager.getAdapter().isEnabled()) {
            mBluetoothManager.getAdapter().enable();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //we need to get user to grant access to the ACCESS_COARSE_LOCATION, beacon scanning requires this
            if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // so if its not granted yet, we do need the request it
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("This app needs location access");
                builder.setMessage("Please grant location access so this app can detect beacons.");
                builder.setPositiveButton(android.R.string.ok, null);
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
                        }
                    }
                });
                builder.show();
            }
        }
        occ = (LinearLayout) findViewById(R.id.occupancy);
        occ_lost = (LinearLayout) findViewById(R.id.occupancy_lost);
        dim = (LinearLayout) findViewById(R.id.dim);
        blink = (LinearLayout) findViewById(R.id.blink);
        con_wifi = (LinearLayout) findViewById(R.id.configure_wifi);

        occ.setOnClickListener(this);
        occ_lost.setOnClickListener(this);
        dim.setOnClickListener(this);
        blink.setOnClickListener(this);
        con_wifi.setOnClickListener(this);
        int result = BeaconTransmitter.checkTransmissionSupported(getApplicationContext());
        Toast.makeText(this, "Device info " + result, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.occupancy:
                snackBarShow("ON Message Sent.");
                StartAdvertiser(
                        0x528,
                        "4c554e45-5241-534d-4152-544c49474854",
                        "05",
                        "",
                        -69);
                break;
            case R.id.occupancy_lost:
                if (beaconTransmitter != null) {
                    beaconTransmitter.stopAdvertising();
                }
                /*snackBarShow("OFF Message Sent.");
                StartAdvertiser(
                        0x528,
                        "4c554e45-5241-534d-4152-544c49474854",
                        "06",
                        "",
                        -69);*/
                break;
            case R.id.dim:
                snackBarShow("Dim Request NOT IMPLEMENTED.");
                //StartAdvertiser();
                break;
            case R.id.blink:
                snackBarShow("Blink Request NOT IMPLEMENTED.");
                //StartAdvertiser();
                break;
            case R.id.configure_wifi:
                snackBarShow("WiFi Settings NOT IMPLEMENTED.");
                //StartAdvertiser();
                break;
        }
    }

    public void snackBarShow(String text) {
        CoordinatorLayout coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinator);
        Snackbar snackbar = Snackbar.make(coordinatorLayout, text, Snackbar.LENGTH_LONG);
        View view = snackbar.getView();
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) view.getLayoutParams();
        params.gravity = Gravity.TOP;
        view.setLayoutParams(params);
        snackbar.show();

    }

    /**
     * This method will be used to send advertisement Please use this with below params
     *
     * @param MFGID
     * @param beaconID1
     * @param beaconID2
     * @param beaconID3
     * @param ReferenceRSSI
     */
    private void StartAdvertiser(Integer MFGID,
                                 String beaconID1, String beaconID2,
                                 String beaconID3, Integer ReferenceRSSI) {

        //API is not available before Lollipop
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Start(MFGID, beaconID1, beaconID2, beaconID3, ReferenceRSSI);
        } else {
            Log.e("Ex", "Advertisement Not supported by platform version : " + Build.VERSION.SDK_INT);
        }
    }


    public void Start(Integer MFGID, String beaconID1, String beaconID2, String beaconID3, Integer ReferenceRSSI) {

        Beacon beacon = new Beacon.Builder()
                .setManufacturer(MFGID)
                .setId1(beaconID1)
                .setId2(beaconID2)
                .setId3(beaconID3)
                .setRssi(ReferenceRSSI)
                .setTxPower(-59)
                .setDataFields(Collections.singletonList(0L))
                .build();
        BeaconParser beaconParser = new BeaconParser().setBeaconLayout("m:2-3=beac,i:4-19,i:20-20,i:21-23,p:24-24,d:25-25");
        beaconTransmitter = new BeaconTransmitter(MainActivity.this, beaconParser);

        if (!beaconTransmitter.isStarted()) {
//            beaconTransmitter.startAdvertising(beacon, new AdvertiseCallback() {
//                @Override
//                public void onStartSuccess(AdvertiseSettings settingsInEffect) {
//                    super.onStartSuccess(settingsInEffect);
//                    Log.e("StartSuccess", settingsInEffect.toString() + " ");
//                }
//
//                @Override
//                public void onStartFailure(int errorCode) {
//                    super.onStartFailure(errorCode);
//                    if (errorCode == ADVERTISE_FAILED_INTERNAL_ERROR) {
//                        Log.e("Errorcode", "1");
//                    } else if (errorCode == ADVERTISE_FAILED_ALREADY_STARTED) {
//                        Log.e("Errorcode", "2");
//                    } else if (errorCode == ADVERTISE_FAILED_DATA_TOO_LARGE) {
//                        Log.e("Errorcode", "3");
//                    } else if (errorCode == ADVERTISE_FAILED_FEATURE_UNSUPPORTED) {
//                        Log.e("Errorcode", "4");
//                    } else if (errorCode == ADVERTISE_FAILED_TOO_MANY_ADVERTISERS) {
//                        Log.e("Errorcode", "5");
//                    }
//                }
//            });
            beaconTransmitter.startAdvertising(beacon);
        }
        /*
         *  Timer will stop after 2 Seconds, Change 2 will other numbers for following seconds
         */
//        reScheduleTimer(2);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_COARSE_LOCATION: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //"coarse location permission granted");
                } else {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Functionality limited");
                    builder.setMessage("Since location access has not been granted, this app will not be able to discover beacons when in the background.");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

                        @Override
                        public void onDismiss(DialogInterface dialog) {
                        }

                    });
                    builder.show();
                }
                return;
            }
        }
    }

    public void reScheduleTimer(int duration) {
        timer = new Timer("alertTimer", true);
        timerTask = new MyTimerTask();
        timer.schedule(timerTask, 1000L, duration * 1000L);
    }

    private class MyTimerTask extends TimerTask {

        @Override
        public void run() {
            beaconTransmitter.stopAdvertising();
        }
    }
}
