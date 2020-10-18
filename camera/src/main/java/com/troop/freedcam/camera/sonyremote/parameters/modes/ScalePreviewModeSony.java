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

package com.troop.freedcam.camera.sonyremote.parameters.modes;


import java.util.Set;

import com.troop.freedcam.processor.RenderScriptManager;
import com.troop.freedcam.utils.ContextApplication;
import com.troop.freedcam.camera.sonyremote.PreviewStreamDrawer;
import com.troop.freedcam.settings.SettingKeys;

/**
 * Created by troop on 16.08.2016.
 */
public class ScalePreviewModeSony extends BaseModeParameterSony {

    private final PreviewStreamDrawer simpleStreamSurfaceView;

    public ScalePreviewModeSony(PreviewStreamDrawer simpleStreamSurfaceView) {
        super(null, null, null, null,null, SettingKeys.SCALE_PREVIEW);
        this.simpleStreamSurfaceView = simpleStreamSurfaceView;
        if (RenderScriptManager.isSupported())
            setViewState(ViewState.Visible);
    }

    @Override
    public void SetValue(String valueToSet, boolean setToCamera) {
        if (ContextApplication.getStringFromRessources(com.troop.freedcam.camera.R.string.on_).equals(valueToSet))
            simpleStreamSurfaceView.ScalePreview(true);
        else
            simpleStreamSurfaceView.ScalePreview(false);
    }

    @Override
    public String GetStringValue() {
        if (simpleStreamSurfaceView.isScalePreview())
            return ContextApplication.getStringFromRessources(com.troop.freedcam.camera.R.string.on_);
        else
            return ContextApplication.getStringFromRessources(com.troop.freedcam.camera.R.string.off_);
    }

    @Override
    public String[] getStringValues() {
        return new String[]{ContextApplication.getStringFromRessources(com.troop.freedcam.camera.R.string.on_), ContextApplication.getStringFromRessources(com.troop.freedcam.camera.R.string.off_)};
    }

    @Override
    public void SonyApiChanged(Set<String> mAvailableCameraApiSet) {
        //super.SonyApiChanged(mAvailableCameraApiSet);
    }

    @Override
    protected void processValuesToSet(String valueToSet) {
        //super.processValuesToSet(valueToSet);
    }

    @Override
    protected String processGetString() {
        return null;
    }

    @Override
    protected String[] processValuesToReturn() {
        return null;
    }
}
