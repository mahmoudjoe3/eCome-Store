package com.mahmoudjoe3.eComStore.ui.main;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        mFirebaseAuthViewModel = new ViewModelProvider(this).get(FirebaseAuthViewModel.class);
        EdtxtBirthDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus)
                    openCalender();
            }
        });
        EdtxtBirthDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCalender();
            }
        });
    }

    @OnClick({R.id.Btn_createAccount})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.Btn_createAccount:
                createAccount();
                break;
        }
    }

    private void openCalender() {
        Calendar calendar=Calendar.getInstance();
        int y,m,d;
        y=calendar.get(Calendar.YEAR);
        m=calendar.get(Calendar.MONTH);
        d=calendar.get(Calendar.DAY_OF_MONTH);
        new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(year,month,dayOfMonth);
                SimpleDateFormat DateFormat = new SimpleDateFormat("MMM dd, yyyy");

                EdtxtBirthDate.setText(DateFormat.format(calendar.getTime()));
            }
        },y,m,d).show();
    }


    private void createAccount() {
        String name = mName.getText().toString();
        String phone = mPhone.getText().toString();
        String password = mPassword.getText().toString();

        if (name.isEmpty()) {
            mName.setError("Name is Empty!");
        } else if (phone.isEmpty()) {
            mPhone.setError("Phone is Empty!");
        } else if (phone.length() != 11) {
            for (int i = 0; i < phone.length(); i++) {
                char c = phone.charAt(i);
                if ((int) c < 0 || (int) c > 9) {
                    mPhone.setError("Phone is incorrect!");
                    break;
                }
            }
        } else if (password.isEmpty()) {
            mPassword.setError("Password is Empty!");
        } else if (password.length() < 8) {
            mPassword.setError("Weak Password!");
        }
        else if (EdtxtBirthDate.getText().toString().isEmpty()) {
            EdtxtBirthDate.setError("Enter your birthday please!");
        }else {
            if (MyLogic.haveNetworkConnection(this)) {
                mFirebaseAuthViewModel.registerUser(name, phone, password,EdtxtBirthDate.getText().toString());
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
            } else {
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