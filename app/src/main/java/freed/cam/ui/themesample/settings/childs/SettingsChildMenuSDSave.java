/*
 *
 *     Copyright (C) 2015 Ingo Fuchs
 *     This program is free software; you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation; either version 2 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License along
 *     with this program; if not, write to the Free Software Foundation, Inc.,
 *     51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 * /
 */

package freed.cam.ui.themesample.settings.childs;

import android.content.Context;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.support.v4.provider.DocumentFile;
import android.util.AttributeSet;
import android.widget.Toast;

import java.io.File;

import freed.ActivityInterface.I_OnActivityResultCallback;
import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.parameters.modes.SDModeParameter;
import freed.settings.AppSettingsManager;
import freed.utils.Log;
import freed.utils.StringUtils;

/**
 * Created by troop on 21.07.2015.
 */
public class SettingsChildMenuSDSave extends SettingsChildMenu implements I_OnActivityResultCallback
{
    private String lastval;

    public SettingsChildMenuSDSave(Context context, int headerid, int descriptionid) {
        super(context, headerid, descriptionid);
    }

    public SettingsChildMenuSDSave(Context context) {
        super(context);
    }

    public SettingsChildMenuSDSave(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void SetCameraUiWrapper(CameraWrapperInterface cameraUiWrapper)
    {
        CameraWrapperInterface cameraUiWrapper1 = cameraUiWrapper;
        SetParameter(cameraUiWrapper.getParameterHandler().SdSaveLocation);
    }

    @Override
    public void SetValue(String value)
    {
        if (VERSION.SDK_INT < VERSION_CODES.KITKAT)
        {
            CheckLowerAPI_KitKat(value);
        }
        else
        {
            if (value.equals(SDModeParameter.external))
            {
                lastval = value;
                fragment_activityInterface.ChooseSDCard(this);
            } else {
                AppSettingsManager.getInstance().SetWriteExternal(false);
                onStringValueChanged(value);
            }
        }
    }

    private void CheckLowerAPI_KitKat(String value) {
        if (value.equals(SDModeParameter.external))
        {
            boolean canWriteExternal = false;
            String path = StringUtils.GetExternalSDCARD() + StringUtils.freedcamFolder + "test.t";
            File f = new File(path);
            try {
                f.mkdirs();
                if (!f.getParentFile().exists()) {
                    boolean foldermakesuccess = f.getParentFile().mkdirs();
                }
                    f.createNewFile();
                    canWriteExternal = true;
                    f.delete();

            } catch (Exception ex) {
                Log.WriteEx(ex);
            }
            if (canWriteExternal) {
                AppSettingsManager.getInstance().SetWriteExternal(true);
                onStringValueChanged(SDModeParameter.external);
            } else {
                Toast.makeText(getContext(), "Cant write to External SD, pls insert SD or apply SD fix", Toast.LENGTH_LONG).show();
                onStringValueChanged(SDModeParameter.internal);
            }
        } else {
            AppSettingsManager.getInstance().SetWriteExternal(false);
            onStringValueChanged(value);
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
        DocumentFile f = DocumentFile.fromTreeUri(getContext(), uri);
        if (f.canWrite() && lastval.equals(SDModeParameter.external))
        {
            AppSettingsManager.getInstance().SetWriteExternal(true);
            onStringValueChanged(SDModeParameter.external);
        }
        else
        {
            AppSettingsManager.getInstance().SetWriteExternal(false);
            onStringValueChanged(SDModeParameter.internal);
        }
        lastval = "";
    }
}
