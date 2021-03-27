package com.mahmoudjoe3.eComStore.ui.userUI.orderSummary;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.mahmoudjoe3.eComStore.Logic.MyLogic;
import com.mahmoudjoe3.eComStore.R;
import com.mahmoudjoe3.eComStore.model.AuthorizedUser;
import com.mahmoudjoe3.eComStore.model.OrderDB;
import com.mahmoudjoe3.eComStore.model.OrderUI;
import com.mahmoudjoe3.eComStore.model.SubOrderDB;
import com.mahmoudjoe3.eComStore.model.SubOrderUI;
import com.mahmoudjoe3.eComStore.repo.FirebaseRepo;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class OrderSummaryActivity extends AppCompatActivity {

    public static final String USER_KEY = "OrderSummaryActivity.userKey";
    public static final String ORDER_KEY = "OrderSummaryActivity.order";
    public static final String TotalPrice_KEY = "OrderSummaryActivity.totalPriceKey";
    AuthorizedUser user;
    OrderUI order;
    Float totalPrice;
    @BindView(R.id.o_my_toolbar)
    Toolbar oMyToolbar;
    @BindView(R.id.s_username)
    TextView sUsername;
    @BindView(R.id.s_userPhone)
    TextView sUserPhone;
    @BindView(R.id.s_delivery_date)
    TextView sDeliveryDate;
    @BindView(R.id.s_total1)
    TextView sTotal1;
    @BindView(R.id.s_freeShipping)
    TextView sFreeShipping;
    @BindView(R.id.s_total2)
    TextView sTotal2;
    @BindView(R.id.s_container)
    RecyclerView sContainer;

    @BindView(R.id.s_userAddress)
    TextView sUserAddress;
    @BindView(R.id.s_pickLocation_btn)
    TextView sPickLocationBtn;
    @BindView(R.id.s_placeOrder_btn)
    Button sPlaceOrderBtn;

    OrderSummaryAdapter adapter;
    @BindView(R.id.s_show_in_map)
    TextView sShowInMap;

    String latitude;
    String longitude;
    String addressLine;
    String delivaryDate;
    float finalPrice;
    private OrderSummaryViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_activity_order_summary);
        ButterKnife.bind(this);
        viewModel = new ViewModelProvider(this).get(OrderSummaryViewModel.class);
        setSupportActionBar(oMyToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        user = (AuthorizedUser) getIntent().getSerializableExtra(USER_KEY);
        order = (OrderUI) getIntent().getSerializableExtra(ORDER_KEY);
        totalPrice = getIntent().getFloatExtra(TotalPrice_KEY, 0);

        init_Recycle();
        init_Data();

    }

    private void init_Data() {
        sTotal1.setText(totalPrice + " EGP");
        if (totalPrice >= 350) {
            sFreeShipping.setText(getString(R.string.FREE_Shipping));
            finalPrice = totalPrice + 5;
        } else {
            sFreeShipping.setText("25.0 EGP");
            finalPrice = totalPrice + 5 + 25;
        }
        sTotal2.setText((finalPrice) + " EGP");
        sUserPhone.setText(user.getPhone());
        sUsername.setText(user.getName());

        String currentDate;
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat DateFormat = new SimpleDateFormat("MMM dd, yyyy");
        currentDate = DateFormat.format(calendar.getTime());
        try {
            calendar.setTime(DateFormat.parse(currentDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        calendar.add(Calendar.DATE, 3);  // number of days to add
        delivaryDate = DateFormat.format(calendar.getTime());  // dt is now the new date

        sDeliveryDate.setText(getString(R.string.Delivered_by)+" " + delivaryDate);


    }

    private void init_Recycle() {
        adapter = new OrderSummaryAdapter(this);
        adapter.setList(order.getOrderList());
        sContainer.setAdapter(adapter);
        sContainer.setLayoutManager(new LinearLayoutManager(this));
    }

    @OnClick({R.id.s_pickLocation_btn, R.id.s_placeOrder_btn, R.id.s_show_in_map})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.s_pickLocation_btn:
                sPickLocationBtn.setError(null);
                getLocation();
                break;
            case R.id.s_show_in_map:
                showMap();
                break;
            case R.id.s_placeOrder_btn:
                placeOrder();
                break;
        }
    }

    private void placeOrder() {
        new AlertDialog.Builder(OrderSummaryActivity.this)
                .setMessage(R.string.Once_you_place_order_you_cant_remove_it)
                .setTitle(R.string.placing_an_order)
                .setNegativeButton(R.string.Place, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (!sUserAddress.getTag().toString().toLowerCase().equals("no"))
                            upLoadOrder();
                        else {
                            sPickLocationBtn.setError(getString(R.string.you_should_enter_your_address));
                        }
                    }
                })
                .setPositiveButton(R.string.back, null)
                .create().show();

    }

    private void upLoadOrder() {
        List<OrderDB> orderDBList = new ArrayList<>();
        List<OrderUI> orderUIList = createOrderList(order);
        for (OrderUI o : orderUIList) {
            orderDBList.add(createDBOrder(o));
        }

        insertOrder_rec(0, orderDBList);

    }

    private void insertOrder_rec(int i, List<OrderDB> orderDBList) {
        if (i >= orderDBList.size()) {
            Snackbar.make(findViewById(android.R.id.content), getString(R.string.Order_Placed_successfully)+" ", Snackbar.LENGTH_LONG).show();
            return;
        }
        viewModel.insertOrder(orderDBList.get(i));
        viewModel.setOnOrderAddedListener(new FirebaseRepo.onOrderAddedListener() {
            @Override
            public void onSuccess() {
                insertOrder_rec(i + 1, orderDBList);
            }

            @Override
            public void onFailure() {
                if (!MyLogic.haveNetworkConnection(OrderSummaryActivity.this)) {
                    Snackbar.make(findViewById(android.R.id.content), R.string.Error_No_internet_connection, Snackbar.LENGTH_LONG)
                            .setAction(R.string.Exit, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    finish();
                                }
                            })
                            .setActionTextColor(getResources().getColor(R.color.red)).show();
                }
            }
        });
    }

    private OrderDB createDBOrder(OrderUI order) {
        List<SubOrderDB> orderDBList = new ArrayList<>();
        for (SubOrderUI s : order.getOrderList()) {
            orderDBList.add(new SubOrderDB(s.getProduct().getmId(), s.getQty()));
        }
        String location = latitude + "," + longitude;
        return new OrderDB(orderDBList, user.getPhone(), order.getTotalPrice(), location, delivaryDate, false, false);
    }

    private List<OrderUI> createOrderList(OrderUI orderUI) {
        List<OrderUI> list = new ArrayList<>();
        List<SubOrderUI> productsList = orderUI.getOrderList();
        Map<String, List<SubOrderUI>> H = new HashMap<>();
        String tempAdmin = "";
        for (SubOrderUI p : productsList) {
            if (!tempAdmin.equals(p.getProduct().getmAdmin().getName())) {
                tempAdmin = p.getProduct().getmAdmin().getName();
                H.put(tempAdmin, new ArrayList<>());
                H.get(tempAdmin).add(p);
            } else {
                H.get(tempAdmin).add(p);
            }
        }
        for (Map.Entry<String, List<SubOrderUI>> entry : H.entrySet()) {
            Float tot = 0f;
            for (SubOrderUI s : entry.getValue()) {
                tot += (s.getProduct().getmPrice() * s.getQty());
            }
            list.add(new OrderUI(entry.getValue(), null, null, tot, null, null, false, false));
        }
        return list;
    }

    private void showMap() {
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

    private void getLocation() {
        FusedLocationProviderClient locationProviderClient = LocationServices.getFusedLocationProviderClient(OrderSummaryActivity.this);
        if (ActivityCompat.checkSelfPermission(OrderSummaryActivity.this
                , Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            //get location
            locationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    Location location = task.getResult();
                    if (location != null) {
                        try {
                            Geocoder geocoder = new Geocoder(OrderSummaryActivity.this
                                    , Locale.getDefault());
                            List<Address> addresses = geocoder.getFromLocation(
                                    location.getLatitude()
                                    , location.getLongitude()
                                    , 1
                            );
                            //get location
                            latitude = String.valueOf(addresses.get(0).getLatitude());
                            longitude = String.valueOf(addresses.get(0).getLongitude());
                            addressLine = addresses.get(0).getAddressLine(0);
                            sUserAddress.setText(addressLine);
                            sUserAddress.setTag("yes");
                            sShowInMap.setVisibility(View.VISIBLE);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(OrderSummaryActivity.this, R.string.open_the_GPS_please, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            ActivityCompat.requestPermissions(OrderSummaryActivity.this
                    , new String[]{Manifest.permission.ACCESS_FINE_LOCATION}
                    , 44);
        }
    }

}