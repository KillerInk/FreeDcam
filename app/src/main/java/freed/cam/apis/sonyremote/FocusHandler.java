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

package freed.cam.apis.sonyremote;

import android.view.MotionEvent;

import java.util.Set;

import freed.cam.apis.basecamera.AbstractFocusHandler;
import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.FocusEvents;
import freed.cam.apis.sonyremote.parameters.modes.I_SonyApi;
import freed.utils.Log;

/**
 * Created by troop on 31.01.2015.
 */
public class FocusHandler extends AbstractFocusHandler implements FocusEvents, I_SonyApi
{
    private final String TAG = FocusHandler.class.getSimpleName();
    private boolean isFocusing;

    @Override
    protected void startTouchFocus(FocusCoordinates obj) {

    }

    public FocusHandler(CameraWrapperInterface cameraUiWrapper)
    {
        super(cameraUiWrapper);
    }

    @Override
    public void StartFocus() {

    }

    @Override
    public void StartTouchToFocus(int x, int y, int width, int height)
    {
        int areasize = (width*height) /8;
        if (this.cameraUiWrapper.getParameterHandler() == null)
            return;
        if (this.isFocusing)
        {
            this.cameraUiWrapper.getCameraHolder().CancelFocus();
            Log.d(this.TAG, "Canceld Focus");
            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
                Log.WriteEx(ex);
            }
        }

        double xproz = x / (double)width * 100;
        double yproz = y / (double)height *100;
        Log.d(this.TAG, "set focus to: x: " + xproz + " y: " +yproz);
        ((CameraHolderSony) this.cameraUiWrapper.getCameraHolder()).StartFocus(this);
        ((CameraHolderSony) this.cameraUiWrapper.getCameraHolder()).SetTouchFocus(xproz, yproz);
        this.isFocusing = true;
        if (this.focusEvent != null)
            this.focusEvent.FocusStarted(x,y);
    }

    @Override
    public void SetMeteringAreas(int x,int y, int width, int height) {

    }

    @Override
    public boolean isAeMeteringSupported() {
        return false;
    }

    @Override
    public void SetMotionEvent(MotionEvent event) {
        this.cameraUiWrapper.getSurfaceView().onTouchEvent(event);
    }


    @Override
    public void onFocusEvent(boolean success)
    {
        this.isFocusing = false;
        if (this.focusEvent != null) {
            this.focusEvent.FocusFinished(success);
            this.focusEvent.FocusLocked(((CameraHolderSony) this.cameraUiWrapper.getCameraHolder()).canCancelFocus());
        }

    }

    @Override
    public void onFocusLock(boolean locked) {
        if (this.focusEvent != null) {
            focusEvent.FocusLocked(locked);
        }
    }

    @Override
    public void SonyApiChanged(Set<String> mAvailableCameraApiSet) {
        if (focusEvent == null)
            return;
        if (mAvailableCameraApiSet.contains("setTouchAFPosition"))
            focusEvent.TouchToFocusSupported(true);
        else
            focusEvent.TouchToFocusSupported(false);
    }
}


