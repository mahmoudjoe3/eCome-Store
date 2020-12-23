package com.mahmoudjoe3.eComStore.ui.userUI;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.mahmoudjoe3.eComStore.R;
import com.mahmoudjoe3.eComStore.model.User;
import com.mahmoudjoe3.eComStore.prevalent.Prevalent;
import com.mahmoudjoe3.eComStore.ui.main.MainActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UserHomeActivity extends AppCompatActivity {

    private static final String TAG = "UserHomeActivityppppp";
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.nav_view)
    NavigationView mNavView;
    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    private AppBarConfiguration mAppBarConfiguration;

    User mUser;
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_activity_home);
        ButterKnife.bind(this);

        preferences=getSharedPreferences(Prevalent.LOGIN_PREF, Context.MODE_PRIVATE);
        mUser= (User) getIntent().getSerializableExtra(Prevalent.USER_DATA);

        setSupportActionBar(mToolbar);
        Toast.makeText(this, ""+mToolbar.getTitle(), Toast.LENGTH_SHORT).show();
        mNavView.setItemIconTintList(null);
        ((TextView)mNavView.getHeaderView(0).findViewById(R.id.profile_username)).setText(mUser.getName());
        ((TextView)mNavView.getHeaderView(0).findViewById(R.id.profile_phone)).setText(mUser.getPhone());

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home,R.id.nav_fashion, R.id.nav_watches, R.id.nav_mobile
                ,R.id.nav_tvs, R.id.nav_pc, R.id.nav_healthy
                ,R.id.nav_furniture, R.id.nav_sport, R.id.nav_orders
                ,R.id.nav_wishList, R.id.nav_setting, R.id.nav_logout)
                .setDrawerLayout(mDrawerLayout)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(mNavView, navController);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.user_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Log.d(TAG, "onOptionsItemSelected: ");
        if(item.getItemId()==R.id.nav_logout){
            LogOut();
        }
        return super.onOptionsItemSelected(item);
    }

    private void LogOut() {
        if(preferences.contains(Prevalent.UserPhoneKey))
            preferences.edit().clear().apply();
        startActivity(new Intent(UserHomeActivity.this, MainActivity.class));
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration) || super.onSupportNavigateUp();
    }
}

