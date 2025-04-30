package com.example.planpalmobile.ui.calendar;

import android.graphics.Color;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.applandeo.materialcalendarview.CalendarDay;
import com.example.planpalmobile.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CalendarViewModel extends ViewModel {

    private final MutableLiveData<List<CalendarDay>> calendarDays = new MutableLiveData<>();

    public LiveData<List<CalendarDay>> getCalendarDays() {
        return calendarDays;
    }

    public void loadCalendarEvents() {
        List<CalendarDay> days = new ArrayList<>();

        Calendar calendar = Calendar.getInstance();
        CalendarDay day = new CalendarDay(calendar);

        day.setLabelColor(Color.parseColor("#228B22"));
        day.setImageResource(R.drawable.baseline_calendar_month_24);

        days.add(day);

        calendarDays.setValue(days);
    }
}
