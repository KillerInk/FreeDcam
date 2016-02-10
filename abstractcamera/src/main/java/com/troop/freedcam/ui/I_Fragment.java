package com.troop.freedcam.ui;

import com.troop.freedcam.i_camera.AbstractCameraUiWrapper;

/**
 * Created by troop on 24.03.2015.
 */
interface I_Fragment
{
    void SetCameraUIWrapper(AbstractCameraUiWrapper wrapper);
    void SetStuff(AppSettingsManager appSettingsManager, I_Activity i_activity);
}
