package freed.cam.apis.camera2.modules.helper;

import android.os.StrictMode;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;

import freed.utils.Log;

public class MySocket {

    private final String TAG = MySocket.class.getSimpleName();
    private boolean hasConnection = false;
    private String ip;
    private int port;
    private Socket mysocket;
    private BufferedOutputStream outputStream;
    public MySocket(String ip, int port)
    {
        this.port = port;
        this.ip = ip;
    }

    public  boolean connect()
    {
        try {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);

            mysocket = new Socket(ip, port);
            try {
                this.outputStream = new BufferedOutputStream(mysocket.getOutputStream());

            } catch (IOException e) {
                Log.WriteEx(e);
            }
            catch (NullPointerException e)
            {
                Log.WriteEx(e);
            }
            hasConnection = true;

        } catch (IOException e) {
            hasConnection = false;
            e.printStackTrace();
            Log.e(TAG, String.valueOf(e));
        }
        return hasConnection;
    }

    public void closeConnection()
    {
        if (mysocket != null) {
            if (outputStream != null) {
                try {
                    outputStream.close();
                    outputStream = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try {
                mysocket.close();
                mysocket = null;
                hasConnection = false;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean isConnected()
    {
        return hasConnection;
    }

    public void write(byte[] b) throws IOException {
        if (outputStream != null) {
            outputStream.write(b);
            outputStream.flush();
        }
    }

    public void write(byte b) throws IOException {
        if (outputStream != null)
            outputStream.write(b);
    }
}
