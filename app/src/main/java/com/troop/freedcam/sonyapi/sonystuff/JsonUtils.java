package com.troop.freedcam.sonyapi.sonystuff;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Set;

/**
 * Created by troop on 14.12.2014.
 */
public class JsonUtils
{
    final static String TAG = JsonUtils.class.getSimpleName();
    /**
     * Retrieve a list of APIs that are supported by the target device.
     *
     * @param replyJson
     */
    public static void loadSupportedApiList(JSONObject replyJson, Set<String> mSupportedApiSet) {
        synchronized (mSupportedApiSet) {
            try {
                JSONArray resultArrayJson = replyJson.getJSONArray("results");
                for (int i = 0; i < resultArrayJson.length(); i++) {
                    mSupportedApiSet.add(resultArrayJson.getJSONArray(i).getString(0));
                }
            } catch (JSONException e) {
                Log.w(TAG, "loadSupportedApiList: JSON format error.");
            }
        }
    }

    /**
     * Check if the specified API is available at present. This works correctly
     * only for Camera API.
     *
     * @param apiName
     * @return
     */
    public static boolean isCameraApiAvailable(String apiName, Set<String> mAvailableCameraApiSet) {
        boolean isAvailable = false;
        synchronized (mAvailableCameraApiSet) {
            isAvailable = mAvailableCameraApiSet.contains(apiName);
        }
        return isAvailable;
    }

    /**
     * Check if the specified API is supported. This is for camera and avContent
     * service API. The result of this method does not change dynamically.
     *
     * @param apiName
     * @return
     */
    public static boolean isApiSupported(String apiName, Set<String> mSupportedApiSet) {
        boolean isAvailable = false;
        synchronized (mSupportedApiSet) {
            isAvailable = mSupportedApiSet.contains(apiName);
        }
        return isAvailable;
    }

    /**
     * Retrieve a list of APIs that are available at present.
     *
     * @param replyJson
     */
    public static void loadAvailableCameraApiList(JSONObject replyJson, Set<String> mAvailableCameraApiSet) {
        synchronized (mAvailableCameraApiSet) {
            mAvailableCameraApiSet.clear();
            try {
                JSONArray resultArrayJson = replyJson.getJSONArray("result");
                JSONArray apiListJson = resultArrayJson.getJSONArray(0);
                for (int i = 0; i < apiListJson.length(); i++) {
                    mAvailableCameraApiSet.add(apiListJson.getString(i));
                }
            } catch (JSONException e) {
                Log.w(TAG, "loadAvailableCameraApiList: JSON format error.");
            }
        }
    }

    public static String[] ConvertJSONArrayToStringArray(JSONArray array)
    {
        String[] ret = new String[array.length()];
        for (int i = 0; i < array.length(); i++)
        {
            try {
                ret[i] = array.get(i).toString();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return ret;
    }
}
