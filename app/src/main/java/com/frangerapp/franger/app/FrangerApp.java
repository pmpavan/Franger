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
import com.frangerapp.network.HttpClientException;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.SocketException;

import io.fabric.sdk.android.Fabric;
import io.reactivex.exceptions.UndeliverableException;
import io.reactivex.plugins.RxJavaPlugins;

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

        initializeRXGlobalErrorConsumer();
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


    /**
     * Used to initialize RXJava global error consumer.
     * <p>
     * 1. One important design requirement for 2.x is that no Throwable errors should be swallowed.
     * This means errors that can't be emitted because the downstream's lifecycle already reached its terminal state or
     * the downstream cancelled a sequence which was about to emit an error.
     * 2. i.e for example - If we dispose a observable which is doing a background work (like making API request),
     * Then the thread which is doing background work is interrupted hence it throws InterruptedException,
     * Since our subscription has been disposed, this exception goes to default RXJava error handler (RxJavaPlugins.onError) which crashes our application,
     * Hence we are overriding default errorHandler & handling those exception.
     * <p>
     * Reference
     * 1. https://github.com/ReactiveX/RxJava/wiki/What's-different-in-2.0#error-handling
     * 2. https://github.com/ReactiveX/RxJava/issues/4880
     */
    private void initializeRXGlobalErrorConsumer() {
        RxJavaPlugins.setErrorHandler(e -> {
            if (e instanceof UndeliverableException) {
                e = e.getCause();
            }

            if (e instanceof HttpClientException) {
                // Our HttpClient wraps the actual exception.
                e = e.getCause();
            }

            if ((e instanceof IOException) || (e instanceof SocketException)) {
                // fine, irrelevant network problem or API that throws on cancellation
                return;
            }
            if (e instanceof InterruptedException) {
                // fine, some blocking code was interrupted by a dispose call
                return;
            } else {
                // Can be Undeliverable exception received.
                // Or, likely a bug in the application
                // Or, a bug in RxJava or in a custom operator
                // Throw the error.
                Thread.currentThread().getUncaughtExceptionHandler()
                        .uncaughtException(Thread.currentThread(), e);
            }
        });
    }
}
