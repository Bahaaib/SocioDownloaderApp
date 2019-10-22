package com.bahaa.sociodownloader.Youtube;

import android.app.Application;
import android.content.Context;

import com.bahaa.sociodownloader.Youtube.receiver.ConnectivityReceiver;
import com.bahaa.sociodownloader.Youtube.root.components.AppComponent;
import com.bahaa.sociodownloader.Youtube.root.components.DaggerAppComponent;
import com.bahaa.sociodownloader.Youtube.root.modules.AppModule;
import com.bahaa.sociodownloader.Youtube.root.modules.ContextModule;


public class ApplicationInstance extends Application {
    private AppComponent component;


    public static ApplicationInstance get(Context context) {
        return (ApplicationInstance) context.getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        component = DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .contextModule(new ContextModule(this))
                .build();

    }

    public AppComponent getComponent() {
        return component;
    }

    public void setConnectivityListener(ConnectivityReceiver.ConnectivityReceiverListener listener) {
        ConnectivityReceiver.connectivityReceiverListener = listener;
    }
}
