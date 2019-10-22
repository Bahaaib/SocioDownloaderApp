package com.bahaa.sociodownloader.Youtube.presenter;

import com.bahaa.sociodownloader.Youtube.ExtractionListener;
import com.bahaa.sociodownloader.Youtube.VideoFile;
import com.bahaa.sociodownloader.Youtube.contracts.Download;
import com.bahaa.sociodownloader.Youtube.model.DownloadModel;

import java.util.ArrayList;


public class DownloadPresenter implements Download.Presenter, ExtractionListener{

    private Download.View downloadView;
    private DownloadModel downloadModel;

    public DownloadPresenter(Download.View downloadView, DownloadModel downloadModel) {
        this.downloadView = downloadView;
        this.downloadModel = downloadModel;
    }

    @Override
    public void validateInputLink(String link) {
        if (link.isEmpty()) {
            downloadView.showErrorMessage();
        } else {
            validateYouTubeLinkFormat(link);
        }
    }

    @Override
    public void validateYouTubeLinkFormat(String link) {
        if (link.contains("://youtu.be/") || link.contains("youtube.com/watch?v=")) {
            downloadModel.beginLinkExtraction(link, this);
            downloadView.showProgressDialog();
        } else {
            downloadView.showErrorMessage();
        }
    }

    @Override
    public void buildButtonsText(ArrayList<VideoFile> videoFiles) {
        String buttonText;
        ArrayList<VideoFile> fileList = new ArrayList<>();

        for (VideoFile file : videoFiles) {
            if (file.getFile().getFormat().getHeight() == -1) {
                buttonText = "Audio ";
                buttonText = buttonText.concat(String.valueOf(file.getFile().getFormat().getAudioBitrate()));
                buttonText = buttonText.concat("kbit/s");
            } else {
                buttonText = String.valueOf(file.getFile().getFormat().getHeight());
                buttonText = buttonText.concat("p");
            }

            if (file.getFile().getFormat().isDashContainer()) {
                buttonText = buttonText.concat(" [DASH]");
            }
            VideoFile vFile = new VideoFile();
            vFile.setButtonText(buttonText);
            vFile.setMetaTitle(file.getMetaTitle());
            vFile.setFile(file.getFile());

            fileList.add(vFile);
        }

        buildFileNames(fileList);
    }

    @Override
    public void buildFileNames(ArrayList<VideoFile> videoFiles) {

        String fileName;
        ArrayList<VideoFile> fileList = new ArrayList<>();

        for (VideoFile file : videoFiles) {
            //In case of link manipulated manually and corrupted..
            if (file.getMetaTitle() == null) {
                downloadView.showUnexpectedError();
                break;
            }
            if (file.getMetaTitle().length() > 55) {
                fileName = file.getMetaTitle().substring(0, 55) + ".";
                fileName = fileName.concat(file.getFile().getFormat().getExt());
            } else {
                fileName = file.getMetaTitle() + "." + file.getFile().getFormat().getExt();
            }

            fileName = fileName.replaceAll("[\\\\><\"|*?%:#/]", "");

            VideoFile videoFile = new VideoFile();
            videoFile.setFile(file.getFile());
            videoFile.setMetaTitle(file.getMetaTitle());
            videoFile.setButtonText(file.getButtonText());
            videoFile.setFileName(fileName);

            fileList.add(videoFile);
        }

        downloadView.addQualityButtons(fileList);
        downloadView.dismissProgressDialog();

    }

    @Override
    public void beginDownload(String url, String metadata, String filename) {
        downloadModel.downloadFromUrl(url, metadata, filename);
    }


    @Override
    public void onExtractionComplete(ArrayList<VideoFile> videoFiles) {
        buildButtonsText(videoFiles);
    }

    @Override
    public void onExtractionFailed() {
        downloadView.showNoVideoToast();
        downloadView.dismissProgressDialog();
    }
}
