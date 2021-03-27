package com.mahmoudjoe3.eComStore.ui.userUI.trackOrder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mahmoudjoe3.eComStore.R;
import com.mahmoudjoe3.eComStore.model.OrderUI;
import com.mahmoudjoe3.eComStore.model.SubOrderUI;
import com.mahmoudjoe3.eComStore.ui.userUI.orderSummary.OrderSummaryAdapter;
import com.xw.repo.BubbleSeekBar;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class TrackOrdersAdapter extends RecyclerView.Adapter<TrackOrdersAdapter.TrackOrderViewHolder> implements Filterable {
    private RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();
    private List<OrderUI> list;
    private List<OrderUI> listFull;
    private Context context;
    private onShowLocationListener onShowLocationListener;
    private Filter productFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            //performed in BG thread
            List<OrderUI> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0
                    || constraint.toString().toLowerCase().trim().contains("all")
                    || constraint.toString().toLowerCase().trim().contains("كل")) {
                filteredList.addAll(listFull);
            } else {
                String pattern = constraint.toString().toLowerCase().trim();
                for (OrderUI o : listFull) {
                    if (o.getDeliveryDate().toLowerCase().contains(pattern)
                            || o.getId().toLowerCase().startsWith(pattern)
                            || checkproducts(o, pattern)) {
                        filteredList.add(o);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            list.clear();
            if (results != null && results.values != null)
                list.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

    public TrackOrdersAdapter(Context context) {
        this.list = new ArrayList<>();
        this.context = context;
    }

    public void setList(List<OrderUI> list) {
        if (list != null) {
            Collections.reverse(list);
            this.list = list;
            listFull = new ArrayList<>(list);
        }
    }

    @NonNull
    @Override
    public TrackOrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TrackOrdersAdapter.TrackOrderViewHolder(LayoutInflater.from(context).inflate(R.layout.user_item_order_receipt_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull TrackOrderViewHolder holder, int position) {
        OrderUI orderUI = list.get(position);
        //init inner recycle
        LinearLayoutManager layoutManager = new LinearLayoutManager(
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
        String orderDate = getDateBefore(orderUI.getDeliveryDate(), -3);
        holder.rOrderDate.setText(orderDate);

        holder.rOrderExpectedDate.setText("    "+context.getString(R.string.Expected_delivery)+" " + orderUI.getDeliveryDate());
        holder.rTotalPrice.setText(orderUI.getTotalPrice() + " EGP");
        holder.rLocation.setOnClickListener(v -> {
            String lat = orderUI.getLocation().split(",")[0];
            String lng = orderUI.getLocation().split(",")[1];
            if (onShowLocationListener != null) onShowLocationListener.onClick(lat, lng);
        });

        //init SeekBar
        holder.rSeekBar.setCustomSectionTextArray((sectionCount, array) -> {
            array.clear();
            array.put(0, context.getString(R.string.InProcessing));
            array.put(1, context.getString(R.string.Shipped));
            array.put(2, context.getString(R.string.Delivered));
            return array;
        });

        if (orderUI.isDelivered())
            holder.rSeekBar.setProgress(3);
        else if (orderUI.isApproved())
            holder.rSeekBar.setProgress(2);
        else
            holder.rSeekBar.setProgress(1);
    }

    private String getDateBefore(String deliveryDate, int x) {
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

    public void setOnShowLocationListener(TrackOrdersAdapter.onShowLocationListener onShowLocationListener) {
        this.onShowLocationListener = onShowLocationListener;
    }

    //new
    ///////////////////////////// search filter ////////////////////////////
    @Override
    public Filter getFilter() {
        return productFilter;
    }

    private boolean checkproducts(OrderUI o, String pattern) {
        for (SubOrderUI s : o.getOrderList()) {
            if (s.getProduct().getmCategory().toLowerCase().startsWith(pattern)
                    || s.getProduct().getmAdmin().getName().toLowerCase().startsWith(pattern)
                    || s.getProduct().getmDescription().toLowerCase().startsWith(pattern)
                    || s.getProduct().getmTitle().toLowerCase().startsWith(pattern)) {
                return true;
            }
        }
        return false;
    }

    interface onShowLocationListener {
        void onClick(String Lat, String Long);
    }

    public class TrackOrderViewHolder extends RecyclerView.ViewHolder {
        TextView rOrderId;
        TextView rOrderDate;
        TextView rOrderExpectedDate;
        TextView rLocation;
        TextView rTotalPrice;
        RecyclerView rProductContainer;
        BubbleSeekBar rSeekBar;

        public TrackOrderViewHolder(@NonNull View itemView) {
            super(itemView);
            rOrderId = itemView.findViewById(R.id.r_orderId);
            rOrderDate = itemView.findViewById(R.id.r_orderDate);
            rOrderExpectedDate = itemView.findViewById(R.id.r_expectedDate);
            rLocation = itemView.findViewById(R.id.r_location);
            rTotalPrice = itemView.findViewById(R.id.r_totalPrice);
            rProductContainer = itemView.findViewById(R.id.r_productContainer);
            rSeekBar = itemView.findViewById(R.id.r_seekBar);
        }
    }

}
