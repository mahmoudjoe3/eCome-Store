package com.mahmoudjoe3.eComStore.viewModel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ShardViewModel extends ViewModel {
    private static final String TAG = "ShardViewModel.me";
    private final MutableLiveData<String> liveSearch = new MutableLiveData<>();

    public LiveData<String> getLiveSearch() {
        return liveSearch;
    }

    public void setLiveSearch(String Search) {
        this.liveSearch.setValue(Search);
        Log.d(TAG, "setLiveSearch: " + liveSearch.getValue());
    }


}
