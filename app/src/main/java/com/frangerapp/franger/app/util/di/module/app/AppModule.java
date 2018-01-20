package com.frangerapp.franger.app.util.di.module.app;

import android.content.Context;

import com.frangerapp.franger.app.FrangerApp;
import com.frangerapp.franger.data.common.util.FSHttpResponseValidator;
import com.frangerapp.network.HttpClient;
import com.frangerapp.network.HttpResponseValidator;
import com.frangerapp.network.volley.VolleyHttpClientImpl;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

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
    Context application() {
        return app;
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
        return new FSHttpResponseValidator();
    }

    @Singleton
    @Provides
    HttpClient httpClient(Context context, Gson jsonParser, HttpResponseValidator httpResponseValidator) {
        return new VolleyHttpClientImpl(context, jsonParser, httpResponseValidator);
    }
}
