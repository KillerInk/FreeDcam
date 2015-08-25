package com.troop.freedcam.ui.menu.themes.classic.shutter;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.troop.freedcam.i_camera.AbstractCameraUiWrapper;
import com.troop.freedcam.ui.AppSettingsManager;
import com.troop.freedcam.ui.I_Activity;
import com.troop.freedcam.ui.I_PreviewSizeEvent;
import com.troop.freedcam.ui.menu.themes.R;


/**
 * Created by troop on 12.03.2015.
 */
public class ShutterItemsFragments extends Fragment
{
    public ShutterHandler shutterHandler;
    public CameraSwitchHandler cameraSwitchHandler;
    public ModuleSwitchHandler moduleSwitchHandler;
    public FlashSwitchHandler flashSwitchHandler;
    public NightModeSwitchHandler nightModeSwitchHandler;
    public TextView exitButton;
    public ExposureLockHandler exposureLockHandler;
    public AppSettingsManager appSettingsManager;
    public AbstractCameraUiWrapper cameraUiWrapper;
    public SurfaceView surfaceView;
    public boolean fragmentloaded = false;
    protected I_Activity activity;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.shutteritems_fragment, container, false);
        cameraSwitchHandler = new CameraSwitchHandler(activity, appSettingsManager, view);
        shutterHandler = new ShutterHandler(view, this);
        moduleSwitchHandler = new ModuleSwitchHandler(view, appSettingsManager, this);
        flashSwitchHandler = new FlashSwitchHandler(view, appSettingsManager, this);
        nightModeSwitchHandler = new NightModeSwitchHandler(view, appSettingsManager,this);
        exposureLockHandler = new ExposureLockHandler(view, appSettingsManager);
        exitButton = (TextView)view.findViewById(R.id.textView_Exit);
        ListView listView = (ListView) view.findViewById(R.id.listView_popup);
        listView.setVisibility(View.GONE);

        if( ViewConfiguration.get(getActivity()).hasPermanentMenuKey())
        {
            exitButton.setVisibility(View.GONE);
        }
        else
        {
            exitButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    getActivity().finish();
                }
            });
        }

        setCameraUIwrapper();
        fragmentloaded = true;
        return view;
    }

    protected void setCameraUIwrapper() {
        cameraSwitchHandler.SetCameraUiWrapper(cameraUiWrapper, surfaceView);
        shutterHandler.SetCameraUIWrapper(cameraUiWrapper);
        moduleSwitchHandler.SetCameraUIWrapper(cameraUiWrapper);
        flashSwitchHandler.SetCameraUIWrapper(cameraUiWrapper);
        try {

            nightModeSwitchHandler.SetCameraUIWrapper(cameraUiWrapper);
        }
        catch (Exception ex)
        {

        }
        exposureLockHandler.SetCameraUIWrapper(cameraUiWrapper);
    }

    public void SetAppSettings(AppSettingsManager appSettingsManager)
    {
        this.appSettingsManager = appSettingsManager;

    }

    public void SetCameraUIWrapper(AbstractCameraUiWrapper cameraUiWrapper, SurfaceView surfaceView, I_Activity activity)
    {
        this.cameraUiWrapper = cameraUiWrapper;
        this.surfaceView = surfaceView;
        this.activity = activity;
        if (fragmentloaded) {
            setCameraUIwrapper();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }


    public void ParametersLoaded()
    {
        cameraSwitchHandler.ParametersLoaded();
        flashSwitchHandler.ParametersLoaded();
        nightModeSwitchHandler.ParametersLoaded();
        exposureLockHandler.ParametersLoaded();
        moduleSwitchHandler.ParametersLoaded();
    }

}
