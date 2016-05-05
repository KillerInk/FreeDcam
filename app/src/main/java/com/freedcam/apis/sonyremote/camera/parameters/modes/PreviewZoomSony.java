package com.freedcam.apis.sonyremote.camera.parameters.modes;

import android.os.Build;
import android.os.Handler;

import com.freedcam.apis.sonyremote.camera.sonystuff.SimpleStreamSurfaceView;
import com.freedcam.apis.basecamera.camera.parameters.modes.AbstractModeParameter;

/**
 * Created by troop on 25.03.2016.
 */
public class PreviewZoomSony extends AbstractModeParameter
{
    private SimpleStreamSurfaceView surfaceView;
    private int zoomFactor = 8;
    public PreviewZoomSony(Handler uiHandler, SimpleStreamSurfaceView surfaceView) {
        super(uiHandler);
        this.surfaceView = surfaceView;
    }

    @Override
    public boolean IsSupported() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
    }

    @Override
    public void SetValue(String valueToSet, boolean setToCamera)
    {
        zoomFactor = Integer.parseInt(valueToSet);
        surfaceView.PreviewZOOMFactor = zoomFactor;
    }

    @Override
    public String GetValue() {
        return zoomFactor+"";
    }

    @Override
    public String[] GetValues() {
        return new String[] {"2","4","8","10","12","14","16","18","20"};
    }

    @Override
    public void addEventListner(I_ModeParameterEvent eventListner) {
        super.addEventListner(eventListner);
    }

    @Override
    public void removeEventListner(I_ModeParameterEvent parameterEvent) {
        super.removeEventListner(parameterEvent);
    }

    @Override
    public void BackgroundValueHasChanged(String value) {

    }

    @Override
    public boolean IsVisible() {
        return IsSupported();
    }

    @Override
    public void BackgroundValuesHasChanged(String[] value) {

    }

    @Override
    public void BackgroundIsSupportedChanged(boolean value) {

    }

    @Override
    public void BackgroundSetIsSupportedHasChanged(boolean value) {

    }

    @Override
    public void BackgroundVisibilityChanged(boolean value) {

    }
}
