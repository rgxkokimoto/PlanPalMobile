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
        setUpEventosInRV();
        calendarViewModel.loadEventos();
    }

    private void observeCalendarDays() {
        calendarViewModel.getDiasCalendario().observe(getViewLifecycleOwner(), eventos -> {
            binding.calendarView.setCalendarDays(eventos);
        });
    }

    private void setUpEventosInRV() {
        calendarViewModel.getEventosList().observe(getViewLifecycleOwner(), eventos -> {
            adapter.updateList(eventos);
        });
    }

    private void setupDayClickListener() {
        binding.calendarView.setOnDayClickListener(eventDay -> {
            int day = eventDay.getCalendar().get(Calendar.DAY_OF_MONTH);
            binding.tvDiaSelect.setText("día " + day);
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
