package freed.cam.apis.featuredetector;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.troop.freedcam.BuildConfig;
import com.troop.freedcam.R;

import java.lang.ref.WeakReference;

import freed.image.ImageManager;
import freed.image.ImageTask;
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
        if (featureRunner == null){
            featureRunner = new CameraFeatureRunner();
            ImageManager.putImageLoadTask(featureRunner);
        }
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
        SettingsManager.getInstance().setAppVersion(BuildConfig.VERSION_CODE);
        SettingsManager.getInstance().setAreFeaturesDetected(true);
        featureDetectorEvents.featuredetectorDone();
        Log.d(TAG,"startFreeDcam");
    }



    private AbstractFeatureDetectorTask.ProgressUpdate cameraListner = new AbstractFeatureDetectorTask.ProgressUpdate() {
        @Override
        public void onProgessUpdate(String msg) {
            Log.d(TAG, msg);
            handler.obtainMessage(FeatureDetectorHandler.MSG_SENDLOG, msg).sendToTarget();

        }

        @Override
        public void onTaskEnd(String msg) {
        }
    };

    private class CameraFeatureRunner extends ImageTask
    {
        @Override
        public boolean process() {
            AbstractFeatureDetectorTask task  = null;
            if (Build.VERSION.SDK_INT >= 21) {
                task =  new Camera2FeatureDetectorTask(cameraListner,getContext());
                task.detect();
            }
            task = new Camera1FeatureDetectorTask(cameraListner);
            task.detect();
            if (SettingsManager.getInstance().hasCamera2Features())
                SettingsManager.getInstance().setCamApi(SettingsManager.API_2);
            handler.obtainMessage(FeatureDetectorHandler.MSG_STARTFREEDCAM).sendToTarget();
            return false;
        }
    }
}
