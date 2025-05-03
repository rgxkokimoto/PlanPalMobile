package com.example.planpalmobile.data.repository;

import android.content.Context;

import com.example.planpalmobile.data.database.FirebaseDataSource;
import com.example.planpalmobile.data.dto.EventoDTOItem;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Consumer;

/*
    El Repositorio actúa como el intermediario entre Firestore y las capas superiores
    esto sirve para desacoplar del toodo la visa de la lógica y mantener limpios y flexibles
    los view models
 */
public class EventosRepository {
    private final FirebaseDataSource dataSource;

    public EventosRepository(Context context) {
        this.dataSource = new FirebaseDataSource();
    }

    /**
     * Obtiene los eventos resumidos creados por un usuario específico.
     * @param callback Callback que recibe la lista de DTOs.
     */
    public void getEventItems(String creadorId, Consumer<List<EventoDTOItem>> callback) {
        dataSource.getEventosItem(listaMapas -> {
            List<EventoDTOItem> lista = new ArrayList<>();

            for (Map<String, Object> map : listaMapas) {
                String codigo = (String) map.get("codigo");
                String descripcion = (String) map.get("descripcion");
                String horaInicioStr = (String) map.get("horaInicio");
                Date horaInicio = mapDateFromString(horaInicioStr);

                lista.add(new EventoDTOItem(codigo, descripcion, horaInicio));
            }
            callback.accept(lista);
        });
    }

    /**
     * @param dateString
     * @return string ==> Date
     */
    public static Date mapDateFromString(String dateString) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
            return sdf.parse(dateString);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

}
