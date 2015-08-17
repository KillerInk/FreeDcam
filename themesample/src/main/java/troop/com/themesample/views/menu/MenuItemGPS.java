package troop.com.themesample.views.menu;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.AttributeSet;

import com.troop.freedcam.i_camera.AbstractCameraHolder;
import com.troop.freedcam.i_camera.AbstractCameraUiWrapper;
import com.troop.freedcam.i_camera.parameters.LocationParameter;
import com.troop.freedcam.ui.AppSettingsManager;

/**
 * Created by troop on 21.07.2015.
 */
public class MenuItemGPS extends MenuItem
{
    public MenuItemGPS(Context context) {
        super(context);
    }

    public MenuItemGPS(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void SetCameraUIWrapper(AbstractCameraUiWrapper cameraUiWrapper)
    {
        super.SetParameter(cameraUiWrapper.camParametersHandler.locationParameter);

    }
}
