package com.freedcam.apis.sonyremote.camera.parameters.modes;

import com.freedcam.apis.sonyremote.camera.sonystuff.JsonUtils;
import com.freedcam.apis.sonyremote.camera.sonystuff.SimpleRemoteApi;
import com.freedcam.utils.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by troop on 31.01.2015.
 */
public class ObjectTrackingSony extends BaseModeParameterSony
{
    final String TAG = ObjectTrackingSony.class.getSimpleName();
    public ObjectTrackingSony(SimpleRemoteApi mRemoteApi) {
        super("getTrackingFocus", "setTrackingFocus", "getAvailableTrackingFocus", mRemoteApi);
    }

    protected String[] processValuesToReturn() {
        String[] ret = null;
        try {
            JSONArray array = jsonObject.getJSONArray("result");
            JSONObject ob = array.optJSONObject(0);
            JSONArray subarray = ob.getJSONArray("candidate");
            ret = JsonUtils.ConvertJSONArrayToStringArray(subarray);
        } catch (JSONException e) {
            Logger.exception(e);
        }
        return ret;
    }

    protected void processValuesToSet(String valueToSet)
    {

        try {
            JSONObject contshot = new JSONObject().put("trackingFocus", valueToSet);
            JSONArray array = new JSONArray().put(0, contshot);
            JSONObject jsonObject = mRemoteApi.setParameterToCamera(VALUE_TO_SET, array);
        } catch (JSONException | IOException e) {
            Logger.exception(e);
        }
    }

    protected String processGetString() {
        JSONArray array = null;
        String ret ="";
        try {
            array = jsonObject.getJSONArray("result");
            ret = array.getJSONObject(0).getString("trackingFocus");
        } catch (JSONException e) {
            Logger.exception(e);
        }
        return ret;
    }
}
