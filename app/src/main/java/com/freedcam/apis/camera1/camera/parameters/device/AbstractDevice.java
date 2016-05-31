package com.freedcam.apis.camera1.camera.parameters.device;

import com.freedcam.apis.basecamera.camera.parameters.manual.AbstractManualParameter;

/**
 * Created by troop on 31.05.2016.
 */
public abstract class AbstractDevice
{
    public abstract AbstractManualParameter getExposureTimeParameter();
    public abstract AbstractManualParameter getIsoParameter();
    public abstract AbstractManualParameter getManualFocusParameter();
    public abstract AbstractManualParameter getCCTParameter();
}
