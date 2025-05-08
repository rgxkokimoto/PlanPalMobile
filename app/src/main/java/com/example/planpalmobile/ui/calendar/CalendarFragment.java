package com.example.planpalmobile.ui.calendar;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.planpalmobile.R;
import com.example.planpalmobile.databinding.FragmentCalendarBinding;

import java.util.ArrayList;
import java.util.Calendar;

public class CalendarFragment extends Fragment {

    private FragmentCalendarBinding binding;
    private ItemEventRecyclerAdapter adapter;
    private CalendarViewModel viewModel;
    private Calendar selectedDay;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCalendarBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(this).get(CalendarViewModel.class);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        setupRecyclerView();
        observeViewModel();
        setupCalendarClickListener();
        viewModel.loadEventos();
    }

    private void setupRecyclerView() {
        RecyclerView rv = binding.rvListEventItems;
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ItemEventRecyclerAdapter(new ArrayList<>());
        rv.setAdapter(adapter);
    }

    private void observeViewModel() {
        viewModel.getDiasCalendario().observe(getViewLifecycleOwner(), eventos ->
                binding.calendarView.setCalendarDays(eventos)
        );

        viewModel.getEventos().observe(getViewLifecycleOwner(), adapter::updateList);
    }

    private void setupCalendarClickListener() {
        binding.calendarView.setOnDayClickListener(eventDay -> {
            Calendar clickedDay = eventDay.getCalendar();

            if (selectedDay == null || !isSameDay(clickedDay, selectedDay)) {
                selectedDay = clickedDay;
                int day = clickedDay.get(Calendar.DAY_OF_MONTH);
                binding.tvDiaSelect.setText(getString(R.string.selected_day_label, day));

                // Aquí podrías cargar eventos específicos de ese día si quieres
                // viewModel.loadEventosDelDia(clickedDay);
            }
        });
    }

    private boolean isSameDay(Calendar c1, Calendar c2) {
        return c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR) &&
                c1.get(Calendar.DAY_OF_YEAR) == c2.get(Calendar.DAY_OF_YEAR);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
