package com.mahmoudjoe3.eComStore.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ShardViewModel extends ViewModel {
    private final MutableLiveData<String> liveSearch = new MutableLiveData<>();

    public LiveData<String> getLiveSearch() {
        return liveSearch;
    }

    public void setLiveSearch(String Search) {
        this.liveSearch.setValue(Search);
    }


}
