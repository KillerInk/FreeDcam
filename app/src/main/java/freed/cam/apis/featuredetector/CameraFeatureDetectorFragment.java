package freed.cam.apis.featuredetector;

import android.hardware.camera2.CameraCharacteristics;
import android.os.Build;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.troop.freedcam.BuildConfig;
import com.troop.freedcam.R;

import freed.FreedApplication;
import freed.cam.previewpostprocessing.PreviewPostProcessingModes;
import freed.image.ImageManager;
import freed.image.ImageTask;
import freed.renderscript.RenderScriptManager;
import freed.settings.SettingKeys;
import freed.settings.SettingsManager;
import freed.utils.Log;

/**
 * Created by troop on 16.07.2017.
 */

public class CameraFeatureDetectorFragment extends Fragment implements FeatureDetectorHandler.FdHandlerInterface {

    public interface FeatureDetectorEvents
    {
        void featuredetectorDone();
    }

    private TextView loggerview;
    private FeatureDetectorEvents featureDetectorEvents;
    private final String TAG = CameraFeatureDetectorFragment.class.getSimpleName();
    private CameraFeatureRunner featureRunner;
    private FeatureDetectorHandler handler = new FeatureDetectorHandler(this);

    public void setFeatureDetectorDoneListner(FeatureDetectorEvents events)
    {
        this.featureDetectorEvents = events;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        Log.d(TAG, "onCreateView");
        return inflater.inflate(R.layout.camerafeaturedetector, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated");
        loggerview = (TextView)view.findViewById(R.id.textview_log);
        loggerview.setMovementMethod(new ScrollingMovementMethod());

    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        if (featureRunner == null && !SettingsManager.getInstance().getAreFeaturesDetected() && this.isAdded()){
            featureRunner = new CameraFeatureRunner();
            ImageManager.putImageLoadTask(featureRunner);
        }
        else
            handler.obtainMessage(FeatureDetectorHandler.MSG_STARTFREEDCAM).sendToTarget();
    }

    @Override
    public void sendLog(String log)
    {
        String tmp = log + " \n"+ loggerview.getText().toString();
        loggerview.setText(tmp);
    }

    @Override
    public void startFreedcam()
    {
        featureRunner = null;
        featureDetectorEvents.featuredetectorDone();
        Log.d(TAG,"startFreeDcam");
    }


    private class CameraFeatureRunner extends ImageTask
    {
        @Override
        public boolean process() {
            Log.d(TAG, "CameraFeatureRunner process");
            SettingsManager.getInstance().setCamApi(SettingsManager.API_SONY);
            Camera2FeatureDetectorTask task  = null;
            Camera1FeatureDetectorTask task1 = null;

            if (Build.VERSION.SDK_INT >= 21) {
                task =  new Camera2FeatureDetectorTask();
                task.detect();
            }
            task1 = new Camera1FeatureDetectorTask();
            task1.detect();
            if (SettingsManager.getInstance().hasCamera2Features()) {
                if (task.hwlvl == CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LEGACY)
                    SettingsManager.getInstance().setCamApi(SettingsManager.API_1);
                else
                    SettingsManager.getInstance().setCamApi(SettingsManager.API_2);
            }
            setGlobalDefaultSettings();
            SettingsManager.getInstance().setAppVersion(BuildConfig.VERSION_CODE);
            SettingsManager.getInstance().setAreFeaturesDetected(true);
            SettingsManager.getInstance().save();
            Log.d(TAG, "Feature Detection done! Start FreeDcam Api: " + SettingsManager.getInstance().getCamApi() + " app version:" + SettingsManager.getInstance().getAppVersion());
            handler.obtainMessage(FeatureDetectorHandler.MSG_STARTFREEDCAM).sendToTarget();
            return false;
        }
    }

    private void setGlobalDefaultSettings()
    {
        if (RenderScriptManager.isSupported()) {
            SettingsManager.getGlobal(SettingKeys.PREVIEW_POST_PROCESSING_MODE).setValues(new String[]{PreviewPostProcessingModes.off.name(),PreviewPostProcessingModes.RenderScript.name()/*,"OpenGL"*/});
            SettingsManager.getGlobal(SettingKeys.PREVIEW_POST_PROCESSING_MODE).set(PreviewPostProcessingModes.off.name());
            SettingsManager.getGlobal(SettingKeys.PREVIEW_POST_PROCESSING_MODE).setIsSupported(true);
            SettingsManager.getGlobal(SettingKeys.FOCUSPEAK_COLOR).setValues(FreedApplication.getContext().getResources().getStringArray(R.array.focuspeakColors));
            SettingsManager.getGlobal(SettingKeys.FOCUSPEAK_COLOR).set(SettingsManager.getGlobal(SettingKeys.FOCUSPEAK_COLOR).getValues()[0]);
            SettingsManager.getGlobal(SettingKeys.FOCUSPEAK_COLOR).setIsSupported(true);
        }
        else
            SettingsManager.getGlobal(SettingKeys.PREVIEW_POST_PROCESSING_MODE).setIsSupported(false);

        SettingsManager.getGlobal(SettingKeys.GuideList).setValues(FreedApplication.getContext().getResources().getStringArray(R.array.guidelist));
        SettingsManager.getGlobal(SettingKeys.GuideList).set(SettingsManager.getGlobal(SettingKeys.GuideList).getValues()[0]);

        SettingsManager.getGlobal(SettingKeys.LOCATION_MODE).setIsSupported(true);
    }
}
