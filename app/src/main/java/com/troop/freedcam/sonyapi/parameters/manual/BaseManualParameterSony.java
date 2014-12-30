package com.troop.freedcam.sonyapi.parameters.manual;

import com.troop.freedcam.i_camera.parameters.AbstractManualParameter;
import com.troop.freedcam.i_camera.parameters.AbstractModeParameter;
import com.troop.freedcam.i_camera.parameters.I_ManualParameter;
import com.troop.freedcam.sonyapi.parameters.ParameterHandlerSony;
import com.troop.freedcam.sonyapi.parameters.modes.BaseModeParameterSony;
import com.troop.freedcam.sonyapi.parameters.modes.I_SonyApi;
import com.troop.freedcam.sonyapi.sonystuff.JsonUtils;
import com.troop.freedcam.sonyapi.sonystuff.SimpleRemoteApi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
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
    int val = 0;

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
        if (isSetSupported != JsonUtils.isCameraApiAvailable(VALUE_TO_SET, mAvailableCameraApiSet))
        {
            isSetSupported = JsonUtils.isCameraApiAvailable(VALUE_TO_SET, mAvailableCameraApiSet);
        }
        BackgroundIsSetSupportedChanged(isSetSupported);


    }

    @Override
    public boolean IsSupported()
    {
        if (ParameterHandler.mAvailableCameraApiSet != null)
            return JsonUtils.isCameraApiAvailable(VALUE_TO_GET, ParameterHandler.mAvailableCameraApiSet);
        return false;
    }

    @Override
    public int GetMaxValue()
    {
        if(values == null)
        {
            getValues();
        }

        return values.length;
    }

    public String[] getValues()
    {
        if (values == null)
        {
            new Thread(new Runnable()
            {
                @Override
                public void run()
                {
                    try {
                        JSONObject object =  ParameterHandler.mRemoteApi.getParameterFromCamera(VALUES_TO_GET);
                        JSONArray array = object.getJSONArray("result");
                        JSONArray subarray = array.getJSONArray(1);
                        values = JsonUtils.ConvertJSONArrayToStringArray(subarray);

                    } catch (IOException e) {
                        e.printStackTrace();
                        values = new String[0];
                    } catch (JSONException e) {
                        e.printStackTrace();
                        values = new String[0];
                    }
                }
            }).start();
            while (values == null)
            {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        return values;

    }

    @Override
    public int GetMinValue() {
        return 0;
    }

    @Override
    public int GetValue()
    {
        val = -1;
        new Thread(new Runnable() {
            @Override
            public void run()
            {
                try {
                    JSONObject object = mRemoteApi.getParameterFromCamera(VALUE_TO_GET);
                    JSONArray array = object.getJSONArray("result");
                    String res = JsonUtils.ConvertJSONArrayToStringArray(array)[0];
                    if (values == null)
                        getValues();
                    for (int i = 0; i < values.length; i++)
                    {
                        if (values[i].equals(res)) {
                            val = i;
                            break;
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    val = 0;
                } catch (JSONException e) {
                    e.printStackTrace();
                    val = 0;
                }
            }
        }).start();
        while (val == -1)
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        return val;
    }

    @Override
    public void SetValue(final int valueToSet)
    {
        this.val = valueToSet;
        new Thread(new Runnable() {
            @Override
            public void run() {
                String val = values[valueToSet];
                JSONArray array = null;
                try {
                    array = new JSONArray().put(0, val);
                    JSONObject object =  ParameterHandler.mRemoteApi.setParameterToCamera(VALUE_TO_SET, array);
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
        if (this.values == null)
        {
            this.values = getValues();

        }
        if (values.length > 0)
            return values[val];
        return "";

    }


    @Override
    public void onIsSupportedChanged(boolean value) {

    }

    @Override
    public void onIsSetSupportedChanged(boolean value) {

    }

    @Override
    public void onMaxValueChanged(int max) {

    }

    @Override
    public void onMinValueChanged(int min) {

    }

    @Override
    public void onCurrentValueChanged(int current) {
        val = current;
    }

    @Override
    public void onValuesChanged(String[] values)
    {
        this.values = values;
    }
}
