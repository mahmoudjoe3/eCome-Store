package com.mahmoudjoe3.eComStore.ui.main;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.mahmoudjoe3.eComStore.BuildConfig;
import com.mahmoudjoe3.eComStore.Logic.MyLogic;
import com.mahmoudjoe3.eComStore.R;
import com.mahmoudjoe3.eComStore.model.Admin;
import com.mahmoudjoe3.eComStore.model.AuthorizedUser;
import com.mahmoudjoe3.eComStore.prevalent.Prevalent;
import com.mahmoudjoe3.eComStore.repo.FirebaseAuthRepo;
import com.mahmoudjoe3.eComStore.ui.adminUI.AdminHomeActivity;
import com.mahmoudjoe3.eComStore.ui.userUI.UserHomeActivity;
import com.mahmoudjoe3.eComStore.viewModel.FirebaseAuthViewModel;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MainActivity extends AppCompatActivity {

    @BindView(R.id.M_Btn_join)
    Button mJoin;
    @BindView(R.id.M_Btn_login)
    Button mLogin;
    AlertDialog mAlertDialog;


    SharedPreferences preferences;
    boolean isAdmin;

    private FirebaseAuthViewModel mFirebaseAuthViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mFirebaseAuthViewModel = new ViewModelProvider(this).get(FirebaseAuthViewModel.class);

        //delay for one sec
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);


    }

    @Override
    protected void onStart() {
        super.onStart();
        LogInProcess();
    }

    private void LogInProcess() {
        preferences = getSharedPreferences(Prevalent.LOGIN_PREF, Context.MODE_PRIVATE);
        if (preferences.contains(Prevalent.UserNameKey) && preferences.contains(Prevalent.UserPasswordKey) && preferences.contains(Prevalent.UserPhoneKey)) {
            String phone = preferences.getString(Prevalent.UserPhoneKey, null);
            String password = preferences.getString(Prevalent.UserPasswordKey, null);
            String name = preferences.getString(Prevalent.UserNameKey, null);
            isAdmin = preferences.getBoolean(Prevalent.ADMIN, false);

            //online
            if (MyLogic.haveNetworkConnection(this)) {
                mFirebaseAuthViewModel.checkVersionName(BuildConfig.VERSION_NAME);
                mFirebaseAuthViewModel.setmOnVersionListener(new FirebaseAuthRepo.OnVersionListener() {
                    @Override
                    public void onRealVersion() {
                        FireStore(phone, password);
                    }

                    @Override
                    public void onOldVersion(String NewVersion) {
                        mAlertDialog = new AlertDialog
                                .Builder(MainActivity.this)
                                .setTitle("Update Version")
                                .setCancelable(false)
                                .setMessage(getResources().getString(R.string.app_name) + " has new Version " + NewVersion + " Please Update..")
                                .setPositiveButton("Update", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent intent = new Intent(Intent.ACTION_VIEW);
                                        intent.setData(Uri.parse(Prevalent.APP_URL));
                                        startActivity(intent);
                                    }
                                })
                                .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        MainActivity.this.finish();

                                    }
                                })
                                .create();
                        mAlertDialog.show();

                    }
                });
                return;
            }

            //offline
            if (phone != null && password != null && name != null) {

                Intent intent;
                if (isAdmin)
                    intent = new Intent(MainActivity.this, AdminHomeActivity.class);
                else
                    intent = new Intent(MainActivity.this, UserHomeActivity.class);
                if (isAdmin)
                    intent.putExtra(Prevalent.USER_DATA, new Admin(name, phone, password));
                else
                    intent.putExtra(Prevalent.USER_DATA, new AuthorizedUser(name, phone, password));
                startActivity(intent);
            }
        }
    }

    private void FireStore(String phone, String password) {
        if (password != null && phone != null) {
            mAlertDialog = createDialoge("User Login", "Please wait while Authentication checked...").create();
            mAlertDialog.show();
            if (MyLogic.haveNetworkConnection(this)) {
                mFirebaseAuthViewModel.login(isAdmin, phone, password, false);
                ////////////////////////////////////////////////
                mFirebaseAuthViewModel.setOnLoginListener(new FirebaseAuthRepo.OnLoginListener() {
                    @Override
                    public void onLogeInSuccess(Object user) {
                        mAlertDialog.dismiss();
                        Intent intent = ((isAdmin) ?
                                new Intent(MainActivity.this, AdminHomeActivity.class) :
                                new Intent(MainActivity.this, UserHomeActivity.class));
                        if (isAdmin)
                            intent.putExtra(Prevalent.USER_DATA, (Admin) user);
                        else intent.putExtra(Prevalent.USER_DATA, (AuthorizedUser) user);
                        startActivity(intent);

                    }

                    @Override
                    public void onLogeInDenied(String errorMsg) {
                        Toast.makeText(MainActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                        mAlertDialog.dismiss();
                    }

                    @Override
                    public void onRemember(Object user) {

                    }
                });
            } else {
                Toast.makeText(getApplicationContext(), "Network error please try again latter..", Toast.LENGTH_SHORT).show();
            }

        }
    }


    @OnClick({R.id.M_Btn_join, R.id.M_Btn_login})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.M_Btn_join:
                startActivity(new Intent(this, RegisterActivity.class));
                break;
            case R.id.M_Btn_login:
                startActivity(new Intent(this, LoginActivity.class));
                break;
        }
    }

    private AlertDialog.Builder createDialoge(String tit, String msg) {
        View view = getLayoutInflater().inflate(R.layout.progress_dialog_layout, null);
        TextView title = view.findViewById(R.id.dialog_title);
        title.setText(tit);
        TextView message = view.findViewById(R.id.dialog_message);
        message.setText(msg);

        return new AlertDialog.Builder(this)
                .setView(view)
                .setCancelable(false);
    }

    @Override
    protected void onPause() {
        super.finish();
        super.onPause();
    }
}