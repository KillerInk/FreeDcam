package com.troop.freedcam.sonyapi.parameters.manual;

import android.util.Log;

import com.troop.freedcam.sonyapi.parameters.ParameterHandlerSony;
import com.troop.freedcam.sonyapi.sonystuff.JsonUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Set;

/**
 * Created by Ingo on 19.04.2015.
 */
public class ProgramShiftManualSony extends BaseManualParameterSony
{
    final String TAG = ProgramShiftManualSony.class.getSimpleName();
    int min =-1000;
    int max =-1000;
    public ProgramShiftManualSony(String VALUE_TO_GET, String VALUES_TO_GET, String VALUE_TO_SET, ParameterHandlerSony parameterHandlerSony) {
        super(VALUE_TO_GET, VALUES_TO_GET, VALUE_TO_SET, parameterHandlerSony);
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
        return min;
    }

    @Override
    public int GetMaxValue()
    {
        if (max == -1000)
            getminmax();
        return max;
    }

    @Override
    public String[] getStringValues()
    {
        //getminmax();
        Log.d(TAG, "Returning values from: " + VALUES_TO_GET);
        return null;

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
                        if (values.length != 2)
                            return;
                        max = Integer.parseInt(values[0]);
                        BackgroundMaxValueChanged(max);
                        min = Integer.parseInt(values[1]);
                        BackgroundMinValueChanged(min);
                        values = null;


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
}
