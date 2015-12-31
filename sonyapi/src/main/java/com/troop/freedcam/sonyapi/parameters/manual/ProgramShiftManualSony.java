package com.troop.freedcam.sonyapi.parameters.manual;

import android.util.Log;

import com.troop.freedcam.sonyapi.parameters.ParameterHandlerSony;
import com.troop.freedcam.sonyapi.sonystuff.JsonUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;

/**
 * Created by Ingo on 19.04.2015.
 */
public class ProgramShiftManualSony extends BaseManualParameterSony
{
    final String TAG = ProgramShiftManualSony.class.getSimpleName();
    int min =-1000;
    int max =-1000;
    private String[] values;
    private BaseManualParameterSony shutter;
    private BaseManualParameterSony fnumber;
    public ProgramShiftManualSony(String VALUE_TO_GET, String VALUES_TO_GET, String VALUE_TO_SET, ParameterHandlerSony parameterHandlerSony) {
        super(VALUE_TO_GET, VALUES_TO_GET, VALUE_TO_SET, parameterHandlerSony);
        this.shutter = (BaseManualParameterSony)parameterHandlerSony.ManualShutter;
        this.fnumber = (BaseManualParameterSony)parameterHandlerSony.ManualFNumber;
    }

    @Override
    public void SonyApiChanged(Set<String> mAvailableCameraApiSet)
    {
        this.mAvailableCameraApiSet = mAvailableCameraApiSet;
        if (isSupported != JsonUtils.isCameraApiAvailable(VALUE_TO_SET, mAvailableCameraApiSet))
        {
            isSupported = JsonUtils.isCameraApiAvailable(VALUE_TO_SET, mAvailableCameraApiSet);
        }
        BackgroundIsSupportedChanged(isSupported);
        BackgroundIsSetSupportedChanged(true);
        if (isSetSupported != JsonUtils.isCameraApiAvailable(VALUE_TO_SET, mAvailableCameraApiSet))
        {
            isSetSupported = JsonUtils.isCameraApiAvailable(VALUE_TO_SET, mAvailableCameraApiSet);
        }
        BackgroundIsSetSupportedChanged(isSetSupported);

    }

    @Override
    public int GetMinValue()
    {
        if (min == -1000)
            getminmax();
        return 0;
    }

    @Override
    public int GetMaxValue()
    {
        if (max == -1000)
            getminmax();
        return values.length;
    }

    @Override
    public String[] getStringValues()
    {
        if (values == null)
            getminmax();
        return values;
    }

    @Override
    public String GetStringValue()
    {
        if (values == null)
            getminmax();
        return values[val];
    }

    private void getminmax() {
        if (isSupported && isSetSupported)
        {
            new Thread(new Runnable()
            {
                @Override
                public void run()
                {
                    try
                    {
                        Log.d(TAG, "Trying to get String Values from: " + VALUES_TO_GET);
                        JSONObject object =  ParameterHandler.mRemoteApi.getParameterFromCamera(VALUES_TO_GET);
                        JSONArray array = object.getJSONArray("result");
                        JSONArray subarray = array.getJSONArray(0);
                        values = JsonUtils.ConvertJSONArrayToStringArray(subarray);
                        if (values == null || values.length != 2)
                            return;
                        max = Integer.parseInt(values[0]);
                        min = Integer.parseInt(values[1]);
                        ArrayList<String> r = new ArrayList<String>();
                        for (int i = min; i<= max; i++)
                        {
                            r.add(i+"");
                        }
                        values =new String[r.size()];

                        String[] shut = shutter.getStringValues();
                        if (shut != null && r != null && shut.length == r.size())
                        {
                            String s = shutter.GetStringValue();
                            for (int i = 0; i < shut.length; i++)
                            {
                                if (s.equals(shut[i]))
                                {
                                    val = i;
                                    break;
                                }
                            }
                        }
                        r.toArray(values);
                        BackgroundValuesChanged(values);
                        BackgroundMinValueChanged(min);
                        BackgroundMaxValueChanged(max);
                        onCurrentValueChanged(val);


                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.e(TAG, "Error Trying to get String Values from: " +VALUES_TO_GET);
                        values = new String[0];
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e(TAG, "Error Trying to get String Values from: " + VALUES_TO_GET);
                        values = new String[0];
                    }
                }
            }).start();
            while (values == null)
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
        }
    }

    @Override
    public void SetValue(final int valueToSet)
    {
        this.val = valueToSet;
        new Thread(new Runnable() {
            @Override
            public void run()
            {
                JSONArray array = null;
                try {
                    array = new JSONArray().put(0, val);
                    JSONObject object =  ParameterHandler.mRemoteApi.setParameterToCamera(VALUE_TO_SET, array);
                    currentValueChanged(valueToSet);
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public int GetValue() {
        return val;
    }

    @Override
    public void onCurrentValueChanged(int current) {
        this.val = current;
    }

    @Override
    public void onMaxValueChanged(int max) {
        this.max = max;
    }

    @Override
    public void onMinValueChanged(int min) {
        this.min = min;
    }
}
