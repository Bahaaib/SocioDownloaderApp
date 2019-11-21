package com.bahaaapps.sociodownloader.Youtube;


import java.util.ArrayList;

public interface ExtractionListener {

    void onExtractionComplete(ArrayList<VideoFile> videoFiles);

    void onExtractionFailed();

}
