package freed.cam.ui.themesample.settings.opcode;

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

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import freed.image.ImageTask;
import freed.settings.OpCodeUrl;
import freed.utils.Log;
import freed.utils.StringUtils;

/**
 * Created by KillerInk on 18.12.2017.
 */

public class OpCodeDownloadTask extends ImageTask {

    public interface DownloadEvents
    {
        void onError(String msg);
        void onComplete();
    }

    private OpCodeUrl url;
    private DownloadEvents eventslistner;

    private final String TAG = OpCodeDownloadTask.class.getSimpleName();

    public OpCodeDownloadTask(OpCodeUrl opCodeUrl, DownloadEvents eventslistner)
    {
        this.url = opCodeUrl;
        this.eventslistner = eventslistner;
    }

    @Override
    public boolean process() {
        if (!TextUtils.isEmpty(url.getOpcode2Url()))
            try {
                httpsGet(url.getOpcode2Url(), url.getID() + "opc2.bin");
            } catch (IOException ex) {
                Log.WriteEx(ex);
            }
        if (!TextUtils.isEmpty(url.getOpcode3Url()))
            try {
                httpsGet(url.getOpcode3Url(), url.getID() + "opc3.bin");
            } catch (IOException ex) {
                Log.WriteEx(ex);
            }
            if (eventslistner != null)
                eventslistner.onComplete();
        return false;
    }


    private void httpsGet(String url, String fileending) throws IOException {
        HttpsURLConnection httpConn = null;
        InputStream inputStream = null;

        // Open connection and input stream
        try {
            trustAllHosts();
            URL urlObj = new URL(url);
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
