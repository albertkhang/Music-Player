package com.example.demo_19.model;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.ImageView;

import androidx.annotation.Nullable;

@SuppressLint("AppCompatCustomView")
public class RoundCornerImageView extends ImageView {
    public static float radius=20f;

    public RoundCornerImageView(Context context) {
        super(context);
    }

    public RoundCornerImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public RoundCornerImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        RectF rectF=new RectF(0, 0, getWidth(), getHeight());
        Path path=new Path();
        path.addRoundRect(rectF, radius, radius, Path.Direction.CW);
        canvas.clipPath(path);

        super.onDraw(canvas);
    }
}
