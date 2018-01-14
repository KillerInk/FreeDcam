package freed.cam.apis.camera1.renderscript;


import android.annotation.TargetApi;
import android.os.Build;
import android.renderscript.Allocation;
import android.renderscript.ScriptGroup;

import java.util.concurrent.BlockingQueue;

import freed.cam.apis.camera1.CameraHolder;
import freed.utils.Log;
import freed.utils.RenderScriptManager;

public class FocusPeakRunner implements Runnable {

    private final RenderScriptManager renderScriptManager;
    private final CameraHolder cameraHolder;
    private final BlockingQueue<byte[]> frameQueue;
    private ScriptGroup peakGroup;

    private boolean enable = false;
    private boolean isWorking;

    public FocusPeakRunner(RenderScriptManager renderScriptManager, CameraHolder cameraHolder,BlockingQueue<byte[]> frameQueue)
    {
        this.renderScriptManager = renderScriptManager;
        this.cameraHolder = cameraHolder;
        this.frameQueue = frameQueue;
    }

    public void setScriptGroup(ScriptGroup scriptGroup)
    {
        this.peakGroup = scriptGroup;
    }

    public void setEnable(boolean enable)
    {
        this.enable = enable;
    }

    public boolean isEnable()
    {
        return enable;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void run() {
        isWorking = true;
        byte[] tmp;
        while (enable)
        {
            try {
                //take one stored frame for processing
                tmp = frameQueue.take();
                renderScriptManager.GetIn().copyFrom(tmp);
                //pass frame back to camera that it get reused
                cameraHolder.GetCamera().addCallbackBuffer(tmp);
                peakGroup.execute();
                renderScriptManager.GetOut().ioSend();


            } catch (InterruptedException | NullPointerException e) {
                Log.WriteEx(e);
            }
            finally
            {
                //clear frames and pass them back the camera
                while (frameQueue.size()>0)
                    try {
                        cameraHolder.GetCamera().addCallbackBuffer(frameQueue.take());
                    } catch (InterruptedException | NullPointerException e)
                    {
                        Log.WriteEx(e);
                    }
                frameQueue.clear();
            }
        }
        isWorking = false;
    }
}
