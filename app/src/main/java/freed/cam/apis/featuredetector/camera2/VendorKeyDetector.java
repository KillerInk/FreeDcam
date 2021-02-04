package freed.cam.apis.featuredetector.camera2;

import java.util.HashSet;

public interface VendorKeyDetector
{
    void checkIfVendorKeyIsSupported(HashSet<String> keys);
}
