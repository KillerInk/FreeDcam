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

import com.troop.freedcam.R;

import freed.cam.apis.basecamera.AbstractFocusHandler;
import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.parameters.ParameterEvents;
import freed.utils.Log;

/**
 * Created by troop on 12.12.2014.
 */
@TargetApi(VERSION_CODES.LOLLIPOP)
public class FocusHandler extends AbstractFocusHandler
{
    private int mState;
    private boolean focusenabled;

    private final String TAG = FocusHandler.class.getSimpleName();



    public FocusHandler(CameraWrapperInterface cameraUiWrapper)
    {
        super(cameraUiWrapper);
    }

    public ParameterEvents focusModeListner = new ParameterEvents() {
        @Override
        public void onIsSupportedChanged(boolean value) {

        }

        @Override
        public void onIsSetSupportedChanged(boolean value) {

        }

        @Override
        public void onIntValueChanged(int current) {

        }

        @Override
        public void onValuesChanged(String[] values) {

        }

        @Override
        public void onStringValueChanged(String val) {
            if (val.contains("Continous")|| val.equals(cameraUiWrapper.getContext().getString(R.string.off)))
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
    };

    @Override
    public void StartFocus() {
    }

    @Override
    public void StartTouchToFocus(int x, int y, int width, int height)
    {
       super.StartTouchToFocus(x,y,width,height);
        if (focusEvent != null)
            focusEvent.FocusStarted(x,y);
    }

    @Override
    protected void startTouchFocus(AbstractFocusHandler.FocusCoordinates viewCoordinates) {
        //logFocusRect(rect);
        Log.d(TAG, "Width:" + viewCoordinates.width + "Height" + viewCoordinates.height + " X: " + viewCoordinates.x + "Y : "+viewCoordinates.y);
        if (!focusenabled)
            return;

        Rect sensorSize =  ((CameraHolderApi2) cameraUiWrapper.getCameraHolder()).characteristics.get(CameraCharacteristics.SENSOR_INFO_ACTIVE_ARRAY_SIZE);
        logRect(sensorSize);
        int areasize = (sensorSize.width() /8);
        float xf = (float)viewCoordinates.x * sensorSize.width() / viewCoordinates.width;
        float yf = (float)viewCoordinates.y * sensorSize.height() /  viewCoordinates.height;
        int x_c = (int)xf; //(int)((float)x/width * m.right);
        int y_C = (int) yf; //(int)((float)y/height * m.bottom);
        int left = x_c - areasize;
        int right =x_c +areasize;
        int top = y_C -areasize;
        int bottom = y_C +areasize;
        Rect targetFocusRect = new Rect(left, top,right,bottom);

        logFocusRect(targetFocusRect);
        if (targetFocusRect.left < 0) {
            targetFocusRect.left = 0;
            targetFocusRect.right = areasize*2;
        }
        if (targetFocusRect.right > sensorSize.right) {
            targetFocusRect.right = sensorSize.width();
            targetFocusRect.left = sensorSize.width() -areasize*2;
        }
        if (targetFocusRect.top < sensorSize.top) {
            targetFocusRect.top = 0;
            targetFocusRect.bottom = areasize*2;

        }
        if (targetFocusRect.bottom > sensorSize.bottom)
        {
            targetFocusRect.bottom = sensorSize.height();
            targetFocusRect.top = sensorSize.height() - areasize*2;
        }

        logFocusRect(targetFocusRect);
        MeteringRectangle rectangle = new MeteringRectangle(targetFocusRect.left,targetFocusRect.top,targetFocusRect.right,targetFocusRect.bottom, 1000);
        MeteringRectangle[] mre = { rectangle};
        ((Camera2Fragment) cameraUiWrapper).captureSessionHandler.SetFocusArea(CaptureRequest.CONTROL_AF_REGIONS, mre);
    }

    public ParameterEvents aeModeListner = new ParameterEvents() {
        @Override
        public void onIsSupportedChanged(boolean value) {

        }

        @Override
        public void onIsSetSupportedChanged(boolean value) {

        }

        @Override
        public void onIntValueChanged(int current) {

        }

        @Override
        public void onValuesChanged(String[] values) {

        }

        @Override
        public void onStringValueChanged(String val) {
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
    };

    @Override
    public void SetMeteringAreas(int x, int y, int width, int height)
    {
        int areasize = (width/8)/2;
        Rect sensor_size = ((CameraHolderApi2) cameraUiWrapper.getCameraHolder()).characteristics.get(CameraCharacteristics.SENSOR_INFO_ACTIVE_ARRAY_SIZE);

        int left = (x - areasize) * sensor_size.right /width;

        Rect targetFocusRect = new Rect(
                left,
                (x + areasize) * sensor_size.right /width, //right
                (y - areasize) * sensor_size.bottom /height, //top
                (y + areasize) * sensor_size.bottom / height); //bottom
        if (targetFocusRect.left < 0)
            targetFocusRect.left = sensor_size.left;
        if (targetFocusRect.right > sensor_size.right)
            targetFocusRect.right = sensor_size.right;
        if (targetFocusRect.top < 0)
            targetFocusRect.top = sensor_size.top;
        if (targetFocusRect.bottom > sensor_size.bottom)
            targetFocusRect.bottom = sensor_size.bottom;


        MeteringRectangle rectangle = new MeteringRectangle(targetFocusRect.left,targetFocusRect.top,targetFocusRect.right,targetFocusRect.bottom, 1000);
        MeteringRectangle[] mre = { rectangle};
        ((Camera2Fragment) cameraUiWrapper).captureSessionHandler.SetParameter(CaptureRequest.CONTROL_AE_REGIONS, mre);
        ((Camera2Fragment) cameraUiWrapper).captureSessionHandler.SetParameter(CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER,CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER_START);
    }

    @Override
    public boolean isAeMeteringSupported() {
        return false;
    }

    @Override
    public void SetMotionEvent(MotionEvent event) {

    }

}
