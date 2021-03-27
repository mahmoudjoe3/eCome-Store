package com.mahmoudjoe3.eComStore.ui.userUI;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.mahmoudjoe3.eComStore.Logic.MyLogic;
import com.mahmoudjoe3.eComStore.R;
import com.mahmoudjoe3.eComStore.model.AuthorizedUser;
import com.mahmoudjoe3.eComStore.prevalent.Prevalent;
import com.mahmoudjoe3.eComStore.ui.main.MainActivity;
import com.mahmoudjoe3.eComStore.ui.userUI.cart.UserCartActivity;
import com.mahmoudjoe3.eComStore.viewModel.ShardViewModel;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UserHomeActivity extends AppCompatActivity {

    private static final int RECOGNIZER_RES = 0;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.nav_view)
    NavigationView mNavView;
    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    AuthorizedUser mUser;
    SharedPreferences preferences;
    MenuItem cart_item, voice_item;
    private AppBarConfiguration mAppBarConfiguration;
    private ShardViewModel shardViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_activity_home);
        ButterKnife.bind(this);
        shardViewModel = new ViewModelProvider(this).get(ShardViewModel.class);

        preferences = getSharedPreferences(Prevalent.LOGIN_PREF, Context.MODE_PRIVATE);
        mUser = (AuthorizedUser) getIntent().getSerializableExtra(Prevalent.USER_DATA);

        setSupportActionBar(mToolbar);
        mNavView.setItemIconTintList(null);
        ((TextView) mNavView.getHeaderView(0).findViewById(R.id.profile_username)).setText(mUser.getName());
        ((TextView) mNavView.getHeaderView(0).findViewById(R.id.profile_phone)).setText(mUser.getPhone());

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_fashion, R.id.nav_watches, R.id.nav_mobile
                , R.id.nav_tvs, R.id.nav_pc, R.id.nav_healthy
                , R.id.nav_furniture, R.id.nav_sport, R.id.nav_orders
                , R.id.nav_wishList, R.id.nav_logout2)
                .setDrawerLayout(mDrawerLayout)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(mNavView, navController);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Snackbar snackbar = Snackbar.make(this.findViewById(android.R.id.content), getString(R.string.No_internet_connection), Snackbar.LENGTH_INDEFINITE);

        if (!MyLogic.haveNetworkConnection(this)) {
            snackbar.setActionTextColor(getResources().getColor(R.color.red))
                    .setAction(R.string.Exit, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            UserHomeActivity.this.finish();
                        }
                    }).show();
        } else if (snackbar.isShown()) snackbar.dismiss();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.user_home, menu);
        cart_item = menu.findItem(R.id.nav_cart);
        voice_item = menu.findItem(R.id.nav_voiceSearch);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.nav_logout:
                LogOut();
                return true;
            case R.id.nav_voiceSearch:
                openVoiceRecognizerInent();
                return true;
            case R.id.nav_search:
                textSearch(item);
                item.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
                    @Override
                    public boolean onMenuItemActionExpand(MenuItem item) {
                        cart_item.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
                        voice_item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
                        return true;
                    }

                    @Override
                    public boolean onMenuItemActionCollapse(MenuItem item) {
                        cart_item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
                        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
                        voice_item.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
                        return true;
                    }
                });
                return true;
            case R.id.nav_cart:
                Intent intent = new Intent(UserHomeActivity.this, UserCartActivity.class);
                intent.putExtra(UserCartActivity.UserCartActivity_User_Key, mUser.getPhone());
                startActivity(intent);
                return true;
        }
        return false;
    }

    private void LogOut() {
        if (preferences.contains(Prevalent.UserPhoneKey))
            preferences.edit().clear().apply();
        startActivity(new Intent(UserHomeActivity.this, MainActivity.class));
        finish();
    }

    private void textSearch(@NonNull MenuItem item) {
        SearchView searchView = (SearchView) item.getActionView();
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                shardViewModel.setLiveSearch(newText);
                return false;
            }
        });
    }


    private void openVoiceRecognizerInent() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.Say_what_you_wanna)
                +"\n"+getString(R.string.Say_all_for_all_Product));
        startActivityForResult(intent, RECOGNIZER_RES);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == RECOGNIZER_RES && resultCode == RESULT_OK && data != null) {
            String pattern = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS).get(0);
            shardViewModel.setLiveSearch(pattern);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration) || super.onSupportNavigateUp();
    }
}

