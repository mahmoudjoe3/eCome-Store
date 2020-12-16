package com.mahmoudjoe3.eComStore.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.textfield.TextInputEditText;
import com.mahmoudjoe3.eComStore.Logic.MyLogic;
import com.mahmoudjoe3.eComStore.R;
import com.mahmoudjoe3.eComStore.repo.FirebaseAuthRepo;
import com.mahmoudjoe3.eComStore.viewModel.FirebaseAuthViewModel;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RegisterActivity extends AppCompatActivity {

    @BindView(R.id.Edtxt_name)
    TextInputEditText mName;
    @BindView(R.id.Edtxt_phone)
    TextInputEditText mPhone;
    @BindView(R.id.Edtxt_password)
    TextInputEditText mPassword;
    @BindView(R.id.logo)
    ImageView logo;
    @BindView(R.id.Btn_createAccount)
    Button mCreateAccountBtn;


    FirebaseAuthViewModel mFirebaseAuthViewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        mFirebaseAuthViewModel = new ViewModelProvider(this).get(FirebaseAuthViewModel.class);

    }

    @OnClick(R.id.Btn_createAccount)
    public void onViewClicked() {
        createAccount();
    }

    private void createAccount() {
        String name=mName.getText().toString();
        String phone=mPhone.getText().toString();
        String password=mPassword.getText().toString();

        if(name.isEmpty()){
            mName.setError("Name is Empty!");
        }
        else if(phone.isEmpty()){
            mPhone.setError("Phone is Empty!");
        }
        else if(phone.length()!=11){
            for(int i=0;i<phone.length();i++){
                char c=phone.charAt(i);
                if((int)c < 0||(int)c>9){
                    mPhone.setError("Phone is incorrect!");
                    break;
                }
            }
        }
        else if(password.isEmpty()){
            mPassword.setError("Password is Empty!");
        }
        else if(password.length()<8){
            mPassword.setError("Weak Password!");
        }
        else{
            if(MyLogic.haveNetworkConnection(this)) {
                mFirebaseAuthViewModel.registerUser(name, phone, password);
                mFirebaseAuthViewModel.setOnRegisterListener(new FirebaseAuthRepo.OnRegisterListener() {
                    @Override
                    public void onRegisterSuccess() {
                        Toast.makeText(RegisterActivity.this, "Congratulations!! Your Account created Successfully..", Toast.LENGTH_SHORT).show();
                        startActivity((new Intent(RegisterActivity.this, LoginActivity.class)));
                    }

                    @Override
                    public void onRegisterExist() {
                        Toast.makeText(RegisterActivity.this, "This phone number is exist try another one..", Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onRegisterFailure() {
                        Toast.makeText(RegisterActivity.this, "Network error please try again latter..", Toast.LENGTH_SHORT).show();
                        startActivity((new Intent(RegisterActivity.this, MainActivity.class)));

                    }
                });
            }
            else {
                Toast.makeText(getApplicationContext(), "Network error please try again latter..", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        }
    }

    private AlertDialog.Builder createDialoge() {
        return new AlertDialog.Builder(this)
                .setView(R.layout.progress_dialog_layout)
                .setCancelable(false);
    }

    protected void onStop() {
        super.onStop();
        finish();
    }


}