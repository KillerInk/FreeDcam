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

package freed.cam.apis.sonyremote;

import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;

import com.troop.freedcam.R.id;
import com.troop.freedcam.R.layout;

import org.greenrobot.eventbus.Subscribe;

import java.util.Set;

import freed.ActivityInterface;
import freed.cam.apis.basecamera.CameraFragmentAbstract;
import freed.cam.apis.basecamera.CameraThreadHandler;
import freed.cam.events.CameraStateEvents;
import freed.cam.events.EventBusHelper;
import freed.cam.events.EventBusLifeCycle;
import freed.cam.previewpostprocessing.PreviewPostProcessingModes;
import freed.cam.previewpostprocessing.RenderScriptPreview;
import freed.cam.ui.themesample.handler.UserMessageHandler;
import freed.utils.Log;
import freed.views.AutoFitTextureView;

/**
 * Created by troop on 06.06.2015.
 */
public class SonyCameraRemoteFragment extends CameraFragmentAbstract<SonyRemoteCamera> implements EventBusLifeCycle, TextureView.SurfaceTextureListener
{
    private final String TAG = SonyCameraRemoteFragment.class.getSimpleName();
    private AutoFitTextureView surfaceView;

    private final int STATE_IDEL = 0;
    private final int STATE_DEVICE_CONNECTED = 3;
    private int STATE = STATE_IDEL;
    private PreviewStreamDrawer previewStreamDrawer;

    public static SonyCameraRemoteFragment getInstance()
    {
        SonyCameraRemoteFragment fragment = new SonyCameraRemoteFragment();
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);
        view = inflater.inflate(layout.camerafragment, container, false);
        surfaceView =  view.findViewById(id.autofitview);
        //getPreview().setTextureView(surfaceView);
        getPreview().initPreview(PreviewPostProcessingModes.RenderScript,getContext(),null);
        RenderScriptPreview rsPrev = (RenderScriptPreview)getPreview();
        previewStreamDrawer = new PreviewStreamDrawer(surfaceView,rsPrev.getRenderScriptManager());

        //textView_wifi = view.findViewById(id.textView_wificonnect);
        camera = new SonyRemoteCamera(previewStreamDrawer);
        camera.setPreview(getPreview());
        camera.init((ActivityInterface) getActivity());
        CameraThreadHandler.setCameraInterface(camera);

        //this.onCameraOpenFinish("");
        return view;
    }

    @Override
    public void startListning() {
        EventBusHelper.register(this);
    }

    @Override
    public void stopListning() {
        EventBusHelper.unregister(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        startListning();
        camera.onResume();
        CameraThreadHandler.startCameraAsync();

    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause.stopCamera");
        camera.onPause();
        CameraThreadHandler.stopCameraAsync();
        stopListning();
    }


    private void setTextFromWifi(final String txt)
    {
        UserMessageHandler.sendMSG(txt,false);
    }

    public Set<String> getAvailableApiSet(){return camera.getAvailableApiSet();}

    @Subscribe
    public void onCameraClose(CameraStateEvents.CameraCloseEvent message) {
        camera.release();
    }

    @Subscribe
    public void onCameraError(CameraStateEvents.CameraErrorEvent error)
    {
        Log.d(TAG, "###################### onCamerError:"+ error + " ################################");
        setTextFromWifi(error.msg);
        STATE = STATE_IDEL;
        camera.stop();
        previewStreamDrawer.stop();
        //setCameraEventListner(SonyCameraRemoteFragment.this);
        CameraThreadHandler.startCameraAsync(5000);

    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        camera.stopPreview();
        CameraThreadHandler.stopCameraAsync();
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }
}
