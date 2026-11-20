package com.tuusuario.dvdscreensaver;

public final class BrightnessController {
    public static final int MIN = 0;
    public static final int MAX = 100;
    public static final int DEFAULT_DARKNESS = 80;

    private BrightnessController() {
    }

    public static int clamp(int value) {
        return Math.max(MIN, Math.min(MAX, value));
    }

    public static int adjust(int current, int delta) {
        return clamp(current + delta);
    }

    public static float darknessToAlpha(int darknessPercent) {
        int clamped = clamp(darknessPercent);
        return Math.max(0f, Math.min(1f, clamped / 100f));
    }
}
