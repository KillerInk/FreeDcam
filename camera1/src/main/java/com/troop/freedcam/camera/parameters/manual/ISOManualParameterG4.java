package com.troop.freedcam.camera.parameters.manual;

import com.troop.freedcam.camera.BaseCameraHolder;
import com.troop.freedcam.i_camera.parameters.AbstractParameterHandler;
import com.troop.freedcam.utils.DeviceUtils;

import java.util.HashMap;

public class ISOManualParameterG4 extends BaseManualParameter {

    BaseCameraHolder baseCameraHolder;
    public ISOManualParameterG4(HashMap<String, String> parameters, String value, String maxValue, String MinValue, AbstractParameterHandler camParametersHandler) {
        super(parameters, value, maxValue, MinValue, camParametersHandler);

        //TODO add missing logic
    }
    public ISOManualParameterG4(HashMap<String, String> parameters, String value, String maxValue, String MinValue, BaseCameraHolder cameraHolder, AbstractParameterHandler camParametersHandler) {
        super(parameters, value, maxValue, MinValue, camParametersHandler);

        this.baseCameraHolder = cameraHolder;

            this.isSupported = true;
    }

    @Override
    public boolean IsSupported()
    {

        return isSupported;

    }

    @Override
    public int GetMaxValue() {

        return 2700;

    }

    @Override
    public int GetMinValue() {

            return -1;
        }

    @Override
    public int GetValue() {

        try {
            if (parameters.get("iso").equals("auto"))
                return  -1;
            else
                return Integer.parseInt(parameters.get("iso"));
        } catch (NullPointerException ex) {
            return 0;
        }



    }

    @Override
    protected void setvalue(int valueToSet) {
        if (valueToSet == -1)
            parameters.put("iso", "auto");
        else
            parameters.put("iso", valueToSet + "");
    }

}


