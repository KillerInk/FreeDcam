package com.troop.freedcam.camera.parameters.manual;

import android.hardware.Camera;

import com.lge.media.TimedTextEx;
import com.troop.freedcam.camera.parameters.CamParametersHandler;
import com.troop.freedcam.i_camera.interfaces.I_CameraHolder;
import com.troop.freedcam.i_camera.parameters.AbstractParameterHandler;
import com.troop.freedcam.utils.DeviceUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

public class CCTManualParameter extends BaseManualParameter {
	
	I_CameraHolder baseCameraHolder;
    public CCTManualParameter(HashMap<String, String> parameters, String value, String maxValue, String MinValue,AbstractParameterHandler camParametersHandler)
    {
        super(parameters, value, maxValue, MinValue, camParametersHandler);
        if (DeviceUtils.isSonyM5_MTK())
        {
            //temp disable
            this.isSupported = false;
        }

       else if (DeviceUtils.isMoto_MSM8974())
        {
            this.isSupported = false;
        }
      else  if (DeviceUtils.isOnePlusOne())
        {
            this.value = "wb-current-cct";
            this.min_value = "min-wb-cct";
            this.max_value = "max-wb-cct";
            this.isSupported = true;
        }
        else if (DeviceUtils.isRedmiNote()  || !DeviceUtils.isZTEADV()||!DeviceUtils.isZTEADVIMX214()||!DeviceUtils.isZTEADV234())
        {
            this.value = "wb-manual-cct";
            this.max_value = "max-wb-cct";
            this.min_value = "min-wb-cct";
            this.isSupported = true;
        }
        else if (parameters.containsKey("wb-cct"))
        {
            this.value = "wb-cct";
            this.max_value = "max-wb-cct";
            this.min_value = "min-wb-cct";
            this.isSupported = true;
        }
        else if (parameters.containsKey("wb-ct"))
        {
            this.value = "wb-ct";
            this.max_value = "max-wb-ct";
            this.min_value = "min-wb-ct";
            this.isSupported = true;
        } //&& !DeviceUtils.isZTEADV()
        else if (parameters.containsKey("wb-manual-cct") || DeviceUtils.isMoto_MSM8982_8994()||DeviceUtils.isAlcatel_Idol3())
        {
            try {


                this.value = "wb-manual-cct";
                this.max_value = "max-wb-cct";
                this.min_value = "min-wb-cct";
                this.isSupported = true;

            }
            catch (NullPointerException ex)
            {
                this.isSupported=false;
            }
        }
        else
            this.isSupported=false;
        //force close app
        /*else if (DeviceUtils.isG4()) {
            this.value = "lg-wb";
            this.max_value = "lg-wb-supported-max";
            this.min_value = "lg-wb-supported-min";
            this.isSupported = true;
        }*/
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
        if (DeviceUtils.isZTEADV()||DeviceUtils.isZTEADVIMX214()||DeviceUtils.isZTEADV234())
            return 150;
        else if(parameters.containsKey("wb-manual-cct"))
        {
            try {
                return Integer.parseInt(parameters.get("max-wb-cct"));
            }
            catch (NullPointerException ex)
            {
                return 0;
            }
        }
        else
        try {
            if(DeviceUtils.isMoto_MSM8974())
            {
                return 8000;
            }
            else {
            String wbct  = parameters.get(max_value);
            if(wbct.equals("null"))
            {
                isSupported = false;
                wbct = "0";
            }
            return Integer.parseInt(wbct);
        }
        catch (NullPointerException ex)
        {
            return 0;
        }


    }
//M8 Step values "wb-ct-step"
    @Override
    public int GetMinValue()
    {
        if (DeviceUtils.isZTEADV()||DeviceUtils.isZTEADVIMX214()||DeviceUtils.isZTEADV234())
            return -1;
        else if(parameters.containsKey("wb-manual-cct"))
        {
            return Integer.parseInt(parameters.get("min-wb-cct"));
        }
        else
        try {
            return Integer.parseInt(parameters.get(min_value));
        }
        catch (NumberFormatException ex)
        {
            ex.printStackTrace();
        }
        return 0;

    }

    @Override
    public int GetValue()
    {


            return 0;
    }

    @Override
   public String GetStringValue() {
        return null;
    }

    @Override
    protected void setvalue(int valueToSet)
    {
        if (DeviceUtils.isZTEADV()||DeviceUtils.isZTEADVIMX214()||DeviceUtils.isZTEADV234())
        {
            if(valueToSet != -1)
            {
                try {
                    camParametersHandler.WhiteBalanceMode.SetValue("manual-cct", true);

                    parameters.put("wb-manual-cct", String.valueOf(valueToSet * 40 + 2000));
                }
                catch (Exception exc)
                {

                }
            }
            else
                camParametersHandler.WhiteBalanceMode.SetValue("auto", true);
        }
        else if (parameters.containsKey("wb-manaul-cct") ||DeviceUtils.isAlcatel_Idol3() || DeviceUtils.isMoto_MSM8982_8994())
        {
            try{
            camParametersHandler.WhiteBalanceMode.SetValue("manual", true);}
            catch (Exception c)
            {
                System.out.println("Freedcam Error Setting Manual Color Temp");
            }
                        parameters.put("wb-manual-cct", valueToSet + "");
        }
        else if (DeviceUtils.isLG_G3())
            setCTReflection(valueToSet);
            //parameters.put("cct", valueToSet + "");

        else if (DeviceUtils.isHTC_M8()|| DeviceUtils.isHTC_M9())
            parameters.put("wb-ct", valueToSet + "");
        else if(DeviceUtils.isG4()) {
            //"lg-manual-mode-reset"
            parameters.put("lge-camera", "1");
            parameters.put("lg-wb", valueToSet + "");
        }
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


