package com.bahaa.sociodownloader.Youtube.root.components;

import com.bahaa.sociodownloader.Youtube.contracts.Download;
import com.bahaa.sociodownloader.Youtube.root.modules.AppModule;
import com.bahaa.sociodownloader.Youtube.root.modules.DownloadModule;
import com.bahaa.sociodownloader.Youtube.root.scopes.DownloadActivityScope;
import com.bahaa.sociodownloader.Youtube.view.YoutubeActivity;

import dagger.Component;

@DownloadActivityScope
@Component(dependencies = {AppComponent.class}, modules = {DownloadModule.class, AppModule.class})
public interface ActivityComponent {

    void inject(YoutubeActivity target);

    Download.Presenter provideDownloadPresenter();
}
