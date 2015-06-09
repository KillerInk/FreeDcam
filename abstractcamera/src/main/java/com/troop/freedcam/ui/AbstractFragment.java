package com.troop.freedcam.ui;

import android.support.v4.app.Fragment;

import com.troop.freedcam.i_camera.AbstractCameraUiWrapper;
import com.troop.freedcam.ui.AppSettingsManager;
import com.troop.freedcam.ui.I_Activity;
import com.troop.freedcam.ui.I_Fragment;

/**
 * Created by troop on 25.03.2015.
 */
public abstract class AbstractFragment extends Fragment implements I_Fragment
{
    @Override
    public abstract void SetCameraUIWrapper(AbstractCameraUiWrapper wrapper);

}
