package com.troop.apis;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import com.troop.freedcam.apis.AbstractCameraFragment;
import com.troop.freedcam.sonyapi.CameraUiWrapperSony;
import com.troop.freedcam.sonyapi.R;
import com.troop.freedcam.sonyapi.sonystuff.ServerDevice;
import com.troop.freedcam.sonyapi.sonystuff.SimpleSsdpClient;
import com.troop.freedcam.sonyapi.sonystuff.SimpleStreamSurfaceView;
import com.troop.freedcam.sonyapi.sonystuff.WifiUtils;
import com.troop.freedcam.ui.I_PreviewSizeEvent;

/**
 * Created by troop on 06.06.2015.
 */
public class SonyCameraFragment extends AbstractCameraFragment
{
    SimpleStreamSurfaceView surfaceView;
    WifiUtils wifiUtils;
    WifiScanReceiver wifiReciever;
    WifiConnectedReceiver wifiConnectedReceiver;
    private SimpleSsdpClient mSsdpClient;
    ServerDevice serverDevice;
    CameraUiWrapperSony wrapperSony;
    String confnet;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.cameraholdersony, container, false);
        surfaceView = (SimpleStreamSurfaceView) view.findViewById(R.id.view);
        this.wrapperSony = new CameraUiWrapperSony(surfaceView, appSettingsManager);
        this.cameraUiWrapper = wrapperSony;
        super.onCreateView(inflater, container, savedInstanceState);
        wifiReciever = new WifiScanReceiver();
        wifiConnectedReceiver = new WifiConnectedReceiver();
        wifiUtils = new WifiUtils(view.getContext());
        mSsdpClient = new SimpleSsdpClient();

        return view;
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

    @Override
    public void setOnPreviewSizeChangedListner(I_PreviewSizeEvent previewSizeChangedListner) {
        surfaceView.SetOnPreviewSizeCHangedListner(previewSizeChangedListner);
    }

    //WIFI STUFF START
    class WifiScanReceiver extends BroadcastReceiver
    {
        public void onReceive(Context c, Intent intent)
        {
            getActivity().unregisterReceiver(wifiReciever);
            if (confnet == null || confnet.equals(""))
                return;
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
                wrapperSony.onCameraError("Cant find Sony Camera WifiNetwork, Camera turned On?");
                return;
            }
            getActivity().registerReceiver(wifiConnectedReceiver, new IntentFilter(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION));
            wifiUtils.ConnectToSSID(foundnet);
        }
    }

    class WifiConnectedReceiver extends BroadcastReceiver
    {
        public void onReceive(Context c, Intent intent)
        {
            getActivity().unregisterReceiver(wifiConnectedReceiver);
            searchSsdpClient();
        }
    }

    private void searchSsdpClient()
    {
        if (wifiUtils.getWifiConnected())
        {
            mSsdpClient.search(new SimpleSsdpClient.SearchResultHandler()
            {
                @Override
                public void onDeviceFound(ServerDevice device) {
                    wrapperSony.serverDevice = device;
                    wrapperSony.StartCamera();
                }
                @Override
                public void onFinished()
                {
                    if (wrapperSony.serverDevice == null)
                        wrapperSony.onCameraError("Cant find a sony remote Device");

                }

                @Override
                public void onErrorFinished()
                {
                    wrapperSony.onCameraError("Error happend while searching for sony remote device");
                }
            });
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();


        connect();
    }

    @Override
    public void onPause() {
        super.onPause();


    }

    public void connect()
    {
        String wifis = null;
        try {
            wifis = wifiUtils.getConnectedNetworkSSID();
            if (wifis == null || wifis.equals("")) {
                wrapperSony.onCameraError("Wifi disabled");
                return;
            }

        }
        catch (Exception ex)
        {
            wrapperSony.onCameraError("Wifi disabled");
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
                wrapperSony.onCameraError("Wifi disabled");
                return;
            }

            confnet = "";
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
                wrapperSony.onCameraError("No Sony Camera Device Configured in WifiSettings");
                return;
            }
            getActivity().registerReceiver(wifiReciever, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
            wifiUtils.StartScan();

        }
        else
            searchSsdpClient();
    }
}
