package com.mahmoudjoe3.eComStore.ui.userUI.trackOrder;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Pie;
import com.anychart.enums.Align;
import com.anychart.enums.LegendLayout;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.navigation.NavigationView;
import com.mahmoudjoe3.eComStore.R;
import com.mahmoudjoe3.eComStore.model.OrderUI;
import com.mahmoudjoe3.eComStore.model.SubOrderUI;
import com.mahmoudjoe3.eComStore.viewModel.ShardViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TrackOrderFragment extends Fragment {

    //new
    Map<String, Integer> cat_freq = new HashMap<>();
    Button showSummary;
    ProgressBar progressBar;
    private RecyclerView mContainer;
    private TrackOrdersAdapter trackOrdersAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        TrackOrderViewModel trackOrderViewModel = new ViewModelProvider(this).get(TrackOrderViewModel.class);
        View root = inflater.inflate(R.layout.user_fragment_track_order, container, false);

        NavigationView navView = (NavigationView) getActivity().findViewById(R.id.nav_view);
        String userId = ((TextView) navView.getHeaderView(0).findViewById(R.id.profile_phone)).getText().toString();
        mContainer = root.findViewById(R.id.track_container);

        trackOrdersAdapter = new TrackOrdersAdapter(getActivity());

        trackOrderViewModel.getOrderList(userId).observe(getViewLifecycleOwner(), orderUIS -> {
            trackOrdersAdapter.setList(orderUIS);
            mContainer.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
            mContainer.setAdapter(trackOrdersAdapter);
            //new
            if (!orderUIS.isEmpty()) {
                cat_freq = fill_cat_freq_Map(orderUIS);
            }

        });
        trackOrdersAdapter.setOnShowLocationListener((Lat, Long) -> showMap(Lat, Long));

        //new
        showSummary = root.findViewById(R.id.showSummary);
        showSummary.setOnClickListener(v -> {
            if (!cat_freq.isEmpty()) {
                BuildSheetDialog();
            } else Toast.makeText(getActivity(), R.string.No_Orders_yet, Toast.LENGTH_SHORT).show();
        });
        return root;
    }

    //new
    @SuppressLint("ResourceAsColor")
    private void BuildSheetDialog() {
        BottomSheetDialog sheetDialog = new BottomSheetDialog(getActivity());
        sheetDialog.setContentView(R.layout.user_pi_chart_sheet_dialog);

        //init sheet data
        AnyChartView chartView = sheetDialog.findViewById(R.id.pi_chart_view);

        List<DataEntry> data = init_PiChart_Data();

        Pie pie = AnyChart.pie();
        pie.data(data);
        pie.background().fill("#334253");
        pie.title(getString(R.string.Category_Summary));
        pie.labels().position("outside");

        pie.legend()
                .position("center-bottom")
                .itemsLayout(LegendLayout.HORIZONTAL)
                .align(Align.CENTER).fontColor("#ffffff");

        chartView.setChart(pie);
        chartView.setBackgroundColor("#334253");

        progressBar = sheetDialog.findViewById(R.id.progressBarsheet);

        chartView.setProgressBar(progressBar);


        //show sheet
        sheetDialog.show();
    }

    //new
    // make freq itemSet of size 1
    private Map<String, Integer> fill_cat_freq_Map(List<OrderUI> orderUIS) {
        Map<String, Integer> cat_map = new HashMap<>();
        for (OrderUI orderUI : orderUIS) {
            for (SubOrderUI subOrderUI : orderUI.getOrderList()) {

                String cat = subOrderUI.getProduct().getmCategory();
                int quantity = subOrderUI.getQty();

                if (cat_map.containsKey(cat)) {
                    cat_map.put(cat, cat_map.get(cat) + quantity);
                } else {
                    cat_map.put(cat, quantity);
                }

            }
        }
        return cat_map;
    }

    //new
    //convert map to list<DataEntry>
    private List<DataEntry> init_PiChart_Data() {
        List<DataEntry> data = new ArrayList<>();
        for (Map.Entry<String, Integer> pair : cat_freq.entrySet()) {
            data.add(new ValueDataEntry(pair.getKey(), pair.getValue()));
        }
        return data;
    }

    private void showMap(String latitude, String longitude) {
        String uri = "http://maps.google.com/maps?daddr=" + latitude + "," + longitude + " (" + "Where the party is at" + ")";
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        intent.setPackage("com.google.android.apps.maps");
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException ex) {
            try {
                Intent unrestrictedIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                startActivity(unrestrictedIntent);
            } catch (ActivityNotFoundException innerEx) {
                Toast.makeText(getActivity(), R.string.Please_install_a_maps_application, Toast.LENGTH_LONG).show();
            }
        }
    }

    //new for search filter

    @Override
    public void onStart() {
        super.onStart();
        ShardViewModel shardViewModel = new ViewModelProvider(getActivity()).get(ShardViewModel.class);
        shardViewModel.getLiveSearch().observe(getActivity(), s -> trackOrdersAdapter.getFilter().filter(s));
    }
}