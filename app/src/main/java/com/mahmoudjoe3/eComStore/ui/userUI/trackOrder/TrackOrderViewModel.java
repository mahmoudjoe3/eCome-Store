package com.mahmoudjoe3.eComStore.ui.userUI.trackOrder;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class TrackOrderViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public TrackOrderViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is slideshow fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}