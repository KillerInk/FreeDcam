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

import java.io.IOException;

import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.sonyremote.sonystuff.JsonUtils;
import freed.cam.apis.sonyremote.sonystuff.SimpleRemoteApi;
import freed.utils.FreeDPool;
import freed.utils.Log;

/**
 * Created by troop on 09.08.2016.
 */
public class FocusModeSony extends BaseModeParameterSony {
    public FocusModeSony(String VALUE_TO_GET, String VALUE_TO_SET, String VALUES_TO_GET, SimpleRemoteApi mRemoteApi, CameraWrapperInterface wrapperInterface) {
        super(VALUE_TO_GET, VALUE_TO_SET, VALUES_TO_GET, mRemoteApi,wrapperInterface);
    }

    @Override
    public void SetValue(final String valueToSet, boolean setToCamera) {
        super.SetValue(valueToSet, setToCamera);
        if (JsonUtils.isApiSupported("setLiveviewFrameInfo", mAvailableCameraApiSet))
        {
            FreeDPool.Execute(new Runnable() {
                @Override
                public void run()
                {
                    try {
                        if (valueToSet.equals("MF"))
                            mRemoteApi.setLiveviewFrameInfo(false);
                        else
                            mRemoteApi.setLiveviewFrameInfo(true);
                    } catch (IOException e) {
                        Log.WriteEx(e);
                    }
                }
            });

        }
    }
}
