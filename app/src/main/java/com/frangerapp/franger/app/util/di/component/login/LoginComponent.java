package com.frangerapp.franger.app.util.di.component.login;

import com.frangerapp.franger.app.util.di.module.login.CountriesListModule;
import com.frangerapp.franger.app.util.di.module.login.LoginModule;
import com.frangerapp.franger.app.util.di.module.login.SignUpModule;
import com.frangerapp.franger.app.util.di.module.login.SplashModule;
import com.frangerapp.franger.app.util.di.module.login.VerifyUserModule;
import com.frangerapp.franger.app.util.di.scope.ActivityScope;
import com.frangerapp.franger.app.util.di.scope.FragmentScope;
import com.frangerapp.franger.app.util.di.scope.LoginScope;
import com.frangerapp.franger.ui.countries.CountriesListDialogFragment;
import com.frangerapp.franger.ui.login.LoginActivity;
import com.frangerapp.franger.ui.login.VerifyUserActivity;
import com.frangerapp.franger.ui.splash.SplashActivity;

import org.jetbrains.annotations.NotNull;

import dagger.Subcomponent;

/**
 * Created by Pavan on 24/01/18.
 */
@LoginScope
@Subcomponent(modules = {LoginModule.class})
public interface LoginComponent {
    @NotNull
    SplashComponent plus(@NotNull SplashModule splashModule);

    @NotNull
    SignUpComponent plus(@NotNull SignUpModule signUpModule);

    @NotNull
    VerifyUserComponent plus(@NotNull VerifyUserModule verifyUserModule);


    CountriesListComponent plus(CountriesListModule module);

    @ActivityScope
    @Subcomponent(modules = SplashModule.class)
    interface SplashComponent {
        void inject(SplashActivity activity);
    }

    @ActivityScope
    @Subcomponent(modules = SignUpModule.class)
    interface SignUpComponent {
        void inject(LoginActivity activity);
    }

    @ActivityScope
    @Subcomponent(modules = VerifyUserModule.class)
    interface VerifyUserComponent {
        void inject(VerifyUserActivity activity);
    }

    @FragmentScope
    @Subcomponent(modules = CountriesListModule.class)
    interface CountriesListComponent {
        void inject(CountriesListDialogFragment activity);
    }
}
