package com.frangerapp.franger.app.util.di.module.user.common;

import android.arch.lifecycle.ViewModelProvider;
import android.content.Context;
import android.support.annotation.NonNull;

import com.frangerapp.franger.app.util.di.scope.FragmentScope;
import com.frangerapp.franger.ui.common.AlertDialogFragment;
import com.frangerapp.franger.viewmodel.common.AlertDialogViewModel;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;

import dagger.Module;
import dagger.Provides;

@Module
public class AlertDialogModule {

    public AlertDialogModule(@NonNull AlertDialogFragment activity) {
    }

    @Provides
    @FragmentScope
    ViewModelProvider.Factory alertDialogViewModel(@NonNull Context context, @NotNull EventBus eventBus) {
        return new AlertDialogViewModel.Factory(context, eventBus);
    }
}