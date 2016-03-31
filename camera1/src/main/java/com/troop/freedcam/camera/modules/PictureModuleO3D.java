package com.troop.freedcam.camera.modules;

import android.os.Handler;

import com.troop.freedcam.camera.BaseCameraHolder;
import com.troop.freedcam.i_camera.modules.ModuleEventHandler;
import com.troop.freedcam.ui.AppSettingsManager;
import com.troop.freedcam.utils.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;



/**
 * Created by troop on 24.11.2014.
 */
public class PictureModuleO3D extends PictureModule
{
    private static String TAG = StringUtils.TAG+PictureModuleO3D.class.getSimpleName();
    public PictureModuleO3D(BaseCameraHolder baseCameraHolder, ModuleEventHandler eventHandler)
    {
        super(baseCameraHolder, eventHandler);
    }


    protected String[] getRawSize()
    {
        return null;// RawToDng.Optimus3DRawSize.split("x");
    }

    /*@Override
    protected void takePicture()
    {
        File dataDirectory = new File(Environment.getExternalStorageDirectory() + "/DCIM/FreeCam/Bayer_Raw/");
        if (!dataDirectory.exists())
            dataDirectory.mkdirs();
        super.takePicture();
    }*/

    /*@Override
    public void onPictureTaken(byte[] data) {
        Logger.d(TAG, "PictureCallback recieved! Data size: " + data.length);
        if (Settings.getString(AppSettingsManager.SETTING_PICTUREFORMAT).equals(("dng"))
                || Settings.getString(AppSettingsManager.SETTING_PICTUREFORMAT).equals("raw"))
        {
            File dataDirectory = new File(Environment.getExternalStorageDirectory() + "/DCIM/FreeCam/Bayer_Raw/");
            File folders[] = dataDirectory.listFiles();
            for (int i = 0; i < folders.length; i++)
            {
                try {
                    data = readBytesFromFile(folders[i]);
                    if (processCallbackData(data)) return;
                } catch (IOException e) {
                    Logger.exception(e);
                }
            }
            for (int i = 0; i < folders.length; i++)
            {
                boolean deleted =  folders[i].delete();
                if(!deleted);
                    Logger.d(TAG, "file delted fail");

            }

            baseCameraHolder.StartPreview();

        }
        else {
            if (processCallbackData(data)) return;
            baseCameraHolder.StartPreview();
        }
    }*/

    public static byte[] readBytesFromFile(File file) throws IOException {
        InputStream is = new FileInputStream(file);

        // Get the size of the file
        long length = file.length();

        // You cannot create an array using a long type.
        // It needs to be an int type.
        // Before converting to an int type, check
        // to ensure that file is not larger than Integer.MAX_VALUE.
        if (length > Integer.MAX_VALUE) {
            throw new IOException("Could not completely read file " + file.getName() + " as it is too long (" + length + " bytes, max supported " + Integer.MAX_VALUE + ")");
        }

        // Create the byte array to hold the data
        byte[] bytes = new byte[(int)length];

        // Read in the bytes
        int offset = 0;
        int numRead = 0;
        while (offset < bytes.length && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
            offset += numRead;
        }

        // Ensure all the bytes have been read in
        if (offset < bytes.length) {
            throw new IOException("Could not completely read file " + file.getName());
        }

        // Close the input stream and return bytes
        is.close();
        return bytes;
    }


    /*@Override
    protected File getFileAndChooseEnding(String s1)
    {
        String zsl = ParameterHandler.ZSL.GetValue();
        if (zsl != null && zsl.equals("high-quality"))
            return new File((new StringBuilder(String.valueOf(s1))).append("_").append(".raw").toString());
        else
            return new File((new StringBuilder(String.valueOf(s1))).append(".jpg").toString());
    }*/
}
