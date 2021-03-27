package com.mahmoudjoe3.eComStore.ui.adminUI.viewOrder;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.mahmoudjoe3.eComStore.model.OrderDB;
import com.mahmoudjoe3.eComStore.model.OrderUI;
import com.mahmoudjoe3.eComStore.model.Product;
import com.mahmoudjoe3.eComStore.model.SubOrderDB;
import com.mahmoudjoe3.eComStore.model.SubOrderUI;
import com.mahmoudjoe3.eComStore.repo.FirebaseRepo;

import java.util.ArrayList;
import java.util.List;

public class ViewOrderViewModel extends ViewModel {

    private FirebaseRepo repo;
    private MutableLiveData<List<OrderUI>> orderList;

    public ViewOrderViewModel() {
        orderList = new MutableLiveData<>();
        repo = FirebaseRepo.getInstance();
    }


    public LiveData<List<OrderUI>> getOrderList() {
        repo.fitchOrders(null);
        repo.setonOrderRetrievedListener(orderDBList -> {
            List<OrderUI> orderUIList = new ArrayList<>();
            rec(0, orderDBList, orderUIList);
        });
        return orderList;
    }

    void rec(int i, List<OrderDB> orderDBList, List<OrderUI> orderUIList) {
        if (i >= orderDBList.size()) {
            orderList.setValue(orderUIList);
            return;
        }
        List<String> productsIds = new ArrayList<>();
        for (SubOrderDB subOrderDB : orderDBList.get(i).getOrderList()) {
            productsIds.add(subOrderDB.getProduct_Key());
        }
        repo.getProductListByIds(productsIds);
        repo.setOnGetProductListener(products -> {
            List<SubOrderUI> subOrderUiList = new ArrayList<>();
            int j = 0;
            for (Product p : products) {
                subOrderUiList.add(new SubOrderUI(p, orderDBList.get(i).getOrderList().get(j).getQty()));
                j++;
            }
            orderUIList.add(new OrderUI(subOrderUiList
                    , orderDBList.get(i).getId()
                    , orderDBList.get(i).getPhone()
                    , orderDBList.get(i).getTotalPrice()
                    , orderDBList.get(i).getLocation()
                    , orderDBList.get(i).getDeliveryDate()
                    , orderDBList.get(i).isDelivered()
                    , orderDBList.get(i).isApproved()));
            rec(i + 1, orderDBList, orderUIList);
        });
    }

    public void approveOn(OrderDB dbOrder) {
        repo.approveOn(dbOrder);
    }

    public void deliver(OrderDB dbOrder) {
        repo.deliver(dbOrder);
    }
}