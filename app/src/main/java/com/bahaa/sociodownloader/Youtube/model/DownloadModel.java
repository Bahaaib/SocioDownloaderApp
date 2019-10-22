package com.bahaa.sociodownloader.Youtube.model;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.util.SparseArray;

import com.bahaa.sociodownloader.Models.ProgressModel;
import com.bahaa.sociodownloader.Youtube.ExtractionListener;
import com.bahaa.sociodownloader.Youtube.VideoFile;
import com.bahaa.sociodownloader.Youtube.contracts.Download;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

import javax.inject.Inject;
import javax.inject.Named;

import at.huber.youtubeExtractor.VideoMeta;
import at.huber.youtubeExtractor.YouTubeExtractor;
import at.huber.youtubeExtractor.YtFile;

import static com.bahaa.sociodownloader.HomeActivity.progressList;


public class DownloadModel implements Download.Model {

    private WeakReference<Context> weakReference;
    private ArrayList<VideoFile> videoFiles = new ArrayList<>();

    @Inject
    public DownloadModel(@Named("Application Context") Context context) {
        weakReference = new WeakReference<>(context);
    }


    @SuppressLint("StaticFieldLeak")
    @Override
    public void beginLinkExtraction(String link, final ExtractionListener listener) {
        new YouTubeExtractor(weakReference.get()) {
            @Override
            protected void onExtractionComplete(SparseArray<YtFile> ytFiles, VideoMeta videoMeta) {
                videoFiles.clear();
                if (ytFiles == null) {
                    listener.onExtractionFailed();
                } else {
                    for (int i = 0, itag; i < ytFiles.size(); i++) {
                        itag = ytFiles.keyAt(i);
                        YtFile ytFile = ytFiles.get(itag);

                        if (ytFile.getFormat().getHeight() == -1 || ytFile.getFormat().getHeight() >= 360) {
                            VideoFile file = new VideoFile();
                            file.setFile(ytFile);
                            file.setMetaTitle(videoMeta.getTitle());
                            videoFiles.add(file);
                        }
                    }
                    listener.onExtractionComplete(videoFiles);
                }

            }
        }.extract(link, true, false);
    }

    @Override
    public void downloadFromUrl(String url, String metadata, String filename) {
        Uri uri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(uri);

        File socioDir = new File(Environment.DIRECTORY_DOWNLOADS, "Socio Downloader");

        if (!socioDir.exists()) {
            socioDir.mkdir();
        }


        request.setTitle(metadata);
        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(socioDir.getAbsolutePath(), filename);

        DownloadManager manager = (DownloadManager) weakReference.get().getSystemService(Context.DOWNLOAD_SERVICE);
        Long downloadId = manager.enqueue(request);

        ProgressModel progress = new ProgressModel();
        progress.setName(filename);
        progress.setDownloadId(downloadId);
        String path = "/storage/emulated/0/" + socioDir.getPath() + "/" + filename;
        progress.setPath(path);

        progressList.add(progress);
    }
}
