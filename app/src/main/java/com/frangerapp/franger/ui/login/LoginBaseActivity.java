package com.frangerapp.franger.ui.login;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.frangerapp.franger.app.FrangerApp;
import com.frangerapp.franger.ui.BaseActivity;
import com.frangerapp.franger.ui.home.HomeActivity;

/**
 * Created by Pavan on 24/01/18.
 */

public class LoginBaseActivity extends BaseActivity {

    // ACTIVITY METHODS.
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        createLoginComponentIfNeeded();
    }

    // PROTECTED METHODS.
    protected void goToHomeScreenAndFinishAllLoginActivities() {
        releaseLoginComponent();
        releaseUserComponent();

//        Intent intent = HomeActivity.getIntent(this)
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        startActivity(intent);
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
//            finishAffinity();
//        } else {
//            finish();
//        }
    }

    // PRIVATE METHODS.
    private void createLoginComponentIfNeeded() {
        if (FrangerApp.get(this).loginComponent() == null) {
            FrangerApp.get(this).createLoginComponent();
        }
    }

    private void releaseLoginComponent() {
        FrangerApp.get(this).releaseLoginComponent();
    }

    private void releaseUserComponent() {
        FrangerApp.get(this).releaseUserComponent();
    }
}
