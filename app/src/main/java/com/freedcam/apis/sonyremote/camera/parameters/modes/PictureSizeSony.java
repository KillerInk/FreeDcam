package com.freedcam.apis.sonyremote.camera.parameters.modes;

import android.os.Handler;

import com.freedcam.apis.sonyremote.camera.sonystuff.SimpleRemoteApi;
import com.freedcam.utils.Logger;
import com.freedcam.utils.FreeDPool;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by troop on 15.12.2014.
 */
public class PictureSizeSony extends BaseModeParameterSony
{
    final String TAG = PictureSizeSony.class.getSimpleName();
    public PictureSizeSony(Handler handler, String VALUE_TO_SET, String VALUES_TO_GET, SimpleRemoteApi api)
    {
        super(handler, "getStillSize", "setStillSize", "getAvailableStillSize", api);
    }

    @Override
    public String[] GetValues()
    {
        jsonObject =null;
        FreeDPool.Execute(new Runnable() {
            @Override
            public void run()
            {
                try {
                    jsonObject = mRemoteApi.getParameterFromCamera(VALUES_TO_GET);
                    values = processValuesToReturn();
                    BackgroundValuesHasChanged(values);
                } catch (IOException e) {
                    Logger.exception(e);
                }
            }
        });



        return values;
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
                Logger.exception(e);
            }


        } catch (IOException e) {
            Logger.exception(e);
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
            Logger.exception(e);
        }
        return ret;
    }

    protected String processGetString() {
        JSONArray array = null;
        try {
            array = jsonObject.getJSONArray("result");
        } catch (JSONException e) {
            Logger.exception(e);
        }
        String ret ="";
        try
        {
            JSONObject size = array.getJSONObject(0);
            ret = size.getString("aspect") + "+" +size.getString("size");
        } catch (JSONException | NullPointerException e)
        {
            Logger.exception(e);
        }
        return ret;
    }
}
