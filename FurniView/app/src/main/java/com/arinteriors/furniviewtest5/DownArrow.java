package com.arinteriors.furniviewtest5;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class DownArrow extends View {
    private Paint paint;
    private Path path;

    public DownArrow(Context context) {
        super(context);
        init(null);
    }

    public DownArrow(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public DownArrow(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.DownArrow);
        paint.setColor(typedArray.getColor(R.styleable.DownArrow_fillColorArrow, getResources().getColor(R.color.green)));
        typedArray.recycle();
        paint.setStyle(Paint.Style.FILL);
        path = new Path();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;
        int size = Math.min(getWidth(), getHeight());
        int strokeWidth = 12;
        int halfStrokeWidth = strokeWidth / 2;

        float cornerRadius = size / 8;

        RectF rectFVertical = new RectF(centerX - halfStrokeWidth, centerY - size / 4, centerX + halfStrokeWidth, centerY + size / 4);

        path.reset();
        path.addRoundRect(rectFVertical, cornerRadius, cornerRadius, Path.Direction.CW);

        canvas.drawPath(path, paint);

        path.reset();

        path.moveTo((float) centerX, (float) (centerY + size / 6.5));
        path.lineTo((float) (getWidth() * 0.7), centerY);
        path.lineTo((float) (getWidth() * 0.74), centerY + size / 15);
        path.lineTo((float) (centerX), (float) (centerY + size / 4));
        canvas.drawPath(path, paint);

        path.reset();

        path.moveTo((float) centerX, (float) (centerY + size / 6.5));
        path.lineTo((float) (getWidth() * 0.3), centerY);
        path.lineTo((float) (getWidth() * 0.26), centerY + size / 15);
        path.lineTo((float) (centerX), (float) (centerY + size / 4));
        canvas.drawPath(path, paint);
    }
}
