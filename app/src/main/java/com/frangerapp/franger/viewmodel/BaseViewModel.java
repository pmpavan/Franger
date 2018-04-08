package com.frangerapp.franger.viewmodel;

import android.arch.lifecycle.ViewModel;

import com.frangerapp.franger.viewmodel.common.BaseView;

import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by Pavan on 20/01/18.
 */

public class BaseViewModel extends ViewModel implements BaseView {

    public CompositeDisposable disposables = new CompositeDisposable();


    @Override
    public void onViewDestroyed() {
        if (disposables != null && !disposables.isDisposed())
            disposables.dispose();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        onViewDestroyed();
    }
}
