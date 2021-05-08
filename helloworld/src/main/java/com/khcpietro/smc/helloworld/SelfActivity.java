package com.khcpietro.smc.helloworld;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SelfActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_self);
    }

    public void onSaveClick(View view) {
        Toast.makeText(this, "저장했습니다", Toast.LENGTH_SHORT).show();
    }

    public void onLoadClick(View view) {
        Toast.makeText(this, "불러왔습니다", Toast.LENGTH_SHORT).show();
    }

    public void onCloseClick(View view) {
        Toast.makeText(this, "종료합니다", Toast.LENGTH_SHORT).show();
        finish();
    }
}
