package com.frangerapp.franger.viewmodel.common;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.content.Context;
import android.databinding.ObservableField;
import android.support.annotation.NonNull;

import com.frangerapp.franger.viewmodel.BaseViewModel;
import com.frangerapp.franger.viewmodel.common.eventbus.AlertDialogEvent;
import com.frangerapp.franger.viewmodel.common.eventbus.EventBusConstants;

import org.greenrobot.eventbus.EventBus;

public class AlertDialogViewModel extends BaseViewModel {

    private EventBus eventBus;
    private Context context;
    private String title, message, okStr, cancelStr;

    public ObservableField<String> messageTxt, yesTxt, noTxt;

    public AlertDialogViewModel(String message, String okStr, String cancelStr) {
        this.message = message;
        this.okStr = okStr;
        this.cancelStr = cancelStr;

        init();
    }

    public AlertDialogViewModel(Context context, EventBus eventBus) {
        this.context = context;
        this.eventBus = eventBus;
    }

    private void init() {
        messageTxt = new ObservableField<>();
        yesTxt = new ObservableField<>();
        noTxt = new ObservableField<>();

        messageTxt.set(message);
        yesTxt.set(okStr);
        noTxt.set(cancelStr);
    }

    public void onDialogNoClicked() {
        AlertDialogEvent event = new AlertDialogEvent();
        event.setId(EventBusConstants.DIALOG_NO_CLICKED);
        EventBus.getDefault().post(event);
    }

    public void onDialogYesClicked() {
        AlertDialogEvent event = new AlertDialogEvent();
        event.setId(EventBusConstants.DIALOG_YES_CLICKED);
        EventBus.getDefault().post(event);
    }

    public static class Factory implements ViewModelProvider.Factory {

        private EventBus eventBus;
        private Context context;

        public Factory(Context context, EventBus eventBus) {
            this.context = context;
            this.eventBus = eventBus;

        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            if (modelClass.isAssignableFrom(AlertDialogViewModel.class)) {
                return (T) new AlertDialogViewModel(context, eventBus);
            }
            throw new IllegalArgumentException("Unknown class name");
        }
    }

}
