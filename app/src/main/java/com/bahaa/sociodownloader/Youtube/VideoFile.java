package com.bahaa.sociodownloader.Youtube;

import at.huber.youtubeExtractor.YtFile;

public class VideoFile {

    private String metaTitle;

    private String ButtonText;

    private YtFile file;

    private String fileName;

    public VideoFile() {
    }

    public String getMetaTitle() {
        return metaTitle;
    }

    public void setMetaTitle(String metaTitle) {
        this.metaTitle = metaTitle;
    }

    public String getButtonText() {
        return ButtonText;
    }

    public void setButtonText(String buttonText) {
        ButtonText = buttonText;
    }

    public YtFile getFile() {
        return file;
    }

    public void setFile(YtFile file) {
        this.file = file;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
