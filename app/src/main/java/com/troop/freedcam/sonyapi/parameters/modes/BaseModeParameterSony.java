package com.troop.freedcam.sonyapi.parameters.modes;

import com.troop.freedcam.i_camera.parameters.I_ModeParameter;
import com.troop.freedcam.sonyapi.sonystuff.JsonUtils;
import com.troop.freedcam.sonyapi.sonystuff.SimpleRemoteApi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Set;

/**
 * Created by troop on 15.12.2014.
 */
public class BaseModeParameterSony implements I_SonyApi, I_ModeParameter
{

    protected String VALUE_TO_GET = "";
    protected String VALUE_TO_SET ="";
    protected String VALUES_TO_GET ="";

    private SimpleRemoteApi mRemoteApi;
    private Set<String> mAvailableCameraApiSet;
    private Set<String> mSupportedApiSet;
    JSONObject jsonObject;

    public BaseModeParameterSony(String VALUE_TO_GET, String VALUE_TO_SET, String VALUES_TO_GET, SimpleRemoteApi mRemoteApi)
    {
        this.VALUE_TO_GET = VALUE_TO_GET;
        this.VALUE_TO_SET = VALUE_TO_SET;
        this.VALUES_TO_GET = VALUES_TO_GET;
        this.mRemoteApi = mRemoteApi;
    }

    @Override
    public void SonyApiChanged(Set<String> mAvailableCameraApiSet)
    {
        this.mAvailableCameraApiSet = mAvailableCameraApiSet;
    }

    @Override
    public boolean IsSupported()
    {
        return JsonUtils.isCameraApiAvailable(VALUE_TO_GET, mAvailableCameraApiSet);
    }

    @Override
    public void SetValue(String valueToSet, boolean setToCamera)
    {
        try {
            JSONObject jsonObject = mRemoteApi.setParameterToCamera(VALUE_TO_SET, valueToSet);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public String GetValue()
    {
        jsonObject = null;
        new Thread(new Runnable() {
            @Override
            public void run()
            {
                try {
                    jsonObject = mRemoteApi.getParameterFromCamera(VALUE_TO_GET);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        while (jsonObject == null)
        {
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        JSONArray array;
        try {
            array = jsonObject.getJSONArray("result");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return "";

    }

    @Override
    public String[] GetValues() {
        return new String[0];
    }
}
