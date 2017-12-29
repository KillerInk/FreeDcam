package freed.cam.apis.basecamera.parameters.ae;

/**
 * Created by KillerInk on 29.12.2017.
 */

public interface AeManagerInterface {
    void setExposureTime(int valueToSet,boolean setToCamera);
    void setIso(int valueToSet,boolean setToCamera);
    void setExposureCompensation(int valueToSet,boolean setToCamera);
    void setAeMode(AeStates aeState);

    boolean isExposureTimeWriteable();
    boolean isExposureCompensationWriteable();
    boolean isIsoWriteable();
}
