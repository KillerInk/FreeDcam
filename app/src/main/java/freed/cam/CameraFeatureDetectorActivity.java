package freed.cam;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

import com.troop.freedcam.BuildConfig;
import com.troop.freedcam.R;

import freed.ActivityAbstract;
import freed.cam.featuredetector.AbstractFeatureDetectorTask;
import freed.cam.featuredetector.Camera1FeatureDetectorTask;
import freed.cam.featuredetector.Camera2FeatureDetectorTask;
import freed.utils.AppSettingsManager;
import freed.utils.LocationHandler;
import freed.utils.Log;
import freed.utils.PermissionHandler;
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
    }

    @Override
    protected void setContentToView() {
        setContentView(R.layout.camerafeaturedetector);
    }

    @Override
    protected void initOnCreate() {
        super.initOnCreate();
        loggerview = (TextView)findViewById(R.id.textview_log);
        loggerview.setMovementMethod(new ScrollingMovementMethod());
        if (getPermissionHandler().hasCameraPermission(onCameraPermission)) {
            onCameraPermission.permissionGranted(true);
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

    private PermissionHandler.PermissionCallback onCameraPermission = new PermissionHandler.PermissionCallback() {
        @Override
        public void permissionGranted(boolean granted) {
            if (granted) {
                if (Build.VERSION.SDK_INT >= 21) {
                    new Camera2FeatureDetectorTask(camera2Listner,getAppSettings(),getContext()).execute("");
                }
                else {
                    new Camera1FeatureDetectorTask(camera1Listner, getAppSettings()).execute("");
                }
            }
            else {
                finish();
            }
        }
    };


    private void sendLog(String log)
    {
        String tmp = log + " \n"+ loggerview.getText().toString();
        loggerview.setText(tmp);
    }

    private void startFreedcam()
    {
        getAppSettings().setAppVersion(BuildConfig.VERSION_CODE);
        getAppSettings().setAreFeaturesDetected(true);
        Intent intent = new Intent(this, ActivityFreeDcamMain.class);
        startActivity(intent);
        this.finish();
    }

    private AbstractFeatureDetectorTask.ProgressUpdate camera1Listner = new AbstractFeatureDetectorTask.ProgressUpdate() {
        @Override
        public void onProgessUpdate(String msg) {
            Log.d(TAG, msg);
            sendLog(msg);
        }

        @Override
        public void onTaskEnd(String msg) {
            if(getAppSettings().IsCamera2FullSupported())
                getAppSettings().setCamApi(AppSettingsManager.API_2);
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
            new Camera1FeatureDetectorTask(camera1Listner, getAppSettings()).execute("");
        }
    };

}
