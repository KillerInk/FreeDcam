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

package freed.cam.apis;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build.VERSION;

import freed.cam.apis.basecamera.CameraFragmentAbstract;
import freed.cam.apis.camera1.Camera1Fragment;
import freed.cam.apis.camera2.Camera2Fragment;
import freed.cam.apis.camera2.CameraHolderApi2;
import freed.cam.apis.sonyremote.SonyCameraFragment;
import freed.utils.AppSettingsManager;
import freed.utils.FreeDPool;
import freed.utils.RenderScriptHandler;


/**
 * Created by troop on 11.12.2014.
 * this class is used to check the supported api level of the device and to
 * load the different cameraFragments.
 * The api detection runs only once at first start when appsettings are empty
 * to grant a faster start next time
 */
public class ApiHandler
{
    //The interface to implement to work with the ApiHandler
    public interface ApiEvent
    {
        //get thrown when api detection has finished
        void apiDetectionDone();
    }

    private final  String TAG = ApiHandler.class.getSimpleName();
    private final Context context;
    private final AppSettingsManager appSettingsManager;
    private final RenderScriptHandler renderScriptHandler;

    private final ApiEvent event;

    public ApiHandler(Context context, ApiEvent event, AppSettingsManager appSettingsManager, RenderScriptHandler renderScriptHandler) {
        this.event = event;
        this.context = context;
        this.appSettingsManager = appSettingsManager;
        this.renderScriptHandler = renderScriptHandler;
    }

    private class AsyncCheckApi2 extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... unused) {
            boolean legacy = CameraHolderApi2.IsLegacy(context);
            if (legacy) {
                appSettingsManager.SetCamera2FullSupported("false");
                appSettingsManager.setCamApi(AppSettingsManager.API_1);
            } else {
                appSettingsManager.SetCamera2FullSupported("true");
                appSettingsManager.setCamApi(AppSettingsManager.API_2);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void s) {
            event.apiDetectionDone();
        }
    }

    /**
     * Check the device supported api
     */
    public void CheckApi()
    {
        //if its the first start of the app settings are empty
        if (appSettingsManager.IsCamera2FullSupported().equals("")) {
            if (VERSION.SDK_INT >= 21) {
                //camera2 is avail since L
                new AsyncCheckApi2().execute();
            }
            else {
                //older android version dont support it
                appSettingsManager.SetCamera2FullSupported("false");
                appSettingsManager.setCamApi(AppSettingsManager.API_1);
                event.apiDetectionDone();
            }
        }
        else {
            //appsetting was not empty
            event.apiDetectionDone();
        }
    }


    /**
     * create and returns the Api specific CameraFragment
     * @return
     */
    public CameraFragmentAbstract getCameraFragment()
    {
        CameraFragmentAbstract ret;
        //create SonyCameraFragment
        if (appSettingsManager.getCamApi().equals(AppSettingsManager.API_SONY))
        {
            ret = new SonyCameraFragment();
            ret.SetRenderScriptHandler(renderScriptHandler);

        }
        //create Camera2Fragment
        else if (appSettingsManager.getCamApi().equals(AppSettingsManager.API_2))
        {
            ret = new Camera2Fragment();
            ret.SetRenderScriptHandler(renderScriptHandler);
        }
        else //default is Camera1Fragment is supported by all devices
        {
            ret = new Camera1Fragment();
            ret.SetRenderScriptHandler(renderScriptHandler);
        }
        ret.SetAppSettingsManager(appSettingsManager);
        return ret;
    }

}
