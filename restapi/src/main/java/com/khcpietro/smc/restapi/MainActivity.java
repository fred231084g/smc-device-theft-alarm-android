package com.khcpietro.smc.restapi;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.concurrent.Executors;

import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private TextView tvCovidResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvCovidResult = findViewById(R.id.covid_result);

        loadCovidData();
    }

    private void loadCovidData() {
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                String country = "Korea, South";
                Response<JsonObject> response = RestClient.getService().getCases(country).execute();

                runOnUiThread(() -> {
                    int confirmed = response.body().getAsJsonObject("All").get("confirmed").getAsInt();
                    int recovered = response.body().getAsJsonObject("All").get("recovered").getAsInt();
                    int deaths = response.body().getAsJsonObject("All").get("deaths").getAsInt();

                    String result = country
                            + "\n확진자: " + confirmed
                            + "\n격리해제: " + recovered
                            + "\n사망자: " + deaths;

                    tvCovidResult.setText(result);
                });
            } catch (IOException e) {
                Toast.makeText(this, "데이터를 불러올 수 없습니다", Toast.LENGTH_SHORT).show();
            }
        });
    }
}