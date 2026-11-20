package com.tuusuario.dvdscreensaver;

import android.view.KeyEvent;

public final class TvButtonMapper {
    public static final int KEYCODE_DPAD_UP = KeyEvent.KEYCODE_DPAD_UP;
    public static final int KEYCODE_DPAD_DOWN = KeyEvent.KEYCODE_DPAD_DOWN;
    public static final int KEYCODE_DPAD_LEFT = KeyEvent.KEYCODE_DPAD_LEFT;
    public static final int KEYCODE_DPAD_RIGHT = KeyEvent.KEYCODE_DPAD_RIGHT;
    public static final int KEYCODE_DPAD_CENTER = KeyEvent.KEYCODE_DPAD_CENTER;
    public static final int KEYCODE_ENTER = KeyEvent.KEYCODE_ENTER;

    private TvButtonMapper() {
    }

    public static int brightnessDeltaForKeyCode(int keyCode) {
        switch (keyCode) {
            case KEYCODE_DPAD_UP:
            case KeyEvent.KEYCODE_VOLUME_UP:
            case KeyEvent.KEYCODE_MEDIA_NEXT:
                return 10;
            case KEYCODE_DPAD_DOWN:
            case KeyEvent.KEYCODE_VOLUME_DOWN:
            case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
                return -10;
            case KEYCODE_DPAD_LEFT:
                return -15;
            case KEYCODE_DPAD_RIGHT:
                return 15;
            default:
                return 0;
        }
    }

    public static boolean isToggleKey(int keyCode) {
        return keyCode == KEYCODE_DPAD_CENTER
                || keyCode == KEYCODE_ENTER
                || keyCode == KeyEvent.KEYCODE_MENU
                || keyCode == KeyEvent.KEYCODE_INFO
                || keyCode == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE;
    }

    public static String describeKey(int keyCode) {
        switch (keyCode) {
            case KEYCODE_DPAD_UP:
                return "D-pad arriba";
            case KEYCODE_DPAD_DOWN:
                return "D-pad abajo";
            case KEYCODE_DPAD_LEFT:
                return "D-pad izquierda";
            case KEYCODE_DPAD_RIGHT:
                return "D-pad derecha";
            case KEYCODE_DPAD_CENTER:
                return "D-pad centro";
            case KEYCODE_ENTER:
                return "Botón OK";
            case KeyEvent.KEYCODE_VOLUME_UP:
                return "Volumen +";
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                return "Volumen -";
            case KeyEvent.KEYCODE_MENU:
                return "Menú";
            default:
                return "Tecla no mapeada";
        }
    }
}
