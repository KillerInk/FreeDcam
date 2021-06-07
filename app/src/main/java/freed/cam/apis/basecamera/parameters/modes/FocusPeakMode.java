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


import com.troop.freedcam.R;

import freed.FreedApplication;
import freed.cam.ActivityFreeDcamMain;
import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.parameters.AbstractParameter;
import freed.cam.previewpostprocessing.PreviewController;
import freed.cam.previewpostprocessing.PreviewPostProcessingModes;
import freed.settings.SettingKeys;

/**
 * Created by troop on 10.09.2015.
 */
public class FocusPeakMode extends AbstractParameter {
    protected PreviewController previewController;
    public FocusPeakMode(CameraWrapperInterface cameraUiWrapper)
    {
        super(cameraUiWrapper,SettingKeys.Focuspeak);
        previewController = ActivityFreeDcamMain.previewController();
        currentString = FreedApplication.getStringFromRessources(R.string.off_);
    }

    public FocusPeakMode(CameraWrapperInterface cameraWrapperInterface, SettingKeys.Key key)
    {
        super(cameraWrapperInterface,key);
        previewController = ActivityFreeDcamMain.previewController();
        currentString = FreedApplication.getStringFromRessources(R.string.off_);
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
        if (valueToSet.equals(FreedApplication.getStringFromRessources(R.string.on_)))
        {
            previewController.setFocusPeak(true);
        }
        else {
            previewController.setFocusPeak(false);
        }
        fireStringValueChanged(valueToSet);

    }

    @Override
    public String[] getStringValues() {
        return new String[] {FreedApplication.getStringFromRessources(R.string.on_), FreedApplication.getStringFromRessources(R.string.off_)};
    }

}
