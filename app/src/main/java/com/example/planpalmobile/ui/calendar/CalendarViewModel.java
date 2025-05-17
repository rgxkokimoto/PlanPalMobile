package com.example.planpalmobile.ui.calendar;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.applandeo.materialcalendarview.CalendarDay;
import com.example.planpalmobile.R;
import com.example.planpalmobile.data.dto.EventoDTOItem;
import com.example.planpalmobile.data.repository.EventosRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CalendarViewModel extends ViewModel {
    // Salida a produción sprint2
    private final MutableLiveData<List<CalendarDay>> diasCalendario = new MutableLiveData<>();
    private final MutableLiveData<List<EventoDTOItem>> eventosList = new MutableLiveData<>();
    private final EventosRepository repository = new EventosRepository();
    private final MutableLiveData<LocalDate> fechaSeleccionada = new MutableLiveData<>();


    public CalendarViewModel () {

        // Pruebas con el calendario
        List<CalendarDay> listaEventos = new ArrayList<>();

        Calendar fecha = Calendar.getInstance();
        fecha.set(2025,Calendar.APRIL, 5);
        CalendarDay dia3 = new CalendarDay(fecha);
        dia3.setImageResource(R.drawable.baseline_event_24);
        dia3.setLabelColor(R.color.bar_color);

        listaEventos.add(dia3);
        diasCalendario.setValue(listaEventos);

    }

    /**
     * @DEPRECADO
     * Ahora usamos setFechaSeleccionada para la UI de CalendarFragment
     * Voy a mover este método al EventManager
     */

    /*
    public void loadEventos() {
        repository.getEventItems(eventosList::postValue);
    }
    */


     /*
     *  BACK --> Devuelve lista de eventos por fecha
     */
    public void loadEventosPorFecha(Calendar fecha) {
        repository.getEventsItemsByDate(fecha, eventosList::postValue);
    }

    public LiveData<List<CalendarDay>> getDiasCalendario() {
        return diasCalendario;
    }

    public LiveData<List<EventoDTOItem>> getEventosList() {return  eventosList;}




}
