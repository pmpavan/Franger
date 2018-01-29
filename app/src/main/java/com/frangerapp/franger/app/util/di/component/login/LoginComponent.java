package com.frangerapp.franger.app.util.di.component.login;

import com.frangerapp.franger.app.util.di.module.login.LoginModule;
import com.frangerapp.franger.app.util.di.module.login.SplashModule;
import com.frangerapp.franger.app.util.di.scope.ActivityScope;
import com.frangerapp.franger.app.util.di.scope.LoginScope;
import com.frangerapp.franger.ui.splash.SplashActivity;

import org.jetbrains.annotations.NotNull;

import javax.inject.Singleton;

import dagger.Subcomponent;

/**
 * Created by Pavan on 24/01/18.
 */
@LoginScope
@Subcomponent(modules = {LoginModule.class})
public interface LoginComponent {
    @NotNull
    SplashComponent plus(@NotNull SplashModule splashModule);

    @ActivityScope
    @Subcomponent(modules = SplashModule.class)
    interface SplashComponent {
        void inject(SplashActivity activity);
    }
}
