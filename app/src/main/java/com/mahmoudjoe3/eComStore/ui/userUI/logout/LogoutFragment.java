package com.mahmoudjoe3.eComStore.ui.userUI.logout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.mahmoudjoe3.eComStore.R;
import com.mahmoudjoe3.eComStore.prevalent.Prevalent;
import com.mahmoudjoe3.eComStore.ui.main.MainActivity;

import java.util.Objects;

public class LogoutFragment extends Fragment {
    SharedPreferences preferences;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        LogOut();
        View root = inflater.inflate(R.layout.user_fragment_logout, container, false);
        return root;
    }

    private void LogOut() {
        preferences = Objects.requireNonNull(getActivity()).getSharedPreferences(Prevalent.LOGIN_PREF, Context.MODE_PRIVATE);

        if (preferences.contains(Prevalent.UserPhoneKey))
            preferences.edit().clear().apply();
        startActivity(new Intent(getActivity(), MainActivity.class));
        getActivity().finish();
    }
}