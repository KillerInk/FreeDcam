package freed;

import android.os.Bundle;
import android.support.annotation.NonNull;

import freed.utils.PermissionManager;


public abstract class PermissionActivity extends HideNavBarActivity {


    private PermissionManager permissionManager;
    public PermissionManager getPermissionManager() {
        return permissionManager;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentToView();
        permissionManager =new PermissionManager(this);

        permissionManager.hasCameraAndSdPermission(logSDPermission);
    }

    protected abstract void setContentToView();

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        permissionManager.onRequestPermissionsResult(requestCode,permissions,grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private PermissionManager.PermissionCallback logSDPermission = granted -> {
        if (granted) {

            onCreatePermissionGranted();
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        if (permissionManager.hasCameraAndSdPermission(null))
            onResumePermissionGranted();
    }

    public abstract void onCreatePermissionGranted();

    public abstract void onResumePermissionGranted();
}
