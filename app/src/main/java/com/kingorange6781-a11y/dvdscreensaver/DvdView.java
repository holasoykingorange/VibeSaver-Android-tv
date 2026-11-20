package com.tuusuario.dvdscreensaver;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import java.util.Random;

public class DvdView extends View {

    private static final float MIN_SPEED = 1.5f;
    private static final float MAX_SPEED = 12f;
    private static final int MIN_DARKNESS = 0;
    private static final int MAX_DARKNESS = 100;
    private static final float BASE_RECT_WIDTH = 350f;
    private static final float BASE_RECT_HEIGHT = 175f;

    private float posX = 100f, posY = 100f;
    private float velX = 4f, velY = 4f;
    private int rectWidth = 350;
    private int rectHeight = 175;
    private float sizeScale = 1f;
    private float speedScale = 1f;
    private String logoKey = DvdSettings.DEFAULT_LOGO;

    private final Paint paint;
    private final Paint overlayPaint;
    private Bitmap dvdBitmap;
    private int currentColor;
    private int darkness = MAX_DARKNESS;
    private final Handler handler;
    private final Runnable runnable;
    private final Random random;

    private final int[] colors = {
        Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW,
        Color.CYAN, Color.MAGENTA, Color.WHITE, Color.rgb(255, 165, 0)
    };

    public DvdView(Context context) {
        super(context);
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        overlayPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        overlayPaint.setColor(Color.BLACK);
        random = new Random();
        currentColor = colors[random.nextInt(colors.length)];

        SharedPreferences prefs = context.getSharedPreferences(DvdSettings.PREFS, Context.MODE_PRIVATE);
        sizeScale = DvdSettings.clampSizeScale(prefs.getFloat(DvdSettings.KEY_SIZE, 1f));
        speedScale = DvdSettings.clampSpeedScale(prefs.getFloat(DvdSettings.KEY_SPEED, 1f));
        darkness = clampDarkness(prefs.getInt(DvdSettings.KEY_DARKNESS, MAX_DARKNESS));
        logoKey = DvdSettings.normalizeLogoKey(prefs.getString(DvdSettings.KEY_LOGO, DvdSettings.DEFAULT_LOGO));

        applySize(sizeScale);
        applySpeed(speedScale);
        rebuildLogoBitmap();

        handler = new Handler(Looper.getMainLooper());
        runnable = new Runnable() {
            @Override
            public void run() {
                updatePosition();
                invalidate();
                handler.postDelayed(this, 16);
            }
        };
    }

    public void startAnimation() {
        handler.post(runnable);
    }

    public void stopAnimation() {
        handler.removeCallbacks(runnable);
    }

    public void setDarkness(int value) {
        darkness = clampDarkness(value);
        invalidate();
    }

    public int getDarkness() {
        return darkness;
    }

    public void adjustDarkness(int delta) {
        setDarkness(darkness + delta);
    }

    public void setSizeScale(float scale) {
        sizeScale = DvdSettings.clampSizeScale(scale);
        applySize(sizeScale);
    }

    public void setSpeedScale(float scale) {
        speedScale = DvdSettings.clampSpeedScale(scale);
        applySpeed(speedScale);
    }

    public void setLogoKey(String key) {
        logoKey = DvdSettings.normalizeLogoKey(key);
        rebuildLogoBitmap();
        invalidate();
    }

    public void adjustSpeed(float delta) {
        setSpeedScale(speedScale + delta);
    }

    public void toggleColor() {
        int newColor;
        do {
            newColor = colors[random.nextInt(colors.length)];
        } while (newColor == currentColor);
        currentColor = newColor;
    }

    private void applySize(float scale) {
        rectWidth = Math.round(BASE_RECT_WIDTH * scale);
        rectHeight = Math.round(BASE_RECT_HEIGHT * scale);
        rebuildLogoBitmap();
        invalidate();
    }

    private void applySpeed(float scale) {
        float magnitude = 4f * scale;
        if (magnitude < MIN_SPEED) {
            magnitude = MIN_SPEED;
        }
        if (magnitude > MAX_SPEED) {
            magnitude = MAX_SPEED;
        }

        velX = Math.copySign(magnitude, velX == 0 ? 1f : velX);
        velY = Math.copySign(magnitude, velY == 0 ? 1f : velY);
    }

    private void rebuildLogoBitmap() {
        if ("custom".equals(logoKey) || "vibesaver".equals(logoKey) || "tv".equals(logoKey)) {
            dvdBitmap = buildTextLogo(DvdSettings.getDisplayName(logoKey));
            return;
        }

        Bitmap rawBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.dvd);
        if (rawBitmap != null) {
            dvdBitmap = Bitmap.createScaledBitmap(rawBitmap, rectWidth, rectHeight, true);
        }
    }

    private Bitmap buildTextLogo(String text) {
        Bitmap bitmap = Bitmap.createBitmap(rectWidth, rectHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        Paint backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        backgroundPaint.setColor(Color.rgb(13, 94, 148));
        canvas.drawRoundRect(new RectF(0, 0, rectWidth, rectHeight), 38f, 38f, backgroundPaint);

        Paint borderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(Math.max(4f, rectWidth * 0.03f));
        borderPaint.setColor(Color.argb(180, 255, 255, 255));
        canvas.drawRoundRect(new RectF(8f, 8f, rectWidth - 8f, rectHeight - 8f), 30f, 30f, borderPaint);

        Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(Math.min(rectWidth, rectHeight) * 0.32f);
        textPaint.setTypeface(Typeface.DEFAULT_BOLD);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setShadowLayer(1.5f, 0, 0, Color.argb(140, 0, 0, 0));

        float baseline = rectHeight / 2f - ((textPaint.descent() + textPaint.ascent()) / 2f);
        canvas.drawText(text, rectWidth / 2f, baseline, textPaint);
        return bitmap;
    }

    private int clampDarkness(int value) {
        return Math.max(MIN_DARKNESS, Math.min(MAX_DARKNESS, value));
    }

    private void updatePosition() {
        int width = getWidth();
        int height = getHeight();

        if (width == 0 || height == 0) return;

        posX += velX;
        posY += velY;

        boolean hitWall = false;

        if (posX <= 0) {
            posX = 0;
            velX = -velX;
            hitWall = true;
        } else if (posX + rectWidth >= width) {
            posX = width - rectWidth;
            velX = -velX;
            hitWall = true;
        }

        if (posY <= 0) {
            posY = 0;
            velY = -velY;
            hitWall = true;
        } else if (posY + rectHeight >= height) {
            posY = height - rectHeight;
            velY = -velY;
            hitWall = true;
        }

        if (hitWall) {
            toggleColor();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.BLACK);

        overlayPaint.setAlpha((int) ((darkness / 100f) * 255f));
        canvas.drawRect(0, 0, getWidth(), getHeight(), overlayPaint);

        if (dvdBitmap != null) {
            paint.setAlpha(255);
            paint.setColorFilter(new PorterDuffColorFilter(currentColor, PorterDuff.Mode.SRC_IN));
            canvas.drawBitmap(dvdBitmap, posX, posY, paint);
        }
    }
}