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

import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.modules.ModuleHandlerAbstract;
import freed.cam.apis.sonyremote.sonystuff.JsonUtils;
import freed.cam.apis.sonyremote.sonystuff.SimpleRemoteApi;
import freed.utils.Log;

/**
 * Created by troop on 26.01.2015.
 */
public class ContShootModeParameterSony extends BaseModeParameterSony
{
    final String TAG = ContShootModeParameterSony.class.getSimpleName();
    private  ModuleHandlerAbstract moduleHandlerAbstract;
    public ContShootModeParameterSony(SimpleRemoteApi mRemoteApi, ModuleHandlerAbstract moduleHandlerAbstract, CameraWrapperInterface wrapperInterface) {
        super("getContShootingMode", "setContShootingMode", "getAvailableContShootingMode", mRemoteApi,wrapperInterface);
        this.moduleHandlerAbstract =moduleHandlerAbstract;
    }

    protected String[] processValuesToReturn() {
        String[] ret = null;
        try {
            JSONArray array = jsonObject.getJSONArray("result");
            JSONObject ob = array.optJSONObject(0);
            JSONArray subarray = ob.getJSONArray("candidate");
            ret = JsonUtils.ConvertJSONArrayToStringArray(subarray);
        } catch (JSONException ex) {
            Log.WriteEx(ex);
        }
        return ret;
    }

    protected void processValuesToSet(String valueToSet)
    {
        try
        {
            try {
                if (valueToSet.equals("Single"))
                    moduleHandlerAbstract.changeCaptureState(ModuleHandlerAbstract.CaptureStates.image_capture_stop);
                else if (valueToSet.equals("Spd Priority Cont.") || valueToSet.equals("Continuous"))
                    moduleHandlerAbstract.changeCaptureState(ModuleHandlerAbstract.CaptureStates.continouse_capture_work_stop);
                JSONObject contshot = new JSONObject().put("contShootingMode", valueToSet);
                JSONArray array = new JSONArray().put(0, contshot);
                JSONObject jsonObject = mRemoteApi.setParameterToCamera(VALUE_TO_SET, array);
            } catch (JSONException ex) {
                Log.WriteEx(ex);
            }


        } catch (IOException ex) {
            Log.WriteEx(ex);
        }
    }

    protected String processGetString() {
        JSONArray array = null;
        String ret ="";
        try {
            array = jsonObject.getJSONArray("result");
            ret = array.getJSONObject(0).getString("contShootingMode");
        } catch (JSONException ex) {
            Log.WriteEx(ex);
        }
        return ret;
    }
}
