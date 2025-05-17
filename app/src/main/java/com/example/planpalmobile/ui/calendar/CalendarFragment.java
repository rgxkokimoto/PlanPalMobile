package com.example.planpalmobile.ui.calendar;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.planpalmobile.databinding.FragmentCalendarBinding;

import java.util.ArrayList;
import java.util.Calendar;

public class CalendarFragment extends Fragment {

    private FragmentCalendarBinding binding;
    private ItemEventRecyclerAdapter adapter;
    private CalendarViewModel calendarViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentCalendarBinding.inflate(inflater, container, false);
        calendarViewModel = new ViewModelProvider(this).get(CalendarViewModel.class);

        observeCalendarDays();
        setupDayClickListener();

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        setupRecyclerView();
        updateEventsInAdapter();
        //calendarViewModel.loadEventos(); Deprecado
    }

    /*
        Este método se encarga de buscar los eventos y cargar en el calendario
        los iconos correspondientes dando feedback visual al usuario.
     */
    private void observeCalendarDays() {
        calendarViewModel.getDiasCalendario().observe(getViewLifecycleOwner(), eventos -> {
            binding.calendarView.setCalendarDays(eventos);
        });
    }

    /*
     *  Este método se encarga de escuchar los clicks del usuario
     *  en el calendario y actualizar la UI con la fecha seleccionada
     *  ademas esta relacionado con los métodos de filtrado por dia
     *  que cargan la lista de eventos en el recycler view.
     *
     */
    private void setupDayClickListener() {
        binding.calendarView.setOnDayClickListener(eventDay -> {
            Calendar fechaSeleccionada = eventDay.getCalendar();

            int day = fechaSeleccionada.get(Calendar.DAY_OF_MONTH);
            binding.tvDiaSelect.setText("Día " + day);

            calendarViewModel.loadEventosPorFecha(fechaSeleccionada);
        });
    }

    /*
     *  Este método se encarga de actualizar la lista de eventos
     *  en el recycler view cuando se actualiza la lista de eventos
     *  debido a la interación del usuario con el calendario.
     */
    private void updateEventsInAdapter() {
        calendarViewModel.getEventosList().observe(getViewLifecycleOwner(), eventos -> {
            adapter.updateList(eventos);
        });
    }

    private void setupRecyclerView() {
        adapter = new ItemEventRecyclerAdapter(new ArrayList<>());
        binding.rvListEventItems.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvListEventItems.setAdapter(adapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
