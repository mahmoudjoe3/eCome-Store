package com.mahmoudjoe3.eComStore.ui.adminUI.viewOrder;

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

public class ViewSubOrderAdapter extends RecyclerView.Adapter<ViewSubOrderAdapter.ViewOrderViewHolder> {
    private List<SubOrderUI> list;
    private Context context;
    private String admin;
    private Float Total;
    public ViewSubOrderAdapter(Context context,String admin,List<SubOrderUI> list) {
        this.list = new ArrayList<>();
        this.context = context;
        this.admin=admin;
        Total=0f;
        this.list=getListByAdminName(list,admin);
    }

    private List<SubOrderUI> getListByAdminName(List<SubOrderUI> list, String admin) {
        List<SubOrderUI> list1=new ArrayList<>();
        for(SubOrderUI subOrder:list){
            if(subOrder.getProduct().getmAdmin().getName().equalsIgnoreCase(admin)){
                Total+=(subOrder.getProduct().getmPrice()*subOrder.getQty());
                list1.add(subOrder);
            }
        }
        return list1;
    }

    public Float getTotal() {
        return Total;
    }

    public List<SubOrderUI> getList() {
        return list;
    }

    @NonNull
    @Override
    public ViewOrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewSubOrderAdapter.ViewOrderViewHolder(LayoutInflater.from(context).inflate(R.layout.user_item_summary_layout,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewOrderViewHolder holder, int position) {
        SubOrderUI subOrder = list.get(position);
        Product product = subOrder.getProduct();
        holder.pTitle.setText(product.getmTitle());
        holder.pOwner.setText(product.getmAdmin().getName());
        holder.pQty.setText("Qty:"+subOrder.getQty());
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

    public class ViewOrderViewHolder extends RecyclerView.ViewHolder{
        ImageView pImage;
        TextView pTitle;
        TextView pOwner;
        TextView pQty;
        TextView pPrice;
        public ViewOrderViewHolder(@NonNull View itemView) {
            super(itemView);
            pImage=itemView.findViewById(R.id.s_image);
            pTitle=itemView.findViewById(R.id.s_title);
            pOwner=itemView.findViewById(R.id.s_owner);
            pQty=itemView.findViewById(R.id.s_Qty);
            pPrice=itemView.findViewById(R.id.s_price);
        }
    }
}
