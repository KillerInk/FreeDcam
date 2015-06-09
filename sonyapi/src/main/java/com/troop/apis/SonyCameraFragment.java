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
import android.widget.TextView;

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
    TextView textView_wifi;
    private final int IDEL = 0;
    private final int WAITING_FOR_SCANRESULT = 1;
    private final int WAITING_FOR_DEVICECONNECTION = 2;
    private int STATE = IDEL;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.cameraholdersony, container, false);
        surfaceView = (SimpleStreamSurfaceView) view.findViewById(R.id.view);
        this.wrapperSony = new CameraUiWrapperSony(surfaceView, appSettingsManager);
        this.cameraUiWrapper = wrapperSony;
        this.textView_wifi =(TextView)view.findViewById(R.id.textView_wificonnect);
        super.onCreateView(inflater, container, savedInstanceState);
        wifiReciever = new WifiScanReceiver();
        wifiConnectedReceiver = new WifiConnectedReceiver();
        wifiUtils = new WifiUtils(view.getContext());
        mSsdpClient = new SimpleSsdpClient();
        hideTextViewWifi(true);
        getActivity().registerReceiver(wifiConnectedReceiver, new IntentFilter(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION));
        getActivity().registerReceiver(wifiReciever, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        connect();

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
            if (STATE == WAITING_FOR_SCANRESULT)
            {
                STATE = IDEL;
                if (confnet == null || confnet.equals(""))
                    return;
                String[] foundNetWorks = wifiUtils.getNetworkSSIDs();
                String foundnet = "";
                for (String s : foundNetWorks) {
                    if (confnet.equals(s)) {
                        foundnet = s;
                        break;
                    }
                }
                if (foundnet.equals("")) {
                    setTextFromWifi("Cant find Sony Camera WifiNetwork, Camera turned On?");
                    return;
                }
                STATE = WAITING_FOR_DEVICECONNECTION;
                wifiUtils.ConnectToSSID(foundnet);
            }
        }
    }

    class WifiConnectedReceiver extends BroadcastReceiver
    {
        public void onReceive(Context c, Intent intent)
        {
            if (STATE == WAITING_FOR_DEVICECONNECTION)
            {
                STATE = IDEL;
                searchSsdpClient();
            }
        }
    }

    private void searchSsdpClient()
    {
        if (wifiUtils.getWifiConnected())
        {
            setTextFromWifi("Search SSDP Client...");
            mSsdpClient.search(new SimpleSsdpClient.SearchResultHandler()
            {
                @Override
                public void onDeviceFound(ServerDevice device)
                {
                    setTextFromWifi("Found SSDP Client... Connecting");
                    wrapperSony.serverDevice = device;
                    wrapperSony.StartCamera();
                    hideTextViewWifi(true);
                }
                @Override
                public void onFinished()
                {
                    if (wrapperSony.serverDevice == null)
                       setTextFromWifi("Cant find a sony remote Device");

                }

                @Override
                public void onErrorFinished()
                {
                    if (wrapperSony.serverDevice == null)
                        setTextFromWifi("Error happend while searching for sony remote device");
                }
            });
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();



    }

    @Override
    public void onPause() {
        super.onPause();


    }

    @Override
    public void onDetach() {
        super.onDetach();
        getActivity().unregisterReceiver(wifiReciever);
        getActivity().unregisterReceiver(wifiConnectedReceiver);
    }

    private void connect()
    {
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
            String[] configuredNetworks = null;
            try {
                configuredNetworks = wifiUtils.getConfiguredNetworkSSIDs();
            }
            catch (Exception ex) {
                setTextFromWifi("Wifi disabled");
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
            if (confnet.equals("")) {
                setTextFromWifi("No Sony Camera Device Configured in WifiSettings");
                return;
            }
            STATE = WAITING_FOR_SCANRESULT;
            wifiUtils.StartScan();

        }
        else
            searchSsdpClient();
    }
}
