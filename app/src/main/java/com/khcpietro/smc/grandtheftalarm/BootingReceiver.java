package com.khcpietro.smc.grandtheftalarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.karumi.dexter.Dexter;

public class BootingReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        BeaconService.startBeaconService(context);
    }
}