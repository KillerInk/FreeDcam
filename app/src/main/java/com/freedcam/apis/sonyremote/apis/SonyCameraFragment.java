package com.freedcam.apis.sonyremote.apis;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.freedcam.apis.apis.AbstractCameraFragment;
import com.freedcam.apis.i_camera.interfaces.I_Module;
import com.freedcam.apis.sonyremote.sonyapi.CameraUiWrapperSony;
import com.freedcam.apis.sonyremote.sonyapi.sonystuff.ServerDevice;
import com.freedcam.apis.sonyremote.sonyapi.sonystuff.SimpleSsdpClient;
import com.freedcam.apis.sonyremote.sonyapi.sonystuff.SimpleStreamSurfaceView;
import com.freedcam.apis.sonyremote.sonyapi.sonystuff.WifiUtils;
import com.freedcam.utils.Logger;
import com.freedcam.apis.i_camera.interfaces.I_CameraChangedListner;
import com.troop.freedcam.R;

/**
 * Created by troop on 06.06.2015.
 */
public class SonyCameraFragment extends AbstractCameraFragment implements I_CameraChangedListner
{
    private final String TAG = SonyCameraFragment.class.getSimpleName();
    private SimpleStreamSurfaceView surfaceView;
    private WifiUtils wifiUtils;
    private WifiScanReceiver wifiReciever;
    private WifiConnectedReceiver wifiConnectedReceiver;
    private SimpleSsdpClient mSsdpClient;
    private ServerDevice serverDevice;
    private CameraUiWrapperSony cameraUiWrapper;

    private TextView textView_wifi;
    private final int IDEL = 0;
    private final int WAITING_FOR_SCANRESULT = 1;
    private final int WAITING_FOR_DEVICECONNECTION = 2;
    private int STATE = IDEL;

    private String[] configuredNetworks = null;
    private String deviceNetworkToConnect;
    private boolean connected = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.cameraholdersony, container, false);
        surfaceView = (SimpleStreamSurfaceView) view.findViewById(R.id.view);

        this.textView_wifi =(TextView)view.findViewById(R.id.textView_wificonnect);
        super.onCreateView(inflater, container, savedInstanceState);

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
        if(connected)
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
        mSsdpClient.search(new SimpleSsdpClient.SearchResultHandler()
        {
            @Override
            public void onDeviceFound(ServerDevice device)
            {
                if(connected)
                    return;
                setTextFromWifi("Found SSDP Client... Connecting");
                connected = true;
                cameraUiWrapper.serverDevice = device;
                cameraUiWrapper.StartCamera();
                hideTextViewWifi(true);
            }
            @Override
            public void onFinished()
            {
                if (cameraUiWrapper.serverDevice == null)
                   setTextFromWifi("Cant find a sony remote Device");

            }

            @Override
            public void onErrorFinished()
            {
                if (cameraUiWrapper.serverDevice == null)
                    setTextFromWifi("Error happend while searching for sony remote device \n pls restart remote");
                startScanning();
            }
        });
    }

    @Override
    public void onResume()
    {
        super.onResume();
        //getActivity().registerReceiver(wifiConnectedReceiver, new IntentFilter(WifiManager.NETWORK_STATE_CHANGED_ACTION));
        setupWrapper();
        wifiReciever = new WifiScanReceiver();
        wifiConnectedReceiver = new WifiConnectedReceiver();
        wifiUtils = new WifiUtils(view.getContext());
        mSsdpClient = new SimpleSsdpClient();

        startScanning();

        //connect();
    }

    private void setupWrapper()
    {
        this.cameraUiWrapper = new CameraUiWrapperSony(surfaceView);
        cameraUiWrapper.SetCameraChangedListner(this);
        if (onrdy != null)
            onrdy.onCameraUiWrapperRdy(cameraUiWrapper);
    }

    private void startScanning()
    {
        connected = false;
        if (getActivity() != null) {
            getActivity().registerReceiver(wifiReciever, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
            getConfiguredNetworks();
            lookupAvailNetworks();
        }

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
            searchSsdpClient();
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
        connected = false;
        Logger.d(TAG, "Camera error:" +error );
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run()
            {
                Logger.d(TAG, "StartScanning For networks after onCameraError" );
                try {
                    setupWrapper();
                    startScanning();
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
    public void onModuleChanged(I_Module module) {

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
                searchSsdpClient();
            }
        }
    }


}
