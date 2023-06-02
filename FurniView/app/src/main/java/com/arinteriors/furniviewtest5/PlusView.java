package com.arinteriors.furniviewtest5;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.core.content.ContextCompat;

public class PlusView extends View {
    private Paint paint;
    private Path path;
    private RectF rectFHorizontal, rectFVertical;
    private float cornerRadius;

    public PlusView(Context context) {
        super(context);
        init(null);
    }

    public PlusView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public PlusView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        paint = new Paint();
        TypedArray typedArray = getContext()
                .obtainStyledAttributes(attrs, R.styleable.PlusView);
        paint.setColor(typedArray.getColor(R.styleable.SuperEllipse_setColor,
                ContextCompat.getColor(this.getContext(), R.color.green)));
        typedArray.recycle();
        paint.setStrokeWidth(10);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        path = new Path();
        rectFHorizontal = new RectF();
        rectFVertical = new RectF();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int size = Math.min(getMeasuredWidth(), getMeasuredHeight());
        cornerRadius = size / 8f;
        int centerX = size / 2;
        int centerY = size / 2;
        int quarterSize = size / 4;
        int halfStrokeWidth = 1;

        rectFHorizontal.set(centerX - quarterSize, centerY - halfStrokeWidth,
                centerX + quarterSize, centerY + halfStrokeWidth);

        rectFVertical.set(centerX - halfStrokeWidth, centerY - quarterSize,
                centerX + halfStrokeWidth, centerY + quarterSize);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        path.reset();
        path.addRoundRect(rectFHorizontal, cornerRadius, cornerRadius, Path.Direction.CW);
        path.addRoundRect(rectFVertical, cornerRadius, cornerRadius, Path.Direction.CW);
        canvas.drawPath(path, paint);
    }
}
