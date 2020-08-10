package freed.cam.apis.camera2.modules.capture;

import android.graphics.ImageFormat;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CaptureResult;
import android.media.Image;
import android.os.Build;
import android.util.Size;

import androidx.annotation.RequiresApi;

import com.troop.freedcam.R;

import java.io.File;
import java.nio.ByteBuffer;

import freed.ActivityInterface;
import freed.FreedApplication;
import freed.cam.apis.basecamera.modules.ModuleInterface;
import freed.dng.DngProfile;
import freed.dng.opcode.OpCodeCreator;
import freed.image.ImageManager;
import freed.image.ImageSaveTask;
import freed.image.ImageTask;
import freed.image.ImageTaskDngConverter;
import freed.jni.OpCode;
import freed.settings.SettingKeys;
import freed.settings.SettingsManager;
import freed.utils.Log;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class RawImageCapture extends StillImageCapture {

    private final String TAG = RawImageCapture.class.getSimpleName();

    public RawImageCapture(Size size,int format, boolean setToPreview, ActivityInterface activityInterface, ModuleInterface moduleInterface,String file_ending) {
        super(size, format, setToPreview, activityInterface, moduleInterface,file_ending);
    }

    @Override
    public boolean onCaptureCompleted(Image image, CaptureResult result) {
        ImageTask task = null;
        boolean consumerFreeImage = false;
        File file = new File(getFilepath() + file_ending);
        //Log.d(TAG, "save dng");
        if(image.getFormat() == ImageFormat.RAW10) {
            Log.d(TAG, "save 10bit dng");
            task = process_rawWithDngConverter(image, DngProfile.Mipi, file,result,characteristics);
        }
        else if(image.getFormat() == ImageFormat.RAW_SENSOR) {
            if (forceRawToDng) // use freedcam dngconverter
                if (support12bitRaw)
                    task = process_rawWithDngConverter(image, DngProfile.Pure16bit_To_12bit, file, result, characteristics);
                else
                    task = process_rawWithDngConverter(image, DngProfile.Plain, file, result, characteristics);
            else { // use android dngCreator
                task = process_rawSensor(image, file, result);
                consumerFreeImage = true;
            }
        }
        if (task != null) {
            ImageManager.putImageSaveTask(task);
            Log.d(TAG, "Put task to Queue");
        }
        return consumerFreeImage;
    }

    private ImageTask process_rawWithDngConverter(Image image, int rawFormat,File file, CaptureResult captureResult,CameraCharacteristics characteristics) {
        ImageSaveTask saveTask = new ImageSaveTask(activityInterface,moduleInterface);
        Log.d(TAG, "Create DNG VIA RAw2DNG");
        ByteBuffer buffer = image.getPlanes()[0].getBuffer();
        byte[] bytes = new byte[buffer.remaining()];
        buffer.get(bytes);
        saveTask.setBytesTosave(bytes,ImageSaveTask.RAW_SENSOR);
        buffer.clear();

        if (!SettingsManager.get(SettingKeys.LOCATION_MODE).get().equals(FreedApplication.getStringFromRessources(R.string.off_)))
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
            prof = DngProfileCreator.getDngProfile(rawFormat, image.getWidth(),image.getHeight(),characteristics,customMatrix,captureResult);
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

    private ImageTask process_rawSensor(Image image, File file,CaptureResult captureResult) {
        ImageTaskDngConverter taskDngConverter = new ImageTaskDngConverter(captureResult,image,characteristics,file,activityInterface,orientation,location,moduleInterface);
        return taskDngConverter;
    }
}
