package com.bahaa.sociodownloader.Youtube.root.modules;

import android.content.Context;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

@Module
public class ContextModule {

    private Context context;


    public ContextModule(Context context) {
        this.context = context;
    }


    @Provides
    public Context provideContext() {
        return context;
    }

    @Provides
    @Named("Application Context")
    public Context provideAppContext() {
        return context;
    }


}
