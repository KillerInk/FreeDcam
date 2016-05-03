package com.troop.freedcam.i_camera.modules;

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


