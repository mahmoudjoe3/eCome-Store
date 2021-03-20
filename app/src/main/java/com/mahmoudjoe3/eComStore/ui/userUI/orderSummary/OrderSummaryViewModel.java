package com.mahmoudjoe3.eComStore.ui.userUI.orderSummary;

import androidx.lifecycle.ViewModel;

import com.mahmoudjoe3.eComStore.model.OrderDB;
import com.mahmoudjoe3.eComStore.repo.FirebaseRepo;

public class OrderSummaryViewModel extends ViewModel {
    private FirebaseRepo repo;
    private FirebaseRepo.onOrderAddedListener onOrderAddedListener;

    public OrderSummaryViewModel() {
        this.repo = FirebaseRepo.getInstance();
    }

    public void insertOrder(OrderDB orderDB) {
        repo.insertOrder(orderDB);
        repo.setOnOrderAddedListener(new FirebaseRepo.onOrderAddedListener() {
            @Override
            public void onSuccess() {
                if (onOrderAddedListener != null) onOrderAddedListener.onSuccess();
            }

            @Override
            public void onFailure() {
                if (onOrderAddedListener != null) onOrderAddedListener.onFailure();
            }
        });
    }

    public void setOnOrderAddedListener(FirebaseRepo.onOrderAddedListener onOrderAddedListener) {
        this.onOrderAddedListener = onOrderAddedListener;
    }

}
