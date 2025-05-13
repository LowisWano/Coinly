package com.example.coinly;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;

public class ScannerOverlayView extends View {
    private static final float WIDTH = 800f;
    private static final float HEIGHT = 800f;

    private Paint backgroundPaint;
    private Paint clearPaint;
    private RectF scannerRect;

    public ScannerOverlayView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        backgroundPaint = new Paint();
        backgroundPaint.setColor(0x99000000);

        clearPaint = new Paint();
        clearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));

        float left = (getResources().getDisplayMetrics().widthPixels - WIDTH) / 2f;
        float top = (getResources().getDisplayMetrics().heightPixels - HEIGHT) / 4f;

        scannerRect = new RectF(left, top, left + WIDTH, top + HEIGHT);
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawPaint(backgroundPaint);
        canvas.drawRoundRect(scannerRect, 40f, 40f, clearPaint);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        int saveCount = canvas.saveLayer(null, null);
        super.dispatchDraw(canvas);
        canvas.restoreToCount(saveCount);
    }

    public RectF getScannerRect() {
        return scannerRect;
    }
}
