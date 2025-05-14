package com.example.planpalmobile.ui.eventmanager;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.Date;
import java.util.List;


/**
 * ViewModel for {@link EventManagerFragment}.
 * Haber chavales este método se va a encargar tanto de la vista principal de este fragmento como de
 * la de crear y editar eventos.

 * -Se encarga de la listas de fechas de fragmento FragmentEventManagerBinding
 *
 *- Se encarga de el trasporte de datos a la api de eventos mediante el repositorio desde el fragmento
 * de crear evento. tambien controla si los datos enviados son correctos y devuelve un callback con
 * un mensaje en consecuencia.
 */
public class EventManagerViewModel extends ViewModel {

    private final MutableLiveData<String> respNewEvent = new MutableLiveData<>();

    public LiveData<String> respNewEventIsOk() {
        return respNewEvent;
    }

    // Métodos de control de la creación de eventos en el fragmento CreateEventDetailFragment

    public void validateNewEvent(String title, Date dateIn, Date dateEnd, String dscript, List<Date> dateList) {

        if (title == null || title.trim().isEmpty()) {
            respNewEvent.setValue("ERROR_TITLE");
            return;
        }

        if (!dateIn.before(dateEnd)) {
            respNewEvent.setValue("ERROR_DATE");
            return;
        }

        // TODO: Guardar evento en base de datos

        respNewEvent.setValue("OK");
    }

    public boolean validateNewMeet(Date newDate, List<Date> evetDate, Date evenDate, Date eventEndEnd) {

        if (newDate.before(evenDate)) {
            return false;
        }

        if (newDate.after(eventEndEnd)) {
            return false;
        }

        for (Date date : evetDate) {
            if (date.equals(newDate)) {
                return false;
            }
        }

        return true;
    }


}