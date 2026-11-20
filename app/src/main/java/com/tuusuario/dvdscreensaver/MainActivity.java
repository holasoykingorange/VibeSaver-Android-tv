package com.tuusuario.dvdscreensaver;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

    private static final int DEFAULT_DARKNESS = 80;
    private SharedPreferences prefs;
    private int darkness = DEFAULT_DARKNESS;
    private float sizeScale = 1f;
    private float speedScale = 1f;
    private String dvdLogoKey = DvdSettings.DEFAULT_LOGO;
    private boolean landscape = true;
    private BlackDimmerOverlay dimmerOverlay;
    private TextView darknessValue;
    private TextView sizeValue;
    private TextView speedValue;
    private TextView dvdLogoValue;
    private Button logoPresetButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prefs = getSharedPreferences(DvdSettings.PREFS, Context.MODE_PRIVATE);
        darkness = prefs.getInt(DvdSettings.KEY_DARKNESS, DEFAULT_DARKNESS);
        sizeScale = DvdSettings.clampSizeScale(prefs.getFloat(DvdSettings.KEY_SIZE, 1f));
        speedScale = DvdSettings.clampSpeedScale(prefs.getFloat(DvdSettings.KEY_SPEED, 1f));
        dvdLogoKey = DvdSettings.normalizeLogoKey(prefs.getString(DvdSettings.KEY_LOGO, DvdSettings.DEFAULT_LOGO));
        dimmerOverlay = new BlackDimmerOverlay(this);

        TextView title = findViewById(R.id.titleText);
        title.setText("VibeSaver - TV Controls");

        darknessValue = findViewById(R.id.darknessValue);
        sizeValue = findViewById(R.id.sizeValue);
        speedValue = findViewById(R.id.speedValue);
        dvdLogoValue = findViewById(R.id.dvdLogoValue);

        SeekBar darknessBar = findViewById(R.id.darknessBar);
        SeekBar sizeBar = findViewById(R.id.sizeBar);
        SeekBar speedBar = findViewById(R.id.speedBar);

        Button startDreamButton = findViewById(R.id.startDreamButton);
        Button settingsButton = findViewById(R.id.openSettingsButton);
        Button toggleOrientationButton = findViewById(R.id.orientationButton);
        Button remapButton = findViewById(R.id.remapButton);
        Button dimmerButton = findViewById(R.id.dimmerButton);
        logoPresetButton = findViewById(R.id.logoPresetButton);

        darknessBar.setMax(100);
        darknessBar.setProgress(darkness);

        sizeBar.setMax(100);
        sizeBar.setProgress(Math.round((sizeScale - 0.4f) * 100f));

        speedBar.setMax(100);
        speedBar.setProgress(Math.round((speedScale - 0.5f) * 100f));

        setVisualizerValues();

        darknessBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                darkness = progress;
                prefs.edit().putInt(DvdSettings.KEY_DARKNESS, darkness).apply();
                setVisualizerValues();
                if (dimmerOverlay != null) {
                    dimmerOverlay.show(darkness);
                }
            }

            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        sizeBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                sizeScale = DvdSettings.clampSizeScale(0.4f + (progress / 100f));
                prefs.edit().putFloat(DvdSettings.KEY_SIZE, sizeScale).apply();
                setVisualizerValues();
            }

            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        speedBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                speedScale = DvdSettings.clampSpeedScale(0.5f + (progress / 100f));
                prefs.edit().putFloat(DvdSettings.KEY_SPEED, speedScale).apply();
                setVisualizerValues();
            }

            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        logoPresetButton.setOnClickListener(v -> {
            dvdLogoKey = DvdSettings.nextLogoKey(dvdLogoKey);
            prefs.edit().putString(DvdSettings.KEY_LOGO, dvdLogoKey).apply();
            setVisualizerValues();
            Toast.makeText(this, "Logo DVD: " + DvdSettings.getDisplayName(dvdLogoKey), Toast.LENGTH_SHORT).show();
        });

        startDreamButton.setOnClickListener(v -> startSaver());

        settingsButton.setOnClickListener(v -> startActivity(new Intent(Settings.ACTION_DISPLAY_SETTINGS)));

        dimmerButton.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !BlackDimmerOverlay.canDrawOverOtherApps(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivity(intent);
                Toast.makeText(this, "Permite la superposición para oscurecer la pantalla", Toast.LENGTH_LONG).show();
                return;
            }

            if (dimmerOverlay.isVisible()) {
                dimmerOverlay.hide();
                dimmerButton.setText("OSCURECER PANTALLA");
                Toast.makeText(this, "Pantalla normalizada", Toast.LENGTH_SHORT).show();
            } else {
                dimmerOverlay.show(darkness);
                dimmerButton.setText("NORMALIZAR PANTALLA");
                Toast.makeText(this, "Pantalla oscurecida al " + darkness + "%", Toast.LENGTH_SHORT).show();
            }
        });

        toggleOrientationButton.setOnClickListener(v -> {
            landscape = !landscape;
            if (landscape) {
                setRequestedOrientation(android.content.pm.ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                toggleOrientationButton.setText("ORIENTACIÓN: LANDSCAPE");
            } else {
                setRequestedOrientation(android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                toggleOrientationButton.setText("ORIENTACIÓN: PORTRAIT");
            }
        });

        remapButton.setOnClickListener(v -> {
            startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
            Toast.makeText(this, "Activa el servicio para mapear los botones del control", Toast.LENGTH_LONG).show();
        });

        startSaver();
    }

    private void setVisualizerValues() {
        darknessValue.setText("Oscurecer: " + darkness + "%");
        sizeValue.setText("Tamaño: " + String.format("%.2f", sizeScale) + "x");
        speedValue.setText("Velocidad: " + String.format("%.2f", speedScale) + "x");
        dvdLogoValue.setText("Logo DVD: " + DvdSettings.getDisplayName(dvdLogoKey));
        logoPresetButton.setText("LOGO: " + DvdSettings.getDisplayName(dvdLogoKey));
    }

    private void startSaver() {
        Intent intent = new Intent(this, DvdScreensaverService.class);
        startService(intent);
        Toast.makeText(this, "Salvapantallas iniciado", Toast.LENGTH_SHORT).show();
    }
}
