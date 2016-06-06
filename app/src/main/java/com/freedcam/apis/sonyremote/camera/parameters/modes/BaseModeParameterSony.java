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

package com.freedcam.apis.sonyremote.camera.parameters.modes;

import com.freedcam.apis.basecamera.camera.parameters.modes.AbstractModeParameter;
import com.freedcam.apis.sonyremote.camera.sonystuff.JsonUtils;
import com.freedcam.apis.sonyremote.camera.sonystuff.SimpleRemoteApi;
import com.freedcam.utils.FreeDPool;
import com.freedcam.utils.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Set;

/**
 * Created by troop on 15.12.2014.
 */
public class BaseModeParameterSony extends AbstractModeParameter implements I_SonyApi
{

    protected String VALUE_TO_GET = "";
    protected String VALUE_TO_SET ="";
    protected String VALUES_TO_GET ="";

    protected SimpleRemoteApi mRemoteApi;
    protected Set<String> mAvailableCameraApiSet;
    JSONObject jsonObject;

    protected boolean isSupported =false;
    protected boolean isSetSupported = false;
    protected String value ="";
    protected String[] values;
    private static String TAG = BaseModeParameterSony.class.getSimpleName();

    public BaseModeParameterSony(String VALUE_TO_GET, String VALUE_TO_SET, String VALUES_TO_GET, SimpleRemoteApi mRemoteApi)
    {
        super();
        this.VALUE_TO_GET = VALUE_TO_GET;
        this.VALUE_TO_SET = VALUE_TO_SET;
        this.VALUES_TO_GET = VALUES_TO_GET;
        this.mRemoteApi = mRemoteApi;
    }

    @Override
    public void SonyApiChanged(final Set<String> mAvailableCameraApiSet)
    {
        this.mAvailableCameraApiSet = mAvailableCameraApiSet;
        if (isSupported != JsonUtils.isCameraApiAvailable(VALUE_TO_GET, mAvailableCameraApiSet))
        {
            isSupported = JsonUtils.isCameraApiAvailable(VALUE_TO_GET, mAvailableCameraApiSet);
            BackgroundIsSupportedChanged(isSupported);
            BackgroundValueHasChanged(GetValue());
        }

    }


    @Override
    public boolean IsSupported()
    {
        if (mAvailableCameraApiSet != null)
        {
            boolean sup = JsonUtils.isCameraApiAvailable(VALUE_TO_GET, mAvailableCameraApiSet);
            Logger.d(TAG, VALUE_TO_GET + " is supported: " + sup);
            BackgroundIsSupportedChanged(sup);
            return sup;
        }else
            return false;
    }

    @Override
    public void SetValue(final String valueToSet, boolean setToCamera)
    {
        value = valueToSet;
        FreeDPool.Execute(new Runnable() {
            @Override
            public void run() {
                processValuesToSet(valueToSet);
                BackgroundValueHasChanged(valueToSet);
            }
        });
    }

    protected void processValuesToSet(String valueToSet)
    {
        try
        {
            try {
                JSONArray array = new JSONArray().put(0, valueToSet);
                JSONObject jsonObject = mRemoteApi.setParameterToCamera(VALUE_TO_SET, array);
            } catch (JSONException e) {
                Logger.exception(e);
            }


        } catch (IOException e) {
            Logger.exception(e);
        }
    }



    @Override
    public String GetValue()
    {
        /*if (key_value == null || key_value.equals("")) {
            jsonObject = null;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        jsonObject = mRemoteApi.getParameterFromCamera(VALUE_TO_GET);
                        key_value = processGetString();
                        BackgroundValueHasChanged(key_value);
                    } catch (IOException e) {
                        Logger.exception(e);
                    }
                }
            }).start();
        }*/
            return value;

    }

    protected String processGetString() {
        JSONArray array = null;
        String ret ="";
        try {
            array = jsonObject.getJSONArray("result");
            ret = array.getString(0);
        } catch (JSONException e) {
            Logger.exception(e);
        }
        return ret;
    }

    @Override
    public String[] GetValues()
    {
        /*jsonObject =null;
        new Thread(new Runnable() {
            @Override
            public void run()
            {
                try {
                    jsonObject = mRemoteApi.getParameterFromCamera(VALUES_TO_GET);
                } catch (IOException e) {
                    Logger.exception(e);
                }
            }
        }).start();
        while (jsonObject == null)
        {
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                Logger.exception(e);
            }
        }
        String[] ret = processValuesToReturn();*/

        return values;
    }

    protected String[] processValuesToReturn() {
        String[] ret = null;
        try {
            JSONArray array = jsonObject.getJSONArray("result");
            JSONArray subarray = array.getJSONArray(1);
            ret = JsonUtils.ConvertJSONArrayToStringArray(subarray);
        } catch (JSONException e) {
            Logger.exception(e);
        }
        return ret;
    }

    @Override
    public void BackgroundValueHasChanged(String value)
    {
        this.value = value;
        super.BackgroundValueHasChanged(value);

    }

    @Override
    public void BackgroundValuesHasChanged(String[] value)
    {
        this.values = value;
        super.BackgroundValuesHasChanged(value);
    }
}
