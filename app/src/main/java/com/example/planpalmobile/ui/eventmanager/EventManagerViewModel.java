package com.example.planpalmobile.ui.eventmanager;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.planpalmobile.data.entities.Evento;
import com.example.planpalmobile.data.repository.EventosRepository;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.List;

public class EventManagerViewModel extends ViewModel {

    private final MutableLiveData<String> respNewEvent = new MutableLiveData<>();
    private final MutableLiveData<List<Evento>> eventosUsuario = new MutableLiveData<>();

    private final EventosRepository repository = new EventosRepository();

    public LiveData<String> respNewEventIsOk() {
        return respNewEvent;
    }

    public LiveData<List<Evento>> getEventosUsuario() {
        return eventosUsuario;
    }

    public void cargarEventosDelUsuario(String userId) {
        Log.d("EventManagerViewModel", "Cargando eventos para userId: " + userId);
        repository.getEventosUsuario(userId, new EventosRepository.EventosCallback() {
            @Override
            public void onSuccess(List<Evento> eventos) {
                Log.d("EventManagerViewModel", "Eventos recibidos: " + (eventos != null ? eventos.size() : "null"));
                eventosUsuario.setValue(eventos);
            }

            @Override
            public void onError(String error) {
                Log.e("EventManagerViewModel", "Error al cargar eventos: " + error);
                eventosUsuario.setValue(null);
            }
        });
    }

    public void eliminarEventoDeFirestore(String codigoEvento) {
        FirebaseFirestore.getInstance()
                .collection("eventos")
                .whereEqualTo("codigo", codigoEvento)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        document.getReference().delete();
                    }
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Error eliminando evento", e));
    }



    // Validación y creación de nuevo evento
    public void validateNewEvent(String title, Date dateIn, Date dateEnd, String dscript, List<Date> dateList) {

        if (title == null || title.trim().isEmpty()) {
            respNewEvent.setValue("ERROR_TITLE");
            return;
        }

        if (!dateIn.before(dateEnd)) {
            respNewEvent.setValue("ERROR_DATE");
            return;
        }

        repository.createNewEvent(title, dateIn, dateEnd, dscript, dateList, resp -> {
            respNewEvent.setValue(resp);
        });
    }

    public boolean validateNewMeet(Date newDate, List<Date> evetDate, Date evenDate, Date eventEndEnd) {
        if (newDate.before(evenDate) || newDate.after(eventEndEnd)) return false;
        for (Date date : evetDate) {
            if (date.equals(newDate)) return false;
        }
        return true;
    }
}
