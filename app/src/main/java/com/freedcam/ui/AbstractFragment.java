package com.freedcam.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.freedcam.apis.i_camera.AbstractCameraUiWrapper;

/**
 * Created by troop on 25.03.2015.
 */
public abstract class AbstractFragment extends Fragment implements I_Fragment
{
    protected AbstractCameraUiWrapper wrapper;
    protected I_Activity i_activity;
    protected View view;

    public void SetCameraUIWrapper(AbstractCameraUiWrapper wrapper)
    {
        this.wrapper = wrapper;
    }

    @Override
    public void SetStuff(I_Activity i_activity)
    {
        this.i_activity =i_activity;
    }

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
