package com.bahaa.sociodownloader.Youtube.root.modules;

import android.app.Application;

import com.bahaa.sociodownloader.Youtube.ApplicationInstance;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class AppModule {
    private ApplicationInstance applicationInstance;

    public AppModule(ApplicationInstance applicationInstance) {
        this.applicationInstance = applicationInstance;
    }

    @Singleton
    @Provides
    public Application provideApplication() {
        return applicationInstance;
    }


}
