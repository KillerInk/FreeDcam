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
    private SimpleSsdpClient mSsdpClient;
    private ServerDevice serverDevice;

    private TextView textView_wifi;
    private final int STATE_IDEL = 0;
    private final int STATE_DEVICE_CONNECTED = 3;
    private int STATE = STATE_IDEL;

    private String[] configuredNetworks;
    private String deviceNetworkToConnect;
    private boolean isWifiListnerRegistered = false;
    private boolean hasLocationPermission =false;
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
        wifiUtils = new WifiUtils(view.getContext());
        mSsdpClient = new SimpleSsdpClient();

        parametersHandler = new ParameterHandler(this, surfaceView, getContext());

        moduleHandler = new ModuleHandlerSony(this);
        Focus = new FocusHandler(this);
        cameraHolder = new CameraHolderSony(getContext(), surfaceView, this);
        moduleHandler.initModules();

        SetCameraStateChangedListner(this);
        ((ActivityFreeDcamMain) getActivity()).onCameraUiWrapperRdy(this);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(getActivityInterface().hasLocationPermission() == true) {
            getActivity().registerReceiver(wifiReciever, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
            isWifiListnerRegistered = true;
            StartLookUp();
            //startWifiScanning();
        }
        else
            setTextFromWifi("Location Permission is needed to find the camera!");
    }

    @Override
    public void onPause() {
        super.onPause();
        StopCamera();
        if (isWifiListnerRegistered) {
            getActivity().unregisterReceiver(wifiReciever);
            isWifiListnerRegistered = false;
        }
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


    public void StartLookUp()
    {
        Logger.d(TAG,"StartLookup");
        if (STATE == STATE_DEVICE_CONNECTED)
            return;
        //check if Wifi is on and LocationService too, to lookup wifinetworks
        if (wifiUtils.isWifiEnabled() && wifiUtils.isLocationServiceEnabled())
        {
            setTextFromWifi("Lookup Connected Network");

            //check if we are already connected to a DIRECT network
            String connectedWifi = wifiUtils.getConnectedNetworkSSID();
            if (connectedWifi.contains("DIRECT"))
            {
                if (!wifiUtils.isWifiConnected())
                {
                   postDelayed(500);
                    return;
                }
                if (serverDevice != null)
                {
                    Logger.d(TAG,"Have ServerDevice already, StartCamera");
                    StartCamera();
                }
                else {
                    Logger.d(TAG, "ServerDevice is empty, searchSSDPClient");
                    searchSSDPClient();
                }
                return;
            }
            else {
                //we are not connected. look up configured networks
                Logger.d(TAG, "Lookup configured Networks for Remote Camera");
                String[] configuredNetworks = wifiUtils.getConfiguredNetworkSSIDs();
                String cameraRemoteNetworkToConnect = findConfiguredCameraRemoteNetwork(configuredNetworks);
                if (cameraRemoteNetworkToConnect == null) {
                    setTextFromWifi("No Camera Remote Configured");
                    return;
                } else
                {
                    //lookup avail wifi networks
                    Logger.d(TAG, "lookup availNetworks if Remote Camera Network is present");
                    setTextFromWifi("Look for Camera Remote Network");
                    String[] foundNetWorks = wifiUtils.getNetworkSSIDs();
                    if (foundNetWorks != null || foundNetWorks.length > 0)
                    {
                        String foundnet = "";
                        for (String s : foundNetWorks) {
                            if (cameraRemoteNetworkToConnect.equals(s)) {
                                //we found the network that we want to connect
                                foundnet = s;
                                break;
                            }
                        }
                        if (foundnet.equals(""))
                        {
                            Logger.d(TAG,"Not networkfound,Start Scan");
                            setTextFromWifi("No Network found, Start Scan");
                            wifiUtils.StartScan();
                            return;
                        }
                        else //connect to direct network
                        {
                            Logger.d(TAG,"Connect to " + foundnet);
                            setTextFromWifi("Connect to: " +foundnet);
                            //getActivity().registerReceiver(wifiConnectedReceiver, new IntentFilter(WifiManager.NETWORK_STATE_CHANGED_ACTION));
                            wifiUtils.ConnectToSSID(foundnet);
                            postDelayed(1000);
                            return;
                        }
                    }
                    else {
                        Logger.d(TAG,"Not networkfound,Start Scan");
                        setTextFromWifi("No Network found, Start Scan");
                        wifiUtils.StartScan();
                    }
                }
            }
        }
        else
        {
            if (!wifiUtils.isWifiEnabled())
                setTextFromWifi("Pls enable Wifi");
            if (!wifiUtils.isLocationServiceEnabled())
                setTextFromWifi("Pls enable LocationService");
        }
    }

    private void postDelayed(int ms)
    {
        uiHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                StartLookUp();
            }
        },ms);
    }

    private void searchSSDPClient()
    {
        mSsdpClient.search(new SearchResultHandler()
        {
            @Override
            public void onDeviceFound(ServerDevice device)
            {
                if(STATE == STATE_DEVICE_CONNECTED)
                    return;
                setTextFromWifi("Found SSDP Client... Connecting");
                STATE = STATE_DEVICE_CONNECTED;
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
                    setTextFromWifi("Error happend while searching for sony remote device");
                StartLookUp();
            }
        });
    }

    private String findConfiguredCameraRemoteNetwork(String[] configuredCameraRemoteNetworks)
    {
        for (String s : configuredCameraRemoteNetworks)
        {
            if (s.contains("DIRECT"))
            {
                return s;
            }
        }
        return null;
    }


    @Override
    public void onCameraOpen(String message) {
        STATE = STATE_DEVICE_CONNECTED;
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
        hideTextViewWifi(false);
        serverDevice = null;
        STATE = STATE_IDEL;
        Logger.d(TAG, "Camera error:" +error );
        surfaceView.stop();
        SetCameraStateChangedListner(SonyCameraFragment.this);
        ((ActivityFreeDcamMain) getActivity()).onCameraUiWrapperRdy(SonyCameraFragment.this);
        postDelayed(5000);

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
            Logger.d(TAG, "WifiScanReceiver.onRecieve()");
            StartLookUp();
        }
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
            StartLookUp();
            return;
        }
        Logger.d(TAG,"StartCamera");

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
        STATE = STATE_IDEL;
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
