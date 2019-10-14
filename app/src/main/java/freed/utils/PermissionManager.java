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

    public boolean hasCameraPermission(PermissionCallback callbackToReturn)
    {
        return hasPermission(callbackToReturn, Manifest.permission.CAMERA);
    }

    public boolean hasRecordAudioPermission(PermissionCallback callbackToReturn)
    {
        return hasPermission(callbackToReturn, Manifest.permission.RECORD_AUDIO);
    }

    public boolean hasExternalSDPermission(PermissionCallback callbackToReturn)
    {
        return hasPermission(callbackToReturn, new String[]{ Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE});
    }

    public boolean hasLocationPermission()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            return activity.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        else
            return true;
    }

    public boolean hasLocationPermission(PermissionCallback callbackToReturn)
    {
        this.callbackToReturn = callbackToReturn;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if (activity.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED)
            {
                Log.d(TAG, "Request LocationPermission");
                activity.requestPermissions(new String[]{
                        Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},1);
                if (callbackToReturn != null)
                    callbackToReturn.permissionGranted(false);
                return false;
            }
        }
        if (callbackToReturn != null)
            callbackToReturn.permissionGranted(true);
        return true;
    }

    public boolean hasWifiPermission(PermissionCallback callbackToReturn) {
        this.callbackToReturn = callbackToReturn;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if (activity.checkSelfPermission(Manifest.permission.ACCESS_WIFI_STATE)
                    != PackageManager.PERMISSION_GRANTED)
            {
                Log.d(TAG, "Request wifiPermission");
                activity.requestPermissions(new String[]{
                        Manifest.permission.ACCESS_WIFI_STATE, Manifest.permission.CHANGE_WIFI_STATE},1);
                if (callbackToReturn != null)
                    callbackToReturn.permissionGranted(false);
                return false;
            }
        }
        if (callbackToReturn != null)
            callbackToReturn.permissionGranted(true);
        return true;
    }

    private boolean hasPermission(PermissionCallback callbackToReturn, String permission)
    {
        this.callbackToReturn = callbackToReturn;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if (activity.checkSelfPermission(permission)
                    != PackageManager.PERMISSION_GRANTED)
            {
                Log.d(TAG, "Request Permission:"+permission);
                activity.requestPermissions(new String[]{
                        permission},1);
                return false;
            }
        }
        if (callbackToReturn != null)
            callbackToReturn.permissionGranted(true);
        return true;
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
