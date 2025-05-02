package com.example.planpalmobile.ui.calendar;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.applandeo.materialcalendarview.CalendarDay;
import com.example.planpalmobile.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CalendarViewModel extends ViewModel {

    private final MutableLiveData<List<CalendarDay>> diasCalendario = new MutableLiveData<>();
    private final MutableLiveData<CalendarDay> diaSelecionado = new MutableLiveData<>();
    private MutableLiveData<String> tvdiaSelected = new MutableLiveData<>();



    public CalendarViewModel () {
        // Pruebas con el calendario
        List<CalendarDay> listaEventos = new ArrayList<>();

        Calendar fecha = Calendar.getInstance();
        fecha.set(2025,Calendar.MAY, 3);
        CalendarDay dia3 = new CalendarDay(fecha);
        dia3.setImageResource(R.drawable.baseline_event_24);
        dia3.setLabelColor(R.color.bar_color);

        listaEventos.add(dia3);
        diasCalendario.setValue(listaEventos);

    }

    public LiveData<List<CalendarDay>> getDiasCalendario() {
        return diasCalendario;
    }

    public LiveData<CalendarDay> getDiaSelecionado() {
        return diaSelecionado;
    }

    public void setDiaSelecionado(CalendarDay calendarDay) {
        diaSelecionado.setValue(calendarDay);
    }

    public  LiveData<String> getTvDiaSelecionado() {
        return tvdiaSelected;
    }


}
