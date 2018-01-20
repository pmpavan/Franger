package com.frangerapp.franger.app.util.di.component;

import com.frangerapp.franger.app.FrangerApp;
import com.frangerapp.franger.app.util.di.module.app.AppModule;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by Pavan on 15/01/18.
 * Component exposing basic singleton dependencies.
 */
@Singleton
@Component(modules = {AppModule.class})
public interface AppComponent {

    void inject(FrangerApp app);
}
