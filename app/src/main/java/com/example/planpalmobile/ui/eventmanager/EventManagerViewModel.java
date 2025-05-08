package com.example.planpalmobile.ui.eventmanager;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;


/**
 * ViewModel for {@link EventManagerFragment}.
 * Haber chavales este método se va a encargar tanto de la vista principal de este fragmento como de
 * la de crear y editar eventos.

 * -Se encarga de la listas de fechas de fragmento FragmentEventManagerBinding
 *
 *
 *
 */
public class EventManagerViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public EventManagerViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is dashboard fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}