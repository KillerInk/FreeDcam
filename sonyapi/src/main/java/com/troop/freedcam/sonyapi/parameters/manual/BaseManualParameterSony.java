package com.troop.freedcam.sonyapi.parameters.manual;

import android.util.Log;

import com.troop.freedcam.i_camera.parameters.AbstractManualParameter;
import com.troop.freedcam.sonyapi.parameters.ParameterHandlerSony;
import com.troop.freedcam.sonyapi.parameters.modes.I_SonyApi;
import com.troop.freedcam.sonyapi.sonystuff.JsonUtils;
import com.troop.freedcam.sonyapi.sonystuff.SimpleRemoteApi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Set;

/**
 * Created by troop on 15.12.2014.
 */
public class BaseManualParameterSony extends AbstractManualParameter implements I_SonyApi, AbstractManualParameter.I_ManualParameterEvent
{
    protected String VALUE_TO_GET;
    protected String VALUES_TO_GET;
    protected String VALUE_TO_SET;
    protected ParameterHandlerSony ParameterHandler;
    protected SimpleRemoteApi mRemoteApi;
    protected Set<String> mAvailableCameraApiSet;
    boolean isSupported = false;
    boolean isSetSupported = false;
    String[] values;
    int val = -200;
    String value;

    private static String TAG = BaseManualParameterSony.class.getSimpleName();

    public BaseManualParameterSony(String VALUE_TO_GET, String VALUES_TO_GET, String VALUE_TO_SET, ParameterHandlerSony parameterHandlerSony)
    {
        super(parameterHandlerSony);
        this.VALUE_TO_GET = VALUE_TO_GET;
        this.VALUES_TO_GET = VALUES_TO_GET;
        this.VALUE_TO_SET = VALUE_TO_SET;
        this.ParameterHandler = parameterHandlerSony;
        this.mRemoteApi = parameterHandlerSony.mRemoteApi;
        addEventListner(this);

    }


    @Override
    public void SonyApiChanged(Set<String> mAvailableCameraApiSet)
    {
        this.mAvailableCameraApiSet = mAvailableCameraApiSet;
        if (isSupported != JsonUtils.isCameraApiAvailable(VALUE_TO_GET, mAvailableCameraApiSet))
        {
            isSupported = JsonUtils.isCameraApiAvailable(VALUE_TO_GET, mAvailableCameraApiSet);
        }
        BackgroundIsSupportedChanged(isSupported);
        BackgroundIsSetSupportedChanged(false);
        if (isSetSupported != JsonUtils.isCameraApiAvailable(VALUE_TO_SET, mAvailableCameraApiSet))
        {
            isSetSupported = JsonUtils.isCameraApiAvailable(VALUE_TO_SET, mAvailableCameraApiSet);
        }
        BackgroundIsSetSupportedChanged(isSetSupported);



    }

    @Override
    public boolean IsSupported()
    {
        return isSupported;
    }

    @Override
    public boolean IsSetSupported() {
        return isSetSupported;
    }

    @Override
    public int GetMaxValue()
    {
        if(values == null)
        {
            getStringValues();
        }
        if (values != null && values.length > 0)
            return values.length -1;
        else
            return 0;
    }

    public String[] getStringValues()
    {
        if (values == null)
        {
            new Thread(new Runnable()
            {
                @Override
                public void run()
                {
                    try
                    {
                        Log.d(TAG, "Trying to get String Values from: " +VALUES_TO_GET);
                        JSONObject object =  ParameterHandler.mRemoteApi.getParameterFromCamera(VALUES_TO_GET);
                        JSONArray array = object.getJSONArray("result");
                        JSONArray subarray = array.getJSONArray(1);
                        values = JsonUtils.ConvertJSONArrayToStringArray(subarray);
                        BackgroundValuesChanged(values);

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
        Log.d(TAG, "Returning values from: " + VALUES_TO_GET);
        return values;

    }

    @Override
    public int GetMinValue() {
        return 0;
    }

    @Override
    public int GetValue()
    {
        if(val == -200) {
            val = -1;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        JSONObject object = mRemoteApi.getParameterFromCamera(VALUE_TO_GET);
                        JSONArray array = object.getJSONArray("result");
                        String res = JsonUtils.ConvertJSONArrayToStringArray(array)[0];
                        if (values == null)
                            getStringValues();
                        value = res;
                        if (values == null)
                            return;
                        for (int i = 0; i < values.length; i++) {
                            if (values[i].equals(res)) {
                                val = i;
                                break;
                            }
                        }
                        currentValueChanged(val);
                    } catch (IOException e) {
                        e.printStackTrace();
                        val = 0;
                    } catch (JSONException e) {
                        e.printStackTrace();
                        val = 0;
                    }
                }
            }).start();

        }
        return val;
    }

    @Override
    public void SetValue(final int valueToSet)
    {
        this.val = valueToSet;
        new Thread(new Runnable() {
            @Override
            public void run()
            {
                if (valueToSet == values.length || valueToSet < 0)
                    return;
                String val = values[valueToSet];
                value = val;
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
    public void RestartPreview() {

    }

    public String GetStringValue()
    {
        if (value == null || value.equals("")) {
            if (this.values == null) {
                this.values = getStringValues();

            }
            if (values != null && values.length > 0 && val < values.length) {
                if (val == -200)
                    GetValue();
                if (val == -1)
                    return value;
                return values[val];
            }
        }
        return value;

    }


    @Override
    public void onIsSupportedChanged(boolean value)
    {
        isSupported = value;
    }

    @Override
    public void onIsSetSupportedChanged(boolean value)
    {
        isSetSupported = value;
    }

    @Override
    public void onMaxValueChanged(int max) {

    }

    @Override
    public void onMinValueChanged(int min) {

    }

    @Override
    public void onCurrentValueChanged(int current)
    {
        Log.d(TAG, "onCurrentValueChanged = "  +current);
        this.val = current;
    }

    @Override
    public void onValuesChanged(String[] values)
    {
        Log.d(TAG, "onValueSChanged = "  +values.toString());
        this.values = values;
    }

    @Override
    public void onCurrentStringValueChanged(String value)
    {
        this.value = value;
    }
}
