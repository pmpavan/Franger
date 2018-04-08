package com.frangerapp.franger.ui.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.ImageView;

import com.frangerapp.franger.data.util.FRUtils;

import java.util.Locale;

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

    public static int getColorBase(String text) {
        String color = "#99DBA0";
        if (!TextUtils.isEmpty(text)) {
            String s = text.substring(0, 1);
            switch (s.toLowerCase()) {
                case "b":
                case "l":
                case "q":
                case "v":
                case "g":
                    color = "#99BEDB";
                    break;
                case "c":
                case "h":
                case "m":
                case "r":
                case "w":
                    color = "#BEB290";
                    break;
                case "d":
                case "i":
                case "n":
                case "s":
                case "x":
                    color = "#B8B8B8";
                    break;
                case "e":
                case "j":
                case "o":
                case "t":
                case "y":
                    color = "#ACA2C9";
                    break;
            }
        }
//        FDLogger.msg("color --> " + color);
        return Color.parseColor(color);
    }


    public static String toCamelcase(String text) {
        StringBuilder captializeText = new StringBuilder();
        String[] strArray = text.split("\\s");

        for (String aStrArray : strArray) {
            text = aStrArray.trim();
            if (text.length() > 0) {
                text = String.valueOf(text.charAt(0)).toUpperCase(
                        Locale.getDefault())
                        + text.subSequence(1, text.length());
                captializeText.append(text);
                captializeText.append(" ");
            }
        }
        return captializeText.toString().trim();
    }

    public static void setImageInCircularView(Context context, Bitmap bm, int boundBoxInDp, ImageView imageView) {
        float width = bm.getWidth();
        float height = bm.getHeight();

//        System.out.println("bitmap width => " + bm.getWidth());
//        System.out.println("bitmap height => " + bm.getHeight());

        int boxInPx = dpAsPixels(context, boundBoxInDp);
//        System.out.println("boxInPx => " + boxInPx);
        float scale;

        if (width >= height) {
            scale = (height / width);
//            System.out.println("scale => " + scale);
            width = boxInPx;
            height = (int) (width * scale);

        } else {
            scale = (width / height);
//            System.out.println("scale => " + scale);
            height = boxInPx;
            width = (int) (height * scale);
        }

//        System.out.println("width => " + width);
//        System.out.println("height => " + height);

        Bitmap resizedBmp = Bitmap.createScaledBitmap(bm, (int) width, (int) height, true);
        Bitmap roundedBmp = getRoundedRectBitmap(resizedBmp, (int) width, (int) height);
        imageView.setImageBitmap(roundedBmp);
        imageView.invalidate();
    }

    private static Bitmap getRoundedRectBitmap(Bitmap bitmap, int boxInPx, int bmHeight) {
        Bitmap result = null;
        try {
            result = Bitmap.createBitmap(boxInPx, boxInPx, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(result);

            Paint paint = new Paint();

            Rect srcRect = new Rect(0, 0, boxInPx, bmHeight);
            Rect desRect = new Rect(0, (boxInPx / 2 - bmHeight / 2), boxInPx, (boxInPx / 2 + bmHeight / 2));

            paint.setAntiAlias(true);
            canvas.drawARGB(0, 0, 0, 0);
            paint.setColor(Color.WHITE);

            canvas.drawCircle(boxInPx / 2 + 0.7f,
                    boxInPx / 2 + 0.7f,
                    boxInPx / 2 + 0.1f, paint);

            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            canvas.drawBitmap(bitmap, srcRect, desRect, paint);

        } catch (NullPointerException e) {
        } catch (OutOfMemoryError o) {
        }
        return result;
    }

    public static Bitmap getBitmapFromColor(int color, int width, int height) {
        Paint p = new Paint();
        p.setDither(true);
        p.setColor(color);

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawRect(new RectF(0, 0, width, height), p);
        return bitmap;
    }

    public static int dpAsPixels(Context context, int sizeInDp) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (sizeInDp * scale + 0.5f);
    }
}
