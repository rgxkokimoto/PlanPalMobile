package com.example.planpalmobile.data.database;


import com.example.planpalmobile.data.entities.Evento;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface EventApiControler {

    String API_URL = "http://192.168.1.150:8080/";

    Gson gson = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
            .create();

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(API_URL)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build();

    @GET("eventos/{codigo}")
    Call<Evento> getEvent(@Path("codigo") String codigoEvento);

    @POST("eventos")
    Call<String> createEvent(@Body Evento evento);



    @PUT("eventos/{id}")
    Call<String> updateEvento(@Path("id") String codigoEvento, @Body Map<String, Object> camposActualizados);


}
