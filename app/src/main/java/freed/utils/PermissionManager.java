package freed.utils;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;

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
        SdCard_Camera,
    }

    private final String TAG = PermissionManager.class.getSimpleName();

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
            case SdCard_Camera:
                return isPermissionGranted(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA});
        }
        return false;
    }

    private boolean isPermissionGranted(String[] permission)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (String s : permission) {
                if (activity.checkSelfPermission(s) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
            return true;
        }
        return true;
    }

    public void requestPermission(Permissions permission)
    {
        switch (permission) {
            case SdCard:
                requestPermission(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE});
                break;
            case Camera:
                requestPermission(new String[]{Manifest.permission.CAMERA});
                break;
            case Location:
                requestPermission(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION});
                break;
            case Wifi:
                requestPermission(new String[]{Manifest.permission.ACCESS_WIFI_STATE, Manifest.permission.CHANGE_WIFI_STATE});
                break;
            case RecordAudio:
                requestPermission(new String[]{Manifest.permission.RECORD_AUDIO});
                break;
            case SdCard_Camera:
                requestPermission(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA});
        }
    }

    private void requestPermission(String[] permissions)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activity.requestPermissions(permissions, 1);
        }
    }

}
