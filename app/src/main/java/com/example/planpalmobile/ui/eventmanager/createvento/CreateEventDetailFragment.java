package com.example.planpalmobile.ui.eventmanager.createvento;

import static com.example.planpalmobile.R.*;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.planpalmobile.databinding.FragmentCreateEventDetailBinding;
import com.example.planpalmobile.ui.eventmanager.comon.EventManagerViewModel;

import com.google.android.flexbox.FlexboxLayout;
import com.google.android.material.chip.Chip;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CreateEventDetailFragment extends Fragment {

    private FragmentCreateEventDetailBinding binding;
    private final List<Date> dateList = new ArrayList<>();
    private FlexboxLayout flexboxLayout;
    private final String description = "";
    private EventManagerViewModel eMvm;
    private static final String DATE_FORMAT = "dd/MM/yyyy";
    private static final String TIME_FORMAT = "HH:mm";
    private static final SimpleDateFormat SDF = new SimpleDateFormat("HH:mm", Locale.getDefault());


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCreateEventDetailBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        eMvm = new ViewModelProvider(requireActivity()).get(EventManagerViewModel.class);
        flexboxLayout = binding.flexboxChips;

        setCurrentDateTv();

        binding.btnDescription.setOnClickListener(v -> {
            eMvm.putNewDesc(requireContext(), newDesc -> {
                // TODO
                Log.d("Descripción", newDesc);
            });
        });

        binding.btnPickDay.setOnClickListener(v -> {
            eMvm.showDatePicker(requireContext(), binding.btnPickDay, binding.btnPickDayEn, null);
            blanckButtons(color.blank_background);
        });

        binding.btnPickTime.setOnClickListener(v -> {
            eMvm.showTimePicker(requireContext(), binding.btnPickTime , null);
            blanckButtons(color.blank_background);
        });

        binding.btnPickDayEn.setOnClickListener(v -> {
            eMvm.showDatePicker(requireContext(), binding.btnPickDayEn, null, null);
            blanckButtons(color.blank_background);
        });

        binding.btnPickTimeEnd.setOnClickListener(v -> {
            eMvm.showTimePicker(requireContext(), binding.btnPickTimeEnd , null);
            blanckButtons(color.blank_background);
        });

        binding.btnNewDate.setOnClickListener(v -> { createNewMeet(); });

        binding.btnCategoria.setOnClickListener(v -> {
            eMvm.putNewCategory(requireContext(), etiqueta -> {
                binding.btnCategoria.setText(etiqueta);
            });
        });

        binding.btnCancelar.setOnClickListener(v -> {

            // TODO enseñar un dialog si la lista de fechas no esta vacia
            Navigation.findNavController(v).popBackStack();

        });

        binding.goBackCreate.setOnClickListener(v -> {
            Navigation.findNavController(v).popBackStack();
        });

        return root;
    }

    private void blanckButtons(int blank_background) {
        binding.btnPickDay.setBackgroundColor(getResources().getColor(blank_background));
        binding.btnPickTime.setBackgroundColor(getResources().getColor(blank_background));
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        eMvm.getEventCreationStatus().observe(getViewLifecycleOwner(), message -> {
            Log.d("CreateEventDetailFragment", "onViewCreated: " + message);
            switch (message) {
                case ERROR_TITLE:
                    Toast.makeText(requireContext(), "El título no puede estar vacío", Toast.LENGTH_SHORT).show();
                    break;
                case ERROR_DATE:
                    Toast.makeText(requireContext(), "La fecha de inicio debe ser anterior a la fecha de fin", Toast.LENGTH_SHORT).show();
                    blanckButtons(color.red_error_btn);
                    break;
                case ERROR_RESPONSE:
                    Toast.makeText(requireContext(), "Error en la creación del evento", Toast.LENGTH_SHORT).show();
                    break;
                case ERROR_USER:
                    Toast.makeText(requireContext(), "Error del registro del usuario en el evento", Toast.LENGTH_SHORT).show();
                    break;
                case ERROR_NETWORK:
                    Toast.makeText(requireContext(), "Error al conectar con el servidor", Toast.LENGTH_SHORT).show();
                    break;
                case OK:
                    Toast.makeText(requireContext(), "Evento creado correctamente", Toast.LENGTH_SHORT).show();
                    eMvm.setNewEventState();
                    Navigation.findNavController(requireView()).popBackStack();
                    break;
            }

            binding.btnCrear.setOnClickListener(this::createdNewEvent);

        });


        binding.btnCrear.setOnClickListener(this::createdNewEvent);

    }


    private void createdNewEvent(View v) {
        String title = binding.etTitulo.getText().toString();
        Date dateIn = eMvm.parseDateTimeFromButtons(binding.btnPickDay, binding.btnPickTime);
        Date dateEnd = eMvm.parseDateTimeFromButtons(binding.btnPickDayEn, binding.btnPickTimeEnd);

        eMvm.setTitle(title);
        eMvm.setStartDate(dateIn);
        eMvm.setEndDate(dateEnd);
        // eMvm.addAvailableDate(dateIn); esto ya se guarda en el método putNewDesc en el ViewModel
        eMvm.setAvailableDates(dateList);

        eMvm.validateNewEvent(); // Esto activa getEventCreationStatus() en el ViewModel
    }


    private void createNewMeet() {

        Calendar now = Calendar.getInstance();
        int hour = now.get(Calendar.HOUR_OF_DAY);
        int minute = now.get(Calendar.MINUTE);

        new TimePickerDialog(requireContext(), (view, hourOfDay, minute1) -> {
            try {

                String dateStr = binding.btnPickDay.getText().toString();

                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                Date date = sdf.parse(dateStr);

                Calendar calendar = Calendar.getInstance();
                if (date != null) {
                    calendar.setTime(date);
                }

                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute1);

                Date newDate = calendar.getTime();
                Log.d("CreateEventDetailFragment", "NewDateMeet: " + newDate);

                Date evetInitDate = eMvm.parseDateTimeFromButtons(binding.btnPickDay, binding.btnPickTime);
                Date eventEndEnd = eMvm.parseDateTimeFromButtons(binding.btnPickDayEn, binding.btnPickTimeEnd);
                Log.d("CreateEventDetailFragment", "DateInMeet: " + evetInitDate);
                Log.d("CreateEventDetailFragment", "DateEndMeet: " + eventEndEnd);

                boolean isNewMeetValid = eMvm.validateNewMeet(newDate, dateList, evetInitDate, eventEndEnd);

                if (!isNewMeetValid) {
                    Toast.makeText(requireContext(), "Fecha invalida, fuera del evento oh repetida", Toast.LENGTH_SHORT).show();
                    return;
                }

                dateList.add(newDate);
                Log.d("CreateEventDetailFragment", "createNewMeet: " + dateList);
                updateChipGroup();

            } catch (ParseException e) {
                e.printStackTrace();
                Toast.makeText(requireContext(), "Formato de meet incorrecto", Toast.LENGTH_SHORT).show();
            }

        }, hour, minute, true).show();
    }

    private void updateChipGroup() {
        flexboxLayout.removeAllViews();
        for (int i = 0; i < dateList.size(); i++) {
            Date date = dateList.get(i);
            Chip chip = new Chip(requireContext());
            chip.setText(SDF.format(date));
            chip.setCloseIconVisible(true);
            int finalI = i;
            chip.setOnCloseIconClickListener(v -> {
                dateList.remove(finalI);
                Log.d("CreateEventDetailFragment", "updateChipGroup: " + dateList);
                updateChipGroup();
            });
            flexboxLayout.addView(chip);
        }
    }

    private void setCurrentDateTv() {
        Calendar now = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
        SimpleDateFormat timeFormat = new SimpleDateFormat(TIME_FORMAT, Locale.getDefault());

        String currentDate = dateFormat.format(now.getTime());
        String currentTime = timeFormat.format(now.getTime());

        binding.btnPickDay.setText(currentDate);
        binding.btnPickDayEn.setText(currentDate);
        binding.btnPickTime.setText(currentTime);
        binding.btnPickTimeEnd.setText(currentTime);
    }


    // TODO DEPRECAR
    private void showDatePicker(Button button, @Nullable Button endBtn) {
        Calendar calendar = Calendar.getInstance();

        new DatePickerDialog(requireContext(), (view, year, month, dayOfMonth) -> {
            Calendar selectedDate = Calendar.getInstance();
            selectedDate.set(year, month, dayOfMonth);


            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            String formatDate = sdf.format(selectedDate.getTime());

            button.setText(formatDate);
            button.setHint(formatDate);

            if (endBtn != null) {
                endBtn.setText(formatDate);
                endBtn.setHint(formatDate);
            }

        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
        binding.btnPickDay.setBackgroundColor(getResources().getColor(color.blank_background));
        binding.btnPickTime.setBackgroundColor(getResources().getColor(color.blank_background));
    }

    // TODO DEPRECAR
    private void showTimePicker(Button targetBtn) {
        Calendar now = Calendar.getInstance();

        new TimePickerDialog(requireContext(), (view, hourOfDay, minute) -> {
            String formattedTime = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute);
            targetBtn.setText(formattedTime);
            targetBtn.setHint(formattedTime);

        }, now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), true).show();
        binding.btnPickDay.setBackgroundColor(getResources().getColor(color.blank_background));
        binding.btnPickTime.setBackgroundColor(getResources().getColor(color.blank_background));
    }

}