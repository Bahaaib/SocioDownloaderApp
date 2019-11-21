package com.bahaaapps.sociodownloader.Youtube.contracts;

import com.bahaaapps.sociodownloader.Youtube.ExtractionListener;
import com.bahaaapps.sociodownloader.Youtube.VideoFile;

import java.util.ArrayList;


public interface Download {

    interface View {

        void showProgressDialog();

        void dismissProgressDialog();

        void showErrorMessage();

        void showNoVideoToast();

        void showUnexpectedError();

        void addQualityButtons(ArrayList<VideoFile> titles);
    }

    interface Presenter {

        void validateInputLink(String link);

        void validateYouTubeLinkFormat(String link);

        void buildButtonsText(ArrayList<VideoFile> videoFiles);

        void buildFileNames(ArrayList<VideoFile> videoFiles);

        void beginDownload(String url, String metadata, String filename);
    }

    interface Model {

        void beginLinkExtraction(String link, final ExtractionListener listener);

        void downloadFromUrl(String url, String metadata, String filename);

    }
}
