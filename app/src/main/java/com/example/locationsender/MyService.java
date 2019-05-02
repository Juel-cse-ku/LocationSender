package com.example.locationsender;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import static com.example.locationsender.App.CHANNEL_ID;


public class MyService extends Service {
    public static final String N_TITLE = "Ship Tracking";
    public static final String N_MESSAGE = "App is Running in Background.";

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(N_TITLE)
                .setContentText(N_MESSAGE)
                .setSmallIcon(R.drawable.ic_directions_boat_black_24dp)
                .setContentIntent(pendingIntent)
                .build();

        startForeground(1, notification);

        return START_REDELIVER_INTENT;
}

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

   @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
