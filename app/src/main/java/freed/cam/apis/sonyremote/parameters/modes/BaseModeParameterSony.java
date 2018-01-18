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

package freed.cam.apis.sonyremote.parameters.modes;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Set;

import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.parameters.AbstractParameter;
import freed.cam.apis.basecamera.parameters.ParameterEvents;
import freed.cam.apis.sonyremote.sonystuff.JsonUtils;
import freed.cam.apis.sonyremote.sonystuff.SimpleRemoteApi;
import freed.utils.Log;

/**
 * Created by troop on 15.12.2014.
 */
public class BaseModeParameterSony extends AbstractParameter implements I_SonyApi, ParameterEvents
{

    protected String VALUE_TO_GET = "";
    protected String VALUE_TO_SET ="";
    protected String VALUES_TO_GET ="";

    protected SimpleRemoteApi mRemoteApi;
    protected Set<String> mAvailableCameraApiSet;
    JSONObject jsonObject;
    private final String TAG = BaseModeParameterSony.class.getSimpleName();

    public BaseModeParameterSony(String VALUE_TO_GET, String VALUE_TO_SET, String VALUES_TO_GET, SimpleRemoteApi mRemoteApi, CameraWrapperInterface  wrapperInterface)
    {
        super(wrapperInterface,null);
        this.VALUE_TO_GET = VALUE_TO_GET;
        this.VALUE_TO_SET = VALUE_TO_SET;
        this.VALUES_TO_GET = VALUES_TO_GET;
        this.mRemoteApi = mRemoteApi;

    }

    @Override
    public void SonyApiChanged(Set<String> mAvailableCameraApiSet)
    {
        this.mAvailableCameraApiSet = mAvailableCameraApiSet;
        if (isSupported != JsonUtils.isCameraApiAvailable(VALUE_TO_GET, mAvailableCameraApiSet))
        {
            isSupported = JsonUtils.isCameraApiAvailable(VALUE_TO_GET, mAvailableCameraApiSet);
            fireIsSupportedChanged(isSupported);
            onStringValueChanged(GetStringValue());
        }

    }


    @Override
    public boolean IsSupported()
    {
        return isSupported;
    }


    @Override
    protected void setValue(String valueToSet, boolean setToCamera) {
        super.setValue(valueToSet, setToCamera);
        processValuesToSet(valueToSet);
        onStringValueChanged(valueToSet);
    }

    protected void processValuesToSet(String valueToSet)
    {
        try
        {
            try {
                JSONArray array = new JSONArray().put(0, valueToSet);
                JSONObject jsonObject = mRemoteApi.setParameterToCamera(VALUE_TO_SET, array);
            } catch (JSONException ex) {
                Log.WriteEx(ex);
            }


        } catch (IOException ex) {
            Log.WriteEx(ex);
        }
    }



    @Override
    public String GetStringValue()
    {
        /*if (key_value == null || key_value.equals("")) {
            jsonObject = null;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        jsonObject = mRemoteApi.getParameterFromCamera(VALUE_TO_GET);
                        key_value = processGetString();
                        onValueHasChanged(key_value);
                    } catch (IOException e) {
                        Log.exception(e);
                    }
                }
            }).start();
        }*/
            return currentString;

    }

    protected String processGetString() {
        JSONArray array = null;
        String ret ="";
        try {
            array = jsonObject.getJSONArray("result");
            ret = array.getString(0);
        } catch (JSONException ex) {
            Log.WriteEx(ex);
        }
        return ret;
    }

    @Override
    public String[] getStringValues()
    {
        /*jsonObject =null;
        new Thread(new Runnable() {
            @Override
            public void run()
            {
                try {
                    jsonObject = mRemoteApi.getParameterFromCamera(VALUES_TO_GET);
                } catch (IOException e) {
                    Log.exception(e);
                }
            }
        }).start();
        while (jsonObject == null)
        {
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                Log.exception(e);
            }
        }
        String[] ret = processValuesToReturn();*/

        return stringvalues;
    }

    protected String[] processValuesToReturn() {
        String[] ret = null;
        try {
            JSONArray array = jsonObject.getJSONArray("result");
            JSONArray subarray = array.getJSONArray(1);
            ret = JsonUtils.ConvertJSONArrayToStringArray(subarray);
        } catch (JSONException ex) {
            Log.WriteEx(ex);
        }
        return ret;
    }

    @Override
    public void onIsSupportedChanged(boolean value) {
        isSupported = value;
    }

    @Override
    public void onIsSetSupportedChanged(boolean value) {
        isNotReadOnly = value;

    }

    @Override
    public void onIntValueChanged(int current) {

    }

    @Override
    public void onValuesChanged(String[] values) {
        this.stringvalues = values;
    }

    @Override
    public void onStringValueChanged(String value) {
        this.currentString = value;
    }

}
