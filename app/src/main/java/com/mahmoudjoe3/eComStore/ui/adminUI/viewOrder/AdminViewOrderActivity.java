package com.mahmoudjoe3.eComStore.ui.adminUI.viewOrder;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mahmoudjoe3.eComStore.R;
import com.mahmoudjoe3.eComStore.model.OrderDB;

public class AdminViewOrderActivity extends AppCompatActivity {
    public static final String AdminName = "AdminViewOrderActivity_adminName";
    private ViewOrderViewModel viewOrderViewModel;
    private ViewOrderAdapter viewOrderAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_activity_view_order);

        Toolbar pMyToolbar = findViewById(R.id.vo_my_toolbar);
        setSupportActionBar(pMyToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewOrderViewModel = new ViewModelProvider(this).get(ViewOrderViewModel.class);
        String adminName = getIntent().getStringExtra(AdminName);
        RecyclerView mContainer = findViewById(R.id.admin_view_order_container);

        viewOrderAdapter = new ViewOrderAdapter(this, adminName);
        mContainer.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mContainer.setAdapter(viewOrderAdapter);

        viewOrderViewModel.getOrderList().observe(this, orderUIS -> viewOrderAdapter.setList(orderUIS));
        viewOrderAdapter.setOnClickListener(new ViewOrderAdapter.onClickListener() {
            @Override
            public void onShowLocationClick(String Lat, String Long) {
                showMap(Lat, Long);
            }

            @Override
            public void onApproveClick(OrderDB dbOrder) {
                viewOrderViewModel.approveOn(dbOrder);
            }

            @Override
            public void onDeliverClick(OrderDB dbOrder) {
                viewOrderViewModel.deliver(dbOrder);
            }
        });
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
                Toast.makeText(this, R.string.Please_install_a_maps_application, Toast.LENGTH_LONG).show();
            }
        }
    }

}
