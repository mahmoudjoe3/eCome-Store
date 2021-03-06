package com.mahmoudjoe3.eComStore.ui.adminUI;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mahmoudjoe3.eComStore.R;
import com.mahmoudjoe3.eComStore.model.Product;
import com.mahmoudjoe3.eComStore.model.Admin;
import com.mahmoudjoe3.eComStore.prevalent.Prevalent;
import com.mahmoudjoe3.eComStore.ui.adminUI.addProduct.AdminAddProductActivity;
import com.mahmoudjoe3.eComStore.ui.adminUI.viewOrder.AdminViewOrderActivity;
import com.mahmoudjoe3.eComStore.ui.main.MainActivity;
import com.mahmoudjoe3.eComStore.ui.userUI.productAdapter;
import com.mahmoudjoe3.eComStore.viewModel.admin.AdminHomePageViewModel;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AdminHomeActivity extends AppCompatActivity {

    Admin mAdmin;
    SharedPreferences preferences;
    @BindView(R.id.addNewProduct)
    FloatingActionButton button2;
    @BindView(R.id.my_toolbar)
    Toolbar my_toolbar;
    @BindView(R.id.recycleList)
    RecyclerView mRecycleView;
    productAdapter mAdapter;

    AdminHomePageViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_activity_home);
        ButterKnife.bind(this);


        preferences = getSharedPreferences(Prevalent.LOGIN_PREF, Context.MODE_PRIVATE);
        mAdmin = (Admin) getIntent().getSerializableExtra(Prevalent.USER_DATA);
        my_toolbar.setTitle(mAdmin.getName()+" Home page");
        setSupportActionBar(my_toolbar);

        initRecycle();
        fitchDataByLiveDate();

        mAdapter.setListener(new productAdapter.onClickListener() {
            @Override
            public void onClick(Product product) {
                //TODO intent to add product frag
            }

            @Override
            public void onDelete(Product product) {

                new AlertDialog.Builder(AdminHomeActivity.this)
                        .setIcon(R.drawable.ic_delete)
                        .setMessage("Are you sure? ")
                        .setTitle("Delete")
                        .setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //delete here
                                mViewModel.deleteProduct(product);
                            }
                        })
                        .setPositiveButton("Back",null)
                        .create().show();
            }
        });

    }

    private void fitchDataByLiveDate() {
        mViewModel=new ViewModelProvider(this).get(AdminHomePageViewModel.class);
        mViewModel.getProductsLiveData(mAdmin.getPhone()).observe(this, new Observer<List<Product>>() {
            @Override
            public void onChanged(List<Product> productList) {
                mAdapter.setProductList(productList);
            }
        });
    }

    private void initRecycle() {
        mAdapter=new productAdapter(this,R.layout.admin_item_product_layout);
        mRecycleView.setAdapter(mAdapter);
        mRecycleView.setHasFixedSize(true);
        mRecycleView.setLayoutManager(new LinearLayoutManager(this));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.admin_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }


    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.logoutMenu:
                logout();
                break;
            case R.id.admin_orders:
                Intent intent=new Intent(AdminHomeActivity.this, AdminViewOrderActivity.class);
                intent.putExtra(AdminViewOrderActivity.AdminName, mAdmin.getName());
                startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        if (preferences.contains(Prevalent.UserPhoneKey) && preferences.contains(Prevalent.UserNameKey) && preferences.contains(Prevalent.UserPasswordKey))
            preferences.edit().clear().apply();
        startActivity(new Intent(AdminHomeActivity.this, MainActivity.class));
        finish();
    }

    @OnClick(R.id.addNewProduct)
    public void onViewClicked() {
        Intent i=new Intent(this, AdminAddProductActivity.class);
        i.putExtra(Prevalent.USER_DATA, mAdmin);
        startActivity(i);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}