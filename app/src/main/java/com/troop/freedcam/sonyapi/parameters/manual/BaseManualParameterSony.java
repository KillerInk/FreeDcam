package com.troop.freedcam.sonyapi.parameters.manual;

import com.troop.freedcam.i_camera.parameters.AbstractManualParameter;
import com.troop.freedcam.i_camera.parameters.I_ManualParameter;
import com.troop.freedcam.sonyapi.parameters.ParameterHandlerSony;
import com.troop.freedcam.sonyapi.parameters.modes.BaseModeParameterSony;
import com.troop.freedcam.sonyapi.sonystuff.JsonUtils;
import com.troop.freedcam.sonyapi.sonystuff.SimpleRemoteApi;

import java.util.Set;

/**
 * Created by troop on 15.12.2014.
 */
public abstract class BaseManualParameterSony extends AbstractManualParameter
{
    protected String MAX_TO_GET;
    protected String MIN_TO_GET;
    protected String CURRENT_TO_GET;
    protected ParameterHandlerSony ParameterHandler;
    protected SimpleRemoteApi mRemoteApi;
    protected Set<String> mAvailableCameraApiSet;

    public BaseManualParameterSony(String MAX_TO_GET, String MIN_TO_GET, String CURRENT_TO_GET, ParameterHandlerSony parameterHandlerSony)
    {
        super(parameterHandlerSony);
        this.MAX_TO_GET = MAX_TO_GET;
        this.MIN_TO_GET = MIN_TO_GET;
        this.CURRENT_TO_GET = CURRENT_TO_GET;
        this.ParameterHandler = parameterHandlerSony;

    }

    @Override
    public boolean IsSupported() {
        return false;
    }

    @Override
    public int GetMaxValue()
    {
        return 100;
    }

    @Override
    public int GetMinValue() {
        return 0;
    }

    @Override
    public int GetValue()
    {
        return 0;
    }

    @Override
    public void SetValue(int valueToSet)
    {

    }

    @Override
    public void RestartPreview() {

    }
}
