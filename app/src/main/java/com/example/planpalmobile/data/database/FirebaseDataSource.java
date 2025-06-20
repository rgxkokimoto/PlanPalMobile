package com.example.planpalmobile.data.database;

import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

// FirebaseDataSource.java
public class FirebaseDataSource {
    private final FirebaseFirestore db;

    public FirebaseDataSource() {
        this.db = FirebaseFirestore.getInstance();
    }

    public Task<QuerySnapshot> getEventos() {
        return db.collection("eventos").get();
    }

    /**
     * Retorna los elementos necesarios mostrarlos en el item de la RecyclerView principal
     * del calendario.
     * Estaba deprecado pero ahora se usa para cargar los iconos de los eventos
     */
    public Task<QuerySnapshot> getEventosItem(Consumer<List<Map<String, Object>>> callback) {
        return db.collection("eventos")
                .get()
                .addOnSuccessListener(snapshot -> {
                    List<Map<String, Object>> resultado = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : snapshot) {
                        Map<String, Object> data = new HashMap<>();
                        data.put("id", doc.getId());
                        data.put("codigo", doc.getString("codigo"));
                        data.put("horaInicio", doc.getTimestamp("horaInicio"));
                        data.put("creadorId", doc.getString("creadorId"));
                        data.put("etiqueta", doc.getString("etiqueta"));
                        resultado.add(data);
                    }
                    callback.accept(resultado);
                });
    }


    public void buscarEventosPorPrefijoCodigo(String prefijo, Consumer<List<Map<String, Object>>> callback) {
        if (prefijo == null || prefijo.isEmpty()) {
            callback.accept(new ArrayList<>());
            return;
        }

        db.collection("eventos")
                .whereGreaterThanOrEqualTo("codigo", prefijo)
                .whereLessThanOrEqualTo("codigo", prefijo + "\uf8ff")
                .get()
                .addOnSuccessListener(snapshot -> {
                    List<Map<String, Object>> resultado = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : snapshot) {
                        Map<String, Object> data = new HashMap<>();
                        data.put("id", doc.getId());
                        data.put("codigo", doc.getString("codigo"));
                        data.put("horaInicio", doc.getTimestamp("horaInicio"));
                        data.put("creadorId", doc.getString("creadorId"));
                        data.put("etiqueta", doc.getString("etiqueta"));
                        resultado.add(data);
                    }
                    callback.accept(resultado);
                })
                .addOnFailureListener(e -> {
                    Log.e("FirebaseDataSource", "Error buscando eventos por prefijo: " + e.getMessage(), e);
                    callback.accept(new ArrayList<>());
                });
    }




    public void eliminarEventoPorCodigo(String codigoEvento, Consumer<Boolean> callback) {
        FirebaseFirestore.getInstance().collection("eventos")
                .whereEqualTo("codigo", codigoEvento)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        doc.getReference().delete();
                    }
                    callback.accept(true);
                })
                .addOnFailureListener(e -> {
                    Log.e("Firebase", "Error eliminando evento: " + e.getMessage());
                    callback.accept(false);
                });
    }


    /**
     * Va a recoger los eventos todos los eventos de un dia en concreto.
     * @param fecha
     * @param callback
     *
     * Para usamos el parametro fecha para crear un avanico
     * y usamos métodos nativos de firebase para hacer el filtrado
     * esto ahorrara muchas llamadas inecesarias cada vez que se quiera
     * ver los eventos de un dia. 
     *
     */
    public void getEventosItemByDateSelected(Calendar fecha, Consumer<List<Map<String, Object>>> callback) {
        Calendar start = (Calendar) fecha.clone();
        start.set(Calendar.HOUR_OF_DAY, 0);
        start.set(Calendar.MINUTE, 0);
        start.set(Calendar.SECOND, 0);
        start.set(Calendar.MILLISECOND, 0);

        Calendar end = (Calendar) fecha.clone();
        end.set(Calendar.HOUR_OF_DAY, 23);
        end.set(Calendar.MINUTE, 59);
        end.set(Calendar.SECOND, 59);
        end.set(Calendar.MILLISECOND, 999);

        Timestamp startTimestamp = new Timestamp(start.getTime());
        Timestamp endTimestamp = new Timestamp(end.getTime());

        db.collection("eventos")
                .whereGreaterThanOrEqualTo("horaInicio", startTimestamp)
                .whereLessThanOrEqualTo("horaInicio", endTimestamp)
                .get()
                .addOnSuccessListener(snapshot -> {
                    List<Map<String, Object>> resultado = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : snapshot) {
                        Map<String, Object> data = new HashMap<>();
                        data.put("id", doc.getId());
                        data.put("codigo", doc.getString("codigo"));
                        data.put("etiqueta", doc.getString("etiqueta"));
                        data.put("horaInicio", doc.getTimestamp("horaInicio"));
                        resultado.add(data);
                    }
                    callback.accept(resultado);
                })
                .addOnFailureListener(e -> {
                    callback.accept(new ArrayList<>());
                });
    }

    public  Task<QuerySnapshot> getEventsByUser(String user, Consumer<List<Map<String, Object>>> callback) {
        return db.collection("eventos")
                .whereEqualTo("creadorId", user)
                .get()
                .addOnSuccessListener(snapshot -> {
                    List<Map<String, Object>> resultado = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : snapshot) {
                        Map<String, Object> data = new HashMap<>();
                        data.put("id", doc.getId());
                        Log.d("getEventsByUser", "id: " + doc.getId());
                        data.put("codigo", doc.getString("codigo"));
                        data.put("horaInicio", doc.getTimestamp("horaInicio"));
                        data.put("creadorId", doc.getString("creadorId"));
                        data.put("descripcion", doc.getString("descripcion"));
                        data.put("horaFin", doc.getTimestamp("horaFin"));
                        data.put("horasDisponibles", doc.get("horasDisponibles"));
                        data.put("citasReservadas", doc.get("citasReservadas"));
                        data.put("etiqueta", doc.getString("etiqueta"));
                        resultado.add(data);
                    }
                    callback.accept(resultado);
                })
                .addOnFailureListener(e -> {
                    callback.accept(new ArrayList<>());
                });
    }


    /**
     * DEPRECADO para este ya usamos la API
     * @param codigo
     * @param callback
     */
    public void getEventoByCodigo(String codigo, Consumer<Map<String, Object>> callback) {
        db.collection("eventos")
                .whereEqualTo("codigo", codigo)
                .limit(1)
                .get()
                .addOnSuccessListener(snapshot -> {
                    if (!snapshot.isEmpty()) {
                        QueryDocumentSnapshot doc = (QueryDocumentSnapshot) snapshot.getDocuments().get(0);
                        Map<String, Object> data = doc.getData();
                        callback.accept(data);
                    } else {
                        callback.accept(null);
                    }
                })
                .addOnFailureListener(e -> {
                    callback.accept(null);
                });
    }


    public void actualizarEvento(String idDocument, Map<String, Object> datosActualizados, Consumer<Boolean> callback) {
        db.collection("eventos")
                .document(idDocument)
                .update(datosActualizados)
                .addOnSuccessListener(aVoid -> {
                    Log.d("FirebaseDataSource", "Evento actualizado correctamente");
                    callback.accept(true);
                })
                .addOnFailureListener(e -> {
                    Log.e("FirebaseDataSource", "Error actualizando evento: " + e.getMessage());
                    callback.accept(false);
                });
    }

}

