package com.troop.freedcam.cameraui.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.troop.freedcam.cameraui.R;
import com.troop.freedcam.cameraui.fragment.MainFragment;

public class CameraUiActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_ui_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, MainFragment.newInstance())
                    .commitNow();
        }
    }
}