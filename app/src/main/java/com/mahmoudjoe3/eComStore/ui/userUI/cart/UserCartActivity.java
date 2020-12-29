package com.mahmoudjoe3.eComStore.ui.userUI.cart;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.mahmoudjoe3.eComStore.Logic.MyLogic;
import com.mahmoudjoe3.eComStore.R;
import com.mahmoudjoe3.eComStore.model.AuthorizedUser;
import com.mahmoudjoe3.eComStore.model.OrderUI;
import com.mahmoudjoe3.eComStore.model.Product;
import com.mahmoudjoe3.eComStore.model.SubOrderUI;
import com.mahmoudjoe3.eComStore.ui.userUI.ViewProductActivity;
import com.mahmoudjoe3.eComStore.ui.userUI.orderSummary.OrderSummaryActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UserCartActivity extends AppCompatActivity {


    public final static String UserCartActivity_User_Key = "UserCartActivity.userKey";
    private static final String TAG = "UserCartActivity.me";
    @BindView(R.id.o_my_toolbar)
    Toolbar oMyToolbar;
    @BindView(R.id.o_listview)
    RecyclerView oListview;
    @BindView(R.id.o_freeShipTXT)
    TextView oFreeShipTXT;
    @BindView(R.id.o_subTotal)
    TextView oSubTotal;
    @BindView(R.id.o_goToCheckout)
    Button oGoToCheckout;

    SupOrderAdapter supOrderAdapter;
    AuthorizedUser mUser;
    UserCartViewModel cartViewModel;
    Float total;
    @BindView(R.id.card1)
    CardView card1;
    @BindView(R.id.card2)
    CardView card2;
    String userKey;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_activity_cart);
        ButterKnife.bind(this);
        cartViewModel = new ViewModelProvider(this).get(UserCartViewModel.class);
        userKey= getIntent().getStringExtra(UserCartActivity_User_Key);
        setSupportActionBar(oMyToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    @Override
    protected void onStart() {
        super.onStart();
        Snackbar snackbar = Snackbar.make(this.findViewById(android.R.id.content), "No internet connection..", Snackbar.LENGTH_INDEFINITE);
        if (!MyLogic.haveNetworkConnection(this)) {
            card1.setVisibility(View.GONE);
            card2.setVisibility(View.GONE);
            snackbar.setActionTextColor(getResources().getColor(R.color.red))
                    .setAction("Exit", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            UserCartActivity.this.finish();
                        }
                    }).show();
        }else if(snackbar.isShown()) {
            card1.setVisibility(View.VISIBLE);
            card2.setVisibility(View.VISIBLE);
            snackbar.dismiss();
        }


        supOrderAdapter = new SupOrderAdapter(UserCartActivity.this);

        cartViewModel.getUserLiveData(userKey).observe(UserCartActivity.this, new Observer<AuthorizedUser>() {
            @Override
            public void onChanged(AuthorizedUser user) {
                mUser = user;
                cartViewModel.getProductsByIds(mUser.getCartList()).observe(UserCartActivity.this, new Observer<List<Product>>() {
                    @Override
                    public void onChanged(List<Product> products) {
                        List<SubOrderUI> list = new ArrayList<>();
                        for (Product p : products) {
                            list.add(new SubOrderUI(p, 1));
                        }
                        supOrderAdapter.setList(list);
                    }
                });
            }
        });


        oListview.setAdapter(supOrderAdapter);
        oListview.setLayoutManager(new LinearLayoutManager(this));
        supOrderAdapter.getTotalPriceLiveData().observe(this, new Observer<Float>() {
            @Override
            public void onChanged(Float totalPrice) {

                oSubTotal.setText(totalPrice + " EGP");
                total = totalPrice;
                String temp = "";
                if (totalPrice < 350) {
                    temp = "Add " + "<b style=\"color:black;\">" + (350 - totalPrice) + "EGP" + "</b>"
                            + " of eligible items to your order to qualify for " +
                            "<b style=\"color:black;\">" + "FREE Shipping." + "</b>";
                } else
                    temp = "Your order qualifies for " + "<b style=\"color:black;\">" + "FREE Shipping." + "</b>";

                oFreeShipTXT.setText(Html.fromHtml(temp));
            }
        });

        supOrderAdapter.setmOrderOnClickListener(new SupOrderAdapter.orderOnClickListener() {
            @Override
            public void onClick(Product product) {
                Intent intent = new Intent(UserCartActivity.this, ViewProductActivity.class);
                boolean isCart = mUser.getCartList().contains(product.getmId());
                intent.putExtra(ViewProductActivity.inCart_Key, isCart);
                boolean isFav = mUser.getFavList().contains(product.getmId());
                intent.putExtra(ViewProductActivity.inFav_Key, isFav);
                intent.putExtra(ViewProductActivity.product_Key, product);
                intent.putExtra(ViewProductActivity.user_Key, mUser);
                startActivity(intent);
            }

            @Override
            public void onDelete(Product product) {
                new AlertDialog.Builder(UserCartActivity.this)
                        .setIcon(R.drawable.ic_delete)
                        .setMessage("Are you sure? ")
                        .setTitle("Remove Form Cart")
                        .setNegativeButton("Remove", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //delete here
                                cartViewModel.removeCart(product.getmId(), mUser);
                                for (SubOrderUI s : supOrderAdapter.getList()) {
                                    if (s.getProduct().getmId() == product.getmId()) {
                                        supOrderAdapter.getList().remove(s);
                                        break;
                                    }
                                }
                                supOrderAdapter.setList(supOrderAdapter.getList());
                            }
                        })
                        .setPositiveButton("Back", null)
                        .create().show();
            }

        });

        oGoToCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(supOrderAdapter.getList().size()>0) {
                    OrderUI order = new OrderUI(supOrderAdapter.getList());
                    Float totalPrice = total;
                    Intent intent = new Intent(UserCartActivity.this, OrderSummaryActivity.class);
                    intent.putExtra(OrderSummaryActivity.USER_KEY, mUser);
                    intent.putExtra(OrderSummaryActivity.ORDER_KEY, order);
                    intent.putExtra(OrderSummaryActivity.TotalPrice_KEY, totalPrice);
                    startActivity(intent);
                }
                else Toast.makeText(UserCartActivity.this, "There is No Cart", Toast.LENGTH_SHORT).show();
            }
        });
    }
}