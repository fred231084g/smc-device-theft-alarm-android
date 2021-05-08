package com.khcpietro.smc.grandtheftalarm;

import android.app.Application;

public class App extends Application {

    public void onCreate() {
        super.onCreate();


        BeaconService.startBeaconService(this);
    }
}
