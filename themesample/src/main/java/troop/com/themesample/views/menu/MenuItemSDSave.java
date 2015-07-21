package troop.com.themesample.views.menu;

import android.content.Context;
import android.util.AttributeSet;

import com.troop.freedcam.i_camera.AbstractCameraUiWrapper;
import com.troop.freedcam.i_camera.parameters.SDModeParameter;
import com.troop.freedcam.utils.StringUtils;

import java.io.File;

/**
 * Created by troop on 21.07.2015.
 */
public class MenuItemSDSave extends MenuItem
{
    final String internal = "Internal";
    final String external ="External";
    AbstractCameraUiWrapper cameraUiWrapper;

    public MenuItemSDSave(Context context) {
        super(context);
    }

    public MenuItemSDSave(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void SetCameraUiWrapper(AbstractCameraUiWrapper cameraUiWrapper)
    {
        this.cameraUiWrapper = cameraUiWrapper;
        super.SetParameter(new SDModeParameter(null, appSettingsManager));
    }

    @Override
    public void SetValue(String value) {
        if (value.equals(SDModeParameter.external))
        {
            boolean canWriteExternal = false;
            final String path = StringUtils.GetExternalSDCARD() + StringUtils.freedcamFolder + "/test.t";
            final File f = new File(path);
            try {
                f.createNewFile();
                canWriteExternal = true;
                f.delete();
            }
            catch (Exception ex)
            {
                canWriteExternal =false;
            }
            if (canWriteExternal) {
                appSettingsManager.SetWriteExternal(true);
                onValueChanged(SDModeParameter.external);
            }
            else {
                cameraUiWrapper.onCameraError("Cant write on External SD, pls apply SD fix");
                onValueChanged(SDModeParameter.internal);
            }
        }
        else {
            appSettingsManager.SetWriteExternal(false);
            onValueChanged(value);
        }
    }

    @Override
    public String[] GetValues() {
        return super.GetValues();
    }
}
