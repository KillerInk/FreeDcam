package com.troop.freedcam.sonyapi.parameters.modes;

import com.troop.freedcam.sonyapi.sonystuff.JsonUtils;
import com.troop.freedcam.sonyapi.sonystuff.SimpleRemoteApi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by troop on 17.12.2014.
 */
public class ExposureModeSony extends BaseModeParameterSony {
    public ExposureModeSony(String VALUE_TO_GET, String VALUE_TO_SET, String VALUES_TO_GET, SimpleRemoteApi mRemoteApi) {
        super(VALUE_TO_GET, VALUE_TO_SET, VALUES_TO_GET, mRemoteApi);
    }

    @Override
    protected void processValuesToSet(String valueToSet) {
        super.processValuesToSet(valueToSet);
    }


}
