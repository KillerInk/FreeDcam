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


import android.text.TextUtils;

import com.troop.freedcam.R;

import freed.FreedApplication;
import freed.cam.ActivityFreeDcamMain;
import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.parameters.AbstractParameter;
import freed.cam.previewpostprocessing.PreviewController;
import freed.cam.previewpostprocessing.PreviewPostProcessingModes;
import freed.settings.SettingKeys;
import freed.settings.mode.ApiBooleanSettingMode;
import freed.settings.mode.BooleanSettingModeInterface;
import freed.utils.StringUtils;

/**
 * Created by troop on 10.09.2015.
 */
public class FocusPeakMode extends AbstractParameter implements BooleanSettingModeInterface {
    protected PreviewController previewController;

    protected SettingKeys.Key<ApiBooleanSettingMode> settingMode;
    public FocusPeakMode(CameraWrapperInterface cameraUiWrapper, SettingKeys.Key<ApiBooleanSettingMode> settingMode) {
        super(cameraUiWrapper, settingMode);
        previewController = ActivityFreeDcamMain.previewController();
        this.settingMode = settingMode;
    }

    @Override
    public String getStringValue()
    {
        if (cameraUiWrapper == null && !settingsManager.get(settingMode).get())
            return FreedApplication.getStringFromRessources(R.string.off_);
        return FreedApplication.getStringFromRessources(R.string.on_);
    }

    @Override
    public ViewState getViewState() {
        if (!settingsManager.getGlobal(SettingKeys.PREVIEW_POST_PROCESSING_MODE).get().equals(PreviewPostProcessingModes.off.name()))
            return ViewState.Visible;
        else
            return ViewState.Hidden;
    }

    @Override
    public void setStringValue(String valueToSet, boolean setToCamera)
    {
        currentString = valueToSet;
        boolean toset = false;
        if (valueToSet.equals(FreedApplication.getStringFromRessources(R.string.on_)))
        {
            toset = true;
        }
        previewController.setFocusPeak(toset);
        settingsManager.get(settingMode).set(toset);
        fireStringValueChanged(valueToSet);

    }

    @Override
    public String[] getStringValues() {
        return new String[] { FreedApplication.getStringFromRessources(R.string.off_), FreedApplication.getStringFromRessources(R.string.on_) };
    }

    @Override
    public boolean get() {
        return settingsManager.get(settingMode).get();
    }

    @Override
    public void set(boolean bool) {
        previewController.setFocusPeak(bool);
        settingsManager.get(settingMode).set(bool);
    }
}
