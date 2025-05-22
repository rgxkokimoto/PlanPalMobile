package com.example.planpalmobile.ui.calendar.pmedf;

import android.text.format.DateUtils;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.planpalmobile.data.entities.Evento;
import com.example.planpalmobile.data.repository.EventosRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.stream.Collectors;

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

    public void reservarCita(String id, Date citaSeleccionada) {
        Evento evento = eventoMutable.getValue();
        if (evento == null) return;

        FirebaseUser userLoged = FirebaseAuth.getInstance().getCurrentUser();
        if (userLoged == null) return;
        String nombreUsuario = userLoged.getEmail();
        if (nombreUsuario == null || nombreUsuario.isEmpty()) return;

        Map<Date, String> reservasActuales = evento.getCitasReservadas();
        if (reservasActuales == null) reservasActuales = new HashMap<>();
        reservasActuales.put(citaSeleccionada, nombreUsuario);

        List<Date> disponiblesActuales = evento.getHorasDisponibles();
        if (disponiblesActuales == null) disponiblesActuales = new ArrayList<>();
        disponiblesActuales.remove(citaSeleccionada);

        evento.setCitasReservadas(reservasActuales);
        evento.setHorasDisponibles(disponiblesActuales);
        eventoMutable.setValue(evento); // actualiza observers


        Map<String, Object> datosActualizados = new HashMap<>();

        SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
        isoFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        List<String> fechasDisponiblesStr = disponiblesActuales.stream()
                .map(date -> isoFormat.format(date))
                .collect(Collectors.toList());

        Map<String, String> citasStrMap = reservasActuales.entrySet().stream()
                .collect(Collectors.toMap(
                        entry -> isoFormat.format(entry.getKey()),
                        Map.Entry::getValue
                ));


        datosActualizados.put("horasDisponibles", fechasDisponiblesStr);
        datosActualizados.put("citasReservadas", citasStrMap);

        repository.actualizarEvento(id, datosActualizados, success -> {
            if (success) {
                Log.d("PickMeetViewModel", "Cita reservada correctamente");
            } else {
                Log.e("PickMeetViewModel", "Error al reservar cita");
            }
        });
    }



    private List<Map<Date, String>> convertirMapaEnLista(Map<Date, String> mapa) {
        List<Map<Date, String>> lista = new ArrayList<>();
        for (Map.Entry<Date, String> entry : mapa.entrySet()) {
            Map<Date, String> item = new HashMap<>();
            item.put(entry.getKey(), entry.getValue());
            lista.add(item);
        }
        return lista;
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
