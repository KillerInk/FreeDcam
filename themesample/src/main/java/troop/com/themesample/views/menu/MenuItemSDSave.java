package troop.com.themesample.views.menu;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Toast;

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
        super.SetParameter(cameraUiWrapper.camParametersHandler.SdSaveLocation);
    }

    @Override
    public void SetValue(String value) {
        if (value.equals(SDModeParameter.external))
        {


            boolean canWriteExternal = false;
            final String path = StringUtils.GetExternalSDCARD() + StringUtils.freedcamFolder + "test.t";
            final File f = new File(path);
            try {
                f.mkdirs();
                f.createNewFile();
                canWriteExternal = true;
                f.delete();
            }
            catch (Exception ex)
            {
                final String path2 = "/storage/sdcard1" + StringUtils.freedcamFolder + "test.t";
                final File f2 = new File(path2);
                try {
                    f2.mkdirs();
                    f2.createNewFile();
                    canWriteExternal = true;
                    f2.delete();
                    if (canWriteExternal) {
                        Toast.makeText(context,"Success at "
                                +path2, Toast.LENGTH_LONG).show();
                    }
                }
                catch (Exception ex2)
                {
                    canWriteExternal =false;
                }

            }
            if (canWriteExternal) {
                appSettingsManager.SetWriteExternal(true);
                onValueChanged(SDModeParameter.external);
            }
            else {
                Toast.makeText(context,"Cant write to External SD, pls insert SD or apply SD fix", Toast.LENGTH_LONG).show();
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
