package freed.cam.apis.camera2.modules.capture;

import android.hardware.camera2.CaptureResult;
import android.media.Image;
import android.media.ImageReader;
import android.view.Surface;

public interface ImageCaptureInterface extends ImageReader.OnImageAvailableListener {
    /**
     * @return the Surface from the ImageReader attached to this instance
     */
    Surface getSurface();

    /**
     * Add a captureResult to the BlockingQueue
     * @param captureResult to store
     */
    boolean setCaptureResult(CaptureResult captureResult);

    /**
     * gets thrown when when a capture is completed and its ready to save
     * @param image
     * @param result
     */
    boolean onCaptureCompleted(Image image, CaptureResult result);

    /**
     * release and clean up this instance
     */
    void release();

    /**
     * if set to true the imagereader gets attached to the preview and triggers continouse onImageAvailible
     * @return
     */
    boolean setToPreview();

    /**
     * takes and remove the first image from the blockingqueue and lock it
     * @return returns a locked image. when done make sure you call releaseImage
     */
    Image pollImage();

    /**
     * Remove the lock from the image and free its ressources
     * @param img to clear
     */
    void releaseImage(Image img);

    /**
     * returns, but dont remove, the first image from the blockingqueue
     * @return
     */
    Image peekImage();

    /**
     * @return the first captureResult from blockingqueue and remove it
     */
    CaptureResult pollCaptureResult();
    /**
     * @return the first captureResult from blockingqueue but dont remove it
     */
    CaptureResult peekCaptureResult();
}
