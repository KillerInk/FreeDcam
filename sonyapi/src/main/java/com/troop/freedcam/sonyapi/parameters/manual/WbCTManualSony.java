package com.troop.freedcam.sonyapi.parameters.manual;

import com.troop.freedcam.sonyapi.parameters.ParameterHandlerSony;
import com.troop.freedcam.sonyapi.sonystuff.JsonUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Set;

/**
 * Created by troop on 19.04.2015.
 */
public class WbCTManualSony extends BaseManualParameterSony
{
    int min =0;
    int max = 0;
    int step = 0;
    public WbCTManualSony(String VALUE_TO_GET, String VALUES_TO_GET, String VALUE_TO_SET, ParameterHandlerSony parameterHandlerSony) {
        super(VALUE_TO_GET, VALUES_TO_GET, VALUE_TO_SET, parameterHandlerSony);
    }

    @Override
    public void SonyApiChanged(Set<String> mAvailableCameraApiSet)
    {
        this.mAvailableCameraApiSet = mAvailableCameraApiSet;
    }

    @Override
    public int GetMaxValue()
    {
        if (max == 0)
            getMinMax();
        return max;
    }

    @Override
    public int GetMinValue() {
        if (min == 0)
            getMinMax();
        return min;
    }

    @Override
    public int GetValue()
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject object = mRemoteApi.getParameterFromCamera("getWhiteBalance");
                    JSONArray array = null;

                    try {
                        array = object.getJSONArray("result");
                        int ret = array.getJSONObject(0).getInt("colorTemperature");
                        val = ret;
                        if (step == 0) {
                            getMinMax();
                            return;
                        }

                        onCurrentValueChanged(val / step);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } catch (IOException e) {
                    e.printStackTrace();

                }
            }
        }).start();
        return val;
    }

    @Override
    public void SetValue(final int valueToSet)
    {
        val = valueToSet;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try
                {
                    JSONArray array = new JSONArray().put("Color Temperature").put(true).put(val * step) ;
                    JSONObject jsonObject = mRemoteApi.setParameterToCamera("setWhiteBalance", array);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public void onIsSupportedChanged(boolean value)
    {
        super.onIsSupportedChanged(value);
        if (step != 0 && value)
        {
            GetValue();
        }
    }


    private void getMinMax()
    {
        new Thread(new Runnable() {
            @Override
            public void run()
            {
                try {
                    JSONObject jsonObject = mRemoteApi.getParameterFromCamera("getAvailableWhiteBalance");
                    try {
                        JSONArray array = jsonObject.getJSONArray("result");
                        JSONArray subarray = array.getJSONArray(1);

                        for (int i = 0; i< subarray.length(); i++)
                        {
                            JSONObject ob = subarray.getJSONObject(i);
                            if(ob.getString("whiteBalanceMode").equals("Color Temperature"))
                            {
                                JSONArray ar = ob.getJSONArray("colorTemperatureRange");
                                step = ar.getInt(2);
                                max = ar.getInt(0)/step;
                                min = ar.getInt(1)/step;
                                BackgroundMaxValueChanged(max);
                                BackgroundMinValueChanged(min);
                                BackgroundIsSetSupportedChanged(true);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
