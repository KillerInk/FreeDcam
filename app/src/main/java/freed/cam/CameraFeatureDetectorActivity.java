package freed.cam;

import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.widget.TextView;

import com.lge.hardware.LGCamera;
import com.troop.freedcam.R;

import java.lang.reflect.Method;
import java.util.ArrayList;

import freed.ActivityAbstract;
import freed.cam.apis.KEYS;
import freed.cam.apis.camera1.cameraholder.CameraHolderMTK;
import freed.cam.apis.camera1.parameters.DeviceSelector;
import freed.cam.apis.camera1.parameters.device.I_Device;
import freed.cam.apis.camera1.parameters.modes.PictureFormatHandler;
import freed.utils.AppSettingsManager;
import freed.utils.DeviceUtils;
import freed.utils.LocationHandler;
import freed.viewer.holder.FileHolder;

import static freed.cam.apis.KEYS.BAYER;

/**
 * Created by troop on 27.12.2016.
 */

public class CameraFeatureDetectorActivity extends ActivityAbstract
{
    private final String TAG = CameraFeatureDetectorActivity.class.getSimpleName();
    private TextView loggerview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camerafeaturedetector);
        loggerview = (TextView)findViewById(R.id.textview_log);
        new Camera1AsyncTask().execute("");
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public LocationHandler getLocationHandler() {
        return null;
    }

    @Override
    public void WorkHasFinished(FileHolder fileHolder) {

    }

    @Override
    public void WorkHasFinished(FileHolder[] fileHolder) {

    }

    @Override
    protected void cameraPermsissionGranted(boolean granted) {
        Log.d(TAG, "cameraPermission Granted:" + granted);
        if (granted) {
            new Camera1AsyncTask().execute("");
        }
        else {
            finish();
        }
    }

    private void sendLog(String log)
    {
        String tmp = log + " \n"+ loggerview.getText().toString();
        loggerview.setText(tmp);
    }

    private class Camera1AsyncTask extends AsyncTask<String,String, String>
    {

        @Override
        protected String doInBackground(String... params)
        {
            //detect Device
            if (getAppSettings().getDevice() == null)
                getAppSettings().SetDevice(new DeviceUtils().getDevice(getResources()));
            publishProgress("Device:"+getAppSettings().getDevice().name());
            //detect frameworks
            getAppSettings().setFramework(getFramework());
            publishProgress("FrameWork:"+getAppSettings().getFrameWork());
            //can open legcay
            getAppSettings().setCanOpenLegacy(canOpenLegacy());
            publishProgress("CanOpenLegacy:"+getAppSettings().getCanOpenLegacy());
            if (hasCameraPermission())
            {
                Camera camera = null;
                int cameraCounts = Camera.getNumberOfCameras();
                for (int i = 0; i < cameraCounts; i++)
                {
                    getAppSettings().SetCurrentCamera(i);
                    detectFrontCamera(i);
                    publishProgress("isFrontCamera:"+getAppSettings().getIsFrontCamera() + " CameraID:"+ i);

                    Camera.Parameters parameters = getParameters(i);
                    publishProgress("Detecting Features");
                    I_Device device = new DeviceSelector().getDevice(null,parameters, getAppSettings());

                    detectedPictureFormats(parameters,device);
                    publishProgress("DngSupported:" + device.IsDngSupported() + " RawSupport:"+getAppSettings().isRawPictureFormatSupported());
                    publishProgress("PictureFormats:" + getStringFromArray(getAppSettings().getPictureFormatValues()));
                    publishProgress("RawFormats:" + getStringFromArray(getAppSettings().getRawPictureFormatValues()));
                    publishProgress(" RawFormat:" + getAppSettings().getRawPictureFormat());

                    detectPictureSizes(parameters);
                    publishProgress("PictureSizes:" + getStringFromArray(getAppSettings().getPictureSizeValues()));
                    publishProgress("PictureSize:" + getAppSettings().getPictureSize());

                    detectFocusModes(parameters);
                    publishProgress("FocusModes:" + getStringFromArray(getAppSettings().getFocusModeValues()));
                    publishProgress("FocusMode:" + getAppSettings().getFocusmode());


                    //publishProgress("\n");
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            sendLog(values[0]);
        }

        private String getStringFromArray(String[] arr)
        {
            String t = "";
            for (int i =0; i<arr.length;i++)
                t+=arr[i]+",";
            return t;
        }

        private void detectFrontCamera(int i) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i,info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK)
                getAppSettings().setIsFrontCamera(false);
            else
                getAppSettings().setIsFrontCamera(true);
        }

        private boolean hasLGFramework()
        {
            try {
                Class c = Class.forName("com.lge.hardware.LGCamera");
                Log.d(TAG, "Has Lg Framework");
                c = Class.forName("com.lge.media.CamcorderProfileEx");
                Log.d(TAG, "Has Lg Framework");
                return true;

            } catch (ClassNotFoundException|NullPointerException|UnsatisfiedLinkError | ExceptionInInitializerError e) {

                Log.d(TAG, "No LG Framework");
                return false;
            }
        }

        private boolean isMotoExt()
        {
            try {
                Class c = Class.forName("com.motorola.android.camera.CameraMotExt");
                Log.d(TAG, "Has Moto Framework");
                c = Class.forName("com.motorola.android.media.MediaRecorderExt");
                Log.d(TAG, "Has Moto Framework");
                return true;

            } catch (ClassNotFoundException|NullPointerException|UnsatisfiedLinkError | ExceptionInInitializerError e) {
                Log.d(TAG, "No Moto Framework");
                return false;
            }
        }

        private boolean isMTKDevice()
        {
            try
            {
                Class camera = Class.forName("android.hardware.Camera");
                Method[] meths = camera.getMethods();
                Method app = null;
                for (Method m : meths)
                {
                    if (m.getName().equals("setProperty"))
                        app = m;
                }
                if (app != null) {
                    Log.d(TAG,"MTK Framework found");
                    return true;
                }
                Log.d(TAG, "MTK Framework not found");
                return false;
            }
            catch (ClassNotFoundException|NullPointerException|UnsatisfiedLinkError | ExceptionInInitializerError e)
            {
                e.printStackTrace();
                Log.d(TAG, "MTK Framework not found");
                return false;
            }
        }

        private int getFramework()
        {
            if (hasLGFramework())
                return AppSettingsManager.FRAMEWORK_LG;
            else if (isMTKDevice())
                return AppSettingsManager.FRAMEWORK_MTK;
            else if (isMotoExt())
                return AppSettingsManager.FRAMEWORK_MOTO_EXT;
            else
                return AppSettingsManager.FRAMEWORK_NORMAL;
        }

        private boolean canOpenLegacy()
        {
            try {
                Class[] arrclass = {Integer.TYPE, Integer.TYPE};
                Method method = Class.forName("android.hardware.Camera").getDeclaredMethod("openLegacy", arrclass);
                if (method != null)
                    return true;
                else
                    return false;
            }
            catch
                    (NoSuchMethodException e) {
                e.printStackTrace();
                return false;
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                return false;
            }
        }

        private Camera.Parameters getParameters(int currentcamera)
        {
            Camera camera;
            switch (getAppSettings().getFrameWork())
            {
                case AppSettingsManager.FRAMEWORK_LG:
                {
                    LGCamera lgCamera;
                    if (getAppSettings().getDevice() == DeviceUtils.Devices.LG_G4 || getAppSettings().getDevice() == DeviceUtils.Devices.LG_V20)
                        lgCamera = new LGCamera(currentcamera, 256);
                    else
                        lgCamera = new LGCamera(currentcamera);
                    return lgCamera.getLGParameters().getParameters();
                }
                case AppSettingsManager.FRAMEWORK_MOTO_EXT:
                {
                    camera  = Camera.open(currentcamera);
                    Camera.Parameters parameters = camera.getParameters();
                    parameters.set("mot-app", "true");
                    camera.setParameters(parameters);
                    parameters = camera.getParameters();
                    camera.release();
                    return parameters;
                }
                case AppSettingsManager.FRAMEWORK_MTK:
                {
                    CameraHolderMTK.setMtkAppMode();
                }
                default:
                {
                    camera  = Camera.open(currentcamera);
                    Camera.Parameters parameters = camera.getParameters();
                    camera.release();
                    return parameters;
                }

            }
        }


        private void detectedPictureFormats(Camera.Parameters parameters, I_Device device)
        {
            //drop raw for front camera
            if (getAppSettings().getIsFrontCamera())
            {
                getAppSettings().setBoolean(AppSettingsManager.PICTUREFORMATSUPPORTED,false);
                getAppSettings().setBoolean(AppSettingsManager.RAWPICTUREFORMATSUPPORTED,false);
            }
            else {

                if (getAppSettings().getFrameWork() == AppSettingsManager.FRAMEWORK_MTK) {
                    getAppSettings().setBoolean(AppSettingsManager.PICTUREFORMATSUPPORTED, true);
                    getAppSettings().setBoolean(AppSettingsManager.RAWPICTUREFORMATSUPPORTED, true);
                } else {
                    if (getAppSettings().getDevice() == DeviceUtils.Devices.LG_G2)
                    {
                        getAppSettings().setBoolean(AppSettingsManager.PICTUREFORMATSUPPORTED, true);
                        getAppSettings().setBoolean(AppSettingsManager.RAWPICTUREFORMATSUPPORTED, true);
                        getAppSettings().setRawPictureFormat(KEYS.BAYER_MIPI_10BGGR);
                    }
                    else if (getAppSettings().getDevice() == DeviceUtils.Devices.HTC_OneA9 )
                    {
                        getAppSettings().setBoolean(AppSettingsManager.PICTUREFORMATSUPPORTED, true);
                        getAppSettings().setBoolean(AppSettingsManager.RAWPICTUREFORMATSUPPORTED, true);
                        getAppSettings().setRawPictureFormat(KEYS.BAYER_MIPI_10RGGB);
                    }else if(getAppSettings().getDevice() == DeviceUtils.Devices.MotoG3 ||getAppSettings().getDevice() == DeviceUtils.Devices.MotoG_Turbo)
                    {
                        getAppSettings().setBoolean(AppSettingsManager.PICTUREFORMATSUPPORTED, true);
                        getAppSettings().setBoolean(AppSettingsManager.RAWPICTUREFORMATSUPPORTED, true);
                        getAppSettings().setRawPictureFormat(KEYS.BAYER_QCOM_10RGGB);
                    }

                    else if(getAppSettings().getDevice() == DeviceUtils.Devices.Htc_M8 && Build.VERSION.SDK_INT >= 21)
                    {
                        getAppSettings().setBoolean(AppSettingsManager.PICTUREFORMATSUPPORTED, true);
                        getAppSettings().setBoolean(AppSettingsManager.RAWPICTUREFORMATSUPPORTED, true);
                        getAppSettings().setRawPictureFormat(KEYS.BAYER_QCOM_10GRBG);
                    }
                    else
                    {
                        String formats = parameters.get(KEYS.PICTURE_FORMAT_VALUES);

                        if (formats.contains("bayer-mipi") || formats.contains("raw"))
                        {
                            getAppSettings().setBoolean(AppSettingsManager.RAWPICTUREFORMATSUPPORTED, true);
                            String[] forms = formats.split(",");
                            for (String s : forms) {
                                if (s.contains("bayer-mipi") || s.contains("raw"))
                                {
                                    getAppSettings().setRawPictureFormat(s);
                                    break;
                                }
                            }
                        }
                        if (formats.contains(BAYER))
                        {
                            ArrayList<String> tmp = new ArrayList<>();
                            String[] forms = formats.split(",");
                            for (String s : forms) {
                                if (s.contains(BAYER))
                                {
                                    tmp.add(s);
                                }
                            }
                            String[] rawFormats = new String[tmp.size()];
                            tmp.toArray(rawFormats);
                            getAppSettings().setRawPictureFormatValues(rawFormats);
                        }
                    }
                }

                if (device.IsDngSupported())
                {
                    getAppSettings().setPictureFormatValues(new String[]{
                            AppSettingsManager.CaptureMode[AppSettingsManager.JPEG], AppSettingsManager.CaptureMode[AppSettingsManager.DNG], AppSettingsManager.CaptureMode[AppSettingsManager.RAW]
                    });
                }
                else if (getAppSettings().isRawPictureFormatSupported()) {
                        getAppSettings().setPictureFormatValues(new String[]{
                                AppSettingsManager.CaptureMode[AppSettingsManager.JPEG], AppSettingsManager.CaptureMode[AppSettingsManager.RAW]
                        });
                }
                else
                {
                    getAppSettings().setPictureFormatValues(new String[]{
                            AppSettingsManager.CaptureMode[AppSettingsManager.JPEG]
                    });
                }

            }
        }

        private void detectPictureSizes(Camera.Parameters parameters)
        {
            String[] sizes = parameters.get(KEYS.PICTURE_SIZE_VALUES).split(",");
            getAppSettings().setPictureSizeValues(sizes);
            getAppSettings().setPictureSize(parameters.get(KEYS.PICTURE_SIZE));
        }

        private void detectFocusModes(Camera.Parameters parameters)
        {
            getAppSettings().setFocusModeValues(parameters.get(KEYS.FOCUS_MODE_VALUES).split(","));
            getAppSettings().setFocusmode(parameters.get(KEYS.FOCUS_MODE));
        }
    }


}
