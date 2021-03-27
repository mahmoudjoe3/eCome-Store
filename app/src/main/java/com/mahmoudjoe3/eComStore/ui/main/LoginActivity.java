package com.mahmoudjoe3.eComStore.ui.main;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.mahmoudjoe3.eComStore.Logic.MyLogic;
import com.mahmoudjoe3.eComStore.R;
import com.mahmoudjoe3.eComStore.model.Admin;
import com.mahmoudjoe3.eComStore.model.AuthorizedUser;
import com.mahmoudjoe3.eComStore.prevalent.Prevalent;
import com.mahmoudjoe3.eComStore.repo.FirebaseAuthRepo;
import com.mahmoudjoe3.eComStore.ui.adminUI.AdminHomeActivity;
import com.mahmoudjoe3.eComStore.ui.userUI.UserHomeActivity;
import com.mahmoudjoe3.eComStore.viewModel.FirebaseAuthViewModel;
import com.rey.material.widget.CheckBox;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class LoginActivity extends AppCompatActivity {

    final static int STANDARD_PHONE_SIZE = 11;
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
    String VerificationId = "";
    String phone_Dialog;
    AlertDialog progress;
    private FirebaseAuthViewModel mFirebaseAuthViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        mFirebaseAuthViewModel = new ViewModelProvider(this).get(FirebaseAuthViewModel.class);

    }

    private void loginProcess() {
        String phone = mPhone.getText().toString();
        String password = mPassword.getText().toString();

        if (phone.isEmpty()) {
            mPhone.setError(getString(R.string.PhoneisEmpty));
        } else if (phone.length() != STANDARD_PHONE_SIZE) {
            mPhone.setError(getString(R.string.Phone_is_incorrect));
        } else if (password.isEmpty()) {
            mPassword.setError(getString(R.string.Password_is_Empty));
        } else {
            mAlertDialog = createDialoge(getString(R.string.User_Login), getString(R.string.Please_wait_while_Authentication_checked)).create();


            if (MyLogic.haveNetworkConnection(this)) {
                boolean isAdmin = mNotAdminTxt.getVisibility() == View.VISIBLE;
                mAlertDialog.show();
                mFirebaseAuthViewModel.login(isAdmin, phone, password, mRemember_me.isChecked());

                mFirebaseAuthViewModel.setOnLoginListener(new FirebaseAuthRepo.OnLoginListener() {
                    @Override
                    public void onLogeInSuccess(Object user) {
                        mAlertDialog.dismiss();
                        Intent intent = ((isAdmin) ?
                                new Intent(LoginActivity.this, AdminHomeActivity.class) :
                                new Intent(LoginActivity.this, UserHomeActivity.class));
                        if (isAdmin)
                            intent.putExtra(Prevalent.USER_DATA, (Admin) user);
                        else intent.putExtra(Prevalent.USER_DATA, (AuthorizedUser) user);
                        startActivity(intent);

                    }

                    @Override
                    public void onLogeInDenied(String errorMsg) {
                        String err = (errorMsg.contains("password")) ?
                                getString(R.string.password_incorrect)
                                : getString(R.string.Login_Failed_You_should_have_account);
                        Toast.makeText(LoginActivity.this, err, Toast.LENGTH_SHORT).show();
                        if (errorMsg.contains("password")) mPassword.setError(err);
                        mAlertDialog.dismiss();
                    }

                    @Override
                    public void onRemember(Object user) {
                        SharedPreferences preferences = getSharedPreferences(Prevalent.LOGIN_PREF, Context.MODE_PRIVATE);
                        preferences.edit().clear().apply();
                        SharedPreferences.Editor editor = preferences.edit();
                        String phone = null, pass = null, name = null;
                        if (isAdmin) { //admin
                            phone = ((Admin) user).getPhone();
                            pass = ((Admin) user).getPassword();
                            name = ((Admin) user).getName();
                            editor.putBoolean(Prevalent.ADMIN, true);
                        } else {
                            phone = ((AuthorizedUser) user).getPhone();
                            pass = ((AuthorizedUser) user).getPassword();
                            name = ((AuthorizedUser) user).getName();
                            editor.putBoolean(Prevalent.ADMIN, false);
                        }

                        editor.putString(Prevalent.UserPhoneKey, phone);
                        editor.putString(Prevalent.UserPasswordKey, pass);
                        editor.putString(Prevalent.UserNameKey, name);
                        editor.apply();
                    }
                });
            } else {
                Toast.makeText(getApplicationContext(), R.string.Network_error_please_try_again_latter, Toast.LENGTH_SHORT).show();
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

        if (MyLogic.haveNetworkConnection(this)) {
            createForgetPasswordDialog().show();
        } else
            Toast.makeText(getApplicationContext(), R.string.Network_error_please_try_again_latter, Toast.LENGTH_SHORT).show();
    }

    private AlertDialog createForgetPasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.forget_pass_dialog, null);
        ((TextView) view.findViewById(R.id.forget_dialog_title)).setText(R.string.Forget_Password);
        ((TextView) view.findViewById(R.id.forget_dialog_hint)).setHint(R.string.Enter_Your_Phone_Number);
        progress = createDialoge(getString(R.string.OTP_Request), getString(R.string.Wait_till_we_send_OTP_Code)).create();
        builder.setView(view)
                .setPositiveButton(R.string.Get_Code, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                        phone_Dialog = ((TextView) view.findViewById(R.id.forget_dialog_hint)).getText().toString();
                        if (phone_Dialog.isEmpty() || phone_Dialog.length() != STANDARD_PHONE_SIZE)
                            Toast.makeText(LoginActivity.this, R.string.invalid_Phone_Number, Toast.LENGTH_SHORT).show();
                        else {
                            mFirebaseAuthViewModel.isUser(phone_Dialog, new FirebaseAuthRepo.OnUserValidationListener() {
                                @Override
                                public void onUserValid() {
                                    progress.show();
                                    sendVerificationPhoneCodeMSG();
                                }

                                @Override
                                public void onUserNotValid() {
                                    Toast.makeText(LoginActivity.this, R.string.You_are_not_a_User_Please_register_first, Toast.LENGTH_SHORT).show();
                                }
                            });

                        }
                    }
                })
                .setNegativeButton(R.string.back, null);
        return builder.create();
    }

    private void sendVerificationPhoneCodeMSG() {
        PhoneAuthProvider.verifyPhoneNumber(
                PhoneAuthOptions.newBuilder()
                        .setPhoneNumber("+20" + phone_Dialog)
                        .setActivity(LoginActivity.this)
                        .setTimeout(60L, TimeUnit.SECONDS).setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                    @Override
                    public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        super.onCodeSent(s, forceResendingToken);
                        VerificationId = s;
                        progress.dismiss();
                        createVerifyDialog().show();
                    }

                    @Override
                    public void onCodeAutoRetrievalTimeOut(@NonNull String s) {
                        super.onCodeAutoRetrievalTimeOut(s);
                        Toast.makeText(LoginActivity.this, R.string.Request_time_out, Toast.LENGTH_SHORT).show();
                        progress.dismiss();
                    }

                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        if (phoneAuthCredential.getSmsCode() == null || phoneAuthCredential.getSmsCode().equals("")) {
                            verifyCode(phoneAuthCredential.getSmsCode());
                        }

                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        Toast.makeText(LoginActivity.this, R.string.Verification_Failed, Toast.LENGTH_SHORT).show();
                        progress.dismiss();
                    }
                }).build());

    }

    private void verifyCode(String smsCode) {
        AlertDialog progress2 = createDialoge(getString(R.string.OTP_Verify), getString(R.string.Please_Wait_Until_Code_is_verified)).create();
        progress2.show();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(VerificationId, smsCode);
        auth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    progress.dismiss();
                    createResetPasswordDialog().show();
                    progress2.dismiss();
                    Toast.makeText(LoginActivity.this, R.string.Verified_Successfully, Toast.LENGTH_SHORT).show();

                } else {
                    progress.dismiss();
                    progress2.dismiss();
                    Toast.makeText(LoginActivity.this, R.string.verification_Code_Failed, Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    private AlertDialog createVerifyDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.forget_pass_dialog, null);
        ((TextView) view.findViewById(R.id.forget_dialog_title)).setText(R.string.Verification);
        ((TextView) view.findViewById(R.id.forget_dialog_hint)).setHint(R.string.Enter_Verification_Code);
        builder.setCancelable(false).setView(view)
                .setPositiveButton(R.string.Verify, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        String enteredCode = ((TextView) view.findViewById(R.id.forget_dialog_hint)).getText().toString();
                        verifyCode(enteredCode);
                    }
                })
                .setNegativeButton(R.string.back, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        return builder.create();
    }

    private AlertDialog createResetPasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.forget_password_reset_dialog, null);
        builder.setCancelable(false).setView(view)
                .setPositiveButton(R.string.Reset, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        String newPassword = ((TextView) view.findViewById(R.id.dialog_reset_pass)).getText().toString();
                        boolean isAdmin = mNotAdminTxt.getVisibility() == View.VISIBLE;
                        mFirebaseAuthViewModel.forgetPassword(isAdmin, phone_Dialog, newPassword);
                        //TODO phone not exist hundaling
                        Toast.makeText(LoginActivity.this, R.string.Password_rest_successfully, Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton(R.string.back, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        return builder.create();
    }


    @OnClick({R.id.txt_forgetPassword, R.id.Btn_login, R.id.txt_admin, R.id.txt_not_admin,R.id.login_back})
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
            case R.id.login_back:
                startActivity(new Intent(LoginActivity.this,MainActivity.class));
                break;
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(LoginActivity.this,MainActivity.class));
    }

    public AlertDialog.Builder createDialoge(String tit, String msg) {
        View view = getLayoutInflater().inflate(R.layout.progress_dialog_layout, null);
        TextView title = view.findViewById(R.id.dialog_title);
        title.setText(tit);
        TextView message = view.findViewById(R.id.dialog_message);
        message.setText(msg);

        return new AlertDialog.Builder(this)
                .setView(view)
                .setCancelable(false).setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
    }


}