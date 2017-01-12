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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.troop.freedcam.R.id;
import com.troop.freedcam.R.layout;

import freed.ActivityInterface;
import freed.cam.apis.basecamera.AbstractFocusHandler;
import freed.cam.apis.basecamera.CameraFragmentAbstract;
import freed.cam.apis.basecamera.CameraHolderInterface;
import freed.cam.apis.basecamera.FocuspeakProcessor;
import freed.cam.apis.basecamera.modules.ModuleHandlerAbstract;
import freed.cam.apis.basecamera.parameters.AbstractParameterHandler;
import freed.cam.apis.sonyremote.parameters.ParameterHandler;
import freed.cam.apis.sonyremote.sonystuff.ServerDevice;
import freed.cam.apis.sonyremote.sonystuff.SimpleSsdpClient;
import freed.cam.apis.sonyremote.sonystuff.SimpleSsdpClient.SearchResultHandler;
import freed.cam.apis.sonyremote.sonystuff.SimpleStreamSurfaceView;
import freed.cam.apis.sonyremote.sonystuff.WifiHandler;
import freed.cam.apis.sonyremote.sonystuff.WifiUtils;
import freed.utils.AppSettingsManager;
import freed.utils.FreeDPool;
import freed.utils.RenderScriptHandler;

/**
 * Created by troop on 06.06.2015.
 */
public class SonyCameraFragment extends CameraFragmentAbstract implements SurfaceHolder.Callback, WifiHandler.WifiEvents
{
    private final String TAG = SonyCameraFragment.class.getSimpleName();
    private SimpleStreamSurfaceView surfaceView;

    private ServerDevice serverDevice;

    private TextView textView_wifi;
    private final int STATE_IDEL = 0;
    private final int STATE_DEVICE_CONNECTED = 3;
    private int STATE = STATE_IDEL;

    private String[] configuredNetworks;
    private String deviceNetworkToConnect;

    private boolean hasLocationPermission =false;
    WifiHandler wifiHandler;
    //private boolean connected;
    //private CameraHolderSony cameraHolder;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);
        view = inflater.inflate(layout.cameraholdersony, container, false);
        surfaceView = (SimpleStreamSurfaceView) view.findViewById(id.view);
        surfaceView.SetRenderScriptHandlerAndInterface(renderScriptHandler, (ActivityInterface) getActivity());

        textView_wifi =(TextView) view.findViewById(id.textView_wificonnect);

        wifiHandler = new WifiHandler(getActivityInterface());
        parametersHandler = new ParameterHandler(this, surfaceView, getContext());

        moduleHandler = new ModuleHandlerSony(this);
        Focus = new FocusHandler(this);
        cameraHolder = new CameraHolderSony(getContext(), surfaceView, this);
        moduleHandler.initModules();

        this.onCameraOpenFinish("");
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        wifiHandler.onResume();
        StartCamera();

    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause.StopCamera");
        wifiHandler.onPause();
        StopCamera();

    }


    private void setTextFromWifi(final String txt)
    {
        textView_wifi.post(new Runnable() {
            @Override
            public void run() {
                textView_wifi.setText(txt);
            }
        });
    }

    private void hideTextViewWifi(final boolean hide)
    {
        textView_wifi.post(new Runnable() {
            @Override
            public void run() {
                if (hide)
                    textView_wifi.setVisibility(View.GONE);
                else
                    textView_wifi.setVisibility(View.VISIBLE);
            }
        });
    }


    @Override
    public String CameraApiName() {

        return AppSettingsManager.API_SONY;
    }

    @Override
    public void StartCamera()
    {
        if (serverDevice == null)
        {
            wifiHandler.setEventsListner(this);
            wifiHandler.StartLookUp();
            return;
        }
        Log.d(TAG,"StartCamera");

        FreeDPool.Execute(new Runnable() {
            @Override
            public void run() {
                ((CameraHolderSony) cameraHolder).OpenCamera(serverDevice);
                Log.d(TAG, "onCameraOpen State:" + STATE);
                STATE = STATE_DEVICE_CONNECTED;
            }
        });
    }

    @Override
    public void StopCamera()
    {
        cameraHolder.CloseCamera();
        STATE = STATE_IDEL;
    }


    @Override
    public void DoWork() {
        moduleHandler.DoWork();
    }





    @Override
    public int getMargineLeft() {
        return surfaceView.getLeft();
    }

    @Override
    public int getMargineRight() {
        return surfaceView.getRight();
    }

    @Override
    public int getMargineTop() {
        return surfaceView.getTop();
    }

    @Override
    public int getPreviewWidth() {
        return surfaceView.getWidth();
    }

    @Override
    public int getPreviewHeight() {
        return surfaceView.getHeight();
    }


    @Override
    public SurfaceView getSurfaceView() {
        return surfaceView;
    }


    //SurfaceHolder.Callback
    @Override
    public void surfaceCreated(SurfaceHolder holder)
    {
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

        StopPreview();
        StopCamera();
    }

    //WifiHandler.WifiEvents
    @Override
    public void onDeviceFound(ServerDevice serverDevice) {
        this.serverDevice = serverDevice;
        wifiHandler.setEventsListner(null);
        hideTextViewWifi(true);
        StartCamera();
    }

    @Override
    public void onMessage(String msg) {
        setTextFromWifi(msg);
    }


    @Override
    public void onCameraOpen(String message)
    {

        //this.onCameraOpenFinish("");
    }

    @Override
    public void onCameraClose(String message) {

    }

    @Override
    public void onPreviewOpen(String message) {

    }

    @Override
    public void onPreviewClose(String message) {

    }

    @Override
    public void onCameraError(String error)
    {
        hideTextViewWifi(false);
        setTextFromWifi(error);
        serverDevice = null;
        STATE = STATE_IDEL;
        Log.d(TAG, "Camera error:" +error );
        surfaceView.stop();
        //SetCameraStateChangedListner(SonyCameraFragment.this);
        surfaceView.postDelayed(new Runnable() {
            @Override
            public void run() {
                StartCamera();
            }
        },5000);

    }

    @Override
    public void onCameraStatusChanged(String status) {

    }
}
