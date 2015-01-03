package com.troop.freedcam.sonyapi.sonystuff;

import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by troop on 02.01.2015.
 */
public class ExtendedCameraEventObserver extends SimpleCameraEventObserver {
    /**
     * Constructor.
     *
     * @param context   context to notify the changes by UI thread.
     * @param apiClient API client
     */
    public ExtendedCameraEventObserver(Context context, SimpleRemoteApi apiClient)
    {
        super(context, apiClient);
    }
    ExtendedChangeListener mListner;


    public void setEventChangeListener(ExtendedChangeListener listener) {
        super.setEventChangeListener(listener);
        this.mListner = listener;
    }

    public abstract class ExtendedChangeListener extends ChangeListenerTmpl
    {


    }

    @Override
    protected void processEvents(JSONObject replyJson) throws JSONException {
        super.processEvents(replyJson);


    }




}
