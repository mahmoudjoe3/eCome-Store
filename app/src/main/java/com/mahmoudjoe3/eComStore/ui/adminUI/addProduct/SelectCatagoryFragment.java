package com.mahmoudjoe3.eComStore.ui.adminUI.addProduct;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.mahmoudjoe3.eComStore.R;

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
        View view = inflater.inflate(R.layout.admin_fragment_select_catagory, container, false);
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
        fashion.setBackgroundResource(R.color.colorAccent);
        furniture.setBackgroundResource(R.color.colorAccent);
        healthy.setBackgroundResource(R.color.colorAccent);
        watches.setBackgroundResource(R.color.colorAccent);
        pc.setBackgroundResource(R.color.colorAccent);
        phones.setBackgroundResource(R.color.colorAccent);
        tv.setBackgroundResource(R.color.colorAccent);
        sports.setBackgroundResource(R.color.colorAccent);
    }

    @OnClick({R.id.fashion, R.id.furniture, R.id.healthy, R.id.watches, R.id.pc, R.id.tv, R.id.phones, R.id.sports})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.fashion:
                restoreButtonsBG();
                fashion.setBackgroundResource(R.color.colorOpenMap);
                mCat = "fashion";
                break;
            case R.id.furniture:
                restoreButtonsBG();
                furniture.setBackgroundResource(R.color.colorOpenMap);
                mCat = "furniture";
                break;
            case R.id.healthy:
                restoreButtonsBG();
                healthy.setBackgroundResource(R.color.colorOpenMap);
                mCat = "health & beauty";
                break;
            case R.id.watches:
                restoreButtonsBG();
                watches.setBackgroundResource(R.color.colorOpenMap);
                mCat = "watches";
                break;
            case R.id.pc:
                restoreButtonsBG();
                pc.setBackgroundResource(R.color.colorOpenMap);
                mCat = "pc & accessories";
                break;
            case R.id.tv:
                restoreButtonsBG();
                tv.setBackgroundResource(R.color.colorOpenMap);
                mCat = "tvs";
                break;
            case R.id.phones:
                restoreButtonsBG();
                phones.setBackgroundResource(R.color.colorOpenMap);
                mCat = "mobiles & tablets";
                break;
            case R.id.sports:
                restoreButtonsBG();
                sports.setBackgroundResource(R.color.colorOpenMap);
                mCat = "sports";
                break;
        }
    }



}