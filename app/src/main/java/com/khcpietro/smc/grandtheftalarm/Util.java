package com.khcpietro.smc.grandtheftalarm;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.PixelFormat;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.khcpietro.smc.grandtheftalarm.network.ApiClient;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Random;

public class Util {

    private static WeakReference<View> alertView = null;
    private static final Object alertViewLock = new Object();

    private static final MediaPlayer mediaPlayer = new MediaPlayer();

    public static void enableBluetooth() {
        BluetoothAdapter.getDefaultAdapter().enable();
    }

    public static void disableBluetooth() {
        BluetoothAdapter.getDefaultAdapter().disable();
    }

    public static void reportTheft() {
        try {
            ApiClient.getService().reportTheft("test-id-" + new Random().nextInt(100)).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void playAlarm(Context context) {
        // TODO; set volume max
        Log.d("Util", "playAlarm");

        try {
            AssetFileDescriptor assetFd = context.getAssets().openFd("beep.mp3");
            mediaPlayer.reset();
            mediaPlayer.setDataSource(assetFd.getFileDescriptor(), assetFd.getStartOffset(), assetFd.getLength());
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException ignored) {
            Log.w("Util", "can't find beep.mp3 asset file");

            Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
            Ringtone ringtone = RingtoneManager.getRingtone(context.getApplicationContext(), alarmUri);
            ringtone.play();
        }
    }

    public static void showAlertView(Context context) {
        synchronized (alertViewLock) {
            if (alertView != null && alertView.get() != null) {
                return;
            }

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

            alertView = new WeakReference<>(inflater.inflate(R.layout.alert_view, null));

            int layoutParamsType;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                layoutParamsType = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
            } else {
                layoutParamsType = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
            }

            DisplayMetrics displayMetrics = new DisplayMetrics();
            windowManager.getDefaultDisplay().getMetrics(displayMetrics);
            int width = displayMetrics.widthPixels;
            int height = displayMetrics.heightPixels;

            WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                    width,
                    height,
                    layoutParamsType,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
                            WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                    PixelFormat.TRANSLUCENT);

            params.gravity = Gravity.TOP;

            alertView.get().setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

            windowManager.addView(alertView.get(), params);
        }
    }

    public static void hideAlertView(Context context) {
        synchronized (alertViewLock) {
            if (alertView == null || alertView.get() == null) {
                return;
            }

            WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

            windowManager.removeView(alertView.get());
            alertView.clear();
            alertView = null;
        }
    }
}
