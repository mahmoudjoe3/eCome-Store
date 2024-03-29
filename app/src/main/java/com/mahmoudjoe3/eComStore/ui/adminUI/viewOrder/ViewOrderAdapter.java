package com.mahmoudjoe3.eComStore.ui.adminUI.viewOrder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mahmoudjoe3.eComStore.R;
import com.mahmoudjoe3.eComStore.model.OrderDB;
import com.mahmoudjoe3.eComStore.model.OrderUI;
import com.mahmoudjoe3.eComStore.model.SubOrderDB;
import com.mahmoudjoe3.eComStore.model.SubOrderUI;
import com.xw.repo.BubbleSeekBar;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class ViewOrderAdapter extends RecyclerView.Adapter<ViewOrderAdapter.TrackOrderViewHolder> {
    private RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();
    private List<OrderUI> list;
    private Context context;
    private String adminName;
    private onClickListener onClickListener;

    public ViewOrderAdapter(Context context, String adminName) {
        this.list = new ArrayList<>();
        this.context = context;
        this.adminName = adminName;
    }

    public void setList(List<OrderUI> list) {

        if (list != null) {
            Collections.reverse(list);
            this.list = list;
            List<OrderUI> removedList = new ArrayList<>();
            for (OrderUI orderUI : list) {
                int ii = 0;
                for (SubOrderUI subOrderUI : orderUI.getOrderList()) {
                    if (!subOrderUI.getProduct().getmAdmin().getName().equals(adminName)) {
                        ii++;
                    }
                }
                if (ii == orderUI.getOrderList().size()) {
                    removedList.add(orderUI);
                }
            }
            list.removeAll(removedList);
            notifyDataSetChanged();
        }
    }

    @NonNull
    @Override
    public TrackOrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewOrderAdapter.TrackOrderViewHolder(LayoutInflater.from(context).inflate(R.layout.admin_item_view_order_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull TrackOrderViewHolder holder, int position) {
        OrderUI orderUI = list.get(position);
        ViewSubOrderAdapter adapter = new ViewSubOrderAdapter(context, adminName, orderUI.getOrderList());
        //init inner recycle
        LinearLayoutManager layoutManager = new LinearLayoutManager(
                holder.vo_ProductContainer.getContext(),
                LinearLayoutManager.VERTICAL,
                false
        );
        layoutManager.setInitialPrefetchItemCount(orderUI.getOrderList().size());
        holder.vo_ProductContainer.setLayoutManager(layoutManager);
        holder.vo_ProductContainer.setAdapter(adapter);
        holder.vo_ProductContainer.setRecycledViewPool(viewPool);

        holder.vo_OrderId.setText(orderUI.getId());

        //init date
        String orderDate = getDateBefore(orderUI.getDeliveryDate(), -3);
        holder.vo_OrderDate.setText(orderDate);

        if (orderUI.isApproved()) {
            holder.vo_Location.setVisibility(View.VISIBLE);
            holder.vo_approveOrder.setBackground(context.getDrawable(R.drawable.solid_button_layout_ripple_red));
            //holder.vo_approveOrder.setEnabled(false);
            holder.vo_approveOrder.setText(R.string.ORDER_APPROVED);
        }
        if (orderUI.isDelivered()) {
            holder.vo_approveOrder.setBackground(context.getDrawable(R.drawable.solid_button_layout_ripple));
            holder.vo_approveOrder.setEnabled(false);
            holder.vo_approveOrder.setText(R.string.ORDER_DELIVERED);
        }
        holder.vo_OrderExpectedDate.setText(String.format("    %s %s", context.getString(R.string.Expected_delivery), orderUI.getDeliveryDate()));

        holder.vo_TotalPrice.setText(String.format("%s EGP", adapter.getTotal()));
        holder.vo_Location.setOnClickListener(v -> {
            String lat = orderUI.getLocation().split(",")[0];
            String lng = orderUI.getLocation().split(",")[1];
            if (onClickListener != null) onClickListener.onShowLocationClick(lat, lng);
        });

        holder.vo_approveOrder.setOnClickListener(v -> {

            holder.vo_approveOrder.setBackground(context.getDrawable(R.drawable.solid_button_layout_ripple_red));
            holder.vo_approveOrder.setText(R.string.ORDER_APPROVED_long_click_to_delivery);
            holder.vo_SeekBar.setProgress(2);
            if (onClickListener != null)
                onClickListener.onApproveClick(createDBOrder(orderUI, false, true));
        });

        holder.vo_approveOrder.setOnLongClickListener(v -> {
            if (holder.vo_Location.getVisibility() == View.VISIBLE) {
                if (onClickListener != null) {
                    new AlertDialog.Builder(context)
                            .setMessage(R.string.You_Want_To_Delivery_This_Order)
                            .setTitle(R.string.Order_Delivery)
                            .setNegativeButton(R.string.Delivery, (dialog, which) -> {
                                //TODO you can cheek the distance between the delivery boy and the customer
                                holder.vo_approveOrder.setText(R.string.ORDER_DELIVERED);
                                holder.vo_SeekBar.setProgress(3);
                                holder.vo_approveOrder.setEnabled(false);
                                holder.vo_approveOrder.setBackground(context.getDrawable(R.drawable.solid_button_layout_ripple));
                                onClickListener.onDeliverClick(createDBOrder(orderUI, true, true));
                            })
                            .setPositiveButton(R.string.No, null)
                            .create().show();

                }
            }
            return false;
        });

        //init SeekBar
        holder.vo_SeekBar.setCustomSectionTextArray((sectionCount, array) -> {
            array.clear();
            array.put(0, context.getString(R.string.InProcessing));
            array.put(1, context.getString(R.string.Shipped));
            array.put(2, context.getString(R.string.Delivered));
            return array;
        });

        if (orderUI.isDelivered())
            holder.vo_SeekBar.setProgress(3);
        else if (orderUI.isApproved())
            holder.vo_SeekBar.setProgress(2);
        else
            holder.vo_SeekBar.setProgress(1);

    }

    private OrderDB createDBOrder(OrderUI order, boolean isDelivered, boolean isApproved) {
        List<SubOrderDB> orderDBList = new ArrayList<>();
        for (SubOrderUI s : order.getOrderList()) {
            orderDBList.add(new SubOrderDB(s.getProduct().getmId(), s.getQty()));
        }
        OrderDB orderDB = new OrderDB(orderDBList, order.getPhone(), order.getTotalPrice(), order.getLocation()
                , order.getDeliveryDate(), isDelivered, isApproved);
        orderDB.setId(order.getId());
        return orderDB;
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

    public void setOnClickListener(ViewOrderAdapter.onClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    interface onClickListener {
        void onShowLocationClick(String Lat, String Long);

        void onApproveClick(OrderDB dbOrder);

        void onDeliverClick(OrderDB dbOrder);
    }

    public class TrackOrderViewHolder extends RecyclerView.ViewHolder {
        TextView vo_OrderId;
        TextView vo_OrderDate;
        TextView vo_OrderExpectedDate;
        TextView vo_Location;
        TextView vo_TotalPrice;
        RecyclerView vo_ProductContainer;
        BubbleSeekBar vo_SeekBar;
        Button vo_approveOrder;

        public TrackOrderViewHolder(@NonNull View itemView) {
            super(itemView);
            vo_OrderId = itemView.findViewById(R.id.vo_orderId);
            vo_OrderDate = itemView.findViewById(R.id.vo_orderDate);
            vo_OrderExpectedDate = itemView.findViewById(R.id.vo_expectedDate);
            vo_Location = itemView.findViewById(R.id.vo_location);
            vo_TotalPrice = itemView.findViewById(R.id.vo_totalPrice);
            vo_ProductContainer = itemView.findViewById(R.id.vo_productContainer);
            vo_SeekBar = itemView.findViewById(R.id.vo_seekBar);
            vo_approveOrder = itemView.findViewById(R.id.vo_approveOrder_btn);
        }
    }
}
