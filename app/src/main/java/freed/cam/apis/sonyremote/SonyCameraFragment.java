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
import android.os.Handler;
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
import freed.cam.ActivityFreeDcamMain;
import freed.cam.apis.basecamera.AbstractFocusHandler;
import freed.cam.apis.basecamera.CameraFragmentAbstract;
import freed.cam.apis.basecamera.CameraHolderInterface;
import freed.cam.apis.basecamera.FocuspeakProcessor;
import freed.cam.apis.basecamera.modules.ModuleHandlerAbstract;
import freed.cam.apis.basecamera.modules.ModuleInterface;
import freed.cam.apis.basecamera.parameters.AbstractParameterHandler;
import freed.cam.apis.sonyremote.parameters.ParameterHandler;
import freed.cam.apis.sonyremote.sonystuff.ServerDevice;
import freed.cam.apis.sonyremote.sonystuff.SimpleSsdpClient;
import freed.cam.apis.sonyremote.sonystuff.SimpleSsdpClient.SearchResultHandler;
import freed.cam.apis.sonyremote.sonystuff.SimpleStreamSurfaceView;
import freed.cam.apis.sonyremote.sonystuff.WifiUtils;
import freed.utils.AppSettingsManager;
import freed.utils.FreeDPool;
import freed.utils.Logger;
import freed.utils.RenderScriptHandler;

/**
 * Created by troop on 06.06.2015.
 */
public class SonyCameraFragment extends CameraFragmentAbstract implements SurfaceHolder.Callback
{
    private final String TAG = SonyCameraFragment.class.getSimpleName();
    private SimpleStreamSurfaceView surfaceView;
    private WifiUtils wifiUtils;
    private WifiScanReceiver wifiReciever;
    private WifiConnectedReceiver wifiConnectedReceiver;
    private SimpleSsdpClient mSsdpClient;
    private ServerDevice serverDevice;

    private TextView textView_wifi;
    private final int IDEL = 0;
    private final int WAITING_FOR_SCANRESULT = 1;
    private final int WAITING_FOR_DEVICECONNECTION = 2;
    private final int DEVICE_CONNECTED = 3;
    private int STATE = IDEL;

    private String[] configuredNetworks;
    private String deviceNetworkToConnect;
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
        wifiReciever = new WifiScanReceiver();
        wifiConnectedReceiver = new WifiConnectedReceiver();
        wifiUtils = new WifiUtils(view.getContext());
        mSsdpClient = new SimpleSsdpClient();

        parametersHandler = new ParameterHandler(this, surfaceView, getContext());

        moduleHandler = new ModuleHandlerSony(this);
        Focus = new FocusHandler(this);
        cameraHolder = new CameraHolderSony(getContext(), surfaceView, this);
        moduleHandler.initModules();

        SetCameraChangedListner(this);
        ((ActivityFreeDcamMain) getActivity()).onCameraUiWrapperRdy(this);

        return view;
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




    //WIFI STUFF START


    private void searchSsdpClient()
    {
        if(STATE == DEVICE_CONNECTED)
        {
            Log.d(TAG,"already connected");
            return;
        }
        setTextFromWifi("Search SSDP Client...");
        while (!wifiUtils.getWifiConnected())
        {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Logger.exception(e);
            }
        }
        mSsdpClient.search(new SearchResultHandler()
        {
            @Override
            public void onDeviceFound(ServerDevice device)
            {
                if(STATE == DEVICE_CONNECTED)
                    return;
                setTextFromWifi("Found SSDP Client... Connecting");
                STATE = DEVICE_CONNECTED;
                serverDevice = device;
                StartCamera();
                hideTextViewWifi(true);
            }
            @Override
            public void onFinished()
            {
                if (serverDevice == null)
                    setTextFromWifi("Cant find a sony remote Device");

            }

            @Override
            public void onErrorFinished()
            {
                if (serverDevice == null)
                    setTextFromWifi("Error happend while searching for sony remote device \n pls restart remote");
                //startWifiScanning();
            }
        });
    }

    private void startWifiScanning()
    {
        STATE = IDEL;
        if (getActivity() != null) {
            getActivity().registerReceiver(wifiReciever, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
            getConfiguredNetworks();
            lookupAvailNetworks();
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        if(getActivityInterface().hasLocationPermission())
            startWifiScanning();
    }

    @Override
    public void onPause() {
        super.onPause();
        StopCamera();
        getActivity().unregisterReceiver(wifiReciever);
    }

    private void getConfiguredNetworks()
    {
        if(STATE == DEVICE_CONNECTED)
            return;
        setTextFromWifi("Looking up Configured Wifi Networks");
        try {
            configuredNetworks = wifiUtils.getConfiguredNetworkSSIDs();
        }
        catch (Exception ex) {
            setTextFromWifi("Wifi disabled");
        }
        if (configuredNetworks == null)
        {
            setTextFromWifi("Wifi disabled");
            return;
        }
        deviceNetworkToConnect = "";
        for (String s : configuredNetworks)
        {
            if (s.contains("DIRECT"))
            {
                deviceNetworkToConnect = s;
                Logger.d("Wifi", "Device to Connect:" + deviceNetworkToConnect);
                setTextFromWifi("Device to Connect:" + deviceNetworkToConnect);
                break;
            }
        }
        if (deviceNetworkToConnect.equals("")) {
            setTextFromWifi("No Sony Camera Device Configured in WifiSettings");
        }
    }

    private void lookupAvailNetworks()
    {
        Logger.d("Wifi", "Lookup networks:");
        String wifis = null;
        try {
            setTextFromWifi("Looking up WifiNetworks");
            hideTextViewWifi(false);
            wifis = wifiUtils.getConnectedNetworkSSID();
            if (wifis == null || wifis.equals("")) {
                setTextFromWifi("Wifi disabled");
                return;
            }

        }
        catch (Exception ex) {
            setTextFromWifi("Wifi disabled");
            return;
        }
        if (!wifis.contains("DIRECT"))
        {
            STATE = WAITING_FOR_SCANRESULT;
            Logger.d("Wifi", "No DIRECT network found start scan:");
            wifiUtils.StartScan();
        }
        else {
            Logger.d("Wifi", "Connect to:" + deviceNetworkToConnect);
            STATE = IDEL;
            if (serverDevice == null)
                searchSsdpClient();
            else
            {
                setTextFromWifi("");
                StartCamera();
            }
        }
    }

    @Override
    public void onCameraOpen(String message) {

    }

    @Override
    public void onCameraOpenFinish(String message) {

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
        STATE = IDEL;
        Logger.d(TAG, "Camera error:" +error );
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run()
            {
                Logger.d(TAG, "StartScanning For networks after onCameraError" );
                try {
                    SetCameraChangedListner(SonyCameraFragment.this);
                    ((ActivityFreeDcamMain) getActivity()).onCameraUiWrapperRdy(SonyCameraFragment.this);
                    startWifiScanning();
                }
                catch (NullPointerException ex)
                {
                    Logger.exception(ex);
                }

            }
        }, 5000);

    }

    @Override
    public void onCameraStatusChanged(String status) {

    }

    @Override
    public void onModuleChanged(ModuleInterface module) {

    }

    class WifiScanReceiver extends BroadcastReceiver
    {
        public void onReceive(Context c, Intent intent)
        {
            Logger.d("Wifi", "WifiScanReceiver");
            if (STATE == WAITING_FOR_SCANRESULT)
            {
                STATE = IDEL;
                if (deviceNetworkToConnect == null || deviceNetworkToConnect.equals(""))
                    return;
                String[] foundNetWorks = wifiUtils.getNetworkSSIDs();
                String foundnet = "";
                for (String s : foundNetWorks) {
                    if (deviceNetworkToConnect.equals(s)) {
                        foundnet = s;
                        break;
                    }
                }
                if (foundnet.equals("")) {
                    setTextFromWifi("Cant find Sony Camera WifiNetwork, Camera turned On?");
                    Logger.d("Wifi", "WifiScanReceiver no device to connect found lookupnetworks");
                    lookupAvailNetworks();
                }
                else {
                    STATE = WAITING_FOR_DEVICECONNECTION;
                    Logger.d("Wifi", "WifiScanReceiver found device connect to it");
                    if (!wifiUtils.ConnectToSSID(foundnet))
                    {
                        Logger.d("Wifi", "Connect to device failed");
                        STATE = WAITING_FOR_SCANRESULT;
                        wifiUtils.StartScan();
                    }
                    else
                    {
                        STATE = IDEL;
                        searchSsdpClient();
                    }
                }
            }
            else if (STATE == IDEL && wifiUtils.getWifiConnected())
            {
                STATE = IDEL;
                searchSsdpClient();
            }
        }
    }

    class WifiConnectedReceiver extends BroadcastReceiver
    {
        public void onReceive(Context c, Intent intent)
        {
            if (STATE == WAITING_FOR_DEVICECONNECTION && wifiUtils.getWifiConnected())
            {
                STATE = IDEL;
                if (serverDevice == null)
                    searchSsdpClient();
                else
                    StartCamera();
            }
        }
    }


    @Override
    public String CameraApiName() {

        return AppSettingsManager.API_SONY;
    }

    @Override
    public void StartCamera()
    {
        STATE = DEVICE_CONNECTED;
        FreeDPool.Execute(new Runnable() {
            @Override
            public void run() {
                ((CameraHolderSony) cameraHolder).OpenCamera(serverDevice);
                onCameraOpen("");
            }
        });
    }

    @Override
    public void StopCamera()
    {
        cameraHolder.CloseCamera();
        STATE = IDEL;
    }


    @Override
    public void DoWork() {
        moduleHandler.DoWork();
    }

    @Override
    public CameraHolderInterface GetCameraHolder() {
        return cameraHolder;
    }

    @Override
    public AbstractParameterHandler GetParameterHandler() {
        return parametersHandler;
    }

    @Override
    public ModuleHandlerAbstract GetModuleHandler() {
        return moduleHandler;
    }


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
    public boolean isAeMeteringSupported() {
        return Focus.isAeMeteringSupported();
    }

    @Override
    public FocuspeakProcessor getFocusPeakProcessor() {
        return null;
    }

    @Override
    public RenderScriptHandler getRenderScriptHandler() {
        return renderScriptHandler;
    }

    @Override
    public ActivityInterface getActivityInterface() {
        return (ActivityInterface)getActivity();
    }

    @Override
    public SurfaceView getSurfaceView() {
        return surfaceView;
    }

    @Override
    public AbstractFocusHandler getFocusHandler() {
        return Focus;
    }

}
