package freed.cam.apis.camera2.modules.capture;

import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.os.Build;
import android.view.Surface;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.List;

import freed.cam.apis.camera2.modules.helper.RdyToSaveImg;
import freed.image.EmptyTask;
import freed.image.ImageManager;
import freed.image.ImageTask;
import freed.utils.Log;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class CaptureController extends CameraCaptureSession.CaptureCallback
{
    private final String TAG = CaptureController.class.getSimpleName();
    private List<AbstractImageCapture> imageCaptures;
    private RdyToSaveImg rdyToSaveImg;

    public CaptureController(RdyToSaveImg rdyToSaveImg)
    {
        imageCaptures = new ArrayList<>();
        this.rdyToSaveImg = rdyToSaveImg;
    }

    public List<AbstractImageCapture> getImageCaptures() {
        return imageCaptures;
    }

    public void add(AbstractImageCapture stillImageCapture)
    {
        imageCaptures.add(stillImageCapture);
    }

    public void clear()
    {
        for (AbstractImageCapture s:imageCaptures) {
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

        for(AbstractImageCapture imageCapture : imageCaptures) {
            imageCapture.setCaptureResult(result);
            ImageTask task = imageCapture.getSaveTask();
            imageCapture.resetTask();
            if (task != null && !(task instanceof EmptyTask)) {
                ImageManager.putImageSaveTask(task);
                Log.d(TAG, "Put task to Queue");
                Log.d(TAG, "wait for capture end done");
            }
        }
        rdyToSaveImg.onRdyToSaveImg();
    }
}
