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
import android.os.AsyncTask;
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
import freed.cam.ui.handler.MediaScannerManager;
import freed.dng.DngProfile;
import freed.jni.RawToDng;
import freed.viewer.holder.FileHolder;

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

    public void scanFile(File file)
    {
        MediaScannerManager.ScanMedia(activityInterface.getContext(),file);
        activityInterface.WorkHasFinished(new FileHolder(file, activityInterface.getAppSettings().GetWriteExternal()));
    }

    public void SaveDngWithRawToDng(File fileName, byte[] bytes, float fnumber, float focal, float exposuretime, int iso, int orientation, String wb, DngProfile dngProfile)
    {
        final  DngSaver dngSaver = new DngSaver(fileName,bytes,fnumber,focal,exposuretime,iso,orientation,wb,dngProfile);
        AsyncTask.THREAD_POOL_EXECUTOR.execute(dngSaver);
    }

    private class DngSaver implements Runnable
    {
        final File fileName;
        final byte[] bytes;
        final float fnumber;
        final float focal;
        final float exposuretime;
        final int iso;
        final int orientation;
        final String wb;
        final DngProfile dngProfile;
        public DngSaver(File fileName, byte[] bytes, float fnumber, float focal, float exposuretime, int iso, int orientation, String wb, DngProfile dngProfile)
        {
            this.fileName = fileName;
            this.bytes =bytes;
            this.fnumber = fnumber;
            this.focal = focal;
            this.exposuretime = exposuretime;
            this.iso = iso;
            this.orientation = orientation;
            this.wb = wb;
            this.dngProfile = dngProfile;
        }
        /**
         * Starts executing the active part of the class' code. This method is
         * called when a thread is started that has been created with a class which
         * implements {@code Runnable}.
         */
        @Override
        public void run() {
            RawToDng dngConverter = RawToDng.GetInstance();
            Logger.d(TAG,"saveDng");
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
                    Logger.d(TAG, "location:" + location.toString());
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
                Logger.d(TAG, "Write To internal or kitkat<");
                checkFileExists(fileName);
                dngConverter.setBayerData(bytes, fileName.getAbsolutePath());
                dngConverter.WriteDngWithProfile(dngProfile);
            }
            else
            {
                DocumentFile df = activityInterface.getFreeDcamDocumentFolder();
                Logger.d(TAG,"Filepath: " + df.getUri());
                DocumentFile wr = df.createFile("image/dng", fileName.getName().replace(".jpg", ".dng"));
                Logger.d(TAG,"Filepath: " + wr.getUri());
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
                    try {
                        pfd.close();
                    } catch (IOException e) {
                        Logger.exception(e);
                    }
                    pfd = null;
                }
            }
            scanFile(fileName);
        }
    }

    public void SaveBitmapToFile(Bitmap bitmap, File file)
    {
        final BitmapSaver bitmapSaver = new BitmapSaver(file,bitmap);
        AsyncTask.THREAD_POOL_EXECUTOR.execute(bitmapSaver);
    }

    private class BitmapSaver implements Runnable
    {
        private final  File file;
        private final Bitmap bitmap;
        public BitmapSaver(File file, Bitmap bitmap)
        {
            this.file = file;
            this.bitmap = bitmap;
        }
        /**
         * Starts executing the active part of the class' code. This method is
         * called when a thread is started that has been created with a class which
         * implements {@code Runnable}.
         */
        @Override
        public void run() {
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
            scanFile(file);
        }
    }

    public void SaveJpegByteArray(File file, byte[]bytes)
    {
        final JpegSaver jpegSaver = new JpegSaver(file, bytes);
        AsyncTask.THREAD_POOL_EXECUTOR.execute(jpegSaver);
    }

    private class JpegSaver implements Runnable {

        private final  File file;
        private final byte[] bytes;
        public JpegSaver(File file, byte[] bytes)
        {
            this.file = file;
            this.bytes = bytes;
        }
        /**
         * Starts executing the active part of the class' code. This method is
         * called when a thread is started that has been created with a class which
         * implements {@code Runnable}.
         */
        @Override
        public void run() {
            Logger.d(TAG, "Start Saving Bytes");
            OutputStream outStream = null;
            try {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP || Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP&& !activityInterface.getAppSettings().GetWriteExternal())
                {
                    checkFileExists(file);
                    outStream = new FileOutputStream(file);
                }
                else
                {
                    DocumentFile df = activityInterface.getFreeDcamDocumentFolder();
                    Logger.d(TAG,"Filepath: " + df.getUri());
                    DocumentFile wr = df.createFile("image/*", file.getName());
                    Logger.d(TAG,"Filepath: " + wr.getUri());
                    outStream = activityInterface.getContext().getContentResolver().openOutputStream(wr.getUri());
                }
                outStream.write(bytes);
                outStream.flush();
                outStream.close();


            } catch (IOException e) {
                Logger.exception(e);
            }
            scanFile(file);
            Logger.d(TAG, "End Saving Bytes");
        }
    }
}
