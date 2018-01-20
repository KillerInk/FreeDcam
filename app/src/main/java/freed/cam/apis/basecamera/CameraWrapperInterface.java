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

package freed.cam.apis.basecamera;

import android.content.Context;
import android.os.HandlerThread;
import android.view.SurfaceView;

import freed.ActivityInterface;
import freed.cam.apis.basecamera.modules.ModuleHandlerAbstract;
import freed.cam.apis.basecamera.parameters.AbstractParameterHandler;
import freed.renderscript.RenderScriptManager;
import freed.renderscript.RenderScriptProcessorInterface;

/**
 * Created by troop on 09.12.2014.
 */
public interface CameraWrapperInterface extends CameraStateEvents
{
    /**
     * Start the Camera
     */
    void startCameraAsync();

    /**
     * Stop the Camera
     */
    void stopCameraAsync();
    void restartCameraAsync();

    void startPreviewAsync();
    void stopPreviewAsync();

    /*void initCamera();
    void createCamera();*/

    /**
     * Get the current active CameraHolderSony
     * @return
     */
    CameraHolderInterface getCameraHolder();

    /**
     * get the active parameterhandler
     * @return
     */
    AbstractParameterHandler getParameterHandler();

    ModuleHandlerAbstract getModuleHandler();
    SurfaceView getSurfaceView();
    AbstractFocusHandler getFocusHandler();

    /**
     * set the listner that get notifyed when camera state has changed
     * @param cameraChangedListner to notify
     */
    void setCameraEventListner(CameraStateEvents cameraChangedListner);


    /**
     * get the left margine between display and preview
     * @return
     */
    int getMargineLeft();
    /**
     * get the right margine between display and preview
     * @return
     */
    int getMargineRight();
    /**
     * get the top margine between display and preview
     * @return
     */
    int getMargineTop();
    /**
     * get the preview width
     * @return
     */
    int getPreviewWidth();
    /**
     * get the preview height
     * @return
     */
    int getPreviewHeight();

    boolean isAeMeteringSupported();

    Context getContext();

    RenderScriptProcessorInterface getFocusPeakProcessor();

    RenderScriptManager getRenderScriptManager();

    ActivityInterface getActivityInterface();

    String getResString(int id);

    HandlerThread getCameraHandlerThread();

    void fireCameraOpen();
    void fireCameraOpenFinished();
    void fireCameraClose();
    void firePreviewClose();
    void firePreviewOpen();
    void fireCameraError(String msg);

}
