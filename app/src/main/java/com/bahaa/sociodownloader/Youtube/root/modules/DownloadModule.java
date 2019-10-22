package com.bahaa.sociodownloader.Youtube.root.modules;

import android.content.Context;

import com.bahaa.sociodownloader.Youtube.contracts.Download;
import com.bahaa.sociodownloader.Youtube.model.DownloadModel;
import com.bahaa.sociodownloader.Youtube.presenter.DownloadPresenter;

import dagger.Module;
import dagger.Provides;

@Module
public class DownloadModule {

    private Download.View downloadView;

    public DownloadModule(Download.View downloadView) {
        this.downloadView = downloadView;
    }

    @Provides
    public Download.View provideView() {
        return downloadView;
    }

    @Provides
    public DownloadModel provideModel(Context context) {
        return new DownloadModel(context);
    }

    @Provides
    public Download.Presenter providePresenter(Download.View view, DownloadModel model) {
        return new DownloadPresenter(view, model);
    }
}
