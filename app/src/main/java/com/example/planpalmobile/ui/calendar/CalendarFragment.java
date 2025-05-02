package com.example.planpalmobile.ui.calendar;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.applandeo.materialcalendarview.CalendarDay;
import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.listeners.OnDayClickListener;
import com.example.planpalmobile.databinding.FragmentCalendarBinding;

import java.util.Calendar;
import java.util.List;

public class CalendarFragment extends Fragment {

    private FragmentCalendarBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        CalendarViewModel calendarViewModel =
                new ViewModelProvider(this).get(CalendarViewModel.class);

        binding = FragmentCalendarBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        // Leemos los dias que se insertan en el calendario
        calendarViewModel.getDiasCalendario().observe(getViewLifecycleOwner(), new Observer<List<CalendarDay>>() {
            @Override
            public void onChanged(List<CalendarDay> eventos) {
                binding.calendarView.setCalendarDays(eventos);
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
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}