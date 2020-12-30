package com.mahmoudjoe3.eComStore.ui.userUI;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mahmoudjoe3.eComStore.R;
import com.mahmoudjoe3.eComStore.model.AuthorizedUser;
import com.mahmoudjoe3.eComStore.model.Product;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class productAdapter extends RecyclerView.Adapter<productAdapter.ProductViewHolder> implements Filterable {

    private Context context;
    private List<Product> productList;
    private List<Product> productListFull;
    private int layoutId;
    private Boolean admin;
    AuthorizedUser mUser;



    public void setProductList(List<Product> productList, AuthorizedUser mUser) {
        this.productList = productList;
        this.mUser=mUser;
        productListFull=new ArrayList<>(productList);
        notifyDataSetChanged();
    }
    public void setProductList(List<Product> productList) {
        this.productList = productList;
        notifyDataSetChanged();
    }

    public productAdapter(Context context, int layoutId) {
        this.context = context;
        productList = new ArrayList<>();
        this.layoutId = layoutId;//R.layout.admin_item_product_layout||R.layout.user_item_product_layout
        admin = (layoutId == R.layout.admin_item_product_layout);
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(layoutId, parent, false);
        return new ProductViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        
        Product currentProduct = productList.get(position);
        Picasso.get()
                .load(currentProduct.getmImageUri().get(0))
                .fit()
                .centerCrop()
                .into(holder.mProductImage);
        holder.mProductTitle.setText(currentProduct.getmTitle());
        holder.mProductPrice.setText(currentProduct.getmPrice() + " EGP");

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onClick(currentProduct);
                }
            }
        });
        if (admin) {
            holder.mProductDate.setText(currentProduct.getmDate());
            if (currentProduct.getQuantity() > 0) {
                holder.mProductStoke.setText(currentProduct.getQuantity() + " in stoke");
                holder.mProductStoke.setTextColor(context.getResources().getColor(R.color.indicator_1));
            } else {
                holder.mProductStoke.setText("SOLD OUT");
                holder.mProductStoke.setTextColor(context.getResources().getColor(R.color.colorOutStoke));
            }
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (listener != null) {
                        listener.onDelete(currentProduct);
                    }
                    return false;
                }
            });
        }
        else {//user
            String temp="sold by " + "<b style=\"color:black;\">" + currentProduct.getmAdmin().getName() + "</b>";
            holder.mProductStoke.setText(Html.fromHtml(temp));
            if(currentProduct.getQuantity()==0) {
                holder.mAdd_cart.setEnabled(false);
                holder.mAdd_cart.setTag("of");
                holder.mAdd_cart.setImageResource(R.drawable.ic_add_cart);
            }
            else {
                holder.mAdd_cart.setEnabled(true);
                holder.mAdd_cart.setTag("on");
                holder.mAdd_cart.setImageResource(R.drawable.ic_remove_cart);
            }
            //////////fill fav and cart icon///////////
            if(mUser!=null&&mUser.getFavList()!=null&&mUser.getFavList().contains(currentProduct.getmId())){
                holder.mAdd_fav.setTag("on");
                holder.mAdd_fav.setImageResource(R.drawable.ic_fav_on);
            }
            else {
                holder.mAdd_fav.setTag("of");
                holder.mAdd_fav.setImageResource(R.drawable.ic_fav_off);
            }
            if(mUser!=null&&mUser.getCartList()!=null&&mUser.getCartList().contains(currentProduct.getmId())&&currentProduct.getQuantity()>0){
                holder.mAdd_cart.setTag("on");
                holder.mAdd_cart.setImageResource(R.drawable.ic_remove_cart);
            }
            else {
                holder.mAdd_cart.setTag("of");
                holder.mAdd_cart.setImageResource(R.drawable.ic_add_cart);
            }

            holder.mAdd_cart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onImageButtonClickListener != null) {
                        onImageButtonClickListener.onCartClick(currentProduct,(ImageButton)v);
                    }
                }
            });

            holder.mAdd_fav.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onImageButtonClickListener != null) {
                        onImageButtonClickListener.onFavClick(currentProduct,(ImageButton)v);
                    }
                }
            });
        }


    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView mProductImage;
        TextView mProductTitle, mProductStoke, mProductPrice, mProductDate;
        FloatingActionButton mAdd_cart, mAdd_fav;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            mProductImage = itemView.findViewById(R.id.item_productImage);
            mProductTitle = itemView.findViewById(R.id.item_productName);
            mProductStoke = itemView.findViewById(R.id.stoke);
            mProductPrice = itemView.findViewById(R.id.item_productPrice);
            if (admin)
                mProductDate = itemView.findViewById(R.id.Date);
            else {
                mAdd_cart = itemView.findViewById(R.id.add_to_cart);
                mAdd_fav = itemView.findViewById(R.id.add_to_fav);
            }
        }
    }

    ///////////////////////////// search filter ////////////////////////////
    @Override
    public Filter getFilter() {
        return productFilter;
    }

    private Filter productFilter=new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            //performed in BG thread
            List<Product> filteredList=new ArrayList<>();
            if(constraint==null||constraint.length()==0
                    || constraint.toString().toLowerCase().trim().contains("all")){
                filteredList.addAll(productListFull);
            }else {
                String pattern=constraint.toString().toLowerCase().trim();
                for(Product p:productListFull){
                    if(p.getmTitle().toLowerCase().contains(pattern)
                            || p.getmCategory().toLowerCase().startsWith(pattern)
                            || p.getmAdmin().getName().toLowerCase().startsWith(pattern))
                    {
                        filteredList.add(p);
                    }
                }
            }

            FilterResults results=new FilterResults();
            results.values=filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            productList.clear();
            if(results!=null&&results.values!=null)
                productList.addAll((List)results.values);
            notifyDataSetChanged();
        }
    };



///////////////////////////////////////  listener  /////////////////////////////////
    onClickListener listener;

    public void setListener(onClickListener listener) {
        this.listener = listener;
    }

    public interface onClickListener {
        void onClick(Product product);

        void onDelete(Product product);
    }

    onImageButtonClickListener onImageButtonClickListener;

    public void setOnImageButtonClickListener(onImageButtonClickListener listener) {
        this.onImageButtonClickListener = listener;
    }

    public interface onImageButtonClickListener {
        void onCartClick(Product product, ImageButton v);

        void onFavClick(Product product, ImageButton v);
    }
}
