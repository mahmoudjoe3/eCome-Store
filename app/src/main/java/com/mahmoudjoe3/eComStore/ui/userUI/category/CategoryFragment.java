package com.mahmoudjoe3.eComStore.ui.userUI.category;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mahmoudjoe3.eComStore.R;
import com.mahmoudjoe3.eComStore.model.Product;
import com.mahmoudjoe3.eComStore.ui.ViewProductActivity;
import com.mahmoudjoe3.eComStore.ui.adminUI.AdminHomeActivity;
import com.mahmoudjoe3.eComStore.ui.adminUI.productAdapter;

import java.util.List;

import butterknife.BindView;

public class CategoryFragment extends Fragment {

    RecyclerView pList;
    private CategoryViewModel categoryViewModel;
    String Cat;
    private productAdapter productAdapter;
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.user_fragment_catagory, container, false);

        categoryViewModel = new ViewModelProvider(this).get(CategoryViewModel.class);
        Cat= String.valueOf(((Toolbar) getActivity().findViewById(R.id.toolbar)).getTitle());

        productAdapter=new productAdapter(getActivity());
        pList=root.findViewById(R.id.pList);
        pList.setAdapter(productAdapter);
        pList.setHasFixedSize(true);
        pList.setLayoutManager(new LinearLayoutManager(getActivity()));

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Toast.makeText(getActivity(), ""+Cat, Toast.LENGTH_SHORT).show();
        categoryViewModel.getProductsLiveData(null, Cat).observe(getViewLifecycleOwner(), new Observer<List<Product>>() {
            @Override
            public void onChanged(List<Product> products) {
                productAdapter.setProductList(products);
            }
        });

        productAdapter.setListener(new productAdapter.onClickListener() {
            @Override
            public void onClick(Product product) {
                Intent i=new Intent(getActivity(), ViewProductActivity.class);
                i.putExtra(ViewProductActivity.product_Key,product);
                startActivity(i);
            }

            @Override
            public void onDelete(Product product) {

            }
        });
    }
}