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

/**
 * Created by troop on 03.01.2015.
 */
public class ExposureCompManualParameterSony extends BaseManualParameterSony
{
    private static String TAG = ExposureCompManualParameterSony.class.getSimpleName();
    public ExposureCompManualParameterSony(com.freedcam.apis.sonyremote.camera.parameters.ParameterHandler parameterHandler) {
        super("getExposureCompensation", "getAvailableExposureCompensation", "setExposureCompensation", parameterHandler);
        currentInt = -200;
    }

    @Override
    public void SetValue(final int valueToSet)
    {
        currentInt = valueToSet;
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
        currentInt = current;
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
