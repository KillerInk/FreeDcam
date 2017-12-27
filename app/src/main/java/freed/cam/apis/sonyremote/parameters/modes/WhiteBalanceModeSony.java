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
import freed.cam.apis.sonyremote.parameters.manual.WbCTManualSony;
import freed.cam.apis.sonyremote.sonystuff.SimpleRemoteApi;
import freed.utils.Log;

public class WhiteBalanceModeSony extends BaseModeParameterSony
{
    final String TAG = WhiteBalanceModeSony.class.getSimpleName();
    private final WbCTManualSony wb;
    public WhiteBalanceModeSony(SimpleRemoteApi mRemoteApi, WbCTManualSony wb, CameraWrapperInterface wrapperInterface) {
        super("getWhiteBalance", "setWhiteBalance", "getAvailableWhiteBalance", mRemoteApi,wrapperInterface);
        this.wb = wb;
    }

    @Override
    public void SonyApiChanged(Set<String> mAvailableCameraApiSet)
    {
        this.mAvailableCameraApiSet = mAvailableCameraApiSet;
    }

    /*@Override
    public String[] getStringValues()
    {
        if(stringvalues == null || stringvalues.length == 0) {
            jsonObject = null;
            stringvalues = new String[0];
            FreeDPool.Execute(new Runnable() {
                @Override
                public void run() {
                try {

                    jsonObject = mRemoteApi.getParameterFromCamera(VALUES_TO_GET);
                    stringvalues = processValuesToReturn();

                    fireStringValuesChanged(stringvalues);
                } catch (IOException ex) {
                    Log.WriteEx(ex);
                }
                }
            });
        }

        return stringvalues;
    }*/

    protected String[] processValuesToReturn() {
        String[] ret = null;
        try {
            JSONArray array = jsonObject.getJSONArray("result");
            JSONArray subarray = array.getJSONArray(1);
            ret = new String[subarray.length()];
            for (int i = 0; i< subarray.length(); i++)
            {
                JSONObject ob = subarray.getJSONObject(i);
                ret[i] = ob.getString("whiteBalanceMode");
                if (ret[i].equals("Color Temperature"))
                {

                    wb.SetMinMAx(ob);
                }
            }
            JSONObject ob = array.getJSONObject(0);
            if(ob.getString("whiteBalanceMode").equals("Color Temperature"))
            {
                int cur = ob.getInt("colorTemperature");
                wb.setValueInternal(cur);
            }

        } catch (JSONException ex) {
            Log.WriteEx(ex);
        }
        return ret;
    }

    //{
    // "method":"setWhiteBalance",
    // "params":[{"setWhiteBalance":"Daylight"},false,-1],
    // "id":21,"version":"1.0"
    // }

    /*{
        "method": "setWhiteBalance",
            "params": ["Color Temperature", true, 2500],
        "id": 1,
            "version": "1.0"
    }*/
    protected void processValuesToSet(String valueToSet)
    {
        try
        {
            JSONArray array = new JSONArray().put(valueToSet).put(false).put(-1) ;
            JSONObject jsonObject = mRemoteApi.setParameterToCamera(VALUE_TO_SET, array);
        } catch (IOException ex) {
            Log.WriteEx(ex);
        }
    }

    protected String processGetString() {
        JSONArray array = null;
        String ret ="";
        try {
            array = jsonObject.getJSONArray("result");
            ret = array.getJSONObject(0).getString("whiteBalanceMode");
        } catch (JSONException ex) {
            Log.WriteEx(ex);
        }
        return ret;
    }
}
