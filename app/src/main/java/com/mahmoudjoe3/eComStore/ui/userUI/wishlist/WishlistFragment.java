package com.mahmoudjoe3.eComStore.ui.userUI.wishlist;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;
import com.mahmoudjoe3.eComStore.R;
import com.mahmoudjoe3.eComStore.model.AuthorizedUser;
import com.mahmoudjoe3.eComStore.model.Product;
import com.mahmoudjoe3.eComStore.ui.userUI.ViewProductActivity;
import com.mahmoudjoe3.eComStore.ui.userUI.productAdapter;
import com.mahmoudjoe3.eComStore.viewModel.ShardViewModel;

import java.util.List;

public class WishlistFragment extends Fragment {

    String userId;
    RecyclerView pList;
    AuthorizedUser mUser;
    private WishlistViewModel wishlistViewModel;
    private productAdapter productAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        wishlistViewModel = new ViewModelProvider(this).get(WishlistViewModel.class);
        View root = inflater.inflate(R.layout.user_fragment_wishlist, container, false);

        NavigationView navView = (NavigationView) getActivity().findViewById(R.id.nav_view);
        userId = ((TextView) navView.getHeaderView(0).findViewById(R.id.profile_phone)).getText().toString();

        pList = root.findViewById(R.id.w_pList);

        productAdapter = new productAdapter(getActivity(), R.layout.user_list_item_product_layout);
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
                if (v.getTag().equals("of")) {
                    v.setTag("on");
                    v.setImageResource(R.drawable.ic_remove_cart);
                    wishlistViewModel.addCart(product.getmId(), mUser);

                } else {
                    v.setTag("of");
                    v.setImageResource(R.drawable.ic_add_cart);
                    wishlistViewModel.removeCart(product.getmId(), mUser);
                }
            }

            @Override
            public void onFavClick(Product product, ImageButton v) {
                if (v.getTag().equals("of")) {
                    v.setTag("on");
                    v.setImageResource(R.drawable.ic_fav_on);
                    wishlistViewModel.addFav(product.getmId(), mUser);
                } else {
                    v.setTag("of");
                    v.setImageResource(R.drawable.ic_fav_off);
                    wishlistViewModel.RemoveFav(product.getmId(), mUser);
                }
            }
        });

        productAdapter.setListener(new productAdapter.onClickListener() {
            @Override
            public void onClick(Product product) {
                boolean inFav = mUser.getFavList().contains(product.getmId());
                boolean inCart = mUser.getCartList().contains(product.getmId());
                Intent i = new Intent(getActivity(), ViewProductActivity.class);
                i.putExtra(ViewProductActivity.inFav_Key, inFav);
                i.putExtra(ViewProductActivity.inCart_Key, inCart);
                i.putExtra(ViewProductActivity.product_Key, product);
                i.putExtra(ViewProductActivity.user_Key, mUser);
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
        wishlistViewModel.getUserLiveData(userId).observe(getViewLifecycleOwner(), new Observer<AuthorizedUser>() {
            @Override
            public void onChanged(AuthorizedUser user) {
                mUser = user;
                wishlistViewModel.getProductsByIds(user.getFavList()).observe(getViewLifecycleOwner(), new Observer<List<Product>>() {
                    @Override
                    public void onChanged(List<Product> products) {
                        productAdapter.setProductList(products, user);
                    }
                });

            }
        });

        ShardViewModel shardViewModel = new ViewModelProvider(getActivity()).get(ShardViewModel.class);
        shardViewModel.getLiveSearch().observe(getActivity(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                productAdapter.getFilter().filter(s);
            }
        });

    }
}