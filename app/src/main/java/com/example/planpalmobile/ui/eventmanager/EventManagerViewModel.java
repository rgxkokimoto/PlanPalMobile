package com.example.planpalmobile.ui.eventmanager;

import android.app.AlertDialog;
import android.content.Context;
import android.text.InputType;
import android.util.Log;
import android.widget.EditText;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.planpalmobile.data.entities.Evento;
import com.example.planpalmobile.data.repository.EventosRepository;
import com.google.android.material.button.MaterialButton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class EventManagerViewModel extends ViewModel {

    private final EventosRepository repository = new EventosRepository();

    public enum EventCreationStatus {
        ERROR_TITLE,
        ERROR_DATE,
        ERROR_RESPONSE,
        ERROR_USER,
        ERROR_NETWORK,
        OK
    }

    private final MutableLiveData<String> title = new MutableLiveData<>();
    private final MutableLiveData<Date> startDate = new MutableLiveData<>();
    private final MutableLiveData<Date> endDate = new MutableLiveData<>();
    private final MutableLiveData<String> description = new MutableLiveData<>();
    private final MutableLiveData<List<Date>> availableDates = new MutableLiveData<>(new ArrayList<>());

    private final MutableLiveData<EventCreationStatus> respNewEvent = new MutableLiveData<>();
    public LiveData<EventCreationStatus> getEventCreationStatus() { return respNewEvent; }

    public void setTitle(String t) { title.setValue(t); }
    public void setStartDate(Date d) { startDate.setValue(d); }
    public void setEndDate(Date d) { endDate.setValue(d); }
    public void setDescription(String d) { description.setValue(d); }
    public void addAvailableDate(Date d) {
        List<Date> list = availableDates.getValue();
        if (!list.contains(d)) {
            list.add(d);
            availableDates.setValue(list);
        }
    }

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

        if (t == null || t.trim().isEmpty()) {
            respNewEvent.setValue(EventCreationStatus.ERROR_TITLE);
            return;
        }

        if (dateIn == null || dateEnd == null || !dateIn.before(dateEnd)) {
            respNewEvent.setValue(EventCreationStatus.ERROR_DATE);
            return;
        }

        repository.createNewEvent(t, dateIn, dateEnd, desc, dateList, resp -> {
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


    // METODOS PARA LA VISTA

    public void putNewDesc(Context usedFragment) {
        EditText input = new EditText(usedFragment);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        new AlertDialog.Builder(usedFragment)
                .setTitle("Introduce una descripción")
                .setView(input)
                .setPositiveButton("Guardar", (dialog, which) -> {
                    description.setValue(input.getText().toString());
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

}
