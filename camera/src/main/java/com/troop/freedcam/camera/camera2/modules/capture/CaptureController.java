package com.troop.freedcam.camera.camera2.modules.capture;

import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.os.Build;
import android.view.Surface;

import androidx.annotation.RequiresApi;

import com.troop.freedcam.camera.camera2.modules.helper.RdyToSaveImg;
import com.troop.freedcam.camera.image.EmptyTask;
import com.troop.freedcam.image.ImageManager;
import com.troop.freedcam.image.ImageTask;
import com.troop.freedcam.utils.Log;

import java.util.ArrayList;
import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class CaptureController extends CameraCaptureSession.CaptureCallback
{
    private final String TAG = CaptureController.class.getSimpleName();
    private List<StillImageCapture> imageCaptures;
    private RdyToSaveImg rdyToSaveImg;

    public CaptureController(RdyToSaveImg rdyToSaveImg)
    {
        imageCaptures = new ArrayList<>();
        this.rdyToSaveImg = rdyToSaveImg;
    }

    public List<StillImageCapture> getImageCaptures() {
        return imageCaptures;
    }

    public void add(StillImageCapture stillImageCapture)
    {
        imageCaptures.add(stillImageCapture);
    }

    public void clear()
    {
        for (StillImageCapture s:imageCaptures) {
            s.release();
        }
        imageCaptures.clear();
    }

    public Surface[] getSurfaces()
    {
        Surface[] surfaces = new Surface[imageCaptures.size()];
        for (int i = 0; i < imageCaptures.size();i++)
            surfaces[i] = imageCaptures.get(i).getSurface();
        return surfaces;
    }

    @Override
    public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request, TotalCaptureResult result) {
        Log.d(TAG, "onCaptureCompleted FrameNum:" +result.getFrameNumber());

        for(StillImageCapture imageCapture : imageCaptures) {
            imageCapture.setCaptureResult(result);
            ImageTask task = imageCapture.getSaveTask();
            imageCapture.resetTask();
            if (task != null && !(task instanceof EmptyTask)) {
                ImageManager.putImageSaveTask(task);
                Log.d(TAG, "Put task to Queue");
            }
        }
        rdyToSaveImg.onRdyToSaveImg();
    }
}
