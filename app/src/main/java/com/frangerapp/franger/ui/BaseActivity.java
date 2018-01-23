package com.frangerapp.franger.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

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
        }    }
}
