package com.khcpietro.smc.grandtheftalarm.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.khcpietro.smc.grandtheftalarm.R;
import com.khcpietro.smc.grandtheftalarm.Util;

public class RentActivity extends AppCompatActivity {

    private EditText etUserName;

    public static void start(Context context) {
        Intent intent = new Intent(context, RentActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rent);

        TextView tvDeviceId = findViewById(R.id.device_id);
        etUserName = findViewById(R.id.user_name);

        tvDeviceId.setText(Util.getDeviceId(this));
    }

    @Override
    protected void onStart() {
        super.onStart();

        Util.hideRentView(this);
        Util.showKeyboard(etUserName);
    }

    @Override
    protected void onStop() {
        if (!Util.nowRenting(this)) {
            Util.showRentView(this);
        }
        Util.hideKeyboard(etUserName);

        super.onStop();
    }

    @Override
    public void onBackPressed() {
        // do nothing
    }

    public void onRentClick(View view) {
        String userName = etUserName.getText().toString();
        if (userName.isEmpty()) {
            Toast.makeText(this, "이름을 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        Util.setRentUserName(this, userName);
        Util.hideRentView(this);
        Toast.makeText(this, "대여 되었습니다.\n허용된 곳에서만 사용해주세요.", Toast.LENGTH_SHORT).show();
        finish();
    }
}