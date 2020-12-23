package com.mahmoudjoe3.eComStore.ui.userUI.wishlist;

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

public class WishlistFragment extends Fragment {

    private WishlistViewModel wishlistViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        wishlistViewModel =
                new ViewModelProvider(this).get(WishlistViewModel.class);
        View root = inflater.inflate(R.layout.user_fragment_wishlist, container, false);

        return root;
    }
}