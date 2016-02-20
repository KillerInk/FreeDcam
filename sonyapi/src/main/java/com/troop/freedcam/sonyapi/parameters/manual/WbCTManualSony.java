package com.troop.freedcam.sonyapi.parameters.manual;

import android.util.Log;

import com.troop.freedcam.sonyapi.parameters.ParameterHandlerSony;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;

/**
 * Created by troop on 19.04.2015.
 */
public class WbCTManualSony extends BaseManualParameterSony
{
    int min =0;
    int max = 0;
    int step = 0;

    private String[] values;
    public WbCTManualSony(String VALUE_TO_GET, String VALUES_TO_GET, String VALUE_TO_SET, ParameterHandlerSony parameterHandlerSony) {
        super(VALUE_TO_GET, VALUES_TO_GET, VALUE_TO_SET, parameterHandlerSony);
    }

    @Override
    public void SonyApiChanged(Set<String> mAvailableCameraApiSet)
    {
        this.mAvailableCameraApiSet = mAvailableCameraApiSet;
    }


    @Override
    public int GetValue()
    {
        if (this.currentInt == -200)
            new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject object = mRemoteApi.getParameterFromCamera("getWhiteBalance");
                    JSONArray array = null;

                    try {
                        array = object.getJSONArray("result");
                        int ret = array.getJSONObject(0).getInt("colorTemperature");
                        currentInt = ret;
                        if (step == 0) {
                            getMinMax();
                            return;
                        }

                        ThrowCurrentValueChanged(currentInt / step);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } catch (IOException e) {
                    e.printStackTrace();

                }
            }
        }).start();
        return currentInt;
    }

    @Override
    public void SetValue(final int valueToSet)
    {
        this.currentInt = valueToSet;
        if (valueToSet > values.length)
            this.currentInt = values.length;
        if (valueToSet < 0)
            this.currentInt = 0;
        final int set= currentInt;
        final String[] t = values;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try
                {
                    Log.d("WBCT", values[set]);

                    JSONArray array = new JSONArray().put("Color Temperature").put(true).put(Integer.parseInt(t[set])) ;
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

    public void SetMinMAx(JSONObject ob) throws JSONException {
        if(ob.getString("whiteBalanceMode").equals("Color Temperature"))
        {
            JSONArray ar = ob.getJSONArray("colorTemperatureRange");
            step = ar.getInt(2);
            max = ar.getInt(0)/step;
            min = ar.getInt(1)/step;
            ArrayList<String> r = new ArrayList<String>();
            for (int t = min; t < max; t++)
                r.add(t*step+"");
            values =new String[r.size()];
            r.toArray(values);
            BackgroundValuesChanged(values);
            BackgroundIsSetSupportedChanged(true);
        }
    }

    public void setValueInternal(int val)
    {
        for (int i= 0; i< values.length; i++)
        {
            if (values[i].equals(val))
                this.currentInt = i;
        }
        if (this.currentInt == -200)
            return;
        ThrowCurrentValueStringCHanged(val+"");

        ThrowCurrentValueChanged(this.currentInt);

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
                            SetMinMAx(ob);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
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

    @Override
    public String[] getStringValues()
    {
        if (values == null)
            getMinMax();
        return values;
    }

    @Override
    public String GetStringValue()
    {
        if (values == null)
            return "";
        return values[this.currentInt];
    }
}
