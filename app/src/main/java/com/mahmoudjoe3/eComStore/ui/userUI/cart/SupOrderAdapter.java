package com.mahmoudjoe3.eComStore.ui.userUI.cart;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mahmoudjoe3.eComStore.R;
import com.mahmoudjoe3.eComStore.model.Product;
import com.mahmoudjoe3.eComStore.model.SubOrderUI;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class SupOrderAdapter extends RecyclerView.Adapter<SupOrderAdapter.SubOrderViewHolder> {
    orderOnClickListener mOrderOnClickListener;
    private List<SubOrderUI> list;
    private Context context;
    private MutableLiveData<Float> totalPriceLiveData;


    public SupOrderAdapter(Context context) {
        this.list = new ArrayList<>();
        this.context = context;
        totalPriceLiveData = new MutableLiveData<>((float) 0);
    }

    public List<SubOrderUI> getList() {
        return list;
    }

    public void setList(List<SubOrderUI> list) {
        this.list = list;
        float t = 0;
        for (SubOrderUI s : list) {
            t += s.getProduct().getmPrice();
        }
        totalPriceLiveData.setValue(t);
        notifyDataSetChanged();
    }

    public LiveData<Float> getTotalPriceLiveData() {
        return totalPriceLiveData;
    }

    @NonNull
    @Override
    public SubOrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SubOrderViewHolder(LayoutInflater.from(context).inflate(R.layout.user_item_order_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SubOrderViewHolder holder, int position) {
        SubOrderUI subOrder = list.get(position);
        Product product = subOrder.getProduct();
        holder.pTitle.setText(product.getmTitle());
        holder.pOwner.setText(product.getmAdmin().getName());
        holder.pQty.setText(String.valueOf(subOrder.getQty()));
        Picasso.get()
                .load(product.getmImageUri().get(0))
                .fit()
                .centerCrop().into(holder.pImage);

        holder.pPrice.setText(product.getmPrice() + " EGP");
        if (product.getmPrice() >= 350)
            holder.pIsFreeShip.setVisibility(View.GONE);
        else
            holder.pIsFreeShip.setVisibility(View.VISIBLE);


        holder.pIncrease.setOnClickListener(v -> {
            int last = Integer.parseInt(holder.pQty.getText().toString());
            if (product.getQuantity() > last) {
                holder.pQty.setText((last + 1) + "");
                subOrder.setQty(last + 1);
                if (((last + 1) * product.getmPrice()) >= 350)
                    holder.pIsFreeShip.setVisibility(View.GONE);
                totalPriceLiveData.setValue(totalPriceLiveData.getValue() + product.getmPrice());
            }

        });

        holder.pDecrease.setOnClickListener(v -> {
            int last = Integer.parseInt(holder.pQty.getText().toString());
            if (last != 1) {
                holder.pQty.setText((last - 1) + "");
                subOrder.setQty(last - 1);
                if (((last - 1) * product.getmPrice()) < 350)
                    holder.pIsFreeShip.setVisibility(View.VISIBLE);
                totalPriceLiveData.setValue(totalPriceLiveData.getValue() - product.getmPrice());
            }
        });

        holder.itemView.setOnClickListener(v -> {
            if (mOrderOnClickListener != null)
                mOrderOnClickListener.onClick(product);
        });

        holder.itemView.setOnLongClickListener(v -> {
            if (mOrderOnClickListener != null) {
                mOrderOnClickListener.onDelete(product);
            }
            return false;
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setmOrderOnClickListener(orderOnClickListener mOrderOnClickListener) {
        this.mOrderOnClickListener = mOrderOnClickListener;
    }

    interface orderOnClickListener {
        void onClick(Product product);

        void onDelete(Product product);
    }

    class SubOrderViewHolder extends RecyclerView.ViewHolder {
        ImageView pImage;
        TextView pTitle;
        TextView pOwner;
        FloatingActionButton pIncrease;
        TextView pQty;
        FloatingActionButton pDecrease;
        TextView pPrice;
        TextView pIsFreeShip;

        public SubOrderViewHolder(@NonNull View itemView) {
            super(itemView);
            pImage = itemView.findViewById(R.id.o_image);
            pTitle = itemView.findViewById(R.id.o_title);
            pOwner = itemView.findViewById(R.id.o_owner);
            pIncrease = itemView.findViewById(R.id.o_increaseQty);
            pQty = itemView.findViewById(R.id.o_Qty);
            pDecrease = itemView.findViewById(R.id.o_decreaseQty);
            pPrice = itemView.findViewById(R.id.o_price);
            pIsFreeShip = itemView.findViewById(R.id.notFreeShip);
        }
    }
}
