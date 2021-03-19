package com.mahmoudjoe3.eComStore.ui.adminUI.addProduct;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.mahmoudjoe3.eComStore.R;
import com.mahmoudjoe3.eComStore.model.Admin;
import com.mahmoudjoe3.eComStore.model.Product;
import com.mahmoudjoe3.eComStore.prevalent.Prevalent;

import butterknife.BindView;
import butterknife.ButterKnife;
import github.chenupt.springindicator.SpringIndicator;
import github.chenupt.springindicator.viewpager.ScrollerViewPager;

public class AdminAddProductActivity extends AppCompatActivity {

    String mCAT;
    Admin mAdmin;
    PagerAdapter pagerAdapter;
    @BindView(R.id.view_pager)
    ScrollerViewPager viewPager;
    @BindView(R.id.indicator)
    SpringIndicator indicator;

    Product product;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_activity_add_product);
        ButterKnife.bind(this);
        mAdmin= (Admin) getIntent().getSerializableExtra(Prevalent.USER_DATA);

        pagerAdapter = new PagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        indicator.setViewPager(viewPager);

        product=(Product) getIntent().getSerializableExtra(Prevalent.refColName_product);
        if(product!=null) {
            AddProductFragment.fitchDataFromOutSide(product,mAdmin);
            viewPager.setCurrentItem(2);
        }else {
            AddProductFragment.fitchDataFromOutSide(null,mAdmin);
        }

    }

    public void sendDataToActivity(String Category){
            mCAT = Category;
            AddProductFragment.sendDataToFragment(mCAT, mAdmin);
    }



}



