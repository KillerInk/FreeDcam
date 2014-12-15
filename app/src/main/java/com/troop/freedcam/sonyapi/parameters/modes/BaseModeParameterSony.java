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
        try {
            JSONObject jsonObject = mRemoteApi.getParameterFromCamera(VALUE_TO_GET);
            JSONArray resultArrayJson = jsonObject.getJSONArray("result");
        } catch (IOException e) {
            e.printStackTrace();
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
