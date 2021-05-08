package com.khcpietro.smc.beacon;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.Region;

import java.util.UUID;

public class MainActivity extends AppCompatActivity implements BeaconConsumer {

    private static final String BEACON_LAYOUT = "m:0-3=4c000215,i:4-19,i:20-21,i:22-23,p:24-24";
    private static final String REGION_ID = "computer-room-1";
    private static final int SCAN_INTERVAL = 500;
    private static final int SCAN_PERIOD = 500;

    private BeaconManager beaconManager;

    private TextView tvBeacon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvBeacon = findViewById(R.id.beacon);

        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            initBeaconManager();
        } else {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            initBeaconManager();
        } else {
            Toast.makeText(this, "권한을 허용해주세요.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void initBeaconManager() {
        beaconManager = BeaconManager.getInstanceForApplication(this);
        BeaconParser beaconParser = new BeaconParser().setBeaconLayout(BEACON_LAYOUT);
        beaconManager.getBeaconParsers().add(beaconParser);
        beaconManager.setBackgroundMode(false);
        beaconManager.setForegroundBetweenScanPeriod(SCAN_INTERVAL);
        beaconManager.setForegroundScanPeriod(SCAN_PERIOD);
        beaconManager.bind(this);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBeaconServiceConnect() {
        beaconManager.removeAllRangeNotifiers();
        beaconManager.addRangeNotifier((beacons, region) -> {
            for (Beacon beacon : beacons) {
                String uuid = beacon.getId1().toString();
                double distance = beacon.getDistance();

                String log = "\nuuid: " + uuid + "\ndistance: " + distance;
                Log.d("Beacon", log);

                runOnUiThread(() -> {
                    tvBeacon.setText(log + "\n" + tvBeacon.getText());
                });
            }
        });

        try {
            beaconManager.startRangingBeaconsInRegion(new Region(REGION_ID, null, null, null));
        } catch (RemoteException e) {
            Toast.makeText(this, "비콘 서비스를 시작할 수 없습니다.", Toast.LENGTH_SHORT).show();
        }
    }
}