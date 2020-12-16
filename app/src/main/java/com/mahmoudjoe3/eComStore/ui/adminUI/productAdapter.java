package com.mahmoudjoe3.eComStore.ui.adminUI;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mahmoudjoe3.eComStore.R;
import com.mahmoudjoe3.eComStore.model.Product;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class productAdapter extends RecyclerView.Adapter<productAdapter.ProductViewHolder> {

    private Context context;
    private List<Product> productList;

    public void setProductList(List<Product> productList) {
        this.productList = productList;
        notifyDataSetChanged();
    }
    public productAdapter(Context context) {
        this.context = context;
        productList=new ArrayList<>();
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v=LayoutInflater.from(context).inflate(R.layout.item_product_layout, parent, false);
        return new ProductViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product currentProduct=productList.get(position);
        Picasso.get()
                .load(currentProduct.getmImageUri().get(0))
                .fit()
                .centerCrop()
                .into(holder.mProductImage);
        holder.mProductTitle.setText(currentProduct.getmTitle());
        holder.mProductPrice.setText(String.valueOf(currentProduct.getmPrice())+" EGP");
        holder.mProductDesc.setText(currentProduct.getmDescription());

    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public class ProductViewHolder extends RecyclerView.ViewHolder{
        ImageView mProductImage;
        TextView mProductTitle,mProductDesc,mProductPrice;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            mProductImage=itemView.findViewById(R.id.item_productImage);
            mProductTitle=itemView.findViewById(R.id.item_productName);
            mProductDesc=itemView.findViewById(R.id.item_productDes);
            mProductPrice=itemView.findViewById(R.id.item_productPrice);
        }
    }
}
