package com.bahaaapps.sociodownloader.Facebook;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bahaaapps.sociodownloader.HomeActivity;
import com.bahaaapps.sociodownloader.Instagram.InstagramActivity;
import com.bahaaapps.sociodownloader.Models.ProgressModel;
import com.bahaaapps.sociodownloader.R;
import com.bahaaapps.sociodownloader.Youtube.view.YoutubeActivity;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import static com.bahaaapps.sociodownloader.HomeActivity.progressList;

@SuppressWarnings("ALL")
public class FacebookActivity extends AppCompatActivity {
    WebView webView;
    private ProgressBar mprogress;
    private SwipeRefreshLayout mySwipeRefreshLayout;
    private TextInputEditText urlBar;
    private AppCompatButton goButton;
    private AppCompatButton goBackButton;
    private int isInternetConnected = 1;
    private static final int PERMISSION_CALLBACK_CONSTANT = 101;
    private static final int REQUEST_PERMISSION_SETTING = 102;
    private boolean sentToSettings = false;
    public static ArrayList<String> downloadList = new ArrayList<String>();
    TextInputEditText userUrl;
    private AppBarLayout appBarLayout;
    private RelativeLayout relativeLayout;
    private RelativeLayout searchLayout;
    private TextView fbTitle;
    private AppCompatButton browseFbButton;
    private String videoPath;


    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().requestFeature(Window.FEATURE_PROGRESS);
        setContentView(R.layout.activity_facebook);
        savePermissionState();
        setupToolbar();
        setupWebView();
        checkFacebookLinkMessage(getIntent());


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
        if (message.contains("://youtu.be/") || message.contains("youtube.com/watch?v=")) {
            navigateToActivityWithExtras(YoutubeActivity.class, message);
        } else if (message.contains("facebook")) {
            navigateToActivityWithExtras(FacebookActivity.class, message);
        } else if (message.contains("instagram")) {
            navigateToActivityWithExtras(InstagramActivity.class, message);
        } else {
            displayToast("Not supported link");
        }

    }

    public void clearEditBox(View v) {
        TextInputLayout inputLayoutUrl = findViewById(R.id.fb_input_layout_url);
        hideKeyboard();
        if (!(userUrl.getText().toString().isEmpty()))
            userUrl.setText("");
        else inputLayoutUrl.setError("Field is already Empty ");
    }

    public void pasteURL(View v) {
        hideKeyboard();
        userUrl.setText("");
        ClipboardManager clipBoard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = clipBoard.getPrimaryClip();
        if (clipData != null) {
            ClipData.Item item = clipData.getItemAt(0);
            String clipURL = item.getText().toString();
            if (clipURL.startsWith("https://www.facebook.com")) userUrl.setText(clipURL + "");
            else {
                Snackbar.make(v, "No Facebook Url Found !!", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        }


    }

    public void loadURL(View v) {
        final String url = userUrl.getText().toString();
        if (!url.isEmpty()) {
            webView.loadUrl(url);
            getData(videoPath);
        }
    }

    private void hideKeyboard() {
        try {
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {
        }
    }


    private void navigateToActivityWithExtras(Class<? extends AppCompatActivity> TargetActivity
            , String data) {
        //buildInterstitialAd();
        Intent intent = new Intent(FacebookActivity.this, TargetActivity);
        intent.putExtra("data", data);
        startActivity(intent);
    }

    private void displayToast(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.facebook_toolbar);
        setSupportActionBar(toolbar);
    }

    void goBack() {
        Log.i("Statuss", "Pressed Back");
        Intent intent = new Intent(FacebookActivity.this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);
    }

    void checkFacebookLinkMessage(Intent intent) {
        if (intent.getStringExtra("data") != null) {
            String fbLink = getIntent().getStringExtra("data");

            urlBar.setText(fbLink);
            webView.loadUrl(fbLink);
        }
    }

    private void setupWebView() {
        searchLayout = findViewById(R.id.fb_search_bar);
        fbTitle = findViewById(R.id.fb_title);
        relativeLayout = findViewById(R.id.fb_link_layout);
        appBarLayout = findViewById(R.id.fb_appbar);
        browseFbButton = findViewById(R.id.fb_browse);
        userUrl = findViewById(R.id.fb_user_url);
        urlBar = findViewById(R.id.url_bar);
        goButton = findViewById(R.id.go_button);
        goBackButton = findViewById(R.id.fb_back_button);
        mySwipeRefreshLayout = findViewById(R.id.swipeContainer);
        mprogress = findViewById(R.id.fb_progressBar);
        mprogress.setProgress(0);
        mprogress.setMax(100);
        webView = findViewById(R.id.webView);
        webView.setVisibility(View.INVISIBLE);
        webView.getSettings().setSupportZoom(true);       //Zoom Control on web (You don't need this
        webView.getSettings().setBuiltInZoomControls(true);
        webView.addJavascriptInterface(this, "mJava");
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);


        urlBar.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                urlBar.getText().clear();
                return false;
            }
        });

        goBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //goBack();
                webView.setVisibility(View.INVISIBLE);
                relativeLayout.setVisibility(View.VISIBLE);
                mprogress.setVisibility(View.INVISIBLE);
                fbTitle.setVisibility(View.VISIBLE);
                searchLayout.setVisibility(View.INVISIBLE);

            }
        });

        browseFbButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("fbStatuss", "Browse clicked");
                webView.setVisibility(View.VISIBLE);
                appBarLayout.setVisibility(View.VISIBLE);
                mprogress.setVisibility(View.VISIBLE);
                relativeLayout.setVisibility(View.INVISIBLE);
                fbTitle.setVisibility(View.GONE);
                searchLayout.setVisibility(View.VISIBLE);
            }
        });

        webView.setWebViewClient(new WebViewClient() {
            public void onPageFinished(WebView view, String url) {
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (mprogress.getProgress() == 100) {
                            mprogress.setVisibility(View.INVISIBLE);
                            webView.setVisibility(View.VISIBLE);
                            mySwipeRefreshLayout.setRefreshing(false);
                        }
                        webView.loadUrl("javascript:" +
                                "var e=0;\n" +
                                "window.onscroll=function()\n" +
                                "{\n" +
                                "\tvar ij=document.querySelectorAll(\"video\");\n" +
                                "\t\tfor(var f=0;f<ij.length;f++)\n" +
                                "\t\t{\n" +
                                "\t\t\tif((ij[f].parentNode.querySelectorAll(\"img\")).length==0)\n" +
                                "\t\t\t{\n" +
                                "\t\t\t\tvar nextimageWidth=ij[f].nextSibling.style.width;\n" +
                                "\t\t\t\tvar nextImageHeight=ij[f].nextSibling.style.height;\n" +
                                "\t\t\t\tvar Nxtimgwd=parseInt(nextimageWidth, 10);\n" +
                                "\t\t\t\tvar Nxtimghght=parseInt(nextImageHeight, 10); \n" +
                                "\t\t\t\tvar DOM_img = document.createElement(\"img\");\n" +
                                "\t\t\t\t\tDOM_img.height=\"68\";\n" +
                                "\t\t\t\t\tDOM_img.width=\"68\";\n" +
                                "\t\t\t\t\tDOM_img.style.top=(Nxtimghght/2-20)+\"px\";\n" +
                                "\t\t\t\t\tDOM_img.style.left=(Nxtimgwd/2-20)+\"px\";\n" +
                                "\t\t\t\t\tDOM_img.style.position=\"absolute\";\n" +
                                "\t\t\t\t\tDOM_img.src = \"https://image.ibb.co/kobwsk/one.png\"; \n" +
                                "\t\t\t\t\tij[f].parentNode.appendChild(DOM_img);\n" +
                                "\t\t\t}\t\t\n" +
                                "\t\t\tij[f].remove();\n" +
                                "\t\t} \n" +
                                "\t\t\te++;\n" +
                                "};" +
                                "var a = document.querySelectorAll(\"a[href *= 'video_redirect']\");\n" +
                                "for (var i = 0; i < a.length; i++) {\n" +
                                "    var mainUrl = a[i].getAttribute(\"href\");\n" +
                                "  a[i].removeAttribute(\"href\");\n" +
                                "\tmainUrl=mainUrl.split(\"/video_redirect/?src=\")[1];\n" +
                                "\tmainUrl=mainUrl.split(\"&source\")[0];\n" +
                                "    var threeparent = a[i].parentNode.parentNode.parentNode;\n" +
                                "    threeparent.setAttribute(\"src\", mainUrl);\n" +
                                "    threeparent.onclick = function() {\n" +
                                "        var mainUrl1 = this.getAttribute(\"src\");\n" +
                                "         mJava.getData(mainUrl1);\n" +
                                "    };\n" +
                                "}" +
                                "var k = document.querySelectorAll(\"div[data-store]\");\n" +
                                "for (var j = 0; j < k.length; j++) {\n" +
                                "    var h = k[j].getAttribute(\"data-store\");\n" +
                                "    var g = JSON.parse(h);\nvar jp=k[j].getAttribute(\"data-sigil\");\n" +
                                "    if (g.type === \"video\") {\n" +
                                "if(jp==\"inlineVideo\")" +
                                "{" +
                                "   k[j].removeAttribute(\"data-sigil\");" +
                                "}\n" +
                                "        var url = g.src;\n" +
                                "        k[j].setAttribute(\"src\", g.src);\n" +
                                "        k[j].onclick = function() {\n" +
                                "            var mainUrl = this.getAttribute(\"src\");\n" +
                                "               mJava.getData(mainUrl);\n" +
                                "        };\n" +
                                "    }\n" +
                                "\n" +
                                "}");
                    }
                }, 3000);
            }

            public void onLoadResource(WebView view, String url) {
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        webView.loadUrl("javascript:" +
                                "var e=document.querySelectorAll(\"span\"); " +
                                "if(e[0]!=undefined)" +
                                "{" +
                                "var fbforandroid=e[0].innerText;" +
                                "if(fbforandroid.indexOf(\"Facebook\")!=-1)" +
                                "{ " +
                                "var h =e[0].parentNode.parentNode.parentNode.style.display=\"none\";" +
                                "} " +
                                "}" +
                                "var installfb=document.querySelectorAll(\"a\");\n" +
                                "for (var hardwares = 0; hardwares < installfb.length; hardwares++) \n" +
                                "{\n" +
                                "\tif(installfb[hardwares].text.indexOf(\"Install\")!=-1)\n" +
                                "\t{\n" +
                                "\t\tvar soft=installfb[hardwares].parentNode.style.display=\"none\";\n" +
                                "\n" +
                                "\t}\n" +
                                "}\n");
                        webView.loadUrl("javascript:" +
                                "var e=0;\n" +
                                "window.onscroll=function()\n" +
                                "{\n" +
                                "\tvar ij=document.querySelectorAll(\"video\");\n" +
                                "\t\tfor(var f=0;f<ij.length;f++)\n" +
                                "\t\t{\n" +
                                "\t\t\tif((ij[f].parentNode.querySelectorAll(\"img\")).length==0)\n" +
                                "\t\t\t{\n" +
                                "\t\t\t\tvar nextimageWidth=ij[f].nextSibling.style.width;\n" +
                                "\t\t\t\tvar nextImageHeight=ij[f].nextSibling.style.height;\n" +
                                "\t\t\t\tvar Nxtimgwd=parseInt(nextimageWidth, 10);\n" +
                                "\t\t\t\tvar Nxtimghght=parseInt(nextImageHeight, 10); \n" +
                                "\t\t\t\tvar DOM_img = document.createElement(\"img\");\n" +
                                "\t\t\t\t\tDOM_img.height=\"68\";\n" +
                                "\t\t\t\t\tDOM_img.width=\"68\";\n" +
                                "\t\t\t\t\tDOM_img.style.top=(Nxtimghght/2-20)+\"px\";\n" +
                                "\t\t\t\t\tDOM_img.style.left=(Nxtimgwd/2-20)+\"px\";\n" +
                                "\t\t\t\t\tDOM_img.style.position=\"absolute\";\n" +
                                "\t\t\t\t\tDOM_img.src = \"https://image.ibb.co/kobwsk/one.png\"; \n" +
                                "\t\t\t\t\tij[f].parentNode.appendChild(DOM_img);\n" +
                                "\t\t\t}\t\t\n" +
                                "\t\t\tij[f].remove();\n" +
                                "\t\t} \n" +
                                "\t\t\te++;\n" +
                                "};" +
                                "var a = document.querySelectorAll(\"a[href *= 'video_redirect']\");\n" +
                                "for (var i = 0; i < a.length; i++) {\n" +
                                "    var mainUrl = a[i].getAttribute(\"href\");\n" +
                                "  a[i].removeAttribute(\"href\");\n" +
                                "\tmainUrl=mainUrl.split(\"/video_redirect/?src=\")[1];\n" +
                                "\tmainUrl=mainUrl.split(\"&source\")[0];\n" +
                                "    var threeparent = a[i].parentNode.parentNode.parentNode;\n" +
                                "    threeparent.setAttribute(\"src\", mainUrl);\n" +
                                "    threeparent.onclick = function() {\n" +
                                "        var mainUrl1 = this.getAttribute(\"src\");\n" +
                                "         mJava.getData(mainUrl1);\n" +
                                "    };\n" +
                                "}" +
                                "var k = document.querySelectorAll(\"div[data-store]\");\n" +
                                "for (var j = 0; j < k.length; j++) {\n" +
                                "    var h = k[j].getAttribute(\"data-store\");\n" +
                                "    var g = JSON.parse(h);var jp=k[j].getAttribute(\"data-sigil\");\n" +
                                "    if (g.type === \"video\") {\n" +
                                "if(jp==\"inlineVideo\")" +
                                "{" +
                                "   k[j].removeAttribute(\"data-sigil\");" +
                                "}\n" +
                                "        var url = g.src;\n" +
                                "        k[j].setAttribute(\"src\", g.src);\n" +
                                "        k[j].onclick = function() {\n" +
                                "            var mainUrl = this.getAttribute(\"src\");\n" +
                                "               mJava.setVideoPath(mainUrl);\n" +
                                "               mJava.getData(mainUrl);\n" +
                                "        };\n" +
                                "    }\n" +
                                "\n" +
                                "}");
                    }
                }, 3000);
            }
        });
        webView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                super.onProgressChanged(view, progress);
                if (mprogress.getProgress() < 100) {
                    String currentUrl = webView.getUrl();
                    if (currentUrl != null) {
                        urlBar.setText(webView.getUrl());
                        mprogress.setVisibility(View.VISIBLE);
                        webView.scrollTo(0, 0);
                    }
                }
                mprogress.setProgress(progress);
                setProgress(progress * 100);

            }
        });
        if (isNetworkAvailable()) {
            isInternetConnected = 1;
            webView.loadUrl("https://m.facebook.com/");
            urlBar.setText("https://m.facebook.com/");
            goButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final String requestUrl = urlBar.getText().toString();
                    webView.loadUrl(requestUrl);
                }
            });
        } else {
            isInternetConnected = 0;
            Toast.makeText(getApplicationContext(), "Please Connect to Internet", Toast.LENGTH_LONG).show();
        }
        mySwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                webView.reload();
            }
        });
        webView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == MotionEvent.ACTION_UP) {
                    if (webView.getUrl().equals("https://m.facebook.com/")
                            || webView.getUrl().equals("https://m.facebook.com/home.php")) {
                        Intent intent = new Intent(FacebookActivity.this, HomeActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("source", "fb");
                        startActivity(intent);
                    } else {
                        webView.goBack();
                    }
                }
                return true;
            }
        });

        if (videoPath != null && !videoPath.isEmpty()) {
            Log.i("fbStatuss", videoPath);
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        assert connectivityManager != null;
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    void savePermissionState() {
        SharedPreferences permissionStatus = getSharedPreferences("permissionStatus", MODE_PRIVATE);
        if (isInternetConnected == 0) {
            Snackbar.make(findViewById(android.R.id.content), "Please Connect to Internet", Snackbar.LENGTH_LONG).show();
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                //Show Information about why you need the permission
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Need Storage Permission");
                builder.setMessage("This app needs phone permission.");
                builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        ActivityCompat.requestPermissions(FacebookActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_CALLBACK_CONSTANT);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            } else if (permissionStatus.getBoolean(Manifest.permission.WRITE_EXTERNAL_STORAGE, false)) {
                //Previously Permission Request was cancelled with 'Dont Ask Again',
                // Redirect to Settings after showing Information about why you need the permission
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Need Storage Permission");
                builder.setMessage("This app needs storage permission.");
                builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        sentToSettings = true;
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        startActivityForResult(intent, REQUEST_PERMISSION_SETTING);
                        Toast.makeText(getApplicationContext(), "Go to Permissions to Grant Phone", Toast.LENGTH_LONG).show();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            } else {
                //just request the permission
                ActivityCompat.requestPermissions(FacebookActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_CALLBACK_CONSTANT);
            }
            SharedPreferences.Editor editor = permissionStatus.edit();
            editor.putBoolean(Manifest.permission.WRITE_EXTERNAL_STORAGE, true);
            editor.apply();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CALLBACK_CONSTANT) {
            //check if all permissions are granted
            boolean areAllGranted = false;
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    areAllGranted = true;
                } else {
                    areAllGranted = false;
                    break;
                }
            }
            if (areAllGranted) {
                webView.loadUrl("https://m.facebook.com/");
                urlBar.setText("https://m.facebook.com/");
            } else if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Need Storage Permission");
                builder.setMessage("This app needs phone permission.");
                builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        ActivityCompat.requestPermissions(FacebookActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_CALLBACK_CONSTANT);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            } else {
                Toast.makeText(getApplicationContext(), "Unable to get Permission", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PERMISSION_SETTING) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                //Got Permission
                Toast.makeText(getApplicationContext(), "Permissions Granted", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (sentToSettings) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                //Got Permission
                Toast.makeText(getApplicationContext(), "Permissions Granted", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onPause() {
        if (webView.isShown() == false) {
            webView.stopLoading();
        }
        super.onPause();
    }

    @JavascriptInterface
    public void setVideoPath(String path) {
        videoPath = path;
        Log.i("fbStatuss", videoPath);
    }

    @JavascriptInterface
    public void getData(final String pathvideo) {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Download Video?");
        alertDialog.setMessage("Do you Really want to download Video ?");
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                String finalurl;
                finalurl = pathvideo;
                finalurl = finalurl.replaceAll("%3A", ":");
                finalurl = finalurl.replaceAll("%2F", "/");
                finalurl = finalurl.replaceAll("%3F", "?");
                finalurl = finalurl.replaceAll("%3D", "=");
                finalurl = finalurl.replaceAll("%26", "&");
                downloadvideo(finalurl);
            }


        });
        // Setting Netural "Cancel" Button
//        alertDialog.setNeutralButton("Copy Url", new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int which) {
//                // User pressed Cancel button. Write Logic Here
//                String finalurl;
//                finalurl = pathvideo;
//                finalurl = finalurl.replaceAll("%3A", ":");
//                finalurl = finalurl.replaceAll("%2F", "/");
//                finalurl = finalurl.replaceAll("%3F", "?");
//                finalurl = finalurl.replaceAll("%3D", "=");
//                finalurl = finalurl.replaceAll("%26", "&");
//                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
//                ClipData clip = ClipData.newPlainText("mainurlcopy", finalurl);
//                clipboard.setPrimaryClip(clip);
//                Toast.makeText(getApplicationContext(), "Url Copied", Toast.LENGTH_SHORT).show();
//            }
//        });

        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog.show();
    }

    public void downloadvideo(String pathvideo) {
        if (pathvideo.contains(".mp4")) {
            File socioDir = new File(Environment.DIRECTORY_DOWNLOADS, "Socio Downloader");

            if (!socioDir.exists()) {
                socioDir.mkdir();
            }
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(pathvideo));
            request.allowScanningByMediaScanner();
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

            SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.US);
            Date now = new Date();
            String fileName = "fb-" + formatter.format(now) + ".mp4";

            request.setDestinationInExternalPublicDir(socioDir.getAbsolutePath(), fileName);
            DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
            downloadList.add(pathvideo);
            Long downloadId = dm.enqueue(request);
            Toast.makeText(getApplicationContext(), "Download Started", Toast.LENGTH_LONG).show();

            ProgressModel progress = new ProgressModel();
            progress.setName(fileName);
            progress.setDownloadId(downloadId);
            String path = "/storage/emulated/0/" + socioDir.getPath() + "/" + fileName;
            progress.setPath(path);

            progressList.add(progress);

            finish();

        }
    }

    public ArrayList<String> getList() {
        return downloadList;
    }


}
