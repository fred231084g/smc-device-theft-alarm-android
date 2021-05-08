package com.khcpietro.smc.helloworld;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onGoToHomeClick(View view) {
        Toast.makeText(this, "집에 갑니다 😆", Toast.LENGTH_SHORT).show();
    }

    public void onGoToSchoolClick(View view) {
        Toast.makeText(this, "학교에 왔습니다...", Toast.LENGTH_SHORT).show();
    }
}