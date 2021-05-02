package com.khcpietro.smc.grandtheftalarm;

import android.Manifest;
import android.app.Application;

import java.util.Collections;
import java.util.List;

public class App extends Application {

    public static List<String> PERMISSIONS = Collections.singletonList(
            Manifest.permission.ACCESS_FINE_LOCATION
    );
}
