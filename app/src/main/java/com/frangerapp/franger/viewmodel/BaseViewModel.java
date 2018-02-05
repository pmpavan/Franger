package com.frangerapp.franger.viewmodel;

import android.arch.lifecycle.ViewModel;

import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by Pavan on 20/01/18.
 */

public class BaseViewModel extends ViewModel {

    public CompositeDisposable disposables = new CompositeDisposable();


    public void onViewDestroyed(){
        disposables.dispose();
    }

}
