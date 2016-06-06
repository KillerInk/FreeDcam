/*
 *
 *     Copyright (C) 2015 Ingo Fuchs
 *     This program is free software; you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation; either version 2 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License along
 *     with this program; if not, write to the Free Software Foundation, Inc.,
 *     51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 * /
 */

package com.freedcam.apis.sonyremote.camera.parameters.manual;

import com.freedcam.utils.FreeDPool;
import com.freedcam.utils.Logger;

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
    final String TAG = WbCTManualSony.class.getSimpleName();
    private int min =0;
    private int max = 0;
    private int step = 0;

    private String[] values;
    public WbCTManualSony(com.freedcam.apis.sonyremote.camera.parameters.ParameterHandler parameterHandler) {
        super("", "", "", parameterHandler);
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
            FreeDPool.Execute(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject object = mRemoteApi.getParameterFromCamera("getWhiteBalance");
                    JSONArray array = null;

                    try {
                        array = object.getJSONArray("result");
                        currentInt = array.getJSONObject(0).getInt("colorTemperature");
                        if (step == 0) {
                            getMinMax();
                            return;
                        }

                        ThrowCurrentValueChanged(currentInt / step);
                    } catch (JSONException e) {
                        Logger.exception(e);
                    }
                } catch (IOException e) {
                    Logger.exception(e);

                }
            }
        });
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
        FreeDPool.Execute(new Runnable() {
            @Override
            public void run() {
                try
                {
                    Logger.d("WBCT", values[set]);

                    JSONArray array = new JSONArray().put("Color Temperature").put(true).put(Integer.parseInt(t[set])) ;
                    JSONObject jsonObject = mRemoteApi.setParameterToCamera("setWhiteBalance", array);
                } catch (IOException e) {
                    Logger.exception(e);
                }
            }
        });
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
            ArrayList<String> r = new ArrayList<>();
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
        FreeDPool.Execute(new Runnable() {
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
                        Logger.exception(e);
                    }
                } catch (IOException e) {
                    Logger.exception(e);
                }
            }
        });
        while (values == null)
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                Logger.exception(e);
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
