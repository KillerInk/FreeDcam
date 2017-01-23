package freed.cam;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.widget.TextView;

import com.troop.freedcam.R;

import freed.ActivityAbstract;
import freed.cam.featuredetector.AbstractFeatureDetectorTask;
import freed.cam.featuredetector.Camera1FeatureDetectorTask;
import freed.cam.featuredetector.Camera2FeatureDetectorTask;
import freed.utils.LocationHandler;
import freed.viewer.holder.FileHolder;

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
        loggerview.setMovementMethod(new ScrollingMovementMethod());
        if (hasCameraPermission()) {
            new Camera1FeatureDetectorTask(camera1Listner,getAppSettings()).execute("");
        }

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
            new Camera1FeatureDetectorTask(camera1Listner,getAppSettings()).execute("");
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

    private void startFreedcam()
    {
        getAppSettings().setAreFeaturesDetected(true);
        Intent intent = new Intent(this, ActivityFreeDcamMain.class);
        startActivity(intent);
        this.finish();
    }

    private AbstractFeatureDetectorTask.ProgressUpdate camera1Listner = new AbstractFeatureDetectorTask.ProgressUpdate() {
        @Override
        public void onProgessUpdate(String msg) {
            sendLog(msg);
            Log.d(TAG, msg);
        }

        @Override
        public void onTaskEnd(String msg) {
            getAppSettings().SetCurrentCamera(0);
            if (Build.VERSION.SDK_INT >= 21) {
                new Camera2FeatureDetectorTask(camera2Listner,getAppSettings(),getContext()).execute("");
            }
            else {
                sendLog("No camera2");
                startFreedcam();
            }
        }
    };

    private AbstractFeatureDetectorTask.ProgressUpdate camera2Listner = new AbstractFeatureDetectorTask.ProgressUpdate() {
        @Override
        public void onProgessUpdate(String msg) {
            sendLog(msg);
            Log.d(TAG, msg);
        }

        @Override
        public void onTaskEnd(String msg) {
            getAppSettings().SetCurrentCamera(0);
            startFreedcam();
        }
    };

}
