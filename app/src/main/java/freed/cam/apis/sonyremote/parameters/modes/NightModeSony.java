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

import android.os.Build.VERSION;

import com.troop.freedcam.R;

import java.util.Set;

import freed.cam.apis.sonyremote.sonystuff.SimpleStreamSurfaceView;

/**
 * Created by troop on 04.12.2015.
 */
public class NightModeSony extends BaseModeParameterSony
{
    private final SimpleStreamSurfaceView simpleStreamSurfaceView;
    private final String GRAYSCALE = "GrayScale";
    private final String EXPOSURE = "Exposure";
    final String ZOOMPREVIEW = "ZoomPreview";

    public NightModeSony(SimpleStreamSurfaceView simpleStreamSurfaceView) {
        super(null, null, null, null,null);
        this.simpleStreamSurfaceView = simpleStreamSurfaceView;
    }

    public void SetValue(String valueToSet, boolean setToCamera)
    {
        if (valueToSet.equals(simpleStreamSurfaceView.getResources().getString(R.string.on_)))
            simpleStreamSurfaceView.nightmode = SimpleStreamSurfaceView.NightPreviewModes.on;
        else if(valueToSet.equals(GRAYSCALE))
            simpleStreamSurfaceView.nightmode = SimpleStreamSurfaceView.NightPreviewModes.grayscale;
        else if(valueToSet.equals(EXPOSURE))
            simpleStreamSurfaceView.nightmode = SimpleStreamSurfaceView.NightPreviewModes.exposure;
        else
            simpleStreamSurfaceView.nightmode = SimpleStreamSurfaceView.NightPreviewModes.off;
    }

    @Override
    public String GetStringValue()
    {
        switch (simpleStreamSurfaceView.nightmode)
        {
            case on:
                return  simpleStreamSurfaceView.getResources().getString(R.string.on_);
            case off:
                return  simpleStreamSurfaceView.getResources().getString(R.string.off_);
            case grayscale:
                return GRAYSCALE;
            case exposure:
                return EXPOSURE;
           /* case zoompreview:
                return ZOOMPREVIEW;*/
            default:
                return  simpleStreamSurfaceView.getResources().getString(R.string.off_);
        }
    }

    @Override
    public String[] getStringValues() {
        return new String[] {simpleStreamSurfaceView.getResources().getString(R.string.on_), simpleStreamSurfaceView.getResources().getString(R.string.off_), GRAYSCALE, EXPOSURE /*, ZOOMPREVIEW*/};
    }

    @Override
    public boolean IsSupported() {
        return VERSION.SDK_INT >= 18;
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
