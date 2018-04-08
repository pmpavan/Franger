package com.frangerapp.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;

import com.frangerapp.ui.utils.UiUtils;

public class CircularTextDrawableView extends CircularImageView {

    private String text;
    private boolean isTextDrawable = false;
    private int fontSize = 35;

    private int viewHeight;
    private int viewWidth;

    private Context context;

    public CircularTextDrawableView(Context context) {
        super(context);
        this.context = context;
    }

    public CircularTextDrawableView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public CircularTextDrawableView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
    }

    public void setTextDrawable(Bitmap bitmap, String text) {
        setImageBitmap(bitmap);
        this.text = text;
        this.isTextDrawable = true;
    }

    public boolean isTextDrawable() {
        return isTextDrawable;
    }

    public void setIsTextDrawable(boolean isTextDrawable) {
        this.isTextDrawable = isTextDrawable;
    }

    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = measureWidth(widthMeasureSpec);
        int height = measureHeight(heightMeasureSpec);

        viewWidth = width;
        viewHeight = height;

        invalidate();
        setMeasuredDimension(width, height);
    }

    private int measureWidth(int measureSpec) {
        int result;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else {
            result = viewWidth;
        }
        return result;
    }

    private int measureHeight(int measureSpecHeight) {
        int result;
        int specMode = MeasureSpec.getMode(measureSpecHeight);
        int specSize = MeasureSpec.getSize(measureSpecHeight);

        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else {
            result = viewHeight;
        }
        return result;
    }


    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (text != null && !text.isEmpty())
            drawText(canvas);
    }

    private void drawText(Canvas canvas) {
        if (isTextDrawable) {
            // draw text
            Paint textPaint = new Paint();
            textPaint.setColor(UiUtils.getColorById(context, R.color.avatar_text));
            textPaint.setTextSize(fontSize);
            textPaint.setAntiAlias(true);
            textPaint.setStyle(Paint.Style.FILL);
            textPaint.setTextAlign(Paint.Align.CENTER);
//            Typeface typeface = Typeface.createFromAsset(getContext().getAssets(), "fonts/Roboto-Bold.ttf");
//            textPaint.setTypeface(typeface);
            canvas.drawText(text, viewWidth / 2, viewHeight / 2 - ((textPaint.descent() + textPaint.ascent()) / 2), textPaint);
        }
    }
}
