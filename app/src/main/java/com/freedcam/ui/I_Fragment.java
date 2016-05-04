package com.freedcam.ui;

import com.freedcam.apis.i_camera.AbstractCameraUiWrapper;
import com.freedcam.utils.AppSettingsManager;

/**
 * Created by troop on 24.03.2015.
 */
interface I_Fragment
{
    void SetCameraUIWrapper(AbstractCameraUiWrapper wrapper);
    void SetStuff(I_Activity i_activity, AppSettingsManager appSettingsManager);
}
