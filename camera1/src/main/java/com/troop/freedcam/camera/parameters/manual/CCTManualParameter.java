package com.troop.freedcam.camera.parameters.manual;

import android.hardware.Camera;
import android.os.Build;
import android.util.Log;

import com.troop.freedcam.camera.parameters.CamParametersHandler;
import com.troop.freedcam.i_camera.interfaces.I_CameraHolder;
import com.troop.freedcam.i_camera.parameters.AbstractParameterHandler;
import com.troop.freedcam.utils.DeviceUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

public class CCTManualParameter extends BaseManualParameter
{
    final String TAG = CCTManualParameter.class.getSimpleName();
    final String WBCURRENT = "wb-current-cct";
    final String WB_CCT = "wb-cct";
    final String WB_CT = "wb-ct";
    final String WB_MANUAL = "wb-manual-cct";
    final String MANUAL_WB_VALUE = "manual-wb-value";
    final String MAX_WB_CCT = "max-wb-cct";
    final String MIN_WB_CCT = "min-wb-cct";
    final String MAX_WB_CT = "max-wb-ct";
    final String MIN_WB_CT = "min-wb-ct";
    final String LG_Min = "lg-wb-supported-min";
    final String LG_Max = "lg-wb-supported-max";
    final String LG_WB = "lg-wb";
    final String WB_MODE_MANUAL = "manual";
    final String WB_MODE_MANUAL_CCT = "manual-cct";


    private int min = -1;
    private int max = -1;
    private String[] wbvalues;
    int currentWBPos = 0;
    private String manualWbMode;
    public CCTManualParameter(HashMap<String, String> parameters, String value, String maxValue, String MinValue,AbstractParameterHandler camParametersHandler)
    {
        super(parameters, value, maxValue, MinValue, camParametersHandler);

        this.isSupported = false;

        if (DeviceUtils.IS_DEVICE_ONEOF(DeviceUtils.MI3_4))
        {
            //TODO cm13 use different values, need rom detection
            if (Build.VERSION.SDK_INT < 23)
            {
                this.max = 7500;
                this.value = WB_MANUAL;
                this.max_value = MAX_WB_CCT;
                setmin(MIN_WB_CCT);
                this.isSupported = true;
                this.manualWbMode = WB_MODE_MANUAL_CCT;
                createStringArray();
            }
            else {
                this.max = 8000;
                this.value = MANUAL_WB_VALUE;
                this.max_value = MAX_WB_CCT;
                setmin(MIN_WB_CCT);
                this.manualWbMode = WB_MODE_MANUAL;
                this.isSupported = true;
                createStringArray();
            }
        }
        else if (DeviceUtils.IS(DeviceUtils.Devices.ZTE_ADV))
        {
            this.min = 2000;
            this.max = 8000;
            this.value = WB_MANUAL;
            this.manualWbMode = WB_MODE_MANUAL_CCT;
            this.isSupported = true;
            createStringArray();
        }
        else
        {
            //check first all possible values
            if (parameters.containsKey(WBCURRENT))
                this.value = WBCURRENT;
            else if (parameters.containsKey(WB_CCT))
                this.value = WB_CCT;
            else if (parameters.containsKey(WB_CT))
                this.value = WB_CT;
            else if (parameters.containsKey(WB_MANUAL))
                this.value = WB_MANUAL;
            else if (parameters.containsKey(MANUAL_WB_VALUE))
                this.value = MANUAL_WB_VALUE;
            else if (parameters.containsKey(LG_WB))
                this.value = LG_WB;

            //check all possible max values
            if (parameters.containsKey(MAX_WB_CCT)) {
                setmax(MAX_WB_CCT);
            }
            else if (parameters.containsKey(MAX_WB_CT))
                setmax(MAX_WB_CT);
            else if (parameters.containsKey(LG_Max))
                setmax(LG_Max);

            //check all possible min values
            if (parameters.containsKey(MIN_WB_CCT)) {
                setmin(MIN_WB_CCT);
            } else if (parameters.containsKey(MIN_WB_CT))
                setmin(MIN_WB_CT);
            else if (parameters.containsKey(LG_Min))
                setmin(LG_Min);

            //check wbmode manual
            if (arrayContainsString(camParametersHandler.WhiteBalanceMode.GetValues(), WB_MODE_MANUAL))
                this.manualWbMode = WB_MODE_MANUAL;
            else if (arrayContainsString(camParametersHandler.WhiteBalanceMode.GetValues(), WB_MODE_MANUAL_CCT))
                this.manualWbMode = WB_MODE_MANUAL_CCT;

            if (min != -1 && max != -1 && !this.value.equals(""))
            {
                isSupported = true;
                createStringArray();
            }
        }
        Log.d(TAG, "value:"+value + " max value:"+maxValue +" min value:" +min_value);
    }

    private boolean arrayContainsString(String[] ar,String dif)
    {
        boolean ret = false;
        for (String s: ar)
            if (s.equals(dif))
                ret = true;
        return ret;
    }

    private void setmax(String m)
    {
        this.max_value = m;
        max = Integer.parseInt(parameters.get(max_value));
    }

    private void setmin(String m)
    {
        this.min_value = m;
        min = Integer.parseInt(parameters.get(min_value));
    }

    private void createStringArray()
    {
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
        else
            return 0;
    }
    //M8 Step values "wb-ct-step"
    @Override
    public int GetMinValue()
    {
        if (wbvalues != null)
            return 0;
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
        currentWBPos = valueToSet;
        //set to auto
        if (currentWBPos == 0)
        {
            if (DeviceUtils.IS_DEVICE_ONEOF(DeviceUtils.HTC_m8_9))
            {
                parameters.put(value, "-1");
            }
            else if (DeviceUtils.IS(DeviceUtils.Devices.LG_G4))
                parameters.put(value, "0");
            else
                camParametersHandler.WhiteBalanceMode.SetValue("auto", true);
        }
        else //set manual wb mode and value
        {
            if(!camParametersHandler.WhiteBalanceMode.GetValue().equals(manualWbMode) && manualWbMode != "")
                camParametersHandler.WhiteBalanceMode.SetValue(manualWbMode, true);
            parameters.put(value, wbvalues[currentWBPos]);
        }
        camParametersHandler.SetParametersToCamera();

        /*if (wbvalues != null)
        {
            currentWBPos = valueToSet;
            if (currentWBPos == 0)
            {
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
            if(valueToSet != 0)
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
        camParametersHandler.SetParametersToCamera();*/

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


