package com.troop.freedcam.sonyapi.parameters.modes;

import android.os.Handler;

import com.troop.freedcam.i_camera.parameters.AbstractModeParameter;
import com.troop.freedcam.sonyapi.sonystuff.SimpleRemoteApi;
import com.troop.freedcam.sonyapi.sonystuff.SimpleStreamSurfaceView;
import com.troop.freedcam.utils.StringUtils;

import java.util.Set;

/**
 * Created by troop on 23.08.2015.
 */
public class FocusPeakSony extends BaseModeParameterSony {

    private String currentval = StringUtils.OFF;
    SimpleStreamSurfaceView simpleStreamSurfaceView;


    public FocusPeakSony(Handler handler, String VALUE_TO_GET, String VALUE_TO_SET, String VALUES_TO_GET, SimpleRemoteApi mRemoteApi, SimpleStreamSurfaceView simpleStreamSurfaceView) {
        super(handler, VALUE_TO_GET, VALUE_TO_SET, VALUES_TO_GET, mRemoteApi);
        this.simpleStreamSurfaceView = simpleStreamSurfaceView;
    }

    public void SetValue(String valueToSet, boolean setToCamera)
    {
        if (valueToSet.equals(StringUtils.ON))
            simpleStreamSurfaceView.focuspeak = true;
        else
            simpleStreamSurfaceView.focuspeak = false;
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
        return true;
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
