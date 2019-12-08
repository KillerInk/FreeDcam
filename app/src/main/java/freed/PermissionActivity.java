package freed;

import android.os.Bundle;


import freed.utils.PermissionManager;


public abstract class PermissionActivity extends HideNavBarActivity {


    private PermissionManager permissionManager;
    public PermissionManager getPermissionManager() {
        return permissionManager;
    }
    private boolean onCreatePermissioGrantedDidRun = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentToView();
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
        if (granted) {

            onCreatePermissionGranted();
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        if (permissionManager.hasCameraAndSdPermission(null)) {
            if (!onCreatePermissioGrantedDidRun)
                onCreatePermissionGranted();
            onResumePermissionGranted();
        }
    }

    public void onCreatePermissionGranted()
    {
        onCreatePermissioGrantedDidRun = true;
    }

    public abstract void onResumePermissionGranted();
}
