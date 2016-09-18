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

package freed.utils;

import android.graphics.Bitmap;
import android.location.Location;
import android.os.Build;
import android.os.ParcelFileDescriptor;
import android.support.v4.provider.DocumentFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import freed.ActivityInterface;
import freed.cam.apis.KEYS;
import freed.dng.DngProfile;
import freed.jni.RawToDng;

/**
 * Created by troop on 18.09.2016.
 */
public class ImageSaver
{
    private final String TAG = ImageSaver.class.getSimpleName();
    private ActivityInterface activityInterface;

    public ImageSaver(ActivityInterface activityInterface)
    {
        this.activityInterface = activityInterface;
    }


    public synchronized void SaveJpegByteArray(File fileName, byte[] bytes)
    {
        Logger.d(TAG, "Start Saving Bytes");
        OutputStream outStream = null;
        try {
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP || Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP&& !activityInterface.getAppSettings().GetWriteExternal())
            {
                checkFileExists(fileName);
                outStream = new FileOutputStream(fileName);
            }
            else
            {
                DocumentFile df = activityInterface.getFreeDcamDocumentFolder();
                Logger.d(TAG,"Filepath: " + df.getUri());
                DocumentFile wr = df.createFile("image/*", fileName.getName());
                Logger.d(TAG,"Filepath: " + wr.getUri());
                outStream = activityInterface.getContext().getContentResolver().openOutputStream(wr.getUri());
            }
            outStream.write(bytes);
            outStream.flush();
            outStream.close();


        } catch (IOException e) {
            Logger.exception(e);
        }
        Logger.d(TAG, "End Saving Bytes");
    }

    private void checkFileExists(File fileName)
    {
        if (fileName.getParentFile() == null)
            return;
        if(!fileName.getParentFile().exists())
            fileName.getParentFile().mkdirs();
        if (!fileName.exists())
            try {
                fileName.createNewFile();
            } catch (IOException e) {
                Logger.exception(e);
            }
    }

    public synchronized void SaveDngWithRawToDng(File fileName, byte[] bytes, float fnumber, float focal, float exposuretime, int iso, int orientation, String wb, DngProfile dngProfile)
    {
        RawToDng dngConverter = RawToDng.GetInstance();
        Logger.d(this.TAG,"saveDng");
        double Altitude = 0;
        double Latitude = 0;
        double Longitude = 0;
        String Provider = "ASCII";
        long gpsTime = 0;
        if (activityInterface.getAppSettings().getString(AppSettingsManager.SETTING_LOCATION).equals(KEYS.ON))
        {
            if (activityInterface.getLocationHandler().getCurrentLocation() != null)
            {
                Location location = activityInterface.getLocationHandler().getCurrentLocation();
                Logger.d(this.TAG, "location:" + location.toString());
                Altitude = location.getAltitude();
                Latitude = location.getLatitude();
                Longitude = location.getLongitude();
                Provider = location.getProvider();
                gpsTime = location.getTime();
                dngConverter.SetGPSData(Altitude, Latitude, Longitude, Provider, gpsTime);
            }
        }
        dngConverter.setExifData(iso, exposuretime, 0, fnumber, focal, "0", orientation + "", 0);
        if (wb != null)
            dngConverter.SetWBCT(wb);
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP || Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && !activityInterface.getAppSettings().GetWriteExternal())
        {
            Logger.d(this.TAG, "Write To internal or kitkat<");
            checkFileExists(fileName);
            dngConverter.SetBayerData(bytes, fileName.getAbsolutePath());
            dngConverter.WriteDngWithProfile(dngProfile);
            dngConverter.RELEASE();
        }
        else
        {
            DocumentFile df = activityInterface.getFreeDcamDocumentFolder();
            Logger.d(this.TAG,"Filepath: " + df.getUri());
            DocumentFile wr = df.createFile("image/dng", fileName.getName().replace(".jpg", ".dng"));
            Logger.d(this.TAG,"Filepath: " + wr.getUri());
            ParcelFileDescriptor pfd = null;
            try {
                pfd = activityInterface.getContext().getContentResolver().openFileDescriptor(wr.getUri(), "rw");
            } catch (FileNotFoundException | IllegalArgumentException e) {
                Logger.exception(e);
            }
            if (pfd != null)
            {
                dngConverter.SetBayerDataFD(bytes, pfd, fileName.getName());
                dngConverter.WriteDngWithProfile(dngProfile);
                dngConverter.RELEASE();
                try {
                    pfd.close();
                } catch (IOException e) {
                    Logger.exception(e);
                }
                pfd = null;
            }
        }
    }

    public synchronized void SaveBitmapToFile(Bitmap bitmap, File file)
    {
        OutputStream outStream = null;
        boolean writetoExternalSD = activityInterface.getAppSettings().GetWriteExternal();
        Logger.d(TAG, "Write External " + writetoExternalSD);
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP || !writetoExternalSD && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            try {
                outStream= new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
                outStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else
        {
            DocumentFile df =  activityInterface.getFreeDcamDocumentFolder();
            Logger.d(TAG,"Filepath: " + df.getUri());
            DocumentFile wr = df.createFile("image/*", file.getName());
            Logger.d(TAG,"Filepath: " + wr.getUri());
            try {
                outStream = activityInterface.getContext().getContentResolver().openOutputStream(wr.getUri());
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
                outStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
