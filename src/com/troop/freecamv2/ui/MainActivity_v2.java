package com.troop.freecamv2.ui;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.troop.freecam.R;

import com.troop.freecamv2.camera.CameraUiWrapper;
import com.troop.freecamv2.camera.modules.ModuleHandler;
import com.troop.freecamv2.ui.menu.ExpandableChild;
import com.troop.freecamv2.ui.menu.ExpandableGroup;
import com.troop.freecamv2.ui.menu.ExpandableListViewMenuAdapter;
import com.troop.freecamv2.ui.menu.MenuHandler;

import java.util.ArrayList;

/**
 * Created by troop on 18.08.2014.
 */
public class MainActivity_v2 extends MenuVisibilityActivity
{

    protected ViewGroup appViewGroup;
    ExpandableListView menuListView;
    ExpandableListViewMenuAdapter expandableListViewMenuAdapter;
    TextureView cameraPreview;
    CameraUiWrapper cameraUiWrapper;
    AppSettingsManager appSettingsManager;
    MenuHandler menuHandler;
    ImageView shutterButton;
    CameraSwitchHandler cameraSwitchHandler;
    ModuleSwitchHandler moduleSwitchHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        appSettingsManager = new AppSettingsManager(PreferenceManager.getDefaultSharedPreferences(this));
        cameraPreview = (TextureView)findViewById(R.id.textureViewCameraPreview);
        cameraUiWrapper = new CameraUiWrapper(cameraPreview, appSettingsManager,null);
        menuHandler = new MenuHandler(this, cameraUiWrapper, appSettingsManager);

        shutterButton = (ImageView)findViewById(R.id.shutter_imageview);
        shutterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraUiWrapper.DoWork();
            }
        });

        cameraSwitchHandler = new CameraSwitchHandler(this, cameraUiWrapper, appSettingsManager);
        moduleSwitchHandler = new ModuleSwitchHandler(this, cameraUiWrapper, appSettingsManager);

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();

    }

}
