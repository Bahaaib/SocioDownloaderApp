package com.bahaa.sociodownloader.Youtube;


import java.util.ArrayList;

public interface ExtractionListener {

    void onExtractionComplete(ArrayList<VideoFile> videoFiles);

    void onExtractionFailed();

}
