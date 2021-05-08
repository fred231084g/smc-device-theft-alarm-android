package com.khcpietro.smc.restapi;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RestClient {

    private static RestService service;

    public static RestService getService() {
        if (service == null) {
            service = new Retrofit.Builder()
                    .baseUrl("https://covid-api.mmediagroup.fr")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(RestService.class);
        }

        return service;
    }
}
