package com.mahmoudjoe3.eComStore.ui.adminUI;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.mahmoudjoe3.eComStore.R;
import com.mahmoudjoe3.eComStore.model.Product;
import com.mahmoudjoe3.eComStore.model.User;
import com.mahmoudjoe3.eComStore.repo.FirebaseRepo;
import com.mahmoudjoe3.eComStore.viewModel.admin.AddProductViewModel;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class AddProductFragment extends Fragment {


    private final static int Gallary_Req = 1;
    private static final String TAG ="Add Product" ;
    @BindView(R.id.img1)
    ImageView mImg1;
    @BindView(R.id.add_img1)
    ImageButton mAddImg1;
    @BindView(R.id.img2)
    ImageView mImg2;
    @BindView(R.id.add_img2)
    ImageButton mAddImg2;
    @BindView(R.id.img3)
    ImageView mImg3;
    @BindView(R.id.add_img3)
    ImageButton mAddImg3;
    @BindView(R.id.img4)
    ImageView mImg4;
    @BindView(R.id.add_img4)
    ImageButton mAddImg4;
    @BindView(R.id.remove_img1)
    ImageButton mRemoveImg1;
    @BindView(R.id.remove_img2)
    ImageButton mRemoveImg2;
    @BindView(R.id.remove_img3)
    ImageButton mRemoveImg3;
    @BindView(R.id.remove_img4)
    ImageButton mRemoveImg4;

    @BindView(R.id.Category)
    TextInputEditText mCategoryTXT;
    @BindView(R.id.Title)
    TextInputEditText mTitle;
    @BindView(R.id.Price)
    TextInputEditText mPrice;
    @BindView(R.id.Quantity)
    TextInputEditText mQuantity;
    @BindView(R.id.Description)
    TextInputEditText mDescription;
    @BindView(R.id.AddProduct)
    Button mAddProduct;
    @BindView(R.id.hintImage)
    TextView hintImage;
    @BindView(R.id.progressBar)
    ProgressBar mProgressBar;

    private Uri[] mImageUri;
    private int mCurrentImgIndex = 0;
    private static User mAdmin;
    private static String mCategory;

    private AddProductViewModel addProductViewModel;
    public AddProductFragment() {
    }


    public static AddProductFragment newInstance() {
        return new AddProductFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mImageUri = new Uri[4];
        addProductViewModel= new ViewModelProvider(this).get(AddProductViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.admin_fragment_add_product, container, false);
        ButterKnife.bind(this, view);

        return view;
    }

    public static void sendDataToFragment(String Category, User Admin) {
        mCategory = Category;
        mAdmin = Admin;
    }

    @Override
    public void onResume() {
        super.onResume();
        mCategoryTXT.setText(mCategory);
    }

    @Override
    public void onPause() {
        super.onPause();
    }


    @OnClick({R.id.add_img1, R.id.add_img2, R.id.add_img3, R.id.add_img4, R.id.remove_img1, R.id.remove_img2, R.id.remove_img3, R.id.remove_img4})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.add_img1:
                openGallery();
                mCurrentImgIndex = 0;
                break;
            case R.id.add_img2:
                openGallery();
                mCurrentImgIndex = 1;
                break;
            case R.id.add_img3:
                openGallery();
                mCurrentImgIndex = 2;
                break;
            case R.id.add_img4:
                openGallery();
                mCurrentImgIndex = 3;
                break;

            case R.id.remove_img1:
                removeImage(1);
                break;
            case R.id.remove_img2:
                removeImage(2);
                break;
            case R.id.remove_img3:
                removeImage(3);
                break;
            case R.id.remove_img4:
                removeImage(4);
                break;
        }
    }

    private void removeImage(int i) {
        switch (i){
            case 1:
                int mCurrentRemovedImgIndex = 0;
                mImg1.setImageResource(android.R.color.transparent);
                mImageUri[mCurrentRemovedImgIndex] = null;
                mRemoveImg1.setVisibility(View.GONE);
                break;
            case 2:
                mCurrentRemovedImgIndex = 1;
                mImg2.setImageResource(android.R.color.transparent);
                mImageUri[mCurrentRemovedImgIndex] = null;
                mRemoveImg2.setVisibility(View.GONE);
                break;
            case 3:
                mCurrentRemovedImgIndex = 2;
                mImg3.setImageResource(android.R.color.transparent);
                mImageUri[mCurrentRemovedImgIndex] = null;
                mRemoveImg3.setVisibility(View.GONE);
                break;
            case 4:
                mCurrentRemovedImgIndex = 3;
                mImg4.setImageResource(android.R.color.transparent);
                mImageUri[mCurrentRemovedImgIndex] = null;
                mRemoveImg4.setVisibility(View.GONE);
                break;
        }

    }

    private void openGallery() {
        Intent gallery = new Intent(Intent.ACTION_GET_CONTENT);
        gallery.setType("image/*");
        startActivityForResult(gallery, Gallary_Req);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        getActivity();
        if (requestCode == Gallary_Req && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            mImageUri[mCurrentImgIndex] = data.getData();
            switch (mCurrentImgIndex) {
                case 0:
                    Picasso.get().load(mImageUri[mCurrentImgIndex]).fit().centerCrop().into(mImg1);
                    mRemoveImg1.setVisibility(View.VISIBLE);
                    break;
                case 1:
                    Picasso.get().load(mImageUri[mCurrentImgIndex]).fit().centerCrop().into(mImg2);
                    mRemoveImg2.setVisibility(View.VISIBLE);
                    break;
                case 2:
                    Picasso.get().load(mImageUri[mCurrentImgIndex]).fit().centerCrop().into(mImg3);
                    mRemoveImg3.setVisibility(View.VISIBLE);
                    break;
                case 3:
                    Picasso.get().load(mImageUri[mCurrentImgIndex]).fit().centerCrop().into(mImg4);
                    mRemoveImg4.setVisibility(View.VISIBLE);
                    break;
            }
        }
    }

    @OnClick(R.id.AddProduct)
    public void onViewClicked() {
        mAddProduct.setEnabled(false);
        AddProduct();
    }

    private void AddProduct() {
        boolean valid=true;
        if (mImageUri[0] == null && mImageUri[1] == null && mImageUri[2] == null && mImageUri[3] == null) {
            hintImage.setError("Please insert at least one image!");
            valid=false;
        }
        else
            hintImage.setError(null);
        if (mTitle.getText().toString().isEmpty()) {
            mTitle.setError("Title is Required");
            valid=false;
        }
        if (mPrice.getText().toString().isEmpty()){
            mPrice.setError("Price is Required");
            valid=false;
        }
        if (mQuantity.getText().toString().isEmpty()){
            mQuantity.setError("Price is Required");
            valid=false;
        }
        if (mDescription.getText().toString().isEmpty()){
            mDescription.setError("description is Required");
            valid=false;
        }
        if(valid) {
            String title,price,description,Quantity;
            title=mTitle.getText().toString();
            price=mPrice.getText().toString();
            description=mDescription.getText().toString();
            Quantity=mQuantity.getText().toString();
            Product product=new Product(mAdmin,title,mCategory,description,Float.parseFloat(price), Integer.parseInt(Quantity));

            mProgressBar.setVisibility(View.VISIBLE);

            addProductViewModel.insertProduct(product,mImageUri);

            addProductViewModel.setmOnAddProductListener(new FirebaseRepo.OnAddProductListener() {
                @Override
                public void onFailure(String error) {
                    Toast.makeText(getActivity(),"Error::"+error,Toast.LENGTH_LONG).show();
                    mProgressBar.setVisibility(View.GONE);
                }

                @Override
                public void onSuccess() {
                    Snackbar.make(getActivity().findViewById(android.R.id.content),"Product Uploaded",Snackbar.LENGTH_LONG).show();
                    mAddProduct.setEnabled(true);
                    mProgressBar.setVisibility(View.GONE);
                    clearData();
                }
            });
        }
    }
    void clearData(){
        removeImage(1);
        removeImage(2);
        removeImage(3);
        removeImage(4);
        mTitle.setText("");
        mPrice.setText("");
        mDescription.setText("");
        mQuantity.setText("");
    }
}