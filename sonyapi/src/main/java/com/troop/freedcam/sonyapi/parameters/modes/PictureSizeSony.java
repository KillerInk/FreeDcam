package com.troop.freedcam.sonyapi.parameters.modes;

import com.troop.freedcam.sonyapi.sonystuff.SimpleRemoteApi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by troop on 15.12.2014.
 */
public class PictureSizeSony extends BaseModeParameterSony
{
    public PictureSizeSony(String VALUE_TO_GET, String VALUE_TO_SET, String VALUES_TO_GET, SimpleRemoteApi api)
    {
        super(VALUE_TO_GET, VALUE_TO_SET, VALUES_TO_GET, api);
    }

    @Override
    protected void processValuesToSet(String valueToSet) {
        try
        {
            String split[] = valueToSet.split("x");
            try {
                JSONArray array = new JSONArray().put(0, split[0]).put(1, split[1]);
                JSONObject jsonObject = mRemoteApi.setParameterToCamera(VALUE_TO_SET, array);
            } catch (JSONException e) {
                e.printStackTrace();
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected String[] processValuesToReturn() {
        String[] ret = null;
        try {
            JSONArray array = jsonObject.getJSONArray("result");
            JSONArray subarray = array.getJSONArray(1);
            ret = new String[subarray.length()];
            for (int i =0; i < subarray.length(); i++)
            {
                JSONObject size = subarray.getJSONObject(i);
                ret[i] = size.getString("aspect") + "x" +size.getString("size");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return ret;
    }

    protected String processGetString() {
        JSONArray array = null;
        try {
            array = jsonObject.getJSONArray("result");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String ret ="";
        try
        {
            JSONObject size = array.getJSONObject(0);
            ret = size.getString("aspect") + "+" +size.getString("size");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return ret;
    }
}
