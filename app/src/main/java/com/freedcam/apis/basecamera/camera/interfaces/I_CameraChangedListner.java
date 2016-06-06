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

package com.freedcam.apis.basecamera.camera.interfaces;

/**
 * Created by troop on 17.12.2014.
 */
public interface I_CameraChangedListner
{
    /**
     * gets thrown when camera starts open
     * @param message
     */
    void onCameraOpen(String message);
    /**
     * gets thrown when camera open has finish
     * @param message
     */
    void onCameraOpenFinish(String message);
    /**
     * gets thrown when camera is closed
     * @param message
     */
    void onCameraClose(String message);
    /**
     * gets thrown when preview is running
     * @param message
     */
    void onPreviewOpen(String message);
    /**
     * gets thrown when preview gets closed
     * @param message
     */
    void onPreviewClose(String message);
    /**
     * gets thrown when camera has a problem
     * @param error to send
     */
    void onCameraError(String error);
    /**
     * gets thrown when camera status changed
     * @param status that has changed
     */
    void onCameraStatusChanged(String status);
    /**
     * gets thrown when current module has changed
     * @param module
     */
    void onModuleChanged(I_Module module);
}
