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
import java.util.TimeZone;
import java.util.function.Consumer;
import com.google.firebase.Timestamp;
import com.google.gson.Gson;


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
     * Obtiene los eventos resumidos para el item de la RecyclerView.
     * @param callback Callback que recibe la lista de DTOs.
     */
    public void getEventItems(Consumer<List<EventoDTOItem>> callback) {
        dataSource.getEventosItem(listaMapas -> {
            List<EventoDTOItem> lista = new ArrayList<>();

            for (Map<String, Object> map : listaMapas) {
                String id = (String) map.get("id");
                String codigo = (String) map.get("codigo");
                String etiqueta = (String) map.get("etiqueta");
                Timestamp timestamp = (Timestamp) map.get("horaInicio");
                Date horaInicio = timestamp.toDate();

                lista.add(new EventoDTOItem(codigo, horaInicio, id, etiqueta));
            }
            callback.accept(lista);
        });
    }

    /**
     * Busca eventos por prefijo de codigo
     * cada prefijo oh similitud que llega desde el usuario lo busca en la base de datos
     * @param prefix
     * @param callback
     */
    public void searchEventsByCodePrefix(String prefix, Consumer<List<EventoDTOItem>> callback) {
        dataSource.buscarEventosPorPrefijoCodigo(prefix, listaMapas -> {
            List<EventoDTOItem> listaDto = new ArrayList<>();
            if (listaMapas != null) {
                for (Map<String, Object> map : listaMapas) {
                    String id = (String) map.get("id");
                    String codigo = (String) map.get("codigo");
                    String etiqueta = (String) map.get("etiqueta");
                    Timestamp timestamp = (Timestamp) map.get("horaInicio");
                    Date horaInicio = null;
                    if (timestamp != null) {
                        horaInicio = timestamp.toDate();
                    }
                    listaDto.add(new EventoDTOItem(codigo, horaInicio, id, etiqueta));
                }
            }
            callback.accept(listaDto);
        });
    }


    /**
     * Obtiene un evento por su codigo para la vista de detalles. y el pick meet
     * @param codigoEvento
     * @param callback
     */
    public void getEvento(String codigoEvento, Consumer<Evento> callback) {
        EventApiControler api = EventApiControler.retrofit.create(EventApiControler.class);
        Call<Evento> call = api.getEvent(codigoEvento);

        call.enqueue(new Callback<Evento>() {
            @Override
            public void onResponse(Call<Evento> call, Response<Evento> response) {
                if (response.isSuccessful() && response.body() != null) {

                    Evento evento = response.body();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
                    try {

                        Evento eventoMapeado = new Evento();
                        eventoMapeado.setCodigo(evento.getCodigo());
                        eventoMapeado.setEtiqueta(evento.getEtiqueta());
                        eventoMapeado.setDescripcion(evento.getDescripcion());
                        eventoMapeado.setCreadorId(evento.getCreadorId());
                        eventoMapeado.setHoraInicio(evento.getHoraInicio());
                        eventoMapeado.setHoraFin(evento.getHoraFin());

                        List<Date> horas = new ArrayList<>();
                        for (Object item : evento.getHorasDisponibles()) {
                            if (item instanceof String) {
                                horas.add(sdf.parse((String) item));
                            } else if (item instanceof Date){
                                horas.add((Date) item);
                            }
                        }
                        eventoMapeado.setHorasDisponibles(horas);

                        Map<Date, String> citas = new HashMap<>();
                        for (Map.Entry<?, String> entry : evento.getCitasReservadas().entrySet()) {
                            if (entry.getKey() instanceof String) {
                                citas.put(sdf.parse((String) entry.getKey()), entry.getValue());
                            } else if (entry.getKey() instanceof Date) {
                                citas.put((Date) entry.getKey(), entry.getValue());
                            }
                        }
                        eventoMapeado.setCitasReservadas(citas);

                        callback.accept(eventoMapeado);
                    } catch (Exception e) {
                        Log.e("EventosRepository", "Error al mapear el evento en repository " + e.getMessage());
                        callback.accept(null);
                    }

                } else {
                    Log.e("EventosRepository", "Error en la respuesta: " + response.code());
                    callback.accept(null);
                }
            }

            @Override
            public void onFailure(Call<Evento> call, Throwable t) {
                Log.e("EventosRepository", "Fallo de red o servidor: " + t.getMessage());
                callback.accept(null);
            }
        });
    }



    /*
     * Devuelve la lista de DTO de eventos por un dia, selecionada por el usuario
     */
    public void getEventsItemsByDate(Calendar fecha, Consumer<List<EventoDTOItem>> callback) {
        dataSource.getEventosItemByDateSelected(fecha, listaMapas -> {
            List<EventoDTOItem> listEventDays = new ArrayList<>();

            for (Map<String, Object> map : listaMapas) {
                String id = (String) map.get("id");
                String codigo = (String) map.get("codigo");
                String etiqueta = (String) map.get("etiqueta");
                Timestamp timestamp = (Timestamp) map.get("horaInicio");
                Date horaInicio = timestamp.toDate();

                listEventDays.add(new EventoDTOItem(codigo, horaInicio, id, etiqueta));
            }

            callback.accept(listEventDays);
        });
    }

    public void getEventosUsuario(String userId, EventosCallback callback) {
        dataSource.getEventsByUser(userId, listaMapas -> {
            List<Evento> eventosDelUsuario = new ArrayList<>();

            for (Map<String, Object> map : listaMapas) {
                try {
                    String id = (String) map.get("id");
                    String codigo = (String) map.get("codigo");
                    String descripcion = (String) map.get("descripcion");
                    String etiqueta = (String) map.get("etiqueta");
                    Timestamp tsInicio = (Timestamp) map.get("horaInicio");
                    Timestamp tsFin = (Timestamp) map.get("horaFin");

                    Date horaInicio = tsInicio != null ? tsInicio.toDate() : null;
                    Date horaFin = tsFin != null ? tsFin.toDate() : null;

                    List<String> listaStrings = (List<String>) map.get("horasDisponibles");
                    //Log.d("FHD", "listaStrings : " + listaStrings);
                    List<Date> fechasDisponibles = new ArrayList<>();

                    if (listaStrings != null) {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
                        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

                        for (String fechaStr : listaStrings) {
                            try {
                                Date fecha = sdf.parse(fechaStr);
                                if (fecha != null) {
                                    fechasDisponibles.add(fecha);
                                }
                            } catch (ParseException e) {
                                Log.e("ParseHoras", "Error al parsear hora disponible: " + fechaStr);
                            }
                        }
                    }


                    Map<String, String> citasFirestore = (Map<String, String>) map.get("citasReservadas");
                    Map<Date, String> citasReservadas = new HashMap<>();

                    if (citasFirestore != null) {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
                        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

                        for (Map.Entry<String, String> entry : citasFirestore.entrySet()) {
                            try {
                                Date fecha = sdf.parse(entry.getKey());
                                if (fecha != null) {
                                    citasReservadas.put(fecha, entry.getValue());
                                }
                            } catch (ParseException e) {
                                Log.e("ParseCita", "Fecha inválida: " + entry.getKey());
                            }
                        }
                    }

                    String creadorId = (String) map.get("creadorId");

                    Evento evento = new Evento(
                            id,
                            codigo,
                            descripcion,
                            horaInicio,
                            horaFin,
                            fechasDisponibles,
                            citasReservadas,
                            creadorId,
                            etiqueta
                    );

                    eventosDelUsuario.add(evento);
                } catch (Exception e) {
                    Log.e("getEventosUsuario", "Error al parsear evento: " + e.getMessage());
                }
            }

            callback.onSuccess(eventosDelUsuario);
        });
    }

    public void eliminarEvento(String codigoEvento, Consumer<Boolean> callback) {
        dataSource.eliminarEventoPorCodigo(codigoEvento, callback);
    }



    /**
     * Crea un nuevo evento en la base de datos.
     * Devolvera un callback con el resultado de la operación.
     *  User --> Petición Crear Evento --> Api
     *  User <-- Respuesta Crear Evento <-- Api
     */
    public void createNewEvent(String codigo, Date dateIn, Date dateEnd, String dscript, List<Date> horasDisponibles, String etiqueta ,Consumer<String> callback) {

        FirebaseUser userLoged = FirebaseAuth.getInstance().getCurrentUser();
        if (userLoged == null) {
            callback.accept("ERROR_USER");
            return;
        }


        String creadorId = userLoged.getEmail();
        if (creadorId != null && creadorId.contains("@gmail.com")) {
            creadorId = creadorId.replace("@gmail.com", "");
        }

        Log.d("EventosRepository", "creadorId usado para evento: " + creadorId);

        Map<Date, String> citasReservadas = new HashMap<>();

        Evento nuevoEvento = new Evento(
                codigo,
                dscript,
                dateIn,
                dateEnd,
                horasDisponibles,
                citasReservadas,
                creadorId,
                etiqueta
        );

        Gson gson = new Gson();
        Log.d("EventosRepository", "Evento JSON: " + gson.toJson(nuevoEvento));

        // Llamada retrofit
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
     * Actualiza un evento en la base de datos desde La fuente de datos
     * @param codigoEvento
     * @param datosActualizados
     * @param callback
     */
    public void actualizarEvento(String codigoEvento, Map<String, Object> datosActualizados, Consumer<Boolean> callback) {
        dataSource.actualizarEvento(codigoEvento, datosActualizados, success -> {
            if (success) {
                Log.d("EventosRepository", "Evento actualizado correctamente");
                callback.accept(true);
            } else {
                Log.e("EventosRepository", "Error al actualizar el evento");
                callback.accept(false);
            }
        });
    }


    public interface EventosCallback {
        void onSuccess(List<Evento> eventos);
        void onError(String error);
    }



}
