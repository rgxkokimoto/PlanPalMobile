package com.example.planpalmobile.ui.calendar;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.os.Handler;
import android.os.Looper;
import androidx.appcompat.widget.SearchView;


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
    private androidx.appcompat.widget.SearchView searchViewEvents;
    private android.os.Handler searchHandler = new android.os.Handler(android.os.Looper.getMainLooper());
    private Runnable searchRunnable;
    private static final long DEBOUNCE_DELAY_MS = 500;
    private boolean isSearchActive = false;

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

        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);
        String todayS = String.valueOf(today.get(Calendar.DAY_OF_MONTH));
        binding.tvDiaSelect.setText("Día " + todayS);


        observeCalendarDays();
        setupDayClickListener();

        binding.ibtnActualDay.setOnClickListener(v -> {


            try {
                binding.calendarView.setDate(today);
                binding.tvDiaSelect.setText("Día " + todayS);
            } catch (OutOfDateRangeException e) {
                e.printStackTrace();
            }
        });

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        setupRecyclerView();
        searchViewEvents = binding.searchViewEvents;
        setupSearchListener();
        observeSearchResults();
        updateEventsInAdapter();
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

    private void setupDayClickListener() {
        binding.calendarView.setOnDayClickListener(eventDay -> {
            Calendar fechaSeleccionada = eventDay.getCalendar();
            int day = fechaSeleccionada.get(Calendar.DAY_OF_MONTH);
            binding.tvDiaSelect.setText("Día " + day);

            if (searchViewEvents != null) {
                searchViewEvents.setQuery("", false);
            }
            searchHandler.removeCallbacks(searchRunnable);
            isSearchActive = false;

            calendarViewModel.searchEventosByCodigo("");

            calendarViewModel.loadEventosPorFecha(fechaSeleccionada);
        });
    }

    private void setupSearchListener() {
        searchViewEvents.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchHandler.removeCallbacks(searchRunnable);
                performSearch(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchHandler.removeCallbacks(searchRunnable);
                searchRunnable = () -> performSearch(newText);
                searchHandler.postDelayed(searchRunnable, DEBOUNCE_DELAY_MS);
                return true;
            }
        });

        searchViewEvents.setOnCloseListener(() -> {
            searchHandler.removeCallbacks(searchRunnable);
            return false;
        });
    }

    private void performSearch(String query) {
        String trimmedQuery = query != null ? query.trim() : "";
        isSearchActive = !trimmedQuery.isEmpty();

        if (isSearchActive) {
            calendarViewModel.searchEventosByCodigo(trimmedQuery);
        } else {
            calendarViewModel.searchEventosByCodigo("");

            Calendar selectedDate = binding.calendarView.getFirstSelectedDate();
            if (selectedDate != null) {
                calendarViewModel.loadEventosPorFecha(selectedDate);
            } else {
                Calendar today = Calendar.getInstance();
                today.set(Calendar.HOUR_OF_DAY, 0);
                today.set(Calendar.MINUTE, 0);
                today.set(Calendar.SECOND, 0);
                today.set(Calendar.MILLISECOND, 0);
                calendarViewModel.loadEventosPorFecha(today);
            }
        }
    }

    private void observeSearchResults() {
        calendarViewModel.getSearchedEventosList().observe(getViewLifecycleOwner(), eventos -> {
            if (isSearchActive) {
                adapter.updateList(eventos);
                updateEmptyViewVisibility(eventos != null ? eventos.size() : 0);
            }
        });
    }


    /*
     *  Este método se encarga de actualizar la lista de eventos
     *  en el recycler view cuando se actualiza la lista de eventos
     *  debido a la interación del usuario con el calendario.
     */
    private void updateEventsInAdapter() {
        calendarViewModel.getEventosList().observe(getViewLifecycleOwner(), eventos -> {
            if (!isSearchActive) {
                adapter.updateList(eventos);
                updateEmptyViewVisibility(eventos != null ? eventos.size() : 0);
            }
        });
    }

    private void updateEmptyViewVisibility(int itemCount) {
        if (itemCount > 0) {
            binding.rvListEventItems.setVisibility(View.VISIBLE);
            if (emptyView != null) {
                emptyView.setVisibility(View.GONE);
            }
        } else {
            binding.rvListEventItems.setVisibility(View.GONE);
            if (emptyView == null && viewStubEmpty != null) {
                emptyView = viewStubEmpty.inflate();
                emptyView.setVisibility(View.VISIBLE);
            } else if (emptyView != null) {
                emptyView.setVisibility(View.VISIBLE);
            }
        }
    }

    private void setupRecyclerView() {
        adapter = new ItemEventRecyclerAdapter(new ArrayList<>());
        binding.rvListEventItems.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvListEventItems.setAdapter(adapter);
    }


}
