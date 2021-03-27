package com.mahmoudjoe3.eComStore.ui.userUI;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mahmoudjoe3.eComStore.R;
import com.mahmoudjoe3.eComStore.model.AuthorizedUser;
import com.mahmoudjoe3.eComStore.model.Product;
import com.mahmoudjoe3.eComStore.ui.adminUI.SliderAdapter;
import com.mahmoudjoe3.eComStore.ui.userUI.category.CategoryViewModel;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ViewProductActivity extends AppCompatActivity {

    public final static String product_Key = "ViewProductActivity.productKey";
    public final static String inFav_Key = "ViewProductActivity.favKey";
    public final static String inCart_Key = "ViewProductActivity.cartKey";
    public final static String user_Key = "ViewProductActivity.userKey";

    public SliderAdapter mSliderAdapter;
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
    @BindView(R.id.p_fav)
    FloatingActionButton pFav;
    @BindView(R.id.p_cartAdded)
    FloatingActionButton pCartAdded;
    @BindView(R.id.p_addCart)
    Button pAddCart;
    private Product product;
    private Boolean inFav, inCart;
    private AuthorizedUser user;
    private CategoryViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_activity_view_product);
        ButterKnife.bind(this);
        product = (Product) getIntent().getSerializableExtra(product_Key);
        inFav = getIntent().getBooleanExtra(inFav_Key, false);
        inCart = getIntent().getBooleanExtra(inCart_Key, false);
        user = (AuthorizedUser) getIntent().getSerializableExtra(user_Key);
        viewModel = new ViewModelProvider(this).get(CategoryViewModel.class);

        setSupportActionBar(pMyToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        init_data();
        init_slider();

    }

    private void init_data() {
        getSupportActionBar().setTitle(product.getmTitle());
        pTitle.setText(product.getmTitle());
        pPrice.setText(product.getmPrice() + " EGP");
        pDate.setText(product.getmTime() + " - " + product.getmDate());
        pCat.setText(product.getmCategory());
        pOwner.setText(product.getmAdmin().getName());
        pDesc.setText(product.getmDescription());

        if (product.getQuantity() > 0) {
            pQuantity.setText(product.getQuantity() + " "+getString(R.string.in_stoke));
            pQuantity.setTextColor(getResources().getColor(R.color.red));
        } else {
            pQuantity.setText(getString(R.string.SOLD_OUT));
            pQuantity.setTextColor(getResources().getColor(R.color.colorOutStoke));
        }
        updateFav_and_Cart();
    }

    private void updateFav_and_Cart() {
        if (product.getQuantity() == 0) {
            pAddCart.setEnabled(false);
            pCartAdded.setEnabled(false);
        }
        updateFav();
        updateCart();
    }

    private void updateFav() {
        if (inFav) {
            pFav.setTag("on");
            pFav.setImageResource(R.drawable.ic_fav_on);
        } else {
            pFav.setTag("of");
            pFav.setImageResource(R.drawable.ic_fav_off);
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void updateCart() {
        if (inCart) {
            pCartAdded.setTag("on");
            pCartAdded.setImageResource(R.drawable.ic_remove_cart);
            pAddCart.setText(R.string.Remove_from_Cart);
            pAddCart.setBackground(getDrawable(R.drawable.solid_button_layout_ripple));
        } else {
            pCartAdded.setTag("of");
            pCartAdded.setImageResource(R.drawable.ic_add_cart);
            pAddCart.setText(R.string.Add_to_Cart);
            pAddCart.setBackground(getDrawable(R.drawable.solid_button_layout_ripple_green));
        }
    }

    private void init_slider() {
        mSliderAdapter = new SliderAdapter(product.getmImageUri());
        slider.setSliderAdapter(mSliderAdapter);
        slider.setIndicatorAnimation(IndicatorAnimationType.WORM);
        slider.setSliderTransformAnimation(SliderAnimations.DEPTHTRANSFORMATION);
        slider.startAutoCycle();
    }

    @OnClick({R.id.p_fav, R.id.p_cartAdded, R.id.p_addCart})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.p_fav:
                inFav = (!inFav);
                updateFav();
                if (!inFav)
                    viewModel.RemoveFav(product.getmId(), user);
                else
                    viewModel.addFav(product.getmId(), user);
                break;
            case R.id.p_cartAdded:
            case R.id.p_addCart:
                cartProcesses();

                break;
        }
    }

    private void cartProcesses() {
        inCart = (!inCart);
        updateCart();
        if (!inCart)
            viewModel.removeCart(product.getmId(), user);
        else
            viewModel.addCart(product.getmId(), user);
    }
}