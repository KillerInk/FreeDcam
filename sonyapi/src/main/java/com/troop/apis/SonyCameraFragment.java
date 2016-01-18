package com.troop.apis;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
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
import com.troop.marshmallowpermission.MPermissions;

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

    TextView textView_wifi;
    private final int IDEL = 0;
    private final int WAITING_FOR_SCANRESULT = 1;
    private final int WAITING_FOR_DEVICECONNECTION = 2;
    private int STATE = IDEL;

    String[] configuredNetworks = null;
    String deviceNetworkToConnect;
    private boolean connected = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.cameraholdersony, container, false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            checkMpermission();
        surfaceView = (SimpleStreamSurfaceView) view.findViewById(R.id.view);
        this.wrapperSony = new CameraUiWrapperSony(surfaceView, appSettingsManager);
        this.cameraUiWrapper = wrapperSony;
        this.textView_wifi =(TextView)view.findViewById(R.id.textView_wificonnect);
        super.onCreateView(inflater, container, savedInstanceState);
        wifiReciever = new WifiScanReceiver();
        wifiConnectedReceiver = new WifiConnectedReceiver();
        wifiUtils = new WifiUtils(view.getContext());
        mSsdpClient = new SimpleSsdpClient();
        //hideTextViewWifi(true);


        return view;
    }

    private void checkMpermission()
    {
        /*if (getActivity().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED)
        {
            MPermissions.requestFineLocationPermission(this);
        }
        if (getActivity().checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED)
        {
            MPermissions.requestCoarsePermission(this);
        }*/
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


    //WIFI STUFF START


    private void searchSsdpClient()
    {
        if(connected)
            return;
        setTextFromWifi("Search SSDP Client...");
        if (true)//wifiUtils.getWifiConnected())
        {
            while (!wifiUtils.getWifiConnected())
            {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            mSsdpClient.search(new SimpleSsdpClient.SearchResultHandler()
            {
                @Override
                public void onDeviceFound(ServerDevice device)
                {
                    if(connected)
                        return;
                    setTextFromWifi("Found SSDP Client... Connecting");
                    connected = true;
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
                        setTextFromWifi("Error happend while searching for sony remote device \n pls restart remote");
                }
            });
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
        //getActivity().registerReceiver(wifiConnectedReceiver, new IntentFilter(WifiManager.NETWORK_STATE_CHANGED_ACTION));
        getActivity().registerReceiver(wifiReciever, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        getConfiguredNetworks();
        lookupAvailNetworks();
        connected = false;

        //connect();
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(wifiReciever);
        //getActivity().unregisterReceiver(wifiConnectedReceiver);
    }

    private void getConfiguredNetworks()
    {
        if(connected)
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
                Log.d("Wifi", "Device to Connect:" + deviceNetworkToConnect);
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
        Log.d("Wifi", "Lookup networks:");
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
            Log.d("Wifi", "No DIRECT network found start scan:");
            wifiUtils.StartScan();
        }
        else {
            Log.d("Wifi", "Connect to:" + deviceNetworkToConnect);
            STATE = IDEL;
            searchSsdpClient();
        }
    }

    class WifiScanReceiver extends BroadcastReceiver
    {
        public void onReceive(Context c, Intent intent)
        {
            Log.d("Wifi", "WifiScanReceiver");
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
                    Log.d("Wifi", "WifiScanReceiver no device to connect found lookupnetworks");
                    lookupAvailNetworks();
                }
                else {
                    STATE = WAITING_FOR_DEVICECONNECTION;
                    Log.d("Wifi", "WifiScanReceiver found device connect to it");
                    if (!wifiUtils.ConnectToSSID(foundnet))
                    {
                        Log.d("Wifi", "Connect to device failed");
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
                searchSsdpClient();
            }
        }
    }


}
