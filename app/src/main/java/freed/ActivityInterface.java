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

package freed;

import android.net.Uri;

import freed.file.FileListController;
import freed.utils.LocationManager;
import freed.utils.PermissionManager;
import freed.viewer.helper.BitmapHelper;

/**
 * Created by troop on 22.03.2015.
 */
public interface ActivityInterface
{
    void closeActivity();
    void ChooseSDCard(I_OnActivityResultCallback callback);
    interface I_OnActivityResultCallback
    {
        void onActivityResultCallback(Uri uri);
    }

    PermissionManager getPermissionManager();

    BitmapHelper getBitmapHelper();

    FileListController getFileListController();

    LocationManager getLocationManager();

    int getOrientation();

    void SetNightOverlay();

    void runFeatureDetector();
}


