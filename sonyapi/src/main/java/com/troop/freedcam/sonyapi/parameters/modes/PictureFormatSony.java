package com.troop.freedcam.sonyapi.parameters.modes;

import com.troop.freedcam.sonyapi.sonystuff.JsonUtils;
import com.troop.freedcam.sonyapi.sonystuff.SimpleRemoteApi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by troop on 30.01.2015.
 */
public class PictureFormatSony extends BaseModeParameterSony {
    public PictureFormatSony(String VALUE_TO_GET, String VALUE_TO_SET, String VALUES_TO_GET, SimpleRemoteApi mRemoteApi) {
        super(VALUE_TO_GET, VALUE_TO_SET, VALUES_TO_GET, mRemoteApi);
    }

    protected String processGetString() {
        JSONArray array = null;
        String ret ="";
        try {
            array = jsonObject.getJSONArray("result");
            ret = array.getJSONObject(0).getString("stillQuality");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return ret;
    }

    protected void processValuesToSet(String valueToSet)
    {
        try
        {
            try {
                JSONObject o = new JSONObject();
                o.put("stillQuality", valueToSet);
                JSONArray array = new JSONArray().put(0, o);
                JSONObject jsonObject = mRemoteApi.setParameterToCamera(VALUE_TO_SET, array);
            } catch (JSONException e) {
                e.printStackTrace();
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
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
}
