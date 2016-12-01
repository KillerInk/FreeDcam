/*
 *
 *     Copyright (C) 2015 Ingo Fuchs
 *     This program is free software; you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation; either version 2 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License along
 *     with this program; if not, write to the Free Software Foundation, Inc.,
 *     51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 * /
 */

package freed.cam.apis.camera2;

import android.annotation.TargetApi;
import android.graphics.Rect;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.MeteringRectangle;
import android.os.Build.VERSION_CODES;
import android.view.MotionEvent;

import freed.cam.apis.KEYS;
import freed.cam.apis.basecamera.AbstractFocusHandler;
import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.FocusRect;
import freed.cam.apis.basecamera.parameters.I_ParametersLoaded;
import freed.cam.apis.basecamera.parameters.modes.AbstractModeParameter.I_ModeParameterEvent;
import android.util.Log;

/**
 * Created by troop on 12.12.2014.
 */
@TargetApi(VERSION_CODES.LOLLIPOP)
public class FocusHandler extends AbstractFocusHandler implements I_ParametersLoaded
{
    private int mState;
    private FocusRect focusRect;
    private boolean focusenabled;

    private final String TAG = FocusHandler.class.getSimpleName();

    public FocusHandler(CameraWrapperInterface cameraUiWrapper)
    {
        super(cameraUiWrapper);
    }

    public I_ModeParameterEvent focusModeListner = new I_ModeParameterEvent() {
        @Override
        public void onParameterValueChanged(String val)
        {
            if (val.contains("continous")|| val.equals(KEYS.OFF))
            {
                focusenabled = false;
                if (focusEvent != null)
                    focusEvent.TouchToFocusSupported(false);
            }
            else
            {
                focusenabled = true;
                if (focusEvent != null)
                    focusEvent.TouchToFocusSupported(true);
            }
        }

        @Override
        public void onParameterIsSupportedChanged(boolean isSupported) {

        }

        @Override
        public void onParameterIsSetSupportedChanged(boolean isSupported) {

        }

        @Override
        public void onParameterValuesChanged(String[] values) {

        }

        @Override
        public void onVisibilityChanged(boolean visible) {

        }
    };

    @Override
    public void StartFocus() {
    }

    @Override
    public void StartTouchToFocus(FocusRect rect, int width, int height)
    {
        logFocusRect(rect);
        Log.d(TAG, "Width:" + width + "Height" + height);
        if (!focusenabled)
            return;
        focusRect = rect;
        Rect m =  ((CameraHolderApi2) cameraUiWrapper.GetCameraHolder()).characteristics.get(CameraCharacteristics.SENSOR_INFO_ACTIVE_ARRAY_SIZE);
        logRect(m);
        FocusRect targetFocusRect = new FocusRect(
                rect.left * m.right /width,
                rect.right * m.right /width,
                rect.top * m.bottom /height,
                rect.bottom * m.bottom / height,rect.x,rect.y);
        logFocusRect(targetFocusRect);
        if (targetFocusRect.left < m.left)
            targetFocusRect.left = m.left;
        if (targetFocusRect.right > m.right)
            targetFocusRect.right = m.right;
        if (targetFocusRect.top < m.top)
            targetFocusRect.top = m.top;
        if (targetFocusRect.bottom > m.bottom)
            targetFocusRect.bottom = m.bottom;
        logFocusRect(targetFocusRect);
        MeteringRectangle rectangle = new MeteringRectangle(targetFocusRect.left,targetFocusRect.top,targetFocusRect.right,targetFocusRect.bottom, 1000);
        MeteringRectangle[] mre = { rectangle};
        ((CameraHolderApi2) cameraUiWrapper.GetCameraHolder()).SetFocusArea(CaptureRequest.CONTROL_AF_REGIONS, mre);
        if (focusEvent != null)
            focusEvent.FocusStarted(focusRect);
    }

    public I_ModeParameterEvent aeModeListner = new I_ModeParameterEvent() {
        @Override
        public void onParameterValueChanged(String val)
        {
            if (val.equals("off"))
            {
                if (focusEvent != null)
                    focusEvent.AEMeteringSupported(false);
            }
            else {
                if (focusEvent != null)
                    focusEvent.AEMeteringSupported(true);
            }

        }

        @Override
        public void onParameterIsSupportedChanged(boolean isSupported) {

        }

        @Override
        public void onParameterIsSetSupportedChanged(boolean isSupported) {

        }

        @Override
        public void onParameterValuesChanged(String[] values) {

        }

        @Override
        public void onVisibilityChanged(boolean visible) {

        }
    };

    @Override
    public void SetMeteringAreas(FocusRect rect, int width, int height)
    {
        Rect m = ((CameraHolderApi2) cameraUiWrapper.GetCameraHolder()).characteristics.get(CameraCharacteristics.SENSOR_INFO_ACTIVE_ARRAY_SIZE);
        if (rect.left < m.left)
            rect.left = m.left;
        if (rect.right > m.right)
            rect.right = m.right;
        if (rect.top < m.top)
            rect.top = m.top;
        if (rect.bottom > m.bottom)
            rect.bottom = m.bottom;
        MeteringRectangle rectangle = new MeteringRectangle(rect.left,rect.top,rect.right,rect.bottom, 1000);
        MeteringRectangle[] mre = { rectangle};
        ((CameraHolderApi2) cameraUiWrapper.GetCameraHolder()).SetParameter(CaptureRequest.CONTROL_AE_REGIONS, mre);
        ((CameraHolderApi2) cameraUiWrapper.GetCameraHolder()).SetParameter(CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER,CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER_START);
    }

    @Override
    public boolean isAeMeteringSupported() {
        return false;
    }

    @Override
    public void SetMotionEvent(MotionEvent event) {

    }

    @Override
    public void ParametersLoaded(CameraWrapperInterface cameraWrapper)
    {
        if (focusEvent == null
                || ((CameraHolderApi2) cameraUiWrapper.GetCameraHolder()).characteristics.get(CameraCharacteristics.CONTROL_MAX_REGIONS_AE) == null
                || ((CameraHolderApi2) cameraUiWrapper.GetCameraHolder()).characteristics.get(CameraCharacteristics.CONTROL_MAX_REGIONS_AWB) == null)
            return;
        if (((CameraHolderApi2) cameraUiWrapper.GetCameraHolder()).characteristics.get(CameraCharacteristics.CONTROL_MAX_REGIONS_AE)>0)
            focusEvent.AEMeteringSupported(true);
        else
            focusEvent.AEMeteringSupported(false);
    }
}
