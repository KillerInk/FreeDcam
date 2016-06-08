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

package com.freedcam.apis.sonyremote.sonystuff;

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
        List<ScanResult> results = wifiManager.getScanResults();
        String[] ret = new String[results.size()];
        int i = 0;
        for (ScanResult s : results)
        {
            ret[i++] = "\"" + s.SSID + "\"";
        }
        return ret;
    }

    public String[] getConfiguredNetworkSSIDs()
    {
        List<WifiConfiguration> configs = wifiManager.getConfiguredNetworks();
        String[] ret = new String[configs.size()];
        int i = 0;
        for (WifiConfiguration config : configs) {
            ret[i++] = config.SSID;
        }
        return ret;
    }


    public String getConnectedNetworkSSID()
    {
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
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
