package com.troop.freedcam.sonyapi.sonystuff;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import java.util.List;

/**
 * Created by troop on 29.12.2014.
 */
public class WifiUtils
{
    private Context context;
    private WifiManager wifiManager;
    private ConnectivityManager connManager;

    public WifiUtils(Context context)
    {
        this.context = context;
        wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    public void StartScan() { wifiManager.startScan();}

    public String[] getNetworkSSIDs()
    {
        final List<ScanResult> results = wifiManager.getScanResults();
        final String[] ret = new String[results.size()];
        int i = 0;
        for (ScanResult s : results)
        {
            ret[i++] = "\"" + s.SSID + "\"";
        }
        return ret;
    }

    public String[] getConfiguredNetworkSSIDs()
    {
        final List<WifiConfiguration> configs = wifiManager.getConfiguredNetworks();
        final String[] ret = new String[configs.size()];
        int i = 0;
        for (WifiConfiguration config : configs) {
            ret[i++] = config.SSID;
        }
        return ret;
    }


    public String getConnectedNetworkSSID()
    {
        final WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        return wifiInfo.getSSID();
    }

    public boolean ConnectToSSID(String SSID)
    {
        List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
        for( WifiConfiguration i : list ) {
            if(i.SSID != null && i.SSID.equals(SSID))
            {
                wifiManager.disconnect();
                wifiManager.enableNetwork(i.networkId, true);
                return wifiManager.reconnect();


            }
        }
        return false;
    }

    public boolean getWifiConnected()
    {
        NetworkInfo networkInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return networkInfo.isConnected();
    }
}
