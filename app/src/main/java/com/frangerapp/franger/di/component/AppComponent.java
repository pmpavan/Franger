package com.frangerapp.franger.di.component;

import com.frangerapp.franger.app.FrangerApp;
import com.frangerapp.franger.di.module.app.AppModule;

import javax.inject.Singleton;

import dagger.BindsInstance;
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
