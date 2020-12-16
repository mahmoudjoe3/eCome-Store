package com.mahmoudjoe3.eComStore.repo;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mahmoudjoe3.eComStore.model.User;
import com.mahmoudjoe3.eComStore.prevalent.Prevalent;
import com.mahmoudjoe3.eComStore.ui.main.LoginActivity;
import com.mahmoudjoe3.eComStore.ui.main.MainActivity;
import com.mahmoudjoe3.eComStore.ui.main.RegisterActivity;
import com.mahmoudjoe3.eComStore.ui.adminUI.AdminHomeActivity;
import com.mahmoudjoe3.eComStore.ui.userUI.UserHomeActivity;



public class FirebaseAuthRepo {

    private OnLoginListener mOnLoginListener;
    public void setOnLoginListener(OnLoginListener onLoginListener) {
        mOnLoginListener = onLoginListener;
    }
    public interface OnLoginListener {
        void onLogeInSuccess(User user);
        void onLogeInDenied(String errorMsg);
        void onRemember(User user);
    }

    private OnRegisterListener mOnRegisterListener;
    public void setOnRegisterListener(OnRegisterListener OnRegisterListener) {
        mOnRegisterListener = OnRegisterListener;
    }
    public interface OnRegisterListener {
        void onRegisterSuccess();
        void onRegisterExist();
        void onRegisterFailure();
    }


    static FirebaseAuthRepo instance;

    static DatabaseReference mReference;

    public static synchronized FirebaseAuthRepo getInstance() {
        if (instance == null) {
            instance = new FirebaseAuthRepo();
        }
        mReference = FirebaseDatabase.getInstance().getReference();
        return instance;
    }

    public void RegisterUser(String name, String phone, String password) {

        mReference = FirebaseDatabase.getInstance().getReference(Prevalent.refColName_User);

        mReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(phone).exists()) { //phone number is exist in database
                    if(mOnRegisterListener!=null){
                        mOnRegisterListener.onRegisterExist();
                    }
                }
                else {//safe to insert new user
                    insertNewUser(name, phone, password);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    private void insertNewUser(String name, String phone, String password) {
        User user=new User(name,phone,password);
        mReference.child(phone).setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                if(mOnRegisterListener!=null){
                    mOnRegisterListener.onRegisterSuccess();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if(mOnRegisterListener!=null){
                    mOnRegisterListener.onRegisterFailure();
                }
            }
        });

    }



    public void login(boolean admin, String phone, String password, boolean rememberMe) {

        String refCollectionName = (admin) ? Prevalent.refColName_Admin : Prevalent.refColName_User;

        mReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(refCollectionName).child(phone).exists()) { //access done
                    User user;
                    user = snapshot.child(refCollectionName).child(phone).getValue(User.class);
                    if (user.getPhone().equals(phone)) {
                        if (user.getPassword().equals(password)) {
                            //pref
                            if (rememberMe) {
                                if (mOnLoginListener != null) {
                                    mOnLoginListener.onRemember(user);
                                }
                            }
                            if (mOnLoginListener != null) {
                                mOnLoginListener.onLogeInSuccess(user);
                            }
                        } else {
                            if (mOnLoginListener != null) {
                                mOnLoginListener.onLogeInDenied("password incorrect");
                            }
                        }
                    }
                } else {  //access denied
                    if (mOnLoginListener != null) {
                        mOnLoginListener.onLogeInDenied("Login Failed, You should have account");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}


