package com.mahmoudjoe3.eComStore.ui.userUI.category;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;
import com.mahmoudjoe3.eComStore.R;
import com.mahmoudjoe3.eComStore.model.AuthorizedUser;
import com.mahmoudjoe3.eComStore.model.Product;
import com.mahmoudjoe3.eComStore.ui.userUI.UserHomeActivity;
import com.mahmoudjoe3.eComStore.ui.userUI.ViewProductActivity;
import com.mahmoudjoe3.eComStore.ui.userUI.productAdapter;
import com.mahmoudjoe3.eComStore.viewModel.ShardViewModel;

import java.util.List;

public class CategoryFragment extends Fragment {
    private static final String TAG = "CategoryFragment.me";
    private CategoryViewModel categoryViewModel;
    String Cat;
    RecyclerView pList;
    private productAdapter productAdapter;
    AuthorizedUser mUser;
    String userId;
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.user_fragment_catagory, container, false);
        NavigationView navView = (NavigationView) getActivity().findViewById(R.id.nav_view);
        userId = ((TextView) navView.getHeaderView(0).findViewById(R.id.profile_phone)).getText().toString();

        categoryViewModel = new ViewModelProvider(this).get(CategoryViewModel.class);

        pList = root.findViewById(R.id.pList);
        Cat = String.valueOf(((Toolbar) getActivity().findViewById(R.id.toolbar)).getTitle());

        productAdapter = new productAdapter(getActivity(), R.layout.user_item_product_layout);
        pList.setAdapter(productAdapter);
        pList.setHasFixedSize(true);
        pList.setLayoutManager(new LinearLayoutManager(getActivity()));

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        productAdapter.setOnImageButtonClickListener(new productAdapter.onImageButtonClickListener() {
            @Override
            public void onCartClick(Product product, ImageButton v) {
                if(v.getTag().equals("of")){
                    v.setTag("on");
                    v.setImageResource(R.drawable.ic_remove_cart);
                    //mUser.getCartList().add(product.getmId());
                    categoryViewModel.addCart(product.getmId(),mUser);

                }
                else {
                    v.setTag("of");
                    v.setImageResource(R.drawable.ic_add_cart);
                    //mUser.getCartList().remove(product.getmId());
                    categoryViewModel.removeCart(product.getmId(),mUser);
                }
            }

            @Override
            public void onFavClick(Product product, ImageButton v) {
                if(v.getTag().equals("of")){
                    v.setTag("on");
                    v.setImageResource(R.drawable.ic_fav_on);
                    categoryViewModel.addFav(product.getmId(),mUser);
                }
                else {
                    v.setTag("of");
                    v.setImageResource(R.drawable.ic_fav_off);
                    categoryViewModel.RemoveFav(product.getmId(),mUser);
                }
            }
        });

        productAdapter.setListener(new productAdapter.onClickListener() {
            @Override
            public void onClick(Product product) {
                boolean inFav= mUser.getFavList().contains(product.getmId());
                boolean inCart= mUser.getCartList().contains(product.getmId());
                Intent i=new Intent(getActivity(), ViewProductActivity.class);
                i.putExtra(ViewProductActivity.inFav_Key,inFav);
                i.putExtra(ViewProductActivity.inCart_Key,inCart);
                i.putExtra(ViewProductActivity.product_Key,product);
                i.putExtra(ViewProductActivity.user_Key,mUser);
                startActivity(i);
            }

            @Override
            public void onDelete(Product product) {

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        categoryViewModel.getProductsLiveData(null, Cat).observe(getViewLifecycleOwner(), new Observer<List<Product>>() {
            @Override
            public void onChanged(List<Product> products) {
                categoryViewModel.getUserLiveData(userId).observe(getViewLifecycleOwner(), new Observer<AuthorizedUser>() {
                    @Override
                    public void onChanged(AuthorizedUser user) {
                        mUser = user;
                        productAdapter.setProductList(products, user);
                    }
                });
            }
        });

        ShardViewModel shardViewModel=new ViewModelProvider(getActivity()).get(ShardViewModel.class);
        shardViewModel.getLiveSearch().observe(getActivity(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                productAdapter.getFilter().filter(s);
            }
        });

    }

}