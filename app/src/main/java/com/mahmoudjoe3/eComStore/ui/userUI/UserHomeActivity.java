package com.mahmoudjoe3.eComStore.ui.userUI;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.mahmoudjoe3.eComStore.R;
import com.mahmoudjoe3.eComStore.model.User;
import com.mahmoudjoe3.eComStore.prevalent.Prevalent;
import com.mahmoudjoe3.eComStore.ui.main.MainActivity;


public class UserHomeActivity extends AppCompatActivity {
    User mUser;
    SharedPreferences preferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home);

        preferences=getSharedPreferences(Prevalent.LOGIN_PREF, Context.MODE_PRIVATE);
        mUser= (User) getIntent().getSerializableExtra(Prevalent.USER_DATA);
        ((TextView)findViewById(R.id.textView)).setText(mUser.getName());
        ((TextView)findViewById(R.id.button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(preferences.contains(Prevalent.UserPhoneKey))
                    preferences.edit().clear().apply();
                startActivity(new Intent(UserHomeActivity.this, MainActivity.class));
                finish();
            }
        });
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        super.onDestroy();
    }
}