package freed.cam.apis.featuredetector.camera2.debug;

import android.hardware.camera2.CameraCharacteristics;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.Arrays;

import camera2_hidden_keys.qcom.CameraCharacteristicsQcom;
import camera2_hidden_keys.xiaomi.CameraCharacteristicsXiaomi;
import freed.cam.apis.featuredetector.camera2.BaseParameter2Detector;
import freed.utils.Log;

public class DumpQCFA extends BaseParameter2Detector {
    private final String TAG = DumpQCFA.class.getSimpleName();
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void findAndFillSettings(CameraCharacteristics cameraCharacteristics) {
        try {
            byte isQcfa = cameraCharacteristics.get(CameraCharacteristicsQcom.is_qcfa_sensor);
            Log.d(TAG, "isQcfa:" + isQcfa);
            Integer[] qcfa_dimens = cameraCharacteristics.get(CameraCharacteristicsQcom.qcfa_dimension);
            Log.d(TAG, "qcfa_dimens:" + Arrays.toString(qcfa_dimens));
            Integer[] qcfa_streamSizes = cameraCharacteristics.get(CameraCharacteristicsQcom.qcfa_availableStreamConfigurations);
            Log.d(TAG, "qcfa avail stream config" + Arrays.toString(qcfa_streamSizes));
            Integer[] active_array_size = cameraCharacteristics.get(CameraCharacteristicsQcom.qcfa_activeArraySize);
            Log.d(TAG, "qcfa acitve array size: " + Arrays.toString(active_array_size));
        }
        catch (IllegalArgumentException | NullPointerException ex)
        {
            Log.d(TAG, "No QCFA sensor");
        }
        //Integer customhw = cameraCharacteristics.get(CameraCharacteristicsQcom.customhw);
        //Log.d(TAG, "customhw: " + customhw);
        try {

            byte qcfaenabled = cameraCharacteristics.get(CameraCharacteristicsXiaomi.qcfa_enabled);
            Log.d(TAG, "qcfa enabled:" + qcfaenabled);
            byte qcfasupported = cameraCharacteristics.get(CameraCharacteristicsXiaomi.qcfa_supported);
            Log.d(TAG, "qcfa supported:" + qcfasupported);
        }
        catch (IllegalArgumentException | NullPointerException ex)
        {
            Log.d(TAG, "No QCFA sensor");
        }
    }
}
