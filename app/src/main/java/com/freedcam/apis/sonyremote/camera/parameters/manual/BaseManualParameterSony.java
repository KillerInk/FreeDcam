package com.freedcam.apis.sonyremote.camera.parameters.manual;

import com.freedcam.apis.sonyremote.camera.parameters.ParameterHandlerSony;
import com.freedcam.apis.sonyremote.camera.parameters.modes.I_SonyApi;
import com.freedcam.apis.sonyremote.camera.sonystuff.JsonUtils;
import com.freedcam.apis.sonyremote.camera.sonystuff.SimpleRemoteApi;
import com.freedcam.utils.Logger;
import com.freedcam.apis.basecamera.camera.parameters.manual.AbstractManualParameter;
import com.freedcam.utils.FreeDPool;

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
    String value;
    final boolean logging =false;

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
    public boolean IsVisible() {
        return isSupported;
    }

    public String[] getStringValues()
    {
        if (stringvalues == null)
        {
            FreeDPool.Execute(new Runnable()
            {
                @Override
                public void run()
                {
                    try
                    {
                        sendLog("Trying to get String Values from: " +VALUES_TO_GET);
                        JSONObject object =  ParameterHandler.mRemoteApi.getParameterFromCamera(VALUES_TO_GET);
                        JSONArray array = object.getJSONArray("result");
                        JSONArray subarray = array.getJSONArray(1);
                        stringvalues = JsonUtils.ConvertJSONArrayToStringArray(subarray);
                        BackgroundValuesChanged(stringvalues);

                    } catch (IOException | JSONException e) {
                        Logger.exception(e);
                        sendLog( "Error Trying to get String Values from: " +VALUES_TO_GET);
                        stringvalues = new String[0];
                    }
                }
            });
        }
        sendLog("Returning values from: " + VALUES_TO_GET);
        return stringvalues;

    }


    @Override
    public void SetValue(final int valueToSet)
    {
        sendLog("Set Value to " + valueToSet);
        this.currentInt = valueToSet;
        FreeDPool.Execute(new Runnable() {
            @Override
            public void run()
            {
                if (valueToSet >= stringvalues.length || valueToSet < 0)
                    return;
                String val = stringvalues[valueToSet];
                value = val;
                JSONArray array = null;
                try {
                    array = new JSONArray().put(0, val);
                    JSONObject object =  ParameterHandler.mRemoteApi.setParameterToCamera(VALUE_TO_SET, array);
                    ThrowCurrentValueChanged(valueToSet);
                } catch (JSONException | IOException e) {
                    Logger.exception(e);
                }
            }
        });
    }

    public String GetStringValue()
    {
        sendLog("GetStringValue");
        if (value == null || value.equals("")) {
            if (this.stringvalues == null) {
                this.stringvalues = getStringValues();

            }
            if (stringvalues != null && stringvalues.length > 0 && currentInt < stringvalues.length) {
                if (currentInt == -200)
                    GetValue();
                if (currentInt == -1)
                    return value;
                return stringvalues[currentInt];
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
    public void onCurrentValueChanged(int current)
    {
        sendLog("onCurrentValueChanged = "  +current);
        this.currentInt = current;
    }

    @Override
    public void onValuesChanged(String[] values)
    {
        sendLog("onValueSChanged = "  +values.toString());
        this.stringvalues = values;
    }

    @Override
    public void onCurrentStringValueChanged(String value)
    {
        this.value = value;
        if (stringvalues == null)
            return;
        for (int i = 0; i< stringvalues.length; i++)
        {
            if (value.equals(stringvalues[i]))
                onCurrentValueChanged(i);
        }
    }

    protected void sendLog(String log)
    {
        if (logging)
            Logger.d(TAG, VALUE_TO_SET + ":" + log);
    }
}
