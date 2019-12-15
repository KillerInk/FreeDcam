package freed.cam.apis.camera2.modules.helper;

import android.annotation.TargetApi;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.ColorSpaceTransform;
import android.hardware.camera2.params.LensShadingMap;
import android.location.Location;
import android.media.Image;
import android.media.ImageReader;
import android.os.Build;
import android.util.Pair;
import android.util.Rational;

import androidx.annotation.NonNull;

import com.troop.freedcam.R;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import freed.ActivityInterface;
import freed.cam.apis.basecamera.modules.ModuleInterface;
import freed.cam.apis.basecamera.modules.WorkFinishEvents;
import freed.dng.CustomMatrix;
import freed.dng.DngProfile;
import freed.dng.ToneMapProfile;
import freed.dng.opcode.OpCodeCreator;
import freed.image.ImageManager;
import freed.image.ImageSaveTask;
import freed.image.ImageTask;
import freed.image.ImageTaskDngConverter;
import freed.jni.OpCode;
import freed.settings.SettingKeys;
import freed.settings.SettingsManager;
import freed.utils.Log;

/**
 * Created by troop on 12.06.2017.
 */


@TargetApi(Build.VERSION_CODES.LOLLIPOP)

public class ImageCaptureHolder extends CameraCaptureSession.CaptureCallback implements ImageReader.OnImageAvailableListener {
    public interface RdyToSaveImg
    {
        void onRdyToSaveImg(ImageCaptureHolder holder);
    }

    private final String TAG = ImageCaptureHolder.class.getSimpleName();
    private CaptureResult captureResult;
    private List<Image> images;
    private CameraCharacteristics characteristics;
    private CustomMatrix customMatrix;
    private ToneMapProfile toneMapProfile;
    private int orientation = 0;
    private Location location;
    private boolean externalSD =false;

    public String getFilepath() {
        return filepath;
    }

    private String filepath;

    private boolean forceRawToDng = false;
    private boolean support12bitRaw = false;
    private CaptureType captureType;

    private ActivityInterface activityInterface;
    private RdyToSaveImg rdyToSaveImg;
    private ModuleInterface moduleInterface;
    private int cropWidth, cropHeight;

    WorkFinishEvents workerfinish;

    private final boolean LOG_CAPTURE_RESULT = false;

    public ImageCaptureHolder(CameraCharacteristics characteristicss, CaptureType captureType, ActivityInterface activitiy, ModuleInterface imageSaver, WorkFinishEvents finish, RdyToSaveImg rdyToSaveImg)
    {
        images = new ArrayList<>();
        this.characteristics = characteristicss;
        this.captureType = captureType;
        this.activityInterface = activitiy;
        this.moduleInterface = imageSaver;
        this.workerfinish = finish;
        this.rdyToSaveImg =rdyToSaveImg;
        customMatrix = null;
    }


    public void setCustomMatrix(CustomMatrix custmMat)
    {
        this.customMatrix = custmMat;
    }

    public void setToneMapProfile(ToneMapProfile toneMapProfile)
    {
        this.toneMapProfile = toneMapProfile;
    }

    public void setOrientation(int or)
    {
        this.orientation = or;
    }

    public void setFilePath(String path, boolean extSD)
    {
        this.filepath = path;
        this.externalSD = extSD;
    }

    public void setLocation(Location location)
    {
        this.location = location;
    }

    public void setForceRawToDng(boolean force)
    {
        this.forceRawToDng = force;
    }

    public void setSupport12bitRaw(boolean support12bitRaw)
    {
        this.support12bitRaw =support12bitRaw;
    }

    public void setCropSize(int cropWidth,int cropHeight)
    {
        this.cropHeight = cropHeight;
        this.cropWidth = cropWidth;
    }

    public synchronized void SetCaptureResult(CaptureResult captureResult)
    {
        this.captureResult = captureResult;

        if (LOG_CAPTURE_RESULT) {
            try {
                Log.d(TAG, "ColorMatrix1:" + characteristics.get(CameraCharacteristics.SENSOR_COLOR_TRANSFORM1).toString());
                Log.d(TAG, "ColorMatrix2:" + characteristics.get(CameraCharacteristics.SENSOR_COLOR_TRANSFORM2).toString());
                logNeutralMatrix();
                Log.d(TAG, "Transform1:" + characteristics.get(CameraCharacteristics.SENSOR_COLOR_TRANSFORM1).toString());
                Log.d(TAG, "Transform2:" + characteristics.get(CameraCharacteristics.SENSOR_COLOR_TRANSFORM2).toString());
                Log.d(TAG, "Foward1:" + characteristics.get(CameraCharacteristics.SENSOR_FORWARD_MATRIX1).toString());
                Log.d(TAG, "Foward2:" + characteristics.get(CameraCharacteristics.SENSOR_FORWARD_MATRIX2).toString());
                Log.d(TAG, "Reduction1:" + characteristics.get(CameraCharacteristics.SENSOR_CALIBRATION_TRANSFORM1).toString());
                Log.d(TAG, "Reduction2:" + characteristics.get(CameraCharacteristics.SENSOR_CALIBRATION_TRANSFORM2).toString());
                logColorPattern();
                Log.d(TAG, "Blacklvl:" + characteristics.get(CameraCharacteristics.SENSOR_BLACK_LEVEL_PATTERN).getOffsetForIndex(0, 0));
                LensShadingMap lensShadingMap = captureResult.get(CaptureResult.STATISTICS_LENS_SHADING_CORRECTION_MAP);
                Log.d(TAG,"LensShading: " + lensShadingMap.toString());
                Log.d(TAG, "SensorNoiseProfile" + captureResult.get(CaptureResult.SENSOR_NOISE_PROFILE));
                Log.d(TAG, "TonemapCurve" + captureResult.get(CaptureResult.TONEMAP_CURVE).toString());
                Log.d(TAG, "Rolling Shutter Skew" + captureResult.get(CaptureResult.SENSOR_ROLLING_SHUTTER_SKEW).toString());
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    Log.d(TAG, "Dynamic BlackLVl" + captureResult.get(CaptureResult.SENSOR_DYNAMIC_BLACK_LEVEL).toString());
                    Log.d(TAG, "Dynamic WhiteLVl" + captureResult.get(CaptureResult.SENSOR_DYNAMIC_WHITE_LEVEL).toString());
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    Log.d(TAG, "TonemapGamma" + captureResult.get(CaptureResult.TONEMAP_GAMMA).toString());
                    Log.d(TAG, "TonemapPreset" + captureResult.get(CaptureResult.TONEMAP_PRESET_CURVE).toString());
                }
                if (captureResult.get(CaptureResult.COLOR_CORRECTION_GAINS) != null)
                    Log.d(TAG, "CC Gain " + captureResult.get(CaptureResult.COLOR_CORRECTION_GAINS).toString());
            } catch (NullPointerException ex) {
                Log.WriteEx(ex);
            }
        }
    }

    public synchronized void AddImage(Image image)
    {
        images.add(image);
        logImageFormat(image);
        Log.d(TAG,"WxH:" + image.getWidth() +"x"+image.getHeight());
    }

    public synchronized boolean rdyToGetSaved()
    {
        if (captureType == CaptureType.JpegDng16 || captureType == CaptureType.JpegDng10)
            return images.size() == 2 && captureResult != null;
        else
            return images.size() == 1 && captureResult != null;
    }

    @Override
    public void onImageAvailable(ImageReader reader) {
        Image img = null;
        Log.d(TAG, "OnRawAvailible, in buffer: " + images.size());
        try {
            img = reader.acquireLatestImage();
            Log.d(TAG, "ImageFormat:" + img.getFormat() +  " CaptureType:" + captureType + " Size in Bytes: " +img.getPlanes()[0].getBuffer().remaining());
            Log.d(TAG, "Img WxH:" + img.getWidth() +"x" + img.getHeight());

            switch (captureType)
            {
                case Jpeg:
                    if (img.getFormat() == ImageFormat.JPEG) {
                        AddImage(img);
                        Log.d(TAG,"Add Jpeg");
                    }
                    else
                        img.close();
                    break;
                case JpegDng16:
                    if (img.getFormat() == ImageFormat.JPEG || img.getFormat() == ImageFormat.RAW_SENSOR) {
                        AddImage(img);
                        Log.d(TAG,"Add Jpeg + dng 16");
                    }
                    else
                        img.close();
                    break;
                case JpegDng10:
                    if (img.getFormat() == ImageFormat.JPEG || img.getFormat() == ImageFormat.RAW10) {
                        AddImage(img);
                        Log.d(TAG,"Add Jpeg + dng10");
                    }
                    else
                        img.close();
                    break;
                case Dng10:
                    if (img.getFormat() == ImageFormat.RAW10) {
                        AddImage(img);
                        Log.d(TAG,"Add dng10");
                    }
                    else
                        img.close();
                    break;
                case Dng16:
                    if (img.getFormat() == ImageFormat.RAW_SENSOR) {
                        AddImage(img);
                        Log.d(TAG,"Add dng16");
                    }
                    else
                        img.close();
                    break;
                case Dng12:
                    if (img.getFormat() == ImageFormat.RAW12) {
                        AddImage(img);
                        Log.d(TAG,"Add Dng12");
                    }
                    else
                        img.close();
                    break;
                case Bayer10:
                    if (img.getFormat() == ImageFormat.RAW10) {
                        AddImage(img);
                        Log.d(TAG,"Add Bayer10");
                    }
                    else if(img.getFormat() == ImageFormat.RAW_SENSOR)
                    {
                        AddImage(img);
                        Log.d(TAG, "Add RawSensor from expected raw10.");
                    }
                    else {
                        img.close();
                    }
                    break;
                case Bayer16:
                    if (img.getFormat() == ImageFormat.RAW_SENSOR) {
                        AddImage(img);
                        Log.d(TAG,"Add Bayer16");
                    }
                    else
                        img.close();
                    break;
                default:
                    if (images.contains(img))
                        images.remove(img);
                    img.close();
                    Log.d(TAG,"Close already added image");
                    break;
            }
        }
        catch (IllegalStateException ex)
        {
            Log.WriteEx(ex);
            if (images.contains(img))
                images.remove(img);
            if (img != null)
                img.close();
        }
        if (rdyToGetSaved()) {
            save();
            rdyToSaveImg.onRdyToSaveImg(ImageCaptureHolder.this);
        }
    }

    @Override
    public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request, TotalCaptureResult result) {
        Log.d(TAG, "onCaptureCompleted FrameNum:" +result.getFrameNumber());

        Log.d(TAG, "OnCaptureResultAvailible, Images stored:" + images.size());
        SetCaptureResult(result);
        if (rdyToGetSaved()) {
            save();
            rdyToSaveImg.onRdyToSaveImg(ImageCaptureHolder.this);
        }
    }

    private void save()
    {
        Log.d(TAG,"save " + images.size());
        for(int i=0; i< images.size();i++)
            saveImage(images.get(i),filepath);
        images.clear();
    }

    public void CLEAR()
    {
        for (Image img : images)
            img.close();
        clear();
    }

    private void clear()
    {
        images = null;
        customMatrix = null;
        captureResult = null;
        location = null;
        characteristics = null;
        activityInterface = null;
        moduleInterface = null;
    }


    protected void saveImage(Image image,String f) {
        File file = null;
        ImageTask task = null;

        switch (captureType)
        {

            case Jpeg:
                file = new File(f+".jpg");
                task = process_jpeg(image, file);
                break;
            case JpegDng16:
                if (image.getFormat() == ImageFormat.JPEG)
                {
                    Log.d(TAG, "save jpg");
                    file = new File(f+".jpg");
                    task = process_jpeg(image, file);
                }
                else if (image.getFormat() == ImageFormat.RAW_SENSOR)
                {
                    Log.d(TAG, "save dng");
                    file = new File(f + ".dng");
                    if (forceRawToDng) // use freedcam dngconverter
                        if (support12bitRaw)
                            task = process_rawWithDngConverter(image, DngProfile.Pure16bit_To_12bit, file);
                        else
                            task = process_rawWithDngConverter(image, DngProfile.Plain, file);
                    else // use android dngCreator
                        task = process_rawSensor(image, file);
                }
                break;
            case JpegDng10:
                if (image.getFormat() == ImageFormat.JPEG)
                {
                    Log.d(TAG, "save jpg");
                    file = new File(f+".jpg");
                    task = process_jpeg(image, file);
                }
                else if(image.getFormat() == ImageFormat.RAW10) {
                    Log.d(TAG, "save 10bit dng");
                    file = new File(f + ".dng");
                    task = process_rawWithDngConverter(image, DngProfile.Mipi, file);
                }
                break;
            case Dng10:
                if(image.getFormat() == ImageFormat.RAW10) {
                    Log.d(TAG, "save 10bit dng");
                    file = new File(f + ".dng");
                    task = process_rawWithDngConverter(image, DngProfile.Mipi, file);
                }
                break;
            case Dng16:
                if (image.getFormat() == ImageFormat.RAW_SENSOR)
                {
                    file = new File(f + ".dng");
                    if (forceRawToDng) // use freedcam dngconverter
                        if (support12bitRaw)
                            task = process_rawWithDngConverter(image, DngProfile.Pure16bit_To_12bit, file);
                        else
                            task = process_rawWithDngConverter(image, DngProfile.Plain, file);
                    else // use android dngCreator
                        task = process_rawSensor(image, file);
                }
                break;
            case Dng12:
                if (image.getFormat() == ImageFormat.RAW12) {
                    Log.d(TAG, "save 12bit dng");
                    file = new File(f + ".dng");
                    task = process_rawWithDngConverter(image, DngProfile.Mipi12, file);
                }
                break;
            case Bayer10:
                Log.d(TAG, "ImageFormat RAW10 = " + image.getFormat());
                if (image.getFormat() == ImageFormat.RAW10 || image.getFormat() == ImageFormat.RAW_SENSOR)
                {
                    Log.d(TAG, "save bayer10");
                    file = new File(f + ".bayer");
                    task = process_jpeg(image,file);
                }
                break;
            case Bayer16:
                if (image.getFormat() == ImageFormat.RAW_SENSOR)
                {
                    Log.d(TAG, "save bayer16");
                    file = new File(f + ".bayer");
                    task = process_jpeg(image,file);
                }
                break;
        }

        if (task != null) {
            ImageManager.putImageSaveTask(task);
            Log.d(TAG, "Put task to Queue");
        }
    }




    private ImageTask process_jpeg(Image image, File file) {

        Log.d(TAG, "Create JPEG");
        ByteBuffer buffer = image.getPlanes()[0].getBuffer();
        byte[] bytes = new byte[buffer.remaining()];
        buffer.get(bytes);
        ImageSaveTask task = new ImageSaveTask(activityInterface,moduleInterface);
        task.setBytesTosave(bytes, ImageSaveTask.JPEG);
        task.setFilePath(file,externalSD);
        buffer.clear();
        image.close();
        buffer = null;
        image = null;
        return task;
    }



    private ImageTask process_rawSensor(Image image, File file) {
        ImageTaskDngConverter taskDngConverter = new ImageTaskDngConverter(captureResult,image,characteristics,file,activityInterface,orientation,location,moduleInterface);
        return taskDngConverter;
    }


    private ImageTask process_rawWithDngConverter(Image image, int rawFormat,File file) {
        ImageSaveTask saveTask = new ImageSaveTask(activityInterface,moduleInterface);
        Log.d(TAG, "Create DNG VIA RAw2DNG");
        ByteBuffer buffer = image.getPlanes()[0].getBuffer();
        byte[] bytes = new byte[buffer.remaining()];
        buffer.get(bytes);
        saveTask.setBytesTosave(bytes,ImageSaveTask.RAW_SENSOR);
        buffer.clear();

        saveTask.setCropSize(cropWidth,cropHeight);
        if (!SettingsManager.get(SettingKeys.LOCATION_MODE).get().equals(SettingsManager.getInstance().getResString(R.string.off_)))
            saveTask.setLocation(activityInterface.getLocationManager().getCurrentLocation());
        saveTask.setForceRawToDng(true);
        try {
            saveTask.setFocal(captureResult.get(CaptureResult.LENS_FOCAL_LENGTH));
        } catch (NullPointerException e) {
            Log.WriteEx(e);
        }
        try {
            saveTask.setFnum(captureResult.get(CaptureResult.LENS_APERTURE));
        } catch (NullPointerException e) {
            Log.WriteEx(e);
        }
        try {
            saveTask.setIso(captureResult.get(CaptureResult.SENSOR_SENSITIVITY));
        } catch (NullPointerException e) {
            Log.WriteEx(e);
            saveTask.setIso(100);
        }
        try {
            double mExposuretime = captureResult.get(CaptureResult.SENSOR_EXPOSURE_TIME).doubleValue() / 1000000000;
            saveTask.setExposureTime((float) mExposuretime);
        } catch (NullPointerException e) {
            Log.WriteEx(e);
            saveTask.setExposureTime(0);
        }
        try {
            saveTask.setExposureIndex(captureResult.get(CaptureResult.CONTROL_AE_EXPOSURE_COMPENSATION) * characteristics.get(CameraCharacteristics.CONTROL_AE_COMPENSATION_STEP).floatValue());
        } catch (NullPointerException e) {
            Log.WriteEx(e);
            saveTask.setExposureIndex(0);
        }
        //disabled baseline exposure because on some devices extreme values get used.
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            try {
                int postraw = captureResult.get(CaptureResult.CONTROL_POST_RAW_SENSITIVITY_BOOST);
                double postrawgain = postraw / 100f;
                //double baselineExposure = log10(postrawgain) / log10(2.0f);
                //Log.d(TAG, "BaselineExposure:" + baselineExposure);
                saveTask.setBaselineExposure((float) postrawgain);
            } catch (NullPointerException ex) {
                Log.WriteEx(ex);
                saveTask.setBaselineExposure(0);
            }
        }*/

        try {
            float greensplit = captureResult.get(CaptureResult.SENSOR_GREEN_SPLIT);
            int fgreen = (int)(greensplit * 5000) -5000;
            Log.d(TAG,"GreenSplit:" + fgreen);
            saveTask.setBayerGreenSplit(fgreen);
        }
        catch (NullPointerException ex)
        {
            Log.WriteEx(ex);
        }



        DngProfile prof = null;
        if (SettingsManager.get(SettingKeys.useCustomMatrixOnCamera2).get() && SettingsManager.getInstance().getDngProfilesMap().get(bytes.length) != null)
            prof = SettingsManager.getInstance().getDngProfilesMap().get(bytes.length);
        else
            prof = getDngProfile(rawFormat, image.getWidth(),image.getHeight());
        image.close();
        image = null;
        prof.toneMapProfile = this.toneMapProfile;
        OpCodeCreator opCodeCreator = new OpCodeCreator();
        byte opcode[] = opCodeCreator.createOpCode2(characteristics,captureResult);
        OpCode opCode =new OpCode(opcode,null);
        saveTask.setOpCode(opCode);
        saveTask.setDngProfile(prof);
        saveTask.setFilePath(file, externalSD);
        saveTask.setOrientation(orientation);
        return saveTask;
    }


    protected DngProfile getDngProfile(int rawFormat, int width, int height) {
        int black, white,c;
        try {
            black = characteristics.get(CameraCharacteristics.SENSOR_BLACK_LEVEL_PATTERN).getOffsetForIndex(0,0);
        } catch (NullPointerException e) {
            Log.WriteEx(e);
            black = 64;
        }
        try {
            white = characteristics.get(CameraCharacteristics.SENSOR_INFO_WHITE_LEVEL);
        } catch (Exception e) {
            Log.WriteEx(e);
            white = 1023;
        }
        try {
            c = characteristics.get(CameraCharacteristics.SENSOR_INFO_COLOR_FILTER_ARRANGEMENT);
        } catch (Exception e) {
            Log.WriteEx(e);
            c = 0;
        }
        String colorpattern;
        int[] cfaOut = new int[4];
        switch (c)
        {
            case 1:
                colorpattern = DngProfile.GRBG;
                cfaOut[0] = 1;
                cfaOut[1] = 0;
                cfaOut[2] = 2;
                cfaOut[3] = 1;
                break;
            case 2:
                colorpattern = DngProfile.GBRG;
                cfaOut[0] = 1;
                cfaOut[1] = 2;
                cfaOut[2] = 0;
                cfaOut[3] = 1;
                break;
            case 3:
                colorpattern = DngProfile.BGGR;
                cfaOut[0] = 2;
                cfaOut[1] = 1;
                cfaOut[2] = 1;
                cfaOut[3] = 0;
                break;
            default:
                colorpattern = DngProfile.RGGB;
                cfaOut[0] = 0;
                cfaOut[1] = 1;
                cfaOut[2] = 1;
                cfaOut[3] = 2;
                break;
        }
        float[] color2;
        float[] color1;
        float[] neutral = new float[3];
        float[] forward2 = null;
        float[] forward1 = null;
        float[] reduction1 = null;
        float[] reduction2 = null;
        double[]finalnoise = null;
        if (customMatrix == null)
        {
            Log.d(TAG, "No Custom Matrix set, get it from the characteristics");
            //dont catch errors on cc1 cc2 and neutral, these 3 are needed and that case should never happen
            color1 = getFloatMatrix(characteristics.get(CameraCharacteristics.SENSOR_COLOR_TRANSFORM1));
            color2 = getFloatMatrix(characteristics.get(CameraCharacteristics.SENSOR_COLOR_TRANSFORM2));
            Rational[] n = captureResult.get(CaptureResult.SENSOR_NEUTRAL_COLOR_POINT);
            neutral[0] = n[0].floatValue();
            neutral[1] = n[1].floatValue();
            neutral[2] = n[2].floatValue();
            try {
                forward2  = getFloatMatrix(characteristics.get(CameraCharacteristics.SENSOR_FORWARD_MATRIX2));
            } catch (NullPointerException e) {
                Log.WriteEx(e);
                forward2 = null;
            }
            try {
                forward1  = getFloatMatrix(characteristics.get(CameraCharacteristics.SENSOR_FORWARD_MATRIX1));
            } catch (Exception e) {
                Log.WriteEx(e);
                forward1 = null;
            }
            try {
                reduction1 = getFloatMatrix(characteristics.get(CameraCharacteristics.SENSOR_CALIBRATION_TRANSFORM1));
            } catch (Exception e) {
                Log.WriteEx(e);
                reduction1 = null;
            }
            try {
                reduction2 = getFloatMatrix(characteristics.get(CameraCharacteristics.SENSOR_CALIBRATION_TRANSFORM2));
            } catch (Exception e) {
                Log.WriteEx(e);
                reduction2 = null;
            }
            try {
                finalnoise = new double[6];
                getNoiseMatrix(cfaOut, finalnoise);
            } catch (Exception e) {
                Log.WriteEx(e);
                finalnoise = null;
            }
            customMatrix = new CustomMatrix(color1,color2,neutral,forward1,forward2,reduction1,reduction2,finalnoise);

        }

        DngProfile profile = new DngProfile(black,white,width, height,rawFormat, colorpattern, 0,
                customMatrix,
                ""
        );

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                Rect activar = characteristics.get(CameraCharacteristics.SENSOR_INFO_PRE_CORRECTION_ACTIVE_ARRAY_SIZE);
                int ar[] = {activar.left, activar.top,activar.right,activar.bottom};
                profile.setActiveArea(ar);
            } catch (Exception ex) {
                Log.WriteEx(ex);
            }
        }


        return profile;
    }

    private void getNoiseMatrix(int[] cfaOut, double[] finalnoise) {
        //noise
        Pair[] p = captureResult.get(CaptureResult.SENSOR_NOISE_PROFILE);
        double[] noiseys = new double[p.length*2];
        int i = 0;
        for (int h = 0; h < p.length; h++)
        {
            noiseys[i++] = (double)p[h].first;
            noiseys[i++] = (double)p[h].second;
        }
        double[] noise = new double[6];
        int[] cfaPlaneColor = {0, 1, 2};
        generateNoiseProfile(noiseys,cfaOut, cfaPlaneColor,3,noise);

        for (i = 0; i < noise.length; i++)
            if (noise[i] > 2 || noise[i] < -2)
                finalnoise[i] = 0;
            else
                finalnoise[i] = (float)noise[i];
        //noise end
    }

    private void generateNoiseProfile(double[] perChannelNoiseProfile, int[] cfa,
                                      int[] planeColors, int numPlanes,
        /*out*/double[] noiseProfile) {

        for (int p = 0; p < 3; ++p) {
            int S = p * 2;
            int O = p * 2 + 1;

            noiseProfile[S] = 0;
            noiseProfile[O] = 0;
            boolean uninitialized = true;
            for (int c = 0; c < 4; ++c) {
                if (cfa[c] == planeColors[p] && perChannelNoiseProfile[c * 2] > noiseProfile[S]) {
                    noiseProfile[S] = perChannelNoiseProfile[c * 2];
                    noiseProfile[O] = perChannelNoiseProfile[c * 2 + 1];
                    uninitialized = false;
                }
            }
            if (uninitialized) {
                Log.d(TAG, "%s: No valid NoiseProfile coefficients for color plane %zu");
            }
        }
    }

    private float[]getFloatMatrix(ColorSpaceTransform transform)
    {
        float[] ret = new float[9];
        ret[0] = roundTo6Places(transform.getElement(0, 0).floatValue());
        ret[1] = roundTo6Places(transform.getElement(1, 0).floatValue());
        ret[2] = roundTo6Places(transform.getElement(2, 0).floatValue());
        ret[3] = roundTo6Places(transform.getElement(0, 1).floatValue());
        ret[4] = roundTo6Places(transform.getElement(1, 1).floatValue());
        ret[5] = roundTo6Places(transform.getElement(2, 1).floatValue());
        ret[6] = roundTo6Places(transform.getElement(0, 2).floatValue());
        ret[7] = roundTo6Places(transform.getElement(1, 2).floatValue());
        ret[8] = roundTo6Places(transform.getElement(2, 2).floatValue());
        return ret;
    }

    private float roundTo6Places(float f )
    {
        return Math.round(f*1000000f)/1000000f;
    }


    private void logColorPattern()
    {
        int c= characteristics.get(CameraCharacteristics.SENSOR_INFO_COLOR_FILTER_ARRANGEMENT);
        switch (c)
        {
            case 1:
                Log.d(TAG, "ColorPattern: GRBG");
                break;
            case 2:
                Log.d(TAG, "ColorPattern: GBRG");
                break;
            case 3:
                Log.d(TAG, "ColorPattern: BGGR");
                break;
            default:
                Log.d(TAG, "ColorPattern: RGGB");
                break;
        }
    }

    private void logNeutralMatrix()
    {
        Rational[] n = captureResult.get(CaptureResult.SENSOR_NEUTRAL_COLOR_POINT);
        Log.d(TAG,"NeutralMatrix:" + n[0].floatValue() + ","+ n[1].floatValue()+","+n[2].floatValue());
    }

    private void logImageFormat(Image image)
    {
        switch (image.getFormat())
        {
            case ImageFormat.RAW10:
                Log.d(TAG,"ImageFormat:RAW10");
                break;
            case ImageFormat.RAW12:
                Log.d(TAG,"ImageFormat:RAW12");
                break;
            case ImageFormat.RAW_SENSOR:
                Log.d(TAG,"ImageFormat:RAW_SENSOR");
                break;
            case ImageFormat.JPEG:
                Log.d(TAG,"ImageFormat:JPEG");
                break;
        }
    }

}
