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

package freed.cam.ui.themesample;



import androidx.fragment.app.Fragment;

import freed.ActivityInterface;
import freed.cam.apis.basecamera.CameraWrapperInterface;

/**
 * Created by troop on 25.03.2015.
 */
public abstract class AbstractFragment extends Fragment implements I_Fragment
{
    protected CameraWrapperInterface cameraUiWrapper;
    protected ActivityInterface fragment_activityInterface;

    public void setCameraToUi(CameraWrapperInterface wrapper)
    {
        cameraUiWrapper = wrapper;
    }


}
