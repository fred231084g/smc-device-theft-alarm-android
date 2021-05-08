package com.khcpietro.smc.grandtheftalarm.ui;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.khcpietro.smc.grandtheftalarm.R;
import com.khcpietro.smc.grandtheftalarm.Util;

import java.util.Collections;
import java.util.List;

public class IntroActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        Util.generateDeviceId(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        checkRuntimePermissions();
    }

    private void checkRuntimePermissions() {
        Dexter.withContext(this)
                .withPermissions(Collections.singletonList(Manifest.permission.ACCESS_FINE_LOCATION))
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report != null && report.areAllPermissionsGranted()) {
                            checkAlertWindowPermission();
                        } else {
                            Toast.makeText(IntroActivity.this, "권한을 허용해주세요.", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                        if (permissionToken != null) {
                            permissionToken.continuePermissionRequest();
                        }
                    }
                }).check();
    }

    private void checkAlertWindowPermission() {
        if (Settings.canDrawOverlays(this)) {
            startMainActivity();
        } else {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
            intent.setData(Uri.parse("package:" + getPackageName()));
            startActivity(intent);
        }
    }

    private void startMainActivity() {
        MainActivity.start(IntroActivity.this);
        finish();
    }
}