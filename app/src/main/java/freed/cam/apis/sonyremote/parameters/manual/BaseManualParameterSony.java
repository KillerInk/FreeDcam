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

import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;
import java.util.Set;

import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.parameters.AbstractParameter;
import freed.cam.apis.basecamera.parameters.ParameterEvents;
import freed.cam.apis.sonyremote.parameters.ParameterHandler;
import freed.cam.apis.sonyremote.parameters.modes.I_SonyApi;
import freed.cam.apis.sonyremote.sonystuff.JsonUtils;
import freed.cam.apis.sonyremote.sonystuff.SimpleRemoteApi;
import freed.utils.FreeDPool;
import freed.utils.Log;

/**
 * Created by troop on 15.12.2014.
 */
public class BaseManualParameterSony extends AbstractParameter implements I_SonyApi, ParameterEvents
{
    protected String VALUE_TO_GET;
    protected String VALUES_TO_GET;
    protected String VALUE_TO_SET;
    protected SimpleRemoteApi mRemoteApi;
    protected Set<String> mAvailableCameraApiSet;
    boolean isSupported;
    boolean isSetSupported;
    String value;
    final boolean logging =false;

    private final String TAG = BaseManualParameterSony.class.getSimpleName();

    public BaseManualParameterSony(String VALUE_TO_GET, String VALUES_TO_GET, String VALUE_TO_SET, CameraWrapperInterface cameraUiWrapper)
    {
        super(cameraUiWrapper);
        this.VALUE_TO_GET = VALUE_TO_GET;
        this.VALUES_TO_GET = VALUES_TO_GET;
        this.VALUE_TO_SET = VALUE_TO_SET;
        mRemoteApi = ((ParameterHandler)cameraUiWrapper.getParameterHandler()).mRemoteApi;
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
        fireIsSupportedChanged(isSupported);
        fireIsReadOnlyChanged(false);
        if (isSetSupported != JsonUtils.isCameraApiAvailable(VALUE_TO_SET, mAvailableCameraApiSet))
        {
            isSetSupported = JsonUtils.isCameraApiAvailable(VALUE_TO_SET, mAvailableCameraApiSet);
        }
        fireIsReadOnlyChanged(isSetSupported);
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
                        sendLog("Trying to get String Values from: " + VALUES_TO_GET);
                        JSONObject object = mRemoteApi.getParameterFromCamera(VALUES_TO_GET);
                        JSONArray array = object.getJSONArray("result");
                        JSONArray subarray = array.getJSONArray(1);
                        stringvalues = JsonUtils.ConvertJSONArrayToStringArray(subarray);
                        fireStringValuesChanged(stringvalues);

                    } catch (IOException | JSONException ex) {
                        Log.WriteEx(ex);
                        sendLog( "Error Trying to get String Values from: " + VALUES_TO_GET);
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
        currentInt = valueToSet;
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
                    JSONObject object = mRemoteApi.setParameterToCamera(VALUE_TO_SET, array);
                    fireIntValueChanged(valueToSet);
                } catch (JSONException | IOException ex) {
                    Log.WriteEx(ex);
                }
            }
        });
    }

    public String GetStringValue()
    {
        sendLog("GetStringValue");
        if (value == null || TextUtils.isEmpty(value)) {
            if (stringvalues == null) {
                stringvalues = getStringValues();

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
    public void onIntValueChanged(int current)
    {
        sendLog("onIntValueChanged = "  +current);
        currentInt = current;
    }

    @Override
    public void onValuesChanged(String[] values)
    {
        sendLog("onValueSChanged = "  + Arrays.toString(values));
        stringvalues = values;
    }

    @Override
    public void onStringValueChanged(String value)
    {
        this.value = value;
        if (stringvalues == null)
            return;
        for (int i = 0; i< stringvalues.length; i++)
        {
            if (value.equals(stringvalues[i]))
                onIntValueChanged(i);
        }
    }

    protected void sendLog(String log)
    {
        if (logging)
            Log.d(TAG, VALUE_TO_SET + ":" + log);
    }
}
