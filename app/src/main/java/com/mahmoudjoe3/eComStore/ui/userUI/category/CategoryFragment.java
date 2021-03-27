package com.mahmoudjoe3.eComStore.ui.userUI.category;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.android.material.navigation.NavigationView;
import com.mahmoudjoe3.eComStore.R;
import com.mahmoudjoe3.eComStore.model.AuthorizedUser;
import com.mahmoudjoe3.eComStore.model.Product;
import com.mahmoudjoe3.eComStore.ui.userUI.ViewProductActivity;
import com.mahmoudjoe3.eComStore.ui.userUI.productAdapter;
import com.mahmoudjoe3.eComStore.viewModel.ShardViewModel;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CategoryFragment extends Fragment {
    String Cat;
    RecyclerView pList;
    Button filter, sort;
    ImageButton showAsGrid;
    String sortType;
    AuthorizedUser mUser;
    List<Product> mProducts;
    String userId;
    private CategoryViewModel categoryViewModel;
    private productAdapter productAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.user_fragment_catagory, container, false);
        NavigationView navView = (NavigationView) getActivity().findViewById(R.id.nav_view);
        userId = ((TextView) navView.getHeaderView(0).findViewById(R.id.profile_phone)).getText().toString();

        categoryViewModel = new ViewModelProvider(this).get(CategoryViewModel.class);

        findView(root);
        Cat = String.valueOf(((Toolbar) getActivity().findViewById(R.id.toolbar)).getTitle());

        productAdapter = new productAdapter(getActivity(), R.layout.user_list_item_product_layout);
        pList.setAdapter(productAdapter);
        pList.setHasFixedSize(true);
        pList.setLayoutManager(new LinearLayoutManager(getActivity()));

        return root;
    }


    private void findView(View root) {
        pList = root.findViewById(R.id.pList);
        //filter=root.findViewById(R.id.filter);
        sort = root.findViewById(R.id.sort);
        showAsGrid = root.findViewById(R.id.showGrid);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sort.setOnClickListener(v -> sortByDialog().show());

        showAsGrid.setOnClickListener(v -> {
            if (showAsGrid.getTag().equals("grid")) {
                MakeGrid();
            } else MakeList();
            AdapterListener();
        });

        AdapterListener();
    }

    private AlertDialog sortByDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = getLayoutInflater().inflate(R.layout.sortby_dialoge, null);
        RadioButton highToLow = view.findViewById(R.id.highToLow);
        builder.setView(view)
                .setPositiveButton(R.string.sort, (dialog, id) -> {

                    if (highToLow.isChecked()) {
                        sortType = getString(R.string.HighToLow);
                        SortByPrice(sortType);
                    } else {
                        sortType = getString(R.string.LowToHigh);
                        SortByPrice(sortType);
                    }
                })
                .setNegativeButton(R.string.back, null);
        return builder.create();
    }

    private void SortByPrice(String type) {
        Collections.sort(mProducts, new SortByPrice(type));
        productAdapter.setProductList(mProducts);
    }

    private void MakeList() {
        showAsGrid.setImageResource(R.drawable.ic_grid);
        showAsGrid.setTag("grid");

        productAdapter = new productAdapter(getActivity(), R.layout.user_list_item_product_layout);
        productAdapter.setProductList(mProducts, mUser);
        pList.setAdapter(productAdapter);
        pList.setHasFixedSize(true);
        pList.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    private void MakeGrid() {
        showAsGrid.setImageResource(R.drawable.ic_list);
        showAsGrid.setTag("list");

        productAdapter = new productAdapter(getActivity(), R.layout.user_grid_item_product_layout);
        productAdapter.setProductList(mProducts, mUser);
        pList.setAdapter(productAdapter);
        pList.setHasFixedSize(true);
        pList.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
    }

    private void AdapterListener() {
        productAdapter.setOnImageButtonClickListener(new productAdapter.onImageButtonClickListener() {
            @Override
            public void onCartClick(Product product, ImageButton v) {
                if (v.getTag().equals("of")) {
                    v.setTag("on");
                    v.setImageResource(R.drawable.ic_remove_cart);
                    //mUser.getCartList().add(product.getmId());
                    categoryViewModel.addCart(product.getmId(), mUser);

                } else {
                    v.setTag("of");
                    v.setImageResource(R.drawable.ic_add_cart);
                    //mUser.getCartList().remove(product.getmId());
                    categoryViewModel.removeCart(product.getmId(), mUser);
                }
            }

            @Override
            public void onFavClick(Product product, ImageButton v) {
                if (v.getTag().equals("of")) {
                    v.setTag("on");
                    v.setImageResource(R.drawable.ic_fav_on);
                    categoryViewModel.addFav(product.getmId(), mUser);
                } else {
                    v.setTag("of");
                    v.setImageResource(R.drawable.ic_fav_off);
                    categoryViewModel.RemoveFav(product.getmId(), mUser);
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
        categoryViewModel.getProductsLiveData(null, Cat).observe(getViewLifecycleOwner(), products -> categoryViewModel.getUserLiveData(userId).observe(getViewLifecycleOwner(), user -> {
            mUser = user;
            if (mProducts == null || mProducts.isEmpty())
                mProducts = products;

            productAdapter.setProductList(mProducts, user);
        }));

        ShardViewModel shardViewModel = new ViewModelProvider(getActivity()).get(ShardViewModel.class);
        shardViewModel.getLiveSearch().observe(getActivity(), s -> productAdapter.getFilter().filter(s));

    }

    class SortByPrice implements Comparator<Product> {
        String type;

        public SortByPrice(String type) {
            this.type = type;
        }

        @Override
        public int compare(Product o1, Product o2) {
            return (type.equals(getString(R.string.LowToHigh))) ? (int) (o1.getmPrice() - o2.getmPrice()) : (int) (o2.getmPrice() - o1.getmPrice());
        }
    }

}