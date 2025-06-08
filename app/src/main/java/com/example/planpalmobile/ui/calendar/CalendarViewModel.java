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
    private final MutableLiveData<List<EventoDTOItem>> eventosList = new MutableLiveData<>();
    private final MutableLiveData<List<EventoDTOItem>> searchedEventosList = new MutableLiveData<>();
    private final EventosRepository repository = new EventosRepository();
    private final MutableLiveData<List<Calendar>> datesIcons = new MutableLiveData<>();




    public CalendarViewModel () {
        setIconsDateInCalendarGUI();
    }



    /**
     * Recogemos todos los eventos y cargamos su fecha de inicio en el calendario
     *
     */
    public void setIconsDateInCalendarGUI() {
        repository.getEventItems(eventos -> {
            List<Calendar> fechas = new ArrayList<>();
            for (EventoDTOItem e : eventos) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(e.getHoraInicio());
                fechas.add(cal);
            }
            datesIcons.postValue(fechas);
        });
    }

     /*
     *  BACK --> Devuelve lista de eventos por fecha Para el Recycler View
     */
    public void loadEventosPorFecha(Calendar fecha) {
        repository.getEventsItemsByDate(fecha, eventosList::postValue);
    }

    public LiveData<List<EventoDTOItem>> getSearchedEventosList() {
        return searchedEventosList;
    }

    public void searchEventosByCodigo(String searchText) {
        if (searchText == null || searchText.trim().isEmpty()) {
            searchedEventosList.postValue(new ArrayList<>());
            return;
        }
        repository.searchEventsByCodePrefix(searchText, eventos -> {
            searchedEventosList.postValue(eventos);
        });
    }

    public LiveData<List<EventoDTOItem>> getEventosList() {return  eventosList;}

    public LiveData<List<Calendar>> getFechasEventos() {
        return datesIcons;
    }


}
