package com.tuusuario.dvdscreensaver;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class BrightnessControllerTest {
    @Test
    public void clampKeepsValuesInsideRange() {
        assertEquals(0, BrightnessController.clamp(-10));
        assertEquals(100, BrightnessController.clamp(150));
        assertEquals(45, BrightnessController.clamp(45));
    }

    @Test
    public void adjustChangesBrightnessByDelta() {
        assertEquals(30, BrightnessController.adjust(20, 10));
        assertEquals(10, BrightnessController.adjust(20, -10));
        assertEquals(0, BrightnessController.adjust(0, -5));
        assertEquals(100, BrightnessController.adjust(95, 20));
    }

    @Test
    public void dvdSettingsNormalizeValuesAndPresets() {
        assertEquals("dvd", DvdSettings.normalizeLogoKey("DVD"));
        assertEquals(0.4f, DvdSettings.clampSizeScale(-2f), 0.0001f);
        assertEquals(2.5f, DvdSettings.clampSpeedScale(100f), 0.0001f);
    }
}
