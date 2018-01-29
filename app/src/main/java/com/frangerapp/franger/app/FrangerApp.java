package com.frangerapp.franger.app;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;

import com.crashlytics.android.core.CrashlyticsCore;
import com.franger.mobile.logger.FRLogger;
import com.frangerapp.franger.BuildConfig;
import com.frangerapp.franger.app.util.AppConstants;
import com.frangerapp.franger.app.util.di.component.AppComponent;
import com.frangerapp.franger.app.util.di.component.DaggerAppComponent;
import com.frangerapp.franger.app.util.di.component.login.LoginComponent;
import com.frangerapp.franger.app.util.di.component.user.UserComponent;
import com.frangerapp.franger.app.util.di.module.app.AppModule;
import com.frangerapp.franger.app.util.di.module.login.LoginModule;
import com.frangerapp.franger.app.util.di.module.user.UserModule;
import com.frangerapp.franger.domain.user.model.User;

import org.jetbrains.annotations.NotNull;

import io.fabric.sdk.android.Fabric;

/**
 * Created by Pavan on 15/01/18.
 */

public class FrangerApp extends Application {

    private AppComponent appComponent;
    private LoginComponent loginComponent;
    private UserComponent userComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        initFabric();


        appComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .build();

//        DaggerAppComponent
//                .builder()
//                .appModule(new AppModule(this))
//                .build()
//                .inject(this);

        enableLogger();
    }

    public static FrangerApp get(Context context) {
        return (FrangerApp) context.getApplicationContext();
    }

    public AppComponent appComponent() {
        return appComponent;
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

    public UserComponent createUserComponent(@NonNull User user) {
        userComponent = appComponent.plus(new UserModule(user));
        return userComponent;
    }

    public void releaseUserComponent() {
        userComponent = null;
    }

    public UserComponent userComponent() {
        return userComponent;
    }

    public LoginComponent createLoginComponent() {
        loginComponent = appComponent.plus(new LoginModule());
        return loginComponent;
    }

    public void releaseLoginComponent() {
        loginComponent = null;
    }

    public LoginComponent loginComponent() {
        return loginComponent;
    }
}
