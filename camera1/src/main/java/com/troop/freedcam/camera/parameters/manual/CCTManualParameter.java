package com.troop.freedcam.camera.parameters.manual;

import android.hardware.Camera;

import com.troop.freedcam.camera.BaseCameraHolder;
import com.troop.freedcam.camera.parameters.CamParametersHandler;
import com.troop.freedcam.i_camera.interfaces.I_CameraHolder;
import com.troop.freedcam.i_camera.parameters.AbstractParameterHandler;
import com.troop.freedcam.utils.DeviceUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

public class CCTManualParameter extends BaseManualParameter {
	
	I_CameraHolder baseCameraHolder;
    public CCTManualParameter(HashMap<String, String> parameters, String value, String maxValue, String MinValue,AbstractParameterHandler camParametersHandler) {
        super(parameters, value, maxValue, MinValue, camParametersHandler);
        try {
            String t = parameters.get("max-wb-cct");
            if (t != null || t.equals(""))
            {
                this.value = "wb-cct";
                this.max_value = "max-wb-cct";
                this.min_value = "min-wb-cct";
                this.isSupported = true;
            }
        }
        catch (Exception ex)
        {}
        if (!isSupported)
        {
            try {
                String t = parameters.get("max-wb-ct");
                if (t != null || t.equals(""))
                {
                    this.value = "wb-ct";
                    this.max_value = "max-wb-ct";
                    this.min_value = "min-wb-ct";
                    this.isSupported = true;
                }
            }
            catch (Exception ex)
            {}
        }
        //TODO add missing logic
    }
    public CCTManualParameter(HashMap<String, String> parameters, String value, String maxValue, String MinValue, I_CameraHolder cameraHolder, AbstractParameterHandler camParametersHandler) {
        super(parameters, value, maxValue, MinValue, camParametersHandler);

        this.baseCameraHolder = cameraHolder;
        //TODO add missing logic
    }
    
    @Override
    public boolean IsSupported()
    {
        return isSupported;
    }

    @Override
    public int GetMaxValue() {
    	return Integer.parseInt(parameters.get(max_value));

    }
//M8 Step values "wb-ct-step"
    @Override
    public int GetMinValue() {
	    return Integer.parseInt(parameters.get(min_value));
    }

    @Override
    public int GetValue()
    {
        int i = 0;
        try {
            if (DeviceUtils.isZTEADV())
                i = -1;
            else if (DeviceUtils.isLGADV())
                i = getCTReflection();
            else
                i = Integer.parseInt(parameters.get(value));
        }
        catch (Exception ex)
        {

        }

        return i;
    }

    @Override
    public String GetStringValue() {
        return null;
    }

    @Override
    protected void setvalue(int valueToSet)
    {
        if (DeviceUtils.isZTEADV())
        {
            if(valueToSet != -1)
            {
                camParametersHandler.WhiteBalanceMode.SetValue("manual-cct", true);
                parameters.put("wb-manual-cct", valueToSet + "");
            }
            else
                camParametersHandler.WhiteBalanceMode.SetValue("auto", true);
        }
        if (DeviceUtils.isLGADV())
            setCTReflection(valueToSet);
            //parameters.put("cct", valueToSet + "");

        if (DeviceUtils.isHTC_M8()|| DeviceUtils.isHTC_M9())
            parameters.put("wb-ct", valueToSet + "");
        camParametersHandler.SetParametersToCamera();

    }

    private int getCTReflection()
    {
        int ret = 0;
        Camera.Parameters param = ((CamParametersHandler)camParametersHandler).baseCameraHolder.GetCamera().getParameters();
        try {
            Class camera = Class.forName("android.hardware.Camera");
            Class[] intefaces = camera.getClasses();

            Class parameters = null;
            for (Class i : intefaces)
            {
                if (i.getSimpleName().equals("Parameters"))
                    parameters = i;
            }
            Method[] meths = parameters.getMethods();
            Method getCt = null;
            for (Method m : meths)
            {
                if (m.getName().equals("getWBCurrentCCT"))
                    getCt = m;
            }
            Object r = getCt.invoke(param, null);
            ret = Integer.parseInt((String)r);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        catch (NullPointerException ex){ex.printStackTrace();}
        return ret;
    }

    private void setCTReflection(int val)
    {
        Camera.Parameters param = ((CamParametersHandler)camParametersHandler).baseCameraHolder.GetCamera().getParameters();
        try {
            Class camera = Class.forName("android.hardware.Camera");
            Class[] intefaces = camera.getClasses();

            Class parameters = null;
            for (Class i : intefaces)
            {
                if (i.getSimpleName().equals("Parameters"))
                    parameters = i;
            }
            Method[] meths = parameters.getMethods();
            Method getCt = null;
            for (Method m : meths)
            {
                if (m.getName().equals("setWBManualCCT"))
                    getCt = m;
            }
            getCt.invoke(param, val);

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        catch (NullPointerException ex){ex.printStackTrace();}
    }

}


