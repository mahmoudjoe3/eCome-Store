package com.mahmoudjoe3.eComStore.ui.userUI.trackOrder;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mahmoudjoe3.eComStore.R;
import com.mahmoudjoe3.eComStore.model.OrderUI;
import com.mahmoudjoe3.eComStore.model.Product;
import com.mahmoudjoe3.eComStore.model.SubOrderUI;
import com.mahmoudjoe3.eComStore.ui.userUI.orderSummary.OrderSummaryAdapter;
import com.squareup.picasso.Picasso;
import com.xw.repo.BubbleSeekBar;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class TrackOrdersAdapter extends RecyclerView.Adapter<TrackOrdersAdapter.TrackOrderViewHolder> {
    private RecyclerView.RecycledViewPool viewPool=new RecyclerView.RecycledViewPool();
    private List<OrderUI> list;
    private Context context;

    public TrackOrdersAdapter(Context context) {
        this.list = new ArrayList<>();
        this.context = context;
    }

    public void setList(List<OrderUI> list) {
        if(list!=null)
            this.list = list;
    }

    @NonNull
    @Override
    public TrackOrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TrackOrdersAdapter.TrackOrderViewHolder(LayoutInflater.from(context).inflate(R.layout.user_item_order_receipt_layout,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull TrackOrderViewHolder holder, int position) {
        OrderUI orderUI = list.get(position);
        //init inner recycle
        LinearLayoutManager layoutManager=new LinearLayoutManager(
          holder.rProductContainer.getContext(),
          LinearLayoutManager.VERTICAL,
          false
        );
        layoutManager.setInitialPrefetchItemCount(orderUI.getOrderList().size());

        OrderSummaryAdapter adapter = new OrderSummaryAdapter(context);
        adapter.setList(orderUI.getOrderList());

        holder.rProductContainer.setLayoutManager(layoutManager);
        holder.rProductContainer.setAdapter(adapter);
        holder.rProductContainer.setRecycledViewPool(viewPool);

        holder.rOrderId.setText(orderUI.getId());

        //init date
        String orderDate=getDateBefore(orderUI.getDeliveryDate(),-3);
        holder.rOrderDate.setText(orderDate);

        holder.rOrderExpectedDate.setText("    Expected delivery "+orderUI.getDeliveryDate());
        holder.rTotalPrice.setText(orderUI.getTotalPrice()+" EGP");
        holder.rLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String lat=orderUI.getLocation().split(",")[0];
                String lng=orderUI.getLocation().split(",")[1];
                if(onShowLocationListener!=null)onShowLocationListener.onClick(lat,lng);
            }
        });

        //init SeekBar
        holder.rSeekBar.setCustomSectionTextArray(new BubbleSeekBar.CustomSectionTextArray() {
            @NonNull
            @Override
            public SparseArray<String> onCustomize(int sectionCount, @NonNull SparseArray<String> array) {
                array.clear();
                array.put(0,"InProcessing");
                array.put(1,"Shipped");
                array.put(2,"Delivered");
                return array;
            }
        });

        if(orderUI.isDelivered())
            holder.rSeekBar.setProgress(3);
        else if(orderUI.isApproved())
            holder.rSeekBar.setProgress(2);
        else
            holder.rSeekBar.setProgress(1);
    }



    private String getDateBefore(String deliveryDate,int x) {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat DateFormat = new SimpleDateFormat("MMM dd, yyyy");
        try {
            calendar.setTime(DateFormat.parse(deliveryDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        calendar.add(Calendar.DATE, x);  // number of days to add
        return DateFormat.format(calendar.getTime());  // dt is now the new date
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public class TrackOrderViewHolder extends RecyclerView.ViewHolder{
        TextView rOrderId;
        TextView rOrderDate;
        TextView rOrderExpectedDate;
        TextView rLocation;
        TextView rTotalPrice;
        RecyclerView rProductContainer;
        BubbleSeekBar rSeekBar;
        public TrackOrderViewHolder(@NonNull View itemView) {
            super(itemView);
            rOrderId=itemView.findViewById(R.id.r_orderId);
            rOrderDate=itemView.findViewById(R.id.r_orderDate);
            rOrderExpectedDate=itemView.findViewById(R.id.r_expectedDate);
            rLocation=itemView.findViewById(R.id.r_location);
            rTotalPrice=itemView.findViewById(R.id.r_totalPrice);
            rProductContainer=itemView.findViewById(R.id.r_productContainer);
            rSeekBar=itemView.findViewById(R.id.r_seekBar);
        }
    }

    private onShowLocationListener onShowLocationListener;

    public void setOnShowLocationListener(TrackOrdersAdapter.onShowLocationListener onShowLocationListener) {
        this.onShowLocationListener = onShowLocationListener;
    }

    interface onShowLocationListener{
        void onClick(String Lat,String Long);
    }
}
