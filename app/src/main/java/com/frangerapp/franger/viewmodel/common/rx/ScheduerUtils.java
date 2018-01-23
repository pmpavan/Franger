package com.frangerapp.franger.viewmodel.common.rx;

import io.reactivex.CompletableTransformer;
import io.reactivex.FlowableTransformer;
import io.reactivex.ObservableTransformer;
import io.reactivex.Scheduler;
import io.reactivex.SingleTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Pavan on 15/01/18.
 * A class contains Scheduler util methods.
 */
public class ScheduerUtils {

    /**
     * Transform the Observable to - 1. subscribe in IO thread & 2. observe in Main thread.
     */
    public static <T> ObservableTransformer<T, T> ioToMainObservableScheduler() {
        return upstream -> upstream.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * Transform the Single to - 1. subscribe in IO thread & 2. observe in Main thread.
     */
    public static <T> SingleTransformer<T, T> ioToMainSingleScheduler() {
        return upstream -> upstream.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * Transform the Completable to - 1. subscribe in IO thread & 2. observe in Main thread.
     */
    public static CompletableTransformer ioToMainCompletableScheduler() {
        return upstream -> upstream.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * Transform the Flowable to - 1. subscribe in IO thread & 2. observe in Main thread.
     */
    public static <T> FlowableTransformer<T, T> ioToMainFlowableScheduler() {
        return upstream -> upstream.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * Return IO thread scheduler.
     */
    public static Scheduler getIOThreadScheduler() {
        return Schedulers.io();
    }

    /**
     * Returns Main thread scheduler.
     */
    public static Scheduler getMainThreadScheduler() {
        return AndroidSchedulers.mainThread();
    }
}