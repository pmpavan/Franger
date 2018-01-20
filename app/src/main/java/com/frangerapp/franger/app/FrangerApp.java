package com.frangerapp.franger.app;

import android.app.Application;
import android.content.Context;

import com.crashlytics.android.core.CrashlyticsCore;
import com.franger.mobile.logger.FRLogger;
import com.frangerapp.franger.BuildConfig;
import com.frangerapp.franger.app.util.AppConstants;
import com.frangerapp.franger.app.util.di.module.app.AppModule;

import io.fabric.sdk.android.Fabric;

/**
 * Created by Pavan on 15/01/18.
 */

public class FrangerApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        initFabric();

//        DaggerAppComponent
//                .builder()
//                .appModule(new AppModule(this))
//                .application(this)
//                .build()
//                .inject(this);

        enableLogger();
    }

    public static FrangerApp get(Context context) {
        return (FrangerApp) context.getApplicationContext();
    }

    private void initFabric() {
        CrashlyticsCore core = new CrashlyticsCore.Builder()
                .disabled(BuildConfig.DEBUG)
                .build();
        Fabric.with(this, core);
    }

    private void enableLogger() {
        FRLogger.setSwitch(AppConstants.IS_LOGGER_ENABLED);
    }

}
