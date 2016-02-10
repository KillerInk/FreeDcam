package com.troop.freedcam.sonyapi.parameters.manual;

import android.util.Log;

import com.troop.freedcam.sonyapi.parameters.ParameterHandlerSony;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by troop on 03.01.2015.
 */
public class ExposureCompManualParameterSony extends BaseManualParameterSony
{
    int min = -1;
    int max = -1;

    private static String TAG = ExposureCompManualParameterSony.class.getSimpleName();
    public ExposureCompManualParameterSony(String VALUE_TO_GET, String VALUES_TO_GET, String VALUE_TO_SET, ParameterHandlerSony parameterHandlerSony) {
        super(VALUE_TO_GET, VALUES_TO_GET, VALUE_TO_SET, parameterHandlerSony);
        val = -200;
    }

    @Override
    public int GetMaxValue()
    {
        if (max == -1)
        {
            Log.d(TAG, "GetMaxValue() max not loaded loading it");
            getMinMaxValues();
        }
        return max;
    }

    @Override
    public int GetMinValue()
    {
        if (min == -1)
        {
            Log.d(TAG, "GetMinValue() min not loaded loading it");
            getMinMaxValues();
        }
        return min;
    }

    @Override
    public void SetValue(final int valueToSet)
    {
        this.val = valueToSet;
        new Thread(new Runnable() {
            @Override
            public void run()
            {
                //String val = valueToSet +"";
                JSONArray array = null;
                try {
                    Log.d(TAG, "SetValue " + valueToSet);
                    array = new JSONArray().put(0, valueToSet);
                    JSONObject object =  ParameterHandler.mRemoteApi.setParameterToCamera(VALUE_TO_SET, array);

                        //ThrowCurrentValueChanged(valueToSet);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e(TAG, "Error SetValue " + valueToSet);
                } catch (IOException e)
                {
                    Log.e(TAG, "Error SetValue " + valueToSet);
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void getMinMaxValues()
    {
        if (min == -1 && max == -1)
        {
            new Thread(new Runnable()
            {
                @Override
                public void run()
                {
                    try {
                        Log.d(TAG, "try get min max values ");
                        JSONObject object =  ParameterHandler.mRemoteApi.getParameterFromCamera(VALUES_TO_GET);
                        JSONArray array = object.getJSONArray("result");
                        min = array.getInt(2);
                        max = array.getInt(1);
                    } catch (IOException e)
                    {

                        Log.e(TAG, "Error getMinMaxValues ");
                        e.printStackTrace();

                    } catch (JSONException e)
                    {

                        Log.e(TAG, "Error getMinMaxValues ");
                        e.printStackTrace();

                    }
                }
            }).start();
            /*while (max == -1 && min == -1)
            {
                try {
                    Thread.sleep(10);
                    Log.d(TAG, "Wait for getMinMaxValues");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }*/
        }
    }

    @Override
    public int GetValue()
    {
        if (val == -100) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        JSONObject object = mRemoteApi.getParameterFromCamera(VALUE_TO_GET);
                        JSONArray array = object.getJSONArray("result");
                        val = array.getInt(0);
                        //onCurrentValueChanged(val);
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.e(TAG, "Error GetValue() ");

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e(TAG, "Error GetValue() ");

                    }
                }
            }).start();
            while (val == -200)
                try {
                    Thread.sleep(10);
                    Log.d(TAG, "Wait for getValues");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
        }
        return val;
    }

    @Override
    public void onMaxValueChanged(int max) {
        this.max = max;
    }

    @Override
    public void onMinValueChanged(int min) {
        this.min = min;
    }

    @Override
    public void onCurrentValueChanged(int current) {
        this.val = current;
    }

    public String[] getStringValues()
    {
        return null;
    }


}
