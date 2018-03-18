package com.frangerapp.franger.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

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

    public void manageActionBarWithTitle(Toolbar toolbar, String title) {
        manageActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        setActionBarTitle(actionBar, title, getColorById(R.color.white));
    }

    public void manageActionBarWithTitle(Toolbar toolbar, String title, int colorId) {
        manageActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        setActionBarTitle(actionBar, title, colorId);
    }

    public void setActionBarTitle(ActionBar actionBar, String title, int colorId) {
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);

        LayoutInflater inflator = LayoutInflater.from(this);
        View v = inflator.inflate(R.layout.toolbar_title_layout, null);
        TextView titleTxt = v.findViewById(R.id.filter_txt);
        titleTxt.setTextColor(colorId);
        titleTxt.setText(title);
        actionBar.setCustomView(v);
    }

    public void manageActionBar(Toolbar toolbar) {
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.show();
        actionBar.setElevation(2);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_24dp);
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
