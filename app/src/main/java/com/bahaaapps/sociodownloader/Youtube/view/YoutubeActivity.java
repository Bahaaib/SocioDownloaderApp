package com.bahaaapps.sociodownloader.Youtube.view;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bahaaapps.sociodownloader.Facebook.FacebookActivity;
import com.bahaaapps.sociodownloader.Instagram.InstagramActivity;
import com.bahaaapps.sociodownloader.R;
import com.bahaaapps.sociodownloader.Youtube.ApplicationInstance;
import com.bahaaapps.sociodownloader.Youtube.VideoFile;
import com.bahaaapps.sociodownloader.Youtube.contracts.Download;
import com.bahaaapps.sociodownloader.Youtube.receiver.ConnectivityReceiver;
import com.bahaaapps.sociodownloader.Youtube.root.components.DaggerActivityComponent;
import com.bahaaapps.sociodownloader.Youtube.root.modules.DownloadModule;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindDrawable;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class YoutubeActivity extends AppCompatActivity implements Download.View
        , ConnectivityReceiver.ConnectivityReceiverListener {

    @Inject
    Download.Presenter presenter;

    @Inject
    Context context;

    @BindView(R.id.link_input_layout)
    TextInputLayout linkInputLayout;

    @BindView(R.id.link_text)
    TextInputEditText linkEditText;

    @BindView(R.id.download_btn)
    AppCompatButton downloadButton;

    @BindView(R.id.main_layout)
    LinearLayout linearLayout;

    @BindDrawable(R.drawable.button_background)
    Drawable buttonBackground;

    @BindView(R.id.network_state)
    TextView networkStateText;

    private VideoFile tempFile;
    private ProgressDialog progressDialog;
    private Unbinder unbinder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ytdownloader);

        DaggerActivityComponent.builder()
                .appComponent(ApplicationInstance.get(this).getComponent())
                .downloadModule(new DownloadModule(this))
                .build()
                .inject(this);

        initViews();
        checkYouTubeLinkMessage(getIntent());

    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.i("IntentMsg", "Received Intent");
        checkIntentLinkMessage(intent);
    }

    private void checkIntentLinkMessage(Intent intent) {
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

    private void navigateToActivityWithExtras(Class<? extends AppCompatActivity> TargetActivity
            , String data) {
        //buildInterstitialAd();
        Intent intent = new Intent(YoutubeActivity.this, TargetActivity);
        intent.putExtra("data", data);
        startActivity(intent);
    }

    @OnClick(R.id.download_btn)
    void pressButton() {
        String link = linkEditText.getText().toString();
        if (isNetworkAvailable()) {
            presenter.validateInputLink(link);
        } else {
            displayToast("No Network");
        }
    }

    void initViews() {
        unbinder = ButterKnife.bind(this);
    }

    void checkYouTubeLinkMessage(Intent intent) {
        if (intent.getStringExtra("data") != null) {
            String ytLink = getIntent().getStringExtra("data");

            linkEditText.setText(ytLink);
            linkInputLayout.requestFocus();

            presenter.validateInputLink(ytLink);


        }
    }

    int getDP(float pixels) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                pixels,
                context.getResources().getDisplayMetrics());
    }

    private boolean isStoragePermissionGranted() {
        int result = ContextCompat.checkSelfPermission(YoutubeActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(YoutubeActivity.this
                , new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
    }

    private void displayToast(String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();

    }

    private void watchText(TextInputEditText editText, TextInputLayout textInputLayout) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                textInputLayout.setErrorEnabled(false);

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    @Override
    public void showProgressDialog() {
        progressDialog = new ProgressDialog(this, R.style.MaterialAlertDialogStyle);
        progressDialog.setMessage("Fetching Available Quality..");
        progressDialog.show();
    }

    @Override
    public void dismissProgressDialog() {
        progressDialog.dismiss();
    }

    @Override
    public void addQualityButtons(ArrayList<VideoFile> fileList) {

        if (linearLayout.getChildCount() > 0) {
            linearLayout.removeAllViews();
        }

        for (VideoFile file : fileList) {
            Button button = new Button(this);
            button.setBackground(buttonBackground);
            button.setText(file.getButtonText());
            button.setTextColor(getResources().getColor(R.color.colorPrimaryLight));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(getDP(10), getDP(10), getDP(10), getDP(10));
            button.setLayoutParams(params);

            button.setOnClickListener(v -> {
                if (!isStoragePermissionGranted()) {
                    requestStoragePermission();
                    tempFile = file;
                } else {
                    if (isNetworkAvailable()) {
                        presenter.beginDownload(
                                file.getFile().getUrl(),
                                file.getMetaTitle(),
                                file.getFileName());
                        displayToast("Download started");
                        finish();
                    } else {
                        displayToast("Network Disconnected");
                    }

                }


            });


            linearLayout.addView(button);
        }
    }

    @Override
    public void showErrorMessage() {
        linkInputLayout.setErrorEnabled(true);
        linkInputLayout.setError("Invalid YouTube link");
        watchText(linkEditText, linkInputLayout);
    }

    @Override
    public void showUnexpectedError() {
        linearLayout.removeAllViews();
        displayToast("Unexpected Error");
    }

    @Override
    public void showNoVideoToast() {
        linearLayout.removeAllViews();
        displayToast("Video NOT Found");
    }

    @Override
    protected void onResume() {
        Log.i("IntentMsg", "YT onResume Called");
        super.onResume();

        ApplicationInstance.get(context).setConnectivityListener(this);
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
       /* if (isConnected) {
            networkStateText.setText("Connected");
            networkStateText.setTextColor(Color.WHITE);
            networkStateText.setBackgroundColor(Color.GREEN);
           // networkStateText.postDelayed(() ->
                 //   networkStateText.setVisibility(View.INVISIBLE), 1000);
        } else {
            networkStateText.setVisibility(View.VISIBLE);
            networkStateText.setText("Waiting for network...");
            networkStateText.setTextColor(Color.parseColor("#707070"));
            networkStateText.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        }*/


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
            displayToast("Permission Denied!");
        } else {
            if (isNetworkAvailable()) {
                presenter.beginDownload(
                        tempFile.getFile().getUrl(),
                        tempFile.getMetaTitle(),
                        tempFile.getFileName());
                displayToast("Download started");
            } else {
                displayToast("Network Disconnected");
            }

        }
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
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

}
