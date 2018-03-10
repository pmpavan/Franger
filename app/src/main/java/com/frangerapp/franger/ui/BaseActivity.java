package com.frangerapp.franger.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Window;
import android.view.WindowManager;

import com.frangerapp.franger.R;

/**
 * Created by Pavan on 20/01/18.
 */

@SuppressLint("Registered")
public class BaseActivity extends AppCompatActivity {

    public int getColorRes(int colorId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return getResources().getColor(colorId, this.getTheme());
        } else {
            return getResources().getColor(colorId);
        }
    }

    public void manageStatusBar(int statusbarColor) {
//        FDLogger.msg("build version => " + android.os.Build.VERSION.SDK_INT);
        if (Build.VERSION.SDK_INT > 19) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                window.setStatusBarColor(statusbarColor);
            }
        }
    }

    public void setHomeIndicatorButton(int drawable) {
        getSupportActionBar().setHomeAsUpIndicator(drawable);
    }

    public int getColorById(int id) {
        return ContextCompat.getColor(this, id);
    }

    public String getStringValue(int id) {
        return getResources().getString(id);
    }

}
