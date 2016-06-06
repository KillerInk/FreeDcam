package com.freedcam.apis.camera1.camera.parameters.manual;

import android.hardware.Camera;

import com.freedcam.apis.basecamera.camera.modules.AbstractModuleHandler;
import com.freedcam.apis.basecamera.camera.modules.I_ModuleEvent;
import com.freedcam.apis.basecamera.camera.parameters.manual.AbstractManualParameter;
import com.freedcam.apis.basecamera.camera.parameters.modes.AbstractModeParameter;
import com.freedcam.apis.camera1.camera.parameters.CamParametersHandler;
import com.freedcam.apis.camera1.camera.parameters.modes.PictureFormatHandler;
import com.freedcam.utils.Logger;

/**
 * Created by troop on 17.08.2014.
 */
public class BaseManualParameter extends AbstractManualParameter
{

    private static String TAG = BaseManualParameter.class.getSimpleName();
    /**
     * Holds the list of Supported parameters
     */
    protected Camera.Parameters  parameters;
    /*
     * The name of the current key_value to get like brightness
     */
    protected String key_value;

    /**
     * The name of the current value to get like brightness-max
     */
    protected String key_max_value;
    /**
     * The name of the current value to get like brightness-min
     */
    protected String key_min_value;

    protected float step;

    protected CamParametersHandler camParametersHandler;



    private int default_value = 0;
    public void Set_Default_Value(int val){default_value = val; Logger.d(TAG, "set default to:" + val);}
    public int Get_Default_Value(){return default_value;}

    public void ResetToDefault()
    {
        if (isSupported)
        {
            Logger.d(TAG,"Reset Back from:" + currentInt + " to:" + default_value);
            SetValue(default_value);
            ThrowCurrentValueChanged(default_value);
        }
    }

    /**
     *
     * @param @parameters
     * @param @key_value
     * @param @key_max_value
     * @param @key_min_value
     * @param @camParametersHandler
     */
    public BaseManualParameter(Camera.Parameters  parameters, String key_value, String maxValue, String MinValue, CamParametersHandler camParametersHandler, float step) {
        super(camParametersHandler);
        this.camParametersHandler = camParametersHandler;
        this.parameters = parameters;
        this.key_value = key_value;
        this.key_max_value = maxValue;
        this.key_min_value = MinValue;
        this.step = step;
        if (!this.key_value.equals("") && !this.key_max_value.equals("") && !key_min_value.equals(""))
        {
            if (parameters.get(this.key_value) != null && parameters.get(key_max_value) != null && parameters.get(key_min_value) != null)
            {
                Logger.d(TAG, "parameters contains all 3 parameters " + key_value +" " + key_min_value +" " + key_max_value);
                if (!parameters.get(key_min_value).equals("") && !parameters.get(key_max_value).equals(""))
                {
                    Logger.d(TAG, "parameters get min/max success");
                    stringvalues = createStringArray(Integer.parseInt(parameters.get(key_min_value)), Integer.parseInt(parameters.get(key_max_value)), step);
                    currentString = parameters.get(this.key_value);
                    if (parameters.get(key_min_value).contains("-"))
                    {
                        Logger.d(TAG, "processing negative values");
                        currentInt = stringvalues.length /2 + Integer.parseInt(currentString);
                        default_value = currentInt;
                        this.isSupported = true;
                        this.isVisible = isSupported;
                    }
                    else
                    {
                        Logger.d(TAG, "processing positiv values");
                        for (int i = 0; i < stringvalues.length; i++) {
                            if (stringvalues[i].equals(currentString)) {
                                currentInt = i;
                                default_value = i;

                            }
                            this.isSupported = true;
                            this.isVisible = isSupported;
                        }
                    }

                }
                else
                    Logger.d(TAG, "min or max is empty in parameters");
            }
            else
                Logger.d(TAG, "parameters does not contain value, key_max_value or key_min_value");
        }
        else
            Logger.d(TAG, "failed to lookup value, key_max_value or key_min_value are empty");
    }
    @Override
    public boolean IsSupported()
    {
        return isSupported;
    }

    @Override
    public boolean IsSetSupported() {
        return true;
    }

    @Override
    public boolean IsVisible() {
        return isVisible;
    }

    @Override
    public int GetValue()
    {
        return super.GetValue();
    }

    @Override
    public void SetValue(int valueToset)
    {
        currentInt = valueToset;
        Logger.d(TAG, "set " + key_value + " to " + valueToset);
        if(stringvalues == null || stringvalues.length == 0)
            return;
        parameters.set(key_value, stringvalues[valueToset]);
        ThrowCurrentValueChanged(valueToset);
        ThrowCurrentValueStringCHanged(stringvalues[valueToset]);
        try
        {
            camParametersHandler.SetParametersToCamera(parameters);
        }
        catch (Exception ex)
        {
            Logger.exception(ex);
        }
    }


    public AbstractModeParameter.I_ModeParameterEvent GetPicFormatListner()
    {
        return picformatListner;
    }

    private AbstractModeParameter.I_ModeParameterEvent picformatListner = new AbstractModeParameter.I_ModeParameterEvent()
    {

        @Override
        public void onValueChanged(String val)
        {
           if (val.equals(PictureFormatHandler.CaptureMode[PictureFormatHandler.JPEG]) && BaseManualParameter.this.isSupported)
           {
               isVisible = true;
               BackgroundIsSupportedChanged(true);
           }
            else {
               isVisible = false;
               BackgroundIsSupportedChanged(false);
               ResetToDefault();
           }
        }

        @Override
        public void onIsSupportedChanged(boolean isSupported) {

        }

        @Override
        public void onIsSetSupportedChanged(boolean isSupported) {

        }

        @Override
        public void onValuesChanged(String[] values) {

        }

        @Override
        public void onVisibilityChanged(boolean visible) {

        }
    };

    public I_ModuleEvent GetModuleListner()
    {
        return moduleListner;
    }

    private I_ModuleEvent moduleListner =new I_ModuleEvent() {
        @Override
        public void ModuleChanged(String module)
        {
            if (module.equals(AbstractModuleHandler.MODULE_VIDEO) && isSupported)
                BackgroundIsSupportedChanged(true);
            else if (module.equals(AbstractModuleHandler.MODULE_PICTURE)
                    || module.equals(AbstractModuleHandler.MODULE_INTERVAL)
                    || module.equals(AbstractModuleHandler.MODULE_HDR))
            {
                BackgroundIsSupportedChanged(isVisible);
            }
        }
    };
}
