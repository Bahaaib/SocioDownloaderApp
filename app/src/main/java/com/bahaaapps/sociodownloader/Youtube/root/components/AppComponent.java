package com.bahaaapps.sociodownloader.Youtube.root.components;

import android.app.Application;
import android.content.Context;

import com.bahaaapps.sociodownloader.Youtube.ApplicationInstance;
import com.bahaaapps.sociodownloader.Youtube.root.modules.AppModule;
import com.bahaaapps.sociodownloader.Youtube.root.modules.ContextModule;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {AppModule.class, ContextModule.class})
public interface AppComponent {

    void inject(ApplicationInstance applicationInstance);

    Context getContext();

    Application getApplication();

}
