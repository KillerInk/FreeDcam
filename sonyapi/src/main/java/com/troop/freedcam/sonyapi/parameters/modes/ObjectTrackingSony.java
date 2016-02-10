package com.troop.freedcam.sonyapi.parameters.modes;

import android.os.Handler;

import com.troop.freedcam.sonyapi.sonystuff.JsonUtils;
import com.troop.freedcam.sonyapi.sonystuff.SimpleRemoteApi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by troop on 31.01.2015.
 */
public class ObjectTrackingSony extends BaseModeParameterSony {
    public ObjectTrackingSony(Handler handler, String VALUE_TO_GET, String VALUE_TO_SET, SimpleRemoteApi mRemoteApi) {
        super(handler, "getTrackingFocus", "setTrackingFocus", "getAvailableTrackingFocus", mRemoteApi);
    }

    protected String[] processValuesToReturn() {
        String[] ret = null;
        try {
            JSONArray array = jsonObject.getJSONArray("result");
            JSONObject ob = array.optJSONObject(0);
            JSONArray subarray = ob.getJSONArray("candidate");
            ret = JsonUtils.ConvertJSONArrayToStringArray(subarray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return ret;
    }

    protected void processValuesToSet(String valueToSet)
    {

        try {
            JSONObject contshot = new JSONObject().put("trackingFocus", valueToSet);
            JSONArray array = new JSONArray().put(0, contshot);
            JSONObject jsonObject = mRemoteApi.setParameterToCamera(VALUE_TO_SET, array);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected String processGetString() {
        JSONArray array = null;
        String ret ="";
        try {
            array = jsonObject.getJSONArray("result");
            ret = array.getJSONObject(0).getString("trackingFocus");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return ret;
    }
}
