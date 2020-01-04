package freed.utils;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;


import java.util.Arrays;

/**
 * Created by troop on 09.03.2017.
 */

public class PermissionManager
{

    public enum Permissions
    {
        SdCard,
        Camera,
        Location,
        Wifi,
        RecordAudio,
    }

    private final String TAG = PermissionManager.class.getSimpleName();
    private PermissionCallback callbackToReturn;

    public interface PermissionCallback
    {
        void permissionGranted(boolean granted);
    }

    Activity activity;

    public PermissionManager(Activity activity)
    {
        this.activity = activity;
    }

    public boolean isPermissionGranted(Permissions permissions)
    {
        switch (permissions) {
            case SdCard:
                return isPermissionGranted(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE});
            case Camera:
                return isPermissionGranted(new String[]{Manifest.permission.CAMERA});
            case Location:
                return isPermissionGranted(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION});
            case Wifi:
                return isPermissionGranted(new String[]{Manifest.permission.ACCESS_WIFI_STATE, Manifest.permission.CHANGE_WIFI_STATE});
            case RecordAudio:
                return isPermissionGranted(new String[]{Manifest.permission.RECORD_AUDIO});
        }
        return false;
    }

    private boolean isPermissionGranted(String[] permission)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            boolean granted = true;
            for (String s : permission) {
                if (activity.checkSelfPermission(s) != PackageManager.PERMISSION_GRANTED) {
                    granted = false;
                    break;
                }
            }
            return granted;
        }
        return true;
    }

    public void requestPermission(Permissions permission,PermissionCallback callbackToReturn)
    {
        switch (permission) {
            case SdCard:
                requestPermission(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},callbackToReturn);
                break;
            case Camera:
                requestPermission(new String[]{Manifest.permission.CAMERA},callbackToReturn);
                break;
            case Location:
                requestPermission(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION},callbackToReturn);
                break;
            case Wifi:
                requestPermission(new String[]{Manifest.permission.ACCESS_WIFI_STATE, Manifest.permission.CHANGE_WIFI_STATE},callbackToReturn);
                break;
            case RecordAudio:
                requestPermission(new String[]{Manifest.permission.RECORD_AUDIO}, callbackToReturn);
                break;
        }
    }

    private void requestPermission(String[] permissions,PermissionCallback callbackToReturn)
    {
        this.callbackToReturn = callbackToReturn;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activity.requestPermissions(permissions, 1);
        }
    }


    public boolean hasCameraAndSdPermission(PermissionCallback callbackToReturn)
    {
        return hasPermission(callbackToReturn, new String[]{
                Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE
        });
    }

    private boolean hasPermission(PermissionCallback callbackToReturn, String[] permission)
    {
        this.callbackToReturn = callbackToReturn;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            boolean granted = true;
            for (String s : permission)
            {
                if(activity.checkSelfPermission(s)!= PackageManager.PERMISSION_GRANTED) {
                    granted = false;
                    break;
                }

            }
            if (!granted)
            {
                Log.d(TAG, "Request Permission:"+ Arrays.toString(permission));
                activity.requestPermissions(permission,1);
                return false;
            }
        }
        if (callbackToReturn != null)
            callbackToReturn.permissionGranted(true);
        return true;
    }


    public void onRequestPermissionsResult(int requestCode,  String[] permissions,  int[] grantResults)
    {
        if (callbackToReturn == null)
            return;
        boolean allGranted = true;
        for (int i = 0; i < permissions.length;i++) {
            String perm = permissions[i];
            allGranted = grantResults[i] == PackageManager.PERMISSION_GRANTED;
            if (!allGranted)
                break;
        }
        callbackToReturn.permissionGranted(allGranted);
        callbackToReturn = null;
    }
}
