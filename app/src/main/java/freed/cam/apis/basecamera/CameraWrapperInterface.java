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

import freed.cam.event.camera.CameraHolderEvent;
import freed.cam.event.camera.CameraHolderEventHandler;
import freed.cam.event.capture.CaptureStateChangedEventHandler;
import freed.cam.event.module.ModuleChangedEventHandler;

/**
 * Created by troop on 09.12.2014.
 */
public interface CameraWrapperInterface extends CameraObjects, CameraInterface, CameraHolderEvent
{
    void setCaptureStateChangedEventHandler(CaptureStateChangedEventHandler eventHandler);
    void setCameraHolderEventHandler(CameraHolderEventHandler cameraHolderEventHandler);

    boolean isAeMeteringSupported();


    void setModuleChangedEventHandler(ModuleChangedEventHandler moduleChangedEventHandler);
}
