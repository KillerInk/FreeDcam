package freed.cam.featuredetector;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.troop.freedcam.BuildConfig;
import com.troop.freedcam.R;

import freed.utils.AppSettingsManager;
import freed.utils.Log;

/**
 * Created by troop on 16.07.2017.
 */

public class CameraFeatureDetectorFragment extends Fragment {

    public interface FeatureDetectorEvents
    {
        void featuredetectorDone();
    }

    private TextView loggerview;
    private AppSettingsManager appSettingsManager;
    private FeatureDetectorEvents featureDetectorEvents;
    private final String TAG = CameraFeatureDetectorFragment.class.getSimpleName();

    public void setAppSettingsManagerAndListner(AppSettingsManager appSettingsManager, FeatureDetectorEvents events)
    {
        this.appSettingsManager = appSettingsManager;
        this.featureDetectorEvents = events;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.camerafeaturedetector, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loggerview = (TextView)view.findViewById(R.id.textview_log);
        loggerview.setMovementMethod(new ScrollingMovementMethod());

    }

    @Override
    public void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= 21) {
            new Camera2FeatureDetectorTask(camera2Listner,appSettingsManager,getContext()).execute("");
        }
        else {
            new Camera1FeatureDetectorTask(camera1Listner, appSettingsManager).execute("");
        }
    }

    private void sendLog(String log)
    {
        String tmp = log + " \n"+ loggerview.getText().toString();
        loggerview.setText(tmp);
    }

    private void startFreedcam()
    {
        appSettingsManager.setAppVersion(BuildConfig.VERSION_CODE);
        appSettingsManager.setAreFeaturesDetected(true);
       /* Intent intent = new Intent(this, ActivityFreeDcamMain.class);
        startActivity(intent);*/
        featureDetectorEvents.featuredetectorDone();
        Log.d(TAG,"startFreeDcam");
    }

    private AbstractFeatureDetectorTask.ProgressUpdate camera1Listner = new AbstractFeatureDetectorTask.ProgressUpdate() {
        @Override
        public void onProgessUpdate(String msg) {
            Log.d(TAG, msg);
            sendLog(msg);
        }

        @Override
        public void onTaskEnd(String msg) {
            if(appSettingsManager.hasCamera2Features())
                appSettingsManager.setCamApi(AppSettingsManager.API_2);
            Log.d(TAG,"startFreeDcam from cam1Listner");
            startFreedcam();
        }
    };

    private AbstractFeatureDetectorTask.ProgressUpdate camera2Listner = new AbstractFeatureDetectorTask.ProgressUpdate() {
        @Override
        public void onProgessUpdate(String msg) {
            Log.d(TAG, msg);
            sendLog(msg);

        }

        @Override
        public void onTaskEnd(String msg) {
            Log.d(TAG,"Camera2 featuresdetected, lookup cam1");
            new Camera1FeatureDetectorTask(camera1Listner, appSettingsManager).execute("");
        }
    };
}
