package com.troop.freedcam.cameraui.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.troop.freedcam.cameraui.R;
import com.troop.freedcam.cameraui.fragment.MainFragment;
import com.troop.freedcam.cameraui.utils.HideNavBarHelper;

public class CameraUiActivity extends AppCompatActivity {

    private HideNavBarHelper hideNavBarHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_ui_activity);
        hideNavBarHelper = new HideNavBarHelper();
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, MainFragment.newInstance())
                    .commitNow();
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus)
            hideNavBarHelper.HIDENAVBAR(getWindow());
        else
            hideNavBarHelper.showNavbar(getWindow());
    }
}