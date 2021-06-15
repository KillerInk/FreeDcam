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

package freed.cam.apis.sonyremote.parameters.manual;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Set;

import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.sonyremote.parameters.ParameterHandler;
import freed.settings.SettingKeys;
import freed.utils.FreeDPool;
import freed.utils.Log;

/**
 * Created by troop on 03.01.2015.
 */
public class ExposureCompManualParameterSony extends BaseManualParameterSony
{
    private final String TAG = ExposureCompManualParameterSony.class.getSimpleName();
    public ExposureCompManualParameterSony(CameraWrapperInterface cameraUiWrapper) {
        super("getExposureCompensation", "getAvailableExposureCompensation", "setExposureCompensation", cameraUiWrapper, SettingKeys.M_ExposureCompensation);
        currentInt = -200;
    }

    @Override
    public void SonyApiChanged(Set<String> mAvailableCameraApiSet) {
        super.SonyApiChanged(mAvailableCameraApiSet);
    }

    @Override
    public void setIntValue(final int valueToSet, boolean setToCamera)
    {
        currentInt = valueToSet;
        FreeDPool.Execute(() -> {
            //String val = valueToSet +"";
            JSONArray array = null;
            if (stringvalues == null)
            {
                getMinMaxValues();
                return;
            }
            try {
                int toset;
                Log.d(TAG, "SetValue " + valueToSet);
                if (valueToSet > stringvalues.length)
                    toset = stringvalues.length -1;
                else
                    toset = valueToSet;
                array = new JSONArray().put(0, Integer.parseInt(stringvalues[toset]));
                JSONObject object =  ((ParameterHandler) cameraUiWrapper.getParameterHandler()).mRemoteApi.setParameterToCamera(VALUE_TO_SET, array);

                    fireIntValueChanged(valueToSet);
            } catch (JSONException ex) {
                Log.WriteEx(ex);
                Log.e(TAG, "Error SetValue " + valueToSet);
            } catch (IOException ex)
            {
                Log.e(TAG, "Error SetValue " + valueToSet);
                Log.WriteEx(ex);
            }
        });
    }

    private void getMinMaxValues()
    {
        FreeDPool.Execute(() -> {
            try {
                Log.d(TAG, "try get min max values ");
                JSONObject object =  ((ParameterHandler) cameraUiWrapper.getParameterHandler()).mRemoteApi.getParameterFromCamera(VALUES_TO_GET);
                JSONArray array = object.getJSONArray("result");
                int min = array.getInt(2);
                int max = array.getInt(1);
                stringvalues = createStringArray(min,max,1);
                fireStringValuesChanged(stringvalues);
            } catch (IOException | JSONException ex)
            {

                Log.e(TAG, "Error getMinMaxValues ");
                Log.WriteEx(ex);

            }
        });

    }

    public int getIntValue()
    {
        if (currentInt == -100) {
            FreeDPool.Execute(() -> {
                try {
                    JSONObject object = mRemoteApi.getParameterFromCamera(VALUE_TO_GET);
                    JSONArray array = object.getJSONArray("result");
                    currentInt = array.getInt(0);
                    fireIntValueChanged(currentInt);
                    //onIntValueChanged(val);
                } catch (IOException | JSONException ex) {
                    Log.WriteEx(ex);
                    Log.e(TAG, "Error GetStringValue() ");

                }
            });
        }
        return currentInt;
    }

    public String[] getStringValues()
    {
        if (stringvalues == null || stringvalues.length == 0)
            getMinMaxValues();
        return stringvalues;
    }

    @Override
    public String getStringValue()
    {
        if (stringvalues != null && currentInt != -100 && stringvalues.length > currentInt)
            return stringvalues[currentInt];
        return 0+"";
    }
}
