package freed.cam.apis.camera2.modules.helper;

import android.os.StrictMode;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;

import freed.utils.BackgroundHandlerThread;
import freed.utils.Log;

public class MySocket {

    private final String TAG = MySocket.class.getSimpleName();
    private boolean hasConnection = false;
    private String ip;
    private int port;
    private Socket mysocket;
    private BufferedOutputStream outputStream;
    private BackgroundHandlerThread backgroundHandlerThread;
    public MySocket(String ip, int port)
    {
        this.port = port;
        this.ip = ip;
        backgroundHandlerThread = new BackgroundHandlerThread("SocketThread");
        backgroundHandlerThread.create();
    }

    public void destroy()
    {
        backgroundHandlerThread.destroy();
    }

    public  void connect()
    {
        backgroundHandlerThread.execute(()->{
            try {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                        .permitAll().build();
                StrictMode.setThreadPolicy(policy);

                mysocket = new Socket(ip, port);
                Log.d(TAG, "Connected to " + ip + ":" + port);
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
                Log.d(TAG, "failed Connected to " + ip + ":" + port);
                e.printStackTrace();
                Log.e(TAG, String.valueOf(e));
            }
        });
    }

    public void closeConnection()
    {
        backgroundHandlerThread.execute(()->{
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
        });
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
