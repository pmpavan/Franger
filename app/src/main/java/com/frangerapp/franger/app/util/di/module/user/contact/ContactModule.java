package com.frangerapp.franger.app.util.di.module.user.contact;

import android.app.Activity;
import android.support.annotation.NonNull;

import com.frangerapp.franger.ui.contact.ContactActivity;

import dagger.Module;

/**
 * Created by pavanm on 10/03/18.
 */
@Module
public class ContactModule {
    private final Activity activity;

    public ContactModule(@NonNull ContactActivity activity) {
        this.activity = activity;
    }
}
