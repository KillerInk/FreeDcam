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

package freed.cam.apis.basecamera.parameters.modes;

import android.content.Context;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.widget.Toast;

import androidx.documentfile.provider.DocumentFile;

import java.io.File;

import dagger.hilt.android.internal.managers.FragmentComponentManager;
import freed.ActivityInterface;
import freed.FreedApplication;
import freed.cam.ActivityFreeDcamMain;
import freed.cam.apis.basecamera.parameters.AbstractParameter;
import freed.settings.SettingKeys;
import freed.utils.Log;
import freed.utils.StringUtils;

/**
 * Created by troop on 21.07.2015.
 */
public class SDModeParameter extends AbstractParameter  implements ActivityInterface.I_OnActivityResultCallback
{
    public static final String internal = "Internal";
    public static final String external ="External";
    private String lastval;
    private Context context;

    public SDModeParameter() {
        super(SettingKeys.SD_SAVE_LOCATION);
    }

    public void setContext(Context context)
    {
        this.context = context;
    }


    @Override
    public ViewState getViewState() {
        try {
            if (VERSION.SDK_INT <= VERSION_CODES.LOLLIPOP) {
                File file = new File(StringUtils.GetExternalSDCARD());
                if (file.exists())
                    return ViewState.Visible;
            }
            else
                return ViewState.Visible;
        }
        catch (Exception ex)
        {
            return ViewState.Hidden;
        }
        return super.getViewState();
    }

    @Override
    public void setStringValue(String valueToSet, boolean setToCamera)
    {
        setValue(valueToSet,setToCamera);
    }

    @Override
    protected void setValue(String value, boolean setToCamera) {
        if (VERSION.SDK_INT < VERSION_CODES.KITKAT)
        {
            CheckLowerAPI_KitKat(value);
        }
        else
        {
            if (value.equals(SDModeParameter.external))
            {
                lastval = value;
                ActivityFreeDcamMain activity = (ActivityFreeDcamMain) FragmentComponentManager.findActivity(context);
                activity.ChooseSDCard(this::onActivityResultCallback);
            } else {
                settingsManager.SetWriteExternal(false);
                //onStringValueChanged(value);
            }
        }
        fireStringValueChanged(value);
    }

    @Override
    public String getStringValue()
    {
        if (settingsManager.GetWriteExternal())
            return external;
        else
            return internal;
    }

    @Override
    public String[] getStringValues() {
        return new String[] {internal, external};
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
                settingsManager.SetWriteExternal(true);
                //onStringValueChanged(SDModeParameter.external);
            } else {
                Toast.makeText(context, "Cant write to External SD, pls insert SD or apply SD fix", Toast.LENGTH_LONG).show();
                //onStringValueChanged(SDModeParameter.internal);
            }
        } else {
            settingsManager.SetWriteExternal(false);
            //onStringValueChanged(value);
        }
    }

    //content://com.android.externalstorage.documents/document/59EC-12E2%3ADCIM%2FFreeDcam%2Ftest.txt
    @Override
    public void onActivityResultCallback(Uri uri)
    {
        DocumentFile f = DocumentFile.fromTreeUri(context, uri);
        //onStringValueChanged(SDModeParameter.external);
        //onStringValueChanged(SDModeParameter.internal);
        settingsManager.SetWriteExternal(f.canWrite() && lastval.equals(SDModeParameter.external));
        lastval = "";
    }

}
