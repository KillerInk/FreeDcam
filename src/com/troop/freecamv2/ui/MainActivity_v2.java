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

import com.troop.freecam.R;
import com.troop.freecam.manager.AppSettingsManager;
import com.troop.freecamv2.camera.CameraUiWrapper;
import com.troop.freecamv2.ui.menu.ExpandableChild;
import com.troop.freecamv2.ui.menu.ExpandableGroup;
import com.troop.freecamv2.ui.menu.ExpandableListViewMenuAdapter;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ArrayList<ExpandableGroup> grouplist = createMenu();
        appSettingsManager = new AppSettingsManager(PreferenceManager.getDefaultSharedPreferences(this));
        expandableListViewMenuAdapter = new ExpandableListViewMenuAdapter(MainActivity_v2.this, grouplist);
        menuListView.setAdapter(expandableListViewMenuAdapter);
        cameraPreview = (TextureView)findViewById(R.id.textureViewCameraPreview);
        cameraUiWrapper = new CameraUiWrapper(cameraPreview, appSettingsManager,null);

    }

    private ArrayList<ExpandableGroup> createMenu() {
        menuListView = (ExpandableListView)findViewById(R.id.expandableListViewSettings);
        ArrayList<ExpandableGroup> grouplist = new ArrayList<ExpandableGroup>();
        ArrayList<ExpandableChild> pcilist = new ArrayList<ExpandableChild>();
        ExpandableGroup picGroup = new ExpandableGroup();
        picGroup.setName("Picture Settings");
        ExpandableChild picSize = new ExpandableChild();
        picSize.setName("PictureSize");
        pcilist.add(picSize);
        picGroup.setItems(pcilist);
        grouplist.add(picGroup);
        return grouplist;
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
