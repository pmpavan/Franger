package com.frangerapp.franger.ui.util;

import android.widget.EditText;

/**
 * Created by Pavan on 21/01/18.
 */

public class UiUtils {

    public static boolean isNullOrEmpty(String string) {
        return string == null || string.isEmpty();
    }

    public static void setEditTextAsClickable(EditText editText) {
        editText.setSingleLine();
        editText.setFocusable(false);
        editText.setSelected(false);
        editText.setFocusableInTouchMode(false);
        editText.setCursorVisible(false);
        editText.setClickable(true);
    }

}
