package freed.cam.apis.featuredetector;

import java.util.List;

public interface FeatureDetectorTask
{
    List<Class> createParametersToCheckList();
    void detect();
    void preDetect();
    List<String> findCameraIDs();
    void checkCameraID(int id, List<String> cameraids, List<Class> parametersToDetect);
    void postDetect();
}
