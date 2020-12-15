package com.mahmoudjoe3.eComStore.ui.adminUI;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.mahmoudjoe3.eComStore.R;
import com.mahmoudjoe3.eComStore.model.User;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class SelectCatagoryFragment extends Fragment {
    @BindView(R.id.fashion)
    LinearLayout fashion;
    @BindView(R.id.furniture)
    LinearLayout furniture;
    @BindView(R.id.healthy)
    LinearLayout healthy;
    @BindView(R.id.watches)
    LinearLayout watches;
    @BindView(R.id.pc)
    LinearLayout pc;
    @BindView(R.id.tv)
    LinearLayout tv;
    @BindView(R.id.phones)
    LinearLayout phones;
    @BindView(R.id.sports)
    LinearLayout sports;

    private String mCat="fashion";

    public SelectCatagoryFragment() {
        // Required empty public constructor
    }


    public static SelectCatagoryFragment newInstance() {
        return new SelectCatagoryFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_select_catagory, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onPause() {
        super.onPause();
        ((AdminAddProductActivity)getActivity()).sendDataToActivity(mCat);
    }


    void restoreButtonsBG() {
        fashion.setBackgroundResource(R.color.colorWhite);
        furniture.setBackgroundResource(R.color.colorWhite);
        healthy.setBackgroundResource(R.color.colorWhite);
        watches.setBackgroundResource(R.color.colorWhite);
        pc.setBackgroundResource(R.color.colorWhite);
        phones.setBackgroundResource(R.color.colorWhite);
        tv.setBackgroundResource(R.color.colorWhite);
        sports.setBackgroundResource(R.color.colorWhite);
    }

    @OnClick({R.id.fashion, R.id.furniture, R.id.healthy, R.id.watches, R.id.pc, R.id.tv, R.id.phones, R.id.sports})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.fashion:
                restoreButtonsBG();
                fashion.setBackgroundResource(R.color.indicator);
                mCat = "fashion";
                break;
            case R.id.furniture:
                restoreButtonsBG();
                furniture.setBackgroundResource(R.color.indicator);
                mCat = "furniture";
                break;
            case R.id.healthy:
                restoreButtonsBG();
                healthy.setBackgroundResource(R.color.indicator);
                mCat = "healthy";
                break;
            case R.id.watches:
                restoreButtonsBG();
                watches.setBackgroundResource(R.color.indicator);
                mCat = "watches";
                break;
            case R.id.pc:
                restoreButtonsBG();
                pc.setBackgroundResource(R.color.indicator);
                mCat = "pc";
                break;
            case R.id.tv:
                restoreButtonsBG();
                tv.setBackgroundResource(R.color.indicator);
                mCat = "tv";
                break;
            case R.id.phones:
                restoreButtonsBG();
                phones.setBackgroundResource(R.color.indicator);
                mCat = "phones";
                break;
            case R.id.sports:
                restoreButtonsBG();
                sports.setBackgroundResource(R.color.indicator);
                mCat = "sports";
                break;
        }
    }



}