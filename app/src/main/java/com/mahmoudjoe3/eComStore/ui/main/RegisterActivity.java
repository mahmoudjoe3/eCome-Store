package com.mahmoudjoe3.eComStore.ui.main;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.TextInputEditText;
import com.mahmoudjoe3.eComStore.Logic.MyLogic;
import com.mahmoudjoe3.eComStore.R;
import com.mahmoudjoe3.eComStore.repo.FirebaseAuthRepo;
import com.mahmoudjoe3.eComStore.viewModel.FirebaseAuthViewModel;

import java.text.SimpleDateFormat;
import java.util.Calendar;

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
    @BindView(R.id.Edtxt_birthDate)
    TextInputEditText EdtxtBirthDate;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        mFirebaseAuthViewModel = new ViewModelProvider(this).get(FirebaseAuthViewModel.class);
        EdtxtBirthDate.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_RIGHT = 2;
                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if(event.getRawX() >= (EdtxtBirthDate.getRight() - EdtxtBirthDate.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        // your action here
                        openCalender();
                        return true;
                    }
                }
                return false;
            }
        });

    }

    @OnClick({R.id.Btn_createAccount,R.id.reg_back})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.Btn_createAccount:
                createAccount();
                break;
            case R.id.reg_back:
                startActivity(new Intent(RegisterActivity.this,MainActivity.class));
                break;
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(RegisterActivity.this,MainActivity.class));
    }

    private void openCalender() {
        Calendar calendar = Calendar.getInstance();
        int y, m, d;
        y = calendar.get(Calendar.YEAR);
        m = calendar.get(Calendar.MONTH);
        d = calendar.get(Calendar.DAY_OF_MONTH);
        new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(year, month, dayOfMonth);
                SimpleDateFormat DateFormat = new SimpleDateFormat("MMM dd, yyyy");

                EdtxtBirthDate.setText(DateFormat.format(calendar.getTime()));
            }
        }, y, m, d).show();
    }


    private void createAccount() {
        String name = mName.getText().toString();
        String phone = mPhone.getText().toString();
        String password = mPassword.getText().toString();

        if (name.isEmpty()) {
            mName.setError(getString(R.string.Name_is_Empty));
        } else if (phone.isEmpty()) {
            mPhone.setError(getString(R.string.PhoneisEmpty));
        } else if (phone.length() != 11) {
            for (int i = 0; i < phone.length(); i++) {
                char c = phone.charAt(i);
                if ((int) c < 0 || (int) c > 9) {
                    mPhone.setError(getString(R.string.Phone_is_incorrect));
                    break;
                }
            }
        } else if (password.isEmpty()) {
            mPassword.setError(getString(R.string.Password_is_Empty));
        } else if (password.length() < 8) {
            mPassword.setError(getString(R.string.Weak_Password));
        } else if (EdtxtBirthDate.getText().toString().isEmpty()) {
            EdtxtBirthDate.setError(getString(R.string.Enter_your_birthday_please));
        } else {
            if (MyLogic.haveNetworkConnection(this)) {
                mFirebaseAuthViewModel.registerUser(name, phone, password, EdtxtBirthDate.getText().toString());
                mFirebaseAuthViewModel.setOnRegisterListener(new FirebaseAuthRepo.OnRegisterListener() {
                    @Override
                    public void onRegisterSuccess() {
                        Toast.makeText(RegisterActivity.this, R.string.Congratulations_Your_Account_created_Successfully, Toast.LENGTH_SHORT).show();
                        startActivity((new Intent(RegisterActivity.this, LoginActivity.class)));
                    }

                    @Override
                    public void onRegisterExist() {
                        Toast.makeText(RegisterActivity.this, R.string.This_phone_number_is_exist_try_another_one, Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onRegisterFailure() {
                        Toast.makeText(RegisterActivity.this, R.string.Network_error_please_try_again_latter, Toast.LENGTH_SHORT).show();
                        startActivity((new Intent(RegisterActivity.this, MainActivity.class)));

                    }
                });
            } else {
                Toast.makeText(getApplicationContext(), R.string.Network_error_please_try_again_latter, Toast.LENGTH_SHORT).show();
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