package com.troop.freedcam.camera.modules;

/**
 * Created by troop on 06.01.2015.
 */
public class I_Callbacks
{
    public interface ShutterCallback
    {
        void onShutter();
    }

    public interface PictureCallback {

        void onPictureTaken(byte[] data);
    };

    public interface PreviewCallback
    {

        void onPreviewFrame(byte[] data);
    };
}
