package com.example.sixtyplus.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import androidx.core.content.res.ResourcesCompat;
import android.graphics.Typeface;

import com.example.sixtyplus.R;

public class CircularProgressView extends View {

    private Paint backgroundPaint;
    private Paint progressPaint;
    private Paint glowPaint;
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
        backgroundPaint.setColor(Color.parseColor("#F0F0F0"));
        backgroundPaint.setStyle(Paint.Style.STROKE);
        backgroundPaint.setStrokeWidth(45f);

        glowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        glowPaint.setColor(Color.parseColor("#33F0D238"));
        glowPaint.setStyle(Paint.Style.STROKE);
        glowPaint.setStrokeWidth(60f);
        glowPaint.setMaskFilter(new android.graphics.BlurMaskFilter(
                20f, android.graphics.BlurMaskFilter.Blur.NORMAL));

        progressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        progressPaint.setColor(Color.parseColor("#F0D238"));
        progressPaint.setStyle(Paint.Style.STROKE);
        progressPaint.setStrokeWidth(45f);
        progressPaint.setStrokeCap(Paint.Cap.ROUND);

        Typeface galacti = ResourcesCompat.getFont(getContext(), R.font.galacti);
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.parseColor("#1A1A2E"));
        textPaint.setTextSize(95f);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTypeface(galacti);

        rectF = new RectF();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float width = getWidth();
        float height = getHeight();
        float radius = Math.min(width, height) / 2f - 50f;
        float cx = width / 2f;
        float cy = height / 2f;

        rectF.set(cx - radius, cy - radius, cx + radius, cy + radius);

        // רקע אפור
        canvas.drawArc(rectF, -90, 360, false, backgroundPaint);

        // גלו צהוב
        float sweepAngle = (progress / 100f) * 360f;
        canvas.drawArc(rectF, -90, sweepAngle, false, glowPaint);

        // פרוגרס צהוב
        canvas.drawArc(rectF, -90, sweepAngle, false, progressPaint);

        // אחוז במרכז
        int percent = (int) Math.min(progress, 100);
        canvas.drawText(percent + "%", cx, cy + 35f, textPaint);
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