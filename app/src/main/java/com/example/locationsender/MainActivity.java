package com.example.locationsender;

import android.Manifest;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    public static final int INTERVAL_TIME = 15;  // time in seconds

//    private FusedLocationProviderClient client;

    private TextView textView;

    private String latitude;
    private String longitude;
    private String androidId;
    private String heading;
    private float azimuth;


    private SensorEventListener sensorEventListener;
    private SensorManager sensorManager;
    private Sensor sensorAccelerometer;
    private Sensor sensorMagnetic;

    float[] mGravity;
    float[] mGeomagnetic;

    private static String TAG = "MainActivity";
    private static String NOTIFICATION_TITLE = "Ship Tracking";
    private static String NOTIFICATION_MESSAGE = "app is running in background...";
    private NotificationHelper notificationHelper;


    String url = "http://control.jahajibd.com/api_req/Ship/GpsTracking";
//    String url = "http://192.168.0.106/shipTracking/post.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // disable Doze mood
//        turnOffDozeMode();


        // background service
//        Intent serviceIntent = new Intent(this, MyService.class);
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            startForegroundService(serviceIntent);
//        } else {
//            startService(serviceIntent);
//        }


        //*****************************

//        requestPermission();
//        client = LocationServices.getFusedLocationProviderClient(this);

        // preodic running

//        Thread thread = new Thread(){
//            @Override
//            public void run() {
//                while (!isInterrupted()){
//                    try {
//                        Thread.sleep(INTERVAL_TIME *1000);
//
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                getPosition();
//                            }
//                        });
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        };
//        thread.start();


//        getPosition();
//
//
//        // for heading
//        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
//
//        sensorAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
//        sensorManager.registerListener(MainActivity.this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
//
//        sensorMagnetic = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
//        sensorManager.registerListener(MainActivity.this, sensorMagnetic, SensorManager.SENSOR_DELAY_NORMAL);
    }

//    private void runInNotification(String title, String message) {
//        NotificationCompat.Builder nb = notificationHelper.getChannelNotification(title, message);
//        notificationHelper.getManager().notify(1, nb.build());
//
//    }

    public void startService(View view) {
//        Intent serviceIntent = new Intent(this, MyService.class);
//        serviceIntent.putExtra("inputExtra", "App is Running...");
//        startService(serviceIntent);
    }

//     public void stopService(View view) {
//         Intent serviceIntent = new Intent(this, MyService.class);
//         stopService(serviceIntent);
//         Toast.makeText(this, "Service Stopped.", Toast.LENGTH_SHORT).show();
//     }

    @Override
    public void onSensorChanged(SensorEvent event) {

        textView = findViewById(R.id.tv_location);

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            mGravity = event.values;
        }

        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            mGeomagnetic = event.values;
        }

        if (mGravity != null && mGeomagnetic != null) {
            float R[] = new float[9];
            float I[] = new float[9];

            if (SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic)) {
                float orientation[] = new  float[3];
                SensorManager.getOrientation(R, orientation);

                azimuth = orientation[0];
            }
        }

        heading = String.valueOf((float)(Math.toDegrees(azimuth)+ 360) % 360);
        textView.setText(new StringBuilder().append("Latitude = ").append(latitude).append("\nLongitude = ").append(longitude).append("\nUUID = ").append(androidId).append("\nHeading: ").append(heading).toString());
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

//    private  void  requestPermission(){
//        ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION}, 1);
//    }

    private void post_to_server()
    {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(getApplicationContext(), response, Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, error+"", Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                params.put("serial", androidId);
                params.put("lat", latitude);
                params.put("lng", longitude);
                params.put("heading", heading);

                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void getPosition() {
        ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION}, 1);

//        private FusedLocationProviderClient client;
        FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(this);


        if (ActivityCompat.checkSelfPermission(MainActivity.this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        client.getLastLocation().addOnSuccessListener(MainActivity.this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null)
                {
                    textView = findViewById(R.id.tv_location);

                    latitude = String.valueOf(location.getLatitude());
                    longitude = String.valueOf(location.getLongitude());
                    androidId = Settings.Secure.getString(getContentResolver(),
                            Settings.Secure.ANDROID_ID);
                }
            }
        });
    }

    public void turnOffDozeMode(){  //you can use with or without passing context
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Intent intent = new Intent();
            String packageName = getPackageName();
            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            if (pm.isIgnoringBatteryOptimizations(packageName)) // if you want to desable doze mode for this package
                intent.setAction(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
            else { // if you want to enable doze mode
                intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                intent.setData(Uri.parse("package:" + packageName));
            }
            startActivity(intent);
        }
    }
}
