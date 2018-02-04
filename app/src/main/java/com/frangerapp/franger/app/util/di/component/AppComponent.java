package com.frangerapp.franger.app.util.di.component;

import com.frangerapp.franger.app.util.di.component.login.LoginComponent;
import com.frangerapp.franger.app.util.di.component.user.UserComponent;
import com.frangerapp.franger.app.util.di.module.app.AppModule;
import com.frangerapp.franger.app.util.di.module.login.LoginModule;
import com.frangerapp.franger.app.util.di.module.user.UserModule;
import com.frangerapp.franger.ui.user.UserBaseActivity;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by Pavan on 15/01/18.
 * Component exposing basic singleton dependencies.
 */
@Singleton
@Component(modules = {AppModule.class})
public interface AppComponent {

    void inject(UserBaseActivity activity);

    LoginComponent plus(LoginModule module);

    UserComponent plus(UserModule module);
}
