package com.freedcam.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.freedcam.apis.basecamera.camera.AbstractCameraUiWrapper;
import com.freedcam.utils.AppSettingsManager;

/**
 * Created by troop on 25.03.2015.
 */
public abstract class AbstractFragment extends Fragment implements I_Fragment
{
    protected AbstractCameraUiWrapper cameraUiWrapper;
    protected I_Activity i_activity;
    protected View view;
    protected AppSettingsManager appSettingsManager;

    public void SetCameraUIWrapper(AbstractCameraUiWrapper wrapper)
    {
        this.cameraUiWrapper = wrapper;
        if (cameraUiWrapper != null)
            setCameraUiWrapperToUi();
    }

    protected void setCameraUiWrapperToUi()
    {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(null);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, null);
    }
}
