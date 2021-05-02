package com.khcpietro.smc.grandtheftalarm.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.khcpietro.smc.grandtheftalarm.BeaconService;
import com.khcpietro.smc.grandtheftalarm.R;
import com.khcpietro.smc.grandtheftalarm.Util;

public class MainActivity extends AppCompatActivity {

    public static void start(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BeaconService.startBeaconService(this);

        // TEST;
//        Util.showAlertView(this);
    }
}
