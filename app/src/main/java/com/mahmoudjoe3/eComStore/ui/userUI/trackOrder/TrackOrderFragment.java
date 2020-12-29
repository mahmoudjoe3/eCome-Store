package com.mahmoudjoe3.eComStore.ui.userUI.trackOrder;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;
import com.mahmoudjoe3.eComStore.R;
import com.mahmoudjoe3.eComStore.model.OrderUI;
import com.mahmoudjoe3.eComStore.ui.userUI.orderSummary.OrderSummaryAdapter;

import java.util.List;

public class TrackOrderFragment extends Fragment {

    private static final String TAG = "TrackOrderFragment.me";
    private TrackOrderViewModel trackOrderViewModel;
    private RecyclerView mContainer;
    private TrackOrdersAdapter trackOrdersAdapter;
    private String userId;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        trackOrderViewModel = new ViewModelProvider(this).get(TrackOrderViewModel.class);
        View root = inflater.inflate(R.layout.user_fragment_track_order, container, false);
        NavigationView navView = (NavigationView) getActivity().findViewById(R.id.nav_view);
        userId = ((TextView) navView.getHeaderView(0).findViewById(R.id.profile_phone)).getText().toString();
        Log.d(TAG, "onCreateView: "+userId);
        mContainer=root.findViewById(R.id.track_container);

        trackOrdersAdapter = new TrackOrdersAdapter(getActivity());

        trackOrderViewModel.getOrderList(userId).observe(getViewLifecycleOwner(), new Observer<List<OrderUI>>() {
            @Override
            public void onChanged(List<OrderUI> orderUIS) {
                trackOrdersAdapter.setList(orderUIS);
                mContainer.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false));
                mContainer.setAdapter(trackOrdersAdapter);
            }
        });
        trackOrdersAdapter.setOnShowLocationListener(new TrackOrdersAdapter.onShowLocationListener() {
            @Override
            public void onClick(String Lat, String Long) {
                showMap(Lat,Long);
            }
        });
        return root;
    }


    private void showMap(String latitude,String longitude) {
        String uri = "http://maps.google.com/maps?daddr=" + latitude + "," + longitude + " (" + "Where the party is at" + ")";
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        intent.setPackage("com.google.android.apps.maps");
        try
        {
            startActivity(intent);
        }
        catch(ActivityNotFoundException ex)
        {
            try
            {
                Intent unrestrictedIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                startActivity(unrestrictedIntent);
            }
            catch(ActivityNotFoundException innerEx)
            {
                Toast.makeText(getActivity(), "Please install a maps application", Toast.LENGTH_LONG).show();
            }
        }
    }

}