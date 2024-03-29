package com.mahmoudjoe3.eComStore.viewModel;


import androidx.lifecycle.ViewModel;

import com.mahmoudjoe3.eComStore.repo.FirebaseAuthRepo;

public class FirebaseAuthViewModel extends ViewModel {

    private FirebaseAuthRepo repo;
    private FirebaseAuthRepo.OnLoginListener mOnLoginListener;
    private FirebaseAuthRepo.OnRegisterListener mOnRegisterListener;
    private FirebaseAuthRepo.OnVersionListener mOnVersionListener;

    public FirebaseAuthViewModel() {
        repo = FirebaseAuthRepo.getInstance();
    }

    public void checkVersionName(String ver) {
        repo.checkVersionName(ver);
        repo.setOnVersionListener(new FirebaseAuthRepo.OnVersionListener() {
            @Override
            public void onRealVersion() {
                mOnVersionListener.onRealVersion();
            }

            @Override
            public void onOldVersion(String NewVersion) {
                mOnVersionListener.onOldVersion(NewVersion);
            }
        });
    }

    public void login(boolean isAdmin, String phone, String password, boolean rememberMe) {
        repo.login(isAdmin, phone, password, rememberMe);
        repo.setOnLoginListener(new FirebaseAuthRepo.OnLoginListener() {
            @Override
            public void onLogeInSuccess(Object user) {
                if (mOnLoginListener != null) mOnLoginListener.onLogeInSuccess(user);
            }

            @Override
            public void onLogeInDenied(String errorMsg) {
                if (mOnLoginListener != null) mOnLoginListener.onLogeInDenied(errorMsg);
            }

            @Override
            public void onRemember(Object user) {
                if (mOnLoginListener != null) mOnLoginListener.onRemember(user);
            }
        });
    }
    public void isUser(String phone, FirebaseAuthRepo.OnUserValidationListener validationListener){
        repo.IsUser(phone);
        repo.setOnUserValidationListener(validationListener);
    }

    public void setOnLoginListener(FirebaseAuthRepo.OnLoginListener onLoginListener) {
        mOnLoginListener = onLoginListener;
    }

    public void registerUser(String name, String phone, String password, String date) {
        repo.RegisterUser(name, phone, password, date);
        repo.setOnRegisterListener(new FirebaseAuthRepo.OnRegisterListener() {
            @Override
            public void onRegisterSuccess() {
                if (mOnRegisterListener != null) mOnRegisterListener.onRegisterSuccess();
            }

            @Override
            public void onRegisterExist() {
                if (mOnRegisterListener != null) mOnRegisterListener.onRegisterExist();
            }

            @Override
            public void onRegisterFailure() {
                if (mOnRegisterListener != null) mOnRegisterListener.onRegisterFailure();
            }
        });
    }

    public void setmOnVersionListener(FirebaseAuthRepo.OnVersionListener mOnVersionListener) {
        this.mOnVersionListener = mOnVersionListener;
    }

    public void setOnRegisterListener(FirebaseAuthRepo.OnRegisterListener OnRegisterListener) {
        mOnRegisterListener = OnRegisterListener;
    }

    public void forgetPassword(boolean isAdmin, String phone, String newPassword) {
        repo.forgetPassword(isAdmin, phone, newPassword);
    }
}
