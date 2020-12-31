package com.mahmoudjoe3.eComStore.ui.adminUI.viewOrder;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.util.SparseArray;
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
import com.mahmoudjoe3.eComStore.ui.userUI.orderSummary.OrderSummaryActivity;
import com.xw.repo.BubbleSeekBar;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ViewOrderAdapter extends RecyclerView.Adapter<ViewOrderAdapter.TrackOrderViewHolder> {
    private static final String TAG = "ViewOrderAdapter";
    private RecyclerView.RecycledViewPool viewPool=new RecyclerView.RecycledViewPool();
    private List<OrderUI> list;
    private Context context;
    private String adminName;

    public ViewOrderAdapter(Context context,String adminName) {
        this.list = new ArrayList<>();
        this.context = context;
        this.adminName=adminName;
    }

    public void setList(List<OrderUI> list) {
        if(list!=null)
            this.list = list;
       List<OrderUI> removedList=new ArrayList<>();
        for(OrderUI orderUI:list){
            int ii=0;
            for (SubOrderUI subOrderUI:orderUI.getOrderList()){
                if(!subOrderUI.getProduct().getmAdmin().getName().equals(adminName)){
                    ii++;
                }
            }
            if(ii==orderUI.getOrderList().size()){
                removedList.add(orderUI);
            }
        }
        list.removeAll(removedList);
        Log.d(TAG, "setList: list size-->"+list.size());
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TrackOrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewOrderAdapter.TrackOrderViewHolder(LayoutInflater.from(context).inflate(R.layout.admin_item_view_order_layout,parent,false));
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
                holder.vo_approveOrder.setText("ORDER APPROVED");
            }
            if (orderUI.isDelivered()) {
                holder.vo_approveOrder.setBackground(context.getDrawable(R.drawable.solid_button_layout_ripple));
                holder.vo_approveOrder.setEnabled(false);
                holder.vo_approveOrder.setText("ORDER DELIVERED");
            }
            holder.vo_OrderExpectedDate.setText("    Expected delivery " + orderUI.getDeliveryDate());

            holder.vo_TotalPrice.setText(adapter.getTotal() + " EGP");
            holder.vo_Location.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String lat = orderUI.getLocation().split(",")[0];
                    String lng = orderUI.getLocation().split(",")[1];
                    if (onClickListener != null) onClickListener.onShowLocationClick(lat, lng);
                }
            });

            holder.vo_approveOrder.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("UseCompatLoadingForDrawables")
                @Override
                public void onClick(View v) {
                    holder.vo_Location.setVisibility(View.VISIBLE);
                    holder.vo_approveOrder.setBackground(context.getDrawable(R.drawable.solid_button_layout_ripple_red));
                    holder.vo_approveOrder.setText("ORDER APPROVED long click to delivery");
                    holder.vo_SeekBar.setProgress(2);
                    if (onClickListener != null)
                        onClickListener.onApproveClick(createDBOrder(orderUI, false, true));
                }
            });

            holder.vo_approveOrder.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (holder.vo_Location.getVisibility() == View.VISIBLE) {
                        if (onClickListener != null) {
                            new AlertDialog.Builder(context)
                                    .setMessage("You Want To Delivery This Order ?")
                                    .setTitle("Order Delivery")
                                    .setNegativeButton("Delivery", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            //TODO you can cheek the distance between the delivery boy and the customer
                                            holder.vo_approveOrder.setText("ORDER DELIVERED");
                                            holder.vo_SeekBar.setProgress(3);
                                            holder.vo_approveOrder.setEnabled(false);
                                            holder.vo_approveOrder.setBackground(context.getDrawable(R.drawable.solid_button_layout_ripple));
                                            onClickListener.onDeliverClick(createDBOrder(orderUI, true, true));
                                        }
                                    })
                                    .setPositiveButton("No", null)
                                    .create().show();

                        }
                    }
                    return false;
                }
            });

            //init SeekBar
            holder.vo_SeekBar.setCustomSectionTextArray(new BubbleSeekBar.CustomSectionTextArray() {
                @NonNull
                @Override
                public SparseArray<String> onCustomize(int sectionCount, @NonNull SparseArray<String> array) {
                    array.clear();
                    array.put(0, "InProcessing");
                    array.put(1, "Shipped");
                    array.put(2, "Delivered");
                    return array;
                }
            });

            if (orderUI.isDelivered())
                holder.vo_SeekBar.setProgress(3);
            else if (orderUI.isApproved())
                holder.vo_SeekBar.setProgress(2);
            else
                holder.vo_SeekBar.setProgress(1);

    }


    private OrderDB createDBOrder(OrderUI order,boolean isDelivered,boolean isApproved) {
        List<SubOrderDB> orderDBList=new ArrayList<>();
        for(SubOrderUI s:order.getOrderList()){
            orderDBList.add(new SubOrderDB(s.getProduct().getmId(),s.getQty()));
        }
        OrderDB orderDB=new OrderDB(orderDBList,order.getPhone(),order.getTotalPrice(),order.getLocation()
                ,order.getDeliveryDate(),isDelivered,isApproved);
        orderDB.setId(order.getId());
        return orderDB;
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
            vo_OrderId =itemView.findViewById(R.id.vo_orderId);
            vo_OrderDate =itemView.findViewById(R.id.vo_orderDate);
            vo_OrderExpectedDate =itemView.findViewById(R.id.vo_expectedDate);
            vo_Location =itemView.findViewById(R.id.vo_location);
            vo_TotalPrice =itemView.findViewById(R.id.vo_totalPrice);
            vo_ProductContainer =itemView.findViewById(R.id.vo_productContainer);
            vo_SeekBar =itemView.findViewById(R.id.vo_seekBar);
            vo_approveOrder=itemView.findViewById(R.id.vo_approveOrder_btn);
        }
    }

    private onClickListener onClickListener;

    public void setOnClickListener(ViewOrderAdapter.onClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    interface onClickListener{
        void onShowLocationClick(String Lat, String Long);
        void onApproveClick(OrderDB dbOrder);
        void onDeliverClick(OrderDB dbOrder);
    }
}
