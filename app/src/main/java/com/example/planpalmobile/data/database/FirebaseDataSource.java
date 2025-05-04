package com.example.planpalmobile.data.database;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
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
     * @param callback
     * @return
     * Retorna los elementos necesarios mostrarlos en el item de la RecyclerView principal
     * del calendario.
     */
    public Task<QuerySnapshot> getEventosItem(Consumer<List<Map<String, Object>>> callback) {
        return db.collection("eventos")
                .get()
                .addOnSuccessListener(snapshot -> {
                    List<Map<String, Object>> resultado = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : snapshot) {
                        Map<String, Object> data = new HashMap<>();
                        data.put("codigo", doc.getString("codigo"));
                        data.put("horaInicio", doc.getString("horaInicio"));
                        resultado.add(data);
                    }
                    callback.accept(resultado);
                });
    }

    public Task<DocumentReference> crearEvento(Map<String, Object> eventoMap) {
        return db.collection("eventos").add(eventoMap);
    }

    public Task<Void> actualizarEvento(String id, Map<String, Object> cambios) {
        return db.collection("eventos").document(id).update(cambios);
    }

    public Task<Void> eliminarEvento(String id) {
        return db.collection("eventos").document(id).delete();
    }

    public Task<DocumentSnapshot> getEventoPorId(String id) {
        return db.collection("eventos").document(id).get();
    }
}

