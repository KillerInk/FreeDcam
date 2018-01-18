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

package freed.cam.apis.basecamera;

import android.location.Location;
import android.view.Surface;
import android.view.SurfaceHolder;

/**
 * Created by troop on 15.08.2014.
 */
public interface CameraHolderInterface
{
    /**
     * open the camera
     * @param camera to open
     * @return true when open sucessfull, false when something went wrong
     */
    boolean OpenCamera(int camera);
    void CloseCamera();
    /**
     *
     * @return the count of avail cameras
     */
    int CameraCout();
    boolean IsRdy();

    /**
     * Set the surface to camera
     * @param texture to set
     * @return
     */
    boolean SetSurface(SurfaceHolder texture);
    boolean SetSurface(Surface texture);
    void StartPreview();
    void StopPreview();
    void SetLocation(Location location);
    void CancelFocus();
    void ResetPreviewCallback();

}
