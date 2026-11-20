package com.tuusuario.dvdscreensaver;

import java.util.Locale;

public final class DvdSettings {
    public static final String PREFS = "dvd_settings";
    public static final String KEY_DARKNESS = "darkness";
    public static final String KEY_SIZE = "size_scale";
    public static final String KEY_SPEED = "speed_scale";
    public static final String KEY_LOGO = "dvd_logo";
    public static final String DEFAULT_LOGO = "dvd";

    private DvdSettings() {
    }

    public static String normalizeLogoKey(String value) {
        if (value == null) {
            return DEFAULT_LOGO;
        }

        String normalized = value.trim().toLowerCase(Locale.US);
        if (normalized.isEmpty()) {
            return DEFAULT_LOGO;
        }

        switch (normalized) {
            case "dvd":
            case "classic":
            case "logo":
                return "dvd";
            case "vibe":
            case "vibesaver":
            case "vibe saver":
                return "vibesaver";
            case "tv":
            case "television":
                return "tv";
            case "custom":
            case "texto":
            case "text":
            case "personalizado":
                return "custom";
            default:
                return DEFAULT_LOGO;
        }
    }

    public static String[] availableLogoKeys() {
        return new String[] { "dvd", "vibesaver", "tv", "custom" };
    }

    public static String nextLogoKey(String current) {
        String[] options = availableLogoKeys();
        String normalized = normalizeLogoKey(current);
        for (int i = 0; i < options.length; i++) {
            if (options[i].equals(normalized)) {
                return options[(i + 1) % options.length];
            }
        }
        return DEFAULT_LOGO;
    }

    public static String getDisplayName(String logoKey) {
        switch (normalizeLogoKey(logoKey)) {
            case "vibesaver":
                return "VibeSaver";
            case "tv":
                return "TV";
            case "custom":
                return "Personalizado";
            case "dvd":
            default:
                return "DVD";
        }
    }

    public static float clampSizeScale(float value) {
        return Math.max(0.4f, Math.min(2.2f, value));
    }

    public static float clampSpeedScale(float value) {
        return Math.max(0.5f, Math.min(2.5f, value));
    }
}
