package com.troop.freedcam.camera.modules.image_saver;

import android.os.Build;
import android.os.ParcelFileDescriptor;
import android.support.v4.provider.DocumentFile;
import android.util.Log;

import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataException;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.troop.androiddng.RawToDng;
import com.troop.filelogger.Logger;
import com.troop.freedcam.camera.BaseCameraHolder;
import com.troop.freedcam.ui.AppSettingsManager;
import com.troop.freedcam.ui.FreeDPool;
import com.troop.freedcam.utils.DeviceUtils;
import com.troop.freedcam.utils.FileUtils;
import com.troop.freedcam.utils.StringUtils;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by troop on 15.04.2015.
 */
public class DngSaver extends JpegSaver
{
    final public String fileEnding = ".dng";
    private final RawToDng dngConverter;
   // boolean isDebug = true;
  //  MetaDataExtractor meta;

    private final String TAG = DngSaver.class.getSimpleName();
    public DngSaver(BaseCameraHolder cameraHolder, I_WorkeDone i_workeDone)
    {
        super(cameraHolder, i_workeDone);
        dngConverter = RawToDng.GetInstance();
       // meta = new MetaDataExtractor();

    }

    @Override
    public void TakePicture()
    {
        Logger.d(TAG, "Start Take Picture");
        if (ParameterHandler.ZSL != null && ParameterHandler.ZSL.IsSupported() && ParameterHandler.ZSL.GetValue().equals("on"))
        {
            ParameterHandler.ZSL.SetValue("off", true);
        }
        awaitpicture = true;
        if((DeviceUtils.IS_DEVICE_ONEOF(DeviceUtils.MI3_4) && Build.VERSION.SDK_INT == Build.VERSION_CODES.M)|| DeviceUtils.IS_DEVICE_ONEOF(DeviceUtils.ZTE_DEVICES)) {

          //  MetaDataExtractor.StatiClear();
           // MetaDataExtractor.StatiCEXCute();
           // meta.extractMeta();
        }

        FreeDPool.Execute(new Runnable() {
            @Override
            public void run() {
                cameraHolder.TakePicture(null, DngSaver.this);
            }
        });
    }

    @Override
    public void onPictureTaken(final byte[] data)
    {
        if (!awaitpicture)
            return;
        awaitpicture =false;
        Logger.d(TAG, "Take Picture Callback");
        FreeDPool.Execute(new Runnable() {
            @Override
            public void run() {
                File f = new File(StringUtils.getFilePath(AppSettingsManager.APPSETTINGSMANAGER.GetWriteExternal(), fileEnding));
                processData(data, f, true);
            }
        });

    }

    public void processData(byte[] data, File file, boolean throwOnWorkDone)
    {
        try
        {
            if (data.length < 4500)
            {
                cameraHolder.errorHandler.OnError("Data size is < 4kb");
                return;
            }
            Logger.d(TAG, "Check if if rawStream");
            final Metadata metadata = JpegMetadataReader.readMetadata(new BufferedInputStream(new ByteArrayInputStream(data)));
            final Directory exifsub = metadata.getDirectory(ExifSubIFDDirectory.class);
            int iso = exifsub.getInt(ExifSubIFDDirectory.TAG_ISO_EQUIVALENT);
            if (iso > 0)
            {
                iWorkeDone.OnError("Error: Returned Stream is not a RawStream");
                //dngJpegShot =false;
                //dngcapture = false;
                Logger.d(TAG, "Error no RAw stream!!!!");
                return;
            }
        }
        catch (Exception ex)
        {

        }

        Logger.d(TAG, "Is raw stream");
        int w = 0;
        int h = 0;


        double Altitude = 0;
        double Latitude = 0;
        double Longitude = 0;
        String Provider = "ASCII";
        long gpsTime = 0;
        if (cameraHolder.gpsLocation != null)
        {
            Logger.d(TAG, "Has GPS");
            Altitude = cameraHolder.gpsLocation.getAltitude();
            Latitude = cameraHolder.gpsLocation.getLatitude();
            Longitude = cameraHolder.gpsLocation.getLongitude();
            Provider = cameraHolder.gpsLocation.getProvider();
            gpsTime = cameraHolder.gpsLocation.getTime();
            dngConverter.SetGPSData(Altitude, Latitude, Longitude, Provider, gpsTime);
        }


        float fnum, focal = 0;
        if(DeviceUtils.IS_DEVICE_ONEOF(DeviceUtils.ZTE_DEVICES))
        {
            fnum = 2.0f;
            focal = 28.342f;
        }
        else {
        fnum = (ParameterHandler).GetFnumber();
        focal = (ParameterHandler).GetFocal();}
        //if(meta != null){
         //   dngConverter.setExifData(meta.getIso(), meta.getExp(), meta.getFlash(), fnum, focal, meta.getDescription(), cameraHolder.Orientation + "", 0);}
      //  else

        try
        {
            if (DeviceUtils.IS(DeviceUtils.Devices.ZTE_ADV) ||ParameterHandler.isMTK()) {
                dngConverter.setExifData(ExtractISO(), ExtractShutter(), 0, fnum, focal, "0", cameraHolder.Orientation + "", 0);
            }
            else
                dngConverter.setExifData(0, 0, 0, fnum, focal, "0", cameraHolder.Orientation + "", 0);
        }
        catch (Exception ex)
        {
            dngConverter.setExifData(0, 0, 0, fnum, focal, "0", cameraHolder.Orientation + "", 0);
        }

        if (ParameterHandler.CCT != null && ParameterHandler.CCT.IsSupported())
        {
            String wb = ParameterHandler.CCT.GetStringValue();
            if (!wb.equals("Auto"))
            {
                //int ct = Integer.parseInt(wb);
                dngConverter.SetWBCT(wb);
            }
        }

        Log.d("Raw File Size ", data.length + "");
        if (!StringUtils.IS_L_OR_BIG()
                || StringUtils.WRITE_NOT_EX_AND_L_ORBigger()) {
            checkFileExists(file);
            dngConverter.SetBayerData(data, file.getAbsolutePath());
            dngConverter.WriteDNG(DeviceUtils.DEVICE());
            dngConverter.RELEASE();
        }
        else {

            DocumentFile df = FileUtils.getFreeDcamDocumentFolder(AppSettingsManager.APPSETTINGSMANAGER);
            Logger.d(TAG,"Filepath: " +df.getUri().toString());
            DocumentFile wr = df.createFile("image/dng", file.getName().replace(".jpg", ".dng"));
            Logger.d(TAG,"Filepath: " +wr.getUri().toString());
            ParcelFileDescriptor pfd = null;
            try {

                pfd = AppSettingsManager.APPSETTINGSMANAGER.context.getContentResolver().openFileDescriptor(wr.getUri(), "rw");
            } catch (FileNotFoundException | IllegalArgumentException e) {
                Logger.exception(e);
            }
            if (pfd != null) {
                dngConverter.SetBayerDataFD(data, pfd, file.getName());
                dngConverter.WriteDNG(DeviceUtils.DEVICE());
                dngConverter.RELEASE();
                try {
                    pfd.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                pfd = null;
            }
        }
        if(throwOnWorkDone)
            iWorkeDone.OnWorkDone(file);

    }

    private int ExtractISO()

    {
        if(ParameterHandler.isMTK())
        {
            return ParameterHandler.getMTKISO();
        }
        else {
            if (ParameterHandler.IsoMode.GetValue().equals("auto") || ParameterHandler.IsoMode.GetValue().equals("auto_hjr")) {
                return 0;
            } else {
                return Integer.parseInt(ParameterHandler.IsoMode.GetValue().split("O")[1]);
            }
        }

    }

    private float ExtractShutter()

    {
        if(ParameterHandler.isMTK())
        {
            return ParameterHandler.getMTKShutterSpeed();
        }
        else {
            if (ParameterHandler.ManualShutter.GetStringValue().equals("auto")) {
                return 0f;
            } else {
                if (ParameterHandler.ManualShutter.GetStringValue().contains("/")) {
                    return 1 / Float.parseFloat(ParameterHandler.ManualShutter.GetStringValue().split("/")[1]);

                } else
                {
                    return Float.parseFloat(ParameterHandler.ManualShutter.GetStringValue());

                }
            }
        }

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
                Logger.exception(e);
            }
            try
            {
                flash = exifsub.getInt(ExifSubIFDDirectory.TAG_FLASH);
            } catch (MetadataException e) {
                Logger.exception(e);
            }
            try
            {
                fNumber = exifsub.getFloat(ExifSubIFDDirectory.TAG_FNUMBER);
            } catch (MetadataException e) {
                Logger.exception(e);
            }
            try
            {
                focalLength = exifsub.getFloat(ExifSubIFDDirectory.TAG_FOCAL_LENGTH);
            } catch (MetadataException e) {
                Logger.exception(e);
            }
            try
            {
                exposureIndex = exifsub.getFloat(ExifSubIFDDirectory.TAG_EXPOSURE_TIME);
            } catch (MetadataException e) {
                Logger.exception(e);
            }
        } catch (Exception e) {
            Logger.exception(e);
        }
        String IMGDESC = "ISO:" + String.valueOf(iso) + " Exposure Time:" + exposureIndex + " F Number:" + String.valueOf(fNumber) + " Focal Length:" + focalLength;

        dngConverter.setExifData(iso, exposureIndex, flash, fNumber, focalLength,IMGDESC, cameraHolder.Orientation +"", 0);
    }
}
