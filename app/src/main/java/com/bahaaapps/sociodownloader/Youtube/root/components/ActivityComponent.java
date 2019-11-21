package com.bahaaapps.sociodownloader.Youtube.root.components;

import com.bahaaapps.sociodownloader.Youtube.contracts.Download;
import com.bahaaapps.sociodownloader.Youtube.root.modules.AppModule;
import com.bahaaapps.sociodownloader.Youtube.root.modules.DownloadModule;
import com.bahaaapps.sociodownloader.Youtube.root.scopes.DownloadActivityScope;
import com.bahaaapps.sociodownloader.Youtube.view.YoutubeActivity;

import dagger.Component;

@DownloadActivityScope
@Component(dependencies = {AppComponent.class}, modules = {DownloadModule.class, AppModule.class})
public interface ActivityComponent {

    void inject(YoutubeActivity target);

    Download.Presenter provideDownloadPresenter();
}
