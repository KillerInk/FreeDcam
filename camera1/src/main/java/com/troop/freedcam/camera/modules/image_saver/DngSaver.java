package com.troop.freedcam.camera.modules.image_saver;

import android.os.Build;
import android.os.Handler;
import android.util.Log;

import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.imaging.jpeg.JpegProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataException;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.troop.androiddng.RawToDng;
import com.troop.freedcam.camera.BaseCameraHolder;
import com.troop.freedcam.camera.parameters.CamParametersHandler;
import com.troop.freedcam.utils.DeviceUtils;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;

/**
 * Created by troop on 15.04.2015.
 */
public class DngSaver extends JpegSaver
{
    final public String fileEnding = ".dng";
    private String lastBayerFormat;
    final RawToDng dngConverter;

    final String TAG = DngSaver.class.getSimpleName();
    public DngSaver(BaseCameraHolder cameraHolder, I_WorkeDone i_workeDone, Handler handler)
    {
        super(cameraHolder, i_workeDone, handler);
        dngConverter = RawToDng.GetInstance();
    }

    @Override
    public void TakePicture()
    {
        Log.d(TAG, "Start Take Picture");
        lastBayerFormat = cameraHolder.ParameterHandler.PictureFormat.GetValue();
        if (cameraHolder.ParameterHandler.ZSL != null && cameraHolder.ParameterHandler.ZSL.IsSupported() && cameraHolder.ParameterHandler.ZSL.GetValue().equals("on"))
        {
            iWorkeDone.OnError("Error: Disable ZSL for Raw or Dng capture");

            return;
        }
        super.TakePicture();
    }

    @Override
    public void onPictureTaken(final byte[] data)
    {
        Log.d(TAG, "Take Picture Callback");
        handler.post(new Runnable() {
            @Override
            public void run() {
                processData(data, new File(getStringAddTime() + fileEnding));
            }
        });

    }

    public void processData(byte[] data, File file)
    {
        try
        {
            Log.d(TAG, "Check if if rawStream");
            final Metadata metadata = JpegMetadataReader.readMetadata(new BufferedInputStream(new ByteArrayInputStream(data)));
            final Directory exifsub = metadata.getDirectory(ExifSubIFDDirectory.class);
            int iso = exifsub.getInt(ExifSubIFDDirectory.TAG_ISO_EQUIVALENT);
            if (iso > 0)
            {
                iWorkeDone.OnError("Error: Returned Stream is not a RawStream");
                //dngJpegShot =false;
                //dngcapture = false;
                Log.d(TAG, "Error no RAw stream!!!!");
                return;
            }
        }
        catch (Exception ex)
        {

        }

        Log.d(TAG, "Is raw stream");
        int w = 0;
        int h = 0;


        double Altitude = 0;
        double Latitude = 0;
        double Longitude = 0;
        String Provider = "ASCII";
        long gpsTime = 0;
        if (cameraHolder.gpsLocation != null)
        {
            Log.d(TAG, "Has GPS");
            Altitude = cameraHolder.gpsLocation.getAltitude();
            Latitude = cameraHolder.gpsLocation.getLatitude();
            Longitude = cameraHolder.gpsLocation.getLongitude();
            Provider = cameraHolder.gpsLocation.getProvider();
            gpsTime = cameraHolder.gpsLocation.getTime();
            dngConverter.SetGPSData(Altitude,Latitude,Longitude, Provider, gpsTime);
        }
        String raw[] = getRawSize();
        if (raw != null && raw.length == 2)
        {
            w = Integer.parseInt(raw[0]);
            h = Integer.parseInt(raw[1]);
            Log.d(TAG, "Found RawSize:" + w + "x" + h);
        }
        else
        {
            Log.d(TAG, "Cant find rawSize from Parameters, search HardCoded DEvices");
            RawToDng.SupportedDevices devices = RawToDng.SupportedDevices.GetValue(data.length);
            if (devices != null)
            {
                w = devices.width;
                h = devices.height;
                Log.d(TAG, "Found RawSize:" + w + "x" + h);
            }
            else
            {
                Log.d(TAG, "No HardCoded device found! Start Crashing....");
                throw new NullPointerException("ERROR NO RAWSIZE FOUND! " + Build.MANUFACTURER + " " + Build.DEVICE);
            }
        }
        dngConverter.SetBayerData(data, file.getAbsolutePath(), w, h);
        dngConverter.setExifData(0, 0, 0, 0, 0, "0", cameraHolder.Orientation + "", 0);
        dngConverter.WriteDNG();
        dngConverter.RELEASE();
        iWorkeDone.OnWorkDone(file);

    }

    protected String[] getRawSize()
    {
        String raw[] =null;
        if (DeviceUtils.isXperiaL())
        {
            raw = RawToDng.SonyXperiaLRawSize.split("x");
        }
        else
        {
            if (((CamParametersHandler)cameraHolder.ParameterHandler).GetRawSize() != "") {
                String rawSize = ((CamParametersHandler)cameraHolder.ParameterHandler).GetRawSize();
                if(rawSize != null && !rawSize.equals(""))
                    raw = rawSize.split("x");
            }
        }
        return raw;
    }

    private void addExifAndThumbToDng(byte[] data)
    {

        double x;
        double y;
        double calculatedExpo = 0;

        int iso =0,flash = 0;
        float fNumber =0, focalLength =0, exposureIndex = 0;
        try
        {
            final Metadata metadata = JpegMetadataReader.readMetadata(new BufferedInputStream(new ByteArrayInputStream(data)));
            final Directory exifsub = metadata.getDirectory(ExifSubIFDDirectory.class);
            try
            {
                iso = exifsub.getInt(ExifSubIFDDirectory.TAG_ISO_EQUIVALENT);
            } catch (MetadataException e) {
                e.printStackTrace();
            }
            try
            {
                flash = exifsub.getInt(ExifSubIFDDirectory.TAG_FLASH);
            } catch (MetadataException e) {
                e.printStackTrace();
            }
            try
            {
                fNumber = exifsub.getFloat(ExifSubIFDDirectory.TAG_FNUMBER);
            } catch (MetadataException e) {
                e.printStackTrace();
            }
            try
            {
                focalLength = exifsub.getFloat(ExifSubIFDDirectory.TAG_FOCAL_LENGTH);
            } catch (MetadataException e) {
                e.printStackTrace();
            }
            try
            {
                exposureIndex = exifsub.getFloat(ExifSubIFDDirectory.TAG_EXPOSURE_TIME);
            } catch (MetadataException e) {
                e.printStackTrace();
            }
        }
        catch (JpegProcessingException e)
        {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        String IMGDESC = "ISO:" + String.valueOf(iso) + " Exposure Time:" + exposureIndex + " F Number:" + String.valueOf(fNumber) + " Focal Length:" + focalLength;

        dngConverter.setExifData(iso, exposureIndex, flash, fNumber, focalLength,IMGDESC, cameraHolder.Orientation +"", 0);
    }
}
