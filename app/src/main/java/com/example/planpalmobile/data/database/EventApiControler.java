package com.example.planpalmobile.data.database;


import com.example.planpalmobile.data.entities.Evento;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface EventApiControler {

    String API_URL = "http://192.168.1.148:8080/";

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(API_URL)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    @POST("eventos")
    Call<String> createEvent(@Body Evento evento);
}
