/*
 * Copyright (C) 2016 Cyanogen, Inc.
 */
package org.cyanogenmod.designertools.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import org.cyanogenmod.designertools.R;
import org.cyanogenmod.designertools.utils.PreferenceUtils.GridPreferences;

public class DualColorPicker extends View {
    private static final float STROKE_WIDTH = 5f;
    private static final float COLOR_DARKEN_FACTOR = 0.8f;

    private Paint mPrimaryFillPaint;
    private Paint mSecondaryFillPaint;
    private Paint mPrimaryStrokePaint;
    private Paint mSecondaryStrokePaint;
    private RectF leftHalfOval;
    private RectF rightHalfOval;

    public DualColorPicker(Context context) {
        this(context, null);
    }

    public DualColorPicker(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DualColorPicker(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public DualColorPicker(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.DualColorPicker, 0, 0);
        int primaryColor = ta.getColor(R.styleable.DualColorPicker_primaryColor,
                GridPreferences.getGridLineColor(context, getResources()
                .getColor(R.color.dualColorPickerDefaultPrimaryColor)));
        int secondaryColor = ta.getColor(R.styleable.DualColorPicker_primaryColor,
                GridPreferences.getKeylineColor(context, getResources()
                .getColor(R.color.dualColorPickerDefaultSecondaryColor)));

        mPrimaryFillPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mPrimaryFillPaint.setStyle(Paint.Style.FILL);
        mPrimaryFillPaint.setColor(primaryColor);
        mPrimaryStrokePaint = new Paint(mPrimaryFillPaint);
        mPrimaryStrokePaint.setStyle(Paint.Style.STROKE);
        mPrimaryStrokePaint.setStrokeWidth(STROKE_WIDTH);
        mPrimaryStrokePaint.setColor(getDarkenedColor(primaryColor));

        mSecondaryFillPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mSecondaryFillPaint.setStyle(Paint.Style.FILL);
        mSecondaryFillPaint.setColor(secondaryColor);
        mSecondaryStrokePaint = new Paint(mSecondaryFillPaint);
        mSecondaryStrokePaint.setStyle(Paint.Style.STROKE);
        mSecondaryStrokePaint.setStrokeWidth(STROKE_WIDTH);
        mSecondaryStrokePaint.setColor(getDarkenedColor(secondaryColor));

        leftHalfOval = new RectF();
        rightHalfOval = new RectF();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        final float width = getWidth();
        final float height = getHeight();
        final float centerX = width / 2f;
        final float centerY = height / 2f;
        final float radius = Math.min(centerX, centerY) * 0.9f;

        // erase everything
        canvas.drawColor(0);

        // draw the left half
        leftHalfOval.set(centerX - radius, centerY - radius, centerX + radius, centerY + radius);
        leftHalfOval.offset(-radius * 0.05f, 0);
        canvas.drawArc(leftHalfOval, 90, 180, true, mPrimaryFillPaint);
        leftHalfOval.inset(STROKE_WIDTH / 2, STROKE_WIDTH / 2);
        canvas.drawArc(leftHalfOval, 90, 180, false, mPrimaryStrokePaint);
        canvas.drawLine(centerX - STROKE_WIDTH, centerY - radius + STROKE_WIDTH,
                centerX - STROKE_WIDTH, centerY + radius - STROKE_WIDTH, mPrimaryStrokePaint);

        // draw the right half
        rightHalfOval.set(centerX - radius, centerY - radius, centerX + radius, centerY + radius);
        leftHalfOval.offset(radius * 0.05f, 0);
        canvas.drawArc(rightHalfOval, 270, 180, true, mSecondaryFillPaint);
        rightHalfOval.inset(STROKE_WIDTH / 2, STROKE_WIDTH / 2);
        canvas.drawArc(rightHalfOval, 270, 180, false, mSecondaryStrokePaint);
        canvas.drawLine(centerX + STROKE_WIDTH / 2f, centerY - radius + STROKE_WIDTH,
                centerX + STROKE_WIDTH / 2f, centerY + radius - STROKE_WIDTH, mSecondaryStrokePaint);
    }

    private int getDarkenedColor(int color) {
        int a = Color.alpha(color);
        int r = (int) (Color.red(color) * COLOR_DARKEN_FACTOR);
        int g = (int) (Color.green(color) * COLOR_DARKEN_FACTOR);
        int b = (int) (Color.blue(color) * COLOR_DARKEN_FACTOR);

        return Color.argb(a, r, g, b);
    }

    public void setPrimaryColor(int color) {
        mPrimaryFillPaint.setColor(color);
        mPrimaryStrokePaint.setColor(getDarkenedColor(color));
        invalidate();
    }

    public int getPrimaryColor() {
        return mPrimaryFillPaint.getColor();
    }

    public void setSecondaryColor(int color) {
        mSecondaryFillPaint.setColor(color);
        mSecondaryStrokePaint.setColor(getDarkenedColor(color));
        invalidate();
    }

    public int getSecondaryColor() {
        return mSecondaryFillPaint.getColor();
    }
}
