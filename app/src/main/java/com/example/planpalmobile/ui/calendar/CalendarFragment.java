package com.example.planpalmobile.ui.calendar;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.exceptions.OutOfDateRangeException;
import com.example.planpalmobile.R;
import com.example.planpalmobile.databinding.FragmentCalendarBinding;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CalendarFragment extends Fragment {

    private FragmentCalendarBinding binding;
    private ItemEventRecyclerAdapter adapter;
    private CalendarViewModel calendarViewModel;
    private View emptyView;
    private ViewStub viewStubEmpty;

    @Override
    public void onResume() {
        super.onResume();
        calendarViewModel.setIconsDateInCalendarGUI();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentCalendarBinding.inflate(inflater, container, false);
        calendarViewModel = new ViewModelProvider(this).get(CalendarViewModel.class);

        viewStubEmpty = binding.getRoot().findViewById(R.id.viewStubEmpty);
        emptyView = null;

        observeCalendarDays();
        setupDayClickListener();

        binding.ibtnActualDay.setOnClickListener(v -> {
            Calendar today = Calendar.getInstance();

            today.set(Calendar.HOUR_OF_DAY, 0);
            today.set(Calendar.MINUTE, 0);
            today.set(Calendar.SECOND, 0);
            today.set(Calendar.MILLISECOND, 0);

            try {
                binding.calendarView.setDate(today);
            } catch (OutOfDateRangeException e) {
                e.printStackTrace();
            }
        });

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        setupRecyclerView();
        updateEventsInAdapter();
        //calendarViewModel.loadEventos(); Deprecado
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


    // METODOS DE LA UI

    /*
        Este método se encarga de buscar los eventos y cargar en el calendario
        los iconos correspondientes dando feedback visual al usuario.
     */
    private void observeCalendarDays() {
        calendarViewModel.getFechasEventos().observe(getViewLifecycleOwner(), fechas -> {
            List<EventDay> eventos = new ArrayList<>();

            for (Calendar fecha : fechas) {
                eventos.add(new EventDay(fecha, R.drawable.baseline_event_24));
            }

            binding.calendarView.setEvents(eventos);
        });
    }

    /*
     *  Este método se encarga de escuchar los clicks del usuario
     *  en el calendario y actualizar la UI con la fecha seleccionada
     *  en el dia actual
     *  ademas esta relacionado con los métodos de filtrado por dia
     *  que cargan la lista de eventos en el recycler view.
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

            if (eventos != null && !eventos.isEmpty()) {
                binding.rvListEventItems.setVisibility(View.VISIBLE);

                if (emptyView != null) {
                    emptyView.setVisibility(View.GONE);
                }
            } else {
                binding.rvListEventItems.setVisibility(View.GONE);

                if (emptyView == null) {
                    emptyView = viewStubEmpty.inflate();
                } else {
                    emptyView.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void setupRecyclerView() {
        adapter = new ItemEventRecyclerAdapter(new ArrayList<>());
        binding.rvListEventItems.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvListEventItems.setAdapter(adapter);
    }


}
