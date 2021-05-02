package com.khcpietro.smc.grandtheftalarm;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;
import android.util.Log;
import android.view.WindowManager;
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
    private static final String CHANNEL_ID = "gta-notice-channel";
    private static final String REGION_ID = "computer-room-1";
    private static final int SERVICE_ID = 100;

    private static final double ALARM_DISTANCE = 0.5;
    private static final int SCAN_INTERVAL = 500;

    private BeaconManager beaconManager;
    private final Handler handler = new Handler(Looper.getMainLooper());

    @Override
    public void onCreate() {
        super.onCreate();

        Util.enableBluetooth();
        initService();
        initBeaconManager();

        // TEST;
//        Util.showAlertView(this);
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
                0
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
        startForeground(SERVICE_ID, notification);
    }

    private void initBeaconManager() {
        beaconManager = BeaconManager.getInstanceForApplication(this);
        BeaconParser beaconParser = new BeaconParser().setBeaconLayout(BEACON_LAYOUT);
        beaconManager.getBeaconParsers().add(beaconParser);
        beaconManager.setBackgroundMode(false);
        beaconManager.setForegroundBetweenScanPeriod(0);
        beaconManager.setForegroundScanPeriod(SCAN_INTERVAL);
        beaconManager.bind(this);
    }

    @Override
    public void onBeaconServiceConnect() {
        beaconManager.removeAllRangeNotifiers();
        beaconManager.addRangeNotifier((beacons, region) -> {
            for (Beacon beacon : beacons) {
                double distance = beacon.getDistance();
                Log.d("BEACON", "Distance: " + distance);
                if (distance >= ALARM_DISTANCE) {
                    handler.post(() -> Util.showAlertView(this));
                    Util.reportTheft();
                    Util.playAlarm(this);
                } else {
                    handler.post(() -> Util.hideAlertView(this));
                }
            }
        });

        try {
            beaconManager.startRangingBeaconsInRegion(new Region(REGION_ID, null, null, null));
        } catch (RemoteException e) {
            Toast.makeText(this, "비콘 서비스를 시작할 수 없습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    public static void startBeaconService(Context context) {
        Intent intent = new Intent(context, BeaconService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent);
        } else {
            context.startService(intent);
        }
    }
}
