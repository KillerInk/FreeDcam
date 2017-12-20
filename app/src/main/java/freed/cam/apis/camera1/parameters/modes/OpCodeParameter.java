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

package freed.cam.apis.camera1.parameters.modes;

import android.text.TextUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import freed.cam.apis.basecamera.parameters.AbstractParameter;
import freed.cam.ui.themesample.handler.UserMessageHandler;
import freed.settings.OpCodeUrl;
import freed.settings.SettingsManager;
import freed.utils.FreeDPool;
import freed.utils.Log;
import freed.utils.StringUtils;


/**
 * Created by troop on 28.04.2016.
 */
public class OpCodeParameter extends AbstractParameter
{
    private final String TAG = OpCodeParameter.class.getSimpleName();
    private final boolean isSupported;

    public OpCodeParameter()
    {
        isSupported = SettingsManager.getInstance().opcodeUrlList.size() > 0;
    }

    //https://github.com/troop/FreeDcam/blob/master/camera1_opcodes/HTC_OneA9/opc2.bin?raw=true
    @Override
    public void SetValue(String valueToSet, boolean setToCamera)
    {
        if(valueToSet.equals("Download")) {
            for (final OpCodeUrl url : SettingsManager.getInstance().opcodeUrlList)
            {
                if (!TextUtils.isEmpty(url.getOpcode2Url()))
                    FreeDPool.Execute(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                httpsGet(url.getOpcode2Url(), url.getID() + "opc2.bin");
                            } catch (IOException ex) {
                                Log.WriteEx(ex);
                                UserMessageHandler.sendMSG(ex.getLocalizedMessage(),true);
                            }
                        }
                    });
                if (!TextUtils.isEmpty(url.getOpcode3Url()))
                    FreeDPool.Execute(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                httpsGet(url.getOpcode3Url(), url.getID() + "opc3.bin");
                            } catch (IOException ex) {
                                Log.WriteEx(ex);
                                UserMessageHandler.sendMSG(ex.getLocalizedMessage(),true);
                            }
                        }
                    });
            }
        }
    }

    @Override
    public boolean IsSupported() {
        return isSupported;
    }

    @Override
    public String GetStringValue() {
        return "true";
    }

    @Override
    public String[] getStringValues() {
        List<String> list = new ArrayList<>();

        if (SettingsManager.getInstance().opcodeUrlList.size() >0)
            list.add("Download");
        else list.add("No Opcode avail");
        return list.toArray(new String[list.size()]);
    }

    @Override
    public boolean IsVisible() {
        return isSupported;
    }

    private void httpsGet(String url, String fileending) throws IOException {
        HttpsURLConnection  httpConn = null;
        InputStream inputStream = null;

        // Open connection and input stream
        try {
            trustAllHosts();
            URL urlObj = new URL(url);
            httpConn = (HttpsURLConnection ) urlObj.openConnection();
            httpConn.setHostnameVerifier(OpCodeParameter.DO_NOT_VERIFY);
            httpConn.setRequestMethod("GET");
            httpConn.setConnectTimeout(15000);
            httpConn.setReadTimeout(3000);
            httpConn.connect();

            int responseCode = httpConn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                inputStream = httpConn.getInputStream();
            }
            if (inputStream == null) {
                Log.w(TAG, "httpGet: Response Code Error: " + responseCode + ": " + url);
                throw new IOException("Response Error:" + responseCode);
            }
        } catch (SocketTimeoutException e) {
            Log.w(TAG, "httpGet: Timeout: " + url);
            throw new IOException();
        } catch (MalformedURLException e) {
            Log.w(TAG, "httpGet: MalformedUrlException: " + url);
            throw new IOException();
        } catch (IOException e) {
            Log.w(TAG, "httpGet: " + e.getMessage());
            if (httpConn != null) {
                httpConn.disconnect();
            }
            throw e;
        }

        // Read stream as String
        FileOutputStream responseBuf = null;
        File file = new File(StringUtils.GetFreeDcamConfigFolder+fileending);
        try {

            responseBuf = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            int len;
            while((len=inputStream.read(buf))>0){
                responseBuf.write(buf,0,len);
            }
            responseBuf.flush();
        } catch (IOException e) {
            Log.w(TAG, "httpGet: read error: " + e.getMessage());
            file.delete();
            throw e;
        } finally {
            try {
                if (responseBuf != null)
                    responseBuf.close();
            } catch (IOException e) {
                Log.w(TAG, "IOException while closing BufferedReader");
            }
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                Log.w(TAG, "IOException while closing InputStream");
            }
            fireStringValueChanged("true");
        }
    }

    // always verify the host - dont check for certificate
    private static final HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    };

    /**
     * Trust every server - dont check for any certificate
     */
    private static void trustAllHosts() {
        // Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = { new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[] {};
            }

            public void checkClientTrusted(X509Certificate[] chain,
                                           String authType) throws CertificateException {
            }

            public void checkServerTrusted(X509Certificate[] chain,
                                           String authType) throws CertificateException {
            }
        } };

        // Install the all-trusting trust manager
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new SecureRandom());
            HttpsURLConnection
                    .setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {
            Log.WriteEx(e);
        }
    }
}
