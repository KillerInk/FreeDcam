package com.troop.freedcam.camera.parameters.manual;

import android.hardware.Camera;
import android.os.Build;

import com.troop.freedcam.camera.parameters.CamParametersHandler;
import com.troop.freedcam.i_camera.interfaces.I_CameraHolder;
import com.troop.freedcam.i_camera.parameters.AbstractParameterHandler;
import com.troop.freedcam.utils.DeviceUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

public class CCTManualParameter extends BaseManualParameter {

    I_CameraHolder baseCameraHolder;
    final String WBCURRENT = "wb-current-cct";
    final String WB_CCT = "wb-cct";
    final String WB_CT = "wb-ct";
    final String WB_MANUAL = "wb-manual-cct";
    final String MAX_WB_CCT = "max-wb-cct";
    final String MIN_WB_CCT = "min-wb-cct";
    final String MAX_WB_CT = "max-wb-ct";
    final String MIN_WB_CT = "min-wb-ct";
    final String LG_Min = "lg-wb-supported-min";
    final String LG_Max = "lg-wb-supported-max";
    final String LG_WB = "lg-wb";



    String[] wbvalues;
    int currentWBPos = 0;
    public CCTManualParameter(HashMap<String, String> parameters, String value, String maxValue, String MinValue,AbstractParameterHandler camParametersHandler)
    {
        super(parameters, value, maxValue, MinValue, camParametersHandler);

        this.isSupported = false;
        if (parameters.containsKey(WBCURRENT) && parameters.containsKey(MAX_WB_CCT) && parameters.containsKey(MIN_WB_CCT))
        {
            this.value = WBCURRENT;
            this.max_value = MAX_WB_CCT;
            this.min_value = MIN_WB_CCT;
            this.isSupported = true;
            createStringArray();
        }
        else if (parameters.containsKey(WB_CCT) && parameters.containsKey(MAX_WB_CCT) && parameters.containsKey(MIN_WB_CCT))
        {
            this.value = WB_CCT;
            this.max_value = MAX_WB_CCT;
            this.min_value = MIN_WB_CCT;
            this.isSupported = true;
            createStringArray();
        }
        else if (parameters.containsKey(WB_CT) && parameters.containsKey(MAX_WB_CT) &&  parameters.containsKey(MIN_WB_CT))
        {
            this.value = WB_CT;
            this.max_value = MAX_WB_CT;
            this.min_value = MIN_WB_CT;
            this.isSupported = true;
            createStringArray();
        } //&& !DeviceUtils.isZTEADV()
        else if (DeviceUtils.IS_DEVICE_ONEOF(DeviceUtils.MI3_4))
        {
            if (Build.VERSION.SDK_INT < 23)
            {
                this.value = WB_MANUAL;
                this.max_value = MAX_WB_CCT;
                this.min_value = MIN_WB_CCT;
                this.isSupported = true;
                createStringArray();
            }
            else
            {
                this.value = "manual-wb-value";
                this.max_value = MAX_WB_CCT;
                this.min_value = MIN_WB_CCT;
                this.isSupported = true;
                createStringArray();
            }
        }
        else if (DeviceUtils.IS_DEVICE_ONEOF(DeviceUtils.ZTE_DEVICES) )
            isSupported = true;
        else if (parameters.containsKey(LG_Max) && parameters.containsKey(LG_Min) && parameters.containsKey(LG_WB))
        {
            this.value = LG_WB;
            this.max_value = LG_Max;
            this.min_value = LG_Min;
            this.isSupported = true;
            createStringArray();
        }
        else
            this.isSupported=false;
    }

    public CCTManualParameter(HashMap<String, String> parameters, String value, String maxValue, String MinValue, I_CameraHolder cameraHolder, AbstractParameterHandler camParametersHandler) {
        super(parameters, value, maxValue, MinValue, camParametersHandler);

        this.baseCameraHolder = cameraHolder;
        //TODO add missing logic
    }

    private void createStringArray()
    {
        int min = Integer.parseInt(parameters.get(min_value));
        int max = Integer.parseInt(parameters.get(max_value));
        if (DeviceUtils.IS_DEVICE_ONEOF(DeviceUtils.MI3_4) && Build.VERSION.SDK_INT < 23)
                    max = 7500;
        ArrayList<String> t = new ArrayList<String>();
        t.add("Auto");
        for (int i = min; i<=max;i+=100)
        {
            t.add(i+"");
        }
        wbvalues = new String[t.size()];
        t.toArray(wbvalues);
    }

    @Override
    public boolean IsSupported()
    {
        return this.isSupported;
    }

    @Override
    public boolean IsVisible() {
        return IsSupported();
    }

    @Override
    public int GetMaxValue()
    {
        if (wbvalues != null)
            return wbvalues.length-1;
        else if (DeviceUtils.IS_DEVICE_ONEOF(DeviceUtils.ZTE_DEVICES) )
            return 150;
        else if (DeviceUtils.IS(DeviceUtils.Devices.Moto_MSM8974))
            return 8000;
         else if (DeviceUtils.IS_DEVICE_ONEOF(DeviceUtils.MI3_4))
            {
                if (Build.VERSION.SDK_INT < 23)
                {
                    return 7500;
                }
                else
                {
                    return 8000;
                }
            }

        else
            return 0;
    }
    //M8 Step values "wb-ct-step"
    @Override
    public int GetMinValue()
    {
        if (wbvalues != null)
            return 0;
        else if (DeviceUtils.IS_DEVICE_ONEOF(DeviceUtils.ZTE_DEVICES) )
            return -1;
        else
            return 0;
    }

    @Override
    public int GetValue()
    {
        return currentWBPos;
    }

    @Override
    public String GetStringValue()
    {
        if (wbvalues != null)
            return wbvalues[currentWBPos];
        return null;
    }

    @Override
    protected void setvalue(int valueToSet)
    {
        if (wbvalues != null)
        {
            currentWBPos = valueToSet;
            if (currentWBPos == 0)
            {
                if (DeviceUtils.IS_DEVICE_ONEOF(DeviceUtils.HTC_m8_9))
                {
                    parameters.put(value, "-1");
                }
                else if (max_value.equals(LG_Max))
                    parameters.put(value, "0");
                else
                    camParametersHandler.WhiteBalanceMode.SetValue("auto", true);
            }
            else
            {
                if ((DeviceUtils.IS(DeviceUtils.Devices.OnePlusOne) || DeviceUtils.IS(DeviceUtils.Devices.RedmiNote))
                        && !camParametersHandler.WhiteBalanceMode.GetValue().equals("manual-cct"))
                    camParametersHandler.WhiteBalanceMode.SetValue("manual-cct", true);

                else if (DeviceUtils.IS_DEVICE_ONEOF(DeviceUtils.MI3_4))
                {
                    if (Build.VERSION.SDK_INT < 23)
                    {
                        camParametersHandler.WhiteBalanceMode.SetValue("manual-cct", true);
                    }
                    else
                    {
                        camParametersHandler.WhiteBalanceMode.SetValue("manual", true);
                    }
                }
                else if (!camParametersHandler.WhiteBalanceMode.GetValue().equals("manual") && (DeviceUtils.IS_DEVICE_ONEOF(DeviceUtils.AlcatelIdol3_Moto_MSM8982_8994)))
                camParametersHandler.WhiteBalanceMode.SetValue("manual", true);
                parameters.put(value, wbvalues[currentWBPos]);
            }
        }
        else if (DeviceUtils.IS_DEVICE_ONEOF(DeviceUtils.ZTE_DEVICES) )
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

    @Override
    public String[] getStringValues() {
        return wbvalues;
    }
}


