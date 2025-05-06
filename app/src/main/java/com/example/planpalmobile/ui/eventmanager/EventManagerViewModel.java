package com.example.planpalmobile.ui.eventmanager;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

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