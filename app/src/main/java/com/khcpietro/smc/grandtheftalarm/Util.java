package com.khcpietro.smc.grandtheftalarm;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.graphics.PixelFormat;
import android.media.AudioManager;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.khcpietro.smc.grandtheftalarm.network.ApiClient;
import com.khcpietro.smc.grandtheftalarm.ui.RentActivity;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Random;

import static android.content.Context.AUDIO_SERVICE;

public class Util {

    private static final String PREF_NAME = "prefs";
    private static final String PREF_DEVICE_ID = "pref_device_id";
    private static final String PREF_RENT_USER_NAME = "pref_rent_user_name";

    private static WeakReference<View> rentView = null;
    private static WeakReference<View> alertView = null;
    private static final Object rentViewLock = new Object();
    private static final Object alertViewLock = new Object();

    private static final MediaPlayer mediaPlayer = new MediaPlayer();

    public static void enableBluetooth() {
        BluetoothAdapter.getDefaultAdapter().enable();
    }

    public static void disableBluetooth() {
        BluetoothAdapter.getDefaultAdapter().disable();
    }

    private static SharedPreferences getPrefs(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static String getRentUserName(Context context) {
        return getPrefs(context).getString(PREF_RENT_USER_NAME, null);
    }

    public static void setRentUserName(Context context, String userName) {
        getPrefs(context)
                .edit()
                .putString(PREF_RENT_USER_NAME, userName)
                .apply();
    }

    public static String getDeviceId(Context context) {
        return getPrefs(context).getString(PREF_DEVICE_ID, null);
    }

    public static void generateDeviceId(Context context) {
        String deviceId = getDeviceId(context);
        if (deviceId != null) {
            return;
        }

        deviceId = "Device-" + (new Random().nextInt(200) + 100);
        getPrefs(context)
                .edit()
                .putString(PREF_DEVICE_ID, deviceId)
                .apply();
    }

    public static void showKeyboard(View view) {
        view.requestFocus();
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    public static void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void reportTheft(Context context) {
        try {
            ApiClient.getService().reportTheft(getDeviceId(context), getRentUserName(context)).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void playAlarm(Context context) {
        Log.d("Util", "playAlarm");

        setSoundVolumeMax(context);

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

    public static void setSoundVolumeMax(Context context) {
        AudioManager audioManager = (AudioManager) context.getSystemService(AUDIO_SERVICE);
        int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, maxVolume, 0);

    }

    public static boolean isAlertShowing() {
        return alertView != null && alertView.get() != null;
    }

    public static void showAlertView(Context context) {
        synchronized (alertViewLock) {
            if (isAlertShowing()) {
                return;
            }

            // TODO; 구현하기
        }
    }

    public static void hideAlertView(Context context) {
        synchronized (alertViewLock) {
            if (!isAlertShowing()) {
                return;
            }

            WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

            windowManager.removeView(alertView.get());
            alertView.clear();
            alertView = null;
        }
    }

    public static boolean nowRenting(Context context) {
        return getRentUserName(context) != null;
    }

    public static boolean isRentShowing() {
        return rentView != null && rentView.get() != null;
    }

    public static void showRentView(Context context) {
        synchronized (rentViewLock) {
            if (isRentShowing()) {
                return;
            }

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

            rentView = new WeakReference<>(inflater.inflate(R.layout.rent_view, null));

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

            TextView tvRent = rentView.get().findViewById(R.id.rent);

            tvRent.setOnClickListener(v -> {
                RentActivity.start(context);
            });

            rentView.get().setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

            windowManager.addView(rentView.get(), params);
        }
    }

    public static void hideRentView(Context context) {
        synchronized (rentViewLock) {
            if (!isRentShowing()) {
                return;
            }

            WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

            windowManager.removeView(rentView.get());
            rentView.clear();
            rentView = null;
        }
    }
}
