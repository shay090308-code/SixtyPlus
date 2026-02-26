package com.example.sixtyplus.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class CircularProgressView extends View {

    private Paint backgroundPaint;
    private Paint progressPaint;
    private Paint textPaint;

    private float progress = 0f;
    private float totalHours = 0f;
    private static final float MAX_HOURS = 60f;
    private RectF rectF;

    public CircularProgressView(Context context) {
        super(context);
        init();
    }

    public CircularProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CircularProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        backgroundPaint.setColor(Color.parseColor("#E0E0E0"));
        backgroundPaint.setStyle(Paint.Style.STROKE);
        backgroundPaint.setStrokeWidth(30f);

        progressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        progressPaint.setColor(Color.parseColor("#F0D238"));
        progressPaint.setStyle(Paint.Style.STROKE);
        progressPaint.setStrokeWidth(30f);
        progressPaint.setStrokeCap(Paint.Cap.ROUND);

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(72f);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setFakeBoldText(true);

        rectF = new RectF();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float width = getWidth();
        float height = getHeight();
        float radius = Math.min(width, height) / 2f - 40f;
        float cx = width / 2f;
        float cy = height / 2f;

        rectF.set(cx - radius, cy - radius, cx + radius, cy + radius);

        canvas.drawArc(rectF, -90, 360, false, backgroundPaint);

        float sweepAngle = (progress / 100f) * 360f;
        canvas.drawArc(rectF, -90, sweepAngle, false, progressPaint);

        int percent = (int) Math.min(progress, 100);
        canvas.drawText(percent + "%", cx, cy + 25f, textPaint);
    }

    public void setHours(float hours) {
        this.totalHours = Math.min(hours, MAX_HOURS);
        this.progress = (totalHours / MAX_HOURS) * 100f;
        invalidate();
    }

    public float getTotalHours() {
        return totalHours;
    }
}