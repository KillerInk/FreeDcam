package com.troop.freedcam.sonyapi;

import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.troop.freedcam.i_camera.interfaces.I_Module;
import com.troop.freedcam.i_camera.interfaces.I_error;
import com.troop.freedcam.i_camera.AbstractCameraUiWrapper;
import com.troop.freedcam.sonyapi.modules.ModuleHandlerSony;
import com.troop.freedcam.sonyapi.parameters.ParameterHandlerSony;
import com.troop.freedcam.sonyapi.sonystuff.ServerDevice;
import com.troop.freedcam.sonyapi.sonystuff.SimpleSsdpClient;
import com.troop.freedcam.sonyapi.sonystuff.SimpleStreamSurfaceView;
import com.troop.freedcam.sonyapi.sonystuff.WifiUtils;
import com.troop.freedcam.ui.AppSettingsManager;

import java.util.List;

/**
 * Created by troop on 11.12.2014.
 */
public class CameraUiWrapperSony  extends AbstractCameraUiWrapper implements SurfaceHolder.Callback
{
    protected SimpleStreamSurfaceView surfaceView;

    private SimpleSsdpClient mSsdpClient;
    ServerDevice serverDevice;
    CameraHolderSony cameraHolder;
    AppSettingsManager appSettingsManager;
    WifiUtils wifiUtils;


    public CameraUiWrapperSony(SurfaceView preview, AppSettingsManager appSettingsManager) {
        super(preview, appSettingsManager);
        this.surfaceView = (SimpleStreamSurfaceView)preview;
        this.surfaceView.getHolder().addCallback(this);
        this.appSettingsManager = appSettingsManager;
        this.cameraHolder = new CameraHolderSony(preview.getContext(), surfaceView, this, backGroundThread, backGroundHandler, uiHandler);
        camParametersHandler = new ParameterHandlerSony(cameraHolder, appSettingsManager, backGroundHandler, uiHandler);
        cameraHolder.ParameterHandler = (ParameterHandlerSony)camParametersHandler;
        moduleHandler = new ModuleHandlerSony(cameraHolder, appSettingsManager);
        mSsdpClient = new SimpleSsdpClient();
        wifiUtils = new WifiUtils(surfaceView.getContext());
    }

    @Override
    public void SwitchModule(String moduleName) {
        moduleHandler.SetModule(moduleName);
    }


    @Override
    protected void startCamera()
    {
        String wifis = null;
        try {
            wifis = wifiUtils.getConnectedNetworkSSID();
            if (wifis == null || wifis.equals("")) {
                onCameraError("Wifi disabled");
                return;
            }

        }
        catch (Exception ex)
        {
            onCameraError("Wifi disabled");
            return;
        }
        if (!wifis.contains("DIRECT"))
        {
            String[] configuredNetworks = null;
            try {
                configuredNetworks = wifiUtils.getConfiguredNetworkSSIDs();
            }
            catch (Exception ex)
            {
                onCameraError("Wifi disabled");
                return;
            }

            String confnet = "";
            for (String s : configuredNetworks)
            {
                if (s.contains("DIRECT"))
                {
                    confnet = s;
                    break;
                }
            }
            if (confnet.equals(""))
            {
                onCameraError("No Sony Camera Device Configured in WifiSettings");
                return;
            }
            String[] foundNetWorks = wifiUtils.getNetworkSSIDs();
            String foundnet = "";
            for (String s : foundNetWorks)
            {
                if (confnet.equals(s))
                {
                    foundnet = s;
                    break;
                }
            }
            if (foundnet.equals(""))
            {
                onCameraError("Cant find Sony Camera WifiNetwork, Camera turned On?");
                return;
            }
            wifiUtils.ConnectToSSID(foundnet);
            while (!wifiUtils.getWifiConnected())
            {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }

        mSsdpClient.search(new SimpleSsdpClient.SearchResultHandler() {
            @Override
            public void onDeviceFound(ServerDevice device) {
                serverDevice = device;
                appSettingsManager.context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        cameraHolder.OpenCamera(serverDevice);
                    }
                });


            }

            @Override
            public void onFinished()
            {
                if (serverDevice == null)
                    onCameraError("Cant find a sony remote Device");

            }

            @Override
            public void onErrorFinished()
            {
                    onCameraError("Error happend while searching for sony remote device");
            }
        });
        onCameraOpen("");
    }

    @Override
    protected void stopCamera() {
        cameraHolder.CloseCamera();
    }

    @Override
    protected void stopPreview() {

    }

    @Override
    protected void startPreview() {

    }

    @Override
    public void onCameraOpen(String message) {
        super.onCameraOpen(message);
    }

    @Override
    public void onCameraError(String error) {
        super.onCameraError(error);
    }

    @Override
    public void onCameraStatusChanged(String status) {
        super.onCameraStatusChanged(status);
    }

    @Override
    public void onModuleChanged(I_Module module) {
        super.onModuleChanged(module);
    }

    @Override
    public void DoWork() {
        moduleHandler.DoWork();
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
    public void surfaceCreated(SurfaceHolder holder)
    {
        StartCamera();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        StopPreview();
        StopCamera();
    }
}
