package com.arinteriors.furniviewtest5;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import androidx.core.content.ContextCompat;

public class SuperEllipse extends View {
    private static final float EXPONENT = 5.0f;
    private static final float PI_DOUBLE = (float) (2 * Math.PI);
    private static final float ANGLE_INCREMENT = PI_DOUBLE / 360;

    private Path mSuperellipsePath;
    private Paint mSuperellipsePaint;
    private int color;

    public SuperEllipse(Context context) {
        super(context);
        init(null);
    }

    public SuperEllipse(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public SuperEllipse(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        if (mSuperellipsePath == null)
            mSuperellipsePath = new Path();

        if (mSuperellipsePaint == null) {
            mSuperellipsePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mSuperellipsePaint.setStyle(Paint.Style.FILL_AND_STROKE);
            mSuperellipsePaint.setStrokeWidth(5);
        }

        TypedArray typedArray = getContext()
                .obtainStyledAttributes(attrs, R.styleable.SuperEllipse);
        color = typedArray.getColor(R.styleable.SuperEllipse_setColor,
                ContextCompat.getColor(this.getContext(), R.color.green));
        typedArray.recycle();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float width = getWidth();
        float height = getHeight();
        float centerX = width / 2;
        float centerY = height / 2;
        float a = width / 2; // Horizontal radius
        float b = height / 2; // Vertical radius

        mSuperellipsePath.reset();

        float angle = 0;
        while (angle <= PI_DOUBLE) {
            float cosValue = (float) Math.cos(angle);
            float sinValue = (float) Math.sin(angle);

            float x = (float) (Math.pow(Math.abs(cosValue), 2 / EXPONENT) * a);
            float y = (float) (Math.pow(Math.abs(sinValue), 2 / EXPONENT) * b);

            float pointX = centerX + (Math.signum(cosValue) * x);
            float pointY = centerY + (Math.signum(sinValue) * y);

            if (angle == 0) {
                mSuperellipsePath.moveTo(pointX, pointY);
            } else {
                mSuperellipsePath.lineTo(pointX, pointY);
            }

            angle += ANGLE_INCREMENT;
        }

        mSuperellipsePaint.setColor(color);
        canvas.drawPath(mSuperellipsePath, mSuperellipsePaint);
    }
}

