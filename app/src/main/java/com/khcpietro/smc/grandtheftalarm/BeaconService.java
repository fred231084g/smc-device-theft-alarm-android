package com.khcpietro.smc.grandtheftalarm;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.khcpietro.smc.grandtheftalarm.ui.MainActivity;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Region;

public class BeaconService extends Service implements BeaconConsumer {
    private static final String BEACON_LAYOUT = "m:0-3=4c000215,i:4-19,i:20-21,i:22-23,p:24-24";
    private static final String BEACON_ID = "AB8190D5-D11E-4941-ACC4-42F30510B408";
    private static final String REGION_ID = "computer-room-1";

    private static final double ALARM_DISTANCE = 4.0;
    private static final int SCAN_PERIOD = 300;
    private static final int SCAN_INTERVAL = 100;

    private static final String CHANNEL_ID = "gta-notice-channel";
    private static final int NOTIFICATION_ID = 100;

    private BeaconManager beaconManager;
    private final Handler handler = new Handler(Looper.getMainLooper());

    @Override
    public void onCreate() {
        super.onCreate();

        Util.enableBluetooth();
        initService();
    }

    @Override
    public void onDestroy() {
        beaconManager.unbind(this);

        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void initService() {
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                0,
                new Intent(this, MainActivity.class),
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        Notification.Builder builder;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "도난방지 시스템", NotificationManager.IMPORTANCE_HIGH);
            channel.setLightColor(Color.BLUE);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);

            builder = new Notification.Builder(this, CHANNEL_ID);
        } else {
            builder = new Notification.Builder(this);
        }

        Notification notification = builder
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("GTA")
                .setContentText("도난방지 시스템이 작동 중입니다.")
                .setContentIntent(pendingIntent)
                .build();
        startForeground(NOTIFICATION_ID, notification);

        beaconManager = BeaconManager.getInstanceForApplication(this.getApplicationContext());
        BeaconParser beaconParser = new BeaconParser().setBeaconLayout(BEACON_LAYOUT);
        beaconManager.getBeaconParsers().add(beaconParser);
        beaconManager.enableForegroundServiceScanning(notification, NOTIFICATION_ID);
        beaconManager.setBackgroundMode(false);
        beaconManager.setForegroundBetweenScanPeriod(SCAN_INTERVAL);
        beaconManager.setForegroundScanPeriod(SCAN_PERIOD);
        beaconManager.setEnableScheduledScanJobs(false);
        beaconManager.bind(this);
    }

    @Override
    public void onBeaconServiceConnect() {
        // TODO; 구현하기
    }

    private void onTheftDetected() {
        // TODO; 구현하기
    }

    public static void startBeaconService(Context context) {
        Context appContext = context.getApplicationContext();
        Intent intent = new Intent(appContext, BeaconService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            appContext.startForegroundService(intent);
        } else {
            appContext.startService(intent);
        }
    }
}
