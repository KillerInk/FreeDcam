package freed.cam.apis.sonyremote.sonystuff;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import freed.ActivityInterface;
import freed.utils.Log;
import freed.utils.PermissionManager;

/**
 * Created by troop on 12.01.2017.
 */

public class WifiHandler extends WifiUtils {


    public interface WifiEvents
    {
        void onDeviceFound(ServerDevice serverDevice);
        void onMessage(String msg);
    }

    private final String TAG = WifiHandler.class.getSimpleName();
    private SimpleSsdpClient mSsdpClient;
    private ActivityInterface activityInterface;
    private WifiEvents eventsListner;
    private Handler uiHandler;
    private boolean resumed = false;


    public WifiHandler(ActivityInterface activityInterface) {
        super(activityInterface.getContext());
        this.activityInterface = activityInterface;
        mSsdpClient = new SimpleSsdpClient();
        uiHandler = new Handler(Looper.getMainLooper());
    }

    public void onResume()
    {
        resumed = true;
        if(activityInterface.getPermissionManager().hasLocationPermission(null)) {
            activityInterface.getPermissionManager().hasWifiPermission(onLocationPermission);
        }
        else
            sendMessage("Location Permission is needed to find the camera!");
    }

    private PermissionManager.PermissionCallback onLocationPermission = new PermissionManager.PermissionCallback() {
        @Override
        public void permissionGranted(boolean granted) {
            if (!granted)
                return;
        }
    };

    public void onPause()
    {
        resumed = false;
        uiHandler.removeCallbacks(startLookupRunner);
    }


    public void setEventsListner(WifiEvents eventsListner)
    {
        this.eventsListner = eventsListner;
    }

    private void sendMessage(String msg)
    {
        if (eventsListner != null)
            eventsListner.onMessage(msg);
    }

    private void sendDevice(ServerDevice device)
    {
        if (eventsListner != null)
            eventsListner.onDeviceFound(device);
    }

    public void StartLookUp()
    {
        if (!resumed)
            return;
        Log.d(TAG,"StartLookup");
        //check if Wifi is on and LocationService too, to lookup wifinetworks
        if (isWifiEnabled() && isLocationServiceEnabled())
        {
            sendMessage("Lookup Connected Network");

            //check if we are already connected to a DIRECT network
            String connectedWifi = getConnectedNetworkSSID();
            if (connectedWifi.contains("DIRECT"))
            {
                if (!isWifiConnected())
                {
                    postDelayed(500);
                    return;
                }
                    searchSSDPClient();
                return;
            }
            else {
                //we are not connected. look up configured networks
                Log.d(TAG, "Lookup configured Networks for Remote Camera");
                String[] configuredNetworks = getConfiguredNetworkSSIDs();
                String cameraRemoteNetworkToConnect = findConfiguredCameraRemoteNetwork(configuredNetworks);
                if (cameraRemoteNetworkToConnect == null) {
                    sendMessage("No Camera Remote Configured");
                    return;
                } else
                {
                    //lookup avail wifi networks
                    Log.d(TAG, "lookup availNetworks if Remote Camera Network is present");
                    sendMessage("Look for Camera Remote Network");
                    String[] foundNetWorks = getNetworkSSIDs();
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
                        if (TextUtils.isEmpty(foundnet))
                        {
                            Log.d(TAG,"Not networkfound,Start Scan");
                            sendMessage("No Network found, Start Scan");
                            StartScan();
                            return;
                        }
                        else //connect to direct network
                        {
                            Log.d(TAG,"Connect to " + foundnet);
                            sendMessage("Connect to: " +foundnet);
                            //getActivity().registerReceiver(wifiConnectedReceiver, new IntentFilter(WifiManager.NETWORK_STATE_CHANGED_ACTION));
                            ConnectToSSID(foundnet);
                            postDelayed(1000);
                            return;
                        }
                    }
                    else {
                        Log.d(TAG,"Not networkfound,Start Scan");
                        sendMessage("No Network found, Start Scan");
                        StartScan();
                    }
                }
            }
        }
        else
        {
            if (!isWifiEnabled())
                sendMessage("Pls enable Wifi");
            if (!isLocationServiceEnabled())
                sendMessage("Pls enable LocationService");
            postDelayed(500);
        }
    }

    private void postDelayed(int ms)
    {
        uiHandler.postDelayed(startLookupRunner,ms);
    }

    private Runnable startLookupRunner = new Runnable() {
        @Override
        public void run() {
            StartLookUp();
        }
    };

    private void searchSSDPClient()
    {
        Log.d(TAG, "searchSSDPClient");
        mSsdpClient.search(new SimpleSsdpClient.SearchResultHandler()
        {
            @Override
            public void onDeviceFound(ServerDevice device)
            {
                sendDevice(device);
                mSsdpClient.cancelSearching();

                sendMessage("Found SSDP Client... Connecting");
            }
            @Override
            public void onFinished()
            {

            }

            @Override
            public void onErrorFinished()
            {
                sendMessage("Error happend while searching for sony remote device");
                Log.d(TAG,"searchSSDPClient.onErrorFinishied.StartLookUP");
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
    public void StartScan() {
        if (!resumed)
            return;
        super.StartScan();
        postDelayed(500);
    }
}
