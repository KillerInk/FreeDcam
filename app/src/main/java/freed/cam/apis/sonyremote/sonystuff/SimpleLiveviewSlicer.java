/*
 * Copyright 2014 Sony Corporation
 */

package freed.cam.apis.sonyremote.sonystuff;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import freed.utils.Log;

/**
 * A parser class for Liveview data Packet defined by Camera Remote API
 */
public class SimpleLiveviewSlicer {

    private static final String TAG = SimpleLiveviewSlicer.class.getSimpleName();

    private static final int CONNECTION_TIMEOUT = 2000; // [msec]

    private HttpURLConnection mHttpConn;

    private InputStream mInputStream;

    /**
     * Opens Liveview HTTP GET connection and prepares for reading Packet data.
     * 
     * @param liveviewUrl Liveview data url that is obtained by DD.xml or result
     *            of startLiveview API.
     * @throws IOException generic errors or exception.
     */
    public void open(String liveviewUrl) throws IOException {
        if (mInputStream != null || mHttpConn != null) {
            throw new IllegalStateException("Slicer is already open.");
        }

        URL urlObj = new URL(liveviewUrl);
        mHttpConn = (HttpURLConnection) urlObj.openConnection();
        mHttpConn.setRequestMethod("GET");
        mHttpConn.setConnectTimeout(CONNECTION_TIMEOUT);
        mHttpConn.connect();

        if (mHttpConn.getResponseCode() == HttpURLConnection.HTTP_OK) {
            mInputStream = mHttpConn.getInputStream();
        }
    }

    /**
     * Closes the connection.
     *
     * @throws IOException generic errors or exception.
     */
    public void close() {
        try {
            if (mInputStream != null) {
                mInputStream.close();
                mInputStream = null;
            }
        } catch (IOException e) {
            Log.d(TAG, "Close() IOException.");
        }

        if (mHttpConn != null) {
            mHttpConn.disconnect();
            mHttpConn = null;
        }

    }

    public DataExtractor nextDataExtractor() throws IOException
    {
        DataExtractor dataExtractor = null;
        while (mInputStream != null && dataExtractor == null)
        {
            dataExtractor = new DataExtractor(mInputStream);
        }
        return dataExtractor;
    }


    /**
     * Converts byte array to int.
     *
     * @param byteData
     * @param startIndex
     * @param count
     * @return
     */
    public static int bytesToInt(byte[] byteData, int startIndex, int count) {
        int ret = 0;
        for (int i = startIndex; i < startIndex + count; i++) {
            ret = ret << 8 | byteData[i] & 0xff;
        }
        return ret;
    }

    /**
     * Reads byte array from the indicated input stream.
     *
     * @param in
     * @param length
     * @return
     * @throws IOException
     */
    public static byte[] readBytes(InputStream in, int length) throws IOException {
        ByteArrayOutputStream tmpByteArray = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        while (true) {
            int trialReadlen = Math.min(buffer.length, length - tmpByteArray.size());
            int readlen = in.read(buffer, 0, trialReadlen);
            if (readlen < 0) {
                break;
            }
            tmpByteArray.write(buffer, 0, readlen);
            if (length <= tmpByteArray.size()) {
                break;
            }
        }
        byte[] ret = tmpByteArray.toByteArray();
        tmpByteArray.close();
        return ret;
    }
}
