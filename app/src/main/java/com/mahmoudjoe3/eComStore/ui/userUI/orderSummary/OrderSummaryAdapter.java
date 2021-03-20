package com.mahmoudjoe3.eComStore.ui.userUI.orderSummary;

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
import com.mahmoudjoe3.eComStore.model.SubOrderUI;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class OrderSummaryAdapter extends RecyclerView.Adapter<OrderSummaryAdapter.OrderSummaryViewHolder> {
    private List<SubOrderUI> list;
    private Context context;

    public OrderSummaryAdapter(Context context) {
        this.list = new ArrayList<>();
        this.context = context;
    }

    public void setList(List<SubOrderUI> list) {
        if (list != null)
            this.list = list;
    }

    @NonNull
    @Override
    public OrderSummaryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new OrderSummaryAdapter.OrderSummaryViewHolder(LayoutInflater.from(context).inflate(R.layout.user_item_summary_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull OrderSummaryViewHolder holder, int position) {
        SubOrderUI subOrder = list.get(position);
        Product product = subOrder.getProduct();
        holder.pTitle.setText(product.getmTitle());
        holder.pOwner.setText(product.getmAdmin().getName());
        holder.pQty.setText("Qty:" + subOrder.getQty());
        Picasso.get()
                .load(product.getmImageUri().get(0))
                .fit()
                .centerCrop().into(holder.pImage);

        holder.pPrice.setText(product.getmPrice() + " EGP");
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class OrderSummaryViewHolder extends RecyclerView.ViewHolder {
        ImageView pImage;
        TextView pTitle;
        TextView pOwner;
        TextView pQty;
        TextView pPrice;

        public OrderSummaryViewHolder(@NonNull View itemView) {
            super(itemView);
            pImage = itemView.findViewById(R.id.s_image);
            pTitle = itemView.findViewById(R.id.s_title);
            pOwner = itemView.findViewById(R.id.s_owner);
            pQty = itemView.findViewById(R.id.s_Qty);
            pPrice = itemView.findViewById(R.id.s_price);
        }
    }
}
