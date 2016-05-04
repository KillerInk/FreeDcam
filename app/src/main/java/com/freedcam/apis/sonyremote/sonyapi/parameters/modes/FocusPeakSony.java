package com.freedcam.apis.sonyremote.sonyapi.parameters.modes;

import android.os.Build;
import android.os.Handler;

import com.freedcam.apis.sonyremote.sonyapi.sonystuff.SimpleRemoteApi;
import com.freedcam.apis.sonyremote.sonyapi.sonystuff.SimpleStreamSurfaceView;
import com.freedcam.utils.StringUtils;

import java.util.Set;

/**
 * Created by troop on 23.08.2015.
 */
public class FocusPeakSony extends BaseModeParameterSony {

    private String currentval = StringUtils.OFF;
    private SimpleStreamSurfaceView simpleStreamSurfaceView;


    public FocusPeakSony(Handler handler, String VALUE_TO_SET, String VALUES_TO_GET, SimpleRemoteApi mRemoteApi, SimpleStreamSurfaceView simpleStreamSurfaceView) {
        super(handler, null, null, null, null);
        this.simpleStreamSurfaceView = simpleStreamSurfaceView;
    }

    public void SetValue(String valueToSet, boolean setToCamera)
    {
        simpleStreamSurfaceView.focuspeak = valueToSet.equals(StringUtils.ON);
    }

    @Override
    public String GetValue()
    {
        if (simpleStreamSurfaceView.focuspeak)
            return StringUtils.ON;
        else
            return StringUtils.OFF;
    }

    @Override
    public String[] GetValues() {
        return new String[] {StringUtils.ON, StringUtils.OFF};
    }

    @Override
    public boolean IsSupported() {
        return Build.VERSION.SDK_INT >= 18;
    }


    @Override
    public void SonyApiChanged(Set<String> mAvailableCameraApiSet) {
        //super.SonyApiChanged(mAvailableCameraApiSet);
    }

    @Override
    protected void processValuesToSet(String valueToSet) {
        //super.processValuesToSet(valueToSet);
    }

    @Override
    protected String processGetString() {
        return null;
    }

    @Override
    protected String[] processValuesToReturn() {
        return null;
    }
}
