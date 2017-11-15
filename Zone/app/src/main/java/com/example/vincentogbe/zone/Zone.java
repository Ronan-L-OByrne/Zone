package com.example.vincentogbe.zone;

import android.app.FragmentManager;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.MenuInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.app.Fragment;

import com.estimote.coresdk.common.config.EstimoteSDK;
import com.estimote.coresdk.common.requirements.SystemRequirementsChecker;
import com.estimote.coresdk.recognition.packets.EstimoteTelemetry;
import com.estimote.coresdk.service.BeaconManager;

import java.util.List;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public class Zone extends AppCompatActivity{

    private BeaconManager beaconManager;
    public Double curTemp = 0.0;
    public Double curLight = 0.0;

    //private fragment_zone fz;

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_zone, menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zone);

        EstimoteSDK.initialize(getApplicationContext(), "zone-3rb", "10fe828d8a20bf4bd707fb4057757c48");

        beaconManager = new BeaconManager(getApplicationContext());

        beaconManager.setTelemetryListener(new BeaconManager.TelemetryListener()
        {
            @Override
            public void onTelemetriesFound(List<EstimoteTelemetry> telemetries)
            {
                fragment_zone fz = (fragment_zone) getSupportFragmentManager()
                        .findFragmentById(R.id.container);

                for (EstimoteTelemetry tlm : telemetries)
                {
                    curTemp = Math.round(tlm.temperature * 10.0) / 10.0;
                    curLight = Math.round(tlm.ambientLight * 10.0) / 10.0;
                    if (curTemp >= -30 && curTemp < -10)
                    {
                        if(tlm.motionState)
                        {
                            showNotification("DANGER FREEZING!", curTemp + "°C! Do not leave house unless necessary. Light: " + curLight + "lux");
                        }// end if
                        fz.curLight = curLight;
                        fz.curTemp = curTemp;
                        break;
                    }// end if
                    else if (curTemp >= -10 && curTemp < 0)
                    {
                        if(tlm.motionState)
                        {
                            showNotification("WARNING FREEZING", curTemp + "°C! Wrap up very warm before leaving. Light: " + curLight + "lux");
                        }// end if
                        fz.curLight = curLight;
                        fz.curTemp = curTemp;
                    }// end else if
                    else if (curTemp >= 0 && curTemp < 10)
                    {
                        if(tlm.motionState)
                        {
                            showNotification("Cold", curTemp + "°C. Make sure you have your jacket. Light: " + curLight + "lux");
                        }// end if
                        fz.curLight = curLight;
                        fz.curTemp = curTemp;
                        break;
                    }// end else if
                    else if (curTemp >= 10 && curTemp < 20)
                    {
                        if(tlm.motionState)
                        {
                            showNotification("Good", curTemp + "°C. Good temperature, be comfortable. Light: " + curLight + "lux");
                        }// end if
                        fz.curLight = curLight;
                        fz.curTemp = curTemp;
                        break;
                    }// end else if
                    else if (curTemp >= 20 && curTemp < 30)
                    {
                        if(tlm.motionState)
                        {
                            showNotification("Warm", curTemp + "°C. Dress light. Light: " + curLight + "lux");
                        }// end if
                        fz.curLight = curLight;
                        fz.curTemp = curTemp;
                        break;
                    }// end else if
                    else if (curTemp >= 30 && curTemp < 40)
                    {
                        if(tlm.motionState)
                        {
                            showNotification("WARNING HOT", curTemp + "°C! Very hot dress very light. Light: " + curLight + "lux");
                        }// end if
                        fz.curLight = curLight;
                        fz.curTemp = curTemp;
                        break;
                    }// end else if
                    else if (curTemp >= 40 && curTemp < 60)
                    {
                        if(tlm.motionState)
                        {
                            showNotification("DANGER HOT!", curTemp + "°C! Do not leave house unless necessary. Light: " + curLight + "lux");
                        }// end if
                        fz.curLight = curLight;
                        fz.curTemp = curTemp;
                        break;
                    }// end else if
                    else
                    {
                        if(tlm.motionState)
                        {
                            showNotification("PROBABLY DEAD!", curTemp + "°C! Unfortunately you are probably dead :( Light: " + curLight + "lux");
                        }// end if
                        fz.curLight = curLight;
                        fz.curTemp = curTemp;
                        break;
                    }// end else
                }// end for
                fz.renderWeather(fz.curWth);
            }//end onTelemetryFound
        });// end setTelemetryListener

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new fragment_zone())
                    .commit();
        }
    }

    @Override
    public void onStart()
    {
        super.onStart();
        beaconManager.connect(new BeaconManager.ServiceReadyCallback()
        {
            @Override public void onServiceReady()
            {
                //beaconManager.startLocationDiscovery();
                beaconManager.startTelemetryDiscovery();
            }// end on ServiceReady()
        });// end connect
    }// end onStart()

    @Override
    protected void onResume()
    {
        super.onResume();
        SystemRequirementsChecker.checkWithDefaultDialogs(this);
    }// end onResume

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        beaconManager.disconnect();
    }// end onDestroy

    private boolean notificationAlreadyShown = false;

    public void showNotification(String title, String message)
    {
        if (notificationAlreadyShown) { return; }

        Intent notifyIntent = new Intent(this, Zone.class);
        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivities(this, 0,
                new Intent[] { notifyIntent }, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new Notification.Builder(this)
                .setSmallIcon(android.R.drawable.ic_popup_reminder)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .build();

        notification.defaults |= Notification.DEFAULT_SOUND;
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(1, notification);
        notificationAlreadyShown = true;
    }// end showNotification()

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.change_city){
            showInputDialog();
        }
        else if(item.getItemId() == R.id.becon_option){
            fragment_zone wf = (fragment_zone) getSupportFragmentManager()
                    .findFragmentById(R.id.container);
            Log.d("BEF SWITCH:","" + wf.beacon + "");
            wf.beacon = !wf.beacon;
            Log.d("AFT SWITCH:","" + wf.beacon + "");
        }
        return false;
    }

    private void showInputDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Change city");
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);
        builder.setPositiveButton("Go", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                changeCity(input.getText().toString());
            }
        });
        builder.show();
    }

    public void changeCity(String city){
        fragment_zone wf = (fragment_zone) getSupportFragmentManager()
                .findFragmentById(R.id.container);
        wf.changeCity(city);
        new city_zone(this).setCity(city);
    }

}
