package freed.cam.apis.camera2.modules.capture;

import android.hardware.camera2.CaptureResult;
import android.media.Image;
import android.media.ImageReader;
import android.view.Surface;

import freed.image.ImageTask;

public interface ImageCaptureInterface extends ImageReader.OnImageAvailableListener {
    /**
     * @return the Surface from the ImageReader attached to this instance
     */
    Surface getSurface();

    /**
     * Add a captureResult to the BlockingQueue
     * @param captureResult to store
     */
    void setCaptureResult(CaptureResult captureResult);

    /**
     * release and clean up this instance
     */
    void release();

    /**
     * if set to true the imagereader gets attached to the preview and triggers continouse onImageAvailible
     * @return
     */
    boolean setToPreview();

    ImageTask getSaveTask();

}
