package com.frangerapp.franger.app.util.di.module.app;

import android.content.Context;
import android.support.annotation.NonNull;

import com.frangerapp.franger.app.ChatStore;
import com.frangerapp.franger.app.FrangerApp;
import com.frangerapp.franger.app.UserManager;
import com.frangerapp.franger.app.util.db.AppDatabase;
import com.frangerapp.franger.data.common.AppStore;
import com.frangerapp.franger.data.common.UserStore;
import com.frangerapp.franger.data.common.util.FRHttpResponseValidator;
import com.frangerapp.network.HttpClient;
import com.frangerapp.network.HttpResponseValidator;
import com.frangerapp.network.volley.VolleyHttpClientImpl;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.greenrobot.eventbus.EventBus;

import java.util.Random;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Pavan on 15/01/18.
 * All app level dependencies.
 */
@Module
public class AppModule {

    private FrangerApp app;

    public AppModule(FrangerApp app) {
        this.app = app;
    }

    @Singleton
    @Provides
    FrangerApp frangerApp() {
        return app;
    }

    @Singleton
    @Provides
    Context application() {
        return app;
    }

    @Singleton
    @Provides
    AppStore appStore(@NonNull Context context) {
        return new AppStore(context);
    }

    @Singleton
    @Provides
    UserStore userStore(Context context, Gson gson) {
        return new UserStore(context, gson);
    }

    @Singleton
    @Provides
    Random random() {
        return new Random();
    }

    @Singleton
    @Provides
    ChatStore chatStore(Context context, Gson gson) {
        return new ChatStore(context, gson);
    }

    @Singleton
    @Provides
    UserManager userManager(Context context, UserStore userStore) {
        return new UserManager(context, userStore);
    }

    @Singleton
    @Provides
    EventBus eventBus() {
        return EventBus.getDefault();
    }

    @Singleton
    @Provides
    Gson gson() {
        return new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
    }

    @Singleton
    @Provides
    GsonBuilder gsonbuilder() {
        return new GsonBuilder();
    }

    @Singleton
    @Provides
    HttpResponseValidator httpResponseValidator() {
        return new FRHttpResponseValidator();
    }

    @Singleton
    @Provides
    HttpClient httpClient(Context context, Gson jsonParser, HttpResponseValidator httpResponseValidator) {
        return new VolleyHttpClientImpl(context, jsonParser, httpResponseValidator);
    }

    @Provides
    @Singleton
    AppDatabase getDatabase(FrangerApp app) {
        // TODO https://android.jlelse.eu/introduction-to-android-architecture-components-with-kotlin-room-livedata-1839c17597e
        return AppDatabase.getDatabase(app);
    }

}
