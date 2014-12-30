package com.troop.freedcam.sonyapi.parameters.manual;

import com.troop.freedcam.sonyapi.parameters.ParameterHandlerSony;
import com.troop.freedcam.sonyapi.sonystuff.JsonUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by troop on 30.12.2014.
 */
public class FnumberManualSony extends BaseManualParameterSony
{
    String[] values;
    int val = 0;

    public FnumberManualSony(String MAX_TO_GET, String MIN_TO_GET, String CURRENT_TO_GET, ParameterHandlerSony parameterHandlerSony) {
        super(MAX_TO_GET, MIN_TO_GET, CURRENT_TO_GET, parameterHandlerSony);
        this.mRemoteApi = parameterHandlerSony.mRemoteApi;
    }

    @Override
    public boolean IsSupported() {
        return JsonUtils.isCameraApiAvailable("setFNumber", ParameterHandler.mAvailableCameraApiSet);
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
                        JSONObject object =  ParameterHandler.mRemoteApi.getParameterFromCamera("getAvailableFNumber");
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
                    JSONObject object = mRemoteApi.getParameterFromCamera("getFNumber");
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
                    JSONObject object =  ParameterHandler.mRemoteApi.setParameterToCamera("setFNumber", array);
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();


    }

    public String GetStringValue()
    {
        return values[val];
    }
}
