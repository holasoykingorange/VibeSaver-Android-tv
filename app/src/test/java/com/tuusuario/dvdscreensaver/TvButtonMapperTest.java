package com.tuusuario.dvdscreensaver;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class TvButtonMapperTest {
    @Test
    public void mapsDpadUpAndDownToBrightnessChanges() {
        assertEquals(10, TvButtonMapper.brightnessDeltaForKeyCode(TvButtonMapper.KEYCODE_DPAD_UP));
        assertEquals(-10, TvButtonMapper.brightnessDeltaForKeyCode(TvButtonMapper.KEYCODE_DPAD_DOWN));
        assertEquals(-15, TvButtonMapper.brightnessDeltaForKeyCode(TvButtonMapper.KEYCODE_DPAD_LEFT));
        assertEquals(15, TvButtonMapper.brightnessDeltaForKeyCode(TvButtonMapper.KEYCODE_DPAD_RIGHT));
    }

    @Test
    public void togglesOnCenterAndEnter() {
        assertTrue(TvButtonMapper.isToggleKey(TvButtonMapper.KEYCODE_DPAD_CENTER));
        assertTrue(TvButtonMapper.isToggleKey(TvButtonMapper.KEYCODE_ENTER));
    }
}
