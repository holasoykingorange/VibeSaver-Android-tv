package com.tuusuario.dvdscreensaver;

import android.content.SharedPreferences;
import android.service.dreams.DreamService;
import android.view.KeyEvent;

public class DvdScreensaverService extends DreamService {

    private DvdView dvdView;

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        setInteractive(false);
        setFullscreen(true);

        SharedPreferences prefs = getSharedPreferences(DvdSettings.PREFS, MODE_PRIVATE);
        dvdView = new DvdView(this);
        dvdView.setDarkness(prefs.getInt(DvdSettings.KEY_DARKNESS, BrightnessController.DEFAULT_DARKNESS));
        dvdView.setSizeScale(DvdSettings.clampSizeScale(prefs.getFloat(DvdSettings.KEY_SIZE, 1f)));
        dvdView.setSpeedScale(DvdSettings.clampSpeedScale(prefs.getFloat(DvdSettings.KEY_SPEED, 1f)));
        dvdView.setLogoKey(prefs.getString(DvdSettings.KEY_LOGO, DvdSettings.DEFAULT_LOGO));
        setContentView(dvdView);
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (dvdView != null) {
            dvdView.stopAnimation();
        }
    }

    @Override
    public void onDreamingStarted() {
        super.onDreamingStarted();
        if (dvdView != null) {
            dvdView.startAnimation();
        }
    }

    @Override
    public void onDreamingStopped() {
        super.onDreamingStopped();
        if (dvdView != null) {
            dvdView.stopAnimation();
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event == null || event.getAction() != KeyEvent.ACTION_DOWN) {
            return super.dispatchKeyEvent(event);
        }

        switch (event.getKeyCode()) {
            case KeyEvent.KEYCODE_DPAD_LEFT:
                if (dvdView != null) {
                    dvdView.adjustDarkness(-10);
                }
                return true;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                if (dvdView != null) {
                    dvdView.adjustDarkness(10);
                }
                return true;
            case KeyEvent.KEYCODE_DPAD_UP:
                if (dvdView != null) {
                    dvdView.adjustSpeed(0.35f);
                }
                return true;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                if (dvdView != null) {
                    dvdView.adjustSpeed(-0.35f);
                }
                return true;
            case KeyEvent.KEYCODE_ENTER:
            case KeyEvent.KEYCODE_DPAD_CENTER:
                if (dvdView != null) {
                    dvdView.toggleColor();
                }
                return true;
            case KeyEvent.KEYCODE_BACK:
                finish();
                return true;
            default:
                return super.dispatchKeyEvent(event);
        }
    }
}