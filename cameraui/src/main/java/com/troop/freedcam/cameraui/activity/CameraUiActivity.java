package com.troop.freedcam.cameraui.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.troop.freedcam.camera.CameraApiController;
import com.troop.freedcam.cameraui.R;
import com.troop.freedcam.cameraui.fragment.CameraFragmentManager;
import com.troop.freedcam.cameraui.fragment.MainFragment;
import com.troop.freedcam.cameraui.utils.HideNavBarHelper;
import com.troop.freedcam.eventbus.EventBusHelper;
import com.troop.freedcam.eventbus.events.CloseAppEvent;

import org.greenrobot.eventbus.Subscribe;

public class CameraUiActivity extends AppCompatActivity {

    private HideNavBarHelper hideNavBarHelper;

    @Subscribe
    public void onCloseAppEvent(CloseAppEvent event)
    {
        onBackPressed();
    }

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
    protected void onResume() {
        super.onResume();
        EventBusHelper.register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBusHelper.unregister(this);
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