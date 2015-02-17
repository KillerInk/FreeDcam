package com.troop.freedcam.camera.modules;

import android.hardware.Camera;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.imaging.jpeg.JpegProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataException;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.drew.metadata.exif.ExifSubIFDDirectory;
//import com.drew.metadata.exif.ExifDirectory;
import com.drew.metadata.exif.ExifReader;
import com.drew.metadata.exif.GpsDirectory;
import com.troop.androiddng.RawToDng;
import com.troop.freedcam.camera.BaseCameraHolder;
import com.troop.freedcam.camera.parameters.CamParametersHandler;
import com.troop.freedcam.i_camera.modules.AbstractModule;
import com.troop.freedcam.manager.MediaScannerManager;
import com.troop.freedcam.ui.AppSettingsManager;
import com.troop.freedcam.ui.menu.OrientationHandler;
import com.troop.freedcam.utils.DeviceUtils;
import com.troop.freedcam.utils.StringUtils;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static android.hardware.Camera.ShutterCallback;

/**
 * Created by troop on 15.08.2014.
 */
public class PictureModule extends AbstractModule implements I_Callbacks.PictureCallback {

    private static String TAG = StringUtils.TAG + PictureModule.class.getSimpleName();

    protected String rawFormats = "bayer-mipi-10gbrg,bayer-mipi-10grbg,bayer-mipi-10rggb,bayer-mipi-10bggr,raw,bayer-qcom-10gbrg,bayer-qcom-10grbg,bayer-qcom-10rggb,bayer-qcom-10bggr,bayer-ideal-qcom-10grbg,bayer-ideal-qcom-10bggr";
    protected String jpegFormat = "jpeg";
    protected String jpsFormat = "jps";

    protected String lastBayerFormat;
    private String lastPicSize;
    //META FROM JPEG
    private int iso;
    private String expo;
    private int flash;
    private float fNumber;
    private float focalLength;
    private String exposureIndex;
    private String gainControl;
    private double Altitude;
    double Longitude;
    double Latitude;
    long gpsTime;
    RawToDng dngConverter;

    //private HandlerThread backgroundThread;
    //Handler handler;
    ////////////
//defcomg 31-1-2015 Pull Orientation From Sesnor

    public String OverRidePath = "";
    CamParametersHandler parametersHandler;
    BaseCameraHolder baseCameraHolder;
    boolean dngJpegShot = false;


    File file;
    byte bytes[];
    byte Thumb[];

    public PictureModule(BaseCameraHolder baseCameraHolder, AppSettingsManager appSettingsManager, ModuleEventHandler eventHandler)
    {
        super(baseCameraHolder, appSettingsManager, eventHandler);
        this.baseCameraHolder = baseCameraHolder;
        name = ModuleHandler.MODULE_PICTURE;

        parametersHandler = (CamParametersHandler)ParameterHandler;
        this.baseCameraHolder = baseCameraHolder;
    }

    @Override
    public String ShortName() {
        return "Pic";
    }

    @Override
    public String LongName() {
        return "Picture";
    }

//I_Module START
    @Override
    public String ModuleName() {
        return name;
    }

    @Override
    public void DoWork()
    {
               dowork();
    }

    private void dowork() {
        Log.d(TAG, "PictureFormat: " + baseCameraHolder.ParameterHandler.PictureFormat.GetValue());
        if (!this.isWorking)
        {
            this.isWorking = true;
            lastBayerFormat = baseCameraHolder.ParameterHandler.PictureFormat.GetValue();
            if (baseCameraHolder.ParameterHandler.isDngActive && lastBayerFormat.contains("bayer"))
            {
                lastBayerFormat = baseCameraHolder.ParameterHandler.PictureFormat.GetValue();
                baseCameraHolder.ParameterHandler.PictureFormat.SetValue("jpeg", true);
                String sizes[] = baseCameraHolder.ParameterHandler.PictureSize.GetValues();
                lastPicSize = baseCameraHolder.ParameterHandler.PictureSize.GetValue();
                baseCameraHolder.ParameterHandler.PictureSize.SetValue(sizes[sizes.length-1], true);
                dngJpegShot = true;
            }
            takePicture();
        }
    }

    @Override
    public boolean IsWorking() {
        return isWorking;
    }
//I_Module END

    protected void takePicture()
    {
        workstarted();

        Log.d(TAG, "Start Taking Picture");
        try
        {

            baseCameraHolder.TakePicture(shutterCallback,rawCallback,this);
            Log.d(TAG, "Picture Taking is Started");

        }
        catch (Exception ex)
        {
            Log.d(TAG,"Take Picture Failed");
            ex.printStackTrace();
        }
    }


    I_Callbacks.ShutterCallback shutterCallback = new I_Callbacks.ShutterCallback() {
        @Override
        public void onShutter()
        {

        }
    };

    public I_Callbacks.PictureCallback rawCallback = new I_Callbacks.PictureCallback() {
        public void onPictureTaken(byte[] data)
        {
            if (data!= null)
            {
                Log.d(TAG, "RawCallback data size: " + data.length);
            }
            else
                Log.d(TAG, "RawCallback data size is null" );
        }
    };


    public void onPictureTaken(final byte[] data)
    {
        new Thread() {
            @Override
            public void run() {
                processImage(data);
                isWorking=false;
            }
        }.start();
    }

    private void processImage(byte[] data) {
        Log.d(TAG, "PictureCallback recieved! Data size: " + data.length);
        if (dngJpegShot && lastBayerFormat.contains("bayer"))
        {

            addExifAndThumbToDng(data);

            baseCameraHolder.ParameterHandler.PictureFormat.SetValue(lastBayerFormat, true);
            baseCameraHolder.ParameterHandler.PictureSize.SetValue(lastPicSize, true);
            dngJpegShot = false;
            baseCameraHolder.StartPreview();
            baseCameraHolder.TakePicture(shutterCallback,rawCallback,this);
        }
        else
        {

            if (processCallbackData(data))
            {

                baseCameraHolder.StartPreview();
                return;
            }


            baseCameraHolder.StartPreview();
            workfinished(true);
            Log.d(TAG, "work finished");

        }

    }

    private void addExifAndThumbToDng(byte[] data)
    {
        Thumb = data.clone();
        double x;
        double y;
        double calculatedExpo = 0;
        try
        {
            final Metadata metadata = JpegMetadataReader.readMetadata(new BufferedInputStream(new ByteArrayInputStream(data)));
            Directory exifsub = metadata.getDirectory(ExifSubIFDDirectory.class);


            try
            {
                iso = exifsub.getInt(ExifSubIFDDirectory.TAG_ISO_EQUIVALENT);
                expo = exifsub.getString(ExifSubIFDDirectory.TAG_SHUTTER_SPEED);
                flash = exifsub.getInt(ExifSubIFDDirectory.TAG_FLASH);// dir.getInt(ExifDirectory.TAG_FLASH);
                fNumber =exifsub.getFloat(ExifSubIFDDirectory.TAG_FNUMBER);// dir.getFloat(ExifDirectory.TAG_FNUMBER);
                focalLength =exifsub.getFloat(ExifSubIFDDirectory.TAG_FOCAL_LENGTH);// dir.getFloat(ExifDirectory.TAG_FOCAL_LENGTH);
                exposureIndex =exifsub.getString(ExifSubIFDDirectory.TAG_EXPOSURE_TIME);// dir.getString(ExifDirectory.TAG_EXPOSURE_TIME);
                //  gainControl = dir.getString(ExifDirectory.TAG_GAIN_CONTROL);
                Log.d(TAG, "iso:"+iso+" exposure"+expo+" flash:"+flash +"Shut"+exposureIndex);

                String[] expoRat =  exposureIndex.split("/");


                if(expoRat.length >= 2){
                    x = Double.parseDouble(expoRat[0]);
                    y = Double.parseDouble(expoRat[1]);
                    calculatedExpo = x/y;
                }
                else
                    calculatedExpo = Double.parseDouble(exposureIndex);

            } catch (MetadataException e) {
                e.printStackTrace();
            }
        } catch (JpegProcessingException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        String IMGDESC = "ISO:" + String.valueOf(iso) + " Exposure Time:" + exposureIndex + " F Number:" + String.valueOf(fNumber) + " Focal Length:" + focalLength;
        if (dngConverter != null)
            dngConverter.RELEASE();
        dngConverter = RawToDng.GetInstance(iso, calculatedExpo, flash, fNumber, focalLength,IMGDESC, "0", 0);
    }

    private void sendMsg(final String msg)
    {

                baseCameraHolder.errorHandler.OnError(msg);

    }

    protected boolean processCallbackData(byte[] data) {
        if(data.length < 4500)
        {

            sendMsg("Data size is < 4kb");

            //baseCameraHolder.StartPreview();
            return true;
        }
        else
        {
            sendMsg("Datasize : " + StringUtils.readableFileSize(data.length));
        }
        file = createFileName();
        bytes = data;
        saveFile();
        


        /*if (ParameterHandler.isExposureAndWBLocked)
            ParameterHandler.LockExposureAndWhiteBalance(false);*/
        return false;
    }

    private void saveFile() {
        if (OverRidePath == "")
        {
            if (!file.getAbsolutePath().endsWith(".dng")) {
                saveBytesToFile(bytes, file);
            } else
            {
                String raw[] = getRawSize();
                int w = Integer.parseInt(raw[0]);
                int h = Integer.parseInt(raw[1]);
                String l;
                if(lastBayerFormat != null)
                    l = lastBayerFormat.substring(lastBayerFormat.length() -4);
                else
                    l = parametersHandler.PictureFormat.GetValue().substring(parametersHandler.PictureFormat.GetValue().length() -4);

                Log.d(TAG, "Fnum" + String.valueOf(fNumber) + " FOcal" + focalLength);
                double Altitude = 0;
                double Latitude = 0;
                double Longitude = 0;
                String Provider = "ASCII";
                long gpsTime = 0;
                if (baseCameraHolder.gpsLocation != null)
                {
                    Altitude = baseCameraHolder.gpsLocation.getAltitude();
                    Latitude = baseCameraHolder.gpsLocation.getLatitude();
                    Longitude = baseCameraHolder.gpsLocation.getLongitude();
                    Provider = baseCameraHolder.gpsLocation.getProvider();
                    gpsTime = baseCameraHolder.gpsLocation.getTime();
                    dngConverter.SetGPSData(Altitude,Latitude,Longitude, Provider, gpsTime);
                }
                if (baseCameraHolder.ParameterHandler.PictureFormat.GetValue().equals("raw") || baseCameraHolder.ParameterHandler.PictureFormat.GetValue().contains("qcom"))
                {
                    RawToDng.SupportedDevices device = RawToDng.SupportedDevices.GetValue((int)bytes.length);
                    if (device == null)
                        dngConverter.SetBayerData(RawToDng.SixTeenBit(bytes, w, h), file.getAbsolutePath(),w,h);
                    else
                        dngConverter.SetBayerData(RawToDng.SixTeenBit(bytes, device.width, device.height), file.getAbsolutePath(),device.width, device.height);

                }
                    //new RawToDng().ConvertRawBytesToDng(RawToDng.SixTeenBit(bytes, w, h), file.getAbsolutePath(), w, h, Build.MODEL, iso, calculatedExpo, l, flash, fNumber, focalLength, IMGDESC, Thumb, "0", false, Altitude,Latitude, Longitude, Provider,gpsTime);
                else
                {
                    dngConverter.SetBayerData(bytes,file.getAbsolutePath(),w,h);
                    //new RawToDng().ConvertRawBytesToDng(bytes, file.getAbsolutePath(), w, h, Build.MODEL, iso, calculatedExpo, l, flash, fNumber, focalLength, IMGDESC, Thumb, "0", true, Altitude, Latitude, Longitude, Provider, gpsTime);
                }
                dngConverter.WriteDNG(h,l,bytes.length);
                //}
                Thumb = null;
            }
        }
        else
        {
            file = new File(OverRidePath);
            saveBytesToFile(bytes, file);
        }
        Log.d(TAG, "Start Media Scan " + file.getName());
        MediaScannerManager.ScanMedia(Settings.context.getApplicationContext() , file);
        eventHandler.WorkFinished(file);


    };

    protected String[] getRawSize()
    {
        String raw[];
        if (DeviceUtils.isXperiaL())
        {
            raw = RawToDng.SonyXperiaLRawSize.split("x");
        }
        else
        {
            String rawSize = parametersHandler.GetRawSize();
            raw = rawSize.split("x");
        }
        return raw;
    }

    protected void saveBytesToFile(byte[] bytes, File fileName)
    {
        Log.d(TAG, "Start Saving Bytes");
        FileOutputStream outStream = null;
        try {
            outStream = new FileOutputStream(fileName);
            outStream.write(bytes);
            outStream.flush();
            outStream.close();


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "End Saving Bytes");

    }


    private void setUpQuickJpeg()
    {
        ParameterHandler.PictureFormat.SetValue("jpeg", true);

        ParameterHandler.PictureSize.SetValue(ParameterHandler.PictureSize.GetValues()[ParameterHandler.PictureSize.GetValues().length-1],true);


    }

    protected File createFileName()
    {
        Log.d(TAG, "Create FileName");
        String s1 = getStringAddTime();
        return  getFileAndChooseEnding(s1);
    }

    protected String getStringAddTime()
    {
        File file = new File(Environment.getExternalStorageDirectory() + "/DCIM/FreeCam/");
        if (!file.exists())
            file.mkdirs();
        Date date = new Date();
        String s = (new SimpleDateFormat("yyyy.MM.dd_HH.mm.ss")).format(date);
        return (new StringBuilder(String.valueOf(file.getPath()))).append(File.separator).append("IMG_").append(s).toString();
    }

    protected File getFileAndChooseEnding(String s1)
    {
        String pictureFormat = ParameterHandler.PictureFormat.GetValue();
        if (rawFormats.contains(pictureFormat))
        {
            if (pictureFormat.contains("bayer") && parametersHandler.isDngActive)
                return new File(s1 +"_" + pictureFormat +".dng");
            else
                return new File(s1 + "_" + pictureFormat + ".raw");

        }
        else if (pictureFormat.contains("yuv"))
        {
            return new File(s1 + "_" + pictureFormat + ".yuv");
        }
        else
        {
            if (jpegFormat.contains(pictureFormat))
                return new File((new StringBuilder(String.valueOf(s1))).append(".jpg").toString());
            if (jpsFormat.contains(pictureFormat))
                return new File((new StringBuilder(String.valueOf(s1))).append(".jps").toString());
        }
        return null;
    }

    @Override
    public void LoadNeededParameters()
    {
        /*backgroundThread = new HandlerThread("PictureModuleThread");
        backgroundThread.start();
        handler = new Handler(backgroundThread.getLooper());*/
        if (ParameterHandler.AE_Bracket != null && ParameterHandler.AE_Bracket.IsSupported())
            ParameterHandler.AE_Bracket.SetValue("false", true);
        if (ParameterHandler.VideoHDR != null && ParameterHandler.VideoHDR.IsSupported() && ParameterHandler.VideoHDR.GetValue().equals("off"));
            ParameterHandler.VideoHDR.SetValue("off", true);
        //if (ParameterHandler.CameraMode.IsSupported() && ParameterHandler.CameraMode.GetValue().equals("1"))
            //ParameterHandler.CameraMode.SetValue("0", true);
        //if (ParameterHandler.ZSL.IsSupported() && !ParameterHandler.ZSL.GetValue().equals("off"))
            //ParameterHandler.ZSL.SetValue("off", true);
        //if(ParameterHandler.MemoryColorEnhancement.IsSupported() && ParameterHandler.MemoryColorEnhancement.GetValue().equals("enable"))
            //ParameterHandler.MemoryColorEnhancement.SetValue("disable",true);
        //if (ParameterHandler.DigitalImageStabilization.IsSupported() && ParameterHandler.DigitalImageStabilization.GetValue().equals("enable"))
            //ParameterHandler.DigitalImageStabilization.SetValue("disable", true);

    }

    @Override
    public void UnloadNeededParameters()
    {
        /*handler = null;
        try
        {
            backgroundThread.interrupt();
            backgroundThread.quit();
        }
        catch (Exception ex)
        {

        }
        finally {

            backgroundThread =null;
        }*/


    }
}
