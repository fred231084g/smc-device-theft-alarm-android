package com.khcpietro.smc.grandtheftalarm.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.khcpietro.smc.grandtheftalarm.R;
import com.khcpietro.smc.grandtheftalarm.Util;

public class MainActivity extends AppCompatActivity {

    private TextView tvDeviceId;
    private TextView tvUserName;

    public static void start(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvDeviceId = findViewById(R.id.device_id);
        tvUserName = findViewById(R.id.user_name);

        if (!Util.nowRenting(this)) {
            Util.showRentView(this);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        tvDeviceId.setText(Util.getDeviceId(this));
        tvUserName.setText(Util.getRentUserName(this));
    }

    public void onReturnClick(View view) {
        Util.showRentView(this);
        Util.setRentUserName(this, null);
        Toast.makeText(this, "반납되었습니다.", Toast.LENGTH_SHORT).show();
        finish();
    }
}
