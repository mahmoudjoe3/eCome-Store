package com.mahmoudjoe3.eComStore.ui.main;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.textfield.TextInputEditText;
import com.mahmoudjoe3.eComStore.Logic.MyLogic;
import com.mahmoudjoe3.eComStore.R;
import com.mahmoudjoe3.eComStore.model.User;
import com.mahmoudjoe3.eComStore.prevalent.Prevalent;
import com.mahmoudjoe3.eComStore.repo.FirebaseAuthRepo;
import com.mahmoudjoe3.eComStore.ui.adminUI.AdminHomeActivity;
import com.mahmoudjoe3.eComStore.ui.userUI.UserHomeActivity;
import com.mahmoudjoe3.eComStore.viewModel.FirebaseAuthViewModel;
import com.rey.material.widget.CheckBox;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.Edtxt_phone)
    TextInputEditText mPhone;
    @BindView(R.id.Edtxt_password)
    TextInputEditText mPassword;
    @BindView(R.id.Chbx_rememberme)
    CheckBox mRemember_me;
    @BindView(R.id.txt_forgetPassword)
    TextView mForgetPassword;
    @BindView(R.id.Btn_login)
    Button mLogin;
    @BindView(R.id.logo)
    ImageView logo;
    @BindView(R.id.txt_admin)
    TextView mAdminTxt;
    @BindView(R.id.txt_not_admin)
    TextView mNotAdminTxt;
    AlertDialog mAlertDialog;

    final static int STANDARD_PHONE_SIZE =11;

    private FirebaseAuthViewModel mFirebaseAuthViewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        mFirebaseAuthViewModel = new ViewModelProvider(this).get(FirebaseAuthViewModel.class);

    }



    private void loginProcess() {
        String phone=mPhone.getText().toString();
        String password=mPassword.getText().toString();

        if(phone.isEmpty()){
            mPhone.setError("Phone is Empty!");
        }
        else if(phone.length()!= STANDARD_PHONE_SIZE){
            mPhone.setError("Phone is incorrect!");
        }
        else if(password.isEmpty()){
            mPassword.setError("Password is Empty!");
        }
        else{
            mAlertDialog=createDialoge("User Login","Please wait while Authentication checked...").create();


            if(MyLogic.haveNetworkConnection(this)) {
                boolean isAdmin= mNotAdminTxt.getVisibility() == View.VISIBLE;
                mAlertDialog.show();
                mFirebaseAuthViewModel.login(isAdmin,phone,password,mRemember_me.isChecked());

                mFirebaseAuthViewModel.setOnLoginListener(new FirebaseAuthRepo.OnLoginListener() {
                    @Override
                    public void onLogeInSuccess(User user) {
                        mAlertDialog.dismiss();
                        Intent intent=((isAdmin)?
                                new Intent(LoginActivity.this, AdminHomeActivity.class):
                                new Intent(LoginActivity.this, UserHomeActivity.class));
                        intent.putExtra(Prevalent.USER_DATA,user);
                        startActivity(intent);

                    }

                    @Override
                    public void onLogeInDenied(String errorMsg) {
                        Toast.makeText(LoginActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                        if(errorMsg.contains("password"))mPassword.setError(errorMsg);
                        mAlertDialog.dismiss();
                    }

                    @Override
                    public void onRemember(User user) {
                        SharedPreferences preferences = getSharedPreferences(Prevalent.LOGIN_PREF, Context.MODE_PRIVATE);
                        preferences.edit().clear().apply();
                        SharedPreferences.Editor editor= preferences.edit();
                        editor.putString(Prevalent.UserPhoneKey,user.getPhone());
                        editor.putString(Prevalent.UserPasswordKey,user.getPassword());
                        editor.putString(Prevalent.UserNameKey,user.getName());
                        if(isAdmin) //admin
                            editor.putBoolean(Prevalent.ADMIN,true);
                        else editor.putBoolean(Prevalent.ADMIN,false);
                        editor.apply();
                    }
                });
            }
            else {
                Toast.makeText(getApplicationContext(), "Network error please try again latter..", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
            }
        }
    }


    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }


    private void iAmNotAdminProcess() {
        mAdminTxt.setVisibility(View.VISIBLE);
        mNotAdminTxt.setVisibility(View.GONE);
        mLogin.setText(R.string.login);
    }

    private void iAmAdminProcess() {
        mAdminTxt.setVisibility(View.GONE);
        mNotAdminTxt.setVisibility(View.VISIBLE);
        mLogin.setText(R.string.Login_Admin);
    }

    private void forgetPasswordProcess() {
    }




    @OnClick({R.id.txt_forgetPassword, R.id.Btn_login, R.id.txt_admin, R.id.txt_not_admin})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.txt_forgetPassword:
                forgetPasswordProcess();
                break;
            case R.id.Btn_login:
                loginProcess();
                break;
            case R.id.txt_admin:
                iAmAdminProcess();
                break;
            case R.id.txt_not_admin:
                iAmNotAdminProcess();
                break;
        }
    }

    public AlertDialog.Builder createDialoge(String tit, String msg) {
        View view=getLayoutInflater().inflate(R.layout.progress_dialog_layout,null);
        TextView title=view.findViewById(R.id.dialog_title);
        title.setText(tit);
        TextView message=view.findViewById(R.id.dialog_message);
        message.setText(msg);

        return new AlertDialog.Builder(this)
                .setView(view)
                .setCancelable(false);
    }

}