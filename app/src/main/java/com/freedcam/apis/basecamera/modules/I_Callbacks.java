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

package com.freedcam.apis.basecamera.modules;

/**
 * Created by troop on 06.01.2015.
 */

/**
 * this interface is used to wrap around the different manfactur specific libs like the
 * -lg framework
 * -samsung framework
 *
 * both use different callbacks wich are not extended from the basic camera
 * this way its possible to use one module for all different libs
 */
public class I_Callbacks
{
    public interface ShutterCallback
    {
        void onShutter();
    }

    public interface PictureCallback {

        void onPictureTaken(byte[] data);
    }


    public static int YUV = 1;
    public static int JPEG = 2;
    public interface PreviewCallback
    {

        void onPreviewFrame(byte[] data, int imageFormat);
    }

    public interface AutoFocusCallback
    {
        void onAutoFocus(CameraFocusEvent cameraFocusEvent);
        void onFocusLock(boolean locked);
    }


    public interface ErrorCallback
    {
        void onError(int error);
    }
}


