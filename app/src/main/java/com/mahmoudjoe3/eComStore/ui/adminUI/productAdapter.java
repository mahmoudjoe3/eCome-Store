package com.mahmoudjoe3.eComStore.ui.adminUI;

import android.content.Context;
import android.content.Intent;
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
        holder.mProductPrice.setText(currentProduct.getmPrice() +" EGP");
        if(currentProduct.getQuantity()>0) {
            holder.mProductStoke.setText("in stoke");
            holder.mProductStoke.setBackgroundResource(R.drawable.transparent_layout_with_border);
            holder.mProductStoke.setTextColor(context.getResources().getColor( R.color.colorPrimaryDark));
        }
        else {
            holder.mProductStoke.setText("out of stoke");
            holder.mProductStoke.setBackgroundResource(R.drawable.solid_button_layout_ripple);
            holder.mProductStoke.setTextColor(context.getResources().getColor( R.color.colorBG));
        }
        holder.mProductDate.setText(currentProduct.getmDate());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener!=null) {
                    listener.onClick(currentProduct);
                }
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(listener!=null) {
                    listener.onDelete(currentProduct);
                }
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public class ProductViewHolder extends RecyclerView.ViewHolder{
        ImageView mProductImage;
        TextView mProductTitle,mProductStoke,mProductPrice,mProductDate;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            mProductImage=itemView.findViewById(R.id.item_productImage);
            mProductTitle=itemView.findViewById(R.id.item_productName);
            mProductStoke=itemView.findViewById(R.id.stoke);
            mProductPrice=itemView.findViewById(R.id.item_productPrice);
            mProductDate=itemView.findViewById(R.id.Date);
        }
    }
    onClickListener listener;

    public void setListener(onClickListener listener) {
        this.listener = listener;
    }

    public interface onClickListener{
        void onClick(Product product);
        void onDelete(Product product);
    }
}
