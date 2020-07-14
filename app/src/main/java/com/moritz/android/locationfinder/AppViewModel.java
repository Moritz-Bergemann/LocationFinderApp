package com.moritz.android.locationfinder;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class AppViewModel extends ViewModel {
    private MutableLiveData<Boolean> hasLocationBeenDenied;

    public LiveData<Boolean> getLocationEnabled() {
        //Creating MutableLiveData if this is the first time it is called (since this object will
        //  effectively be a singleton
        hasLocationBeenDenied = new MutableLiveData<>();
        setHasLocationBeenDenied(false);

        //Return MutableLiveData as a LiveData (i.e. not mutable)
        return hasLocationBeenDenied;
    }


    public void setHasLocationBeenDenied(boolean locationEnabled) {
        hasLocationBeenDenied.setValue(locationEnabled);
    }
}
