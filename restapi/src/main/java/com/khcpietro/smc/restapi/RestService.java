package com.khcpietro.smc.restapi;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RestService {

    @GET("/v1/cases")
    Call<JsonObject> getCases(@Query("country") String country);

}
