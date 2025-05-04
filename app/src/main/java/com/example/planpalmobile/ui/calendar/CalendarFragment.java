package com.example.planpalmobile.ui.calendar;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.applandeo.materialcalendarview.CalendarDay;
import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.listeners.OnDayClickListener;
import com.example.planpalmobile.databinding.FragmentCalendarBinding;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class CalendarFragment extends Fragment {

    private FragmentCalendarBinding binding;
    private ItemEventRecyclerAdapter adapter;
    private List<CalendarDay> listaAnterior = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        CalendarViewModel calendarViewModel =
                new ViewModelProvider(this).get(CalendarViewModel.class);

        binding = FragmentCalendarBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        calendarViewModel.getDiasCalendario().observe(getViewLifecycleOwner(), new Observer<List<CalendarDay>>() {
            @Override
            public void onChanged(List<CalendarDay> eventos) {

                // TODO RESOLVER PROBLEMA DEL DELAY AL CAMBIAR EL DIA
                if (!eventos.equals(listaAnterior)) {
                    Log.d("CalendarDebug", "Días actualizados: " + eventos.size());
                    binding.calendarView.setCalendarDays(eventos);
                    listaAnterior = eventos;
                }
            }
        });

        binding.calendarView.setOnDayClickListener(new OnDayClickListener() {
            @Override
            public void onDayClick(EventDay eventDay) {
                Calendar clickedDayCalendar = eventDay.getCalendar();
                int day = clickedDayCalendar.get(Calendar.DAY_OF_MONTH);

                binding.tvDiaSelect.setText("día " + day);
            }
        });



        return view;
    }

    @Override
    public void onViewCreated(View v, Bundle b) {

        RecyclerView rv = binding.rvListEventItems;
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ItemEventRecyclerAdapter(new ArrayList<>());
        rv.setAdapter(adapter);

        CalendarViewModel viewModel = new ViewModelProvider(this).get(CalendarViewModel.class);
        viewModel.getEventos().observe(getViewLifecycleOwner(), lista -> {
            adapter.updateList(lista);
        });

        viewModel.loadEventos();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}