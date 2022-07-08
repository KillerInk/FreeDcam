package freed.cam.ui.themenextgen.adapter;

import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.media.MediaRecorder;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.troop.freedcam.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import camera2_hidden_keys.ReflectionHelper;
import camera2_hidden_keys.VendorKeyParser;
import camera2_hidden_keys.VendorKeyTestLog;
import freed.cam.apis.CameraApiManager;
import freed.cam.apis.basecamera.parameters.AbstractParameter;
import freed.cam.apis.basecamera.parameters.ParameterHandler;
import freed.cam.apis.basecamera.parameters.ParameterInterface;
import freed.cam.apis.basecamera.parameters.modes.ApiParameter;
import freed.cam.apis.basecamera.parameters.modes.SettingModeParamter;
import freed.cam.apis.camera1.Camera1;
import freed.cam.apis.camera1.CameraHolder;
import freed.cam.apis.camera2.Camera2;
import freed.cam.previewpostprocessing.PreviewPostProcessingModes;
import freed.cam.ui.themenextgen.objects.SettingItemConfig;
import freed.cam.ui.themenextgen.view.button.NextGenSettingBoolItem;
import freed.cam.ui.themenextgen.view.button.NextGenSettingButton;
import freed.cam.ui.themenextgen.view.button.NextGenSettingItem;
import freed.cam.ui.themesample.settings.childs.SettingsChildMenuSDSave;
import freed.cam.ui.themesample.settings.childs.SettingsChildMenuTimeLapseFrames;
import freed.cam.ui.themesample.settings.childs.SettingsChild_FreedAe;
import freed.cam.ui.videoprofileeditor.views.VideoProfileEditorActivity;
import freed.settings.SettingKeys;
import freed.settings.SettingsManager;
import freed.settings.mode.AbstractSettingMode;
import freed.settings.mode.BooleanSettingModeInterface;
import freed.utils.Log;
import freed.utils.MediaScannerManager;

public class NextGenSettingItemFragmentAdapter extends ArrayAdapter
{
    private static final String TAG = NextGenSettingItemFragmentAdapter.class.getSimpleName();
    private SettingItemConfig[] keyList;
    private SettingsManager settingsManager;
    private CameraApiManager cameraApiManager;

    public NextGenSettingItemFragmentAdapter(@NonNull Context context, int resource) {
        super(context, resource);
    }


    public void setCameraApiManager(CameraApiManager cameraApiManager) {
        this.cameraApiManager = cameraApiManager;
    }

    public void setSettingsManager(SettingsManager settingsManager) {
        this.settingsManager = settingsManager;
    }

    public SettingItemConfig[] getKeyList() {
        return keyList;
    }

    public void setKeyList(List<SettingItemConfig> keyList) {
        List<SettingItemConfig> validList = new ArrayList<>();
        for (SettingItemConfig conf : keyList)
        {
            ParameterInterface p = null;
            AbstractSettingMode s = null;
            if (conf.getHeader() == R.string.setting_dump_vendor_keys_header && cameraApiManager.getCamera() instanceof Camera2)
                validList.add(conf);
            else if (conf.getHeader() == R.string.setting_api_header)
                validList.add(conf);
            else if (conf.getKey() == SettingKeys.USE_FREEDCAM_AE || conf.getKey() == SettingKeys.MAX_ISO || conf.getKey() == SettingKeys.MIN_ISO || conf.getKey() == SettingKeys.MAX_EXPOSURE || conf.getKey() == SettingKeys.MIN_EXPOSURE)
            {
                if(settingsManager.getGlobal(SettingKeys.PREVIEW_POST_PROCESSING_MODE) != null)
                    if (settingsManager.getGlobal(SettingKeys.PREVIEW_POST_PROCESSING_MODE).get().equals(PreviewPostProcessingModes.OpenGL.name()))
                        validList.add(conf);
            }
            else if (conf.getHeader() == R.string.setting_savecamparams_header)
            {
                if (settingsManager.getCamApi().equals(SettingsManager.API_1))
                    validList.add(conf);
            }
            else if (conf.getKey() == null)
                validList.add(conf);
            else {
                if (!conf.getFromSettingManager())
                    p = cameraApiManager.getCamera().getParameterHandler().get(conf.getKey());
                else
                    s = (AbstractSettingMode) settingsManager.get(conf.getKey());
                if (p != null || s != null)
                    validList.add(conf);
            }
        }
        this.keyList = validList.toArray(new SettingItemConfig[validList.size()]);
        notifyDataSetChanged();
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getCount() {
        if (keyList != null)
            return keyList.length;
        return 0;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        SettingItemConfig config = keyList[position];
        convertView = getViewFromKey(config,cameraApiManager.getCamera().getParameterHandler());
        if(convertView == null) {
            if (config.getKey() != null)
                Log.d(TAG,"error getView for:" + config.getKey().toString());
            else
                Log.d(TAG, "error getView for:" + getContext().getResources().getText(config.getHeader()));
        }


        return convertView;
    }

    private View getViewFromKey(SettingItemConfig key, ParameterHandler parameterHandler)
    {
        if (key.getHeader() == R.string.setting_videoprofileeditor_header)
            return NextGenSettingButton.getInstance(getContext(), key.getHeader(),key.getDescription(), onVideoEditorButtonClick);
        if (key.getHeader() == R.string.setting_sdcard_header)
            return new SettingsChildMenuSDSave(getContext(), key.getHeader(), key.getDescription());
        if (key.getHeader() == R.string.setting_savecamparams_header)
            return NextGenSettingButton.getInstance(getContext(),key.getHeader(),key.getDescription(), onSaveCameraParametersButtonClick);
        if (key.getHeader() == R.string.setting_featuredetector_header)
            return NextGenSettingButton.getInstance(getContext(),R.string.setting_featuredetector_header,R.string.setting_featuredetector_description, onRunFeatureDetectorButtonClick);
        if (key.getHeader() == R.string.setting_dump_vendor_keys_header)
            return NextGenSettingButton.getInstance(getContext(),R.string.setting_dump_vendor_keys_header,R.string.setting_dump_vendor_keys_description, onDumpCamera2VendorKeysButtonClick);
        if (key.getHeader() == R.string.setting_api_header)
            return NextGenSettingItem.getInstance(getContext(),R.string.setting_api_header,R.string.setting_api_description,new ApiParameter(settingsManager,cameraApiManager));

        if (key.getKey() != null) {
            if (key.getKey() == SettingKeys.TIMELAPSE_FRAMES)
                return new SettingsChildMenuTimeLapseFrames(getContext());
            if ((key.getKey() == SettingKeys.USE_FREEDCAM_AE))
                return new SettingsChild_FreedAe(getContext(),settingsManager.getGlobal(SettingKeys.USE_FREEDCAM_AE),key.getHeader(), key.getDescription());
            return getView(key,parameterHandler);
        }
        return null;
    }

    private View getView(SettingItemConfig key, ParameterHandler parameterHandler)
    {
        if (key.getFromSettingManager()) {
            return getViewFromSetting(key);
        } else {
            return getViewFromParameterHandler(key, parameterHandler);
        }
    }

    @Nullable
    private View getViewFromParameterHandler(SettingItemConfig key, ParameterHandler parameterHandler) {
        ParameterInterface parameterInterface = parameterHandler.get(key.getKey());
        if (parameterInterface != null) {
            if (parameterInterface instanceof BooleanSettingModeInterface)
                return NextGenSettingBoolItem.getInstance(getContext(), key.getHeader(), key.getDescription(), (BooleanSettingModeInterface) parameterInterface);
            return NextGenSettingItem.getInstance(getContext(), key.getHeader(), key.getDescription(), (AbstractParameter) parameterInterface);
        }
        return null;
    }

    @NonNull
    private ViewGroup getViewFromSetting(SettingItemConfig key) {
        AbstractSettingMode parameterInterface = (AbstractSettingMode) settingsManager.get(key.getKey());
        if (parameterInterface instanceof BooleanSettingModeInterface)
            return NextGenSettingBoolItem.getInstance(getContext(), key.getHeader(), key.getDescription(), (BooleanSettingModeInterface) parameterInterface);
        return NextGenSettingItem.getInstance(getContext(), key.getHeader(), key.getDescription(), new SettingModeParamter(key.getKey()));
    }

    public NextGenSettingButton.NextGenSettingButtonClick onVideoEditorButtonClick = new NextGenSettingButton.NextGenSettingButtonClick() {
        @Override
        public void onSettingButtonClick() {
            Intent i = new Intent(getContext(), VideoProfileEditorActivity.class);
            getContext().startActivity(i);
        }
    };

    public NextGenSettingButton.NextGenSettingButtonClick onDumpCamera2VendorKeysButtonClick = new NextGenSettingButton.NextGenSettingButtonClick() {
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onSettingButtonClick() {
            Camera2 camera2 = (Camera2) cameraApiManager.getCamera();
            CaptureResult captureResult = camera2.cameraBackroundValuesChangedListner.getCaptureResult();
            CaptureRequest captureRequest = camera2.captureSessionHandler.getCaptureRequest();
            CameraCharacteristics characteristics = camera2.getCameraHolder().characteristics;
            VendorKeyParser vendorKeyParser = new VendorKeyParser();
            VendorKeyTestLog vendorKeyTestLog = new VendorKeyTestLog(vendorKeyParser,characteristics,captureResult,captureRequest);
            vendorKeyTestLog.testKeys();
        }
    };


    public NextGenSettingButton.NextGenSettingButtonClick onRunFeatureDetectorButtonClick = new NextGenSettingButton.NextGenSettingButtonClick() {
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onSettingButtonClick() {
            cameraApiManager.runFeatureDetector();
        }
    };

    public NextGenSettingButton.NextGenSettingButtonClick onSaveCameraParametersButtonClick = new NextGenSettingButton.NextGenSettingButtonClick() {
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onSettingButtonClick() {
            if (cameraApiManager.getCamera() instanceof Camera1)
                saveCamParameters();
        }

        private void saveCamParameters()
        {
            String[] paras = null;
            CameraHolder holder = (CameraHolder) cameraApiManager.getCamera().getCameraHolder();

            paras = holder.GetCamera().getParameters().flatten().split(";");

            Arrays.sort(paras);

            FileOutputStream outputStream;
            File freedcamdir = new File(settingsManager.getAppDataFolder().getAbsolutePath());
            if (!freedcamdir.exists())
                freedcamdir.mkdirs();
            File file = new File(freedcamdir.getAbsolutePath()+"/"+ Build.MODEL + "_CameraParameters.txt");
            try {
                //file.mkdirs();
                file.createNewFile();
            } catch (IOException e) {
                Log.WriteEx(e);
            }

            try {
                outputStream = new FileOutputStream(file);
                outputStream.write((Build.MODEL + "\r\n").getBytes());
                outputStream.write((System.getProperty("os.version") + "\r\n").getBytes());
                for (String s : paras)
                {
                    outputStream.write((s+"\r\n").getBytes());
                }

                ReflectionHelper reflectionHelper = new ReflectionHelper();

                reflectionHelper.dumpClass(Camera.class,outputStream,0);
                reflectionHelper.dumpClass(MediaRecorder.class,outputStream,0);

                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                {
                    reflectionHelper.dumpClass(CameraDevice.class,outputStream,0);
                    reflectionHelper.dumpClass(CameraCharacteristics.class,outputStream,0);
                    reflectionHelper.dumpClass(CaptureRequest.class,outputStream,0);
                    reflectionHelper.dumpClass(CaptureResult.class,outputStream,0);
                    reflectionHelper.dumpClass(CameraManager.class,outputStream,0);
                }

                outputStream.close();
            } catch (Exception e) {
                Log.WriteEx(e);
            }
            MediaScannerManager.ScanMedia(getContext(),file);
        }

    };






}
