package com.troop.freedcam.camera.parameters.manual;

import android.util.Log;

import com.troop.freedcam.camera.parameters.modes.PictureFormatHandler;
import com.troop.freedcam.i_camera.modules.AbstractModuleHandler;
import com.troop.freedcam.i_camera.modules.I_ModuleEvent;
import com.troop.freedcam.i_camera.parameters.AbstractManualParameter;
import com.troop.freedcam.i_camera.parameters.AbstractModeParameter;
import com.troop.freedcam.i_camera.parameters.AbstractParameterHandler;
import com.troop.freedcam.utils.StringUtils;

import java.util.HashMap;

/**
 * Created by troop on 17.08.2014.
 */
public abstract class BaseManualParameter extends AbstractManualParameter
{

    private static String TAG = StringUtils.TAG + BaseManualParameter.class.getSimpleName();
    /**
     * Holds the list of Supported parameters
     */
    HashMap<String, String> parameters;
    /*
     * The name of the current value to get like brightness
     */
    protected String value;

    /**
     * The name of the current value to get like brightness-max
     */
    protected String max_value;
    /**
     * The name of the current value to get like brightness-min
     */
    protected String  min_value;

    /**
     * holds the state if the parameter is supported
     */
    boolean isSupported = false;


    private int default_value = 0;
    public void Set_Default_Value(int val){default_value = val;}
    public int Get_Default_Value(){return default_value;}

    public void ResetToDefault()
    {
        if (isSupported)
        {
            setvalue(default_value);
            ThrowCurrentValueChanged(default_value);
        }
    }

    /**
     *
     * @param @parameters
     * @param @value
     * @param @max_value
     * @param @min_value
     * @param @camParametersHandler
     */
    public BaseManualParameter(HashMap<String, String> parameters, String value, String maxValue, String MinValue, AbstractParameterHandler camParametersHandler)
    {
        super(camParametersHandler);
        this.parameters = parameters;
        this.value = value;
        this.max_value = maxValue;
        this.min_value = MinValue;
    }


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
        return super.IsVisible();
    }

    public int GetMaxValue()
    {
        int max = 100;
        try {
            max = Integer.parseInt(parameters.get(max_value));
        }
        catch (Exception ex)
        {}
        Log.d(TAG, "get " + max_value + " to " + parameters.get(max_value));
        return max;
    }

    public  int GetMinValue()
    {
        int ret = 0;
        try
        {
            Log.d(TAG, "get " + min_value + " to " + parameters.get(min_value));
            ret = Integer.parseInt(parameters.get(min_value));
        }
        catch (Exception ex)
        {
            ret = 0;
            Log.d(TAG, "get " + min_value + " to " + 0);
        }
        return ret;
    }

    public int GetValue()
    {
        if (parameters == null || value == null)
            return 0;
        Log.d(TAG, "get " + value + ": " +parameters.get(value));
        try {
            return Integer.parseInt(parameters.get(value));
        }
        catch (NumberFormatException ex)
        {
            ex.printStackTrace();
            return 0;
        }

    }

    @Override
    protected void setvalue(int valueToset)
    {
        Log.d(TAG, "set " + value + " to " + valueToset);
        parameters.put(value, valueToset + "");
        try
        {
            camParametersHandler.SetParametersToCamera();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

    }

    protected boolean hasSupport()
    {
        try
        {
            if (parameters.containsKey(value))
                isSupported = true;
            else
                isSupported = false;
            isVisible = isSupported;
        }
        catch (Exception ex)
        {
            isSupported = false;
        }
        Log.d(TAG, "issupported " + value + ": " + isSupported);
        return isSupported;
    }

    @Override
    public void RestartPreview() {

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
           if (val.equals(PictureFormatHandler.CaptureMode[PictureFormatHandler.JPEG]) && isSupported)
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
        public String ModuleChanged(String module)
        {
            if (module.equals(AbstractModuleHandler.MODULE_VIDEO) && isSupported)
                BackgroundIsSupportedChanged(true);
            else if (module.equals(AbstractModuleHandler.MODULE_PICTURE)
                    || module.equals(AbstractModuleHandler.MODULE_INTERVAL)
                    || module.equals(AbstractModuleHandler.MODULE_HDR))
            {
                BackgroundIsSupportedChanged(isVisible);
            }
            return null;
        }
    };
}
