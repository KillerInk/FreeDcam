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

import java.util.Set;

import freed.cam.apis.KEYS;
import freed.cam.apis.sonyremote.sonystuff.SimpleStreamSurfaceView;

/**
 * Created by troop on 04.12.2015.
 */
public class NightModeSony extends BaseModeParameterSony
{
    private final String currentval = KEYS.OFF;
    private final SimpleStreamSurfaceView simpleStreamSurfaceView;
    private final String GRAYSCALE = "GrayScale";
    private final String EXPOSURE = "Exposure";
    final String ZOOMPREVIEW = "ZoomPreview";

    public NightModeSony(SimpleStreamSurfaceView simpleStreamSurfaceView) {
        super(null, null, null, null);
        this.simpleStreamSurfaceView = simpleStreamSurfaceView;
    }

    public void SetValue(String valueToSet, boolean setToCamera)
    {
        switch (valueToSet) {
            case KEYS.ON:
                simpleStreamSurfaceView.nightmode = SimpleStreamSurfaceView.NightPreviewModes.on;
                break;
            case GRAYSCALE:
                simpleStreamSurfaceView.nightmode = SimpleStreamSurfaceView.NightPreviewModes.grayscale;
                break;
            case EXPOSURE:
                simpleStreamSurfaceView.nightmode = SimpleStreamSurfaceView.NightPreviewModes.exposure;
                break;
            default:
                simpleStreamSurfaceView.nightmode = SimpleStreamSurfaceView.NightPreviewModes.off;
                break;
        }
    }

    @Override
    public String GetValue()
    {
        switch (simpleStreamSurfaceView.nightmode)
        {
            case on:
                return KEYS.ON;
            case off:
                return KEYS.OFF;
            case grayscale:
                return GRAYSCALE;
            case exposure:
                return EXPOSURE;
           /* case zoompreview:
                return ZOOMPREVIEW;*/
            default:
                return KEYS.OFF;
        }
    }

    @Override
    public String[] GetValues() {
        return new String[] {KEYS.ON, KEYS.OFF, GRAYSCALE, EXPOSURE /*, ZOOMPREVIEW*/};
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
