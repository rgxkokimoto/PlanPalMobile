package com.example.planpalmobile.ui.eventmanager;

import static com.example.planpalmobile.R.*;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.planpalmobile.R;
import com.example.planpalmobile.databinding.FragmentCreateEventDetailBinding;
import com.google.android.material.button.MaterialButton;

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
    private DateAdapter adapter;
    private String description = "";
    private EventManagerViewModel eMviewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCreateEventDetailBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        eMviewModel = new ViewModelProvider(requireActivity()).get(EventManagerViewModel.class);

        // Pongo la fecha actual en el textview por si pudiera facilitar al ususario
        setCurrentDateTv();

        binding.btnDescription.setOnClickListener(v -> { putNewDesc(); });

        binding.btnPickDay.setOnClickListener(v -> { showDatePicker(binding.btnPickDay, binding.btnPickDayEn); });
        binding.btnPickTime.setOnClickListener(v -> { showTimePicker(binding.btnPickTime); });
        binding.btnPickDayEn.setOnClickListener(v -> { showDatePicker(binding.btnPickDayEn, null); });
        binding.btnPickTimeEnd.setOnClickListener(v -> { showTimePicker(binding.btnPickTimeEnd); });

        binding.btnNewDate.setOnClickListener(v -> { createNewMeet(); }); // TODO

        binding.btnCrear.setOnClickListener(this::createdNewEvent);

        binding.btnCancelar.setOnClickListener(v -> {

            // TODO enseñar un dialog si la lista de fechas no esta vacia
            Navigation.findNavController(v).popBackStack();

        });

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        adapter = new DateAdapter(dateList, new DateAdapter.OnDateActionListener() {
            @Override
            public void onDelete(Date date, int position) {
                if (position >= 0 && position < dateList.size()) {
                    dateList.remove(position);
                    adapter.notifyItemRemoved(position);
                } else {
                    Log.w("DeleteError", "Intento de borrar índice fuera de rango: " + position);
                }
                adapter.notifyItemRemoved(position);
            }
        });

        binding.rvDateEvent.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvDateEvent.setAdapter(adapter);
    }


    private void createdNewEvent(View v) {
        String title = binding.etTitulo.getText().toString();
        Date dateIn = StrMapDate(binding.btnPickDay, binding.btnPickTime);
        Date dateEnd = StrMapDate(binding.btnPickDayEn, binding.btnPickTimeEnd);
        String dscript = description;

        eMviewModel.validateNewEvent(
                title,
                dateIn,
                dateEnd,
                dscript,
                dateList
                );

        eMviewModel.respNewEventIsOk().observe(getViewLifecycleOwner(), message -> {

            switch (message) {
                case "ERROR_TITLE":
                    Toast.makeText(requireContext(), "El título no puede estar vacío", Toast.LENGTH_SHORT).show();
                    break;

                case "ERROR_DATE":
                    Toast.makeText(requireContext(), "La fecha de inicio debe ser anterior a la fecha de fin", Toast.LENGTH_SHORT).show();
                    binding.btnPickDay.setBackgroundColor(getResources().getColor(color.red_error_btn));
                    binding.btnPickTime.setBackgroundColor(getResources().getColor(color.red_error_btn));
                    break;

                case "ERROR_RESPONSE":
                    Toast.makeText(requireContext(), "Error en la creación del evento", Toast.LENGTH_SHORT).show();
                    break;

                case "ERROR_USER":
                    Toast.makeText(requireContext(), "Error del registro del usuario en el evento", Toast.LENGTH_SHORT).show();
                    break;

                case "ERROR_NETWORK":
                    Toast.makeText(requireContext(), "Error al conectar con el servidor", Toast.LENGTH_SHORT).show();
                    break;

                case "OK":
                    Toast.makeText(requireContext(), "Evento creado correctamente", Toast.LENGTH_SHORT).show();
                    binding.btnPickDay.setBackgroundColor(getResources().getColor(color.blank_background));
                    binding.btnPickTime.setBackgroundColor(getResources().getColor(color.blank_background));

                    Navigation.findNavController(v).popBackStack();

                    break;
            }


        });
    }


    private Date StrMapDate(MaterialButton btnPickDay, MaterialButton btnPickTime) {
        String dateStr = btnPickDay.getText().toString();
        String timeStr = btnPickTime.getText().toString();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        try {
            Log.d("DateStr", dateStr + " " + timeStr);
            return sdf.parse(dateStr + " " + timeStr);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void putNewDesc() {
        EditText input = new EditText(requireContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        input.setText(description);

        new AlertDialog.Builder(requireContext())
                .setTitle("Introduce una descripción")
                .setView(input)
                .setPositiveButton("Guardar", (dialog, which) -> {
                    description = input.getText().toString();
                })
                .setNegativeButton("Cancelar", null)
                .show();
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

                Date evetInitDate = StrMapDate(binding.btnPickDay, binding.btnPickTime);
                Date eventEndEnd = StrMapDate(binding.btnPickDayEn, binding.btnPickTimeEnd);

                boolean isNewMeetValid = eMviewModel.validateNewMeet(newDate, dateList, evetInitDate, eventEndEnd);

                

                if (!isNewMeetValid) {
                    Toast.makeText(requireContext(), "Fecha invalida, fuera del evento oh repetida", Toast.LENGTH_SHORT).show();
                    return;
                }

                dateList.add(newDate);
                adapter.updateList(dateList);


            } catch (ParseException e) {
                e.printStackTrace();
                Toast.makeText(requireContext(), "Formato de meet incorrecto", Toast.LENGTH_SHORT).show();
            }

        }, hour, minute, true).show();
    }

    private void setCurrentDateTv() {
        Calendar now = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

        String currentDate = dateFormat.format(now.getTime());
        String currentTime = timeFormat.format(now.getTime());

        binding.btnPickDay.setText(currentDate);
        binding.btnPickDayEn.setText(currentDate);
        binding.btnPickTime.setText(currentTime);
        binding.btnPickTimeEnd.setText(currentTime);

    }

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
    }

    private void showTimePicker(Button targetBtn) {
        Calendar now = Calendar.getInstance();

        new TimePickerDialog(requireContext(), (view, hourOfDay, minute) -> {
            String formattedTime = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute);
            targetBtn.setText(formattedTime);
            targetBtn.setHint(formattedTime);

        }, now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), true).show();
    }

}