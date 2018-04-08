package com.frangerapp.ui.utils;

import android.content.Context;
import android.support.v4.content.ContextCompat;

public class UiUtils {

    public static int getColorById(Context context, int id) {
        return ContextCompat.getColor(context, id);
    }
}
