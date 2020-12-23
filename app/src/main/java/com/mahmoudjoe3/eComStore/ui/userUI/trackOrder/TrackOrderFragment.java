package com.mahmoudjoe3.eComStore.ui.userUI.trackOrder;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.mahmoudjoe3.eComStore.R;

public class TrackOrderFragment extends Fragment {

    private TrackOrderViewModel trackOrderViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        trackOrderViewModel =
                new ViewModelProvider(this).get(TrackOrderViewModel.class);
        View root = inflater.inflate(R.layout.user_fragment_track_order, container, false);
        final TextView textView = root.findViewById(R.id.text_slideshow);
        trackOrderViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }
}