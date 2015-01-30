package com.troop.freedcam.camera.modules;

import android.hardware.Camera;
import android.os.Build;
import android.os.Environment;
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
import com.troop.freedcam.i_camera.modules.AbstractModule;
import com.troop.freedcam.manager.MediaScannerManager;
import com.troop.freedcam.ui.AppSettingsManager;
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
    ///////////////////////////////////////

    public String OverRidePath = "";
    CamParametersHandler parametersHandler;
    BaseCameraHolder baseCameraHolder;
    boolean dngJpegShot = false;

    Handler handler;
    File file;
    byte bytes[];
    byte Thumb[];

    public PictureModule(BaseCameraHolder baseCameraHolder, AppSettingsManager appSettingsManager, ModuleEventHandler eventHandler)
    {
        super(baseCameraHolder, appSettingsManager, eventHandler);
        this.baseCameraHolder = baseCameraHolder;
        name = ModuleHandler.MODULE_PICTURE;
        handler = new Handler();
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
        Log.d(TAG, "PictureFormat: " + baseCameraHolder.ParameterHandler.PictureFormat.GetValue());
        if (!this.isWorking)
        {
            lastBayerFormat = baseCameraHolder.ParameterHandler.PictureFormat.GetValue();
            if (baseCameraHolder.ParameterHandler.isDngActive)
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
        return false;
    }
//I_Module END

    protected void takePicture()
    {
        workstarted();
        this.isWorking = true;
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


    public void onPictureTaken(byte[] data)
    {
        //DoMetaExtractCheck();
        //if(DeviceUtils.isDebugging())
          //  System.out.println("defcomg "+ "PictureForm "+ baseCameraHolder.ParameterHandler.PictureFormat.GetValue());

        Log.d(TAG, "PictureCallback recieved! Data size: " + data.length);
        if (dngJpegShot)
        {
            Thumb = data.clone();
            System.out.println("defcomg "+ "In This B1tcH");
            Metadata header;

            try
            {
                final Metadata metadata = JpegMetadataReader.readMetadata(new BufferedInputStream(new ByteArrayInputStream(data)));
                //Iterable<Directory> dirs = metadata.getDirectories();
                Directory exifsub = metadata.getDirectory(ExifSubIFDDirectory.class);
                //ByteArrayInputStream bais = new ByteArrayInputStream(data);
                //ExifReader reader = new ExifReader(bais);
                //header = reader.extract();
                //Directory dir = header.getDirectory(ExifDirectory.class);

                try
                {
                    iso = exifsub.getInt(ExifSubIFDDirectory.TAG_ISO_EQUIVALENT);
                    expo = exifsub.getString(ExifSubIFDDirectory.TAG_SHUTTER_SPEED);
                    flash = exifsub.getInt(ExifSubIFDDirectory.TAG_FLASH);// dir.getInt(ExifDirectory.TAG_FLASH);
                    fNumber =exifsub.getFloat(ExifSubIFDDirectory.TAG_FNUMBER);// dir.getFloat(ExifDirectory.TAG_FNUMBER);
                    focalLength =exifsub.getFloat(ExifSubIFDDirectory.TAG_FOCAL_LENGTH);// dir.getFloat(ExifDirectory.TAG_FOCAL_LENGTH);
                    exposureIndex =exifsub.getString(ExifSubIFDDirectory.TAG_EXPOSURE_TIME);// dir.getString(ExifDirectory.TAG_EXPOSURE_TIME);
                  //  gainControl = dir.getString(ExifDirectory.TAG_GAIN_CONTROL);



                } catch (MetadataException e) {
                    e.printStackTrace();
                }
            } catch (JpegProcessingException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

            baseCameraHolder.ParameterHandler.PictureFormat.SetValue(lastBayerFormat, true);
            baseCameraHolder.ParameterHandler.PictureSize.SetValue(lastPicSize, true);
            dngJpegShot = false;
            baseCameraHolder.StartPreview();
            baseCameraHolder.TakePicture(shutterCallback,rawCallback,this);
        }
        else
        {

            if (processCallbackData(data, saveFileRunner))
                return;

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            baseCameraHolder.StartPreview();
        }
    }

    protected boolean processCallbackData(byte[] data, Runnable saveFileRunner) {
        if(data.length < 4500)
        {
            baseCameraHolder.errorHandler.OnError("Data size is < 4kb");
            isWorking = false;
            //baseCameraHolder.StartPreview();
            return true;
        }
        else
        {
            baseCameraHolder.errorHandler.OnError("Datasize : " + StringUtils.readableFileSize(data.length));
        }
        file = createFileName();
        bytes = data;
        saveFileRunner.run();
        isWorking = false;

        /*if (ParameterHandler.isExposureAndWBLocked)
            ParameterHandler.LockExposureAndWhiteBalance(false);*/
        return false;
    }

    protected Runnable saveFileRunner = new Runnable() {
        @Override
        public void run()
        {
            if (OverRidePath == "")
            {
                if (!file.getAbsolutePath().endsWith(".dng")) {
                    saveBytesToFile(bytes, file);
                }
                else
                {
                    String raw[] = getRawSize();
                    int w = Integer.parseInt(raw[0]);
                    int h = Integer.parseInt(raw[1]);
                    String l;
                    if(lastBayerFormat != null)
                        l = lastBayerFormat.substring(lastBayerFormat.length() -4);
                    else
                        l = parametersHandler.PictureFormat.GetValue().substring(parametersHandler.PictureFormat.GetValue().length() -4);

                    //if(DeviceUtils.isDebugging())
                        //System.out.println("iso:"+iso+" exposure"+expo+" flash:"+flash +"Shut"+exposureIndex );

                    Log.d(TAG, "iso:"+iso+" exposure"+expo+" flash:"+flash +"Shut"+exposureIndex);

                    double x =0,y = 0, calculatedExpo =0 ;
                    if (exposureIndex != null && exposureIndex.contains("/")) {
                        String[] expoRat = exposureIndex.split("/");
                        x = Double.parseDouble(expoRat[0]);
                        y = Double.parseDouble(expoRat[1]);
                        calculatedExpo = x/y;
                    }

                    //float calculatedExpoF = x/y;

                    Log.d(TAG, "Fnum"+String.valueOf(fNumber)+" FOcal"+focalLength);
                    String IMGDESC = "ISO:"+String.valueOf(iso)+" Exposure Time:"+exposureIndex+" F Number:"+String.valueOf(fNumber)+" Focal Length:"+focalLength;

                    RawToDng.ConvertRawBytesToDng(bytes, file.getAbsolutePath(), w, h, Build.MODEL, iso, calculatedExpo, l,flash,fNumber,focalLength,IMGDESC,Thumb);
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
            workfinished(true);
        }
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

    private void DoMetaExtractCheck()
    {
        System.out.println("defcomg "+ "Smallest Size "+ ParameterHandler.PictureSize.GetValues()[ParameterHandler.PictureSize.GetValues().length-1]);

        if (baseCameraHolder.ParameterHandler.PictureFormat.GetValue().equals("bayer-mipi-10bggr") && ParameterHandler.isDngActive == true)
            dngJpegShot = true;
        else
            dngJpegShot = false;
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
            if (pictureFormat.contains("bayer-mipi") && parametersHandler.isDngActive)
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
    public void UnloadNeededParameters() {
        super.UnloadNeededParameters();
    }
}
