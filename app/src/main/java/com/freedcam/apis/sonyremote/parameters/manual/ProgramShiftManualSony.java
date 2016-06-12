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

package com.freedcam.apis.sonyremote.parameters.manual;

import android.content.Context;

import com.freedcam.apis.basecamera.interfaces.I_CameraUiWrapper;
import com.freedcam.apis.sonyremote.parameters.ParameterHandler;
import com.freedcam.apis.sonyremote.sonystuff.JsonUtils;
import com.freedcam.utils.FreeDPool;
import com.freedcam.utils.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;

/**
 * Created by Ingo on 19.04.2015.
 */
public class ProgramShiftManualSony extends BaseManualParameterSony
{
    private final String TAG = ProgramShiftManualSony.class.getSimpleName();
    private BaseManualParameterSony shutter;
    private BaseManualParameterSony fnumber;
    public ProgramShiftManualSony(I_CameraUiWrapper cameraUiWrapper) {
        super("", "getSupportedProgramShift", "setProgramShift", cameraUiWrapper);
        shutter = (BaseManualParameterSony) cameraUiWrapper.GetParameterHandler().ManualShutter;
        fnumber = (BaseManualParameterSony) cameraUiWrapper.GetParameterHandler().ManualFNumber;
    }

    @Override
    public void SonyApiChanged(Set<String> mAvailableCameraApiSet)
    {
        this.mAvailableCameraApiSet = mAvailableCameraApiSet;
        if (isSupported != JsonUtils.isCameraApiAvailable(VALUE_TO_SET, mAvailableCameraApiSet))
        {
            isSupported = JsonUtils.isCameraApiAvailable(VALUE_TO_SET, mAvailableCameraApiSet);
        }
        ThrowBackgroundIsSupportedChanged(isSupported);
        ThrowBackgroundIsSetSupportedChanged(true);
        if (isSetSupported != JsonUtils.isCameraApiAvailable(VALUE_TO_SET, mAvailableCameraApiSet))
        {
            isSetSupported = JsonUtils.isCameraApiAvailable(VALUE_TO_SET, mAvailableCameraApiSet);
        }
        ThrowBackgroundIsSetSupportedChanged(isSetSupported);

    }


    @Override
    public String[] getStringValues()
    {
        if (stringvalues == null)
            getminmax();
        return stringvalues;
    }

    @Override
    public String GetStringValue()
    {
        if (stringvalues == null)
            getminmax();
        return stringvalues[currentInt];
    }

    private void getminmax() {
        if (isSupported && isSetSupported)
        {
            FreeDPool.Execute(new Runnable()
            {
                @Override
                public void run()
                {
                    try
                    {
                        Logger.d(TAG, "Trying to get String Values from: " + VALUES_TO_GET);
                        JSONObject object =  ((ParameterHandler)cameraUiWrapper.GetParameterHandler()).mRemoteApi.getParameterFromCamera(VALUES_TO_GET);
                        JSONArray array = object.getJSONArray("result");
                        JSONArray subarray = array.getJSONArray(0);
                        stringvalues = JsonUtils.ConvertJSONArrayToStringArray(subarray);
                        if (stringvalues == null || stringvalues.length != 2)
                            return;
                        int max = Integer.parseInt(stringvalues[0]);
                        int min = Integer.parseInt(stringvalues[1]);
                        ArrayList<String> r = new ArrayList<>();
                        for (int i = min; i<= max; i++)
                        {
                            r.add(i+"");
                        }
                        stringvalues =new String[r.size()];

                        String[] shut = shutter.getStringValues();
                        if (shut != null && r != null && shut.length == r.size())
                        {
                            String s = shutter.GetStringValue();
                            for (int i = 0; i < shut.length; i++)
                            {
                                if (s.equals(shut[i]))
                                {
                                    currentInt = i;
                                    break;
                                }
                            }
                        }
                        r.toArray(stringvalues);
                        ThrowBackgroundValuesChanged(stringvalues);
                        onCurrentValueChanged(currentInt);


                    } catch (IOException | JSONException e) {
                        Logger.exception(e);
                        Logger.e(TAG, "Error Trying to get String Values from: " +VALUES_TO_GET);
                        stringvalues = new String[0];
                    }
                }
            });
            while (stringvalues == null)
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    Logger.exception(e);
                }
        }
    }

    @Override
    public void SetValue(final int valueToSet)
    {
        currentInt = valueToSet;
       FreeDPool.Execute(new Runnable() {
            @Override
            public void run()
            {
                JSONArray array = null;
                try {
                    array = new JSONArray().put(0, Integer.parseInt(stringvalues[currentInt]));
                    JSONObject object =  mRemoteApi.setParameterToCamera(VALUE_TO_SET, array);
                    ThrowCurrentValueChanged(valueToSet);
                } catch (JSONException | IOException e) {
                    Logger.exception(e);
                }
            }
        });
    }



    @Override
    public void onCurrentValueChanged(int current) {
        currentInt = current;
    }

}
