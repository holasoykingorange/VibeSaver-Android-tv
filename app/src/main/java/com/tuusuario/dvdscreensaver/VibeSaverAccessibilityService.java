package com.tuusuario.dvdscreensaver;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.accessibility.AccessibilityEvent;

public class VibeSaverAccessibilityService extends AccessibilityService {
    private SharedPreferences prefs;
    private BlackDimmerOverlay dimmerOverlay;
    private int currentDarkness = BrightnessController.DEFAULT_DARKNESS;

    @Override
    public void onCreate() {
        super.onCreate();
        prefs = getSharedPreferences("dvd_settings", MODE_PRIVATE);
        currentDarkness = prefs.getInt("darkness", BrightnessController.DEFAULT_DARKNESS);
        dimmerOverlay = new BlackDimmerOverlay(this);
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
    }

    @Override
    public void onInterrupt() {
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        AccessibilityServiceInfo info = new AccessibilityServiceInfo();
        info.eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED;
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;
        info.flags = AccessibilityServiceInfo.FLAG_REQUEST_FILTER_KEY_EVENTS;
        info.notificationTimeout = 100;
        setServiceInfo(info);
    }

    @Override
    public boolean onKeyEvent(KeyEvent event) {
        if (event == null || event.getAction() != KeyEvent.ACTION_DOWN) {
            return super.onKeyEvent(event);
        }

        int keyCode = event.getKeyCode();
        if (TvButtonMapper.isToggleKey(keyCode)) {
            if (dimmerOverlay == null) {
                dimmerOverlay = new BlackDimmerOverlay(this);
            }
            if (BlackDimmerOverlay.canDrawOverOtherApps(this)) {
                dimmerOverlay.toggle();
                return true;
            }
            return super.onKeyEvent(event);
        }

        int delta = TvButtonMapper.brightnessDeltaForKeyCode(keyCode);
        if (delta != 0) {
            currentDarkness = BrightnessController.adjust(currentDarkness, delta);
            prefs.edit().putInt("darkness", currentDarkness).apply();
            if (dimmerOverlay == null) {
                dimmerOverlay = new BlackDimmerOverlay(this);
            }
            if (BlackDimmerOverlay.canDrawOverOtherApps(this)) {
                dimmerOverlay.show(currentDarkness);
            }
            return true;
        }

        return super.onKeyEvent(event);
    }
}
