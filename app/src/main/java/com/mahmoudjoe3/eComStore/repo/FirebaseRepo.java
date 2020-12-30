package com.mahmoudjoe3.eComStore.repo;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mahmoudjoe3.eComStore.model.AuthorizedUser;
import com.mahmoudjoe3.eComStore.model.OrderDB;
import com.mahmoudjoe3.eComStore.model.Product;
import com.mahmoudjoe3.eComStore.model.SubOrderDB;
import com.mahmoudjoe3.eComStore.prevalent.Prevalent;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class FirebaseRepo {
    private static final String TAG = "FirebaseRepo";
    static FirebaseRepo instance;

    static StorageReference mProductImageReference;
    static DatabaseReference mReference;

    ArrayList<String>url = new ArrayList<>();
    Product mProduct;
    public static synchronized FirebaseRepo getInstance(){
        if(instance==null)
        {
            instance=new FirebaseRepo();
        }
        mProductImageReference= FirebaseStorage.getInstance().getReference(Prevalent.refStorage_productImage);
        mReference = FirebaseDatabase.getInstance().getReference(Prevalent.refColName_product);

        return instance;
    }


    /////////////////////////////////////   get user data     //////////////////////////////////////

    public void getUserById(String userId){
        mReference= FirebaseDatabase.getInstance().getReference(Prevalent.refColName_User);
        mReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                AuthorizedUser user=snapshot.child(userId).getValue(AuthorizedUser.class);
                if(mOnFindUserListener!=null)
                    mOnFindUserListener.onSuccess(user);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public onFindUserListener mOnFindUserListener;
    public void setOnFindUserListener(onFindUserListener mOnFindUserListener) {
        this.mOnFindUserListener = mOnFindUserListener;
    }



    public interface onFindUserListener{
        void onSuccess(AuthorizedUser user);
    }

    /////////////////////////////////////   get product data     //////////////////////////////////////

    public void getProductListByIds(List<String> productIds){
        mReference= FirebaseDatabase.getInstance().getReference(Prevalent.refColName_product);
        mReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Product> products=new ArrayList<>();
                for (String key: productIds)
                    products.add(snapshot.child(key).getValue(Product.class));
                if(mOnGetProductListener!=null)mOnGetProductListener.onSuccess(products);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public OnGetProductListener mOnGetProductListener;
    public void setOnGetProductListener(OnGetProductListener mOnGetProductListener) {
        this.mOnGetProductListener = mOnGetProductListener;
    }
    public interface OnGetProductListener{
        void onSuccess(List<Product> products);
    }


    /////////////////////////////////////   manage fav and cart list      //////////////////////////////////////


    public void addCart(String cart, AuthorizedUser user) {
        mReference = FirebaseDatabase.getInstance().getReference(Prevalent.refColName_User);
        user.getCartList().add(cart);
        mReference.child(user.getPhone()).setValue(user);
    }
    public void removeCart(String cart, AuthorizedUser user) {
        mReference = FirebaseDatabase.getInstance().getReference(Prevalent.refColName_User);
        user.getCartList().remove(cart);
        mReference.child(user.getPhone()).setValue(user);
    }

    public void addFav(String fav, AuthorizedUser user) {
        mReference = FirebaseDatabase.getInstance().getReference(Prevalent.refColName_User);
        user.getFavList().add(fav);
        mReference.child(user.getPhone()).setValue(user);
    }

    public void removeFav(String fav, AuthorizedUser user) {
        mReference = FirebaseDatabase.getInstance().getReference(Prevalent.refColName_User);
        user.getFavList().remove(fav);
        mReference.child(user.getPhone()).setValue(user);
    }


    /////////////////////////////////////    product fitch    //////////////////////////////////////

    public void fitchProducts(String ProductOwner,String cat){
        mReference = FirebaseDatabase.getInstance().getReference(Prevalent.refColName_product);
        mReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Product> products=new ArrayList<>();

                for(DataSnapshot snap:snapshot.getChildren()) {
                    Product product=snap.getValue(Product.class);
                    if(ProductOwner==null&&cat==null) { //fitch all to user
                        products.add(product);
                    }
                    else if(ProductOwner==null&&product.getmCategory().equalsIgnoreCase(cat)) { //fitch cat to user
                        products.add(product);
                    }

                    else if (product.getmAdmin().getPhone().equalsIgnoreCase(ProductOwner)) {//fitch to admin
                        products.add(product);
                    }
                }
                if(mOnFitchProductListener!=null)
                    mOnFitchProductListener.onSuccess(products);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if(mOnFitchProductListener!=null)
                    mOnFitchProductListener.onFailure(error.getMessage());
            }
        });


    }

    public OnFitchProductListener mOnFitchProductListener;
    public void setOnFitchProductListener(OnFitchProductListener mOnFitchProductListener) {
        this.mOnFitchProductListener = mOnFitchProductListener;
    }

    public interface OnFitchProductListener{
        void onFailure(String error);
        void onSuccess(List<Product> productList);
    }



    /////////////////////////////////////    product insertion    //////////////////////////////////////
    public void insertProduct(Product product, Uri[] mImageUri) {
        mReference = FirebaseDatabase.getInstance().getReference(Prevalent.refColName_product);
        String date,time,productKey;
        Calendar calendar=Calendar.getInstance();
        SimpleDateFormat DateFormat= new SimpleDateFormat("MMM dd, yyyy");
        SimpleDateFormat TimeFormat= new SimpleDateFormat("HH:mm:ss a");

        date=DateFormat.format(calendar.getTime());
        time=TimeFormat.format(calendar.getTime());
        productKey = mImageUri[0].getLastPathSegment() + date + time  ;

        product.setDate(date);
        product.setTime(time);
        mProduct=new Product(product);
        ArrayList<Uri> imageUri=new ArrayList<>();
        for(Uri u: mImageUri)
            if (u!=null)
                imageUri.add(u);

        uploadImage(imageUri,0,productKey);
        Log.d(TAG, "insertProduct: "+mProduct);
    }

    void uploadImage(ArrayList<Uri> mImageUri, int i, String productKey) {
        if(i>=mImageUri.size()) {
            insertProductTodatabase();
            Log.d(TAG, "onSuccess: Done");
            return;
        }
        if (mImageUri.get(i) != null) {
            mProductImageReference
                    .child(productKey + "_" + i )
                    .putFile(mImageUri.get(i))
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            if (taskSnapshot.getMetadata() != null) {
                                if (taskSnapshot.getMetadata().getReference() != null) {
                                    Task<Uri> result = taskSnapshot.getStorage().getDownloadUrl();
                                    result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            String imageUrl = uri.toString();
                                            mProduct.getmImageUri().add(imageUrl);
                                            Log.d(TAG, "onSuccess: " + i + "  " + imageUrl);
                                            uploadImage(mImageUri,i+1,productKey);
                                        }
                                    });
                                }
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            if (mOnAddProductListener != null)
                                mOnAddProductListener.onFailure(e.getMessage());
                        }
                    });
        }
    }


    private void insertProductTodatabase() {
        mProduct.setmId(mReference.push().getKey());
        mReference.child(mProduct.getmId()).setValue(mProduct);
        if(mOnAddProductListener!=null)
            mOnAddProductListener.onSuccess();
    }

    public OnAddProductListener mOnAddProductListener;
    public void setOnAddProductListener(OnAddProductListener mOnAddProductListener) {
        this.mOnAddProductListener = mOnAddProductListener;
    }
    public interface OnAddProductListener{
        void onFailure(String error);
        void onSuccess();
    }
    /////////////////////////////////////    delete product     //////////////////////////////////////

    public void deleteProduct(Product  product){
        mReference = FirebaseDatabase.getInstance().getReference(Prevalent.refColName_product);
        delete(product,0);

    }
    void delete(Product product,int i){
        if(i>=product.getmImageUri().size()){
            mReference.child(product.getmId()).removeValue();
            DatabaseReference mRef=FirebaseDatabase.getInstance().getReference(Prevalent.refColName_User);
            mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for(DataSnapshot snap:snapshot.getChildren()) {
                        AuthorizedUser user = snap.getValue(AuthorizedUser.class);
                        if(user.getCartList().contains(product.getmId())){
                            removeCart(product.getmId(),user);
                        }
                        if(user.getFavList().contains(product.getmId())){
                            removeFav(product.getmId(),user);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            return;
        }
        StorageReference deletedRef = FirebaseStorage.getInstance().getReferenceFromUrl(product.getmImageUri().get(i));
        deletedRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "onSuccess: ");
                delete(product,i+1);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: "+e.getMessage());
            }
        });

    }


    ///////////////////////////////////////////  insert order  /////////////////////////////////////////////

    public void insertOrder(OrderDB orderDB) {
        DatabaseReference mRef=FirebaseDatabase.getInstance().getReference(Prevalent.refColName_order);
        String key=mRef.push().getKey();
        orderDB.setId(key);
        mRef.child(orderDB.getId()).setValue(orderDB).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                decreaseQty(orderDB.getOrderList());
                deleteCart(orderDB.getPhone());
                if(mOnOrderAddedListener!=null)mOnOrderAddedListener.onSuccess();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if(mOnOrderAddedListener!=null)mOnOrderAddedListener.onFailure();
            }
        });
    }

    private void deleteCart(String phone) {
        DatabaseReference mRef=FirebaseDatabase.getInstance().getReference(Prevalent.refColName_User);
        mRef.child(phone).child("cartList").setValue(null);
    }

    private void decreaseQty(List<SubOrderDB> orderList) {
        DatabaseReference mRef=FirebaseDatabase.getInstance().getReference(Prevalent.refColName_product);
        for(SubOrderDB subOrderDB:orderList) {
            mRef.child(subOrderDB.getProduct_Key()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Product product = snapshot.getValue(Product.class);
                    int lastQty = product.getQuantity();
                    lastQty -= subOrderDB.getQty();
                    product.setQuantity(lastQty);
                    mRef.child(product.getmId()).setValue(product);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    onOrderAddedListener mOnOrderAddedListener;

    public void setOnOrderAddedListener(onOrderAddedListener mOnOrderAddedListener) {
        this.mOnOrderAddedListener = mOnOrderAddedListener;
    }

    public interface onOrderAddedListener{
        void onSuccess();
        void onFailure();
    }

    public void approveOn(OrderDB dbOrder) {
        DatabaseReference mRef=FirebaseDatabase.getInstance().getReference(Prevalent.refColName_order);
        mRef.child(dbOrder.getId()).setValue(dbOrder);
    }

    public void deliver(OrderDB dbOrder) {
        DatabaseReference mRef=FirebaseDatabase.getInstance().getReference(Prevalent.refColName_order);
        mRef.child(dbOrder.getId()).setValue(dbOrder);
    }

        public void fitchOrders(String userPhone) {
        DatabaseReference mRef=FirebaseDatabase.getInstance().getReference(Prevalent.refColName_order);
        List<OrderDB> orderDBList=new ArrayList<>();
        if(userPhone==null||userPhone.equals("")){//fitch all
            mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    for(DataSnapshot snap:snapshot.getChildren()) {
                        OrderDB orderDB = snap.getValue(OrderDB.class);
                            orderDBList.add(orderDB);
                    }
                    if(mOnOrderRetrievedListener!=null)
                        mOnOrderRetrievedListener.onComplete(orderDBList);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        else {
            mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    for (DataSnapshot snap : snapshot.getChildren()) {
                        OrderDB orderDB = snap.getValue(OrderDB.class);
                        if (orderDB.getPhone().equals(userPhone)) {
                            orderDBList.add(orderDB);
                        }
                    }
                    if (mOnOrderRetrievedListener != null)
                        mOnOrderRetrievedListener.onComplete(orderDBList);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    onOrderRetrievedListener mOnOrderRetrievedListener;
    public void setonOrderRetrievedListener(onOrderRetrievedListener mOnOrderRetrievedListener) {
        this.mOnOrderRetrievedListener = mOnOrderRetrievedListener;
    }
    public interface onOrderRetrievedListener{
        void onComplete(List<OrderDB> orderDBList);
    }
}
