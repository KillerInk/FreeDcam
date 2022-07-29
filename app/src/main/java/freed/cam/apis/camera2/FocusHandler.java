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

import androidx.databinding.Observable;

import com.troop.freedcam.R;

import freed.FreedApplication;
import freed.cam.apis.basecamera.AbstractFocusHandler;
import freed.cam.apis.basecamera.parameters.AbstractParameter;
import freed.cam.previewpostprocessing.PreviewController;
import freed.settings.SettingsManager;
import freed.utils.Log;

/**
 * Created by troop on 12.12.2014.
 */
@TargetApi(VERSION_CODES.LOLLIPOP)
public class FocusHandler extends AbstractFocusHandler<Camera2>
{
    private boolean focusenabled;

    private final String TAG = FocusHandler.class.getSimpleName();

    private final int focusSize = 100;
    private SettingsManager settingsManager;


    public FocusHandler(Camera2 cameraUiWrapper)
    {
        super(cameraUiWrapper);
        settingsManager = FreedApplication.settingsManager();
    }

    public Observable.OnPropertyChangedCallback focusmodeObserver =  new Observable.OnPropertyChangedCallback() {
        @Override
        public void onPropertyChanged(Observable sender, int propertyId) {
            String val = ((AbstractParameter)sender).getStringValue();
            if (val.contains("Continous") || val.equals(FreedApplication.getStringFromRessources(R.string.off))) {
                focusenabled = false;
                if (focusEvent != null)
                    focusEvent.TouchToFocusSupported(false);
            } else {
                focusenabled = true;
                if (focusEvent != null)
                    focusEvent.TouchToFocusSupported(true);
            }
        }
    };

    public Observable.OnPropertyChangedCallback aemeteringObserver = new Observable.OnPropertyChangedCallback() {
        @Override
        public void onPropertyChanged(Observable sender, int propertyId) {
            String val = ((AbstractParameter)sender).getStringValue();
            if (val.equals("Spot") && focusEvent != null)
                focusEvent.AEMeteringSupported(true);
            else if (focusEvent != null)
                focusEvent.AEMeteringSupported(false);
        }
    };


    @Override
    protected void startTouchFocus(float x ,float y) {
        if (!focusenabled)
            return;

        MeteringRectangle[] mre = {translateScreenToCameraPos(x,y)};
        cameraUiWrapper.captureSessionHandler.SetFocusArea(mre);
        if (settingsManager.get(SettingsManager.AE_METERING).get().equals("Spot Focus"))
            cameraUiWrapper.captureSessionHandler.setMeteringArea(mre);
    }

    private MeteringRectangle translateScreenToCameraPos(float x, float y)
    {
        Rect sensorSize =  cameraUiWrapper.getCameraHolder().characteristics.get(CameraCharacteristics.SENSOR_INFO_ACTIVE_ARRAY_SIZE);
        int x_pos = (int) (x *(float)sensorSize.width());
        int y_pos = (int) (y * (float)sensorSize.height());

        Log.d(TAG,"x pos:" + x_pos + " y pos:" + y_pos);
        x_pos = clamp(x_pos,sensorSize.width());
        y_pos = clamp(y_pos,sensorSize.height());
        Log.d(TAG,"clamp x pos:" + x_pos + " y pos:" + y_pos);
        return new MeteringRectangle(x_pos-focusSize, y_pos-focusSize,x_pos+focusSize,y_pos+focusSize,MeteringRectangle.METERING_WEIGHT_MAX-1);
    }

    private int clamp(int in, int max)
    {
        if (in -focusSize < 0)
        {
            in = in + (in -focusSize)*-1;
        }
        if (in +focusSize > max)
        {
            in = in - (max - (in +focusSize));
        }
        return in;
    }


    @Override
    public void SetMeteringAreas(int x, int y, int width, int height)
    {
        MeteringRectangle[] mre = {translateScreenToCameraPos(x,y)};
        cameraUiWrapper.captureSessionHandler.setMeteringArea(mre);
    }

    @Override
    public boolean isAeMeteringSupported() {
        return settingsManager.get(SettingsManager.AE_METERING).equals("Spot");
    }

    @Override
    public boolean isTouchSupported() {
        return focusenabled;
    }

}
