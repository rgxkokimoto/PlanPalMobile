package com.example.planpalmobile.ui.eventmanager.comon;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.text.InputType;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.planpalmobile.R;
import com.example.planpalmobile.data.entities.Evento;
import com.example.planpalmobile.data.repository.EventosRepository;
import com.google.android.material.button.MaterialButton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.function.Consumer;

public class EventManagerViewModel extends ViewModel{

    private final EventosRepository repository = new EventosRepository();



    public enum EventCreationStatus {
        ERROR_TITLE,
        ERROR_DATE,
        ERROR_RESPONSE,
        ERROR_USER,
        ERROR_NETWORK,
        OK,
        NEW_EVENT
    }

    MutableLiveData<String> eventoId = new MutableLiveData<>();
    private final MutableLiveData<String> title = new MutableLiveData<>();
    private final MutableLiveData<Date> startDate = new MutableLiveData<>();
    private final MutableLiveData<Date> endDate = new MutableLiveData<>();
    private final MutableLiveData<String> description = new MutableLiveData<>();
    private final MutableLiveData<List<Date>> availableDates = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<String> etiquetas = new MutableLiveData<>("personal");
    private final MutableLiveData<List<Date>> horasDisponibles = new MutableLiveData<>(new ArrayList<>());

    private final MutableLiveData<EventCreationStatus> respNewEvent = new MutableLiveData<>();
    public LiveData<EventCreationStatus> getEventCreationStatus() { return respNewEvent; }

    public void setTitle(String t) { title.setValue(t); }
    public void setStartDate(Date d) { startDate.setValue(d); }
    public void setEndDate(Date d) { endDate.setValue(d); }
    public void setDescription(String d) { description.setValue(d); }
    public void setAvailableDates(List<Date> d) { availableDates.setValue(d); }
    public void setEtiquetas(String e) { etiquetas.setValue(e); }
    public void setHorasDisponibles(List<Date> horasDisponibles) {}
    public void setNewEventState() { respNewEvent.setValue(EventCreationStatus.NEW_EVENT); }
    public void setEventoId(String id) { eventoId.setValue(id); }


    public void removeAvailableDate(Date d) {
        List<Date> list = availableDates.getValue();
        list.remove(d);
        availableDates.setValue(list);
    }

    // Validación y creación de nuevo evento
    public void validateNewEvent() {
        String t = title.getValue();
        Date dateIn = startDate.getValue();
        Date dateEnd = endDate.getValue();
        String desc = description.getValue();
        List<Date> dateList = availableDates.getValue();
        Log.d("CreateEventDetailFragment", "validateNewEvent: " + dateList);
        String category = etiquetas.getValue();

        if (t == null || t.trim().isEmpty()) {
            respNewEvent.setValue(EventCreationStatus.ERROR_TITLE);
            return;
        }

        if (dateIn == null || dateEnd == null || !dateIn.before(dateEnd)) {
            respNewEvent.setValue(EventCreationStatus.ERROR_DATE);
            return;
        }

        repository.createNewEvent(t, dateIn, dateEnd, desc, dateList, category ,resp -> {
            if ("OK".equals(resp)) {
                respNewEvent.setValue(EventCreationStatus.OK);
            } else {
                respNewEvent.setValue(EventCreationStatus.ERROR_NETWORK);
            }
        });
    }

    public boolean validateNewMeet(Date newDate, List<Date> evetDate, Date evenDate, Date eventEndEnd) {
        if (newDate.before(evenDate) || newDate.after(eventEndEnd)) return false;
        for (Date date : evetDate) {
            if (date.equals(newDate)) return false;
        }
        return true;
    }


    private final MutableLiveData<List<Evento>> eventosUsuario = new MutableLiveData<>();
    public LiveData<List<Evento>> getEventosUsuario() { return eventosUsuario; }

    public void cargarEventosDelUsuario(String userId) {
        repository.getEventosUsuario(userId, new EventosRepository.EventosCallback() {
            @Override
            public void onSuccess(List<Evento> eventos) {
                eventosUsuario.setValue(eventos);
            }

            @Override
            public void onError(String error) {
                Log.e("EventManagerViewModel", "Error al cargar eventos: " + error);
                eventosUsuario.setValue(null);
            }
        });
    }

    public Date parseDateTimeFromButtons(MaterialButton btnPickDay, MaterialButton btnPickTime) {
        String dateStr = btnPickDay.getText().toString();
        String timeStr = btnPickTime.getText().toString();

        String combined = dateStr + " " + timeStr;
        SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

        inputFormat.setTimeZone(TimeZone.getTimeZone("Europe/Madrid"));
        inputFormat.setLenient(false);

        try {
            return inputFormat.parse(combined);
        } catch (ParseException e) {
            e.printStackTrace();
            Log.e("EventManagerViewModel", "Error al parsear fecha y hora: " + e.getMessage());
            return null;
        }
    }



    public void eliminarEventoDeFirestore(String codigoEvento) {
        repository.eliminarEvento(codigoEvento, success -> {
            if (success) {
                Log.d("EventManagerViewModel", "Evento eliminado correctamente");
            } else {
                Log.e("EventManagerViewModel", "Error al eliminar el evento");
            }
        });
    }


    public void updateFieldEvent(String codEvnent, Map<String, Object> updField, Consumer<String> onUpdate) {
        repository.actualizarEvento(codEvnent, updField, success -> {
            if (success) {
                onUpdate.accept("OK");
            } else {
                onUpdate.accept("ERROR");
            }
        });
    }

    MutableLiveData<MaterialButton> initDay = new MutableLiveData<>();
    MutableLiveData<MaterialButton> initTime = new MutableLiveData<>();
    MutableLiveData<MaterialButton> endDay = new MutableLiveData<>();
    MutableLiveData<MaterialButton> endTime = new MutableLiveData<>();
    public void setInitDay(MaterialButton initDay) { this.initDay.setValue(initDay); }
    public void setInitTime(MaterialButton initTime) { this.initTime.setValue(initTime); }
    public void setEndDay(MaterialButton endDay) { this.endDay.setValue(endDay); }
    public void setEndTime(MaterialButton endTime) { this.endTime.setValue(endTime); }

    public void validateNewDateUpdated(MaterialButton btnUpdatedA,@Nullable MaterialButton btnUpdatedB ,Consumer<String> status) {
        Date oldDateIn = startDate.getValue();
        Date oldDateEn = endDate.getValue();

        if (oldDateIn == null && oldDateEn == null) {
            status.accept("No hay fechas antiguas");
            return;
        }

        Date newDateIn = parseDateTimeFromButtons(initDay.getValue(), initTime.getValue());
        Date newDateEn = parseDateTimeFromButtons(endDay.getValue(), endTime.getValue());

        if (newDateIn.after(newDateEn) || newDateIn.equals(newDateEn)) {
            if (newDateEn.equals(newDateIn)) status.accept("Las fechas no pueden ser iguales");
            else status.accept("La fecha de inicio es posterior a la fecha de fin");

            btnUpdatedA.setBackgroundColor(btnUpdatedA.getContext().getResources().getColor(R.color.red_error_btn));
            if (btnUpdatedB != null) btnUpdatedB.setBackgroundColor(btnUpdatedB.getContext().getResources().getColor(R.color.red_error_btn));
            return;
        }

        Toast.makeText(btnUpdatedA.getContext(), "Fechas actualizadas correctamente", Toast.LENGTH_SHORT).show();

        Map<String, Object> data = new HashMap<>();
        data.put("horaInicio", newDateIn);
        updateFieldEvent(eventoId.getValue(), data, resp -> {
            if ("OK".equals(resp)) {
                status.accept("OK");
            } else {
                status.accept("ERROR");
            }
        });

        Map<String, Object> data2 = new HashMap<>();
        data2.put("horaFin", newDateEn);
        updateFieldEvent(eventoId.getValue(), data2, resp -> {
            if ("OK".equals(resp)) {
                status.accept("OK");
            } else {
                status.accept("ERROR");
            }
        });
    }



    public void clearMutableData() {
        title.setValue(null);
        startDate.setValue(null);
        endDate.setValue(null);
        description.setValue(null);
        availableDates.setValue(new ArrayList<>());
    }

    // METODOS PARA LA VISTA

    public void putNewDesc(Context usedFragment, Consumer<String> onDescSaved) {
        EditText input = new EditText(usedFragment);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        input.setText(description.getValue());
        new AlertDialog.Builder(usedFragment)
                .setTitle("Introduce una descripción")
                .setView(input)
                .setPositiveButton("Guardar", (dialog, which) -> {
                    String newDesc = input.getText().toString();
                    description.setValue(newDesc);
                    onDescSaved.accept(newDesc);
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }


    public void putNewCategory(Context context, Consumer<String> etiqueta) {
        String[] etiquetasOpciones = {"profesional", "ocio", "personal", "academico"};

        final int[] selectedIndex = {-1};

        new AlertDialog.Builder(context)
                .setTitle("Selecciona una Categoria")
                .setSingleChoiceItems(etiquetasOpciones, -1, (dialog, which) -> {
                    selectedIndex[0] = which;
                })
                .setPositiveButton("Guardar", (dialog, which) -> {
                    if (selectedIndex[0] != -1) {
                        String etiquetaSeleccionada = etiquetasOpciones[selectedIndex[0]];
                        etiquetas.setValue(etiquetaSeleccionada);
                        etiqueta.accept(etiquetaSeleccionada);
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();

    }

    public void showDatePicker(Context context ,Button button, @Nullable Button endBtn, @Nullable Runnable onDateSet) {
        Calendar calendar = Calendar.getInstance();

        new DatePickerDialog(context, (view, year, month, dayOfMonth) -> {
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

            if (onDateSet != null) {
                onDateSet.run();
            }

        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();

    }

    public void showTimePicker(Context context ,Button targetBtn , @Nullable Runnable onTimeSet) {
        Calendar now = Calendar.getInstance();

        new TimePickerDialog(context, (view, hourOfDay, minute) -> {
            String formattedTime = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute);
            targetBtn.setText(formattedTime);
            targetBtn.setHint(formattedTime);

            if (onTimeSet != null) {
                onTimeSet.run();
            }

        }, now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), true).show();
    }


}
