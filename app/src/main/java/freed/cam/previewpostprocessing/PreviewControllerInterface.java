package freed.cam.previewpostprocessing;

import android.content.Context;

import freed.cam.histogram.HistogramController;

public interface PreviewControllerInterface extends Preview {
    void initPreview(PreviewPostProcessingModes previewPostProcessingModes, Context context, HistogramController histogram);

}
