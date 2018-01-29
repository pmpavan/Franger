package com.frangerapp.franger.app.util.di.component.user;

import com.frangerapp.franger.app.util.di.module.user.UserModule;
import com.frangerapp.franger.app.util.di.module.user.home.HomeModule;
import com.frangerapp.franger.app.util.di.scope.ActivityScope;
import com.frangerapp.franger.app.util.di.scope.UserScope;
import com.frangerapp.franger.ui.home.HomeActivity;

import dagger.Subcomponent;

/**
 * Created by Pavan on 24/01/18.
 */
@UserScope
@Subcomponent(modules = {UserModule.class})
public interface UserComponent {


    HomeComponent plus(HomeModule module);


    /**
     * HOME
     */
    @ActivityScope
    @Subcomponent(modules = HomeModule.class)
    interface HomeComponent {
        void inject(HomeActivity activity);
    }
}
