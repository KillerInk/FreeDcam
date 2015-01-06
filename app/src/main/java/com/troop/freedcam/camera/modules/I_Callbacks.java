package com.troop.freedcam.camera.modules;

/**
 * Created by troop on 06.01.2015.
 */
public class I_Callbacks
{
    @Deprecated
    public interface ShutterCallback
    {
        /**
         * Called as near as possible to the moment when a photo is captured
         * from the sensor.  This is a good opportunity to play a shutter sound
         * or give other feedback of camera operation.  This may be some time
         * after the photo was triggered, but some time before the actual data
         * is available.
         */
        void onShutter();
    }

    /**
     * Callback interface used to supply image data from a photo capture.
     *
     * @see
     *
     * @deprecated We recommend using the new {@link android.hardware.camera2} API for new
     *             applications.
     */
    @Deprecated
    public interface PictureCallback {
        /**
         * Called when image data is available after a picture is taken.
         * The format of the data depends on the context of the callback
         * and settings.
         *
         * @param data   a byte array of the picture data
         * @param
         */
        void onPictureTaken(byte[] data);
    };
}
