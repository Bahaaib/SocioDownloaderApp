package com.bahaaapps.sociodownloader;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;

import com.bahaaapps.sociodownloader.Adapter.PagerAdapter;
import com.bahaaapps.sociodownloader.Facebook.FacebookActivity;
import com.bahaaapps.sociodownloader.Instagram.InstagramActivity;
import com.bahaaapps.sociodownloader.Models.ProgressModel;
import com.bahaaapps.sociodownloader.Youtube.view.YoutubeActivity;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class HomeActivity extends AppCompatActivity {

    @BindView(R.id.main_coordinator_layout)
    public CoordinatorLayout mainCooLayout;

    @BindView(R.id.main_toolbar)
    public Toolbar toolbar;

    @BindView(R.id.btm_nav)
    public BottomNavigationView bottomNavigationView;

    @BindView(R.id.pager)
    public ViewPager viewPager;

    @BindView(R.id.drawer_layout)
    public DrawerLayout drawerLayout;

    @BindView(R.id.nv)
    public NavigationView navigationView;

    public static ArrayList<ProgressModel> progressList;
    public static ArrayList<Long> mIds;


    private ActionBarDrawerToggle actionBarDrawerToggle;
    private Unbinder unbinder;
    private boolean isTerminated = true;
    private InterstitialAd interstitialAd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        unbinder = ButterKnife.bind(this);

        MobileAds.initialize(this, "ca-app-pub-9965625406494479~4304101540");

        if (savedInstanceState == null
                && Intent.ACTION_SEND.equals(getIntent().getAction())
                && getIntent().getType() != null
                && getIntent().getType().equals("text/plain")) {
            if (progressList == null) {
                Log.i("statuss", "List RESTARTED");
                progressList = new ArrayList<>();
                mIds = new ArrayList<>();
            } else {
                isTerminated = false;
            }
        } else if (getIntent().getStringExtra("source") != null) {
            isTerminated = false;
        }

        if (isTerminated) {
            Log.i("statuss", "List RESTARTED");
            progressList = new ArrayList<>();
            mIds = new ArrayList<>();
        }
        setSupportActionBar(toolbar);
        checkStoragePermission();
        checkIntentLinkMessage(getIntent());
        setupViewPager();
        setupBottomNavigationView();
        setupNavigationDrawer();

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.i("IntentMsg", "Received Intent");
        checkIntentLinkMessage(intent);
    }

    private void checkIntentLinkMessage(Intent intent) {
        if (Intent.ACTION_SEND.equals(intent.getAction())){
            isTerminated = false;
            String message = intent.getStringExtra(Intent.EXTRA_TEXT);

            Log.i("IntentMsg", message);

            assert message != null;
            if (message.contains("://youtu.be/") || message.contains("youtube.com/watch?v=")){
                navigateToActivityWithExtras(YoutubeActivity.class, message);
            }else if (message.contains("facebook")) {
                navigateToActivityWithExtras(FacebookActivity.class, message);
            } else if (message.contains("instagram")) {
                navigateToActivityWithExtras(InstagramActivity.class, message);
            } else {
                displayToast("Not supported link");
            }
        }


    }


    private void checkStoragePermission() {
        if (!isStoragePermissionGranted(this)) {
            ActivityCompat.requestPermissions(this
                    , new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
    }

    private boolean isStoragePermissionGranted(Activity activity) {
        int result = ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    private void setupViewPager() {
        final PagerAdapter pagerAdapter = new PagerAdapter(getSupportFragmentManager(), 3);
        viewPager.setAdapter(pagerAdapter);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                bottomNavigationView.getMenu().getItem(position).setChecked(true);

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void setupBottomNavigationView() {

        bottomNavigationView.setSelectedItemId(R.id.action_home);

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.action_home:
                    viewPager.setCurrentItem(0);
                    break;
                case R.id.action_progress:
                    viewPager.setCurrentItem(1);
                    break;
                case R.id.action_downloads:
                    viewPager.setCurrentItem(2);
                    break;
            }
            return true;
        });
    }

    private void setupNavigationDrawer() {
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        actionBarDrawerToggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.colorWhite));

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        navigationView.getMenu().getItem(0).setChecked(true);

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            switch (id) {
                case R.id.action_about:
                    //navigateToActivity(ProfileActivity.class);
                    return true;
                case R.id.action_how:
                    navigateToActivity(HowActivity.class);
                    return true;

                case R.id.action_rate:
                    rateApp();
                    return true;

                default:
                    return true;
            }
        });

    }

    private void displayToast(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
    }

    private void navigateToActivityWithExtras(Class<? extends AppCompatActivity> TargetActivity
            , String data) {
        buildInterstitialAd();
        Intent intent = new Intent(HomeActivity.this, TargetActivity);
        intent.putExtra("data", data);
        startActivity(intent);
    }

    private void rateApp() {
        Uri uri = Uri.parse("market://details?id=" + getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName())));
        }
    }

    private void buildInterstitialAd() {
        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
        interstitialAd.loadAd(new AdRequest.Builder().build());

        if (interstitialAd.isLoaded()) {
            interstitialAd.show();
        }
    }

    private void navigateToActivity(Class<? extends AppCompatActivity> TargetActivity) {
        buildInterstitialAd();
        Intent intent = new Intent(HomeActivity.this, TargetActivity);
        startActivity(intent);
    }

    private void uncheckAllDrawerItems() {
        int size = navigationView.getMenu().size();

        for (int i = 0; i < size; i++) {
            navigationView.getMenu().getItem(i).setChecked(false);
        }
    }

    @Override
    protected void onPause() {
        isTerminated = false;
        super.onPause();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults[0] == PackageManager.PERMISSION_DENIED) {

            displayToast("Permission Denied!");
        }


    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (actionBarDrawerToggle.onOptionsItemSelected(item))
            return true;

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            assert imm != null;
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void onBackPressed() {
        if (this.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            navigationView.getMenu().getItem(0).setChecked(false);
            this.drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        if (this.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            this.drawerLayout.closeDrawer(GravityCompat.START);
            uncheckAllDrawerItems();
            navigationView.getMenu().getItem(0).setChecked(true);
            super.onResume();
        } else {
            super.onResume();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
