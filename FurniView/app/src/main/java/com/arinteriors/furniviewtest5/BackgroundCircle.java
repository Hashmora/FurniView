package com.arinteriors.furniviewtest5;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

public class BackgroundCircle extends View {
    private Paint paint;
    private Path circlePath;

    public BackgroundCircle(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public BackgroundCircle(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BackgroundCircle(Context context) {
        this(context, null);
    }

    private void init() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL);

        circlePath = new Path();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();
        int radius = Math.min(width, height) / 2;

        circlePath.reset();
        circlePath.addCircle((float) width / 2, (float) height / 2, radius, Path.Direction.CW);

        paint.setColor(Color.WHITE);

        canvas.drawPath(circlePath, paint);
    }
}

