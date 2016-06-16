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

package freed.cam.apis.camera1.cameraholder;

import android.hardware.Camera;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.camera1.CameraHolder;
import freed.cam.apis.camera1.parameters.ParametersHandler;
import freed.utils.Logger;

/**
 * Created by troop on 08.06.2016.
 */
public class CameraHolderMTK extends CameraHolder
{
    private final String TAG = CameraHolderMTK.class.getSimpleName();
    public CameraHolderMTK(CameraWrapperInterface cameraUiWrapper, Frameworks frameworks) {
        super(cameraUiWrapper,frameworks);
    }

    @Override
    public boolean OpenCamera(int camera)
    {
        try {
            setMtkAppMode();
            mCamera = Camera.open(camera);
            isRdy = true;
        }
        catch (RuntimeException ex)
        {
            cameraUiWrapper.onCameraError("Fail to connect to camera service");
            isRdy = false;
        }

        cameraUiWrapper.onCameraOpen("");
        super.OpenCamera(0);
        return isRdy;
    }

    @Override
    public void StartPreview()
    {
        //not sure if that is realy needed. same stuff gets applied when BaseMTKDevice.java gets created
        ((ParametersHandler) cameraUiWrapper.GetParameterHandler()).SetupMTK();
        super.StartPreview();
    }

    private void setMtkAppMode()
    {
        try {
            Class camera = Class.forName("android.hardware.Camera");
            Method[] meths = camera.getMethods();
            Method app = null;
            for (Method m : meths)
            {
                if (m.getName().equals("setProperty"))
                    app = m;
            }
            if (app == null)
                throw new  NoSuchMethodException();
            app.invoke(null, "client.appmode", "MtkEng");
        } catch (ClassNotFoundException e) {
            Logger.e(TAG,e.getMessage());
        } catch (InvocationTargetException e) {
            Logger.e(TAG,e.getMessage());
        } catch (NoSuchMethodException e) {
            Logger.e(TAG,e.getMessage());
        } catch (IllegalAccessException e) {
            Logger.e(TAG,e.getMessage());
        }
    }
}
