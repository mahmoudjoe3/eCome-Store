package com.mahmoudjoe3.eComStore.ui;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.mahmoudjoe3.eComStore.R;
import com.mahmoudjoe3.eComStore.model.Product;
import com.mahmoudjoe3.eComStore.ui.adminUI.SliderAdapter;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ViewProductActivity extends AppCompatActivity {

    public final static String product_Key = "ViewProductActivity.productKey";
    @BindView(R.id.p_my_toolbar)
    Toolbar pMyToolbar;
    @BindView(R.id.slider)
    SliderView slider;
    @BindView(R.id.p_title)
    TextView pTitle;
    @BindView(R.id.p_price)
    TextView pPrice;
    @BindView(R.id.p_Quantity)
    TextView pQuantity;
    @BindView(R.id.p_date)
    TextView pDate;
    @BindView(R.id.p_cat)
    TextView pCat;
    @BindView(R.id.p_owner)
    TextView pOwner;
    @BindView(R.id.p_desc)
    TextView pDesc;
    private Product product;

    public SliderAdapter mSliderAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_product);
        ButterKnife.bind(this);
        product = (Product) getIntent().getSerializableExtra(product_Key);

        setSupportActionBar(pMyToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        init_data();
        init_slider();

    }

    private void init_data() {
        getSupportActionBar().setTitle(product.getmTitle());
        pTitle.setText(product.getmTitle());
        pPrice.setText(product.getmPrice()+" EGP");
        pDate.setText(product.getmTime()+" - " +product.getmDate());
        pCat.setText(product.getmCategory());
        pOwner.setText(product.getmAdmin().getName());
        pDesc.setText(product.getmDescription());

        if(product.getQuantity()>0) {
            pQuantity.setText(product.getQuantity()+" in stoke");
            pQuantity.setTextColor(getResources().getColor( R.color.red));
        }
        else {
            pQuantity.setText("SOLD OUT");
            pQuantity.setTextColor(getResources().getColor( R.color.colorOutStoke));
        }
    }

    private void init_slider() {
        mSliderAdapter=new SliderAdapter(product.getmImageUri());
        slider.setSliderAdapter(mSliderAdapter);
        slider.setIndicatorAnimation(IndicatorAnimationType.WORM);
        slider.setSliderTransformAnimation(SliderAnimations.DEPTHTRANSFORMATION);
        slider.startAutoCycle();
    }
}