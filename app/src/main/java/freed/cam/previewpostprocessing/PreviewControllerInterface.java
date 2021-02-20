package freed.cam.previewpostprocessing;

import android.content.Context;

import freed.cam.histogram.HistogramController;
import freed.cam.histogram.HistogramFeed;
import freed.viewer.screenslide.views.MyHistogram;

public interface PreviewControllerInterface extends Preview {
    void initPreview(PreviewPostProcessingModes previewPostProcessingModes, Context context, HistogramController histogram);

}
