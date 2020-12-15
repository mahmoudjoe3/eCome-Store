package com.mahmoudjoe3.eComStore.ui.adminUI;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.mahmoudjoe3.eComStore.R;
import com.mahmoudjoe3.eComStore.model.User;
import com.mahmoudjoe3.eComStore.prevalent.Prevalent;
import com.mahmoudjoe3.eComStore.ui.main.MainActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AdminHomeActivity extends AppCompatActivity {

    User mUser;
    SharedPreferences preferences;
    @BindView(R.id.button2)
    Button button2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);
        ButterKnife.bind(this);

        preferences = getSharedPreferences(Prevalent.LOGIN_PREF, Context.MODE_PRIVATE);
        mUser = (User) getIntent().getSerializableExtra(Prevalent.USER_DATA);

    }

    private void logout() {

        ((TextView) findViewById(R.id.textView)).setText(mUser.getName());
        ((TextView) findViewById(R.id.button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (preferences.contains(Prevalent.UserPhoneKey) && preferences.contains(Prevalent.UserNameKey) && preferences.contains(Prevalent.UserPasswordKey))
                    preferences.edit().clear().apply();
                startActivity(new Intent(AdminHomeActivity.this, MainActivity.class));
                finish();
            }
        });
    }

    @OnClick(R.id.button2)
    public void onViewClicked() {
        Intent i=new Intent(this,AdminAddProductActivity.class);
        i.putExtra(Prevalent.USER_DATA,mUser);
        startActivity(i);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}