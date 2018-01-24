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

package freed.renderscript;

import android.view.Surface;

/**
 * Created by troop on 12.06.2016.
 */
public interface RenderScriptProcessorInterface
{
    boolean isEnabled();
    //api1
    void setFocusPeakEnable(boolean enable);
    void setHistogramEnable(boolean enable);
    void setClippingEnable(boolean enable);
    void setBlue(boolean blue);
    void setRed(boolean red);
    void setGreen(boolean green);
    void SetAspectRatio(int w, int h);

    //api2
    void Reset(int width, int height);
    Surface getInputSurface();
    void setOutputSurface(Surface output);

    //api1/2
    void kill();
}
