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

package freed.cam.apis.camera1;

import android.graphics.Rect;

import androidx.databinding.Observable;

import freed.FreedApplication;
import freed.cam.apis.basecamera.AbstractFocusHandler;
import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.FocusEvents;
import freed.cam.apis.basecamera.parameters.AbstractParameter;
import freed.settings.Frameworks;
import freed.settings.SettingKeys;
import freed.settings.SettingsManager;
import freed.utils.Log;

/**
 * Created by troop on 02.09.2014.
 */
public class FocusHandler extends AbstractFocusHandler implements FocusEvents
{
    final String TAG = FocusHandler.class.getSimpleName();
    private boolean aeMeteringSupported;
    private boolean isTouchSupported;
    private final SettingsManager settingsManager;



    public FocusHandler(CameraWrapperInterface cameraUiWrapper)
    {
        super(cameraUiWrapper);
        settingsManager = FreedApplication.settingsManager();
    }


    public Observable.OnPropertyChangedCallback focusmodeObserver =  new Observable.OnPropertyChangedCallback() {
        @Override
        public void onPropertyChanged(Observable sender, int propertyId) {
            String val = ((AbstractParameter)sender).getStringValue();
            if (settingsManager.getFrameWork() != Frameworks.MTK) {
                isTouchSupported = val.equals("auto") || val.equals("macro") || val.equals("touch");
                if (focusEvent != null)
                    focusEvent.TouchToFocusSupported(isTouchSupported);
            }
            else {
                if (focusEvent != null) {
                    aeMeteringSupported = true;
                    focusEvent.TouchToFocusSupported(true);
                }
            }
        }
    };


    @Override
    public boolean isAeMeteringSupported()
    {
        return aeMeteringSupported;
    }

    @Override
    public boolean isTouchSupported() {
        return isTouchSupported;
    }

    @Override
    public void onFocusEvent(boolean event)
    {
        float[] focusdistance = new float[3];
        ((CameraHolder)cameraUiWrapper.getCameraHolder()).GetCameraParameters().getFocusDistances(focusdistance);
        if (focusEvent != null)
            focusEvent.FocusFinished(event,focusdistance[0],focusdistance[2],focusdistance[1]);
    }

    @Override
    public void onFocusLock(boolean locked) {

    }

    @Override
    protected void startTouchFocus(float x, float y) {
        if (cameraUiWrapper == null|| cameraUiWrapper.getParameterHandler() == null || cameraUiWrapper.getParameterHandler().get(SettingKeys.FOCUS_MODE) == null)
            return;

        Log.d(TAG, "start Touch X:Y " + x +":" + y);
        String focusmode = cameraUiWrapper.getParameterHandler().get(SettingKeys.FOCUS_MODE).getStringValue();
        if (focusmode.equals("auto") || focusmode.equals("macro"))
        {
            String[] size = cameraUiWrapper.getParameterHandler().get(SettingKeys.PREVIEW_SIZE).getStringValue().split("x");
            int w = Integer.parseInt(size[0]);
            int h = Integer.parseInt(size[1]);
            /*int x_norm = (int) (x * w);
            int y_norm = (int) (y * h);*/
            Rect targetFocusRect = getFocusRect(x,y, w, h);

            if (targetFocusRect.left >= -1000
                    && targetFocusRect.top >= -1000
                    && targetFocusRect.bottom <= 1000
                    && targetFocusRect.right <= 1000)
            {
                logFocusRect(targetFocusRect);
                //tempDIS
                cameraUiWrapper.getParameterHandler().SetFocusAREA(targetFocusRect);

                if (cameraUiWrapper.getCameraHolder() != null)
                    ((CameraHolder) cameraUiWrapper.getCameraHolder()).StartFocus(this);
            }
        }
    }

    @Override
    public void SetMeteringAreas(int x, int y, int width, int height)
    {
        ((CameraHolder) cameraUiWrapper.getCameraHolder()).SetMeteringAreas(getFocusRect(x,y, width, height));
    }

    private Rect getFocusRect(float inputx, float inputy, int width, int height)
    {
        int areasize = ((width/8) /2);
        Log.d(TAG, "TapToFocus X:Y " + inputx +":"+inputy);
        if (width == 0 || height == 0)
            return null;

        int x = (int)(inputx * 2000) -1000;
        int y = (int)(inputy * 2000) -1000;

        int left =  x- areasize;
        int right = x +areasize;
        int top = y -areasize;
        int bottom = y + areasize;

        Rect targetFocusRect = new Rect(
                left,
                top,
                right,
                bottom);

        //check if stuff is to big or to small and set it to min max
        if (targetFocusRect.left < -1000)
        {
            int dif = targetFocusRect.left + 1000;
            targetFocusRect.left = -1000;
            targetFocusRect.right += dif;
        }
        if (targetFocusRect.right > 1000)
        {
            int dif = targetFocusRect.right - 1000;
            targetFocusRect.right = 1000;
            targetFocusRect.left -= dif;
        }
        if (targetFocusRect.top < -1000)
        {
            int dif = targetFocusRect.top + 1000;
            targetFocusRect.top = -1000;
            targetFocusRect.bottom += dif;
        }
        if (targetFocusRect.bottom > 1000)
        {
            int dif = targetFocusRect.bottom -1000;
            targetFocusRect.bottom = 1000;
            targetFocusRect.top -=dif;
        }
        logFocusRect(targetFocusRect);
        return targetFocusRect;
    }


}
