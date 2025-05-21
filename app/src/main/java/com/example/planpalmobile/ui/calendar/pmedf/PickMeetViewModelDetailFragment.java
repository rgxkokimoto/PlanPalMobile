package com.example.planpalmobile.ui.calendar.pmedf;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.planpalmobile.data.entities.Evento;
import com.example.planpalmobile.data.repository.EventosRepository;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class PickMeetViewModelDetailFragment  extends ViewModel {

    // MÉTODOS DEL_PICK_MEET_EVENT_FRAGMENT

    private final EventosRepository repository = new EventosRepository();

    private final MutableLiveData<String> tituloMutable = new MutableLiveData<>();
    public LiveData<String> titulo = tituloMutable;

    private final MutableLiveData<String> fechaFormateadaMutable = new MutableLiveData<>();
    public LiveData<String> fechaFormateada = fechaFormateadaMutable;

    private final MutableLiveData<String> descripcionMutable = new MutableLiveData<>();
    public LiveData<String> descripcion = descripcionMutable;

    private final MutableLiveData<List<Map<Date, String>>> reservasMutable = new MutableLiveData<>();
    public LiveData<List<Map<Date, String>>> reservas = reservasMutable;

    private final MutableLiveData<Evento> eventoMutable = new MutableLiveData<>();
    public LiveData<Evento> evento = eventoMutable;

    public void cargarEvento(String codigoEvento) {
        repository.getEvento(codigoEvento, eventoRecibido -> {
            if (eventoRecibido != null) {
                eventoMutable.postValue(eventoRecibido);

                tituloMutable.postValue(eventoRecibido.getCodigo());  // Asumo que el título es código, corrige si es otro campo

                descripcionMutable.postValue(eventoRecibido.getDescripcion());

                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
                String fechaFormateada = "";
                if (eventoRecibido.getHoraInicio() != null) {
                    fechaFormateada = sdf.format(eventoRecibido.getHoraInicio());
                }
                fechaFormateadaMutable.postValue(fechaFormateada);

                Map<Date, String> citasMap = eventoRecibido.getCitasReservadas();
                List<Map<Date, String>> listaReservas = new ArrayList<>();
                if (citasMap != null) {
                    for (Map.Entry<Date, String> entry : citasMap.entrySet()) {
                        Map<Date, String> item = new HashMap<>();
                        item.put(entry.getKey(), entry.getValue());
                        listaReservas.add(item);
                    }
                }
                reservasMutable.postValue(listaReservas);

            } else {
                eventoMutable.postValue(null);
                tituloMutable.postValue("");
                descripcionMutable.postValue("");
                fechaFormateadaMutable.postValue("");
                reservasMutable.postValue(Collections.emptyList());
            }
        });
    }

    public LiveData<String> getTitulo() {
        return titulo;
    }

    public LiveData<String> getFechaFormateada() {
        return fechaFormateada;
    }

    public LiveData<String> getDescripcion() {
        return descripcion;
    }

    public LiveData<List<Map<Date, String>>> getReservas() {
        return reservas;
    }

    public LiveData<Evento> getEvento() {
        return evento;
    }

}
