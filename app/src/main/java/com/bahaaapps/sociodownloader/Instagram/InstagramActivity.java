package com.bahaaapps.sociodownloader.Instagram;

import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.print.PrintHelper;

import com.bahaaapps.sociodownloader.Facebook.FacebookActivity;
import com.bahaaapps.sociodownloader.Models.ProgressModel;
import com.bahaaapps.sociodownloader.R;
import com.bahaaapps.sociodownloader.Youtube.view.YoutubeActivity;
import com.bumptech.glide.Glide;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.bahaaapps.sociodownloader.HomeActivity.progressList;

@SuppressWarnings("ALL")
public class InstagramActivity extends AppCompatActivity {
    private Menu menu;
    private EditText userUrl;
    private TextInputLayout inputLayoutUrl;
    private ProgressDialog bar;
    private CardView imageShowLayout, videoShowLayout;
    private ImageView image;
    private ImageButton printImage, downloadImage, shareImage, downloadVideo, shareVideo, playVideo;
    private VideoView video;
    private boolean hasPlayed = false;
    private String url;
    private File downloadFileName, cache;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instagram);
        final Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        //It will ignore URI exposure
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        inputLayoutUrl = findViewById(R.id.input_layout_url);
        userUrl = (EditText) findViewById(R.id.user_url);
        userUrl.addTextChangedListener(new MyTextWatcher(userUrl));
        videoShowLayout = (CardView) findViewById(R.id.videoShowLayout);
        imageShowLayout = (CardView) findViewById(R.id.imageShowLayout);

        printImage = (ImageButton) findViewById(R.id.printImage);
        downloadImage = (ImageButton) findViewById(R.id.downloadImage);
        shareImage = (ImageButton) findViewById(R.id.shareImage);
        downloadVideo = (ImageButton) findViewById(R.id.downloadVideo);
        shareVideo = (ImageButton) findViewById(R.id.shareVideo);
        playVideo = (ImageButton) findViewById(R.id.playVideo);

        image = (ImageView) findViewById(R.id.image);
        video = (VideoView) findViewById(R.id.video);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String appPackage = "com.instagram.android";
                try {
                    Intent i = getPackageManager().getLaunchIntentForPackage(appPackage);
                    startActivity(i);
                } catch (Exception e) {
                    Toast.makeText(InstagramActivity.this, "Sorry, Instagram App Not Found", Toast.LENGTH_LONG).show();
                }
            }
        });

        AppBarLayout mAppBarLayout = findViewById(R.id.app_bar);
        mAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    isShow = true;
                    showOption(R.id.instagram);
                } else if (isShow) {
                    isShow = false;
                    hideOption(R.id.instagram);
                }
            }
        });

        checkInstagramLinkMessage(getIntent());

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

    private void navigateToActivityWithExtras(Class<? extends AppCompatActivity> TargetActivity
            , String data) {
        //buildInterstitialAd();
        Intent intent = new Intent(InstagramActivity.this, TargetActivity);
        intent.putExtra("data", data);
        startActivity(intent);
    }

    private void displayToast(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
    }

    void checkInstagramLinkMessage(Intent intent) {
        if (intent.getStringExtra("data") != null) {
            String igLink = getIntent().getStringExtra("data");

            userUrl.setText(igLink);
            performLoading();
        }
    }

    public void clearEditBox(View v) {
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
        if (clipData!= null) {
            ClipData.Item item = clipData.getItemAt(0);
            String clipURL = item.getText().toString();
            if (clipURL.startsWith("https://www.instagram.com")) userUrl.setText(clipURL + "");
            else {
                Snackbar.make(v, "No Instagram Url Found !!", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }

        }


    }

    public void loadURL(View v) {
        performLoading();
    }


    private void performLoading() {
        hideKeyboard();
        if (!validateUrl()) {
            return;
        }
        bar = new ProgressDialog(InstagramActivity.this, R.style.MaterialAlertDialogStyle);
        bar.setTitle("Connecting Instagram...");
        bar.setMessage("Please Wait ... ");
        bar.setCancelable(false);
        bar.show();
        getUrlFromSourceCode();
    }

    private void getUrlFromSourceCode() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final StringBuilder builder = new StringBuilder();
                try {
                    Document doc = Jsoup.connect(userUrl.getText().toString().trim()).get();
                    Elements links = doc.select("meta[property=og:video]");
                    //checking whether it is video or image
                    if (links.isEmpty()) {
                        links = doc.select("meta[property=og:image]");
                        for (Element link : links) {
                            builder.append(link.attr("content"));
                        }
                    } else {
                        for (Element link : links) {
                            builder.append(link.attr("content"));
                        }
                    }
                } catch (IOException e) {
                }
                url = builder.toString().trim();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (bar.isShowing()) {
                            imageShowLayout.setVisibility(View.GONE);
                            videoShowLayout.setVisibility(View.GONE);
                            if (builder.toString().contains(".jpg")) {
                                imageShowLayout.setVisibility(View.VISIBLE);
                                Glide.with(InstagramActivity.this).load(builder.toString()).fitCenter().into(image);
                                try {
                                    Thread.sleep(2000);
                                } catch (Exception e) {
                                }

                            } else {
                                Uri uri = Uri.parse(builder.toString());
                                video.setVideoURI(uri);
                                video.requestFocus();
                                videoShowLayout.setVisibility(View.VISIBLE);
                                try {
                                    Thread.sleep(5000);
                                } catch (Exception e) {
                                }

                            }
                        }
                        bar.dismiss();
                    }

                });
            }
        }).start();
    }


    public void startPlayingVideo(View view) {
        if (video.getDuration() == -1) {
            Toast.makeText(InstagramActivity.this, "Nothing to play", Toast.LENGTH_SHORT)
                    .show();
        } else {
            if (hasPlayed) {
                if (video.isPlaying()) {
                    playVideo.setImageResource(R.drawable.ic_play);
                    video.pause();
                    hasPlayed = false;
                }

            } else {
                playVideo.setImageResource(R.drawable.ic_pause);
                video.start();
                hasPlayed = true;
            }


        }
    }

    public void downloadVideoToSDCard(View view) {
        SimpleDateFormat sd = new SimpleDateFormat("yymmhh");
        String date = sd.format(new Date());
        final String name = "Ig-video-" + date + ".mp4";
        cache = getApplicationContext().getExternalCacheDir();
        downloadFileName = new File(cache, name);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL u = new URL(url);
                    downloadFromUrl(url, name, "Instagram Video");

                    URLConnection conn = u.openConnection();
                    int contentLength = conn.getContentLength();

                    DataInputStream stream = new DataInputStream(u.openStream());

                    byte[] buffer = new byte[contentLength];
                    stream.readFully(buffer);
                    stream.close();

                    DataOutputStream fos = new DataOutputStream(new FileOutputStream(downloadFileName));
                    fos.write(buffer);
                    fos.flush();
                    fos.close();

                } catch (FileNotFoundException e) {
                    Toast.makeText(InstagramActivity.this, "File Not Found. Try again later", Toast.LENGTH_SHORT).show();
                    return; // swallow a 404
                } catch (IOException e) {
                    Toast.makeText(InstagramActivity.this, "Server Error. Try again later", Toast.LENGTH_SHORT).show();
                    return; // swallow a 404
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(InstagramActivity.this, "Download Completed", Toast.LENGTH_LONG).show();
                    }
                });
            }
        }).start();

    }

    public void shareVideo(View view) {
        final ProgressDialog downloadingBar = new ProgressDialog(InstagramActivity.this);
        downloadingBar.setTitle("Please wait");
        downloadingBar.setMessage("Preparing file for sharing..");
        downloadingBar.setCancelable(false);
        downloadingBar.show();

        cache = getApplicationContext().getExternalCacheDir();
        downloadFileName = new File(cache, "InstagramSharedFile.mp4");
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL u = new URL(url);
                    URLConnection conn = u.openConnection();
                    int contentLength = conn.getContentLength();

                    DataInputStream stream = new DataInputStream(u.openStream());

                    byte[] buffer = new byte[contentLength];
                    stream.readFully(buffer);
                    stream.close();

                    DataOutputStream fos = new DataOutputStream(new FileOutputStream(downloadFileName));
                    fos.write(buffer);
                    fos.flush();
                    fos.close();
                    downloadingBar.dismiss();
                } catch (FileNotFoundException e) {
                    Toast.makeText(InstagramActivity.this, "File Not Found. Try again later", Toast.LENGTH_SHORT).show();
                    return; // swallow a 404
                } catch (IOException e) {
                    Toast.makeText(InstagramActivity.this, "Server Error. Try again later", Toast.LENGTH_SHORT).show();
                    return; // swallow a 404
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        Intent share = new Intent(android.content.Intent.ACTION_SEND);
                        share.setType("video/*");
                        share.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + downloadFileName));
                        share.putExtra(Intent.EXTRA_TEXT, "Hey Want to share & Print Photos/video from Instagram.Check this application " + "https://play.google.com/store/apps/details?id=" + getPackageName());
                        startActivity(Intent.createChooser(share, "Share This Video with"));
                    }
                });
            }
        }).start();
    }

    public void PrintImage(View view) {
        if (image.getDrawable() != null) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) image.getDrawable();
            Bitmap bitmap = bitmapDrawable.getBitmap();
            // Save this bitmap to a file.
            File cache = getApplicationContext().getExternalCacheDir();
            File sharefile = new File(cache, "InstagramSharedFile.png");
            try {
                FileOutputStream out = new FileOutputStream(sharefile);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                out.flush();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            PrintHelper photoPrinter = new PrintHelper(InstagramActivity.this);
            photoPrinter.setScaleMode(PrintHelper.SCALE_MODE_FIT);
            photoPrinter.printBitmap("InstagramSharedFile", bitmap);
            sharefile.delete();
        } else
            Toast.makeText(InstagramActivity.this, "No photo to print", Toast.LENGTH_SHORT).show();
    }

    public void DownloadImage(View view) {
        SimpleDateFormat sd = new SimpleDateFormat("yymmhh");
        String date = sd.format(new Date());
        String name = "Ig-photo-" + date + ".png";
        cache = getApplicationContext().getExternalCacheDir();
        downloadFileName = new File(cache, name);

        if (image.getDrawable() != null) {

            BitmapDrawable bitmapDrawable = (BitmapDrawable) image.getDrawable();
            Bitmap bitmap = bitmapDrawable.getBitmap();
            try {
                downloadFromUrl(url, name, "Instagram Photo");
                FileOutputStream out = new FileOutputStream(downloadFileName);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                out.flush();
                out.close();
                Toast.makeText(InstagramActivity.this, "Download Completed", Toast.LENGTH_LONG).show();

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else
            Toast.makeText(InstagramActivity.this, "No photo to Download ..", Toast.LENGTH_SHORT).show();


    }

    public void ShareImage(View view) {
        if (image.getDrawable() != null) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) image.getDrawable();
            Bitmap bitmap = bitmapDrawable.getBitmap();
            // Save this bitmap to a file.
            File cache = getApplicationContext().getExternalCacheDir();
            File sharefile = new File(cache, "InstagramShare.png");
            try {
                FileOutputStream out = new FileOutputStream(sharefile);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                out.flush();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Intent share = new Intent(android.content.Intent.ACTION_SEND);
            share.setType("image/*");
            share.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + sharefile));
            share.putExtra(Intent.EXTRA_TEXT, "Hey Want to share & Print Photo and video from Instagram.Check this application " + "https://play.google.com/store/apps/details?id=" + getPackageName());
            startActivity(Intent.createChooser(share, "Share This photo with"));

        } else
            Toast.makeText(InstagramActivity.this, "There is no photo to share", Toast.LENGTH_SHORT).show();
    }

    private boolean validateUrl() {
        if (userUrl.getText().toString().trim().isEmpty()) {
            inputLayoutUrl.setError("Link cannot be Empty!");
            requestFocus(userUrl);
            return false;
        } else if (!userUrl.getText().toString().trim().contains("https://www.instagram.com/")) {
            inputLayoutUrl.setError("Invalid Instagram URL!");
            requestFocus(userUrl);
            return false;
        } else {
            inputLayoutUrl.setErrorEnabled(false);
        }
        return true;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private class MyTextWatcher implements TextWatcher {

        private View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.user_url:
                    validateUrl();
                    break;
            }
        }
    }

    private void hideKeyboard() {
        try {
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        this.menu = menu;
        getMenuInflater().inflate(R.menu.menu_scrolling, menu);
        hideOption(R.id.instagram);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.instagram) {
            String appPackage = "com.instagram.android";
            try {
                Intent i = getPackageManager().getLaunchIntentForPackage(appPackage);
                startActivity(i);
            } catch (Exception e) {
                Toast.makeText(this, "Instagram Not Found. Install it first!", Toast.LENGTH_LONG).show();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void downloadFromUrl(String url, String metadata, String filename) {
        Uri uri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(uri);

        File socioDir = new File(Environment.DIRECTORY_DOWNLOADS, "Socio Downloader");

        if (!socioDir.exists()) {
            socioDir.mkdir();
        }

        request.setTitle(metadata);
        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(socioDir.getAbsolutePath(), metadata);

        DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        Long downloadId = manager.enqueue(request);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(InstagramActivity.this, "Download started", Toast.LENGTH_LONG).show();
            }
        });

        ProgressModel progress = new ProgressModel();
        progress.setName(metadata);
        progress.setDownloadId(downloadId);
        String path = "/storage/emulated/0/" + socioDir.getPath() + "/" + metadata;
        progress.setPath(path);

        progressList.add(progress);

        finish();
    }

    private void hideOption(int id) {
        MenuItem item = menu.findItem(id);
        item.setVisible(false);
    }

    private void showOption(int id) {
        MenuItem item = menu.findItem(id);
        item.setVisible(true);
    }
}



