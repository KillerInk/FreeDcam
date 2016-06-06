package com.freedcam.ui.themesample.views.menu;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.support.v4.provider.DocumentFile;
import android.util.AttributeSet;
import android.widget.Toast;

import com.freedcam.apis.basecamera.camera.AbstractCameraUiWrapper;
import com.freedcam.apis.basecamera.camera.parameters.modes.SDModeParameter;
import com.freedcam.ui.I_Activity;
import com.freedcam.utils.Logger;
import com.freedcam.utils.StringUtils;

import java.io.File;

/**
 * Created by troop on 21.07.2015.
 */
public class MenuItemSDSave extends MenuItem implements I_Activity.I_OnActivityResultCallback
{
    final String internal = "Internal";
    final String external ="External";
    private AbstractCameraUiWrapper cameraUiWrapper;
    private String lastval;

    public MenuItemSDSave(Context context) {
        super(context);
    }

    public MenuItemSDSave(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void SetCameraUiWrapper(AbstractCameraUiWrapper cameraUiWrapper)
    {
        this.cameraUiWrapper = cameraUiWrapper;
        super.SetParameter(cameraUiWrapper.parametersHandler.SdSaveLocation);
    }

    @Override
    public void SetValue(String value)
    {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT)
        {
            CheckLowerAPI_KitKat(value);
        }
        else
        {
            if (value.equals(SDModeParameter.external))
            {
                lastval = value;
                i_activity.ChooseSDCard(this);
            } else {
                appSettingsManager.SetWriteExternal(false);
                onValueChanged(value);
            }
        }
    }

    private void CheckLowerAPI_KitKat(String value) {
        if (value.equals(SDModeParameter.external))
        {
            boolean canWriteExternal = false;
            final String path = StringUtils.GetExternalSDCARD() + StringUtils.freedcamFolder + "test.t";
            final File f = new File(path);
            try {
                f.mkdirs();
                if (!f.getParentFile().exists()) {
                    boolean foldermakesuccess = f.getParentFile().mkdirs();
                }
                    f.createNewFile();
                    canWriteExternal = true;
                    f.delete();

            } catch (Exception ex) {
                Logger.exception(ex);
            }
            if (canWriteExternal) {
                appSettingsManager.SetWriteExternal(true);
                onValueChanged(SDModeParameter.external);
            } else {
                Toast.makeText(context, "Cant write to External SD, pls insert SD or apply SD fix", Toast.LENGTH_LONG).show();
                onValueChanged(SDModeParameter.internal);
            }
        } else {
            appSettingsManager.SetWriteExternal(false);
            onValueChanged(value);
        }
    }

    @Override
    public String[] GetValues() {
        return super.GetValues();
    }

    //content://com.android.externalstorage.documents/document/59EC-12E2%3ADCIM%2FFreeDcam%2Ftest.txt
    @Override
    public void onActivityResultCallback(Uri uri)
    {
        DocumentFile f = DocumentFile.fromTreeUri(context, uri);
        if (f.canWrite() && lastval.equals(SDModeParameter.external))
        {
            appSettingsManager.SetWriteExternal(true);
            onValueChanged(SDModeParameter.external);
        }
        else
        {
            appSettingsManager.SetWriteExternal(false);
            onValueChanged(SDModeParameter.internal);
        }
        lastval = "";
    }
}
