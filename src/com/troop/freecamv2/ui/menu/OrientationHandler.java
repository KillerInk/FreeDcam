package com.troop.freecamv2.ui.menu;

import android.app.Activity;
import android.hardware.SensorManager;
import android.view.OrientationEventListener;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.troop.freecam.R;
import com.troop.freecamv2.camera.CameraUiWrapper;
import com.troop.freecamv2.camera.parameters.I_ParametersLoaded;
import com.troop.freecamv2.ui.MainActivity_v2;
import com.troop.freecamv2.ui.handler.HelpOverlayHandler;

/**
 * Created by troop on 17.09.2014.
 */
public class OrientationHandler
{
    /*private final CameraUiWrapper cameraUiWrapper;
    private final MainActivity_v2 activity;*/

    private int currentOrientation = 0;
    /*int helplayoutrot;
    LinearLayout cameraControlsLayout;
    ListView switchControlsSubmenu;
    LinearLayout switchCOntrolLayout;
    *//*LinearLayout menuControlLayout;
    LinearLayout manualSettingsLayout;*//*
    HelpOverlayHandler helpOverlayLayout;*/
    OrientationEventListener orientationEventListener;
    boolean parametersLoaded = false;
    I_orientation orientationListner;
    Activity activity;

    public OrientationHandler(Activity activity, final I_orientation orientationListner)
    {
        this.orientationListner = orientationListner;
        this.activity = activity;
        /*this.cameraUiWrapper = cameraUiWrapper;
        cameraUiWrapper.camParametersHandler.ParametersEventHandler.AddParametersLoadedListner(this);

        cameraControlsLayout = (LinearLayout)activity.findViewById(R.id.layout__cameraControls);
        switchControlsSubmenu = (ListView)activity.findViewById(R.id.listView_popup);
        switchCOntrolLayout = (LinearLayout)activity.findViewById(R.id.moduleSwitch_placeholder);
        *//*menuControlLayout = (LinearLayout)activity.findViewById(R.id.v2_settings_menu);
        manualSettingsLayout = (LinearLayout)activity.findViewById(R.id.v2_manual_menu);*//*
        helpOverlayLayout = (HelpOverlayHandler)activity.findViewById(R.id.helpoverlay);*/

        orientationEventListener = new OrientationEventListener(activity, SensorManager.SENSOR_DELAY_NORMAL) {
            @Override
            public void onOrientationChanged(int orientation)
            {
                if (currentOrientation != calcCurrentOrientation(orientation))
                {
                    currentOrientation = calcCurrentOrientation(orientation);
                    if (orientationListner != null)
                        orientationListner.OrientationChanged(currentOrientation);
                }
            }
        };
    }

    public void Start()
    {
        orientationEventListener.enable();
    }
    public void Stop()
    {
        orientationEventListener.disable();
    }


    private int calcCurrentOrientation(int orientation)
    {
        int orientationToRet = 0;
        if (orientation >= 315 || orientation < 45)
            orientationToRet = 90;
        else if (orientation < 135 && orientation > 45)
            orientationToRet = 180;
        else if (orientation >= 135 && orientation < 230)
            orientationToRet = 270;
        return orientationToRet;
    }

    /*

    private void rotateSettingsMenu(int orientation)
    {
        *//*int wasVisible = menuControlLayout.getVisibility();
        float lastA = menuControlLayout.getAlpha();
        menuControlLayout.setAlpha(0f);
        menuControlLayout.setVisibility(View.VISIBLE);*//*

        int h = menuControlLayout.getHeight();
        int w = menuControlLayout.getWidth();
        if (h == 0 || w == 0)
        {
            return;
        }


        menuControlLayout.getLayoutParams().height = w;
        menuControlLayout.getLayoutParams().width = h;
        menuControlLayout.requestLayout();
        menuControlLayout.setRotation(orientation);

        *//*menuControlLayout.setAlpha(lastA);
        menuControlLayout.setVisibility(wasVisible);*//*
    }

    private void setRotationToCam(int orientation)
    {
        //cameraUiWrapper.cameraHolder.GetCamera().setDisplayOrientation(orientation);
        cameraUiWrapper.camParametersHandler.SetPictureOrientation(orientation);
    }*/

   /* @Override
    public void ParametersLoaded()
    {
        setRotationToCam(currentOrientation);
        rotateViews(-currentOrientation);
        parametersLoaded = true;
    }*/
}
