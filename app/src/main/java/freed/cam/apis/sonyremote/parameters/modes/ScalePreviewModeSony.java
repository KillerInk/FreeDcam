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

package freed.cam.apis.sonyremote.parameters.modes;

import android.os.Build;

import com.troop.freedcam.R;

import java.util.Set;

import freed.cam.apis.sonyremote.sonystuff.SimpleStreamSurfaceView;

/**
 * Created by troop on 16.08.2016.
 */
public class ScalePreviewModeSony extends BaseModeParameterSony {

    private final SimpleStreamSurfaceView simpleStreamSurfaceView;

    public ScalePreviewModeSony(SimpleStreamSurfaceView simpleStreamSurfaceView) {
        super(null, null, null, null,null);
        this.simpleStreamSurfaceView = simpleStreamSurfaceView;
    }

    @Override
    public void SetValue(String valueToSet, boolean setToCamera) {
        if (simpleStreamSurfaceView.getResources().getString(R.string.on_).equals(valueToSet))
            simpleStreamSurfaceView.ScalePreview(true);
        else
            simpleStreamSurfaceView.ScalePreview(false);
    }

    @Override
    public String GetStringValue() {
        if (simpleStreamSurfaceView.isScalePreview())
            return simpleStreamSurfaceView.getResources().getString(R.string.on_);
        else
            return simpleStreamSurfaceView.getResources().getString(R.string.off_);
    }

    @Override
    public String[] getStringValues() {
        return new String[]{simpleStreamSurfaceView.getResources().getString(R.string.on_), simpleStreamSurfaceView.getResources().getString(R.string.off_)};
    }

    @Override
    public boolean IsSupported() {
        return Build.VERSION.SDK_INT >= 18;
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
