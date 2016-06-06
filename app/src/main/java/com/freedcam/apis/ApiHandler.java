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

package com.freedcam.apis;

import android.content.Context;
import android.os.Build;

import com.freedcam.apis.basecamera.apis.AbstractCameraFragment;
import com.freedcam.apis.camera1.apis.Camera1Fragment;
import com.freedcam.apis.camera2.apis.Camera2Fragment;
import com.freedcam.apis.camera2.camera.CameraHolder;
import com.freedcam.apis.sonyremote.apis.SonyCameraFragment;
import com.freedcam.utils.AppSettingsManager;
import com.freedcam.utils.FreeDPool;
import com.freedcam.utils.RenderScriptHandler;


/**
 * Created by troop on 11.12.2014.
 */

public class ApiHandler
{
    private final  String TAG = ApiHandler.class.getSimpleName();
    private Context context;
    private AppSettingsManager appSettingsManager;
    private RenderScriptHandler renderScriptHandler;

    private ApiEvent event;

    public ApiHandler(Context context, ApiEvent event, AppSettingsManager appSettingsManager, RenderScriptHandler renderScriptHandler) {
        this.event = event;
        this.context = context;
        this.appSettingsManager = appSettingsManager;
        this.renderScriptHandler = renderScriptHandler;
    }

    public void CheckApi()
    {
        if (appSettingsManager.IsCamera2FullSupported().equals(""))
        {
            if (Build.VERSION.SDK_INT >= 21)
            {
                FreeDPool.Execute(new Runnable() {
                    @Override
                    public void run() {
                        boolean legacy = CameraHolder.IsLegacy(appSettingsManager,context);
                        if (legacy) {
                            appSettingsManager.SetCamera2FullSupported("false");
                            appSettingsManager.setCamApi(AppSettingsManager.API_1);
                        } else {
                            appSettingsManager.SetCamera2FullSupported("true");
                            appSettingsManager.setCamApi(AppSettingsManager.API_2);
                        }
                        event.apiDetectionDone();
                    }
                });

            }
            else {
                appSettingsManager.SetCamera2FullSupported("false");
                appSettingsManager.setCamApi(AppSettingsManager.API_1);
                event.apiDetectionDone();
            }
        }
        else
            event.apiDetectionDone();
    }


    public AbstractCameraFragment getCameraFragment()
    {
        AbstractCameraFragment ret;
        if (appSettingsManager.getCamApi().equals(AppSettingsManager.API_SONY))
        {
            ret = new SonyCameraFragment();

        }
        else if (appSettingsManager.getCamApi().equals(AppSettingsManager.API_2))
        {
            ret = new Camera2Fragment();
            ret.SetRenderScriptHandler(renderScriptHandler);
        }
        else
        {
            ret = new Camera1Fragment();
            ret.SetRenderScriptHandler(renderScriptHandler);
        }
        ret.SetAppSettingsManager(appSettingsManager);
        return ret;
    }

    public interface ApiEvent
    {
        void apiDetectionDone();
    }


}
