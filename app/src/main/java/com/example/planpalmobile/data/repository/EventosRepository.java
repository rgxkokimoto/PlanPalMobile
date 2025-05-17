package com.example.planpalmobile.data.repository;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.util.Log;

import com.example.planpalmobile.data.database.EventApiControler;
import com.example.planpalmobile.data.database.FirebaseDataSource;
import com.example.planpalmobile.data.dto.EventoDTOItem;
import com.example.planpalmobile.data.entities.Evento;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Consumer;
import com.google.firebase.Timestamp;



/*
    El Repositorio actúa como el intermediario entre Firestore y las capas superiores
    esto sirve para desacoplar del tooodo la visa de la lógica y mantener limpios y flexibles
    los view models
 */
public class EventosRepository {
    private final FirebaseDataSource dataSource;

    public EventosRepository() {
        this.dataSource = new FirebaseDataSource();

    }

    /**
     * Obtiene los eventos resumidos creados por un usuario específico.
     * @param callback Callback que recibe la lista de DTOs.
     */
    public void getEventItems(Consumer<List<EventoDTOItem>> callback) {
        dataSource.getEventosItem(listaMapas -> {
            List<EventoDTOItem> lista = new ArrayList<>();

            for (Map<String, Object> map : listaMapas) {
                String codigo = (String) map.get("codigo");

                Timestamp timestamp = (Timestamp) map.get("horaInicio");
                Date horaInicio = timestamp.toDate();

                lista.add(new EventoDTOItem(codigo, horaInicio));
            }
            callback.accept(lista);
        });
    }


    /*
     * Devuelve la lista de eventos por un dia, selecionada por el usuario
     */
    public void getEventsItemsByDate(Calendar fecha, Consumer<List<EventoDTOItem>> callback) {
        dataSource.getEventosItemByDateSelected(fecha, listaMapas -> {
            List<EventoDTOItem> listEventDays = new ArrayList<>();

            for (Map<String, Object> map : listaMapas) {
                String codigo = (String) map.get("codigo");

                Timestamp timestamp = (Timestamp) map.get("horaInicio");
                Date horaInicio = timestamp.toDate();

                listEventDays.add(new EventoDTOItem(codigo, horaInicio));
            }

            callback.accept(listEventDays);
        });
    }



    /**
     * Crea un nuevo evento en la base de datos.
     * Devolvera un callback con el resultado de la operación.
     *  User --> Petición Crear Evento --> Api
     *  User <-- Respuesta Crear Evento <-- Api
     */
    public void createNewEvent(String codigo, Date dateIn, Date dateEnd, String dscript, List<Date> horasDisponibles, Consumer<String> callback) {

        FirebaseUser userLoged = FirebaseAuth.getInstance().getCurrentUser();
        if (userLoged == null) {
            callback.accept("ERROR_USER");
            return;
        }


        String creadorId = userLoged.getEmail();
        if (creadorId != null && creadorId.contains("@gmail.com")) {
            creadorId = creadorId.replace("@gmail.com", "");
        }

        Map<Date, String> citasReservadas = new HashMap<>();

        Evento nuevoEvento = new Evento(
                codigo,
                dscript,
                dateIn,
                dateEnd,
                horasDisponibles,
                citasReservadas,
                creadorId
        );



        EventApiControler apiControler = EventApiControler.retrofit.create(EventApiControler.class);

        apiControler.createEvent(nuevoEvento).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    Log.d("EventosRepository", "Evento creado correctamente: " + response.body());
                    callback.accept("OK");
                } else {
                    Log.e("EventosRepository", "Error en la creación: " + response.code());
                    callback.accept("ERROR_RESPONSE");
                }
            }


            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e("EventosRepository", "Fallo de red o servidor: " + t.getMessage());
                callback.accept("ERROR_NETWORK");
            }
        });

    }

    /**
     * @param dateString
     * @return string ==> Date
     */
    public static Date mapDateFromString(String dateString) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy HH:mm:ss", Locale.getDefault());
            return sdf.parse(dateString);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }


}
