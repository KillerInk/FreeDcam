package com.freedcam.apis.camera1.camera.parameters.modes;

import android.os.Handler;

import com.freedcam.apis.i_camera.parameters.AbstractModeParameter;
import com.freedcam.utils.DeviceUtils;
import com.freedcam.utils.FreeDPool;
import com.freedcam.utils.Logger;
import com.freedcam.utils.StringUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;


/**
 * Created by troop on 28.04.2016.
 */
public class OpCodeParameter extends AbstractModeParameter
{
    private final String TAG = OpCodeParameter.class.getSimpleName();
    private boolean hasOp2 = false;
    private boolean hasOp3 = false;
    private boolean isSupported = false;
    public OpCodeParameter(Handler uiHandler)
    {
        super(uiHandler);
        File op2 = new File(StringUtils.GetFreeDcamConfigFolder+"opc2.bin");
        if (op2.exists())
            hasOp2=true;
        File op3 = new File(StringUtils.GetFreeDcamConfigFolder+"opc3.bin");
        if (op3.exists())
            hasOp3=true;
        if (DeviceUtils.IS_DEVICE_ONEOF(DeviceUtils.OpCodeRdyToDL))
        {
            this.isSupported = true;
        }
    }

    //https://github.com/troop/FreeDcam/blob/PUBLIC/camera1_opcodes/HTC_OneA9/opc2.bin?raw=true
    @Override
    public void SetValue(String valueToSet, boolean setToCamera)
    {
        if (hasOp2 || hasOp3)
            return;
        final String urlopc2 = "https://github.com/troop/FreeDcam/blob/PUBLIC/camera1_opcodes/"+DeviceUtils.DEVICE().toString()+"/opc2.bin?raw=true";
        final String urlopc3 = "https://github.com/troop/FreeDcam/blob/PUBLIC/camera1_opcodes/"+DeviceUtils.DEVICE().toString()+"/opc3.bin?raw=true";
        FreeDPool.Execute(new Runnable() {
            @Override
            public void run() {
                try {
                    httpsGet(urlopc2, "opc2.bin");
                } catch (IOException e) {
                    Logger.exception(e);
                }
            }
        });
        FreeDPool.Execute(new Runnable() {
            @Override
            public void run() {
                try {
                    httpsGet(urlopc3, "opc3.bin");
                } catch (IOException e) {
                    Logger.exception(e);
                }
            }
        });
    }

    @Override
    public boolean IsSupported() {
        return isSupported;
    }

    @Override
    public String GetValue() {
        return (hasOp2|| hasOp3) +"";
    }

    @Override
    public String[] GetValues() {
        return new String[] {"Download"};
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
            final URL urlObj = new URL(url);
            httpConn = (HttpsURLConnection ) urlObj.openConnection();
            httpConn.setHostnameVerifier(DO_NOT_VERIFY);
            httpConn.setRequestMethod("GET");
            httpConn.setConnectTimeout(15000);
            httpConn.setReadTimeout(3000);
            httpConn.connect();

            int responseCode = httpConn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                inputStream = httpConn.getInputStream();
            }
            if (inputStream == null) {
                Logger.w(TAG, "httpGet: Response Code Error: " + responseCode + ": " + url);
                throw new IOException("Response Error:" + responseCode);
            }
        } catch (final SocketTimeoutException e) {
            Logger.w(TAG, "httpGet: Timeout: " + url);
            throw new IOException();
        } catch (final MalformedURLException e) {
            Logger.w(TAG, "httpGet: MalformedUrlException: " + url);
            throw new IOException();
        } catch (final IOException e) {
            Logger.w(TAG, "httpGet: " + e.getMessage());
            if (httpConn != null) {
                httpConn.disconnect();
            }
            throw e;
        }

        // Read stream as String
        FileOutputStream responseBuf = null;

        try {
            responseBuf = new FileOutputStream(new File(StringUtils.GetFreeDcamConfigFolder+fileending));
            byte[] buf = new byte[1024];
            int len;
            while((len=inputStream.read(buf))>0){
                responseBuf.write(buf,0,len);
            }
            responseBuf.flush();
        } catch (IOException e) {
            Logger.w(TAG, "httpGet: read error: " + e.getMessage());
            throw e;
        } finally {
            try {
                if (responseBuf != null)
                    responseBuf.close();
            } catch (IOException e) {
                Logger.w(TAG, "IOException while closing BufferedReader");
            }
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                Logger.w(TAG, "IOException while closing InputStream");
            }
            OpCodeParameter.this.BackgroundValueHasChanged("true");
        }
    }

    // always verify the host - dont check for certificate
    private final static HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    };

    /**
     * Trust every server - dont check for any certificate
     */
    private static void trustAllHosts() {
        // Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return new java.security.cert.X509Certificate[] {};
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
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection
                    .setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
