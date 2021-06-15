package freed.cam.previewpostprocessing;

import android.content.Context;

import freed.cam.histogram.HistogramController;

public interface PreviewControllerInterface extends Preview {
    void initPreview(PreviewPostProcessingModes previewPostProcessingModes, Context context, HistogramController histogram);
    /**
     * get the left margine between display and preview
     * @return
     */
    int getMargineLeft();
    /**
     * get the right margine between display and preview
     * @return
     */
    int getMargineRight();
    /**
     * get the top margine between display and preview
     * @return
     */
    int getMargineTop();
    /**
     * get the preview width
     * @return
     */
    int getPreviewWidth();
    /**
     * get the preview height
     * @return
     */
    int getPreviewHeight();
}
