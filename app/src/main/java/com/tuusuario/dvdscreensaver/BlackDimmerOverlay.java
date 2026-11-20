package com.tuusuario.dvdscreensaver;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Build;
import android.provider.Settings;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

public class BlackDimmerOverlay {
    private final Context context;
    private final WindowManager windowManager;
    private final View overlayView;
    private boolean visible;
    private int currentDarkness = BrightnessController.DEFAULT_DARKNESS;

    public BlackDimmerOverlay(Context context) {
        this.context = context.getApplicationContext();
        this.windowManager = (WindowManager) this.context.getSystemService(Context.WINDOW_SERVICE);
        this.overlayView = new View(this.context);
        this.overlayView.setBackgroundColor(Color.BLACK);
        this.overlayView.setVisibility(View.GONE);
    }

    public static boolean canDrawOverOtherApps(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return Settings.canDrawOverlays(context);
        }
        return true;
    }

    public void show(int darknessPercent) {
        if (windowManager == null || !canDrawOverOtherApps(context)) {
            return;
        }

        currentDarkness = BrightnessController.clamp(darknessPercent);
        float alpha = BrightnessController.darknessToAlpha(currentDarkness);
        overlayView.setAlpha(alpha);
        overlayView.setVisibility(View.VISIBLE);

        if (!visible) {
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.MATCH_PARENT,
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
                            ? WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                            : WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                            | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                            | WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    PixelFormat.TRANSLUCENT
            );
            layoutParams.gravity = Gravity.TOP | Gravity.START;
            windowManager.addView(overlayView, layoutParams);
            visible = true;
        }
    }

    public void hide() {
        if (visible && windowManager != null) {
            windowManager.removeViewImmediate(overlayView);
            visible = false;
        }
        overlayView.setVisibility(View.GONE);
    }

    public void toggle() {
        if (visible) {
            hide();
        } else {
            show(currentDarkness);
        }
    }

    public boolean isVisible() {
        return visible;
    }

    public int getCurrentDarkness() {
        return currentDarkness;
    }
}
