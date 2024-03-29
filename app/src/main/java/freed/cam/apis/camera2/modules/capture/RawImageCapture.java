package freed.cam.apis.camera2.modules.capture;

import android.graphics.ImageFormat;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CaptureResult;
import android.media.Image;
import android.os.Build;
import android.util.Size;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import java.io.File;
import java.nio.ByteBuffer;

import freed.FreedApplication;
import freed.cam.ActivityFreeDcamMain;
import freed.cam.apis.basecamera.modules.ModuleInterface;
import freed.dng.CustomMatrix;
import freed.dng.DngProfile;
import freed.dng.ToneMapProfile;
import freed.dng.opcode.OpCodeCreator;
import freed.image.ImageSaveTask;
import freed.image.ImageTask;
import freed.image.ImageTaskDngConverter;
import freed.jni.OpCode;
import freed.settings.SettingKeys;
import freed.settings.SettingsManager;
import freed.utils.Log;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class RawImageCapture extends StillImageCapture {

    private final static String TAG = RawImageCapture.class.getSimpleName();

    public RawImageCapture(Size size,int format, boolean setToPreview,ModuleInterface moduleInterface,String file_ending,int max_images) {
        super(size, format, setToPreview, moduleInterface,file_ending,max_images);
    }

    @Override
    protected void createTask() {
        if (image == null || result == null)
            return;
        File file = new File(getFilepath() + file_ending);
        //Log.d(TAG, "save dng");
        if(image.getFormat() == ImageFormat.RAW10) {
            Log.d(TAG, "save 10bit dng");
            task = process_rawWithDngConverter(image, DngProfile.Mipi, file,result,characteristics,image.getWidth(),image.getHeight(),moduleInterface,customMatrix,orientation,externalSD,toneMapProfile);
            image.close();
        }
        else if(image.getFormat() == ImageFormat.RAW_SENSOR) {
            if (forceRawToDng) { // use freedcam dngconverter
                if (support12bitRaw)
                    task = process_rawWithDngConverter(image, DngProfile.Pure16bit_To_12bit, file, result, characteristics,image.getWidth(),image.getHeight(),moduleInterface,customMatrix,orientation,externalSD,toneMapProfile);
                else
                    task = process_rawWithDngConverter(image,
                            DngProfile.Pure16bit_To_Lossless,
                            file,
                            result,
                            characteristics,
                            image.getWidth(),
                            image.getHeight(),
                            moduleInterface,
                            customMatrix,
                            orientation,
                            externalSD,
                            toneMapProfile);
            }
            else { // use android dngCreator
                task = process_rawSensor(image, file, result);
            }
        }
        image = null;
    }

    public static ImageTask process_rawWithDngConverter(byte[] bytes,
                                                           int rawFormat,
                                                           File file,
                                                           CaptureResult captureResult,
                                                           CameraCharacteristics characteristics,
                                                           int width,
                                                           int height,
                                                           ModuleInterface moduleInterface,
                                                           CustomMatrix customMatrix,
                                                           int orientation,
                                                           boolean externalSD,
                                                           ToneMapProfile toneMapProfile) {
        ImageSaveTask saveTask = new ImageSaveTask(moduleInterface);
        Log.d(TAG, "Create DNG VIA RAw2DNG");
        saveTask.setBytesTosave(bytes,ImageSaveTask.RAW_SENSOR);
        return getImageTask(rawFormat, file, captureResult, characteristics, width, height, customMatrix, orientation, externalSD, toneMapProfile, saveTask, bytes.length);
    }


    public static ImageTask process_rawWithDngConverter(Image bytes,
                                                           int rawFormat,
                                                           File file,
                                                           CaptureResult captureResult,
                                                           CameraCharacteristics characteristics,
                                                           int width,
                                                           int height,
                                                           ModuleInterface moduleInterface,
                                                           CustomMatrix customMatrix,
                                                           int orientation,
                                                           boolean externalSD,
                                                           ToneMapProfile toneMapProfile) {
        ImageSaveTask saveTask = new ImageSaveTask(moduleInterface);
        Log.d(TAG, "Create DNG VIA RAw2DNG");
        saveTask.setByteBufferTosave(bytes,ImageSaveTask.RAW_SENSOR);
        return getImageTask(rawFormat, file, captureResult, characteristics, width, height, customMatrix, orientation, externalSD, toneMapProfile, saveTask, bytes.getPlanes()[0].getBuffer().remaining());
    }

    @NonNull
    private static ImageTask getImageTask(int rawFormat, File file, CaptureResult captureResult,
                                          CameraCharacteristics characteristics, int width, int height,
                                          CustomMatrix customMatrix, int orientation, boolean externalSD,
                                          ToneMapProfile toneMapProfile, ImageSaveTask saveTask, int remaining) {
        SettingsManager settingsManager = FreedApplication.settingsManager();
        if (settingsManager.getGlobal(SettingKeys.LOCATION_MODE).get())
            saveTask.setLocation(ActivityFreeDcamMain.locationManager().getCurrentLocation());
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
            int fgreen = (int) (greensplit * 5000) - 5000;
            Log.d(TAG, "GreenSplit:" + fgreen);
            saveTask.setBayerGreenSplit(fgreen);
        } catch (NullPointerException ex) {
            Log.WriteEx(ex);
        }


        DngProfile prof = null;
        if (settingsManager.get(SettingKeys.USE_CUSTOM_MATRIX_ON_CAMERA_2).get() && settingsManager.getDngProfilesMap().get(remaining) != null)
            prof = settingsManager.getDngProfilesMap().get(remaining);
        else
            prof = DngProfileCreator.getDngProfile(rawFormat, width, height, characteristics, customMatrix, captureResult);
        prof.toneMapProfile = toneMapProfile;
        OpCodeCreator opCodeCreator = new OpCodeCreator();
        byte[] opcode = opCodeCreator.createOpCode2(characteristics, captureResult);
        OpCode opCode = new OpCode(opcode, null);
        saveTask.setOpCode(opCode);
        saveTask.setDngProfile(prof);
        saveTask.setFilePath(file, externalSD);
        saveTask.setOrientation(orientation);
        return saveTask;
    }

    protected ImageTask process_rawSensor(Image image, File file,CaptureResult captureResult) {
        ImageTaskDngConverter taskDngConverter = new ImageTaskDngConverter(captureResult,image,characteristics,file,orientation,location,moduleInterface);
        return taskDngConverter;
    }

    protected byte[] imageToByteArray(Image img)
    {
        return byteBufferToByteArray(img.getPlanes()[0].getBuffer());
    }

    private byte[] byteBufferToByteArray(ByteBuffer byteBuffer)
    {
        byte[] bytes = new byte[byteBuffer.remaining()];
        byteBuffer.get(bytes);
        return bytes;
    }
}
