package com.troop.freedcam.sonyapi.parameters.modes;

import android.os.Handler;

import com.troop.freedcam.sonyapi.sonystuff.SimpleRemoteApi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by Ingo on 19.04.2015.
 */
public class WhiteBalanceModeSony extends BaseModeParameterSony
{
    public WhiteBalanceModeSony(Handler handler,String VALUE_TO_GET, String VALUE_TO_SET, String VALUES_TO_GET, SimpleRemoteApi mRemoteApi) {
        super(handler,VALUE_TO_GET, VALUE_TO_SET, VALUES_TO_GET, mRemoteApi);
    }

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
            }
        } catch (JSONException e) {
            e.printStackTrace();
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected String processGetString() {
        JSONArray array = null;
        String ret ="";
        try {
            array = jsonObject.getJSONArray("result");
            ret = array.getJSONObject(0).getString("whiteBalanceMode");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return ret;
    }
}
