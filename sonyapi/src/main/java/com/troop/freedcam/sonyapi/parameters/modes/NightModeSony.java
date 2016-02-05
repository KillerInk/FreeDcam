package com.troop.freedcam.sonyapi.parameters.modes;

import android.os.Build;
import android.os.Handler;

import com.troop.freedcam.sonyapi.sonystuff.SimpleRemoteApi;
import com.troop.freedcam.sonyapi.sonystuff.SimpleStreamSurfaceView;
import com.troop.freedcam.utils.StringUtils;

import java.util.Set;

import static com.troop.freedcam.sonyapi.sonystuff.SimpleStreamSurfaceView.NightPreviewModes.grayscale;
import static com.troop.freedcam.sonyapi.sonystuff.SimpleStreamSurfaceView.NightPreviewModes.off;
import static com.troop.freedcam.sonyapi.sonystuff.SimpleStreamSurfaceView.NightPreviewModes.on;
import static com.troop.freedcam.sonyapi.sonystuff.SimpleStreamSurfaceView.NightPreviewModes.zoompreview;

/**
 * Created by troop on 04.12.2015.
 */
public class NightModeSony extends BaseModeParameterSony
{
    private String currentval = StringUtils.OFF;
    SimpleStreamSurfaceView simpleStreamSurfaceView;
    final String GRAYSCALE = "GrayScale";
    final String ZOOMPREVIEW = "ZoomPreview";

    public NightModeSony(Handler handler, String VALUE_TO_GET, String VALUE_TO_SET, String VALUES_TO_GET, SimpleRemoteApi mRemoteApi, SimpleStreamSurfaceView simpleStreamSurfaceView) {
        super(handler, VALUE_TO_GET, VALUE_TO_SET, VALUES_TO_GET, mRemoteApi);
        this.simpleStreamSurfaceView = simpleStreamSurfaceView;
    }

    public void SetValue(String valueToSet, boolean setToCamera)
    {
        if (valueToSet.equals(StringUtils.ON))
            simpleStreamSurfaceView.nightmode = on;
        else if(valueToSet.equals(GRAYSCALE))
            simpleStreamSurfaceView.nightmode = grayscale;
        else if(valueToSet.equals(ZOOMPREVIEW))
            simpleStreamSurfaceView.nightmode = zoompreview;
        else
            simpleStreamSurfaceView.nightmode = off;
    }

    @Override
    public String GetValue()
    {
        switch (simpleStreamSurfaceView.nightmode)
        {
            case on:
                return StringUtils.ON;
            case off:
                return StringUtils.OFF;
            case grayscale:
                return GRAYSCALE;
            case zoompreview:
                return ZOOMPREVIEW;
            default:
                return StringUtils.OFF;
        }
    }

    @Override
    public String[] GetValues() {
        return new String[] {StringUtils.ON, StringUtils.OFF, GRAYSCALE, ZOOMPREVIEW};
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
