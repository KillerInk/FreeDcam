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
import android.view.MotionEvent;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import freed.cam.apis.basecamera.AbstractFocusHandler;
import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.FocusEvents;
import freed.cam.events.EventBusHelper;
import freed.cam.events.EventBusLifeCycle;
import freed.cam.events.ValueChangedEvent;
import freed.settings.Frameworks;
import freed.settings.SettingKeys;
import freed.settings.SettingsManager;
import freed.utils.Log;

/**
 * Created by troop on 02.09.2014.
 */
public class FocusHandler extends AbstractFocusHandler implements FocusEvents, EventBusLifeCycle
{
    final String TAG = FocusHandler.class.getSimpleName();
    private boolean aeMeteringSupported;
    private boolean isTouchSupported;




    public FocusHandler(CameraWrapperInterface cameraUiWrapper)
    {
        super(cameraUiWrapper);
    }

    @Override
    public void startListning() {
        EventBusHelper.register(this);
    }

    @Override
    public void stopListning() {
        EventBusHelper.unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onStringValueChanged(ValueChangedEvent<String> valueChangedEvent)
    {
        if (valueChangedEvent.key == SettingKeys.FocusMode && valueChangedEvent.type == String.class)
        {
            if (SettingsManager.getInstance().getFrameWork() != Frameworks.MTK) {
                isTouchSupported = valueChangedEvent.newValue.equals("auto") || valueChangedEvent.newValue.equals("macro") || valueChangedEvent.newValue.equals("touch");
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
        else if (valueChangedEvent.key == SettingKeys.ExposureMode && valueChangedEvent.type == String.class)
        {
            if(SettingsManager.getInstance().getFrameWork() != Frameworks.MTK)
            {
                if (valueChangedEvent.newValue.contains("spot")) {
                    if (focusEvent != null) {
                        aeMeteringSupported = true;
                        focusEvent.AEMeteringSupported(true);
                    }
                } else {
                    if (focusEvent != null) {
                        aeMeteringSupported = false;
                        focusEvent.AEMeteringSupported(false);
                    }
                }
            }
            else
            {
                if (focusEvent != null) {
                    aeMeteringSupported = true;
                    focusEvent.AEMeteringSupported(true);
                }
            }
        }
    }


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
    public void SetMotionEvent(MotionEvent event) {

    }

    @Override
    public void onFocusEvent(boolean event)
    {
        if (focusEvent != null)
            focusEvent.FocusFinished(event);
    }

    @Override
    public void onFocusLock(boolean locked) {

    }
    @Override
    public void StartFocus()
    {
        if (focusEvent != null)
        {
            focusEvent.FocusStarted(0, 0);
        }
        ((CameraHolder) cameraUiWrapper.getCameraHolder()).StartFocus(this);
    }

    @Override
    public void StartTouchToFocus(int x_input, int y_input,int width, int height)
    {
        super.StartTouchToFocus(x_input,y_input,width,height);
        if (focusEvent != null)
            focusEvent.FocusStarted(x_input,y_input);

    }

    @Override
    protected void startTouchFocus(FocusCoordinates obj) {
        if (cameraUiWrapper == null|| cameraUiWrapper.getParameterHandler() == null || cameraUiWrapper.getParameterHandler().get(SettingKeys.FocusMode) == null)
            return;

        Log.d(TAG, "start Touch X:Y " + obj.x +":" + obj.y);
        String focusmode = cameraUiWrapper.getParameterHandler().get(SettingKeys.FocusMode).GetStringValue();
        if (focusmode.equals("auto") || focusmode.equals("macro"))
        {
            Rect targetFocusRect = getFocusRect(obj.x,obj.y, obj.width, obj.height);

            if (targetFocusRect.left >= -1000
                    && targetFocusRect.top >= -1000
                    && targetFocusRect.bottom <= 1000
                    && targetFocusRect.right <= 1000)
            {

                /*if (this.isFocusing)
                {
                    this.cameraUiWrapper.getCameraHolder().CancelFocus();
                    Log.d(this.TAG, "Canceld Focus");
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException ex) {
                        Log.WriteEx(ex);
                    }
                }*/

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

    private Rect getFocusRect(int inputx, int inputy, int width, int height)
    {
        int areasize = (width/8) /2;
        Log.d(TAG, "TapToFocus X:Y " + inputx +":"+inputy);
        if (width == 0 || height == 0)
            return null;
        int left = ((inputx - areasize) * 2000) / width - 1000;
        int right = ((inputx + areasize) * 2000) / width - 1000;
        int top = ((inputy - areasize) * 2000) / height - 1000;
        int bottom = ((inputy + areasize) * 2000) / height - 1000;

        Rect targetFocusRect = new Rect(
                left,
                top,
                right,
                bottom);
        logFocusRect(targetFocusRect);
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
        return targetFocusRect;
    }


}
