package com.freedcam.apis.sonyremote.sonyapi.parameters.manual;

import com.freedcam.apis.sonyremote.sonyapi.parameters.ParameterHandlerSony;
import com.freedcam.utils.Logger;
import com.freedcam.utils.FreeDPool;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by troop on 03.01.2015.
 */
public class ExposureCompManualParameterSony extends BaseManualParameterSony
{
    private static String TAG = ExposureCompManualParameterSony.class.getSimpleName();
    public ExposureCompManualParameterSony(String VALUES_TO_GET, String VALUE_TO_SET, ParameterHandlerSony parameterHandlerSony) {
        super("getExposureCompensation", "getAvailableExposureCompensation", "setExposureCompensation", parameterHandlerSony);
        currentInt = -200;
    }

    @Override
    public void SetValue(final int valueToSet)
    {
        this.currentInt = valueToSet;
        FreeDPool.Execute(new Runnable() {
            @Override
            public void run() {
                //String val = valueToSet +"";
                JSONArray array = null;
                if (stringvalues == null)
                {
                    getMinMaxValues();
                    return;
                }
                try {
                    Logger.d(TAG, "SetValue " + valueToSet);
                    array = new JSONArray().put(0, Integer.parseInt(stringvalues[valueToSet]));
                    JSONObject object =  ParameterHandler.mRemoteApi.setParameterToCamera(VALUE_TO_SET, array);

                        //ThrowCurrentValueChanged(valueToSet);
                } catch (JSONException e) {
                    Logger.exception(e);
                    Logger.e(TAG, "Error SetValue " + valueToSet);
                } catch (IOException e)
                {
                    Logger.e(TAG, "Error SetValue " + valueToSet);
                    Logger.exception(e);
                }
            }
        });
    }

    private void getMinMaxValues()
    {
        if (stringvalues == null)
        {
            FreeDPool.Execute(new Runnable()
            {
                @Override
                public void run()
                {
                    try {
                        Logger.d(TAG, "try get min max values ");
                        JSONObject object =  ParameterHandler.mRemoteApi.getParameterFromCamera(VALUES_TO_GET);
                        JSONArray array = object.getJSONArray("result");
                        int min = array.getInt(2);
                        int max = array.getInt(1);
                        stringvalues = createStringArray(min,max,1);
                    } catch (IOException | JSONException e)
                    {

                        Logger.e(TAG, "Error getMinMaxValues ");
                        Logger.exception(e);

                    }
                }
            });
        }
    }

    @Override
    public int GetValue()
    {
        if (currentInt == -100) {
            FreeDPool.Execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        JSONObject object = mRemoteApi.getParameterFromCamera(VALUE_TO_GET);
                        JSONArray array = object.getJSONArray("result");
                        currentInt = array.getInt(0);
                        //onCurrentValueChanged(val);
                    } catch (IOException | JSONException e) {
                        Logger.exception(e);
                        Logger.e(TAG, "Error GetValue() ");

                    }
                }
            });
        }
        return currentInt;
    }


    @Override
    public void onCurrentValueChanged(int current) {
        this.currentInt = current;
    }

    public String[] getStringValues()
    {
        if (stringvalues == null)
            getMinMaxValues();
        return stringvalues;
    }

    @Override
    public String GetStringValue()
    {
        if (stringvalues != null && currentInt != -100)
            return stringvalues[currentInt];
        return 0+"";
    }
}
