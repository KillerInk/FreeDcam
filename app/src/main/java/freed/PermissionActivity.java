package freed;

import android.os.Bundle;


import androidx.annotation.Nullable;

import freed.utils.Log;
import freed.utils.PermissionManager;


public abstract class PermissionActivity extends HideNavBarActivity {

    private final String TAG = PermissionActivity.class.getSimpleName();
    public enum AppState
    {
        Created,
        Resumed,
        Paused,
        Destroyed,
    }
    public AppState currentState = AppState.Destroyed;

    private PermissionManager permissionManager;
    public PermissionManager getPermissionManager() {
        return permissionManager;
    }
    private boolean onCreatePermissioGrantedDidRun = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentState = AppState.Created;
        setContentToView();

    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        permissionManager =new PermissionManager(this);

        permissionManager.hasCameraAndSdPermission(logSDPermission);
    }

    protected abstract void setContentToView();

    @Override
    public void onRequestPermissionsResult(int requestCode,  String[] permissions,  int[] grantResults)
    {
        permissionManager.onRequestPermissionsResult(requestCode,permissions,grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private PermissionManager.PermissionCallback logSDPermission = granted -> {
        Log.d(TAG, "sd permission granted:" + granted);
        if (granted) {

            onCreatePermissionGranted();
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        currentState = AppState.Resumed;
        if (permissionManager.isPermissionGranted(PermissionManager.Permissions.Camera) && permissionManager.isPermissionGranted(PermissionManager.Permissions.SdCard)) {
            if (!onCreatePermissioGrantedDidRun)
                onCreatePermissionGranted();
            onResumePermissionGranted();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        currentState = AppState.Paused;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        currentState = AppState.Destroyed;
    }

    public void onCreatePermissionGranted()
    {
        onCreatePermissioGrantedDidRun = true;
    }

    public abstract void onResumePermissionGranted();
}
